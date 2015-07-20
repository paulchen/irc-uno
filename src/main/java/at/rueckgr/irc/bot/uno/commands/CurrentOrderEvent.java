package at.rueckgr.irc.bot.uno.commands;

import at.rueckgr.irc.bot.uno.Bot;
import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CurrentOrderEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(CurrentOrderEvent.class);

    private static final String COMMAND = "current_player_order";

    @Override
    public String handle(UnoState unoState, JSONObject object) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("order")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object orderObject = object.get("order");
        if(!(orderObject instanceof List)) {
            logger.debug("Discarding message; no List found; found instead: {}", orderObject.getClass().getName());

            return null;
        }

        List<?> currentOrder = (List<?>) orderObject;
        if(currentOrder.isEmpty()) {
            logger.debug("Discarding message; list of players is empty");

            return null;
        }
        Object firstItem = currentOrder.get(0);
        if(!(firstItem instanceof String)) {
            logger.debug("Discarding message; no String found; found instead: {}", firstItem.getClass().getName());

            return null;
        }
        String currentPlayerName = (String) firstItem;
        if(!Bot.NAME.equalsIgnoreCase(currentPlayerName)) {
            logger.debug("Discarding message; other player's turn: {}", currentPlayerName);

            return null;
        }

        String playCommand = UnoHelper.createPlayCommand(unoState);
        if(playCommand == null) {
            logger.debug("Drawing a card");

            return "!draw";
        }

        logger.debug("Created play command: {}", playCommand);

        return playCommand;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
