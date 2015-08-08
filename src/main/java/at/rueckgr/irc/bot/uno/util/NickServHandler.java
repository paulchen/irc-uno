package at.rueckgr.irc.bot.uno.util;

import at.rueckgr.irc.bot.uno.BotInfoProvider;
import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.NickServAction;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public final class NickServHandler {
    private NickServHandler() {
        // don't create instances of this class
    }

    public static List<Action> generateNickServAuthentication(BotInfoProvider botInfoProvider) {
        String configuredNickname = botInfoProvider.getProperty(ConfigurationKeys.NAME);
        String actualNickname = botInfoProvider.getName();

        if(!configuredNickname.equals(actualNickname)) {
            return Collections.emptyList();
        }

        String password = botInfoProvider.getProperty(ConfigurationKeys.NICKSERV_PASSWORD);
        if(StringUtils.isBlank(password)) {
            return Collections.emptyList();
        }

        return Collections.singletonList(new NickServAction(MessageFormat.format("IDENTIFY {0}", password)));
    }
}
