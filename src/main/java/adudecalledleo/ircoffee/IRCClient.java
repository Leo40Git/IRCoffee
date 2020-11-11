package adudecalledleo.ircoffee;

import adudecalledleo.ircoffee.caps.CapabilityNegotiator;
import adudecalledleo.ircoffee.data.IRCChannel;
import adudecalledleo.ircoffee.data.IRCWhoIsReply;
import adudecalledleo.ircoffee.event.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static adudecalledleo.ircoffee.IRCNumerics.*;

public final class IRCClient {
    public static final String CAP_LS_VERSION = "302";

    public final Event<ConnectionEvents.Connected> onConnected = Event.create(ConnectionEvents.Connected.class,
            listeners -> client -> {
                for (ConnectionEvents.Connected listener : listeners)
                    listener.onConnected(client);
            });
    public final Event<ConnectionEvents.Disconnected> onDisconnected = Event.create(ConnectionEvents.Disconnected.class,
            listeners -> client -> {
                for (ConnectionEvents.Disconnected listener : listeners)
                    listener.onDisconnected(client);
            });
    public final Event<ConnectionEvents.Bounced> onBounced = Event.create(ConnectionEvents.Bounced.class,
            listeners -> (client, newHost, newPort, message) -> {
                for (ConnectionEvents.Bounced listener : listeners)
                    listener.onBounced(client, newHost, newPort, message);
            });
    public final Event<ConnectionEvents.Terminated> onTerminated = Event.create(ConnectionEvents.Terminated.class,
            listeners -> (client, message) -> {
                for (ConnectionEvents.Terminated listener : listeners)
                    listener.onTerminated(client, message);
            });
    public final Event<CapabilityEvents.FeaturesAdvertised> onFeaturesAdvertised = Event.create(
            CapabilityEvents.FeaturesAdvertised.class, listeners -> (client, featureMap) -> {
                for (CapabilityEvents.FeaturesAdvertised listener : listeners)
                    listener.onFeaturesAdvertised(client, featureMap);
            });
    public final Event<MessageReceived> onMessageReceived = Event.create(MessageReceived.class,
            listeners -> (client, message) -> {
                for (MessageReceived listener : listeners)
                    listener.onMessageReceived(client, message.copy());
            });
    public final Event<UserEvents.WhoIsReplyReceived> onWhoIsReplyReceived = Event.create(
            UserEvents.WhoIsReplyReceived.class, listeners -> (client, whoIsReply) -> {
                for (UserEvents.WhoIsReplyReceived listener : listeners)
                    listener.onWhoIsReplyReceived(client, whoIsReply);
            });
    public final Event<ListEvents.ChannelsReceived> onChannelsReceived = Event.create(ListEvents.ChannelsReceived.class,
            listeners -> (client, channels) -> {
                for (ListEvents.ChannelsReceived listener : listeners)
                    listener.onChannelsReceived(client, channels);
            });
    public final Event<ListEvents.UsersInChannelReceived> onUsersInChannelReceived = Event.create(
            ListEvents.UsersInChannelReceived.class, listeners -> (client, channel, users) -> {
                for (ListEvents.UsersInChannelReceived listener : listeners)
                    listener.onUsersInChannelReceived(client, channel, users);
            });
    public final Event<UserEvents.NicknameChanged> onNicknameChanged = Event.create(UserEvents.NicknameChanged.class,
            listeners -> (client, oldNickname, newNickname) -> {
                for (UserEvents.NicknameChanged listener : listeners)
                    listener.onNicknameChanged(client, oldNickname, newNickname);
            });
    public final Event<UserEvents.UserHostReplyReceived> onUserHostReplyReceived = Event.create(
            UserEvents.UserHostReplyReceived.class, listeners -> (client, nickname, host1, isOperator, isAway) -> {
                for (UserEvents.UserHostReplyReceived listener : listeners)
                    listener.onUserHostReplyReceived(client, nickname, host1, isOperator, isAway);
            });
    public final Event<UserEvents.IsOnReplyReceived> onIsOnReplyReceived = Event.create(
            UserEvents.IsOnReplyReceived.class, listeners -> (client, users) -> {
                for (UserEvents.IsOnReplyReceived listener : listeners)
                    listener.onIsOnReplyReceived(client, users);
            });

    private String host = "127.0.0.1";
    private int port = -1;
    private boolean sslEnabled = false;

    private String initialNickname = "IRCoffee";
    private String username = "IRCoffee";
    private String realName = "IRCoffee User";
    private String password = "";

    private CapabilityNegotiator capabilityNegotiator;

    private EventLoopGroup group;
    private Channel ch;
    private ChannelFuture lastWriteFuture;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public String getInitialNickname() {
        return initialNickname;
    }

