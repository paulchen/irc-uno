package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.NickServAction;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.NickServHandler;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class ConnectEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(ConnectEvent.class);

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return org.pircbotx.hooks.events.ConnectEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("NickChangeEvent detected");

        String configuredNickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);

        org.pircbotx.hooks.events.ConnectEvent<PircBotX> nickChangeEvent = (org.pircbotx.hooks.events.ConnectEvent<PircBotX>) event;
        if(botInfoProvider.getName().equals(configuredNickname)) {
            return NickServHandler.generateNickServAuthentication(botInfoProvider);
        }

        String nickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);
        String password = botInfoProvider.getProperty(ConfigurationKeys.NICKSERV_PASSWORD);

        if(StringUtils.isBlank(password)) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new NickServAction(MessageFormat.format("GHOST {0} {1}", nickname, password)));
    }
}
