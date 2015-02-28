package org.linuxguy.MarketBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

    public static String formatComment(String appName, Comment c) throws UnsupportedEncodingException {
        String escapedString = c.text.replace("\"", "\\\"")
                                     .replace("\t", "    ")
                                     .replace("\n", "    ");
        return String.format("%s: \\\"%s\\\"  %s \u2014%s",
                appName, escapedString, Utils.formatRatingWithStars(c.rating), c.author);
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
        String text = fetchStringFromUrl(url);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String fetchStringFromUrl(String urlToFetch) {
        try {
            URL url = new URL(urlToFetch);
            URLConnection connection = null;
            connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();

            return "";
        }
    }
}
