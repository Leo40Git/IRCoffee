package adudecalledleo.ircoffee.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class User {
    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder {
        private final String nickname;
        private final String username;
        private final String host;
        private final String realName;
        private String server;
        private String serverInfo;
        private boolean isOperator;
        private boolean isIdle;
        private int secondsIdle;
        private long signOnTime;
        private final ImmutableList.Builder<String> channelsBuilder;

        private Builder(String nickname, String username, String host, String realName) {
            this.nickname = nickname;
            this.username = username;
            this.host = host;
            this.realName = realName;
            server = "";
            serverInfo = "";
            channelsBuilder = ImmutableList.builder();
        }

        public Builder setServer(String server, String serverInfo) {
            this.server = server;
            this.serverInfo = serverInfo;
            return this;
        }

        public Builder setOperator() {
            isOperator = true;
            return this;
        }

        public Builder setIdle(int secondsIdle, long signOnTime) {
            isIdle = true;
            this.secondsIdle = secondsIdle;
            this.signOnTime = signOnTime;
            return this;
        }

        public Builder addChannel(String channel) {
            channelsBuilder.add(channel);
            return this;
        }

        public Builder addChannels(String... channels) {
            channelsBuilder.add(channels);
            return this;
        }

        public User build() {
            return new User(nickname, username, host, realName,
                    server, serverInfo,
                    isOperator,
                    isIdle, secondsIdle, signOnTime,
                    channelsBuilder.build());
        }
    }

    public static Builder builder(String nickname, String username, String host, String realName) {
        return new Builder(nickname, username, host, realName);
    }

    // RPL_WHOISUSER
    private final String nickname;
    private final String username;
    private final String host;
    private final String realName;
    // RPL_WHOISSERVER
    private final String server;
    private final String serverInfo;
    // RPL_WHOISOPERATOR
    private final boolean isOperator;
    // RPL_WHOISIDLE
    private final boolean isIdle;
    private final int secondsIdle;
    private final long signOnTime;
    // RPL_WHOISCHANNELS
    private final List<String> channels;

    private User(String nickname, String username, String host, String realName, String server,
            String serverInfo, boolean isOperator, boolean isIdle,
            int secondsIdle, long signOnTime, List<String> channels) {
        this.nickname = nickname;
        this.username = username;
        this.host = host;
        this.realName = realName;
        this.server = server;
        this.serverInfo = serverInfo;
        this.isOperator = isOperator;
        this.isIdle = isIdle;
        this.secondsIdle = secondsIdle;
        this.signOnTime = signOnTime;
        this.channels = channels;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getHost() {
        return host;
    }

    public String getRealName() {
        return realName;
    }

    public String getServer() {
        return server;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public boolean isIdle() {
        return isIdle;
    }

    public int getSecondsIdle() {
        return secondsIdle;
    }

    public long getSignOnTime() {
        return signOnTime;
    }

    public List<String> getChannels() {
        return channels;
    }
}
