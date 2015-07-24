package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;

public interface Event {
    String handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider);

    String getCommand();
}
