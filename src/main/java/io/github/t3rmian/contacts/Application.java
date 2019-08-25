package io.github.t3rmian.contacts;

import io.github.t3rmian.contacts.dao.AbstractDao;
import io.github.t3rmian.contacts.dao.CustomerBatchManager;
import io.github.t3rmian.contacts.dao.CustomerDao;
import io.github.t3rmian.contacts.data.Customer;
import io.github.t3rmian.contacts.loader.*;
import io.github.t3rmian.contacts.loader.exception.ApplicationException;
import org.apache.commons.cli.*;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Application {

    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws IOException {
        Option inputOption = Option.builder("i")
                .required(true)
                .desc("Input file (required)")
                .longOpt("input")
                .hasArg()
                .build();
        Option typeOption = Option.builder("t")
                .required(true)
                .desc("File extension type (required)")
                .longOpt("type")
                .hasArg()
                .build();
        Option errorOption = Option.builder("e")
                .required(false)
                .desc("Error output (not implemented)")
                .longOpt("error")
                .build();
        Option batchSizeOption = Option.builder("b")
                .required(false)
                .desc("Batch size")
                .longOpt("size")
                .hasArg()
                .build();
        Options options = new Options();
        options.addOption(inputOption);
        options.addOption(typeOption);
        options.addOption(errorOption);
        options.addOption(batchSizeOption);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            invoke(cmd);
        } catch (ParseException e) {
            displayUsage(options);
        }
    }

    private static void invoke(CommandLine cmd) throws IOException, ParseException {
        String type = cmd.getOptionValue("type");
        String size = cmd.getOptionValue("size");
        String input = cmd.getOptionValue("input");
        RecordErrorHandler errorHandler = new RecordErrorHandler() {
            @Override
            public void handleError(int from, int to, ApplicationException exception) {
                LoggerFactory.getLogger(Application.class).error(
                        String.format("Could not import records range: <%d,%d>", from, to),
                        exception
                );
            }

            @Override
            public void handleError(long record, ApplicationException exception) {
                LoggerFactory.getLogger(Application.class).error(
                        String.format("Could not import record: %d", record),
                        exception
                );
            }
        };
        int batchSize = 10000;
        if (size != null) {
            try {
                batchSize = Integer.parseInt(size);
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage());
            }
        }
        RecordLoadListener<Customer> loadListener = new CustomerBatchManager(new CustomerDao(), errorHandler, batchSize);
        RecordLoader<Customer> loader;
        if ("csv".equalsIgnoreCase(type)) {
            loader = new CustomerCsvLoader(loadListener);
        } else if ("xml".equalsIgnoreCase(type)) {
            loader = new CustomerXmlLoader(loadListener);
        } else {
            throw new ParseException("Wrong type");
        }
        if (new UrlValidator().isValid(input)) {
            try (InputStream inputStream = new URL(input).openStream()) {
                loader.parseInput(inputStream);
            }
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(input)) {
                loader.parseInput(fileInputStream);
            }
        }
    }

    private static void displayUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar contacts-loader.jar", null, options,
                "To override connection configuration use system properties before -jar: "
                        + "\t-D" + AbstractDao.DATABASE_URL_KEY + "=..."
                        + "\t-D" + AbstractDao.DATABASE_USER_KEY + "=..."
                        + "\t-D" + AbstractDao.DATABASE_PASSWORD_KEY + "=...");
    }
}
