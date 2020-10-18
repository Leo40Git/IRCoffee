package adudecalledleo.ircoffee.event;

import java.util.List;

@FunctionalInterface
public interface UserListReceived {
    void onUserListReceived(String channel, List<String> users);
}
