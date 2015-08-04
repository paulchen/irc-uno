package at.rueckgr.irc.bot.uno.util;

import at.rueckgr.irc.bot.uno.model.PlayerInfo;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.apache.commons.lang3.Validate;

import java.util.Iterator;
import java.util.Map;

public class UnoStateHelper {
    public static void updatePlayerInfo(final UnoState unoState, final Map<String, Long> extractedCardCounts) {
        Validate.notNull(unoState);
        Validate.notNull(extractedCardCounts);

        for (String playerName : extractedCardCounts.keySet()) {
            PlayerInfo playerInfo = unoState.getPlayers().get(playerName);
            if(playerInfo == null) {
                playerInfo = new PlayerInfo();
                playerInfo.setName(playerName);
            }

            playerInfo.setCardCount(extractedCardCounts.get(playerName));

            unoState.getPlayers().put(playerName, playerInfo);
        }

        Iterator<String> iterator = unoState.getPlayers().keySet().iterator();
        while(iterator.hasNext()) {
            String playerName = iterator.next();
            if(!extractedCardCounts.containsKey(playerName)) {
                iterator.remove();
            }
        }
    }
}
