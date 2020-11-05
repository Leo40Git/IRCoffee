package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;

/**
 * Represents a class which extends an {@link adudecalledleo.ircoffee.IRCClient IRCClinet} instance's functionality.
 *
 * <p><b>Note:</b> A client extension can only be installed on one client at a time!
 */
public abstract class ClientExtension {
    private IRCClient client;

    /**
     * Gets the client this extension is currently installed on.
     * @return client, or {@code null} if the extension currently isn't installed onto any client
     */
    protected final IRCClient getClient() {
        return client;
    }

    /**
     * Installs this extension onto a client.
     *
     * <p>If already installed, uninstalls the extension from the previous client and installs it onto the specified client.
     * @param client client to install extension onto
     */
    public final void installOn(IRCClient client) {
        if (this.client != null)
            uninstall();
        this.client = client;
        doInstall(this.client);
    }

    /**
     * Uninstalls this extension from its current client.
     *
     * <p>Does nothing if this extension isn't installed onto a client.
     */
    public final void uninstall() {
        if (this.client != null) {
            doUninstall(client);
            client = null;
        }
    }

    /**
     * Performs installation operations on the client.
     * @param client client to install onto
     */
    protected abstract void doInstall(IRCClient client);

    /**
     * Performs uninstallation operations on the client.
     * @param client client to uninstall from
     */
    protected abstract void doUninstall(IRCClient client);
}
