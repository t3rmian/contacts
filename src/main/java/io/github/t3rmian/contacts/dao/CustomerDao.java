package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.data.Contact;
import io.github.t3rmian.contacts.data.Customer;

import java.sql.*;
import java.util.Iterator;
import java.util.List;

public class CustomerDao extends AbstractDao {

    /**
     *
     * @param customers to save in one batch, together with all contacts
     * @throws SQLException in case of errors, will rollback whole batch
     */
    void batchSaveAll(List<Customer> customers) throws SQLException {
        String customerSql = "insert into CUSTOMERS (NAME, SURNAME, AGE) values (?, ?, ?)";
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement customerStatement = connection.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS)) {
                batchSaveAllCustomers(customers, customerStatement);
                ResultSet generatedKeys = customerStatement.getGeneratedKeys();

                String contactSql = "insert into CONTACTS (ID_CUSTOMER, CONTACT, TYPE) values (?, ?, ?)";
                try (PreparedStatement contactStatement = connection.prepareStatement(contactSql)) {
                    batchSaveAllContacts(customers, generatedKeys, contactStatement);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private void batchSaveAllCustomers(List<Customer> customers, PreparedStatement ps) throws SQLException {
        for (Customer customer : customers) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setObject(3, customer.getAge(), Types.INTEGER);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private void batchSaveAllContacts(List<Customer> customers, ResultSet generatedKeys, PreparedStatement ps) throws SQLException {
        Iterator<Customer> customerIterator = customers.iterator();
        while (generatedKeys.next()) {
            long parentId = generatedKeys.getLong(1);
            Customer customer = customerIterator.next();
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
