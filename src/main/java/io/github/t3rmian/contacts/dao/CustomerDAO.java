package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;

import java.sql.*;
import java.util.List;

public class CustomerDAO extends AbstractDAO {

    void batchSaveAll(List<Customer> customers) throws SQLException {
        String sql = "insert into CUSTOMERS (NAME, SURNAME, AGE) values (?, ?, ?)";
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (Customer customer : customers) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getSurname());
            ps.setInt(3, customer.getAge());
            ps.addBatch();
        }
        ps.executeBatch();
        ResultSet generatedKeys = ps.getGeneratedKeys();
        ps.close();

        String sql2 = "insert into CONTACTS (ID_CUSTOMERS, CONTACT, TYPE) values (?, ?, ?)";
        PreparedStatement ps2 = connection.prepareStatement(sql2);
        while (generatedKeys.next()) {
            long parentId = generatedKeys.getLong(1);
            Customer customer = customers.iterator().next();
            for (Contact contact : customer.getContacts()) {
                ps2.setLong(1, parentId);
                ps2.setString(2, contact.getContact());
                ps2.setInt(3, contact.getType().ordinal());
                ps2.addBatch();
            }
        }
        ps2.executeBatch();
        ps2.close();
        connection.commit();
        connection.close();
    }

}
