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
public class JoinCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(JoinCommand.class);

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().startsWith(UnoCommands.INVITE_BOT);
    }

    @Override
    public List<Action> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        message = message.trim();

        if(message.equals(UnoCommands.INVITE_BOT)) {
            logger.debug("Join command for all bots detected");

            return Collections.singletonList(new ChannelMessageAction(UnoCommands.BOTJOIN));
        }
        else if(message.equals(MessageFormat.format("{0} {1}", UnoCommands.INVITE_BOT, botInfoProvider.getName()))) {
            logger.debug("Join command for this bot detected");

            return Collections.singletonList(new ChannelMessageAction(UnoCommands.BOTJOIN));
        }

        return Collections.emptyList();
    }
}
