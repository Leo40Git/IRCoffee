package adudecalledleo.ircoffee.util;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.Event;
import adudecalledleo.ircoffee.event.capability.FeaturesAdvertised;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects supported features via the {@link FeaturesAdvertised} event.
 */
public final class FeaturesCollector implements FeaturesAdvertised {
    @FunctionalInterface
    public interface Updated {
        void onFeaturesUpdated(IRCClient client, FeaturesCollector collector);
    }

    private final Event<Updated> onFeaturesUpdated = Event.create(Updated.class, listeners -> (client, collector) -> {
        for (Updated listener : listeners)
            listener.onFeaturesUpdated(client, collector);
    });
    private final IRCClient client;
    private final Map<String, List<String>> featureMap;

    public FeaturesCollector(IRCClient client) {
        this.client = client;
        featureMap = new HashMap<>();
        client.onFeaturesAdvertised.register(this);
    }

    @Override
    public void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap) {
        if (this.client != client)
            return;
        for (Map.Entry<String, List<String>> entry : featureMap.entrySet()) {
            if (entry.getKey().startsWith("-")) {
                this.featureMap.remove(entry.getKey().substring(1));
                continue;
            }
            this.featureMap.put(entry.getKey(), entry.getValue());
        }
        onFeaturesUpdated.invoker().onFeaturesUpdated(client, this);
    }

    public boolean hasFeature(String feature) {
        return featureMap.containsKey(feature);
    }

    public List<String> getFeatureParams(String feature) {
        return featureMap.get(feature);
    }
}
