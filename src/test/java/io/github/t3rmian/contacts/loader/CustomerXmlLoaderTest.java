package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.model.Contact;
import io.github.t3rmian.contacts.model.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

class CustomerXmlLoaderTest {

    @Test
    void parseFile() {
        List<Customer> parsedCustomers = new ArrayList<>();
        InputStream file = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("test-data.xml"));
        new CustomerXmlLoader(parsedCustomers::add).parseInput(file);
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
        new CustomerXmlLoader(customer -> {
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
        new CustomerXmlLoader(customer -> {
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
    void mapToDataRecord_InvalidFormat() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<persons>\n" +
                "    <person>\n" +
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
        Loader loader = new CustomerXmlLoader((customer) -> fail());
        ErrorHandler errorHandler = mock(ErrorHandler.class);
        loader.setErrorHandler(errorHandler);
        loader.parseInput(inputStream);
        verify(errorHandler, times(1)).handleError(eq(1L), any());
    }


    @SuppressWarnings("unchecked")
    @Test
    void mapToDataRecord_InvalidFormat_SkipAndContinue() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<persons>\n" +
                "    <person>\n" +
                "        <name>First</name>\n" +
                "        <surname>Kowalski</surname>\n" +
                "        <age>12</age>\n" +
                "    </person>\n" +
                "    <person>\n" +
                "        <name>Second</name>\n" +
                "    </person>\n" +
                "    <person>\n" +
                "        <name>Third</name>\n" +
                "        <surname>Kowalski</surname>\n" +
                "        <age>12</age>\n" +
                "    </person>\n" +
                "</persons>";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        LoadListener<Customer> loadListener = (LoadListener<Customer>) mock(LoadListener.class);
        new CustomerXmlLoader(loadListener).parseInput(inputStream);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(loadListener, times(2)).onRecordRead(customerCaptor.capture());
        assertThat(customerCaptor.getAllValues(), hasItem(
                hasProperty("name", equalTo("First"))
        ));
        assertThat(customerCaptor.getAllValues(), hasItem(
                hasProperty("name", equalTo("Third"))
        ));
    }
}