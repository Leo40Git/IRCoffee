package adudecalledleo.ircoffee;

import adudecalledleo.ircoffee.event.*;
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

import java.nio.charset.StandardCharsets;
import java.util.*;

import static adudecalledleo.ircoffee.IRCNumerics.*;

public final class IRCClient {
    public final Event<MessageReceived> onMessageReceived = Event.create(MessageReceived.class, listeners -> message -> {
        for (MessageReceived listener : listeners)
            listener.onMessageReceived(message.copy());
    });
    public final Event<ChannelListReceived> onChannelListReceived = Event.create(ChannelListReceived.class, listeners -> channels -> {
        List<IRCChannel> listView = Collections.unmodifiableList(channels);
        for (ChannelListReceived listener : listeners)
            listener.onChannelListReceived(listView);
    });
    public final Event<UserListReceived> onUserListReceived = Event.create(UserListReceived.class, listeners -> (channel, users) -> {
        List<String> listView = Collections.unmodifiableList(users);
        for (UserListReceived listener : listeners)
            listener.onUserListReceived(channel, listView);
    });

    private String host = "127.0.0.1";
    private int port = -1;
    private boolean sslEnabled = false;

    private String initialNickname = "IRCoffee";
    private String username = "IRCoffee";
    private String realName = "IRCoffee User";

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

    public void connect() throws Exception {
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
    }

    public void disconnect() throws Exception {
        try {
            if (lastWriteFuture != null)
                lastWriteFuture.sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void send(IRCMessage message) {
        lastWriteFuture = ch.writeAndFlush(message + "\r\n");
    }

    public void sendCommand(String command, String... params) {
        send(IRCMessage.command(command, params));
    }

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
            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.wrappedBuffer(new byte[] { '\r', '\n' })));
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
                onMessageReceived.invoker().onMessageReceived(message);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private static class Lists {
        private List<IRCChannel> channelList;
        private final Map<String, List<String>> userLists = new HashMap<>();
    }
    private final Lists lists = new Lists();

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
            if (lists.channelList == null)
                lists.channelList = new ArrayList<>();
            String name = message.getParam(1);
            int clientCount = -1;
            try {
                clientCount = Integer.parseInt(message.getParam(2));
            } catch (Exception ignored) { }
            String topic = message.getParam(3);
            lists.channelList.add(new IRCChannel(name, clientCount, topic));
            return false;
        }
        if (RPL_LISTEND.equals(command)) {
            onChannelListReceived.invoker().onChannelListReceived(lists.channelList);
            lists.channelList = null;
            return false;
        }
        // user list stuff
        if (RPL_NAMREPLY.equals(command)) {
            String channel = message.getParam(2);
            List<String> userList = lists.userLists.computeIfAbsent(channel, key -> new ArrayList<>());
            String usersString = message.getParam(3);
            if (usersString != null)
                Collections.addAll(userList, usersString.split(" "));
            return false;
        }
        if (RPL_ENDOFNAMES.equals(command)) {
            String channel = message.getParam(1);
            List<String> userList = lists.userLists.remove(channel);
            if (userList != null)
                onUserListReceived.invoker().onUserListReceived(channel, userList);
            return false;
        }
        return true;
    }
}
