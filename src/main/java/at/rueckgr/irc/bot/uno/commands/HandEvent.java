package at.rueckgr.irc.bot.uno.commands;

import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HandEvent implements Event {
    private static final String COMMAND = "hand_info";

    @Override
    public String handle(UnoState unoState, JSONObject object) {
        if(!object.containsKey("hand")) {
            return null;
        }

        Object handObject = object.get("hand");
        if(!(handObject instanceof List)) {
            return null;
        }

        List<?> hand = (List<?>) handObject;
        List<Card> currentHand = new ArrayList<>();

        for (Object o : hand) {
            if(!(o instanceof String)) {
                return null;
            }

            String cardString = (String) o;
            Card card = UnoHelper.cardFromString(cardString);
            if(card == null) {
                return null;
            }

            currentHand.add(card);
        }

        unoState.setHand(currentHand);

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
