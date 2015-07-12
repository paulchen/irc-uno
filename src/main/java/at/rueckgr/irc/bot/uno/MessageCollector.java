package at.rueckgr.irc.bot.uno;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by paulchen on 12.07.15.
 */
public class MessageCollector {
    private JSONParser jsonParser;

    private int messagesCount;
    private int messagesReceived;
    private String message;

    public MessageCollector() {
        jsonParser = new JSONParser();

        resetState();
    }

    public void collect(String input) {
        if(messagesReceived > 0 && messagesReceived < messagesCount) {
            message = message + input;
            messagesReceived++;
            return;
        }
        if(messagesReceived > 0) {
            resetState();
        }
        int pos = input.indexOf(' ');
        if(pos == -1) {
            return;
        }
        String messageNumberString = input.substring(0, pos);

        int messagesNumber;
        try {
            messagesNumber = Integer.parseInt(messageNumberString);
        }
        catch (NumberFormatException e) {
            return;
        }

        if(messagesNumber < 1) {
            return;
        }

        messagesCount = messagesNumber;
        message = input.substring(pos+1);
        messagesReceived = 1;
    }

    public boolean hasCompleteMessage() {
        return messagesCount > 0 && messagesCount == messagesReceived;
    }

    public JSONObject getMessage() {
        resetState();

        try {
            Object object = jsonParser.parse(message);
            if(!(object instanceof JSONObject)) {
                return null;
            }

            return (JSONObject) object;
        }
        catch (ParseException e) {
            return null;
        }
    }

    private void resetState() {
        messagesCount = 0;
        messagesReceived = 0;
    }
}
