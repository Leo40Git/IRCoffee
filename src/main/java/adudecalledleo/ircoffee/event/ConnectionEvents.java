package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;

public final class ConnectionEvents {
    private ConnectionEvents() { }

    @FunctionalInterface
    public interface Connected {
        void onConnected(IRCClient client);
    }

    @FunctionalInterface
    public interface Disconnected {
        void onDisconnected(IRCClient client);
    }

    /**
     * Fired when the client receives an {@code ERROR} command from the server.<p>
     * The {@code ERROR} command means the server has closed the link to the client, meaning we're effectively disconnected.<br>
     * For convenience's sake, IRCoffee will automatically disconnect when this happens.
     */
    @FunctionalInterface
    public interface Terminated {
        void onTerminated(IRCClient client, String message);
    }

    /**
     * Fired when the client receives an {@link adudecalledleo.ircoffee.IRCNumerics#RPL_BOUNCE RPL_BOUNCE} command from the server.<p>
     * This message is sent to "redirect" the client to another server.<br>
     * For convenience's sake, IRCoffee will automatically follow the redirect, unless the client is explicitly disconnected.
     */
    @FunctionalInterface
    public interface Bounced {
        void onBounced(IRCClient client, String newHost, int newPort, String message);
    }
}
