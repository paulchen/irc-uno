package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.events.BotInfoProvider;

import java.util.List;

public interface UserCommand {
    boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider);

    List<String> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider);
}
