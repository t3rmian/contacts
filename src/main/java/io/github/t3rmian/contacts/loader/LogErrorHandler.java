package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.Application;
import io.github.t3rmian.contacts.loader.exception.ApplicationException;
import org.slf4j.LoggerFactory;

public class LogErrorHandler implements RecordErrorHandler {
    @Override
    public void handleError(int from, int to, ApplicationException exception) {
        LoggerFactory.getLogger(Application.class).error(
                String.format("Could not import records range: <%d,%d>", from, to),
                exception
        );
    }

    @Override
    public void handleError(long record, ApplicationException exception) {
        LoggerFactory.getLogger(Application.class).error(
                String.format("Could not import record: %d", record),
                exception
        );
    }
}
