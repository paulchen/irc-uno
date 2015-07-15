package at.rueckgr.irc.bot.uno;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        BotThread botThread = new BotThread();
        new Thread(botThread).start();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("quit")) {
                break;
            }
        }

        botThread.quit();
    }
}
