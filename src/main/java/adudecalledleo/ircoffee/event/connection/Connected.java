package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

@FunctionalInterface
public interface Connected {
    void onConnected(IRCClient clinet);
}
