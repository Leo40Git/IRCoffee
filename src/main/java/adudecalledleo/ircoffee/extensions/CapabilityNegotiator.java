package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import adudecalledleo.ircoffee.event.CapMessageReceived;

/**
 * Facilitates <a href="https://ircv3.net/specs/core/capability-negotiation">capability negotiation</a>
 * for an {@link IRCClient} instance.
 *
 * <p>Make sure the client has {@linkplain IRCClient#setCapsEnabled(boolean) capabilities enabled}!
 */
public class CapabilityNegotiator extends ClientExtension implements CapMessageReceived {
    @Override
    protected void doInstall(IRCClient client) {
        client.onCapMessageReceived.register(this);
    }

    @Override
    protected void doUninstall(IRCClient client) {
        client.onCapMessageReceived.register(this);
    }

    @Override
    public void onCapMessageReceived(IRCClient client, IRCMessage message) {
        // TODO actually implement capability negotiation
    }
}
