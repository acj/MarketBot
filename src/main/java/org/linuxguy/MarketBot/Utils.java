package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

public class Utils {

    public static class Header {
        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

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

    public static JsonNode fetchJsonFromUrl(String url, List<Header> headers) {
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

    private static String fetchStringFromUrl(String urlToFetch, List<Header> headers) {
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

    private static Request requestForUrlWithHeaders(String url, List<Header> headers) {
        Request.Builder request = new Request.Builder();

        request.url(url);

        if (headers != null) {
            for (Header header : headers) {
                request.addHeader(header.key, header.value);
            }
        }

        return request.build();
    }
}
