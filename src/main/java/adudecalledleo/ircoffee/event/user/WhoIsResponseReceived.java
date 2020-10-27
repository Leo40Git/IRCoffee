package adudecalledleo.ircoffee.event.user;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.User;

@FunctionalInterface
public interface WhoIsResponseReceived {
    void onWhoIsResponseReceived(IRCClient client, User user);
}
