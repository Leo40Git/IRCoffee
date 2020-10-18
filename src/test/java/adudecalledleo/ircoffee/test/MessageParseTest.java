package adudecalledleo.ircoffee.test;

import adudecalledleo.ircoffee.IRCMessage;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessageParseTest {
    @Test
    public void testMessageParsing() {
        String messageStr = "@test1=yes;test2 :CoffeeMan QUIT #thezone ::( I'm sad";
        IRCMessage message = IRCMessage.fromString(messageStr);
        assertEquals("Failed parsing key=value tag!", "yes", message.getTag("test1"));
        assertEquals("Failed parsing implicit key[=true] tag!", "true", message.getTag("test2"));
        assertEquals("Failed parsing source!", "CoffeeMan", message.getSource());
        assertEquals("Failed parsing command!", "QUIT", message.getCommand());
        assertEquals("Failed parsing parameters: incorrect count!", 2, message.getParamCount());
        assertEquals("Failed parsing parameters: standard parameter!", "#thezone", message.getParam(0));
        assertEquals("Failed parsing parameters: trailing parameter!", ":( I'm sad", message.getParam(1));
    }
}
