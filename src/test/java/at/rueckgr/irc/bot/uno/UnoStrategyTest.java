package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.Card;
import at.rueckgr.irc.bot.uno.model.CardType;
import at.rueckgr.irc.bot.uno.model.Color;
import at.rueckgr.irc.bot.uno.model.PlayerInfo;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UnoStrategyTest {

    @Test
    public void testChooseCard1() throws Exception {
        UnoState unoState = new UnoState();
        unoState.setCurrentCard(new Card(CardType.EIGHT, Color.BLUE));
        unoState.setHand(Arrays.asList(new Card(CardType.TWO, Color.RED), new Card(CardType.TWO, Color.BLUE)));
        unoState.setPlayerOrder(Arrays.asList("testbot", "paulchen"));
        Map<String, PlayerInfo> players = new HashMap<>();
        players.put("paulchen", new PlayerInfo("paulchen", 4));
        players.put("testbot", new PlayerInfo("testbot", 3));
        unoState.setPlayers(players);

        Card card = UnoStrategy.chooseCard(unoState, Collections.singletonList(new Card(CardType.TWO, Color.BLUE)), true);
        assertThat(card).isNotNull();
        assertThat(card.getCardType()).isEqualTo(CardType.TWO);
        assertThat(card.getColor()).isEqualTo(Color.BLUE);
    }


    @Test
    public void testChooseCard2() throws Exception {
        UnoState unoState = new UnoState();
        unoState.setCurrentCard(new Card(CardType.SKIP, Color.GREEN));
        List<Card> hand = Arrays.asList(
                new Card(CardType.FOUR, Color.GREEN),
                new Card(CardType.SKIP, Color.GREEN),
                new Card(CardType.DRAW2, Color.GREEN),
                new Card(CardType.FOUR, Color.BLUE),
                new Card(CardType.NINE, Color.BLUE),
                new Card(CardType.THREE, Color.YELLOW),
                new Card(CardType.SEVEN, Color.YELLOW),
                new Card(CardType.NINE, Color.YELLOW),
                new Card(CardType.SKIP, Color.YELLOW),
                new Card(CardType.WILD, Color.WILD),
                new Card(CardType.WILD, Color.WILD),
                new Card(CardType.WD4, Color.WILD)
        );
        unoState.setHand(hand);
        unoState.setPlayerOrder(Arrays.asList("testbot", "paulchen"));
        Map<String, PlayerInfo> players = new HashMap<>();
        players.put("paulchen", new PlayerInfo("paulchen", 4));
        players.put("testbot", new PlayerInfo("testbot", hand.size()));
        unoState.setPlayers(players);

        Card card = UnoStrategy.chooseCard(unoState, UnoHelper.findCompatibleCards(hand, unoState.getCurrentCard()), true);
    }
}
