package io.github.t3rmian.contacts.loader;

public interface RecordLoadListener<T> {
    void onRecordRead(T t);
    default void onFinishRead() {}
}
