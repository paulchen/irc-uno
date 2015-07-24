package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.events.BotInfoProvider;
import at.rueckgr.irc.bot.uno.events.Event;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.usercommands.UserCommand;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import org.json.simple.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.output.OutputChannel;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Bot implements Listener<PircBotX>, BotInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final Map<String, ? extends Event> events;
    private final List<? extends UserCommand> userCommands;
    private final UnoState unoState;
    private final MessageCollector messageCollector;
    private final LastActivityTracker lastActivityTracker;
    private ActivityScheduler activityScheduler;
    private PircBotX pircBotX;
    private Channel channel;

    private final Properties properties;

    public Bot() {
        properties = new Properties();
        try {
            properties.load(new FileReader("bot.properties"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        messageCollector = new MessageCollector();

        events = initEvents();
        userCommands = initUserCommands();

        unoState = new UnoState();

        lastActivityTracker = new LastActivityTracker();
    }

    // TODO move some method to Util class
    private Map<String, ? extends Event> initEvents() {
        Map<String, Event> events = new HashMap<>();

        for (Event eventObject : getClasses(Event.class)) {
            events.put(eventObject.getCommand(), eventObject);
        }

        return events;
    }

    private List<? extends UserCommand> initUserCommands() {
        return getClasses(UserCommand.class);
    }

    private <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> List<T> getClasses(Class<T> clazz) {
        return new Reflections(clazz.getPackage().getName()).getSubTypesOf(clazz).stream().map(this::newInstance).collect(Collectors.toList());
    }

    protected void onMessage(Channel channel, String nickname, String message) {
        logger.debug("Incoming message from channel {} : {}", channel.getName(), message);

        if(properties.get(ConfigurationKeys.CHANNEL).equals(channel.getName())) {
            lastActivityTracker.recordActivity();

            if(message != null) {
                message = message.trim();
            }

            List<String> output = new ArrayList<>();
            for (UserCommand userCommand : userCommands) {
                if(userCommand.isResponsible(nickname, message, this)) {
                    output.addAll(userCommand.handleMessage(nickname, message, this));
                }
            }

            if(output.isEmpty()) {
                logger.debug("Discarding message (irrelevant)");
            }
            else {
                sendOutput(output);
            }
        }
        else {
            logger.debug("Discarding message; wrong channel {}", channel.getName());
        }
    }

    protected synchronized void onPrivateMessage(String sender, String message) {
        if(properties.getProperty(ConfigurationKeys.BOT_NAME).equals(sender)) {
            messageCollector.collect(message);
            if(!messageCollector.hasCompleteMessage()) {
                return;
            }
            JSONObject jsonObject = messageCollector.getMessage();
            if(jsonObject == null) {
                return;
            }

            if(!jsonObject.containsKey("event")) {
                logger.debug("Discarding message; key not found");

                return;
            }

            Object event = jsonObject.get("event");
            //noinspection SuspiciousMethodCalls
            if(!events.containsKey(event)) {
                logger.debug("Discarding message; unhandled command: {}", event);

                return;
            }

            //noinspection SuspiciousMethodCalls
            String result = events.get(event).handle(unoState, jsonObject, this);
            if(result != null) {
                logger.debug("Sending answer to channel: " + result);

                channel.send().message(result);
            }
        }
    }

    public void sendOutput(List<String> messages) {
        lastActivityTracker.recordActivity();

        OutputChannel outputChannel = channel.send();

        boolean firstMessage = true;
        for (String message : messages) {
            if(!firstMessage) {
                try {
                    Thread.sleep(100); // TODO magic number
                }
                catch (InterruptedException e) {
                /* ignore */
                }
            }
            firstMessage = false;

            outputChannel.message(message);
        }
    }

    public void run() throws Exception {
        GenericListenerManager<PircBotX> listenerManager = new GenericListenerManager<>();
        listenerManager.addListener(this);
        Configuration<PircBotX> configuration = new Configuration.Builder<>()
                .setName(properties.getProperty(ConfigurationKeys.NAME))
                .setServerHostname(properties.getProperty(ConfigurationKeys.NETWORK))
                .addAutoJoinChannel(properties.getProperty(ConfigurationKeys.CHANNEL))
                .addListener(this)
                .setMessageDelay(1L)
                .setListenerManager(listenerManager)
                .buildConfiguration();

        activityScheduler = new ActivityScheduler(this, lastActivityTracker);
        activityScheduler.start();

        pircBotX = new PircBotX(configuration);
        pircBotX.startBot();
    }

    public void shutdown() {
        if(activityScheduler != null) {
            activityScheduler.interrupt();
        }

        if(pircBotX != null) {
            pircBotX.sendIRC().quitServer("So long and thanks for all the fish!");
        }
    }

    @Override
    public void onEvent(org.pircbotx.hooks.Event<PircBotX> event) throws Exception {
        // TODO command pattern
        if(event instanceof JoinEvent) {
            logger.debug("Join event detected");

            JoinEvent<PircBotX> joinEvent = (JoinEvent<PircBotX>) event;
            Channel channel = joinEvent.getChannel();
            if(properties.getProperty(ConfigurationKeys.CHANNEL).equals(channel.getName())) {
                this.channel = channel;
            }
        }
        else if(event instanceof MessageEvent) {
            logger.debug("Message event detected");

            MessageEvent<PircBotX> messageEvent = (MessageEvent<PircBotX>) event;
            onMessage(messageEvent.getChannel(), messageEvent.getUser().getNick(), messageEvent.getMessage());
        }
        else if(event instanceof PrivateMessageEvent) {
            logger.debug("Private message event detected");

            PrivateMessageEvent<PircBotX> privateMessageEvent = (PrivateMessageEvent<PircBotX>) event;
            onPrivateMessage(privateMessageEvent.getUser().getNick(), privateMessageEvent.getMessage());
        }
        else {
            logger.debug("Unknown event of type: {}", event.getClass().getName());
        }
    }

    @Override
    public String getName() {
        return pircBotX.getNick();
    }

    public void startGame() {
        sendOutput(Util.createAutoplayCommands());
    }
}
