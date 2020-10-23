package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

@FunctionalInterface
public interface Bounced {
    void onBounced(IRCClient client, String newHost, int newPort, String info);
}
