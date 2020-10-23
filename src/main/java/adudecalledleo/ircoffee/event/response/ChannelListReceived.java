package adudecalledleo.ircoffee.event.response;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.data.Channel;

import java.util.List;

@FunctionalInterface
public interface ChannelListReceived {
    void onChannelListReceived(IRCClient client, List<Channel> channels);
}
