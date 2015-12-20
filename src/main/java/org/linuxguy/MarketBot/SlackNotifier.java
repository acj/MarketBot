package org.linuxguy.MarketBot;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SlackNotifier extends Notifier<Review> {
    private static final String POST_MESSAGE_ENDPOINT = "https://slack.com/api/chat.postMessage";

    private String mAPIToken;
    private String mBotName;
    private String mSlackChannelName;

    public SlackNotifier(String apiToken, String botName, String channelName) {
        mAPIToken = apiToken;
        mBotName = botName;
        mReviewFormatter = this;
        mSlackChannelName = channelName;
    }

    @Override
    public void onNewResult(Review result) {
        try {
            if (!postReviewToSlack(getURLForReview(result))) {
                System.err.println("Failed to post payload");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private boolean postReviewToSlack(String endpointURL) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                                         .url(endpointURL)
                                         .build();

            Response response = client.newCall(request).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private String getURLForReview(Review r) throws UnsupportedEncodingException {
        String characterEncoding = "UTF-8";
        StringBuilder builder = new StringBuilder(POST_MESSAGE_ENDPOINT + "?");
        builder.append("token=" + URLEncoder.encode(mAPIToken, characterEncoding));
        builder.append("&channel=" + URLEncoder.encode(mSlackChannelName, characterEncoding));
        builder.append("&username=" + URLEncoder.encode(mBotName, characterEncoding));
        builder.append("&text=" + URLEncoder.encode(mReviewFormatter.formatReview(r), characterEncoding));
        return builder.toString();
    }

    @Override
    public String formatReview(Review r) {
        String version = Utils.isNonEmptyString(r.version) ? r.version : "Unknown version";
        String deviceName = Utils.isNonEmptyString(r.deviceName) ? r.deviceName : "Unknown device";

        return String.format("\"%s\" \u2014%s %s\n\n%s. %s. %s.",
                r.text, r.author, Utils.formatRatingWithStars(r.rating), r.productName, version, deviceName);
    }
}
