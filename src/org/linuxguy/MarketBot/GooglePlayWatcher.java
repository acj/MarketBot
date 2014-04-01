package org.linuxguy.MarketBot;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;

import java.util.concurrent.TimeUnit;

public class GooglePlayWatcher extends Watcher<Comment> implements MarketSession.Callback<Market.CommentsResponse> {
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);

    private String mUsername;
    private String mPassword;
    private String mAppId;

    public GooglePlayWatcher(String username, String password, String appId) {
        mUsername = username;
        mPassword = password;
        mAppId = appId;
    }

    @Override
    public void run() {
        MarketSession session = new MarketSession();
        session.login(mUsername, mPassword);

        while (true) {
            Market.CommentsRequest commentsRequest = Market.CommentsRequest.newBuilder()
                    .setAppId(mAppId)
                    .setStartIndex(0)
                    .setEntriesCount(5)
                    .build();

            session.append(commentsRequest, this);

            session.flush();

            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void onResult(Market.ResponseContext responseContext, Market.CommentsResponse response) {
        for (Market.Comment c : response.getCommentsList()) {
            if (c.getCreationTime() > mLastPollTime) {
                notifyListeners(Comment.from(c));
            }
        }

        mLastPollTime = System.currentTimeMillis();
    }
}
