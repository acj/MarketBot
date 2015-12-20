package org.linuxguy.MarketBot;

import org.linuxguy.MarketBot.FlowdockNotifier.FlowdockNotificationType;

public class MarketBot { 
    public static void main(String[] args) throws InterruptedException {
        // This can be any valid Google account. For security reasons, I do NOT recommend using the same account
        // that you use to manage your app.
        String googlePlayUsername = "marketbotuser@gmail.com";
        String googlePlayPassword = "foo";
 
        String groupMeBotId = "XYZPDQ";
        String flowdockInboxId = "123456789";
 
        String botName = "MarketBot";

        final String androidPackage = "com.mycompany.MyApp";
        GooglePlayWatcher playWatcher = new GooglePlayWatcher(googlePlayUsername, googlePlayPassword, androidPackage, "My Android App");
        FlowdockNotifier flowdockNotifier = new FlowdockNotifier(botName,
                                                                 flowdockInboxId, 
                                                                 FlowdockNotificationType.INBOX);
        playWatcher.addListener(flowdockNotifier);
        playWatcher.start();
 
        // You can find this app ID in iTunes Connect
        String iOSAppId = "123456789";
        AppStoreRSSWatcher appStoreWatcherUS = new AppStoreRSSWatcher("us", iOSAppId, "My iOS App");
        appStoreWatcherUS.addListener(new GroupMeNotifier(groupMeBotId));
        appStoreWatcherUS.start();

        System.out.println("All pollers started");

        // Keep running until the watchers get tired. (Never!)
        playWatcher.join();
        appStoreWatcherUS.join();
    }
}
