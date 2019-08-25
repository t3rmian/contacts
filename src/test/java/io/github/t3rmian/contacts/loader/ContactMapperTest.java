package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.model.Contact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactMapperTest {

    @Test
    void mapToContact() {
        String value = "asdfvsvzxcvzsdf";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(value, contact.getContact());
    }

    @Test
    void mapToContact_Email() {
        String value = "john@example.com";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.EMAIL, contact.getType());
    }

    @Test
    void mapToContact_Phone() {
        String value = "(903) 208-5686";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone2() {
        String value = "+48-785-5564-63";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone3() {
        String value = "+1-202-555-0175";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone4() {
        String value = "163(480)501-53-96";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone5() {
        String value = "163(480)501-53-96";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone6() {
        String value = "(0607) 123 4567";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone7() {
        String value = "+22 607 123 4567";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Phone_Jabber_ICQ() {
        String value = "10000";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.PHONE, contact.getType());
    }

    @Test
    void mapToContact_Jabber() {
        String value = "jbr";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.JABBER, contact.getType());
    }

    @Test
    void mapToContact_Unknown() {
        String value = "@";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.UNKNOWN, contact.getType());
    }

    @Test
    void mapToContact_Unknown2() {
        String value = "@.";
        ContactMapper contactMapper = new ContactMapper();
        Contact contact = contactMapper.mapToContact(value);
        assertEquals(Contact.Type.UNKNOWN, contact.getType());
    }

}