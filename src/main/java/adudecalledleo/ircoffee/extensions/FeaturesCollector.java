package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.Event;
import adudecalledleo.ircoffee.event.capability.FeaturesAdvertised;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects supported features via the {@link FeaturesAdvertised} event.
 */
public final class FeaturesCollector extends ClientExtension implements FeaturesAdvertised {
    @FunctionalInterface
    public interface Updated {
        void onFeaturesUpdated(IRCClient client, FeaturesCollector collector);
    }

    private final Event<Updated> onFeaturesUpdated = Event.create(Updated.class, listeners -> (client, collector) -> {
        for (Updated listener : listeners)
            listener.onFeaturesUpdated(client, collector);
    });
    private final Map<String, List<String>> collectedFeatures;

    public FeaturesCollector() {
        collectedFeatures = new HashMap<>();
    }

    @Override
    protected void doInstall(IRCClient client) {
        client.onFeaturesAdvertised.register(this);
    }

    @Override
    protected void doUninstall(IRCClient client) {
        client.onFeaturesAdvertised.unregister(this);
    }

    @Override
    public void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap) {
        if (getClient() != client)
            return;
        for (Map.Entry<String, List<String>> entry : featureMap.entrySet()) {
            if (entry.getKey().startsWith("-")) {
                this.collectedFeatures.remove(entry.getKey().substring(1));
                continue;
            }
            this.collectedFeatures.put(entry.getKey(), entry.getValue());
        }
        onFeaturesUpdated.invoker().onFeaturesUpdated(client, this);
    }

    public boolean hasFeature(String feature) {
        return collectedFeatures.containsKey(feature);
    }

    public List<String> getFeatureParams(String feature) {
        return collectedFeatures.get(feature);
    }
}
