package at.rueckgr.irc.bot.uno;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<String> createAutoplayCommands() {
        List<String> result = new ArrayList<>();

        result.add("!uno +a +e");
        result.add("?join");

        // TODO reverse these commands once the bug in GamingProcessLuna has been fixed
        result.add("!deal");
        result.add("!leave");
        result.add("!botjoin");


        return result;
    }
}
