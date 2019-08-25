package io.github.t3rmian.contacts.dao;

import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDaoIT {

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
    void save() throws SQLException {
        CustomerDao dao = new CustomerDao();
        Customer customer = new Customer();
        customer.setName("Damian");
        customer.setSurname("Terlecki");
        customer.setAge(99);
        dao.batchSaveAll(Collections.singletonList(customer));
    }

    @Test
    void saveWithContact() throws SQLException {
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

    @Test
    void saveWithContact_VerifyBinding() throws SQLException {
        CustomerDao dao = new CustomerDao();
        Customer customer = new Customer();
        customer.setName("D1");
        customer.setSurname("T1");
        customer.setAge(99);
        Contact contact = new Contact();
        contact.setType(Contact.Type.UNKNOWN);
        contact.setContact("abc1");
        customer.setContacts(Collections.singletonList(contact));

        Customer customer2 = new Customer();
        customer2.setName("D2");
        customer2.setSurname("T2");
        Contact contact2 = new Contact();
        contact2.setType(Contact.Type.PHONE);
        contact2.setContact("abc2");
        Contact contact3 = new Contact();
        contact3.setType(Contact.Type.JABBER);
        contact3.setContact("abc3");
        customer2.setContacts(Arrays.asList(contact2, contact3));

        dao.batchSaveAll(Arrays.asList(customer, customer2));

        Connection connection = testDAO.getConnection();
        ResultSet customerSet = connection
                .prepareStatement("SELECT * FROM CUSTOMERS ORDER BY NAME")
                .executeQuery();
        ResultSet contactSet = connection
                .prepareStatement("SELECT * FROM CONTACTS ORDER BY CONTACT")
                .executeQuery();
        customerSet.next();
        assertNotNull(customerSet.getString("ID"));
        assertEquals(customerSet.getString("NAME"), customer.getName());
        assertEquals(customerSet.getString("SURNAME"), customer.getSurname());
        assertEquals(customerSet.getInt("AGE"), customer.getAge());
        contactSet.next();
        assertNotNull(contactSet.getString("ID"));
        assertEquals(contactSet.getString("ID_CUSTOMER"), customerSet.getString("ID"));
        assertEquals(contactSet.getString("CONTACT"), contact.getContact());
        assertEquals(contactSet.getInt("TYPE"), contact.getType().ordinal());

        customerSet.next();
        contactSet.next();
        assertNotNull(customerSet.getString("ID"));
        assertNull(customerSet.getString("AGE"));
        assertEquals(contactSet.getString("ID_CUSTOMER"), customerSet.getString("ID"));
        assertEquals(contactSet.getString("CONTACT"), contact2.getContact());
        contactSet.next();
        assertEquals(contactSet.getString("ID_CUSTOMER"), customerSet.getString("ID"));
        assertEquals(contactSet.getString("CONTACT"), contact3.getContact());

        assertFalse(customerSet.next());
        assertFalse(contactSet.next());
    }
}