package io.github.t3rmian.contacts.loader;

import java.io.InputStream;

public interface Loader<O> {
    void parseInput(InputStream inputStream);

    void fireRecordLoad(O output);

    void setErrorHandler(ErrorHandler errorHandler);
}
