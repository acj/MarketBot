package org.linuxguy.MarketBot;

public class Comment {
    public String author;
    public int    rating;
    public String text;
    public long   timestamp;
    public String version;
    public String countryCode;

    @Override
    public boolean equals(Object that) {
        if (that instanceof Comment) {
            Comment thatComment = (Comment)that;

            return safeStringEquals(this.author, thatComment.author) &&
                    this.rating == thatComment.rating &&
                    safeStringEquals(this.text, thatComment.text) &&
                    this.timestamp == thatComment.timestamp;
        }

        return false;
    }

    private static boolean safeStringEquals(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }
    }
}
