package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;
import java.util.Map;

public final class CapabilityEvents {
    private CapabilityEvents() { }

    @FunctionalInterface
    public interface FeaturesAdvertised {
        void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap);
    }

    @FunctionalInterface
    public interface CapsReceived {
        void onCapsReceived(IRCClient client, Map<String, List<String>> capMap, boolean more);
    }

    @FunctionalInterface
    public interface EnabledCapsReceived {
        void onEnabledCapsReceived(IRCClient client, List<String> capList, boolean more);
    }

    @FunctionalInterface
    public interface CapsAcknowledged {
        void onCapsAcknowledged(IRCClient client, List<String> capList);
    }

    @FunctionalInterface
    public interface CapsRejected {
        void onCapsRejected(IRCClient client, List<String> capList);
    }

    @FunctionalInterface
    public interface CapsAdded {
        void onCapsAdded(IRCClient client, Map<String, List<String>> capMap);
    }

    @FunctionalInterface
    public interface CapsRemoved {
        void onCapsRemoved(IRCClient client, List<String> capList);
    }
}
