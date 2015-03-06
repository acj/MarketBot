package org.linuxguy.MarketBot;

import org.linuxguy.MarketBot.FlowdockNotifier.FlowdockNotificationType;

public class MarketBot { 
    public static void main(String[] args) throws InterruptedException {
        String username = "marketbotuser@gmail.com";
        String password = "foo";
 
        String groupMeBotId = "XYZPDQ";
        String flowdockInboxId = "123456789";
 
        final String androidPackage = "com.mycompany.MyApp";
        GooglePlayWatcher playWatcher = new GooglePlayWatcher(username, password, androidPackage, "My Android App");
        FlowdockNotifier flowdockNotifier = new FlowdockNotifier("MarketBot",
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
