package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;

import java.util.List;
import java.util.Map;

public final class CapabilityEvents {
    private CapabilityEvents() { }

    @FunctionalInterface
    public interface FeaturesAdvertised {
        void onFeaturesAdvertised(IRCClient client, Map<String, List<String>> featureMap);
    }

    /**
     * A duplicate of {@link MessageReceived} that will only receive messages with the {@code CAP} command.
     *
     * <p>This event will only be fired if the client has
     * {@linkplain IRCClient#setCapsEnabled(boolean) capabilities enabled}.
     * Otherwise, {@code CAP} messages will be sent to the standard "message received" event.
     */
    @FunctionalInterface
    public interface CapMessageReceived {
        void onCapMessageReceived(IRCClient client, IRCMessage message);
    }
}
