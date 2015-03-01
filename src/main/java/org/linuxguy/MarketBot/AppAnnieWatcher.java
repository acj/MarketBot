package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppAnnieWatcher extends Watcher<Comment> {

    private static final String APP_ANNIE_BASE_URL = "https://api.appannie.com/v1.2";
    private static final long   POLL_INTERVAL_MS   = TimeUnit.MINUTES.toMillis(5);

    private String          mApiKey;
    private Comment         mMostRecentComment;
    private AppAnnieMarket  mMarket;
    private String          mProductId;
    private String          mStartDate;
    private String          mEndDate;
    private String          mVersion;
    private String          mCountries;
    private String          mRating;
    private String          mPageIndex;

    enum AppAnnieMarket {
        iOS            ("ios"),
        Mac            ("mac"),
        GooglePlay     ("google-play"),
        AmazonAppstore ("amazon-appstore"),
        iBooksStore    ("ibooks-store"),
        KindleStore    ("kindle-store");

        private final String mMarket;

        private AppAnnieMarket(String market) {
            mMarket = market;
        }

        public String toString() {
            return mMarket;
        }
    }

    AppAnnieWatcher(String apiKey, AppAnnieMarket market, String appProductId) {
        mApiKey = apiKey;
        mMarket = market;
        mProductId = appProductId;
    }

    public void setMarket(AppAnnieMarket market) {
        mMarket = market;
    }
    public void setProductId(String appProductId) {
        mProductId = appProductId;
    }
    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }
    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }
    public void setVersion(String version) {
        mVersion = version;
    }
    public void setCountries(String countries) {
        mCountries = countries;
    }
    public void setRating(String rating) {
        mRating = rating;
    }
    public void setPageIndex(String pageIndex) {
        mPageIndex = pageIndex;
    }

    @Override
    public void run() {
        final ArrayList<Pair<String, String>> headers = new ArrayList();
        final String bearerToken = String.format("Bearer %s", mApiKey);
        final Pair<String, String> authHeader = new Pair<String, String>("Authorization", bearerToken);
        headers.add(authHeader);

        while (true) {
            List<Comment> comments = getCommentsFromJson(Utils.fetchJsonFromUrl(getAppAnnieURL(), headers));

            if (comments.size() > 0) {
                if (mMostRecentComment == null) {
                    for (Comment c : comments) {
                        if (mMostRecentComment == null || c.timestamp > mMostRecentComment.timestamp) {
                            mMostRecentComment = c;
                        }
                    }
                } else {
                    Comment newMostRecentComment = mMostRecentComment;

                    for (Comment c : comments) {
                        if (c.timestamp > newMostRecentComment.timestamp) {
                            notifyListeners(c);
                            newMostRecentComment = c;
                        }
                    }

                    mMostRecentComment = newMostRecentComment;
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

    private String getAppAnnieURL() {
        String url = String.format("%s/apps/%s/app/%s/reviews?", APP_ANNIE_BASE_URL, mMarket, mProductId);

        if (mStartDate != null && mEndDate != null && mStartDate.length() > 0 && mEndDate.length() > 0) {
            url += String.format("&start_date=%s&end_date=%s", mStartDate, mEndDate);
        }

        if (mVersion != null && mVersion.length() > 0) {
            url += String.format("&version=%s", mVersion);
        }

        if (mCountries != null && mCountries.length() > 0) {
            url += String.format("&countries=%s", mCountries);
        }

        if (mPageIndex != null && mPageIndex.length() > 0) {
            url += String.format("&page_index=%s", mPageIndex);
        }

        return url;
    }

    private List<Comment> getCommentsFromJson(JsonNode json) {
        ArrayList<Comment> comments = new ArrayList<Comment>();

        JsonNode entryNode = json.path("reviews");

        Iterator<JsonNode> childNodes = entryNode.elements();
        while (childNodes.hasNext()) {
            JsonNode n = childNodes.next();

            final Comment c = getCommentFromJsonNode(n);
            if (c != null) {
                comments.add(c);
            }
        }

        return comments;
    }

    private Comment getCommentFromJsonNode(JsonNode node) {
        Comment c = new Comment();
        c.rating = node.get("rating").asInt();
        c.author = node.get("reviewer").textValue();
        c.text = String.format("%s. %s", node.get("title").textValue(), node.get("text").textValue());

        JsonNode versionNode = node.get("version");
        JsonNode countryNode = node.get("country");

        c.version = versionNode != null ? versionNode.textValue() : "";
        c.countryCode = countryNode != null ? countryNode.textValue() : "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(node.get("date").textValue());
            c.timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();

            c.timestamp = -1;
        }

        return (c.timestamp > 0) ? c : null;
    }
}
