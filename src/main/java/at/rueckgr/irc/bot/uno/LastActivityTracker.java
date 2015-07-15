package at.rueckgr.irc.bot.uno;

import java.time.LocalDateTime;
import java.util.Random;

public class LastActivityTracker {
    private static final int MIN_SECONDS = 3600*2;
    private static final int MAX_SECONDS = 3600*5;

    private LocalDateTime lastActivity;
    private LocalDateTime nextActivity;

    private final Random random;

    public LastActivityTracker() {
        random = new Random();

        recordActivity();
    }

    public void recordActivity() {
        lastActivity = LocalDateTime.now();

        long seconds = random.nextInt(MAX_SECONDS - MIN_SECONDS) + MIN_SECONDS;

        nextActivity = lastActivity.plusSeconds(seconds);
    }

    public boolean isActivityNecessary() {
        return nextActivity.isBefore(LocalDateTime.now());
    }
}
