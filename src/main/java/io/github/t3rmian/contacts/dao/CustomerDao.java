package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;

import java.sql.*;
import java.util.List;

public class CustomerDao extends AbstractDao {

    void batchSaveAll(List<Customer> customers) throws SQLException {
        String customerSql = "insert into CUSTOMERS (NAME, SURNAME, AGE) values (?, ?, ?)";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement customerStatement = connection.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS)) {
                batchSaveAllCustomers(customers, customerStatement);
                ResultSet generatedKeys = customerStatement.getGeneratedKeys();

                String contactSql = "insert into CONTACTS (ID_CUSTOMERS, CONTACT, TYPE) values (?, ?, ?)";
                try (PreparedStatement contactStatement = connection.prepareStatement(contactSql)) {
                    batchSaveAllContacts(customers, generatedKeys, contactStatement);
                }
            }
            connection.commit();
        }
    }

    private void batchSaveAllCustomers(List<Customer> customers, PreparedStatement ps) throws SQLException {
        for (Customer customer : customers) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setInt(3, customer.getAge());
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private void batchSaveAllContacts(List<Customer> customers, ResultSet generatedKeys, PreparedStatement ps) throws SQLException {
        while (generatedKeys.next()) {
            long parentId = generatedKeys.getLong(1);
            Customer customer = customers.iterator().next();
            for (Contact contact : customer.getContacts()) {
                ps.setLong(1, parentId);
                ps.setString(2, contact.getContact());
                ps.setInt(3, contact.getType().ordinal());
                ps.addBatch();
            }
        }
        ps.executeBatch();
    }

}
