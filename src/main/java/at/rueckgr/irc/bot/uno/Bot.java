package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.commands.Event;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.json.simple.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.output.OutputChannel;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class Bot implements Listener<PircBotX> {

    // TODO use name reported by PircBot
    public static final String NAME = "unobot";

    // TODO use properties
    private static final String NETWORK = "irc.rueckgr.at";
    private static final String CHANNEL = "#uno";
    private static final String BOT_NAME = "GamingPrincessLuna";
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

    public Bot() {
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
        // TODO command pattern
        if(CHANNEL.equals(channel.getName())) {
            lastActivityTracker.recordActivity();

            if(message != null) {
                message = message.trim();
            }
            if(AUTOPLAY_COMMAND.equalsIgnoreCase(message)) {
                startGame();
            }
            if(JOIN_COMMAND.equalsIgnoreCase(message)) {
                channel.send().message("!botjoin");
            }
            if((JOIN_COMMAND + " " + NAME).equalsIgnoreCase(message)) {
                channel.send().message("!botjoin");
            }
            if(LEAVE_COMMAND.equalsIgnoreCase(message)) {
                channel.send().message("!leave");
            }
            if((LEAVE_COMMAND + " " + NAME).equalsIgnoreCase(message)) {
                channel.send().message("!leave");
            }
        }
    }

    protected synchronized void onPrivateMessage(String sender, String message) {
        if(BOT_NAME.equals(sender)) {
            messageCollector.collect(message);
            if(!messageCollector.hasCompleteMessage()) {
                return;
            }
            JSONObject jsonObject = messageCollector.getMessage();
            if(jsonObject == null) {
                return;
            }

            if(!jsonObject.containsKey("event")) {
                return;
            }

            Object event = jsonObject.get("event");
            //noinspection SuspiciousMethodCalls
            if(!commands.containsKey(event)) {
                return;
            }

            //noinspection SuspiciousMethodCalls
            String result = commands.get(event).handle(unoState, jsonObject);
            if(result != null) {
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
        Configuration<PircBotX> configuration = new Configuration.Builder<>()
                .setName(NAME)
                .setServerHostname(NETWORK)
                .addAutoJoinChannel(CHANNEL)
                .addListener(this)
                .setMessageDelay(1L)
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
            JoinEvent<PircBotX> joinEvent = (JoinEvent<PircBotX>) event;
            Channel channel = joinEvent.getChannel();
            if(CHANNEL.equals(channel.getName())) {
                this.channel = channel;
            }
        }

        if(event instanceof MessageEvent) {
            MessageEvent<PircBotX> messageEvent = (MessageEvent<PircBotX>) event;
            onMessage(messageEvent.getChannel(), messageEvent.getMessage());
        }

        if(event instanceof PrivateMessageEvent) {
            PrivateMessageEvent<PircBotX> privateMessageEvent = (PrivateMessageEvent<PircBotX>) event;
            onPrivateMessage(privateMessageEvent.getUser().getNick(), privateMessageEvent.getMessage());
        }
    }
}
