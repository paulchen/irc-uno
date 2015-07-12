package at.rueckgr.irc.bot.uno.model;

import lombok.Data;

import java.util.List;

@Data
public class UnoState {
    private Card currentCard;

    private List<Card> hand;
}
