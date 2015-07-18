package at.rueckgr.irc.bot.uno;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        BotThread botThread = new BotThread(new CallbackListener());
        new Thread(botThread).start();

        Scanner scanner = new Scanner(System.in);
        while(hasNextLine(scanner)) {
            String line = scanner.nextLine();
            if(line.equalsIgnoreCase("quit")) {
                break;
            }
        }

        botThread.quit();
    }

    private static boolean hasNextLine(Scanner scanner) {
        // hack taken from http://stackoverflow.com/questions/19130822/is-it-possible-to-interrupt-scanner-hasnext
        try {
            while (System.in.available() == 0) {
                Thread.sleep(1000);
            }
            return scanner.hasNextLine();
        }
        catch (IOException e) {
            return false;
        }
        catch (InterruptedException e) {
            return false;
        }
    }
}
