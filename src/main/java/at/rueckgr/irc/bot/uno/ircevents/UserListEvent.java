package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.Util;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class UserListEvent implements IrcEvent {
    @Override
    public boolean isResponsible(final Class<?> eventClass) {
        return org.pircbotx.hooks.events.UserListEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(final Event<PircBotX> event, final BotInfoProvider botInfoProvider) {
        final org.pircbotx.hooks.events.UserListEvent<PircBotX> userListEvent = (org.pircbotx.hooks.events.UserListEvent<PircBotX>) event;
        final String princessName = botInfoProvider.getProperty(ConfigurationKeys.PRINCESS_NAME);

        if (userListEvent.getUsers().stream().noneMatch(s -> s.getNick().equalsIgnoreCase(princessName))) {
            final String botName = botInfoProvider.getProperty(ConfigurationKeys.BOT_NAME);
            if (userListEvent.getUsers().stream().anyMatch(s -> s.getNick().equalsIgnoreCase(botName))) {
                return Util.createInvitePrincessCommands();
            }
        }

        return Collections.emptyList();
    }
}
