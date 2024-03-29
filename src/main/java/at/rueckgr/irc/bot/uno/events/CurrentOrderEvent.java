package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.util.UnoCommands;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class CurrentOrderEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(CurrentOrderEvent.class);

    private static final String COMMAND = "current_player_order";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
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

        for (Object currentOrderObject : currentOrder) {
            if(!(currentOrderObject instanceof String)) {
                logger.debug("Discarding message; no String found; found instead: {}", currentOrderObject.getClass().getName());

                return null;
            }
        }

        //noinspection unchecked
        List<String> playerOrder = (List<String>) currentOrder;
        unoState.setPlayerOrder(playerOrder);

        String currentPlayerName = (String) currentOrder.get(0);
        if(!botInfoProvider.getName().equalsIgnoreCase(currentPlayerName)) {
            logger.debug("Discarding message; other player's turn: {}", currentPlayerName);

            return null;
        }

        String playCommand = UnoHelper.createPlayCommand(unoState, true);
        if(playCommand == null) {
            logger.debug("Drawing a card");

            return new ChannelMessageAction(UnoCommands.DRAW);
        }

        logger.debug("Created play command: {}", playCommand);

        return new ChannelMessageAction(playCommand);
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
