package org.linuxguy.MarketBot.samples;

import org.linuxguy.MarketBot.Review;
import org.linuxguy.MarketBot.GooglePlayWatcher;
import org.linuxguy.MarketBot.Notifier;
import org.linuxguy.MarketBot.Utils;

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

        GooglePlayWatcher playWatcher = new GooglePlayWatcher(username, password, app_id, "My Android App");

        playWatcher.addListener(new ConsoleNotifier());

        playWatcher.start();
        playWatcher.join();
    }

    private static class ConsoleNotifier extends Notifier<Review> {

        @Override
        public void onNewResult(Review result) {
            System.out.println("Response : " + result);
        }

        @Override
        public String formatReview(Review c) {
            String escapedString = c.text.replace("\"", "\\\"")
                    .replace("\t", "    ")
                    .replace("\n", "    ");

            return String.format("\"%s\" \u2014%s %s", escapedString, c.author, Utils.formatRatingWithStars(c.rating));
        }
    }

    private static void printUsage() {
        System.out.println(String.format("Usage: <user> <password> <app package name>", GooglePlayLogger.class.getName()));
    }
}