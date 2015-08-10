package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class GameEndedEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(GameEndedEvent.class);

    private static final String COMMAND = "game_ended";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        logger.debug("Game ended");

        return new ChannelMessageAction("!elo");
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
