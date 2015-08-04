package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class CardPlayedEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(CardPlayedEvent.class);

    private static final String COMMAND = "card_played";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("player") || !object.containsKey("card")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object playerObject = object.get("player");
        if(!(playerObject instanceof String)) {
            logger.debug("Discarding message; no String found; found instead: {}", playerObject.getClass().getName());

            return null;
        }
        String playerName = (String) playerObject;

        Object cardObject = object.get("card");
        if(!(cardObject instanceof String)) {
            logger.debug("Discarding message; no String found; found instead: {}", playerObject.getClass().getName());

            return null;
        }
        String cardString = (String) cardObject;

        Card card = UnoHelper.cardFromString(cardString);
        if(card == null) {
            logger.debug("Discarding message; can't parse card from string: {}", cardString);

            return null;
        }

        logger.debug("Player {} played card {}", playerName, card.toString());

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
