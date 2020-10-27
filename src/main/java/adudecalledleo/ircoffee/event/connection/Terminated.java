package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

/**
 * Fired when the client receives an {@code ERROR} command from the server.<p>
 * The {@code ERROR} command means the server has closed the link to the client, meaning we're effectively disconnected.<br>
 * For convenience's sake, IRCoffee will automatically disconnect when this happens.
 */
@FunctionalInterface
public interface Terminated {
    void onTerminated(IRCClient client, String message);
}
