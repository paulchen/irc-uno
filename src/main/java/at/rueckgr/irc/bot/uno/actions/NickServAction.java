package at.rueckgr.irc.bot.uno.actions;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickServAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(NickServAction.class);

    private final String message;

    public NickServAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(OutputChannel outputChannel, BotInfoProvider botInfoProvider) {
        logger.debug("Sending message to NickServ: {}", message);

        OutputUser nickServ = botInfoProvider.getPrivateMessageChannel("NickServ");
        nickServ.message(message);
    }
}
