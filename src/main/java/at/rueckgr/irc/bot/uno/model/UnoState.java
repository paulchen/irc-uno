package at.rueckgr.irc.bot.uno.model;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UnoState {
    private Card currentCard;

    private List<Card> hand;

    private Map<String, PlayerInfo> players = new HashMap<>();

    private List<String> playerOrder;
}
