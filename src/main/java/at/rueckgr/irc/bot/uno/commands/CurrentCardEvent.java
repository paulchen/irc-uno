package at.rueckgr.irc.bot.uno.commands;

import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;

public class CurrentCardEvent implements Event {
    private static final String COMMAND = "current_card";

    @Override
    public String handle(UnoState unoState, JSONObject object) {
        if(!object.containsKey("current_card")) {
            return null;
        }

        Object currentCardObject = object.get("current_card");
        if(!(currentCardObject instanceof String)) {
            return null;
        }

        Card card = UnoHelper.cardFromString((String) currentCardObject);
        if(card != null) {
            unoState.setCurrentCard(card);
        }

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
