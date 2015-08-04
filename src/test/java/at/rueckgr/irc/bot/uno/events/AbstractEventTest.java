package at.rueckgr.irc.bot.uno.events;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.util.ReflectionsUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractEventTest {
    public UnoState createUnoState() {
        return new UnoState();
    }

    private Event getEventHandler(String message) {
        JSONObject jsonObject = getJsonObject(message);

        return getEventHandler(jsonObject);
    }

    @SuppressWarnings("unchecked")
    private Event getEventHandler(JSONObject jsonObject) {
        assertThat(jsonObject).containsKey("event");
        Object jsonEvent = jsonObject.get("event");
        assertThat(jsonEvent).isNotNull().isInstanceOf(String.class);

        Map<String, ? extends Event> events = ReflectionsUtil.getEvents();
        assertThat(events).containsKey((String) jsonEvent);

        return events.get(jsonEvent);
    }

    private JSONObject getJsonObject(String message) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
            Object object = jsonParser.parse(message);
            assertThat(object).isNotNull().isInstanceOf(JSONObject.class);
            jsonObject = (JSONObject) object;
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    public Class<? extends Event> getEventClass(final String message) {
        assertThat(message).isNotNull();

        return getEventHandler(message).getClass();
    }

    public Action handleMessage(final UnoState unoState, final String message) {
        assertThat(unoState).isNotNull();
        assertThat(message).isNotNull();

        JSONObject jsonObject = getJsonObject(message);
        Event eventHandler = getEventHandler(jsonObject);
        return eventHandler.handle(unoState, jsonObject, null);
    }
}
