package adudecalledleo.ircoffee.util;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.event.capability.FeaturesAdvertised;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects supported features via the {@link FeaturesAdvertised} event.
 */
public final class FeaturesCollector implements FeaturesAdvertised {
    private final Map<String, List<String>> featureMap;

    public FeaturesCollector(IRCClient client) {
        featureMap = new HashMap<>();
        client.onFeaturesAdvertised.register(this);
    }

    @Override
    public void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap) {
        for (Map.Entry<String, List<String>> entry : featureMap.entrySet()) {
            if (entry.getKey().startsWith("-")) {
                this.featureMap.remove(entry.getKey().substring(1));
                continue;
            }
            this.featureMap.put(entry.getKey(), entry.getValue());
        }
    }

    public boolean hasFeature(String feature) {
        return featureMap.containsKey(feature);
    }

    public List<String> getFeatureParams(String feature) {
        return featureMap.get(feature);
    }
}
