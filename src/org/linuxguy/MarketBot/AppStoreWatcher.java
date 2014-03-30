package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppStoreWatcher extends Watcher<Comment> {
    private static final String APP_STORE_URL    = "https://itunes.apple.com/%s/rss/customerreviews/id=%s/sortBy=mostRecent/json";
    private static final long   POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final int    NONE             = -1;

    private String mCountryCode;
    private String mAppId;
    private long   mMostRecentId;

    public AppStoreWatcher(String countryCode, String appId) {
        mCountryCode  = countryCode;
        mAppId        = appId;
        mMostRecentId = NONE;
    }

    @Override
    public void run() {
        while (true) {
            List<Comment> comments = getCommentsFromJson(Utils.fetchJsonFromUrl(getAppStoreUrl(mCountryCode, mAppId)));

            if (comments.size() > 0) {
                if (mMostRecentId == NONE) {
                    mMostRecentId = comments.get(0).timestamp;
                } else {
                    boolean didNotify = false;
                    long newMostRecentId = NONE;

                    for (Comment c : comments) {
                        if (c.timestamp > mMostRecentId) {
                            notifyListeners(c);
                            didNotify = true;
                            newMostRecentId = c.timestamp;
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

    private static List<Comment> getCommentsFromJson(JsonNode json) {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        JsonNode entryNode = json.path("feed").path("entry");

        Iterator<JsonNode> childNodes = entryNode.elements();
        while (childNodes.hasNext()) {
            JsonNode n = childNodes.next();
            if (n.has("author")) {
                System.out.println("Checking author/uri");
                JsonNode authorNode = n.get("author");

                Comment comment = new Comment();

                comment.author = authorNode.get("name").get("label").textValue();
                comment.text = n.get("title").get("label").textValue() +
                        " ... " +
                        n.get("content").get("label").textValue();
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
