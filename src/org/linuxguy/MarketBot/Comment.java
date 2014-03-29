package org.linuxguy.MarketBot;

import com.gc.android.market.api.model.Market;

public class Comment {
    public String author;
    public int    rating;
    public String text;
    public long   timestamp;

    public static Comment from(Market.Comment marketComment) {
        Comment c = new Comment();

        c.author    = marketComment.getAuthorName();
        c.rating    = marketComment.getRating();
        c.text      = marketComment.getText();
        c.timestamp = marketComment.getCreationTime();

        return c;
    }
}
