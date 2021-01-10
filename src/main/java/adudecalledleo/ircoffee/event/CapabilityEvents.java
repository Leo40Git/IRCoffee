package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import com.google.common.collect.Multimap;

import java.util.List;

public final class CapabilityEvents {
    private CapabilityEvents() { }

    @FunctionalInterface
    public interface FeaturesAdvertised {
        void onFeaturesAdvertised(IRCClient client, Multimap<String, String> featureMap);
    }

    @FunctionalInterface
    public interface CapsReceived {
        void onCapsReceived(IRCClient client, Multimap<String, String> capMap, boolean more);
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
        void onCapsAdded(IRCClient client, Multimap<String, String> capMap);
    }

    @FunctionalInterface
    public interface CapsRemoved {
        void onCapsRemoved(IRCClient client, List<String> capList);
    }
}
