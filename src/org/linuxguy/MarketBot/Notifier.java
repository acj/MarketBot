package org.linuxguy.MarketBot;

public abstract class Notifier<T> implements ResultListener<T> {
    public abstract void onNewResult(T result);
}
