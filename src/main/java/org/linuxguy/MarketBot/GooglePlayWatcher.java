package org.linuxguy.MarketBot;

import com.akdeniz.googleplaycrawler.GooglePlay;
import com.akdeniz.googleplaycrawler.GooglePlayAPI;

import java.util.concurrent.TimeUnit;

public class GooglePlayWatcher extends Watcher<Review> {
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final int  NONE             = -1;
    private static final String ANDROID_ID     = "dead000beef";

    private GooglePlayAPI mGooglePlayAPI;
    private String        mUsername;
    private String        mPassword;
    private String        mAppId;

    public GooglePlayWatcher(String username, String password, String appId, String appName) {
        mUsername = username;
        mPassword = password;
        mAppId = appId;
        mAppName = appName;
        mLastPollTime = NONE;
    }

    @Override
    public void run() {
        mGooglePlayAPI = new GooglePlayAPI(mUsername, mPassword, ANDROID_ID);
        mGooglePlayAPI.setLocalization("en-EN");
        try {
            mGooglePlayAPI.login();
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        while (true) {
            try {
                GooglePlay.ReviewResponse reviews = mGooglePlayAPI.reviews(mAppId, GooglePlayAPI.REVIEW_SORT.NEWEST, 0, 5);
                GooglePlay.GetReviewsResponse response = reviews.getGetResponse();
                if (response.getReviewCount() == 0) {
                    System.out.println("No comments in Google Play response");
                }

                for (GooglePlay.Review r : response.getReviewList()) {
                    if (mLastPollTime == NONE) {
                        mLastPollTime = r.getTimestampMsec();
                    } else if (r.getTimestampMsec() > mLastPollTime) {
                        notifyListeners(reviewFromMarketReview(r));
                    }

                    if (response.getReviewCount() > 0) {
                        mLastPollTime = response.getReview(0).getTimestampMsec();
                    }
                }

                try {
                    Thread.sleep(POLL_INTERVAL_MS);
                } catch (InterruptedException e) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Review reviewFromMarketReview(GooglePlay.Review review) {
        Review r = new Review();

        r.productName = mAppName;
        r.author      = review.getAuthorName();
        r.rating      = review.getStarRating();
        r.title       = review.getTitle();
        r.text        = review.getComment();
        r.timestamp   = review.getTimestampMsec();
        r.version     = review.getDocumentVersion();
        r.deviceName  = review.getDeviceName();

        return r;
    }
}