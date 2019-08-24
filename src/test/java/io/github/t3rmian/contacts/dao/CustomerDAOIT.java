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

    private final AbstractDao testDAO = new AbstractDao() {
    };

    @AfterEach
    void cleanUp() throws SQLException {
        Connection connection = testDAO.getConnection();
        ResultSet contactsToDeleteResultCount = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM CONTACTS");
        int contacts = 0;
        if (contactsToDeleteResultCount.next()) {
            contacts = contactsToDeleteResultCount.getInt(1);
        }
        ResultSet customersToDeleteResultCount = connection.createStatement()
                .executeQuery("SELECT COUNT(*) FROM CUSTOMERS");
        int customers = 0;
        if (customersToDeleteResultCount.next()) {
            customers = customersToDeleteResultCount.getInt(1);
        }

        connection
                .prepareStatement("DELETE FROM CONTACTS")
                .executeUpdate();
        connection
                .prepareStatement("DELETE FROM CUSTOMERS")
                .executeUpdate();

        System.out.println("Cleaned up " + contacts + " contacts");
        System.out.println("Cleaned up " + customers + " customers");
    }

    @Test
    void testSave() throws SQLException {
        CustomerDao dao = new CustomerDao();
        Customer customer = new Customer();
        customer.setName("Damian");
        customer.setSurname("Terlecki");
        customer.setAge(99);
        dao.batchSaveAll(Collections.singletonList(customer));
    }

    @Test
    void testSaveWithContact() throws SQLException {
        CustomerDao dao = new CustomerDao();
        Customer customer = new Customer();
        customer.setName("Damian");
        customer.setSurname("Terlecki");
        customer.setAge(99);
        Contact contact = new Contact();
        contact.setType(Contact.Type.UNKNOWN);
        contact.setContact("abc");
        customer.setContacts(Collections.singletonList(contact));
        dao.batchSaveAll(Collections.singletonList(customer));
    }
}