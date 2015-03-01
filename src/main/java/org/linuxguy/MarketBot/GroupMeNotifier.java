package org.linuxguy.MarketBot;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class GroupMeNotifier extends Notifier<Comment> implements ResultListener<Comment> {
    private static final String GROUPME_BOT_API = "https://api.groupme.com/v3/bots/post";

    private String mAppName;
    private String mAccessToken;

    public GroupMeNotifier(String appName, String accessToken) {
        mAppName = appName;
        mAccessToken = accessToken;
    }

    @Override
    public void onNewResult(Comment result) {
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
        MediaType urlEncodedMediaType = MediaType.parse("application/x-www-form-urlencoded");

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

    private String getJsonPayloadForComment(Comment c) throws UnsupportedEncodingException {
        return String.format("{\"text\" : \"%s\", \"bot_id\" : \"%s\"}", formatComment(c), mAccessToken);
    }


    private String formatComment(Comment c) {
        String escapedString = c.text.replace("\"", "\\\"")
                                     .replace("\t", "    ")
                                     .replace("\n", "    ");

        return String.format("%s: \\\"%s\\\"  %s \u2014%s",
                mAppName, escapedString, Utils.formatRatingWithStars(c.rating), c.author);
    }
}
