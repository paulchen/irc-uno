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

@SuppressWarnings("unused") // accessed via reflection
public class PlayerDrewEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(PlayerDrewEvent.class);

    private static final String COMMAND = "player_drew_card";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("player")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object playerObject = object.get("player");
        if(!(playerObject instanceof String)) {
            logger.debug("Discarding message; no String found; found instead: {}", playerObject.getClass().getName());

            return null;
        }

        String playerName = (String) playerObject;
        if(!botInfoProvider.getName().equalsIgnoreCase(playerName)) {
            logger.debug("Discarding message; other player's turn");

            return null;
        }

        String playerCommand = UnoHelper.createPlayCommand(unoState, false);
        if(playerCommand == null) {
            logger.debug("Passing to next player");

            return new ChannelMessageAction(UnoCommands.PASS);
        }

        logger.debug("Play command: {}", playerCommand);
        return new ChannelMessageAction(playerCommand);
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
