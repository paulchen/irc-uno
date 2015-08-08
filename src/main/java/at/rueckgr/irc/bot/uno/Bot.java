package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.ircevents.IrcEvent;
import at.rueckgr.irc.bot.uno.model.UnoState;
import at.rueckgr.irc.bot.uno.util.ConfigurationKeys;
import at.rueckgr.irc.bot.uno.util.ReflectionsUtil;
import at.rueckgr.irc.bot.uno.util.Util;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.managers.GenericListenerManager;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Bot implements Listener<PircBotX>, BotInfoProvider {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final List<? extends IrcEvent> ircEvents;
    private final UnoState unoState;
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

        ircEvents = ReflectionsUtil.getIrcEvents();

        unoState = new UnoState();

        lastActivityTracker = new LastActivityTracker();
    }

    private void executeActions(List<Action> actions) {
        lastActivityTracker.recordActivity();

        OutputChannel outputChannel = (channel == null) ? null : channel.send();
        for (Action action : actions) {
            action.execute(outputChannel, this);
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
                .setAutoNickChange(true)
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
        List<Action> actions = new ArrayList<>();

        boolean anyoneResponsible = false;
        for(IrcEvent ircEvent : ircEvents) {
            if(ircEvent.isResponsible(event.getClass())) {
                anyoneResponsible = true;

                actions.addAll(ircEvent.handleEvent(event, this));
            }
        }

        if(!actions.isEmpty()) {
            executeActions(actions);
        }

        if(!anyoneResponsible) {
            logger.debug("Unknown event of type: {}", event.getClass().getName());
        }
    }

    @Override
    public String getName() {
        return pircBotX.getNick();
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void recordActivity() {
        lastActivityTracker.recordActivity();
    }

    @Override
    public UnoState getUnoState() {
        return unoState;
    }

    @Override
    public OutputUser getPrivateMessageChannel(String nickname) {
        return pircBotX.getUserChannelDao().getUser(nickname).send();
    }

    @Override
    public PircBotX getBot() {
        return pircBotX;
    }

    public void startGame() {
        executeActions(Util.createAutoplayCommands("+a +e"));
    }
}
