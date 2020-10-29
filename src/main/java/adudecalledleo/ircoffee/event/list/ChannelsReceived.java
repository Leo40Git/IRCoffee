package adudecalledleo.ircoffee.event.list;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.Channel;

import java.util.List;

@FunctionalInterface
public interface ChannelsReceived {
    void onChannelsReceived(IRCClient client, List<Channel> channels);
}
