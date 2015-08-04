package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.LogHelper;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.util.UnoStateHelper;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CardCountsEvent implements Event {
    private static final Logger logger = LoggerFactory.getLogger(CardCountsEvent.class);

    private static final String COMMAND = "card_counts";

    @Override
    public Action handle(UnoState unoState, JSONObject object, BotInfoProvider botInfoProvider) {
        LogHelper.dumpState(unoState);

        if(!object.containsKey("counts")) {
            logger.debug("Discarding message; key not found");

            return null;
        }

        Object countsObject = object.get("counts");
        if(!(countsObject instanceof List)) {
            logger.debug("Discarding message; no List found; found instead: {}", countsObject.getClass().getName());

            return null;
        }

        List<?> cardCounts = (List<?>) countsObject;
        if(cardCounts.isEmpty()) {
            logger.debug("Discarding message; list of card counts is empty");

            return null;
        }

        Map<String, Long> extractedCardCounts = new HashMap<>();

        // validity check
        for (Object cardCount : cardCounts) {
            if(!(cardCount instanceof Map)) {
                logger.debug("Discarding message; no Map found; found instead: {}", cardCount.getClass().getName());

                return null;
            }
            Map<?, ?> cardCountMap = (Map<?, ?>) cardCount;
            if(cardCountMap.size() != 2 || !cardCountMap.containsKey("player") || !cardCountMap.containsKey("count")) {
                logger.debug("Discarding message; map does not contain the keys 'player' and 'count'");

                return null;
            }

            Object playerNameObject = cardCountMap.get("player");
            if(!(playerNameObject instanceof String)) {
                logger.debug("Discarding message; player name is not a String; found instead: {}", playerNameObject.getClass().getName());

                return null;
            }

            Object cardCountObject = cardCountMap.get("count");
            if(!(cardCountObject instanceof Number)) {
                logger.debug("Discarding message; card count is not a Number; found instead: {}", playerNameObject.getClass().getName());

                return null;
            }

            String playerName = (String) playerNameObject;
            long count = ((Number) cardCountObject).longValue();

            if(StringUtils.isBlank(playerName)) {
                logger.debug("Discarding message; player name is empty");

                return null;
            }
            if(count < 1) {
                logger.debug("Discarding message; card count {} is invalid", count);

                return null;
            }

            logger.debug("Extracted card count: Player {} has {} cards", playerName, count);

            extractedCardCounts.put(playerName, count);
        }

        // actually update UnoState
        UnoStateHelper.updatePlayerInfo(unoState, extractedCardCounts);

        logger.debug("Successfully updated player info");

        LogHelper.dumpState(unoState);

        return null;
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }
}
