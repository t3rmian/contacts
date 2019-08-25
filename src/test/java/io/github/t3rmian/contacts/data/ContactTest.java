package io.github.t3rmian.contacts.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void getType() {
        assertEquals(0, Contact.Type.UNKNOWN.ordinal());
        assertEquals(1, Contact.Type.EMAIL.ordinal());
        assertEquals(2, Contact.Type.PHONE.ordinal());
        assertEquals(3, Contact.Type.JABBER.ordinal());
    }
}