package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.NickChangeAction;
import at.rueckgr.irc.bot.uno.actions.NickServAction;
import at.rueckgr.irc.bot.uno.actions.WaitAction;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.NickAlreadyInUseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class NicknameAlreadyInUseIrcEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(NicknameAlreadyInUseIrcEvent.class);

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return NickAlreadyInUseEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("NickAlreadyInUseEvent detected");

        NickAlreadyInUseEvent<PircBotX> nickAlreadyInUseEvent = (NickAlreadyInUseEvent<PircBotX>) event;

        String nickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);
        String password = botInfoProvider.getProperty(ConfigurationKeys.NICKSERV_PASSWORD);

        if(StringUtils.isBlank(password)) {
            return Collections.emptyList();
        }

        NickServAction nickServAction = new NickServAction(MessageFormat.format("GHOST {0} {1}", nickname, password));
        WaitAction waitAction = new WaitAction(1000L); // TODO this can be solved in a better way
        NickChangeAction nickChangeAction = new NickChangeAction(nickname);

        return Arrays.asList(nickServAction, waitAction, nickChangeAction);
    }
}
