package adudecalledleo.ircoffee.event.response;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;

@FunctionalInterface
public interface UserListReceived {
    void onUserListReceived(IRCClient client, String channel, List<String> users);
}
