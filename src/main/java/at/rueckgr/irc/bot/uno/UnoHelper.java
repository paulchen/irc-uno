package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.CardType;
import at.rueckgr.irc.bot.uno.model.Color;
import at.rueckgr.irc.bot.uno.model.PanicInfo;
import at.rueckgr.irc.bot.uno.model.PanicMode;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnoHelper {
    private static final Logger logger = LoggerFactory.getLogger(UnoHelper.class);

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

    public static List<Card> findCompatibleCards(List<Card> currentHand, Card currentCard) {
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

    public static String createPlayCommand(UnoState unoState, boolean drawingPossible) {
        List<Card> compatibleCards = findCompatibleCards(unoState.getHand(), unoState.getCurrentCard());
        if(compatibleCards.isEmpty()) {
            return null;
        }

        Card selectedCard = UnoStrategy.chooseCard(unoState, compatibleCards, drawingPossible);

        if(selectedCard == null) {
            if(drawingPossible) {
                return "!draw";
            }
            else {
                return "!pass";
            }
        }

        String cardName;
        if(selectedCard.isWildcard()) {
            Color wildcardColor = selectedCard.getColor();
            if(Color.WILD.equals(wildcardColor)) {
                wildcardColor = UnoStrategy.getWildcardColor(unoState);
            }
            cardName = selectedCard.getCardType().getLongName() + " " + wildcardColor.getLongName();
        }
        else {
            cardName = selectedCard.getLongName();
        }
        return "!play " + cardName;
    }

    public static PanicInfo getPanicInfo(UnoState unoState) {
        List<PanicMode> panicModes = getPanicModes(unoState);

        logger.debug("Detected panic modes: {}", panicModes.toString());

        if(panicModes.contains(PanicMode.FIRST_PLAYER_UNO)) {
            // no forbidden cards; prefer D2, WD4, S, R (in this order); draw if possible
            return new PanicInfo(panicModes, Collections.<CardType>emptyList(), Arrays.asList(CardType.DRAW2, CardType.WD4, CardType.SKIP, CardType.REVERSE), true);
        }
        else if(panicModes.contains(PanicMode.PREVIOUS_PLAYER_UNO)) {
            if(panicModes.contains(PanicMode.SECOND_PLAYER_UNO)) {
                // forbidden cards: R, D2, WD4, S
                return new PanicInfo(panicModes, Arrays.asList(CardType.REVERSE, CardType.DRAW2, CardType.WD4, CardType.SKIP), Collections.<CardType>emptyList(), false);
            }
            else {
                // forbidden cards: R
                return new PanicInfo(panicModes, Collections.singletonList(CardType.REVERSE), Collections.<CardType>emptyList(), false);
            }
        }
        else if(panicModes.contains(PanicMode.SECOND_PLAYER_UNO)) {
            // forbidden cards: D2, WD4, S
            return new PanicInfo(panicModes, Arrays.asList(CardType.DRAW2, CardType.WD4, CardType.SKIP), Collections.<CardType>emptyList(), false);
        }
        else {
            return new PanicInfo(panicModes, Collections.<CardType>emptyList(), Collections.<CardType>emptyList(), false);
        }
    }

    private static List<PanicMode> getPanicModes(UnoState unoState) {
        List<PanicMode> panicModes = new ArrayList<>();

        List<String> playerOrder = unoState.getPlayerOrder();
        String nextPlayerName = playerOrder.size() > 1 ? playerOrder.get(1) : null;
        String secondPlayerName = playerOrder.size() > 2 ? playerOrder.get(2) : null;
        String previousPlayerName = playerOrder.size() > 1 ? playerOrder.get(playerOrder.size()-1) : null;

        if(nextPlayerName != null && hasUno(unoState, nextPlayerName)) {
            panicModes.add(PanicMode.FIRST_PLAYER_UNO);
        }
        if(secondPlayerName != null && hasUno(unoState, secondPlayerName)) {
            panicModes.add(PanicMode.SECOND_PLAYER_UNO);
        }
        if(previousPlayerName != null && hasUno(unoState, previousPlayerName)) {
            panicModes.add(PanicMode.PREVIOUS_PLAYER_UNO);
        }

        return panicModes;
    }

    public static boolean hasUno(UnoState unoState, String playerName) {
        return unoState.getPlayers().get(playerName).getCardCount() == 1;
    }
}
