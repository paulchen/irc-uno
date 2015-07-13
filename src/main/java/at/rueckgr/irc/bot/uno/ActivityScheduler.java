package at.rueckgr.irc.bot.uno;

public class ActivityScheduler extends Thread {
    public static final int SLEEP_TIME_MILLIS = 60000;

    private final Bot bot;
    private final LastActivityTracker lastActivityTracker;

    public ActivityScheduler(Bot bot, LastActivityTracker lastActivityTracker) {
        this.bot = bot;
        this.lastActivityTracker = lastActivityTracker;

        setDaemon(true);
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(SLEEP_TIME_MILLIS);
            }
            catch (InterruptedException e) {
                /* nothing to do */
            }

            if(lastActivityTracker.isActivityNecessary()) {
                bot.startGame();
            }
        }
    }
}
