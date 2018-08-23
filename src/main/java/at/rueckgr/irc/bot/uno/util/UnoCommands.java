package at.rueckgr.irc.bot.uno.util;

public interface UnoCommands {
    String INVITE_PRINCESS = "!ai";
    String START_GAME = "!uno";
    String INVITE_BOT = "?join";
    String LEAVE_GAME = "!leave";
    String REMOVE_BOT = "?leave";
    String BOTJOIN = "!botjoin";
    String DEAL = "!deal";
    String DRAW = "!draw";
    String PASS = "!pass";
    String PLAY = "!play";
    String ELO = "!elo";

    String INCOMING_ENDGAME = "?endgame";
    String OUTGOING_ENDGAME = "!endgame";
}
