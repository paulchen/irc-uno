package at.rueckgr.irc.bot.uno.actions;

import org.pircbotx.output.OutputChannel;

public interface Action {
    void execute(OutputChannel outputChannel);
}
