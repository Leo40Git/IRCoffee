package adudecalledleo.ircoffee.test;

import adudecalledleo.ircoffee.data.Channel;
import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
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
        System.out.print("Enter nickname: ");
        String nickname = scanner.nextLine();
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter real name: ");
        String realName = scanner.nextLine();
        IRCClient client = new IRCClient();
        client.setHost(host);
        client.setPort(port);
        client.setInitialNickname(nickname);
        client.setUsername(username);
        client.setRealName(realName);
        client.onMessageReceived.register((client2, message) -> System.err.println(message));
        client.onWhoIsResponseReceived.register((client2, user) -> {
            System.err.println("WHOIS response:");
            System.err.format("%s (%s), host: %s, real name: %s%n",
                    user.getNickname(), user.getUsername(), user.getHost(), user.getRealName());
            if (!user.getServer().isEmpty())
                System.err.format("Connected to server %s: %s%n", user.getServer(), user.getServerInfo());
            if (user.isOperator())
                System.err.println("Operator");
            if (user.isIdle()) {
                long signOnTime = user.getSignOnTime();
                if (signOnTime < 0)
                    System.err.format("Idle for %d seconds%n", user.getSecondsIdle());
                else
                    System.err.format("Idle for %d seconds since %s%n", user.getSecondsIdle(),
                            Instant.ofEpochSecond(signOnTime).toString());
            }
            List<String> channels = user.getChannels();
            if (!channels.isEmpty())
                System.err.format("In the following channels: %s%n", String.join(", ", channels));
            System.err.println("END WHOIS response");
        });
        client.onChannelListReceived.register((client2, channels) -> {
            System.err.format("%d channels in server:%n", channels.size());
            for (Channel channel : channels)
                System.err.format("%s (%d): %s%n", channel.getName(), channel.getClientCount(), channel.getTopic());
            System.err.println("END Channel list");
        });
        client.onUserListReceived.register((client2, channel, users) -> {
            System.err.format("%d users currently in channel %s:%n", users.size(), channel);
            for (String user : users)
                System.err.println(user);
            System.err.println("END User list");
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
}
