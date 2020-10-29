package adudecalledleo.ircoffee.event.user;

import adudecalledleo.ircoffee.IRCClient;

public interface UserHostReplyReceived {
    void onUserHostReplyReceived(IRCClient client, String nickname, String host, boolean isOperator, boolean isAway);
}
