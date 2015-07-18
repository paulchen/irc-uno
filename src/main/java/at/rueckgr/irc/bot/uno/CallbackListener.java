package at.rueckgr.irc.bot.uno;

import java.io.IOException;

public class CallbackListener implements BotCallback {
    @Override
    public void botTerminated() {
        try {
            System.in.close();
        }
        catch (IOException e) {
            // ignore
        }
    }
}
