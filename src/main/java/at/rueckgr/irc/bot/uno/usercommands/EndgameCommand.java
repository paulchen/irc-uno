package at.rueckgr.irc.bot.uno.usercommands;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EndgameCommand implements UserCommand {
    private static final Logger logger = LoggerFactory.getLogger(EndgameCommand.class);

    private static final String INCOMING_ENDGAME_COMMAND = "?endgame";
    private static final String OUTGOING_ENDGAME_COMMAND = "!endgame";

    @Override
    public boolean isResponsible(String nickname, String message, BotInfoProvider botInfoProvider) {
        return message.trim().equals(INCOMING_ENDGAME_COMMAND);
    }

    @Override
    public List<String> handleMessage(String nickname, String message, BotInfoProvider botInfoProvider) {
        logger.debug("Command ?endgame detected");

        List<String> result = new ArrayList<>();
        result.add(OUTGOING_ENDGAME_COMMAND);
        return result;
    }
}
