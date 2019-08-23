package io.github.t3rmian.contacts.loader.csv;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.github.t3rmian.contacts.loader.ContactMapper;
import io.github.t3rmian.contacts.loader.DataLoadListener;
import io.github.t3rmian.contacts.loader.Loader;
import io.github.t3rmian.contacts.model.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CsvLoader implements Loader<CsvCustomerContacts, Customer> {

    private final ContactMapper contactMapper = new ContactMapper();
    private final DataLoadListener<Customer> loadListener;

    public CsvLoader(DataLoadListener<Customer> loadListener) {
        this.loadListener = loadListener;
    }

    public void parseInput(InputStream inputStream) {
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream)))) {
            CsvToBean<CsvCustomerContacts> csvConverter = new CsvToBeanBuilder<CsvCustomerContacts>(csvReader)
                    .withType(CsvCustomerContacts.class)
                    .build();
            for (CsvCustomerContacts customerContacts : csvConverter) {
                Customer customer = mapToDataRecord(customerContacts);
                loadDataRecord(customer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer mapToDataRecord(CsvCustomerContacts input) {
        Customer output = new Customer();
        output.setName(input.getName());
        output.setAge(input.getAge());
        output.setSurname(input.getSurname());
        if (input.getContacts() != null) {
            output.setContacts(input.getContacts()
                    .values()
                    .stream()
                    .map(contactMapper::mapToContact)
                    .collect(Collectors.toList())
            );
        }
        return output;
    }

    @Override
    public void loadDataRecord(Customer output) {
        loadListener.onDataRecordRead(output);
    }

}
