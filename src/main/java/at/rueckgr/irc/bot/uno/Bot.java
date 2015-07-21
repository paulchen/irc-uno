package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.commands.BotInfoProvider;
import at.rueckgr.irc.bot.uno.commands.Event;
import at.rueckgr.irc.bot.uno.model.UnoState;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Bot implements Listener<PircBotX>, BotInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private static final String JOIN_COMMAND = "?join";
    private static final String LEAVE_COMMAND = "?leave";
    private static final String AUTOPLAY_COMMAND = "?autoplay";

    private final Map<String, Event> commands;
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

        commands = new HashMap<>();
        Reflections reflections = new Reflections(Event.class.getPackage().getName());
        for (Class<? extends Event> commandClass : reflections.getSubTypesOf(Event.class)) {
            try {
                Event eventObject = commandClass.newInstance();
                commands.put(eventObject.getCommand(), eventObject);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        unoState = new UnoState();

        lastActivityTracker = new LastActivityTracker();
    }

    protected void onMessage(Channel channel, String message) {
        logger.debug("Incoming message from channel {} : {}", channel.getName(), message);

        // TODO command pattern
        if(properties.get(ConfigurationKeys.CHANNEL).equals(channel.getName())) {
            lastActivityTracker.recordActivity();

            if(message != null) {
                message = message.trim();
            }
            if(AUTOPLAY_COMMAND.equalsIgnoreCase(message)) {
                logger.debug("Autoplay command detected");

                startGame();
            }
            else if(JOIN_COMMAND.equalsIgnoreCase(message)) {
                logger.debug("Join command for all bots detected");

                channel.send().message("!botjoin");
            }
            else if((JOIN_COMMAND + " " + pircBotX.getNick()).equalsIgnoreCase(message)) {
                logger.debug("Join command for this bot detected");

                channel.send().message("!botjoin");
            }
            else if(LEAVE_COMMAND.equalsIgnoreCase(message)) {
                logger.debug("Leave command for all bots detected");

                channel.send().message("!leave");
            }
            else if((LEAVE_COMMAND + " " + pircBotX.getNick()).equalsIgnoreCase(message)) {
                logger.debug("Leave command for this bot detected");

                channel.send().message("!leave");
            }
            else {
                logger.debug("Discarding message (irrelevant)");
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
            if(!commands.containsKey(event)) {
                logger.debug("Discarding message; unhandled command: {}", event);

                return;
            }

            //noinspection SuspiciousMethodCalls
            String result = commands.get(event).handle(unoState, jsonObject, this);
            if(result != null) {
                logger.debug("Sending answer to channel: " + result);

                channel.send().message(result);
            }
        }
    }

    public void startGame() {
        lastActivityTracker.recordActivity();

        OutputChannel outputChannel = channel.send();

        outputChannel.message("!uno +a +e");
        outputChannel.message("?join");
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            /* ignore */
        }
        outputChannel.message("!deal");
        outputChannel.message("!leave");
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            /* ignore */
        }
        outputChannel.message("!botjoin");
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
            onMessage(messageEvent.getChannel(), messageEvent.getMessage());
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
}
