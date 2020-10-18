package adudecalledleo.ircoffee.test;

import adudecalledleo.ircoffee.IRCChannel;
import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class InteractiveTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter address (leave empty for localhost): ");
        String host = scanner.nextLine();
        if (StringUtil.isNullOrEmpty(host))
            host = "127.0.0.1";
        System.out.print("Enter port (leave empty for 6667): ");
        String portStr = scanner.nextLine();
        int port = 6667;
        if (!StringUtil.isNullOrEmpty(portStr)) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ignored) { }
        }
        System.out.print("Enter nickname (max 10 chars): ");
        String nickname = trunc10(scanner.nextLine());
        System.out.print("Enter username (max 10 chars): ");
        String username = trunc10(scanner.nextLine());
        System.out.print("Enter real name: ");
        String realName = scanner.nextLine();
        IRCClient client = new IRCClient();
        client.setHost(host);
        client.setPort(port);
        client.setInitialNickname(nickname);
        client.setUsername(username);
        client.setRealName(realName);
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
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            client.connect();
            while (true) {
                String line = in.readLine();
                if (StringUtil.isNullOrEmpty(line))
                    break;
                client.send(IRCMessage.fromString(line));
            }
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String trunc10(String str) {
        if (str.length() <= 10)
            return str;
        return str.substring(0, 10);
    }
}
