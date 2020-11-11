package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;

import java.util.function.Supplier;

/**
 * Represents a class which extends an {@link adudecalledleo.ircoffee.IRCClient IRCClinet} instance's functionality.
 *
 * <p><b>Note:</b> A client extension can only be installed on one client at a time!
 */
public abstract class ClientExtension {
    private IRCClient client;

    /**
     * Installs a client extension onto a client.
     * @param extensionSupplier supplier that creates extension instance to install
     * @param to client to install extension onto
     * @param <T> type of extension
     * @return newly created and installed extension
     */
    public static <T extends ClientExtension> T install(Supplier<T> extensionSupplier, IRCClient to) {
        T extension = extensionSupplier.get();
        extension.installOn(to);
        return extension;
    }

    /**
     * Gets the client this extension is currently installed on.
     * @return client, or {@code null} if the extension currently isn't installed onto any client
     */
    protected final IRCClient getClient() {
        return client;
    }

    /**
     * Checks if this extension is installed onto a client or not.
     * @return {@code true} if the extension is installed onto a client, {@code false} otherwise
     */
    public final boolean isInstalled() {
        return client != null;
    }

    /**
     * Installs this extension onto a client.
     *
     * <p>If already installed, uninstalls the extension from the previous client and installs it onto the specified client.
     * <p>Does nothing if the specified client is the client this extension is currently installed onto.
     * @param client client to install extension onto
     */
    public final void installOn(IRCClient client) {
        if (client == null)
            throw new NullPointerException("client == null!");
        if (this.client == client)
            return;
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
        if (isInstalled()) {
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
