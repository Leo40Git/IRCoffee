package adudecalledleo.ircoffee;

import adudecalledleo.ircoffee.data.Channel;
import adudecalledleo.ircoffee.data.User;
import adudecalledleo.ircoffee.event.Event;
import adudecalledleo.ircoffee.event.MessageReceived;
import adudecalledleo.ircoffee.event.connection.Bounced;
import adudecalledleo.ircoffee.event.connection.Connected;
import adudecalledleo.ircoffee.event.connection.Disconnected;
import adudecalledleo.ircoffee.event.response.ChannelListReceived;
import adudecalledleo.ircoffee.event.response.UserListReceived;
import adudecalledleo.ircoffee.event.response.WhoIsResponseReceived;
import com.google.common.collect.ImmutableList;
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
import java.util.Locale;
import java.util.Map;

import static adudecalledleo.ircoffee.IRCNumerics.*;

public final class IRCClient {
    public final Event<Connected> onConnected = Event.create(Connected.class, listeners -> client -> {
        for (Connected listener : listeners)
            listener.onConnected(client);
    });
    public final Event<Disconnected> onDisconnected = Event.create(Disconnected.class, listeners -> client -> {
        for (Disconnected listener : listeners)
            listener.onDisconnected(client);
    });
    public final Event<Bounced> onBounced = Event.create(Bounced.class, listeners -> (client, newHost, newPort, info) -> {
        for (Bounced listener : listeners)
            listener.onBounced(client, newHost, newPort, info);
    });
    public final Event<MessageReceived> onMessageReceived = Event.create(MessageReceived.class, listeners -> (client, message) -> {
        for (MessageReceived listener : listeners)
            listener.onMessageReceived(client, message.copy());
    });
    public final Event<WhoIsResponseReceived> onWhoIsResponseReceived = Event.create(WhoIsResponseReceived.class, listeners -> (client, user) -> {
        for (WhoIsResponseReceived listener : listeners)
            listener.onWhoIsResponseReceived(client, user);
    });
    public final Event<ChannelListReceived> onChannelListReceived = Event.create(ChannelListReceived.class, listeners -> (client, channels) -> {
        for (ChannelListReceived listener : listeners)
            listener.onChannelListReceived(client, channels);
    });
    public final Event<UserListReceived> onUserListReceived = Event.create(UserListReceived.class, listeners -> (client, channel, users) -> {
        for (UserListReceived listener : listeners)
            listener.onUserListReceived(client, channel, users);
    });

    private String host = "127.0.0.1";
    private int port = -1;
    private boolean sslEnabled = false;

    private String initialNickname = "IRCoffee";
    private String username = "IRCoffee";
    private String realName = "IRCoffee User";

    private EventLoopGroup group;
    private io.netty.channel.Channel ch;
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

    public void connect() throws SSLException, InterruptedException {
        if (isConnected())
            throw new IllegalStateException("Already connected!");
        try {
            if (port < 0)
                port = sslEnabled ? 6697 : 6667;
            SslContext sslCtx = null;
            if (sslEnabled)
                // TODO Swap InsecureTrustManagerFactory with something proper
                sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new Initializer(sslCtx));
            ch = b.connect(host, port).sync().channel();
            lastWriteFuture = null;
            sendCommand("NICK", initialNickname);
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
        if (group != null)
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
            pipeline.addLast(FRAME_DECODER);
            pipeline.addLast(DECODER);
            pipeline.addLast(ENCODER);
            pipeline.addLast(new Handler());
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
        private ImmutableList.Builder<Channel> channelListBuilder;
        private final Map<String, ImmutableList.Builder<String>> userListsBuilders = new HashMap<>();
        private final Map<String, User.Builder> whoIsBuilders = new HashMap<>();
    }
    private final Buffers buffers = new Buffers();

