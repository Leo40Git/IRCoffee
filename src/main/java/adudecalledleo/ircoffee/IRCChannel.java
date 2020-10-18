package adudecalledleo.ircoffee;

public final class IRCChannel {
    private final String name;
    private final int clientCount;
    private final String topic;

    IRCChannel(String name, int clientCount, String topic) {
        this.name = name;
        this.clientCount = clientCount;
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public int getClientCount() {
        return clientCount;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public String toString() {
        return "IRCChannel{" +
                "name='" + name + '\'' +
                ", clientCount=" + clientCount +
                ", topic='" + topic + '\'' +
                '}';
    }
}
