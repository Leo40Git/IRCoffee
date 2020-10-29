package adudecalledleo.ircoffee.event.list;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;

@FunctionalInterface
public interface UsersInChannelReceived {
    void onUsersInChannelReceived(IRCClient client, String channel, List<String> users);
}
