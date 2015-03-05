package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppStoreRSSWatcher extends Watcher<Review> {
    private static final String APP_STORE_URL    = "https://itunes.apple.com/%s/rss/customerreviews/id=%s/sortBy=mostRecent/json";
    private static final long   POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final int    NONE             = -1;

    private String mCountryCode;
    private String mAppId;
    private long   mMostRecentId;

    public AppStoreRSSWatcher(String countryCode, String appId) {
        mCountryCode  = countryCode;
        mAppId        = appId;
        mMostRecentId = NONE;
    }

    @Override
    public void run() {
        while (true) {
            List<Review> comments = getCommentsFromJson(Utils.fetchJsonFromUrl(getAppStoreUrl(mCountryCode, mAppId)));

            if (comments.size() > 0) {
                if (mMostRecentId == NONE) {
                    for (Review c : comments) {
                        if (c.timestamp > mMostRecentId) {
                            mMostRecentId = c.timestamp;
                        }
                    }
                } else {
                    boolean didNotify = false;
                    long newMostRecentId = NONE;

                    for (Review c : comments) {
                        if (c.timestamp > mMostRecentId) {
                            notifyListeners(c);

                            if (c.timestamp > newMostRecentId) {
                                newMostRecentId = c.timestamp;
                            }

                            didNotify = true;
                        }
                    }

                    if (didNotify) {
                        mMostRecentId = newMostRecentId;
                    }
                }
            }

            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static List<Review> getCommentsFromJson(JsonNode json) {
        ArrayList<Review> comments = new ArrayList<Review>();

        JsonNode entryNode = json.path("feed").path("entry");

        Iterator<JsonNode> childNodes = entryNode.elements();
        while (childNodes.hasNext()) {
            JsonNode n = childNodes.next();
            if (n.has("author")) {
                JsonNode authorNode = n.get("author");

                final Review comment = new Review();

                final String title = n.get("title").get("label").textValue();
                final String text = n.get("content").get("label").textValue();

                comment.author = authorNode.get("name").get("label").textValue();
                comment.text = String.format("%s. %s", title, text);
                comment.rating = n.get("im:rating").get("label").asInt();
                comment.timestamp = n.get("id").get("label").asLong();

                comments.add(comment);
            }
        }

        return comments;
    }

    private static String getAppStoreUrl(String countryCode, String appId) {
        return String.format(APP_STORE_URL, countryCode, appId);
    }
}
