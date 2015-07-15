package at.rueckgr.irc.bot.uno;

public class ActivityScheduler extends Thread {
    public static final int SLEEP_TIME_MILLIS = 60000;

    private final Bot bot;
    private final LastActivityTracker lastActivityTracker;

    public ActivityScheduler(Bot bot, LastActivityTracker lastActivityTracker) {
        this.bot = bot;
        this.lastActivityTracker = lastActivityTracker;
    }

    public void run() {
        try {
            while(!interrupted()) {
                sleep(SLEEP_TIME_MILLIS);

                if(lastActivityTracker.isActivityNecessary()) {
                    bot.startGame();
                }
            }
        }
        catch (InterruptedException e) {
            /* nothing to do */
        }
    }
}
