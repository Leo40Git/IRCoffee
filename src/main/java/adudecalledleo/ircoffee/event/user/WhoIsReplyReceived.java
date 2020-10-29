package adudecalledleo.ircoffee.event.user;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.User;

@FunctionalInterface
public interface WhoIsReplyReceived {
    void onWhoIsReplyReceived(IRCClient client, User user);
}
