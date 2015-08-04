package at.rueckgr.irc.bot.uno.ircevents;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.usercommands.UserCommand;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.ReflectionsUtil;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") // accessed via reflection
public class MessageIrcEvent implements IrcEvent {
    private static final Logger logger = LoggerFactory.getLogger(MessageIrcEvent.class);

    private final List<? extends UserCommand> userCommands;

    public MessageIrcEvent() {
        userCommands = ReflectionsUtil.getUserCommands();
    }

    @Override
    public boolean isResponsible(Class<?> eventClass) {
        return MessageEvent.class.equals(eventClass);
    }

    @Override
    public List<Action> handleEvent(Event<PircBotX> event, BotInfoProvider botInfoProvider) {
        logger.debug("Message event detected");

        MessageEvent<PircBotX> messageEvent = (MessageEvent<PircBotX>) event;

        Channel channel = messageEvent.getChannel();
        String nickname = messageEvent.getUser().getNick();
        String message = messageEvent.getMessage();

        logger.debug("Incoming message from channel {} : {}", channel.getName(), message);

        List<Action> output = new ArrayList<>();
        if(botInfoProvider.getProperty(ConfigurationKeys.CHANNEL).equals(channel.getName())) {
            botInfoProvider.recordActivity();

            if(message != null) {
                message = message.trim();
            }

            for (UserCommand userCommand : userCommands) {
                if(userCommand.isResponsible(nickname, message, botInfoProvider)) {
                    output.addAll(userCommand.handleMessage(nickname, message, botInfoProvider));
                }
            }

            if(output.isEmpty()) {
                logger.debug("Discarding message (irrelevant)");
            }
        }
        else {
            logger.debug("Discarding message; wrong channel {}", channel.getName());
        }

        return output;
    }
}
