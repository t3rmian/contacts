package io.github.t3rmian.contacts.loader.xml;

import io.github.t3rmian.contacts.loader.DataLoadListener;
import io.github.t3rmian.contacts.loader.Loader;
import io.github.t3rmian.contacts.model.Customer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlLoader extends DefaultHandler implements Loader<Customer, Customer> {
    private Customer customer = null;
    private StringBuilder data = null;

    boolean bAge = false;
    boolean bName = false;
    boolean bSurname = false;

    private final DataLoadListener<Customer> loadListener;

    public XmlLoader(DataLoadListener<Customer> loadListener) {
        this.loadListener = loadListener;
    }

    @Override
    public void parseInput(InputStream inputStream) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(inputStream, this);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("person")) {
            customer = new Customer();
        } else if (qName.equalsIgnoreCase("name")) {
            bName = true;
        } else if (qName.equalsIgnoreCase("surname")) {
            bSurname = true;
        } else if (qName.equalsIgnoreCase("age")) {
            bAge = true;
        }
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (bName) {
            customer.setName(data.toString());
            bName = false;
        } else if (bSurname) {
            customer.setSurname(data.toString());
            bSurname = false;
        } else if (bAge) {
            customer.setAge(Integer.parseInt(data.toString()));
            bAge = false;
        }

        if (qName.equalsIgnoreCase("person")) {
            loadDataRecord(customer);
            customer = null;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        data.append(new String(ch, start, length));
    }

    @Override
    public Customer mapToDataRecord(Customer input) {
        return null;
    }

    @Override
    public void loadDataRecord(Customer output) {
        loadListener.onDataRecordRead(output);
    }

}
