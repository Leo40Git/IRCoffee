package adudecalledleo.ircoffee.event.user;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;

@FunctionalInterface
public interface IsOnReplyReceived {
    void onIsOnReplyReceived(IRCClient client, List<String> users);
}
