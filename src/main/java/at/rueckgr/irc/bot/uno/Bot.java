package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.commands.Command;
import at.rueckgr.irc.bot.uno.model.UnoState;
import org.jibble.pircbot.PircBot;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class Bot extends PircBot {

    public static final String NAME = "unobot";

    private static final String NETWORK = "irc.rueckgr.at";
    private static final String CHANNEL = "#irc";
    private static final String BOT_NAME = "GamingPrincessLuna";
    private static final String JOIN_COMMAND = "?join";
    private static final String LEAVE_COMMAND = "?leave";

    private JSONParser jsonParser;
    private Map<String, Command> commands;
    private UnoState unoState;

    public Bot() throws Exception {
        setName(NAME);
        setVerbose(true);
        connect(NETWORK);
        joinChannel(CHANNEL);

        jsonParser = new JSONParser();

        commands = new HashMap<>();
        Reflections reflections = new Reflections(Command.class.getPackage().getName());
        for (Class<? extends Command> commandClass : reflections.getSubTypesOf(Command.class)) {
            Command commandObject = commandClass.newInstance();
            commands.put(commandObject.getCommand(), commandObject);
        }

        unoState = new UnoState();
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if(CHANNEL.equals(channel)) {
            if(message != null) {
                message = message.trim();
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
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if(BOT_NAME.equals(sender)) {
            if(!message.startsWith("###   1 ")) {
                return;
            }
            message = message.substring(8);

            JSONObject jsonObject;
            try {
                Object object = jsonParser.parse(message);
                if(!(object instanceof JSONObject)) {
                    return;
                }

                jsonObject = (JSONObject) object;
            }
            catch (ParseException e) {
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
}
