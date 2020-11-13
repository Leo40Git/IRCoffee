package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import adudecalledleo.ircoffee.event.CapabilityEvents;
import adudecalledleo.ircoffee.event.Event;

import java.util.List;

/**
 * Facilitates <a href="https://ircv3.net/specs/core/capability-negotiation">capability negotiation</a>
 * for an {@link IRCClient} instance.
 *
 * <p>Make sure the client has {@linkplain IRCClient#setCapsEnabled(boolean) capabilities enabled}!
 */
public final class CapabilityNegotiator extends ClientExtension implements CapabilityEvents.CapMessageReceived {
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

    // TODO generic capability events
    public final Event<CapabilityNegotiator.SASLAuthAvailable> onSASLAuthAvailable = Event.create(
            SASLAuthAvailable.class, listeners -> (client, methods) -> {
                for (SASLAuthAvailable listener : listeners)
                    listener.onSASLAuthAvailable(client ,methods);
            });

    /**
     * Fired when SASL authentication becomes available, either on connection or later.
     */
    @FunctionalInterface
    public interface SASLAuthAvailable {
        void onSASLAuthAvailable(IRCClient client, List<String> methods);
    }
}
