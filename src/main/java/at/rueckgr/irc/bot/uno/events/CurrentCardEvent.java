package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused") // accessed via reflection
public class CurrentCardEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(CurrentCardEvent.class);

    private static final String COMMAND = "current_card";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("current_card")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object currentCardObject = object.get("current_card");
        if(!(currentCardObject instanceof String)) {
            logger.debug("Discarding message; no String found; found instead: {}", currentCardObject.getClass().getName());

            return null;
        }

        Card card = UnoHelper.cardFromString((String) currentCardObject);
        if(card != null) {
            logger.debug("Card found: {}", card.toString());

            unoState.setCurrentCard(card);
        }

        LogHelper.dumpState(unoState);

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
