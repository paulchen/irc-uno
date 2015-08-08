package at.rueckgr.irc.bot.uno;

import at.rueckgr.irc.bot.uno.model.UnoState;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputChannel;
import org.pircbotx.output.OutputUser;

public interface BotInfoProvider {
    String getName();

    String getProperty(String key);

    void setChannel(Channel channel);

    void recordActivity();

    UnoState getUnoState();

    OutputUser getPrivateMessageChannel(String nickname);

    PircBotX getBot();
}
