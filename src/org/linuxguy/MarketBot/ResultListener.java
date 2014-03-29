package org.linuxguy.MarketBot;

public interface ResultListener<T> {
    public void onNewResult(T result);
}
