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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UnoStrategy {
    private static final Logger logger = LoggerFactory.getLogger(UnoStrategy.class);

    public static Card chooseCard(UnoState unoState, List<Card> compatibleCards, boolean drawingPossible) {
        Validate.notEmpty(compatibleCards);

        logger.debug("Compatible cards: {}", compatibleCards);

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

        if(card.getCardType().getEvilness() == CardType.ZERO.getEvilness() && panicInfo.isDrawIfPossible()) {
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

        List<List<Card>> cardChains = findChains(unoState.getCurrentCard(), unoState.getHand(), 0);
        logger.debug("Card chains: {}", cardChains);

        List<List<Card>> notForbiddenChains = cardChains.stream().filter(chain -> !forbiddenCards.contains(chain.get(0).getCardType())).collect(Collectors.toList());
        logger.debug("Not forbidden card chains: {}", notForbiddenChains);
        if(notForbiddenChains.isEmpty()) {
            return null;
        }

        int longestChainLength = notForbiddenChains.stream().map(List::size).max(Integer::compare).get();
        logger.debug("Longest chain length: {}", longestChainLength);
        List<List<Card>> longestChains = notForbiddenChains.stream().filter(chain -> chain.size() == longestChainLength).collect(Collectors.toList());
        logger.debug("Longest chains: {}", longestChains);

        int mostEvilChain = longestChains.stream().map(UnoStrategy::getEvilness).max(Integer::compare).get();
        logger.debug("Most evil chain length: {}", mostEvilChain);
        List<List<Card>> mostEvilChains = longestChains.stream().filter(chain -> getEvilness(chain) == mostEvilChain).collect(Collectors.toList());
        logger.debug("Most evil chains: {}", mostEvilChains);

        List<Card> actualChain = mostEvilChains.stream().findAny().get();
        logger.debug("Actual chain: {}", actualChain);
        return actualChain.get(0);
    }

    private static List<List<Card>> findChains(Card currentCard, List<Card> hand, int depth) {
        List<List<Card>> result = new ArrayList<>();

        if(depth > 4) {
            return result;
        }

        if(hand.isEmpty()) {
            return result;
        }

        List<Card> compatibleCards = UnoHelper.findCompatibleCards(hand, currentCard);
        if(compatibleCards.isEmpty()) {
            return result;
        }

//        for (Card compatibleCard : compatibleCards) {
        for (Card compatibleCard : new HashSet<>(compatibleCards)) {
            List<Card> newHand = new ArrayList<>(hand);
            newHand.remove(compatibleCard);

            List<Card> cards;
            if(compatibleCard.isWildcard()) {
                cards = new ArrayList<>(Color.values().length);
                boolean added = false;
                for (Color color : Color.values()) {
                    if(!color.isWildcard() && containsColor(hand, color)) {
                        cards.add(new Card(compatibleCard.getCardType(), color));
                        added = true;
                    }
                }
                if(!added) {
                    cards.add(new Card(compatibleCard.getCardType(), getRandomColor()));
                }
            }
            else {
                cards = Collections.singletonList(compatibleCard);
            }

            for (Card card : cards) {
                List<List<Card>> newLists = findChains(card, newHand, depth + 1);
                if (newLists.isEmpty()) {
                    result.add(Collections.singletonList(card));
                }
                else {
                    for (List<Card> newList : newLists) {
                        List<Card> list = new ArrayList<>(newList.size() + 1);
                        list.add(card);
                        list.addAll(newList);
                        result.add(list);
                    }
                }
            }
        }

        return result;
    }

    private static boolean containsColor(List<Card> hand, Color color) {
        return hand.stream().map(Card::getColor).anyMatch(cardColor -> cardColor.equals(color));
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

    private static List<Card> getCardsByType(List<Card> cards, CardType cardType) {
        return cards.stream().filter(card -> card.getCardType().equals(cardType)).collect(Collectors.toList());
    }

    private static int getEvilness(List<Card> cards) {
        int evilness = 0;
        for(int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int posFromEnd = cards.size() - i - 1;

            int cardEvilness = card.getCardType().getEvilness();
            if(cardEvilness > 0) {
                evilness += cardEvilness + posFromEnd;
            }
        }

        return evilness;
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
