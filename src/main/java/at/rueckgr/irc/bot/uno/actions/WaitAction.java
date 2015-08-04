package at.rueckgr.irc.bot.uno.actions;

import org.pircbotx.output.OutputChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(WaitAction.class);

    private final long waitTime;

    public WaitAction(long waitTime) {
        this.waitTime = waitTime;
    }


    @Override
    public void execute(OutputChannel outputChannel) {
        logger.debug("Waiting {} milliseconds", waitTime);

        try {
            Thread.sleep(waitTime);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
