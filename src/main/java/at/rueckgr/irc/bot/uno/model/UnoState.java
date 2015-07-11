package at.rueckgr.irc.bot.uno.model;

import lombok.Data;

import java.util.List;

@Data
public class UnoState {
    private Card currentCard;

    private List<Card> hand;

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }
}
