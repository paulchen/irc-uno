package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.CardType;
import at.rueckgr.irc.bot.uno.model.Color;
import at.rueckgr.irc.bot.uno.model.UnoState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnoHelper {
    public static Card cardFromString(String cardString) {
        String[] parts = cardString.split(" ");
        if(parts.length != 2) {
            return null;
        }

        Color color = colorFromString(parts[0]);
        CardType cardType = cardTypeFromString(parts[1]);

        if(color != null && cardType != null) {
            return new Card(cardType, color);
        }

        return null;
    }

    private static CardType cardTypeFromString(String cardTypeString) {
        for (CardType cardType : CardType.values()) {
            if(cardType.getShortName().equalsIgnoreCase(cardTypeString) || cardType.getLongName().equalsIgnoreCase(cardTypeString)) {
                return cardType;
            }
        }

        return null;
    }

    private static Color colorFromString(String colorString) {
        for (Color color : Color.values()) {
            if(color.getShortName().equalsIgnoreCase(colorString) || color.getLongName().equalsIgnoreCase(colorString)) {
                return color;
            }
        }

        return null;
    }

    private static List<Card> findCompatibleCards(List<Card> currentHand, Card currentCard) {
        List<Card> result = new ArrayList<>();

        for (Card card : currentHand) {
            if(card.isWildcard()) {
                result.add(card);
            }
            else if(card.getColor().equals(currentCard.getColor())) {
                result.add(card);
            }
            else if (card.getCardType().equals(currentCard.getCardType())) {
                result.add(card);
            }
        }

        return result;
    }

    public static String createPlayCommand(UnoState unoState) {
        List<Card> compatibleCards = findCompatibleCards(unoState.getHand(), unoState.getCurrentCard());
        if(compatibleCards.isEmpty()) {
            return null;
        }

        Card selectedCard = UnoStrategy.chooseCard(unoState, compatibleCards);

        String cardName;
        if(selectedCard.isWildcard()) {
            cardName = selectedCard.getCardType().getLongName() + " " + UnoStrategy.getWildcardColor(unoState).getLongName();
        }
        else {
            cardName = selectedCard.getLongName();
        }
        return "!play " + cardName;
    }
}
