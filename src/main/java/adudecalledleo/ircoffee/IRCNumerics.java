package adudecalledleo.ircoffee;

import com.google.common.collect.ImmutableSet;

/**
 * Contains constants for every single numeric reply.<p>
 * These are server-to-client, and thus shouldn't be used to create new messages.
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public final class IRCNumerics {
    private IRCNumerics() { }

    public static String RPL_WELCOME = "001";
    public static String RPL_YOURHOST = "002";
    public static String RPL_CREATED = "003";
    public static String RPL_MYINFO = "004";
    /**
     * @deprecated This numeric has been repurposed for {@link #RPL_ISUPPORT}.<br>
     * Use {@link #RPL_BOUNCE} instead.
     */
    @Deprecated
    public static String RPL_BOUNCE_OLD = "005";
    public static String RPL_ISUPPORT = RPL_BOUNCE_OLD;
    public static String RPL_BOUNCE = "010";
    public static String RPL_UMODEIS = "221";
    public static String RPL_LUSERCLIENT = "251";
    public static String RPL_LUSEROP = "252";
    public static String RPL_LUSERUNKNOWN = "253";
    public static String RPL_LUSERCHANNELS = "254";
    public static String RPL_LUSERME = "255";
    public static String RPL_ADMINME = "256";
    public static String RPL_ADMINLOC1 = "257";
    public static String RPL_ADMINLOC2 = "258";
    public static String RPL_ADMINEMAIL = "259";
    public static String RPL_TRYAGAIN = "263";
    public static String RPL_LOCALUSERS = "265";
    public static String RPL_GLOBALUSERS = "266";
    public static String RPL_WHOISCERTFP = "276";
    public static String RPL_NONE = "300";
    public static String RPL_AWAY = "301";
    public static String RPL_USERHOST = "302";
    public static String RPL_ISON = "303";
    public static String RPL_UNAWAY = "305";
    public static String RPL_NOWAWAY = "306";
    public static String RPL_WHOISUSER = "311";
    public static String RPL_WHOISSERVER = "312";
    public static String RPL_WHOISOPERATOR = "313";
    public static String RPL_WHOWASUSER = "314";
    public static String RPL_WHOISIDLE = "317";
    public static String RPL_ENDOFWHOIS = "318";
    public static String RPL_WHOISCHANNELS = "319";
    public static String RPL_LISTSTART = "321";
    public static String RPL_LIST = "322";
    public static String RPL_LISTEND = "323";
    public static String RPL_CHANNELMODEIS = "324";
    public static String RPL_CREATIONTIME = "329";
    public static String RPL_NOTOPIC = "331";
    public static String RPL_TOPIC = "332";
    public static String RPL_TOPICWHOTIME = "333";
    public static String RPL_INVITING = "341";
    /**
     * @deprecated "No. Just, no." -<a href="https://modern.ircdocs.horse/#obsolete-numerics">IRC Client Protocol Specification</a>
     */
    @Deprecated
    public static String RPL_SUMMONING = "342";
    public static String RPL_INVITELIST = "346";
    public static String RPL_ENDOFINVITELIST = "347";
    public static String RPL_EXCEPTLIST = "348";
    public static String RPL_ENDOFEXCEPTLIST = "349";
    public static String RPL_VERSION = "351";
    public static String RPL_NAMREPLY = "353";
    public static String RPL_ENDOFNAMES = "366";
    public static String RPL_BANLIST = "367";
    public static String RPL_ENDOFBANLIST = "368";
    public static String RPL_ENDOFWHOWAS = "369";
    public static String RPL_MOTDSTART = "375";
    public static String RPL_MOTD = "372";
    public static String RPL_ENDOFMOTD = "376";
    public static String RPL_YOUREOPER = "381";
    public static String RPL_REHASHING = "382";
    public static String ERR_UNKNOWNERROR = "400";
    public static String ERR_NOSUCHNICK = "401";
    public static String ERR_NOSUCHSERVER = "402";
    public static String ERR_NOSUCHCHANNEL = "403";
    public static String ERR_CANNOTSENDTOCHAN = "404";
    public static String ERR_TOOMANYCHANNELS = "405";
    public static String ERR_UNKNOWNCOMMAND = "421";
    public static String ERR_NOMOTD = "422";
    public static String ERR_ERRONEUSNICKNAME = "432";
    public static String ERR_NICKNAMEINUSE = "433";
    public static String ERR_USERNOTINCHANNEL = "441";
    public static String ERR_NOTONCHANNEL = "442";
    public static String ERR_USERONCHANNEL = "443";
    public static String ERR_NOTREGISTERED = "451";
    public static String ERR_NEEDMOREPARAMS = "461";
    public static String ERR_ALREADYREGISTERED = "462";
    public static String ERR_PASSWDMISMATCH = "464";
    public static String ERR_YOUREBANNEDCREEP = "465";
    public static String ERR_CHANNELISFULL = "471";
    public static String ERR_UNKNOWNMODE = "472";
    public static String ERR_INVITEONLYCHAN = "473";
    public static String ERR_BANNEDFROMCHAN = "474";
    public static String ERR_BADCHANNELKEY = "475";
    public static String ERR_NOPRIVILEGES = "481";
    public static String ERR_CHANOPRIVSNEEDED = "482";
    public static String ERR_CANTKILLSERVER = "483";
    public static String ERR_NOOPERHOST = "491";
    public static String ERR_UMODEUNKNOWNFLAG = "501";
    public static String ERR_USERSDONTMATCH = "502";
    public static String RPL_STARTTLS = "670";
    public static String ERR_STARTTLS = "691";
    public static String ERR_NOPRIVS = "723";
    public static String RPL_LOGGEDIN = "900";
    public static String RPL_LOGGEDOUT = "901";
    public static String ERR_NICKLOCKED = "902";
    public static String RPL_SASLSUCCESS = "903";
    public static String ERR_SASLFAIL = "904";
    public static String ERR_SASLTOOLONG = "905";
    public static String ERR_SASLABORTED = "906";
    public static String ERR_SASLALREADY = "907";
    public static String RPL_SASLMECHS = "908";

    public static boolean isNumeric(String command) {
        if (command.length() != 3)
            return false;
        try {
            int x = Integer.parseUnsignedInt(command, 10);
        } catch (NumberFormatException ignored) {
            return false;
        }
        return true;
    }

    private static final ImmutableSet<String> ERRORS = ImmutableSet.of(
            ERR_UNKNOWNERROR,
            ERR_NOSUCHNICK,
            ERR_NOSUCHSERVER,
            ERR_NOSUCHCHANNEL,
            ERR_CANNOTSENDTOCHAN,
            ERR_TOOMANYCHANNELS,
            ERR_UNKNOWNCOMMAND,
            ERR_NOMOTD,
            ERR_ERRONEUSNICKNAME,
            ERR_NICKNAMEINUSE,
            ERR_USERNOTINCHANNEL,
            ERR_NOTONCHANNEL,
            ERR_USERONCHANNEL,
            ERR_NOTREGISTERED,
            ERR_NEEDMOREPARAMS,
            ERR_ALREADYREGISTERED,
            ERR_PASSWDMISMATCH,
            ERR_YOUREBANNEDCREEP,
            ERR_CHANNELISFULL,
            ERR_UNKNOWNMODE,
            ERR_INVITEONLYCHAN,
            ERR_BANNEDFROMCHAN,
            ERR_BADCHANNELKEY,
            ERR_NOPRIVILEGES,
            ERR_CHANOPRIVSNEEDED,
            ERR_CANTKILLSERVER,
            ERR_NOOPERHOST,
            ERR_UMODEUNKNOWNFLAG,
            ERR_USERSDONTMATCH,
            ERR_STARTTLS,
            ERR_NOPRIVS,
            ERR_NICKLOCKED,
            ERR_SASLFAIL,
            ERR_SASLTOOLONG,
            ERR_SASLABORTED,
            ERR_SASLALREADY
    );

    public boolean isError(String numeric) {
        return ERRORS.contains(numeric);
    }
}
