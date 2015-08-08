package at.rueckgr.irc.bot.uno.actions;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.pircbotx.output.OutputChannel;

public interface Action {
    void execute(OutputChannel outputChannel, BotInfoProvider botInfoProvider);
}
