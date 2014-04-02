package org.linuxguy.MarketBot.samples;

import org.linuxguy.MarketBot.Comment;
import org.linuxguy.MarketBot.GooglePlayWatcher;
import org.linuxguy.MarketBot.Notifier;

/**
 * Sample client implementation
 * <p/>
 * This class monitors an app in the Google Play Store and prints new
 * reviews to the console.
 */
public class GooglePlayLogger {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            printUsage();
            System.exit(-1);
        }

        String username = args[0];
        String password = args[1];
        String app_id   = args[2];

        GooglePlayWatcher playWatcher = new GooglePlayWatcher(username, password, app_id);

        playWatcher.addListener(new ConsoleNotifier());

        playWatcher.start();
        playWatcher.join();
    }

    private static class ConsoleNotifier extends Notifier<Comment> {

        @Override
        public void onNewResult(Comment result) {
            System.out.println("Response : " + result);
        }
    }

    private static void printUsage() {
        System.out.println(String.format("Usage: <user> <password> <app package name>", GooglePlayLogger.class.getName()));
    }
}