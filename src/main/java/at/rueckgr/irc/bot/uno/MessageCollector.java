package at.rueckgr.irc.bot.uno;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCollector {
    private static final Logger logger = LoggerFactory.getLogger(BotThread.class);

    private final JSONParser jsonParser;

    private int messagesCount;
    private int messagesReceived;
    private String message;

    public MessageCollector() {
        jsonParser = new JSONParser();

        resetState();
    }

    public void collect(String input) {
        logger.debug("Collecting input: {}", input);

        if(messagesReceived > 0 && messagesReceived < messagesCount) {
            logger.debug("Appending message to previously collected message");

            message = message + input;
            messagesReceived++;
            return;
        }
        if(messagesReceived > 0) {
            resetState();
        }
        int pos = input.indexOf(' ');
        if(pos == -1) {
            logger.debug("Discarding message; no space found");

            return;
        }
        String messageNumberString = input.substring(0, pos);

        int messagesNumber;
        try {
            messagesNumber = Integer.parseInt(messageNumberString);
        }
        catch (NumberFormatException e) {
            logger.debug("Discarding message; unparseable number: {}", messageNumberString);

            return;
        }

        if(messagesNumber < 1) {
            logger.debug("Discarding message; invalid number of messages: {}", messagesNumber);

            return;
        }

        messagesCount = messagesNumber;
        message = input.substring(pos+1);
        messagesReceived = 1;
    }

    public boolean hasCompleteMessage() {
        boolean result = messagesCount > 0 && messagesCount == messagesReceived;

        if(result) {
            logger.debug("Message complete");
        }
        else {
            logger.debug("Message not yet complete");
        }

        return result;
    }

    public JSONObject getMessage() {
        resetState();

        try {
            Object object = jsonParser.parse(message);
            if(!(object instanceof JSONObject)) {
                logger.debug("Discarding message; no JSONObject; found instead: {}", object.getClass().getName());

                return null;
            }

            return (JSONObject) object;
        }
        catch (ParseException e) {
            logger.debug("Discarding message; exception occurred", e);

            return null;
        }
    }

    private void resetState() {
        messagesCount = 0;
        messagesReceived = 0;
    }
}
