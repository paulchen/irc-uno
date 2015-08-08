package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.NickServHandler;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class NickChangeEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(NickChangeEvent.class);

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return org.pircbotx.hooks.events.NickChangeEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("NickChangeEvent detected");

        String configuredNickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);

        org.pircbotx.hooks.events.NickChangeEvent<PircBotX> nickChangeEvent = (org.pircbotx.hooks.events.NickChangeEvent<PircBotX>) event;
        if(nickChangeEvent.getNewNick().equals(configuredNickname)) {
            return NickServHandler.generateNickServAuthentication(botInfoProvider);
        }

        return Collections.emptyList();
    }
}
