package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;

public class Utils {

    public static String formatRatingWithStars(int rating) {
        switch (rating) {
            case 1:
                return "\u2605\u2606\u2606\u2606\u2606";
            case 2:
                return "\u2605\u2605\u2606\u2606\u2606";
            case 3:
                return "\u2605\u2605\u2605\u2606\u2606";
            case 4:
                return "\u2605\u2605\u2605\u2605\u2606";
            case 5:
                return "\u2605\u2605\u2605\u2605\u2605";
            default:
                return "";
        }
    }

    public static JsonNode fetchJsonFromUrl(String url) {
        return fetchJsonFromUrl(url, null);
    }

    public static JsonNode fetchJsonFromUrl(String url, List<Pair<String, String>> headers) {
        String text = fetchStringFromUrl(url, headers);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static boolean isNonEmptyString(String str) {
        return str != null && str.length() > 0;
    }

    private static String fetchStringFromUrl(String urlToFetch, List<Pair<String, String>> headers) {
        OkHttpClient client = new OkHttpClient();

        Request request = requestForUrlWithHeaders(urlToFetch, headers);

        try {
            Response response = client.newCall(request).execute();

            if (response != null && response.body() != null) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Request requestForUrlWithHeaders(String url, List<Pair<String, String>> headers) {
        Request.Builder request = new Request.Builder();

        request.url(url);

        if (headers != null) {
            for (Pair<String, String> header : headers) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }

        return request.build();
    }
}
