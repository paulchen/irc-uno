package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HandEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(HandEvent.class);

    private static final String COMMAND = "hand_info";

    @Override
    public String handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("hand")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object handObject = object.get("hand");
        if(!(handObject instanceof List)) {
            logger.debug("Discarding message; no List found; found instead: {}", handObject.getClass().getName());

            return null;
        }

        List<?> hand = (List<?>) handObject;
        List<Card> currentHand = new ArrayList<>();

        for (Object o : hand) {
            if(!(o instanceof String)) {
                logger.debug("Discarding message; no String found; found instead: {}", o.getClass().getName());

                return null;
            }

            String cardString = (String) o;
            Card card = UnoHelper.cardFromString(cardString);
            if(card == null) {
                logger.debug("Discarding message; can't parse card from string: {}", cardString);

                return null;
            }

            currentHand.add(card);
        }

        unoState.setHand(currentHand);

        LogHelper.dumpState(unoState);

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
