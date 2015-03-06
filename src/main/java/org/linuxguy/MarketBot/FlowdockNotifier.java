package org.linuxguy.MarketBot;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FlowdockNotifier extends Notifier<Review> {
    private static final String FLOWDOCK_CHAT_API = "https://api.flowdock.com/v1/messages/chat/";
    private static final String FLOWDOCK_INBOX_API = "https://api.flowdock.com/v1/messages/team_inbox/";

    public enum FlowdockNotificationType {
        CHAT,
        INBOX
    }

    private String mFlowDockName;
    private String mAPIToken;
    private FlowdockNotificationType mNotificationType;

    public FlowdockNotifier(String flowDockName, String apiToken, FlowdockNotificationType notificationType) {
        mFlowDockName = flowDockName;
        mAPIToken = apiToken;
        mNotificationType = notificationType;
        mReviewFormatter = this;
    }

    @Override
    public void onNewResult(Review result) {
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

        MediaType urlEncodedMediaType = MediaType.parse("application/json");

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(urlEncodedMediaType, comment);
            Request request = new Request.Builder()
                                         .url(assembledURL)
                                         .post(body)
                                         .build();

            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private String getJsonPayloadForComment(Review r) throws UnsupportedEncodingException {
        final String tags = "[#review]";
        final String flowdockChatJSONFormat =
            "{\"content\" : \"%s\", \"external_user_name\" : \"%s\", \"tags\" : \"%s\" }";
        final String flowdockInboxJSONFormat =
            "{\"source\" : \"%s\", \"from_address\" : \"marketbot@linuxguy.org\", \"subject\" : \"%s Review\", \"content\" : \"%s\", \"tags\" : \"%s\" }";

        String jsonPayload = null;
        switch (mNotificationType) {
            case CHAT:
                jsonPayload = String.format(flowdockChatJSONFormat, mReviewFormatter.formatReview(r), mFlowDockName, tags);
                break;
            case INBOX:
                jsonPayload = String.format(flowdockInboxJSONFormat, mFlowDockName, r.productName, mReviewFormatter.formatReview(r), tags);
                break;
        }

        return jsonPayload;
    }

    private String getURLForNotificationType(FlowdockNotificationType notificationType) {
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

    @Override
    public String formatReview(Review c) {
        String escapedString = c.text.replace("\"", "\\\"")
                                     .replace("\t", "    ")
                                     .replace("\n", "    ");
        return String.format("\\\"%s\\\"  %s \u2014%s", escapedString, Utils.formatRatingWithStars(c.rating), c.author);
    }
}
