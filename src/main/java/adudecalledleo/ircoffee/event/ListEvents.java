package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.IRCChannel;

import java.util.List;

public final class ListEvents {
    @FunctionalInterface
    public interface ChannelsReceived {
        void onChannelsReceived(IRCClient client, List<IRCChannel> channels);
    }

    @FunctionalInterface
    public interface UsersInChannelReceived {
        void onUsersInChannelReceived(IRCClient client, String channel, List<String> users);
    }

    private ListEvents() { }
}
