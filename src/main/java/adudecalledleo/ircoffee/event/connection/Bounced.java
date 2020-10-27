package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

/**
 * Fired when the client receives an {@link adudecalledleo.ircoffee.IRCNumerics#RPL_BOUNCE RPL_BOUNCE} command from the server.<p>
 * This message is sent to "redirect" the client to another server.<br>
 * For convenience, IRCoffee will automatically follow the redirect, unless the client is explicitly disconnected.
 */
@FunctionalInterface
public interface Bounced {
    void onBounced(IRCClient client, String newHost, int newPort, String message);
}
