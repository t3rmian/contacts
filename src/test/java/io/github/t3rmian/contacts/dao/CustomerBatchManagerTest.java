package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.loader.RecordErrorHandler;
import io.github.t3rmian.contacts.loader.exception.DatabaseException;
import io.github.t3rmian.contacts.data.Customer;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class CustomerBatchManagerTest {

    @Test
    void onRecordRead() throws SQLException {
        CustomerDao customerDao = mock(CustomerDao.class);
        RecordErrorHandler errorHandler = mock(RecordErrorHandler.class);
        CustomerBatchManager batchManager = new CustomerBatchManager(customerDao, errorHandler, 10);
        for (int i = 0; i < 9; i++) {
            batchManager.onRecordRead(new Customer());
            verify(customerDao, never()).batchSaveAll(anyList());
        }

        doAnswer(invocation -> {
            verify(customerDao, times(1))
                    .batchSaveAll(argThat(argument -> argument != null && argument.size() == 10));
            return null;
        }).when(customerDao).batchSaveAll(anyList());

        batchManager.onRecordRead(new Customer());
        verify(customerDao, times(1))
                .batchSaveAll(argThat(argument -> argument != null && argument.isEmpty()));
    }

    @Test
    void onFinishRead() throws SQLException {
        CustomerDao customerDao = mock(CustomerDao.class);
        RecordErrorHandler errorHandler = mock(RecordErrorHandler.class);
        CustomerBatchManager batchManager = new CustomerBatchManager(customerDao, errorHandler, 10);
        for (int i = 0; i < 5; i++) {
            batchManager.onRecordRead(new Customer());
            verify(customerDao, never()).batchSaveAll(anyList());
        }

        doAnswer(invocation -> {
            verify(customerDao, times(1))
                    .batchSaveAll(argThat(argument -> argument != null && argument.size() == 5));
            return null;
        }).when(customerDao).batchSaveAll(anyList());

        batchManager.onFinishRead();
        verify(customerDao, times(1))
                .batchSaveAll(argThat(argument -> argument != null && argument.isEmpty()));
    }

    @Test
    void onFinishRead_Error() throws SQLException {
        CustomerDao customerDao = mock(CustomerDao.class);
        RecordErrorHandler errorHandler = mock(RecordErrorHandler.class);
        CustomerBatchManager batchManager = new CustomerBatchManager(customerDao, errorHandler, 10);
        for (int i = 0; i < 5; i++) {
            batchManager.onRecordRead(new Customer());
            verify(customerDao, never()).batchSaveAll(anyList());
        }
        doThrow(new SQLException("mock")).when(customerDao).batchSaveAll(anyList());
        batchManager.onFinishRead();
        verify(errorHandler).handleError(eq(1), eq(5), any(DatabaseException.class));
    }
}