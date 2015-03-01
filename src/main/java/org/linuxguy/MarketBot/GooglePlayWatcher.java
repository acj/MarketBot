package org.linuxguy.MarketBot;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;

import java.util.concurrent.TimeUnit;

public class GooglePlayWatcher extends Watcher<Comment> implements MarketSession.Callback<Market.CommentsResponse> {
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(5);
    private static final int  NONE             = -1;

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
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResult(Market.ResponseContext responseContext, Market.CommentsResponse response) {
        if (response == null || response.getCommentsCount() == 0) {
            System.out.println("No comments in result");
            return;
        }

        for (Market.Comment c : response.getCommentsList()) {
            if ( mLastPollTime == NONE ) {
                mLastPollTime = c.getCreationTime();
            } else if (c.getCreationTime() > mLastPollTime) {
                notifyListeners(commentFromMarketComment(c));
            }
        }

        if (response.getCommentsCount() > 0) {
            mLastPollTime = response.getComments(0).getCreationTime();
        }
    }

    private Comment commentFromMarketComment(Market.Comment marketComment) {
        Comment c = new Comment();

        c.author    = marketComment.getAuthorName();
        c.rating    = marketComment.getRating();
        c.text      = marketComment.getText();
        c.timestamp = marketComment.getCreationTime();

        return c;
    }
}
