package at.rueckgr.irc.bot.uno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(BotThread.class);

    private final BotCallback botCallback;

    private Bot bot;

    public BotThread(BotCallback botCallback) {
        this.botCallback = botCallback;
    }

    @Override
    public void run() {
        try {
            bot = new Bot();
            bot.run();
        }
        catch (Exception e) {
            logger.error("Exception when running the Bot", e);
        }
        finally {
            quit();
            botCallback.botTerminated();
        }
    }

    public void quit() {
        if(bot != null) {
            try {
                bot.shutdown();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }
}
