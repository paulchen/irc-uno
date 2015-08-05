package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.CardType;
import at.rueckgr.irc.bot.uno.model.Color;
import at.rueckgr.irc.bot.uno.model.PanicInfo;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UnoStrategy {
    private static final Logger logger = LoggerFactory.getLogger(UnoStrategy.class);

    public static Card chooseCard(UnoState unoState, List<Card> compatibleCards, boolean drawingPossible) {
        Validate.notEmpty(compatibleCards);

        Card card = findCard(unoState, compatibleCards, Collections.<CardType>emptyList(), Collections.<CardType>emptyList());
        if(UnoHelper.hasUno(unoState, unoState.getPlayerOrder().get(0))) {
            logger.debug("Winning move!");

            // this is our winning move
            return card;
        }

        PanicInfo panicInfo = UnoHelper.getPanicInfo(unoState);
        if(panicInfo.getCurrentPanicState().isEmpty()) {
            logger.debug("Nobody has uno, nothing to worry about");

            // nobody has uno, nothing to worry about
            return card;
        }

        logger.debug("Preferred cards: {}", panicInfo.getPreferredCards().toString());
        logger.debug("Forbidden cards: {}", panicInfo.getForbiddenCards().toString());

        card = findCard(unoState, compatibleCards, panicInfo.getPreferredCards(), panicInfo.getForbiddenCards());
        if(card == null) {
            logger.debug("No card in current hand which is not forbidden");

            return null;
        }

        if(card.getCardType().getEvilness() == 0 && panicInfo.isDrawIfPossible()) {
            logger.debug("No evil card in current hand, drawing now");

            return null;
        }

        return card;
    }

    private static Card findCard(UnoState unoState, List<Card> compatibleCards, List<CardType> preferredCards, List<CardType> forbiddenCards) {
        if(!preferredCards.isEmpty()) {
            List<Card> preferredCompatibleCards = compatibleCards.stream().filter(card -> preferredCards.contains(card.getCardType())).collect(Collectors.toList());

            logger.debug("Preferred cards in current hand: {}", preferredCompatibleCards.toString());

            if(!preferredCompatibleCards.isEmpty()) {
                return findMostPreferredCard(preferredCompatibleCards, preferredCards);
            }

            logger.debug("No preferred card in current hand");
        }

        List<Card> notForbiddenCards = compatibleCards.stream().filter(card -> !forbiddenCards.contains(card.getCardType())).collect(Collectors.toList());
        if(notForbiddenCards.isEmpty()) {
            return null;
        }

        List<Card> nonWildcards = notForbiddenCards.stream().filter(not(Card::isWildcard)).collect(Collectors.toList());
        if(!nonWildcards.isEmpty()) {
            return findMostEvilCard(nonWildcards);
        }

        if(containsCardType(unoState.getHand(), CardType.WILD)) {
            return new Card(CardType.WILD, Color.WILD);
        }

        return new Card(CardType.WD4, Color.WILD);
    }

    private static Card findMostPreferredCard(List<Card> preferredCompatibleCards, List<CardType> preferredCards) {
        for (CardType preferredCard : preferredCards) {
            List<Card> cardsByType = getCardsByType(preferredCompatibleCards, preferredCard);
            if(!cardsByType.isEmpty()) {
                return cardsByType.get(0);
            }
        }

        return null;
    }

    private static boolean containsCardType(List<Card> cards, CardType cardType) {
        return getCardsByType(cards, cardType).size() > 0;
    }

    private static List<Card> getCardsByType(List<Card> cards, CardType cardType) {
        return cards.stream().filter(card -> card.getCardType().equals(cardType)).collect(Collectors.toList());
    }

    private static Card findMostEvilCard(List<Card> cards) {
        Validate.notEmpty(cards);

        // TODO isn't there some Java 8 way to do this?
        Card mostEvilCard = null;

        for (Card card : cards) {
            if(mostEvilCard == null || card.getCardType().getEvilness() > mostEvilCard.getCardType().getEvilness()) {
                mostEvilCard = card;
            }
        }

        return mostEvilCard;
    }

    public static Color getWildcardColor(UnoState unoState) {
        List<Color> colors = getMostFrequentColor(unoState.getHand());
        if(colors.size() == 1) {
            return colors.get(0);
        }

        if(colors.isEmpty()) {
            return getRandomColor();
        }

        colors = getMostEvilColor(unoState.getHand(), colors);
        if(colors.size() == 1) {
            return colors.get(0);
        }
        return getRandomColor(colors);

    }

    private static List<Color> getMostEvilColor(List<Card> hand, List<Color> colors) {
        Validate.notEmpty(colors);

        Map<Color, Long> map = new HashMap<>();
        for (Card card : hand) {
            Color color = card.getColor();
            if(colors.contains(color)) {
                if(!map.containsKey(color)) {
                    map.put(color, 0L);
                }
                map.put(color, map.get(color)+card.getCardType().getEvilness());
            }
        }

        return getElementsWithLargestKeys(map);
    }

    private static Color getRandomColor() {
        return getRandomColor(getNonWildcardColors());
    }

    private static Color getRandomColor(List<Color> colors) {
        return colors.stream().findAny().get();
    }

    private static List<Color> getNonWildcardColors() {
        return Arrays.asList(Color.values()).stream().filter(not(Color::isWildcard)).collect(Collectors.toList());
    }

    private static List<Color> getMostFrequentColor(List<Card> hand) {
        Map<Color, Long> map = hand.stream().filter(not(Card::isWildcard)).collect(Collectors.groupingBy(Card::getColor, Collectors.counting()));
        if (map.isEmpty()) {
            return new ArrayList<>();
        }
        return getElementsWithLargestKeys(map);
    }

    private static <T, U extends Comparable<U>> List<T> getElementsWithLargestKeys(Map<T, U> map) {
        Validate.notEmpty(map);

        U maximumNumber = map.values().stream().max(U::compareTo).get();
        return map.keySet().stream().filter(key -> map.get(key).equals(maximumNumber)).collect(Collectors.toList());
    }

    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
