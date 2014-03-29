package org.linuxguy.MarketBot;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

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
                System.err.println("Failed to post payload");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static boolean postCommentToGroupMe(String comment) {
        try {
            URL url = new URL(GROUPME_BOT_API);
            HttpsURLConnection httpcon = (HttpsURLConnection) url.openConnection();
            httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpcon.setRequestProperty("Accept", "*/*");
            httpcon.setRequestMethod("POST");

            httpcon.setDoOutput(true);
            httpcon.connect();

            byte[] outputBytes = comment.getBytes("UTF-8");
            OutputStream os = new BufferedOutputStream(httpcon.getOutputStream());
            os.write(outputBytes);

            os.flush();
            os.close();

            httpcon.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getJsonPayloadForComment(Comment c) throws UnsupportedEncodingException {
        return String.format("{\"text\" : \"%s\", \"bot_id\" : \"%s\"}", Utils.formatComment(mAppName, c), mAccessToken);
    }
}
