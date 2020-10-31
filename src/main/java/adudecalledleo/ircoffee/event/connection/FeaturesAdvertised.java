package adudecalledleo.ircoffee.event.connection;

import adudecalledleo.ircoffee.IRCClient;

import java.util.List;
import java.util.Map;

public interface FeaturesAdvertised {
    void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap);
}
