package at.rueckgr.irc.bot.uno.actions;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import org.pircbotx.output.OutputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NickChangeAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(NickChangeAction.class);

    private final String nickname;

    public NickChangeAction(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void execute(OutputChannel outputChannel, BotInfoProvider botInfoProvider) {
        logger.debug("Changing nickname to {}", nickname);

        botInfoProvider.getBot().sendIRC().changeNick(nickname);
    }
}
