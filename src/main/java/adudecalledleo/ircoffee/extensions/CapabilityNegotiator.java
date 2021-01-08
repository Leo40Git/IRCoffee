package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.CapabilityEvents;

import java.util.List;
import java.util.Map;

public abstract class CapabilityNegotiator extends ClientExtension implements
        CapabilityEvents.CapsReceived, CapabilityEvents.EnabledCapsReceived, CapabilityEvents.CapsAcknowledged,
        CapabilityEvents.CapsRejected, CapabilityEvents.CapsAdded, CapabilityEvents.CapsRemoved {
    @Override
    protected final void doInstall(IRCClient client) {
        client.onCapsReceived.register(this);
        client.onEnabledCapsReceived.register(this);
        client.onCapsAcknowledged.register(this);
        client.onCapsRejected.register(this);
        client.onCapsAdded.register(this);
        client.onCapsRemoved.register(this);
    }

    @Override
    protected final void doUninstall(IRCClient client) {
        client.onCapsReceived.unregister(this);
        client.onEnabledCapsReceived.unregister(this);
        client.onCapsAcknowledged.unregister(this);
        client.onCapsRejected.unregister(this);
        client.onCapsAdded.unregister(this);
        client.onCapsRemoved.unregister(this);
    }

    @Override
    public abstract void onCapsReceived(IRCClient client, Map<String, List<String>> capMap, boolean more);

    @Override
    public abstract void onCapsAcknowledged(IRCClient client, List<String> capList);

    @Override
    public abstract void onCapsRejected(IRCClient client, List<String> capList);

    @Override
    public abstract void onCapsAdded(IRCClient client, Map<String, List<String>> capMap);

    @Override
    public abstract void onCapsRemoved(IRCClient client, List<String> capList);

    @Override
    public void onEnabledCapsReceived(IRCClient client, List<String> capList, boolean more) { }

    protected final void finishNegotiations(IRCClient client) {
        client.sendCommand("CAP", "END");
    }
}
