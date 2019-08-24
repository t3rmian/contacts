package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

class CustomerDAOIT {

    private final AbstractDAO testDAO = new AbstractDAO() {
    };

    @AfterEach
    void cleanUp() throws SQLException {
        Connection connection = testDAO.getConnection();
        ResultSet toDeleteResultCount = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM CUSTOMERS");
        int customers = 0;
        if (toDeleteResultCount.next()) {
            customers = toDeleteResultCount.getInt(1);
        }

        connection
                .prepareStatement("DELETE FROM CUSTOMERS")
                .executeUpdate();

        System.out.println("Cleaned up " + customers + " customers");
    }

    @Test
    void testSave() throws SQLException {
        CustomerDAO dao = new CustomerDAO();
        Customer customer = new Customer();
        customer.setName("Damian");
        customer.setSurname("Terlecki");
        customer.setAge(99);
        dao.save(Collections.singletonList(customer));
    }

    @Test
    void testSaveWithContact() throws SQLException {
        CustomerDAO dao = new CustomerDAO();
        Customer customer = new Customer();
        customer.setName("Damian");
        customer.setSurname("Terlecki");
        customer.setAge(99);
        Contact contact = new Contact();
        contact.setType(Contact.Type.UNKNOWN);
        contact.setContact("abc");
        customer.setContacts(Collections.singletonList(contact));
        dao.save(Collections.singletonList(customer));
    }
}