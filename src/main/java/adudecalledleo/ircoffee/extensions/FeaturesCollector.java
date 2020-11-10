package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.CapabilityEvents;
import adudecalledleo.ircoffee.event.Event;

import java.util.*;

/**
 * Collects supported features via the {@link CapabilityEvents.FeaturesAdvertised} event.
 */
public final class FeaturesCollector extends ClientExtension implements CapabilityEvents.FeaturesAdvertised {
    @FunctionalInterface
    public interface Updated {
        void onFeaturesUpdated(IRCClient client, FeaturesCollector collector);
    }

    public final Event<Updated> onFeaturesUpdated = Event.create(Updated.class, listeners -> (client, collector) -> {
        for (Updated listener : listeners)
            listener.onFeaturesUpdated(client, collector);
    });
    private final Map<String, List<String>> collectedFeatures;
    private final Set<String> keySetView;

    public FeaturesCollector() {
        collectedFeatures = new HashMap<>();
        keySetView = Collections.unmodifiableSet(collectedFeatures.keySet());
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

    public int getFeatureCount() {
        return collectedFeatures.size();
    }

    public Set<String> getAllFeatures() {
        return keySetView;
    }

    public boolean hasFeature(String feature) {
        return collectedFeatures.containsKey(feature);
    }

    public List<String> getFeatureParams(String feature) {
        return collectedFeatures.getOrDefault(feature, Collections.emptyList());
    }
}
