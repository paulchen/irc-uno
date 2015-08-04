package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;

import java.util.List;

public interface UserCommand {
    boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider);

    List<Action> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider);
}
