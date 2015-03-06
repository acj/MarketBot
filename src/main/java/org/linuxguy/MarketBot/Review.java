package org.linuxguy.MarketBot;

public class Review {
    public String productName;
    public String author;
    public int    rating;
    public String title;
    public String text;
    public long   timestamp;
    public String version;
    public String deviceName;
    public String countryCode;

    @Override
    public boolean equals(Object that) {
        if (that instanceof Review) {
            Review thatReview = (Review)that;

            return safeStringEquals(this.productName, thatReview.productName) &&
                    safeStringEquals(this.author, thatReview.author) &&
                    this.rating == thatReview.rating &&
                    safeStringEquals(this.title, thatReview.title) &&
                    safeStringEquals(this.text, thatReview.text) &&
                    this.timestamp == thatReview.timestamp &&
                    safeStringEquals(this.version, thatReview.version) &&
                    safeStringEquals(this.deviceName, thatReview.deviceName) &&
                    safeStringEquals(this.countryCode, thatReview.countryCode);
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
