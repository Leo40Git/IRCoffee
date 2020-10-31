package adudecalledleo.ircoffee.event.capability;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;
import java.util.Map;

public interface FeaturesAdvertised {
    void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap);
}
