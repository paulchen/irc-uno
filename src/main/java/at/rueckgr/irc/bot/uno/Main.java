package at.rueckgr.irc.bot.uno;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("quit")) {
                break;
            }
        }

        bot.quitServer("So long and thanks for all the fish!");
        Thread.sleep(5000);
        System.exit(0); // TODO srsly?
    }
}
