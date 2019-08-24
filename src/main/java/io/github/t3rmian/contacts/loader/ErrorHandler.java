package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.loader.exception.ApplicationException;

public interface ErrorHandler {
    void handleError(int from, int to, ApplicationException exception);
    void handleError(long record, ApplicationException exception);
}
