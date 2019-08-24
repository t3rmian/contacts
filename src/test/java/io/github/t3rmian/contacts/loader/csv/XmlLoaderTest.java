package io.github.t3rmian.contacts.loader.csv;

import io.github.t3rmian.contacts.loader.xml.XmlLoader;
import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlLoaderTest {

    @Test
    void parseFile() {
        List<Customer> parsedCustomers = new ArrayList<>();
        InputStream file = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("test-data.xml"));
        new XmlLoader(parsedCustomers::add).parseInput(file);
        assertEquals(2, parsedCustomers.size());
    }

    @Test
    void mapToDataRecord() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<persons>\n" +
                "    <person>\n" +
                "        <name>Jan</name>\n" +
                "        <surname>Kowalski</surname>\n" +
                "        <age>12</age>\n" +
                "        <city>Lublin</city>\n" +
                "        <contacts>\n" +
                "            <phone>123123123</phone>\n" +
                "            <phone>654 765 765</phone>\n" +
                "            <email>kowalski@gmail.com</email>\n" +
                "            <email>jan@gmail.com</email>\n" +
                "        </contacts>\n" +
                "    </person>\n" +
                "</persons>";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new XmlLoader(customer -> {
            assertEquals("Jan", customer.getName());
            assertEquals("Kowalski", customer.getSurname());
            assertEquals(12, customer.getAge());
        }).parseInput(inputStream);
    }

    @Test
    void mapToContacts() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<persons>\n" +
                "    <person>\n" +
                "        <name>Jan</name>\n" +
                "        <surname>Kowalski</surname>\n" +
                "        <age>12</age>\n" +
                "        <city>Lublin</city>\n" +
                "        <contacts>\n" +
                "            <phone>123123123</phone>\n" +
                "            <phone>654 765 765</phone>\n" +
                "            <email>kowalski@gmail.com</email>\n" +
                "            <email>jan@gmail.com</email>\n" +
                "        </contacts>\n" +
                "    </person>\n" +
                "</persons>";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new XmlLoader(customer -> {
            assertEquals(4, customer.getContacts().size());
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("123123123")),
                    hasProperty("type", equalTo(Contact.Type.PHONE))
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("654 765 765")),
                    hasProperty("type", equalTo(Contact.Type.PHONE))
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("kowalski@gmail.com")),
                    hasProperty("type", equalTo(Contact.Type.EMAIL))
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("jan@gmail.com")),
                    hasProperty("type", equalTo(Contact.Type.EMAIL))
            )));
        }).parseInput(inputStream);
    }

    @Test
    void loadDataRecord() {
        //TODO: consider removing
    }
}