    public void setInitialNickname(String initialNickname) {
        this.initialNickname = initialNickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CapabilityNegotiator getCapabilityNegotiator() {
        return capabilityNegotiator;
    }

    public void setCapabilityNegotiator(CapabilityNegotiator capabilityNegotiator) {
        this.capabilityNegotiator = capabilityNegotiator;
    }

    public void connect() throws SSLException, InterruptedException {
        if (isConnected())
            throw new IllegalStateException("Already connected!");
        try {
            if (port < 0)
                port = sslEnabled ? 6697 : 6667;
            SslContext sslCtx = null;
            if (sslEnabled)
                // TODO swap InsecureTrustManagerFactory with something proper
                sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new Initializer(sslCtx));
            ch = b.connect(host, port).sync().channel();
            lastWriteFuture = null;

            // declare capability support (if negotiator is set)
            if (capabilityNegotiator != null)
                sendCommand("CAP", "LS", CAP_LS_VERSION);
            // ...then password (if available)
            if (!password.isEmpty())
                sendCommand("PASS", password);
            // and finally, nick and user
            sendCommand("NICK", initialNickname);
            // user *can* be omitted... on non-compliant servers (Twitch chat IRC interface)
            if (username.isEmpty())
                sendCommand("USER", username, "0", "*", realName);

            onConnected.invoker().onConnected(this);
        } catch (SSLException | InterruptedException e) {
            if (group != null)
                //noinspection deprecation
                group.shutdownNow();
            group = null;
            ch = null;
            lastWriteFuture = null;
            throw e;
        }
    }

    public void disconnect() {
        if (!isConnected())
            throw new IllegalStateException("Not connected!");
        try {
            if (lastWriteFuture != null)
                lastWriteFuture.sync();
        } catch (InterruptedException ignored) { }
        group.shutdownGracefully();
        group = null;
        ch = null;
        lastWriteFuture = null;
        onDisconnected.invoker().onDisconnected(this);
    }

    public boolean isConnected() {
        return group != null;
    }

    public void send(IRCMessage message) {
        if (!isConnected())
            throw new IllegalStateException("Not connected!");
        lastWriteFuture = ch.writeAndFlush(message + "\r\n");
    }

    public void sendCommand(String command, String... params) {
        send(IRCMessage.command(command, params));
    }

    private static final DelimiterBasedFrameDecoder FRAME_DECODER
            = new DelimiterBasedFrameDecoder(8192, Unpooled.wrappedBuffer(new byte[] { '\r', '\n' }));
    private static final StringDecoder DECODER = new StringDecoder(StandardCharsets.UTF_8);
    private static final StringEncoder ENCODER = new StringEncoder(StandardCharsets.UTF_8);

    private class Initializer extends ChannelInitializer<SocketChannel> {
        private final SslContext sslCtx;

        public Initializer(SslContext sslCtx) {
            this.sslCtx = sslCtx;
        }

