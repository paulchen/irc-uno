package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.UnoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogHelper {
    private static final Logger logger = LoggerFactory.getLogger(LogHelper.class);

    public static void dumpState(UnoState unoState) {
        if(unoState == null) {
            logger.debug("Dumping UnoState: null");
            return;
        }

        logger.debug("Dumping UnoState:");

        if(unoState.getCurrentCard() != null) {
            logger.debug("Current card: {}", unoState.getCurrentCard().toString());
        }
        else {
            logger.debug("Current card: null");
        }

        if(unoState.getHand() != null) {
            logger.debug("Current hand: {}" + unoState.getHand().toString());
        }
        else {
            logger.debug("Current hand: null");
        }

        if(unoState.getPlayers() != null) {
            logger.debug("Current players ({}):", unoState.getPlayers().size());
            for (String playerName : unoState.getPlayers().keySet()) {
                logger.debug("Player {}: {}", playerName, unoState.getPlayers().get(playerName).toString());
            }
            logger.debug("End of current player list");
        }
        else {
            logger.debug("Current players: null");
        }
    }
}
