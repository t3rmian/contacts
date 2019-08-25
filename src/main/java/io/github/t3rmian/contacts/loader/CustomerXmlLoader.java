package io.github.t3rmian.contacts.loader;

import io.github.t3rmian.contacts.loader.exception.ParsingException;
import io.github.t3rmian.contacts.data.Contact;
import io.github.t3rmian.contacts.data.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CustomerXmlLoader extends DefaultHandler implements RecordLoader<Customer> {

    private final Logger logger = LoggerFactory.getLogger(CustomerCsvLoader.class);

    private final ContactMapper contactMapper = new ContactMapper();
    private Customer customer;
    private StringBuilder data;
    private long recordCount;

    private boolean parsingCustomer = false;
    private boolean parsingAge = false;
    private boolean parsingName = false;
    private boolean parsingSurname = false;
    private boolean parsingContacts = false;

    private final RecordLoadListener<Customer> loadListener;

    private RecordErrorHandler errorHandler;

    public CustomerXmlLoader(RecordLoadListener<Customer> loadListener) {
        this.loadListener = loadListener;
    }

    @Override
    public void setErrorHandler(RecordErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public void parseInput(InputStream inputStream) {
        recordCount = 0;
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = factory.newSchema(new StreamSource(getClass().getClassLoader()
                    .getResourceAsStream("customer_contacts.xsd")));
            parserFactory.setSchema(schema);
            SAXParser parser = parserFactory.newSAXParser();
            logger.info("Starting input parsing");
            parser.parse(inputStream, this);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
        loadListener.onFinishRead();
        logger.info(String.format("Finished parsing %d records", recordCount));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equalsIgnoreCase("person")) {
            recordCount++;
            customer = new Customer();
            parsingCustomer = true;
        } else if (parsingCustomer) {
            if (qName.equalsIgnoreCase("name")) {
                parsingName = true;
            } else if (qName.equalsIgnoreCase("surname")) {
                parsingSurname = true;
            } else if (qName.equalsIgnoreCase("age")) {
                parsingAge = true;
            } else if (qName.equalsIgnoreCase("contacts")) {
                parsingContacts = true;
                customer.setContacts(new ArrayList<>());
            }
        }
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (parsingCustomer) {
            if (qName.equalsIgnoreCase("person")) {
                fireRecordLoad(customer);
                parsingCustomer = false;
            } else if (qName.equalsIgnoreCase("contacts")) {
                parsingContacts = false;
            } else if (parsingName) {
                customer.setName(data.toString());
                parsingName = false;
            } else if (parsingSurname) {
                customer.setSurname(data.toString());
                parsingSurname = false;
            } else if (parsingAge) {
                customer.setAge(Integer.parseInt(data.toString()));
                parsingAge = false;
            } else if (parsingContacts) {
                Contact contact = contactMapper.mapToContact(qName, data.toString());
                customer.getContacts().add(contact);
            }

        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        data.append(new String(ch, start, length));
    }

    @Override
    public void fireRecordLoad(Customer output) {
        loadListener.onRecordRead(output);
    }

    @Override
    public void error(SAXParseException e) {
        if (errorHandler != null) {
            errorHandler.handleError(recordCount, new ParsingException(e));
        }
        parsingCustomer = false;
    }
}
