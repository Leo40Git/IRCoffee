package adudecalledleo.ircoffee.test;

import adudecalledleo.ircoffee.data.IRCChannel;
import adudecalledleo.ircoffee.IRCClient;
import adudecalledleo.ircoffee.IRCMessage;
import adudecalledleo.ircoffee.extensions.CapabilityNegotiator;
import adudecalledleo.ircoffee.extensions.ClientExtension;
import adudecalledleo.ircoffee.extensions.FeaturesCollector;
import adudecalledleo.ircoffee.extensions.SASLAuthHandler;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLITest {
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

        client.onConnected.register(client1 -> System.err.println("Connected to server."));
        client.onDisconnected.register(client1 ->  System.err.println("Disconnected from server."));
        client.onTerminated.register((client1, message) ->
                System.err.format("Connection terminated by server: %s%n", message));
        client.onBounced.register((client1, newHost, newPort, info) ->
                System.err.format("Bouncing to %s:%d: %s%n", newHost, newPort, info));
        client.onMessageReceived.register((client1, message) -> System.err.println(message));
        client.onWhoIsReplyReceived.register((client1, whoIsReply) -> {
            System.err.println("WHOIS reply:");
            System.err.format("%s (%s), host: %s, real name: %s%n",
                    whoIsReply.getNickname(), whoIsReply.getUsername(), whoIsReply.getHost(), whoIsReply.getRealName());
            if (whoIsReply.hasServerInfo())
                System.err.format("Connected to server %s: %s%n", whoIsReply.getServer(), whoIsReply.getServerInfo());
            if (whoIsReply.isOperator())
                System.err.println("Operator");
            if (whoIsReply.hasCertFingerprint())
                System.err.format("Has client certificate fingerprint %s%n", whoIsReply.getCertFingerprint());
            if (whoIsReply.isIdle()) {
                if (whoIsReply.hasSignOnTime())
                    System.err.format("Idle for %d seconds%n", whoIsReply.getSecondsIdle());
                else
                    System.err.format("Idle for %d seconds since %s%n", whoIsReply.getSecondsIdle(),
                            Instant.ofEpochSecond(whoIsReply.getSignOnTime()).toString());
            }
            List<String> channels = whoIsReply.getChannels();
            if (!channels.isEmpty())
                System.err.format("In the following channels: %s%n", String.join(", ", channels));
            System.err.println("END WHOIS reply");
        });
        client.onChannelsReceived.register((client1, channels) -> {
            System.err.format("%d channels in server:%n", channels.size());
            for (IRCChannel channel : channels)
                System.err.format("- %s (%d): %s%n", channel.getName(), channel.getClientCount(), channel.getTopic());
            System.err.println("END Channel list");
        });
        client.onUsersInChannelReceived.register((client1, channel, users) -> {
            System.err.format("%d users currently in channel %s:%n", users.size(), channel);
            for (String user : users)
                System.err.format(" - %s%n", user);
            System.err.println("END User list");
        });
        client.onNicknameChanged.register((client1, oldNickname, newNickname) ->
                System.err.format("%s is now known as %s%n", oldNickname, newNickname));
        client.onUserHostReplyReceived.register((client1, nickname1, host1, isOperator, isAway) ->
                System.err.format("%s, host: %s, operator: %b, away: %b", nickname1, host1, isOperator, isAway));
        client.onIsOnReplyReceived.register((client1, users) -> {
            System.err.println("The following uses are on the server:");
            for (String user : users)
                System.err.format(" - %s%n", user);
            System.err.println("END ISON reply");
        });

        FeaturesCollector featuresCollector = ClientExtension.install(FeaturesCollector::new, client);
        featuresCollector.onFeaturesUpdated.register((client1, collector) -> {
            System.err.format("Got %d features:%n", collector.getFeatureCount());
            for (String feature : collector.getAllFeatures()) {
                List<String> params = collector.getFeatureParams(feature);
                if (params.isEmpty())
                    System.err.format(" - %s%n", feature);
                else
                    System.err.format(" - %s = %s%n", feature, String.join(", ", params));
            }
        });
        client.setCapsEnabled(true);
        CapabilityNegotiator capabilityNegotiator = ClientExtension.install(CapabilityNegotiator::new, client);
        // TODO generic cap events
        SASLAuthHandler saslAuthHandler = new SASLAuthHandler();
        capabilityNegotiator.onSASLAuthAvailable.register(saslAuthHandler);

        try(InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader in = new BufferedReader(isr)) {
            client.connect();
            while (client.isConnected()) {
                String line = in.readLine();
                if (client.isConnected()) {
                    if (StringUtil.isNullOrEmpty(line)) {
                        client.disconnect();
                        break;
                    }
                    client.send(IRCMessage.fromString(line));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
