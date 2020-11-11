package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;

/**
 * A duplicate of {@link MessageReceived} that will only receive messages with the {@code "CAP"} command.
 *
 * <p>This event will only be fired if the client has
 * {@linkplain IRCClient#setCapsEnabled(boolean) capabilities enabled}.
 * Otherwise, {@code "CAP"} messages will be sent to the standard "message received" event.
 */
@FunctionalInterface
public interface CapMessageReceived {
    void onCapMessageReceived(IRCClient client, IRCMessage message);
}
