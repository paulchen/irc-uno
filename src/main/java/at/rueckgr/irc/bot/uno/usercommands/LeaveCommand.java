package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.util.UnoCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class LeaveCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(LeaveCommand.class);

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().startsWith(UnoCommands.REMOVE_BOT);
    }

    @Override
    public List<Action> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        message = message.trim();

        if(message.equals(UnoCommands.REMOVE_BOT)) {
            logger.debug("Leave command for all bots detected");

            return Collections.singletonList(new ChannelMessageAction(UnoCommands.LEAVE_GAME));
        }
        else if(message.equals(MessageFormat.format("{0} {1}", UnoCommands.REMOVE_BOT, botInfoProvider.getName()))) {
            logger.debug("Leave command for this bot detected");

            return Collections.singletonList(new ChannelMessageAction(UnoCommands.LEAVE_GAME));
        }

        return Collections.emptyList();
    }
}
