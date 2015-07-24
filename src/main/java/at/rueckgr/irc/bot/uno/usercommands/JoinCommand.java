package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.events.BotInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class JoinCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(JoinCommand.class);

    private static final String INCOMING_JOIN_COMMAND = "?join";
    private static final String OUTGOING_JOIN_COMMAND = "!botjoin";

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().startsWith(INCOMING_JOIN_COMMAND);
    }

    @Override
    public List<String> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        message = message.trim();

        List<String> result = new ArrayList<>();
        if(message.equals(INCOMING_JOIN_COMMAND)) {
            logger.debug("Join command for all bots detected");

            result.add(OUTGOING_JOIN_COMMAND);
        }
        else if(message.equals(MessageFormat.format("{0} {1}", INCOMING_JOIN_COMMAND, botInfoProvider.getName()))) {
            logger.debug("Join command for this bot detected");

            result.add(OUTGOING_JOIN_COMMAND);
        }

        return result;
    }
}
