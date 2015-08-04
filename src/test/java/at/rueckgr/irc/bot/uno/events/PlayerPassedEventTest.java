package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayerPassedEventTest extends AbstractEventTest {
    @Test
    public void testHandle() {
        UnoState unoState = createUnoState();
        String message = "{\"event\":\"player_passed\", \"player\": \"RavusBot\"}";

        Class<? extends Event> eventClass = getEventClass(message);
        assertThat(eventClass).isEqualTo(PlayerPassedEvent.class);

        Action action = handleMessage(unoState, message);
        assertThat(action).isNull();
    }
}