        @Override
        protected void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            if (sslCtx != null)
                pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
            pipeline.addLast("FrameDecoder", FRAME_DECODER);
            pipeline.addLast("StringDecoder", DECODER);
            pipeline.addLast("StringEncoder", ENCODER);
            pipeline.addLast("MessageHandler", new Handler());
        }
    }

    private class Handler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            IRCMessage message = IRCMessage.fromString(msg);
            if (handleMessage(message))
                onMessageReceived.invoker().onMessageReceived(IRCClient.this, message);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
            disconnect();
        }
    }

    private static class Buffers {
        private ImmutableList.Builder<IRCChannel> channelListBuilder;
        private final Map<String, ImmutableList.Builder<String>> usersInChannelBuilders = new HashMap<>();
        private final Map<String, IRCWhoIsReply.Builder> whoIsBuilders = new HashMap<>();
    }
    private final Buffers buffers = new Buffers();

    private boolean handleMessage(IRCMessage message) {
        String command = message.getCommand().toUpperCase(Locale.ROOT);
        // special case for RPL_NONE, which... does nothing
        if (RPL_NONE.equals(command))
            return false;
        // channel list stuff
        if (RPL_LISTSTART.equals(command))
            // this one isn't guaranteed, so ignore it
            return false;
        if (RPL_LIST.equals(command)) {
            if (buffers.channelListBuilder == null)
                buffers.channelListBuilder = ImmutableList.builder();
            String name = message.getParam(1);
            int clientCount;
            try {
                clientCount = Integer.parseInt(message.getParam(2));
            } catch (Exception ignored) {
                return false;
            }
            String topic = message.getParam(3);
            buffers.channelListBuilder.add(new IRCChannel(name, clientCount, topic));
            return false;
        }
        if (RPL_LISTEND.equals(command)) {
            onChannelsReceived.invoker().onChannelsReceived(this, buffers.channelListBuilder.build());
            buffers.channelListBuilder = null;
            return false;
        }
        // user list stuff
        if (RPL_NAMREPLY.equals(command)) {
            String channel = message.getParam(2);
            ImmutableList.Builder<String> builder = buffers.usersInChannelBuilders.computeIfAbsent(channel,
                    key -> ImmutableList.builder());
            String usersString = message.getParam(3);
            if (usersString != null)
                builder.add(usersString.split(" "));
            return false;
        }
        if (RPL_ENDOFNAMES.equals(command)) {
            String channel = message.getParam(1);
            ImmutableList.Builder<String> builder = buffers.usersInChannelBuilders.remove(channel);
            if (builder != null)
                onUsersInChannelReceived.invoker().onUsersInChannelReceived(this, channel, builder.build());
            return false;
        }
        // TODO ban list stuff
        // WHOIS stuff
        if (RPL_WHOISUSER.equals(command)) {
            String nickname = message.getParam(1);
            String username = message.getParam(2);
            String host = message.getParam(3);
            String realName = message.getParam(5);
            buffers.whoIsBuilders.put(nickname, IRCWhoIsReply.builder(nickname, username, host, realName));
            return false;
        }
        if (RPL_WHOISSERVER.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null)
                whoIsBuilder.setServer(message.getParam(2), message.getParam(3));
            return false;
        }
        if (RPL_WHOISOPERATOR.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null)
                whoIsBuilder.setOperator();
            return false;
        }
        if (RPL_WHOISIDLE.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null) {
                int secondsIdle;
                try {
                    secondsIdle = Integer.parseUnsignedInt(message.getParam(2));
                } catch (NumberFormatException ignored) {
                    return false;
                }
                long signOnTime;
                try {
                    signOnTime = Long.parseUnsignedLong(message.getParam(3));
                } catch (NumberFormatException ignored) {
                    // this one's optional; if parse failed, it's probably missing
                    whoIsBuilder.setIdle(secondsIdle);
                    return false;
                }
                whoIsBuilder.setIdle(secondsIdle, signOnTime);
            }
            return false;
        }
        if (RPL_WHOISCHANNELS.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null) {
                String[] channels = message.getParam(2).split(" ");
                for (String channel : channels)
                    whoIsBuilder.addChannel(channel.substring(1));
            }
            return false;
        }
        if (RPL_WHOISCERTFP.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null) {
                String param2 = message.getParam(2);
                int lastSpace = param2.lastIndexOf(' ');
                if (lastSpace > 0)
                    param2 = param2.substring(lastSpace + 1);
                whoIsBuilder.setCertFingerprint(param2);
            }
        }
        if (RPL_ENDOFWHOIS.equals(command)) {
            IRCWhoIsReply.Builder whoIsBuilder = buffers.whoIsBuilders.remove(message.getParam(1));
            if (whoIsBuilder != null)
                onWhoIsReplyReceived.invoker().onWhoIsReplyReceived(this, whoIsBuilder.build());
            return false;
        }
        // TODO capability negotiation
        // standalone numerics/special commands
        if (RPL_BOUNCE.equals(command)) {
            String newHost = message.getParam(1);
            int newPort = sslEnabled ? 6697 : 6667;
            try {
                newPort = Integer.parseUnsignedInt(message.getParam(2));
            } catch (NumberFormatException ignored) { }
            onBounced.invoker().onBounced(this, newHost, newPort, message.getParam(3));
            if (isConnected()) {
                disconnect();
                setHost(newHost);
                setPort(newPort);
                try {
                    connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        if (RPL_USERHOST.equals(command)) {
            String[] userHostStrs = message.getParam(1).split(" ");
            for (String userHostStr : userHostStrs) {
                String[] parts = userHostStr.split(" ");
                boolean isOperator = parts[0].endsWith("*");
                String nickname = isOperator ? parts[0].substring(0, parts[0].length() - 1) : parts[0];
                String host = parts[1];
                boolean isAway = false;
                if (host.startsWith("-"))
                    isAway = true;
                else if (!host.startsWith("+"))
                    return false;
                host = host.substring(1);
                onUserHostReplyReceived.invoker().onUserHostReplyReceived(this, nickname, host, isOperator, isAway);
            }
            return false;
        }
        if (RPL_ISON.equals(command)) {
            onIsOnReplyReceived.invoker().onIsOnReplyReceived(this,
                    ImmutableList.copyOf(message.getParam(1).split(" ")));
            return false;
        }
        if (RPL_ISUPPORT.equals(command)) {
            ImmutableMap.Builder<String, List<String>> featureMapBuilder = ImmutableMap.builder();
            for (int i = 1; i < message.getParamCount(); i++) {
                String key = message.getParam(i);
                if (key.contains(" "))
                    break;
                String value = "";
                if (key.contains("=")) {
                    String[] parts = key.split("=");
                    key = parts[0];
                    value = parts[1];
                }
                List<String> values;
                if (value.isEmpty())
                    values = ImmutableList.of();
                else
                    values = ImmutableList.copyOf(value.split(","));
                featureMapBuilder.put(key, values);
            }
            onFeaturesAdvertised.invoker().onFeaturesAdvertised(this, featureMapBuilder.build());
            return false;
        }
        if ("PING".equals(command)) {
            // send PONG response
            sendCommand("PONG", message.getParam(0));
            return false;
        }
        if ("ERROR".equals(command)) {
            onTerminated.invoker().onTerminated(this, message.getParam(0));
            if (isConnected())
                disconnect();
            return false;
        }
        if ("NICK".equals(command)) {
            onNicknameChanged.invoker().onNicknameChanged(this, message.getSource(), message.getParam(0));
            return false;
        }
        if ("CAP".equals(command)) {
            if (capabilityNegotiator == null)
                return true;
            // TODO
            return false;
        }
        return true;
    }
}
