package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class PlayerPassedEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(PlayerPassedEvent.class);

    private static final String COMMAND = "player_passed";

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

        logger.debug("Player passed: {}", playerName);

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
