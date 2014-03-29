package org.linuxguy.MarketBot;

import java.io.UnsupportedEncodingException;

public class Utils {

    public static String formatComment(String appName, Comment c) throws UnsupportedEncodingException {
        String escapedString = c.text.replace("\"", "\\\"");
        return String.format("[%s]: \\\"%s\\\"  %s  —%s",
                appName, escapedString, Utils.formatRatingWithStars(c.rating), c.author);
    }

    public static String formatRatingWithStars(int rating) {
        switch (rating) {
            case 1:
                return "★";
            case 2:
                return "★★";
            case 3:
                return "★★★";
            case 4:
                return "★★★★";
            case 5:
                return "★★★★★";
            default:
                return "";
        }
    }
}
