package org.linuxguy.MarketBot;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GroupMeNotifier extends Notifier<Review> {
    private static final String GROUPME_BOT_API = "https://api.groupme.com/v3/bots/post";

    private String mAppName;
    private String mAccessToken;

    public GroupMeNotifier(String appName, String accessToken) {
        mAppName = appName;
        mAccessToken = accessToken;
        mReviewFormatter = this;
    }

    @Override
    public void onNewResult(Review result) {
        try {
            String toPost = getJsonPayloadForComment(result);

            if (!postCommentToGroupMe(toPost)) {
                System.err.println(String.format("Failed to post payload: %s", toPost));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static boolean postCommentToGroupMe(String comment) {
        MediaType urlEncodedMediaType = MediaType.parse("application/json");

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(urlEncodedMediaType, comment);
            Request request = new Request.Builder()
                                         .url(GROUPME_BOT_API)
                                         .post(body)
                                         .build();

            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private String getJsonPayloadForComment(Review c) throws UnsupportedEncodingException {
        return String.format("{\"text\" : \"%s\", \"bot_id\" : \"%s\"}", mReviewFormatter.formatReview(c), mAccessToken);
    }

    @Override
    public String formatReview(Review c) {
        String escapedString = c.text.replace("\"", "\\\"")
                                     .replace("\t", "    ")
                                     .replace("\n", "    ");

        return String.format("\\\"%s\\\" \u2014%s %s\\n\\n%s v%s (%s storefront)",
                escapedString, c.author, Utils.formatRatingWithStars(c.rating), mAppName, c.version, c.countryCode);
    }
}
