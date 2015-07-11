package at.rueckgr.irc.bot.uno.commands;

import at.rueckgr.irc.bot.uno.Bot;
import at.rueckgr.irc.bot.uno.UnoHelper;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;

public class PlayerDrewCommand implements Command {
    private static final String COMMAND = "player_drew_card";

    @Override
    public String handle(UnoState unoState, JSONObject object) {
        if(!object.containsKey("player")) {
            return null;
        }

        Object playerObject = object.get("player");
        if(!(playerObject instanceof String)) {
            return null;
        }

        String playerName = (String) playerObject;
        if(!Bot.NAME.equalsIgnoreCase(playerName)) {
            return null;
        }

        String playerCommand = UnoHelper.createPlayCommand(unoState);
        if(playerCommand == null) {
            return "!pass";
        }
        return playerCommand;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
