package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.util.Util;
import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AutoplayCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(AutoplayCommand.class);

    private static final String AUTOPLAY_COMMAND = "?autoplay";

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().startsWith(AUTOPLAY_COMMAND);
    }

    @Override
    public List<Action> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        logger.debug("Command ?autoplay detected");

        message = message.trim();
        if(message.length() > AUTOPLAY_COMMAND.length()) {
            return Util.createAutoplayCommands(message.substring(AUTOPLAY_COMMAND.length() + 1));
        }

        return Util.createAutoplayCommands("");
    }
}
