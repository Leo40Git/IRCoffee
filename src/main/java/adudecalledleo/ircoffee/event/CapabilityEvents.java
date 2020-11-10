package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;
import java.util.Map;

public final class CapabilityEvents {
    private CapabilityEvents() { }

    public interface FeaturesAdvertised {
        void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap);
    }
}
