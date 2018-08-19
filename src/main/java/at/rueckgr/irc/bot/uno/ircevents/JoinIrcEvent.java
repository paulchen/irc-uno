package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.Util;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
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
    public boolean isResponsible(final Class<?> eventClass) {
        return JoinEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(final Event<PircBotX> event, final BotInfoProvider botInfoProvider) {
        logger.debug("Join event detected");

        final JoinEvent<PircBotX> joinEvent = (JoinEvent<PircBotX>) event;
        final Channel channel = joinEvent.getChannel();
        if (!botInfoProvider.getProperty(ConfigurationKeys.CHANNEL).equalsIgnoreCase(channel.getName())) {
            return Collections.emptyList();
        }

        botInfoProvider.setChannel(channel);

        final User user = joinEvent.getUser();
        if (botInfoProvider.getProperty(ConfigurationKeys.BOT_NAME).equalsIgnoreCase(user.getNick())) {
            return Collections.singletonList(new ChannelMessageAction("!ai"));
        }

        if (botInfoProvider.getProperty(ConfigurationKeys.PRINCESS_NAME).equalsIgnoreCase(user.getNick())) {
            return Util.createAutoplayCommands("+a +e");
        }

        return Collections.emptyList();
    }
}
