package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.CardType;
import at.rueckgr.irc.bot.uno.model.Color;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UnoStrategy {
    public static Card chooseCard(UnoState unoState, List<Card> compatibleCards) {
        Validate.notEmpty(compatibleCards);

        List<Card> nonWildcards = compatibleCards.stream().filter(not(Card::isWildcard)).collect(Collectors.toList());
        if(!nonWildcards.isEmpty()) {
            return findMostEvilCard(nonWildcards);
        }

        if(containsCardType(unoState.getHand(), CardType.WILD)) {
            return new Card(CardType.WILD, Color.WILD);
        }

        return new Card(CardType.WD4, Color.WILD);
    }

    private static boolean containsCardType(List<Card> cards, CardType cardType) {
        return cards.stream().map(Card::getCardType).filter(type -> type.equals(cardType)).count() > 0;
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
	if(map.isEmpty()) {
		return new ArrayList<Color>();
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
