package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.commands.Event;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.jibble.pircbot.PircBot;
import org.json.simple.JSONObject;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class Bot extends PircBot {

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

    public Bot() {
        messageCollector = new MessageCollector();

        commands = new HashMap<>();
        Reflections reflections = new Reflections(Event.class.getPackage().getName());
        for (Class<? extends Event> commandClass : reflections.getSubTypesOf(Event.class)) {
            Event eventObject = null;
            try {
                eventObject = commandClass.newInstance();
                commands.put(eventObject.getCommand(), eventObject);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        unoState = new UnoState();

        lastActivityTracker = new LastActivityTracker();

        setMessageDelay(1L);
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if(CHANNEL.equals(channel)) {
            lastActivityTracker.recordActivity();

            if(message != null) {
                message = message.trim();
            }
            if(AUTOPLAY_COMMAND.equalsIgnoreCase(message)) {
                startGame();
            }
            if(JOIN_COMMAND.equalsIgnoreCase(message)) {
                sendMessage(CHANNEL, "!botjoin");
            }
            if((JOIN_COMMAND + " " + NAME).equalsIgnoreCase(message)) {
                sendMessage(CHANNEL, "!botjoin");
            }
            if(LEAVE_COMMAND.equalsIgnoreCase(message)) {
                sendMessage(CHANNEL, "!leave");
            }
            if((LEAVE_COMMAND + " " + NAME).equalsIgnoreCase(message)) {
                sendMessage(CHANNEL, "!leave");
            }
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
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
                sendMessage(CHANNEL, result);
            }
        }
    }

    public void startGame() {
        lastActivityTracker.recordActivity();

        sendMessage(CHANNEL, "!uno +a +e");
        sendMessage(CHANNEL, "?join");
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            /* ignore */
        }
        sendMessage(CHANNEL, "!deal");
        sendMessage(CHANNEL, "!leave");
        sendMessage(CHANNEL, "!botjoin");
    }

    public void run() throws Exception {
        setName(NAME);
        setVerbose(true);
        connect(NETWORK);
        joinChannel(CHANNEL);

        activityScheduler = new ActivityScheduler(this, lastActivityTracker);
        activityScheduler.start();
    }

    @Override
    public synchronized void dispose() {
        activityScheduler.interrupt();
        super.dispose();
    }
}
