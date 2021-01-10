package adudecalledleo.ircoffee.extensions;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.CapabilityEvents;
import adudecalledleo.ircoffee.event.Event;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

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
    private final Multimap<String, String> collectedFeatures;
    private final Set<String> keySetView;

    public FeaturesCollector() {
        collectedFeatures = HashMultimap.create();
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
    public void onFeaturesAdvertised(IRCClient client, Multimap<String, String> featureMap) {
        if (getClient() != client)
            return;
        for (String key : collectedFeatures.keySet()) {
            if (key.startsWith("-")) {
                this.collectedFeatures.removeAll(key.substring(1));
                continue;
            }
            this.collectedFeatures.putAll(key, featureMap.get(key));
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

    public Collection<String> getFeatureParams(String feature) {
        if (!collectedFeatures.containsKey(feature))
            return Collections.emptySet();
        return collectedFeatures.get(feature);
    }
}
