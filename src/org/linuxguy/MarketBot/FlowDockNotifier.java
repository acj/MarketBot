package org.linuxguy.MarketBot;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class FlowDockNotifier extends Notifier<Comment> implements ResultListener<Comment> {
    private static final String FLOWDOCK_CHAT_API = "https://api.flowdock.com/v1/messages/chat/";
    private static final String FLOWDOCK_INBOX_API = "https://api.flowdock.com/v1/messages/team_inbox/";

    public enum FlowDockNotificationType {
        CHAT,
        INBOX
    }

    private String mAppName;
    private String mFlowDockName;
    private String mAPIToken;
    private FlowDockNotificationType mNotificationType;

    public FlowDockNotifier(String appName, String flowDockName, String apiToken, FlowDockNotificationType notificationType) {
        mAppName = appName;
        mFlowDockName = flowDockName;
        mAPIToken = apiToken;
        mNotificationType = notificationType;
    }

    @Override
    public void onNewResult(Comment result) {
        try {
            String toPost = getJsonPayloadForComment(result);

            if (!postCommentToFlowDock(toPost)) {
                System.err.println("Failed to post payload");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean postCommentToFlowDock(String comment) {
        final String assembledURL = String.format("%s%s", getURLForNotificationType(mNotificationType), mAPIToken);
        HttpsURLConnection httpcon = null;

        try {
            URL url = new URL(assembledURL);
            httpcon = (HttpsURLConnection) url.openConnection();
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestMethod("POST");

            httpcon.setDoOutput(true);
            httpcon.connect();

            byte[] outputBytes = comment.getBytes("UTF-8");
            OutputStream os = new BufferedOutputStream(httpcon.getOutputStream());
            os.write(outputBytes);

            os.flush();
            os.close();

            return httpcon.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if ( httpcon != null ) {
                httpcon.disconnect();
            }
        }
    }

    private String getJsonPayloadForComment(Comment c) throws UnsupportedEncodingException {
        final String tags = "[#review]";
        final String flowdockChatJSONFormat =
            "{\"content\" : \"%s\", \"external_user_name\" : \"%s\", \"tags\" : \"%s\" }";
        final String flowdockInboxJSONFormat =
            "{\"source\" : \"%s\", \"from_address\" : \"marketbot@linuxguy.org\", \"subject\" : \"%s Review\", \"content\" : \"%s\", \"tags\" : \"%s\" }";

        String jsonPayload = null;
        switch (mNotificationType) {
            case CHAT:
                jsonPayload = String.format(flowdockChatJSONFormat, formatComment(c), mFlowDockName, tags);
                break;
            case INBOX:
                jsonPayload = String.format(flowdockInboxJSONFormat, mFlowDockName, mAppName, formatComment(c), tags);
                break;
        }

        return jsonPayload;
    }

    private String getURLForNotificationType(FlowDockNotificationType notificationType) {
        String apiURL = null;

        switch (notificationType) {
            case CHAT:
                apiURL = FLOWDOCK_CHAT_API;
                break;
            case INBOX:
                apiURL = FLOWDOCK_INBOX_API;
                break;
            default:
        }

        return apiURL;
    }

    private String formatComment(Comment c) throws UnsupportedEncodingException {
        String escapedString = c.text.replace("\"", "\\\"")
                                     .replace("\t", "    ")
                                     .replace("\n", "    ");
        return String.format("\\\"%s\\\"  %s \u2014%s", escapedString, Utils.formatRatingWithStars(c.rating), c.author);
    }
}
