package io.github.t3rmian.contacts.loader.csv;

import io.github.t3rmian.contacts.loader.ContactMapper;
import io.github.t3rmian.contacts.loader.LoadListener;
import io.github.t3rmian.contacts.loader.ErrorHandler;
import io.github.t3rmian.contacts.loader.Loader;
import io.github.t3rmian.contacts.loader.exception.ParsingException;
import io.github.t3rmian.contacts.model.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvLoader implements Loader<Customer> {

    private final ContactMapper contactMapper = new ContactMapper();
    private final LoadListener<Customer> loadListener;
    private ErrorHandler errorHandler;

    public CsvLoader(LoadListener<Customer> loadListener) {
        this.loadListener = loadListener;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void parseInput(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            parseReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseReader(BufferedReader reader) throws IOException {
        long lineCount = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            try {
                lineCount++;
                String[] customerRecord = line.split(",");
                Customer customer = mapToDataRecord(customerRecord);
                fireRecordLoad(customer);
            } catch (ParsingException pe) {
                if (errorHandler != null) {
                    errorHandler.handleError(lineCount, pe);
                }
            }
        }
    }

    private Customer mapToDataRecord(String[] input) {
        if (input.length < 4) {
            throw new ParsingException("Customer has missing properties");
        }
        Customer customer = new Customer();
        customer.setName(input[0]);
        customer.setSurname(input[1]);
        if (!input[2].isEmpty()) {
            try {
                customer.setAge(Integer.parseInt(input[2]));
            } catch (NumberFormatException nfe) {
                throw new ParsingException(nfe);
            }
        }
        customer.setContacts(Stream.of(input)
                .skip(4)
                .map(contactMapper::mapToContact)
                .collect(Collectors.toList())
        );
        return customer;
    }

    public void fireRecordLoad(Customer output) {
        loadListener.onRecordRead(output);
    }

}
