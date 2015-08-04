package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.UnoState;
import org.pircbotx.Channel;

public interface BotInfoProvider {
    String getName();

    String getProperty(String key);

    void setChannel(Channel channel);

    void recordActivity();

    UnoState getUnoState();
}
