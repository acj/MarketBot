package org.linuxguy.MarketBot;

import java.util.ArrayList;
import java.util.List;

public abstract class Watcher<T> extends Thread {

    protected long               mLastPollTime;

    private List<ResultListener> mListeners;

    public Watcher() {
        mListeners = new ArrayList<ResultListener>();

        mLastPollTime = System.currentTimeMillis();
    }

    public abstract void run();

    public void addListener(ResultListener<T> listener) {
        mListeners.add(listener);
    }

    public void removeListener(ResultListener<T> listener) {
        mListeners.remove(listener);
    }

    protected void notifyListeners(T result) {
        for (ResultListener listener : mListeners) {
            listener.onNewResult(result);
        }
    }
}
