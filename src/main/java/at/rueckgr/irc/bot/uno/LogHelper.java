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
    }
}