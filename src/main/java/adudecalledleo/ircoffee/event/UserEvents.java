package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.IRCUser;

import java.util.List;

public final class UserEvents {
    @FunctionalInterface
    public interface IsOnReplyReceived {
        void onIsOnReplyReceived(IRCClient client, List<String> users);
    }

    @FunctionalInterface
    public interface NicknameChanged {
        void onNicknameChanged(IRCClient client, String oldNickname, String newNickname);
    }

    @FunctionalInterface
    public interface UserHostReplyReceived {
        void onUserHostReplyReceived(IRCClient client, String nickname, String host, boolean isOperator, boolean isAway);
    }

    @FunctionalInterface
    public interface WhoIsReplyReceived {
        void onWhoIsReplyReceived(IRCClient client, IRCUser user);
    }

    private UserEvents() { }
}
