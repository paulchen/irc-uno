package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.JoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class JoinIrcEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(JoinIrcEvent.class);

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return JoinEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("Join event detected");

        JoinEvent<PircBotX> joinEvent = (JoinEvent<PircBotX>) event;
        Channel channel = joinEvent.getChannel();
        if(botInfoProvider.getProperty(ConfigurationKeys.CHANNEL).equals(channel.getName())) {
            botInfoProvider.setChannel(channel);
        }

        return Collections.emptyList();
    }
}
