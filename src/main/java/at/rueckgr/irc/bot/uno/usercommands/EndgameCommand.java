package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.util.UnoCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class EndgameCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(EndgameCommand.class);

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().equalsIgnoreCase(UnoCommands.INCOMING_ENDGAME);
    }

    @Override
    public List<Action> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        logger.debug("Command ?endgame detected");

        return Collections.singletonList(new ChannelMessageAction(UnoCommands.OUTGOING_ENDGAME));
    }
}
