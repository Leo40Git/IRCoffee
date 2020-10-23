package adudecalledleo.ircoffee.data;

public final class Channel {
    private final String name;
    private final int clientCount;
    private final String topic;

    public Channel(String name, int clientCount, String topic) {
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
