package at.rueckgr.irc.bot.uno;

public class BotThread implements Runnable {
    private Bot bot;

    @Override
    public void run() {
        try {
            bot = new Bot();
            bot.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quit() {
        bot.quitServer("So long and thanks for all the fish!");
        bot.dispose();
    }
}
