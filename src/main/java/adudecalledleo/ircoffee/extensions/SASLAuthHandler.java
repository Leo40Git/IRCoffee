package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;

/**
 * Handles SASL authentication.
 *
 * <p>Unlike other client extensions, this one isn't installed on the client itself - it's added as a
 * listener to {@link CapabilityNegotiator}'s
 * {@link CapabilityNegotiator.SASLAuthAvailable SASLAuthAvailable} event. As such, this can be installed
 * on multiple {@link CapabilityNegotiator}s at a time.
 */
public final class SASLAuthHandler implements CapabilityNegotiator.SASLAuthAvailable {
    @Override
    public void onSASLAuthAvailable(IRCClient client, List<String> methods) {
        // TODO implement this...
    }
}
