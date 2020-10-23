package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.User;

@FunctionalInterface
public interface WhoIsResponseReceived {
    void onWhoIsResponseReceived(IRCClient client, User user);
}
