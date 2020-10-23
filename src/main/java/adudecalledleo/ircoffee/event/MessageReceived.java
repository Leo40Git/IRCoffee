package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;

@FunctionalInterface
public interface MessageReceived {
    void onMessageReceived(IRCClient client, IRCMessage message);
}
