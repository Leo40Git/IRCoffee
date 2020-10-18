package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCChannel;

import java.util.List;

@FunctionalInterface
public interface ChannelListReceived {
    void onChannelListReceived(List<IRCChannel> channels);
}
