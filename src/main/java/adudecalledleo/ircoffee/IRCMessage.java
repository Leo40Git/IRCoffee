package adudecalledleo.ircoffee;

import io.netty.util.internal.StringUtil;

import java.util.*;

public final class IRCMessage {
    private final Map<String, String> tags = new HashMap<>();
    private String source = "";
    private String command = "";
    private final List<String> params = new ArrayList<>();

    public Map<String, String> getTags() {
        return tags;
    }

    public String getTag(String key) {
        return tags.get(key);
    }

    public void putTag(String key, String value) {
        tags.put(key, value);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getParams() {
        return params;
    }

    public int getParamCount() {
        return params.size();
    }

    public String getParam(int i) {
        if (i < 0 || i > params.size())
            return null;
        return params.get(i);
    }

    public void addParam(String param) {
        params.add(param);
    }

    public void clearParams() {
        params.clear();
    }

    public IRCMessage copy() {
        IRCMessage copy = new IRCMessage();
        copy.tags.putAll(tags);
        copy.source = source;
        copy.command = command;
        copy.params.addAll(params);
        return copy;
    }

    public static IRCMessage command(String command, String... params) {
        IRCMessage result = new IRCMessage();
        result.command = command;
        Collections.addAll(result.params, params);
        return result;
    }

    public static IRCMessage fromString(String string) {
        IRCMessage result = new IRCMessage();
        if (string.startsWith("@")) {
            // tags
            int tagsEnd = string.indexOf(' ');
            String tagsStr = string.substring(1, tagsEnd - 1);
            string = string.substring(tagsEnd + 1);
            String[] tagPairs = tagsStr.split(";");
            for (String key : tagPairs) {
                String value = "true";
                if (key.contains("=")) {
                    String[] parts = key.split("=");
                    key = parts[0];
                    value = parts[1];
                }
                result.putTag(key, value);
            }
        }
        if (string.startsWith(":")) {
            // source
            int srcEnd = string.indexOf(' ');
            result.setSource(string.substring(1, srcEnd - 1));
            string = string.substring(srcEnd + 1);
        }
        // command & params
        String[] parts = string.split(" ");
        result.setCommand(parts[0]);
        int i = 1;
        for (; i < parts.length; i++) {
            if (parts[i].startsWith(":"))
                break;
            result.addParam(parts[i]);
        }
        if (i == parts.length)
            return result;
        // trailing param
        StringBuilder trailingSB = new StringBuilder(parts[i++].substring(1) + " ");
        for (; i < parts.length; i++)
            trailingSB.append(parts[i]).append(' ');
        trailingSB.setLength(trailingSB.length() - 1);
        result.addParam(trailingSB.toString());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!tags.isEmpty())
            sb.append('@').append(concatTags(tags)).append(' ');
        if (!StringUtil.isNullOrEmpty(source))
            sb.append(':').append(source).append(' ');
        return sb.append(command).append(concatParams(params)).toString();
    }

    private static String concatTags(Map<String, String> tags) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : tags.entrySet())
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append(';');
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private static String concatParams(List<String> params) {
        StringBuilder sb = new StringBuilder();
        for (String param : params) {
            if (param.contains(" "))
                param = ":" + param;
            sb.append(' ').append(param);
            if (param.contains(":"))
                break;
        }
        return sb.toString();
    }
}
