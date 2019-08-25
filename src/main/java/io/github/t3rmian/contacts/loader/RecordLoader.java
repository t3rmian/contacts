package io.github.t3rmian.contacts.loader;

import java.io.InputStream;

public interface RecordLoader<O> {
    void parseInput(InputStream inputStream);

    void fireRecordLoad(O output);

    void setErrorHandler(RecordErrorHandler errorHandler);
}
