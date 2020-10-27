package adudecalledleo.ircoffee.event.user;

import adudecalledleo.ircoffee.IRCClient;

@FunctionalInterface
public interface NicknameChanged {
    void onNicknameChanged(IRCClient client, String oldNickname, String newNickname);
}
