package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.loader.LoadListener;
import io.github.t3rmian.contacts.loader.ErrorHandler;
import io.github.t3rmian.contacts.loader.exception.DatabaseException;
import io.github.t3rmian.contacts.model.Customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerBatchManager implements LoadListener<Customer> {
    private final int batchSize;
    private final ErrorHandler errorHandler;
    private List<Customer> customers = new ArrayList<>();
    private CustomerDao customerDao = new CustomerDao();
    private int recordNumber;

    public CustomerBatchManager(ErrorHandler errorHandler, int batchSize) {
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
