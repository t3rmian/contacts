package io.github.t3rmian.contacts.loader.csv;

import com.opencsv.bean.CsvBindAndJoinByPosition;
import com.opencsv.bean.CsvBindByPosition;
import org.apache.commons.collections4.MultiValuedMap;

public class CsvCustomerContacts {
    @CsvBindByPosition(position = 0, required = true)
    private String name;
    @CsvBindByPosition(position = 1, required = true)
    private String surname;
    @CsvBindByPosition(position = 2)
    private Integer age;
    @CsvBindByPosition(position = 3, required = true)
    private String city;
    @CsvBindAndJoinByPosition(position = "4-", required = false, elementType = String.class)
    private MultiValuedMap<String, String> contacts;

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getAge() {
        return age;
    }

    public MultiValuedMap<String, String> getContacts() {
        return contacts;
    }
}
