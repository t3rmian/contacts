package io.github.t3rmian.contacts.loader.csv;

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

class CsvLoaderTest {

    @Test
    void parseFile() {
        List<Customer> parsedCustomers = new ArrayList<>();
        InputStream file = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("test-data.csv"));
        new CsvLoader(parsedCustomers::add).parseInput(file);
        assertEquals(2, parsedCustomers.size());
    }

    @Test
    void mapToDataRecord() {
        String input = "Jan,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new CsvLoader(customer -> {
            assertEquals("Jan", customer.getName());
            assertEquals("Kowalski", customer.getSurname());
            assertEquals(12, customer.getAge());
        }).parseInput(inputStream);
    }

    @Test
    void mapToContacts() {
        String input = "Jan,Kowalski,12,Lublin,123123123,654 765 765,kowalski@gmail.com,jan@gmail.com";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        new CsvLoader(customer -> {
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
    void loadDataRecord() {
        //TODO: consider removing
    }
}