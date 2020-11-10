package adudecalledleo.ircoffee.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class IRCWhoIsReply {
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
        private boolean hasSignOnTime;
        private long signOnTime;
        private final ImmutableList.Builder<String> channelsBuilder;
        private String certFingerprint;

        private Builder(String nickname, String username, String host, String realName) {
            this.nickname = nickname;
            this.username = username;
            this.host = host;
            this.realName = realName;
            server = "";
            serverInfo = "";
            channelsBuilder = ImmutableList.builder();
            certFingerprint = "";
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

        public Builder setIdle(int secondsIdle) {
            isIdle = true;
            this.secondsIdle = secondsIdle;
            return this;
        }

        public Builder setIdle(int secondsIdle, long signOnTime) {
            hasSignOnTime = true;
            this.signOnTime = signOnTime;
            return setIdle(secondsIdle);
        }

        public Builder addChannel(String channel) {
            channelsBuilder.add(channel);
            return this;
        }

        public Builder setCertFingerprint(String certFingerprint) {
            this.certFingerprint = certFingerprint;
            return this;
        }

        public IRCWhoIsReply build() {
            return new IRCWhoIsReply(nickname, username, host, realName,
                    server, serverInfo,
                    isOperator,
                    isIdle, secondsIdle, hasSignOnTime, signOnTime,
                    channelsBuilder.build(),
                    certFingerprint);
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
    private final boolean hasSignOnTime;
    private final long signOnTime;
    // RPL_WHOISCHANNELS
    private final List<String> channels;
    // RPL_WHOISCERTFP
    private final String certFingerprint;

    private IRCWhoIsReply(String nickname, String username, String host, String realName, String server,
            String serverInfo, boolean isOperator, boolean isIdle,
            int secondsIdle, boolean hasSignOnTime, long signOnTime, List<String> channels, String certFingerprint) {
        this.nickname = nickname;
        this.username = username;
        this.host = host;
        this.realName = realName;
        this.server = server;
        this.serverInfo = serverInfo;
        this.isOperator = isOperator;
        this.isIdle = isIdle;
        this.secondsIdle = secondsIdle;
        this.hasSignOnTime = hasSignOnTime;
        this.signOnTime = signOnTime;
        this.channels = channels;
        this.certFingerprint = certFingerprint;
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

    public boolean hasServerInfo() {
        return !server.isEmpty();
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

    public boolean hasSignOnTime() {
        return hasSignOnTime;
    }

    public long getSignOnTime() {
        return signOnTime;
    }

    public List<String> getChannels() {
        return channels;
    }

    public boolean hasCertFingerprint() {
        return !certFingerprint.isEmpty();
    }

    public String getCertFingerprint() {
        return certFingerprint;
    }

    @Override
    public String toString() {
        return "IRCWhoIsReply{" +
                "nickname='" + nickname + '\'' +
                ", username='" + username + '\'' +
                ", host='" + host + '\'' +
                ", realName='" + realName + '\'' +
                ", server='" + server + '\'' +
                ", serverInfo='" + serverInfo + '\'' +
                ", isOperator=" + isOperator +
                ", isIdle=" + isIdle +
                ", secondsIdle=" + secondsIdle +
                ", signOnTime=" + signOnTime +
                ", channels=" + channels +
                ", certFingerprint='" + certFingerprint + '\'' +
                '}';
    }
}
