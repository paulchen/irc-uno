package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CardPlayedEventTest extends AbstractEventTest {
    @Test
    public void testHandle() {
        UnoState unoState = createUnoState();
        String message = "{\"event\":\"card_played\",\"player\":\"unobot\",\"card\":\"WILD WD4\"}";

        Class<? extends Event> eventClass = getEventClass(message);
        assertThat(eventClass).isEqualTo(CardPlayedEvent.class);

        Action action = handleMessage(unoState, message);
        assertThat(action).isNull();
    }
}
