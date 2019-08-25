package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.loader.RecordLoadListener;
import io.github.t3rmian.contacts.loader.RecordErrorHandler;
import io.github.t3rmian.contacts.loader.exception.DatabaseException;
import io.github.t3rmian.contacts.data.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects read records into batches for database insert
 */
public class CustomerBatchManager implements RecordLoadListener<Customer> {
    private final CustomerDao customerDao;
    private final RecordErrorHandler errorHandler;
    private final int batchSize;
    private List<Customer> customers = new ArrayList<>();
    private int recordNumber;

    public CustomerBatchManager(CustomerDao customerDao, RecordErrorHandler errorHandler, int batchSize) {
        this.customerDao = customerDao;
        this.errorHandler = errorHandler;
        this.batchSize = batchSize;
    }

    @Override
    public void onRecordRead(Customer customer) {
        recordNumber++;
        customers.add(customer);
        if (customers.size() % batchSize == 0) {
            tryBatchSaveAll();
        }
    }

    @Override
    public void onFinishRead() {
        if (customers.size() > 0) {
            tryBatchSaveAll();
        }
    }

    private void tryBatchSaveAll() {
        try {
            customerDao.batchSaveAll(customers);
        } catch (SQLException e) {
            int to = recordNumber;
            int from = recordNumber - (recordNumber % batchSize) + 1;
            errorHandler.handleError(from, to, new DatabaseException(e));
        }
        customers.clear();
    }
}
