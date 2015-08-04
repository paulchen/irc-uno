package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

import java.util.List;

public interface IrcEvent {
    boolean isResponsible(Class<?> eventClass);

    List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider);
}
