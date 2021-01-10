package adudecalledleo.ircoffee.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.stream.Collectors;

public final class MultimapUtils {
    private MultimapUtils() { }

    public static Multimap<String, String> deserialize(String src) {
        ImmutableMultimap.Builder<String, String> mapBuilder = ImmutableMultimap.builder();
        String[] pairs = src.split(" ");
        for (String key : pairs) {
            String value = "";
            if (key.contains("=")) {
                String[] parts = key.split("=");
                key = parts[0];
                value = parts[1];
            }
            String[] values;
            if (value.isEmpty())
                values = new String[0];
            else
                values = value.split(",");
            mapBuilder.putAll(key, values);
        }
        return mapBuilder.build();
    }

    public static String serialize(Multimap<String, String> map) {
        return map.keySet().stream().map(key -> {
            if (map.get(key).isEmpty())
                return key;
            else
                return key + "=" + String.join(",", map.get(key));
        }).collect(Collectors.joining(" "));
    }
}
