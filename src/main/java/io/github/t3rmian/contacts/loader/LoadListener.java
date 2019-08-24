package io.github.t3rmian.contacts.loader;

public interface LoadListener<T> {
    void onRecordRead(T t);
    default void onFinishRead() {}
}
