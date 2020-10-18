package adudecalledleo.ircoffee.event;

import adudecalledleo.ircoffee.IRCMessage;

@FunctionalInterface
public interface MessageReceived {
    void onMessageReceived(IRCMessage message);
}
