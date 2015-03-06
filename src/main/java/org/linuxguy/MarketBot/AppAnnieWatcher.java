package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppAnnieWatcher extends Watcher<Review> {

    private static final String APP_ANNIE_BASE_URL = "https://api.appannie.com/v1.2";
    private static final long   POLL_INTERVAL_MS   = TimeUnit.MINUTES.toMillis(5);

    private String          mApiKey;
    private Review          mMostRecentComment;
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

    AppAnnieWatcher(String apiKey, AppAnnieMarket market, String appProductId, String appName) {
        mApiKey = apiKey;
        mMarket = market;
        mProductId = appProductId;
        mAppName = appName;
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
        final ArrayList<Utils.Header> headers = new ArrayList();
        final String bearerToken = String.format("Bearer %s", mApiKey);
        final Utils.Header authHeader = new Utils.Header("Authorization", bearerToken);
        headers.add(authHeader);

        while (true) {
            List<Review> comments = getCommentsFromJson(Utils.fetchJsonFromUrl(getAppAnnieURL(), headers));

            if (comments.size() > 0) {
                if (mMostRecentComment == null) {
                    for (Review c : comments) {
                        if (mMostRecentComment == null || c.timestamp > mMostRecentComment.timestamp) {
                            mMostRecentComment = c;
                        }
                    }
                } else {
                    Review newMostRecentComment = mMostRecentComment;

                    for (Review c : comments) {
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

    private List<Review> getCommentsFromJson(JsonNode json) {
        ArrayList<Review> comments = new ArrayList<Review>();

        JsonNode entryNode = json.path("reviews");

        Iterator<JsonNode> childNodes = entryNode.elements();
        while (childNodes.hasNext()) {
            JsonNode n = childNodes.next();

            final Review c = getCommentFromJsonNode(n);
            if (c != null) {
                comments.add(c);
            }
        }

        return comments;
    }

    private Review getCommentFromJsonNode(JsonNode node) {
        Review review = new Review();
        review.productName = mAppName;
        review.rating = node.get("rating").asInt();
        review.author = node.get("reviewer").textValue();
        review.text = String.format("%s. %s", node.get("title").textValue(), node.get("text").textValue());

        JsonNode versionNode = node.get("version");
        JsonNode countryNode = node.get("country");

        review.version = versionNode != null ? versionNode.textValue() : "";
        review.countryCode = countryNode != null ? countryNode.textValue() : "";

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(node.get("date").textValue());
            review.timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();

            review.timestamp = -1;
        }

        return (review.timestamp > 0) ? review : null;
    }
}
