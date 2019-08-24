package io.github.t3rmian.contacts.loader;

public interface DataLoadListener<T> {
    void onDataRecordRead(T t);
    default void onFinishRead() {};
}
