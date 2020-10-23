package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

@FunctionalInterface
public interface Disconnected {
    void onDisconnected(IRCClient client);
}
