package at.rueckgr.irc.bot.uno.commands;

import at.rueckgr.irc.bot.uno.Bot;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Random;

public class CurrentOrderCommand implements Command {
    private Random random = new Random();

    @Override
    public String handle(UnoState unoState, JSONObject object) {
        if(!object.containsKey("order")) {
            return null;
        }

        Object orderObject = object.get("order");
        if(!(orderObject instanceof List)) {
            return null;
        }

        List<?> currentOrder = (List<?>) orderObject;
        if(currentOrder.isEmpty()) {
            return null;
        }
        Object firstItem = currentOrder.get(0);
        if(!(firstItem instanceof String)) {
            return null;
        }
        String currentPlayerName = (String) firstItem;
        if(!Bot.NAME.equalsIgnoreCase(currentPlayerName)) {
            return null;
        }

        String playCommand = UnoHelper.createPlayCommand(unoState);
        if(playCommand == null) {
            return "!draw";
        }
        return playCommand;
    }
}