    private boolean handleMessage(IRCMessage message) {
        String command = message.getCommand().toUpperCase(Locale.ROOT);
        if ("PING".equals(command)) {
            send(IRCMessage.command("PONG", message.getParam(0)));
            return false;
        }
        // channel list stuff
        if (RPL_LISTSTART.equals(command))
            // this one isn't guaranteed, so ignore it
            return false;
        if (RPL_LIST.equals(command)) {
            if (buffers.channelListBuilder == null)
                buffers.channelListBuilder = ImmutableList.builder();
            String name = message.getParam(1);
            int clientCount = -1;
            try {
                clientCount = Integer.parseInt(message.getParam(2));
            } catch (Exception ignored) { }
            String topic = message.getParam(3);
            buffers.channelListBuilder.add(new Channel(name, clientCount, topic));
            return false;
        }
        if (RPL_LISTEND.equals(command)) {
            onChannelListReceived.invoker().onChannelListReceived(this, buffers.channelListBuilder.build());
            buffers.channelListBuilder = null;
            return false;
        }
        // user list stuff
        if (RPL_NAMREPLY.equals(command)) {
            String channel = message.getParam(2);
            ImmutableList.Builder<String> userListBuilder = buffers.userListsBuilders.computeIfAbsent(channel, key -> ImmutableList.builder());
            String usersString = message.getParam(3);
            if (usersString != null)
                userListBuilder.add(usersString.split(" "));
            return false;
        }
        if (RPL_ENDOFNAMES.equals(command)) {
            String channel = message.getParam(1);
            ImmutableList.Builder<String> userListBuilder = buffers.userListsBuilders.remove(channel);
            if (userListBuilder != null)
                onUserListReceived.invoker().onUserListReceived(this, channel, userListBuilder.build());
            return false;
        }
        // TODO ban list stuff
        // WHOIS stuff
        if (RPL_WHOISUSER.equals(command)) {
            String nickname = message.getParam(1);
            String username = message.getParam(2);
            String host = message.getParam(3);
            String realName = message.getParam(5);
            buffers.whoIsBuilders.put(nickname, User.builder(nickname, username, host, realName));
            return false;
        }
        if (RPL_WHOISSERVER.equals(command)) {
            User.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null)
                whoIsBuilder.setServer(message.getParam(2), message.getParam(3));
            return false;
        }
        if (RPL_WHOISOPERATOR.equals(command)) {
            User.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null)
                whoIsBuilder.setOperator();
            return false;
        }
        if (RPL_WHOISIDLE.equals(command)) {
            User.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null) {
                int secondsIdle;
                try {
                    secondsIdle = Integer.parseUnsignedInt(message.getParam(2));
                } catch (NumberFormatException e) {
                    return false;
                }
                long signOnTime;
                try {
                    signOnTime = Long.parseUnsignedLong(message.getParam(3));
                } catch (NumberFormatException ignored) {
                    // this one's optional; if parse failed, it's probably missing
                    signOnTime = -1;
                }
                whoIsBuilder.setIdle(secondsIdle, signOnTime);
            }
            return false;
        }
        if (RPL_WHOISCHANNELS.equals(command)) {
            User.Builder whoIsBuilder = buffers.whoIsBuilders.get(message.getParam(1));
            if (whoIsBuilder != null) {
                String[] channels = message.getParam(2).split(" ");
                for (String channel : channels)
                    whoIsBuilder.addChannel(channel.substring(1));
            }
            return false;
        }
        if (RPL_ENDOFWHOIS.equals(command)) {
            User.Builder whoIsBuilder = buffers.whoIsBuilders.remove(message.getParam(1));
            if (whoIsBuilder != null)
                onWhoIsResponseReceived.invoker().onWhoIsResponseReceived(this, whoIsBuilder.build());
            return false;
        }
        // standalone numerics
        if (RPL_BOUNCE.equals(command)) {
            String newHost = message.getParam(1);
            int newPort = sslEnabled ? 6697 : 6667;
            try {
                newPort = Integer.parseUnsignedInt(message.getParam(2));
            } catch (NumberFormatException ignored) { }
            onBounced.invoker().onBounced(this, newHost, newPort, message.getParam(3));
            if (!isConnected())
                return false;
            disconnect();
            setHost(newHost);
            setPort(newPort);
            try {
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
