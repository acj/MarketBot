package org.linuxguy.MarketBot;

public abstract class Notifier<T> implements ResultListener<T>, ReviewFormatter {
    protected ReviewFormatter mReviewFormatter;

    public void setReviewFormatter(ReviewFormatter formatter) {
        mReviewFormatter = formatter;
    }

    public abstract void onNewResult(T result);
}
