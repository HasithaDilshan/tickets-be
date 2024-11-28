package com.tikets.tickets.interfaces;

public interface ThreadLifecycleListener<T> {
    void onThreadStart(T entity);
    void onThreadExit(T entity);
}
