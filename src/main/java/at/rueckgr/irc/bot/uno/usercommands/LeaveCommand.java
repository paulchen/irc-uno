package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.events.BotInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class LeaveCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(LeaveCommand.class);

    private static final String INCOMING_LEAVE_COMMAND = "?leave";
    private static final String OUTGOING_LEAVE_COMMAND = "!leave";

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        if(nickname == null || message == null) {
            return false;
        }

        return message.trim().startsWith(INCOMING_LEAVE_COMMAND);
    }

    @Override
    public List<String> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        message = message.trim();

        List<String> result = new ArrayList<>();
        if(message.equals(INCOMING_LEAVE_COMMAND)) {
            logger.debug("Leave command for all bots detected");

            result.add(OUTGOING_LEAVE_COMMAND);
        }
        else if(message.equals(MessageFormat.format("{0} {1}", INCOMING_LEAVE_COMMAND, botInfoProvider.getName()))) {
            logger.debug("Leave command for this bot detected");

            result.add(OUTGOING_LEAVE_COMMAND);
        }

        return result;
    }
}
