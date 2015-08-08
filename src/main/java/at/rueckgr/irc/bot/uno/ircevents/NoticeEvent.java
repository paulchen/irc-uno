package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.NickChangeAction;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class NoticeEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(NoticeEvent.class);

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return org.pircbotx.hooks.events.NoticeEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("NoticeEvent detected");

        String configuredNickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);

        org.pircbotx.hooks.events.NoticeEvent<PircBotX> noticeEvent = (org.pircbotx.hooks.events.NoticeEvent<PircBotX>) event;
        if(!noticeEvent.getUser().getNick().equalsIgnoreCase("nickserv")) {
            return Collections.emptyList();
        }
        if(!noticeEvent.getMessage().contains("has been ghosted")) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new NickChangeAction(configuredNickname));
    }
}
