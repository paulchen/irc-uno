package at.rueckgr.irc.bot.uno.util;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.actions.WaitAction;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<Action> createAutoplayCommands(String unoOptions) {
        List<Action> result = new ArrayList<>();

        result.add(new ChannelMessageAction("!uno " + unoOptions));

        result.add(new ChannelMessageAction("?join"));
        result.add(new WaitAction(1000));

        result.add(new ChannelMessageAction("!leave"));
        result.add(new WaitAction(1000));

        result.add(new ChannelMessageAction("!botjoin"));
        result.add(new WaitAction(1000));

        result.add(new ChannelMessageAction("!deal"));



        return result;
    }
}
