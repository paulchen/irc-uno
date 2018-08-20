package at.rueckgr.irc.bot.uno.actions;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.pircbotx.output.OutputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelMessageAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(ChannelMessageAction.class);

    private final String message;

    public ChannelMessageAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(OutputChannel outputChannel, BotInfoProvider botInfoProvider) {
        logger.debug("Sending message to channel: {}", message);

        if (outputChannel != null) {
            outputChannel.message(message);
        }
    }
}
