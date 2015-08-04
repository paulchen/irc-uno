package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.json.simple.JSONObject;

public interface Event {
    Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider);

    String getCommand();
}
