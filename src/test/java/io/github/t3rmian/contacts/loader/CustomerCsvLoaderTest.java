package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.loader.exception.ParsingException;
import io.github.t3rmian.contacts.data.Customer;
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

class CustomerCsvLoaderTest {

    @Test
    void parseFile() {
        List<Customer> parsedCustomers = new ArrayList<>();
        InputStream file = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("test-data.csv"));
        new CustomerCsvLoader(parsedCustomers::add).parseInput(file);
        assertEquals(2, parsedCustomers.size());
    }

    @Test
    void mapToDataRecord() {
        String input = "Jan,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new CustomerCsvLoader(customer -> {
            assertEquals("Jan", customer.getName());
            assertEquals("Kowalski", customer.getSurname());
            assertEquals(12, customer.getAge());
        }).parseInput(inputStream);
    }

    @Test
    void mapToContacts() {
        String input = "Jan,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new CustomerCsvLoader(customer -> {
            assertEquals(4, customer.getContacts().size());
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("123123123")),
                    hasProperty("type", anything())
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("654 765 765")),
                    hasProperty("type", anything())
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("kowalski@gmail.com")),
                    hasProperty("type", anything())
            )));
            assertThat(customer.getContacts(), hasItem(allOf(
                    hasProperty("contact", equalTo("jan@gmail.com")),
                    hasProperty("type", anything())
            )));
        }).parseInput(inputStream);
    }

    @Test
    void mapToDataRecord_InvalidFormat_CallErrorHandler() {
        String input = "Invalid format";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        CustomerCsvLoader customerCsvLoader = new CustomerCsvLoader((customer) -> fail());
        RecordErrorHandler errorHandler = mock(RecordErrorHandler.class);
        customerCsvLoader.setErrorHandler(errorHandler);
        customerCsvLoader.parseInput(inputStream);
        verify(errorHandler, times(1)).handleError(eq(1L), any(ParsingException.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void mapToDataRecord_InvalidFormat_SkipAndContinue() {
        String input = "First,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com\n" +
                "Second -> Missing required values\n" +
                "Third,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        RecordLoadListener<Customer> loadListener = (RecordLoadListener<Customer>) mock(RecordLoadListener.class);
        new CustomerCsvLoader(loadListener).parseInput(inputStream);
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