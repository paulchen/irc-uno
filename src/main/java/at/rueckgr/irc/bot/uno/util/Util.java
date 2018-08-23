package at.rueckgr.irc.bot.uno.util;

import at.rueckgr.irc.bot.uno.actions.Action;
import at.rueckgr.irc.bot.uno.actions.ChannelMessageAction;
import at.rueckgr.irc.bot.uno.actions.WaitAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    private static final int WAIT_TIME = 1000;

    public static List<Action> createAutoplayCommands(UnoMode... unoModes) {
        final String unoOptions = Arrays.stream(unoModes)
                .map(UnoMode::getCode)
                .collect(Collectors.joining(" "));

        return createAutoplayCommands(unoOptions);
    }

    public static List<Action> createAutoplayCommands(String unoOptions) {
        List<Action> result = new ArrayList<>();

        result.add(new ChannelMessageAction(UnoCommands.START_GAME + " " + unoOptions));

        result.add(new ChannelMessageAction(UnoCommands.INVITE_BOT));
        result.add(new WaitAction(WAIT_TIME));

        result.add(new ChannelMessageAction(UnoCommands.LEAVE_GAME));
        result.add(new WaitAction(WAIT_TIME));

        result.add(new ChannelMessageAction(UnoCommands.BOTJOIN));
        result.add(new WaitAction(WAIT_TIME));

        result.add(new ChannelMessageAction(UnoCommands.DEAL));

        return result;
    }

    public static List<Action> createInvitePrincessCommands() {
        return Collections.singletonList(new ChannelMessageAction(UnoCommands.INVITE_PRINCESS));
    }
}
