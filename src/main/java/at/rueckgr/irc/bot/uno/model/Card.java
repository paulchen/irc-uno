package at.rueckgr.irc.bot.uno.model;

import java.text.MessageFormat;

public class Card {
    private final CardType cardType;
    private final Color color;

    public Card(CardType cardType, Color color) {
        this.cardType = cardType;
        this.color = color;
    }

    public CardType getCardType() {
        return cardType;
    }

    public Color getColor() {
        return color;
    }

    public String getLongName() {
        return MessageFormat.format("{0} {1}", color.getLongName(), cardType.getLongName());
    }
}
