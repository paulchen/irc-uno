package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.MessageCollector;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.ReflectionsUtil;
import org.json.simple.JSONObject;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") // accessed via reflection
public class PrivateMessageIrcEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(PrivateMessageIrcEvent.class);

    private final Map<String, ? extends at.rueckgr.irc.bot.uno.events.Event> events;
    private final MessageCollector messageCollector;

    public PrivateMessageIrcEvent() {
        messageCollector = new MessageCollector();

        events = ReflectionsUtil.getEvents();
    }

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return PrivateMessageEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("Private message event detected");

        PrivateMessageEvent<PircBotX> privateMessageEvent = (PrivateMessageEvent<PircBotX>) event;

        String sender = privateMessageEvent.getUser().getNick();
        String message = privateMessageEvent.getMessage();

        if(botInfoProvider.getProperty(ConfigurationKeys.BOT_NAME).equals(sender)) {
            messageCollector.collect(message);
            if(!messageCollector.hasCompleteMessage()) {
                return Collections.emptyList();
            }
            JSONObject jsonObject = messageCollector.getMessage();
            if(jsonObject == null) {
                return Collections.emptyList();
            }

            if(!jsonObject.containsKey("event")) {
                logger.debug("Discarding message; key not found");

                return Collections.emptyList();
            }

            Object jsonEvent = jsonObject.get("event");
            //noinspection SuspiciousMethodCalls
            if(!events.containsKey(jsonEvent)) {
                logger.debug("Discarding message; unhandled command: {}", jsonEvent);

                return Collections.emptyList();
            }

            //noinspection SuspiciousMethodCalls
            Action action = events.get(jsonEvent).handle(botInfoProvider.getUnoState(), jsonObject, botInfoProvider);
            if(action != null) {
                return Collections.singletonList(action);
            }
        }

        return Collections.emptyList();
    }
}
