package adudecalledleo.ircoffee.test;

import adudecalledleo.ircoffee.IRCChannel;
import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OnlyTest {
    public static void main(String[] args) {
        IRCClient client = new IRCClient();
        client.onMessageReceived.register(System.err::println);
        client.onChannelListReceived.register(channels -> {
            System.err.format("%d channels in server:%n", channels.size());
            for (IRCChannel channel : channels)
                System.err.format("%s (%d): %s%n", channel.getName(), channel.getClientCount(), channel.getTopic());
        });
        client.onUserListReceived.register((channel, users) -> {
            System.err.println("Users currently in channel " + channel + ":");
            for (String user : users)
                System.err.println(user);
        });
        try {
            client.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (StringUtil.isNullOrEmpty(line)) {
                    client.disconnect();
                    break;
                }
                client.send(IRCMessage.fromString(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
