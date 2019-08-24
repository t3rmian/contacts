package io.github.t3rmian.contacts.loader;

import java.io.InputStream;

public interface Loader<I, O> {
    void parseInput(InputStream inputStream);

    O mapToDataRecord(I input);

    void loadDataRecord(O output);
}
