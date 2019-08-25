package io.github.t3rmian.contacts;

import io.github.t3rmian.contacts.dao.AbstractDao;
import io.github.t3rmian.contacts.dao.CustomerBatchManager;
import io.github.t3rmian.contacts.loader.ErrorHandler;
import io.github.t3rmian.contacts.loader.LoadListener;
import io.github.t3rmian.contacts.loader.Loader;
import io.github.t3rmian.contacts.loader.CustomerCsvLoader;
import io.github.t3rmian.contacts.loader.exception.ApplicationException;
import io.github.t3rmian.contacts.loader.CustomerXmlLoader;
import io.github.t3rmian.contacts.model.Customer;
import org.apache.commons.cli.*;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        Option inputOption = Option.builder("i")
                .required(true)
                .desc("Input file")
                .longOpt("input")
                .build();
        Option typeOption = Option.builder("t")
                .required(true)
                .desc("File extension type")
                .longOpt("type")
                .build();
        Option errorOption = Option.builder("e")
                .required(false)
                .desc("Error output")
                .longOpt("error")
                .build();
        Option batchSizeOption = Option.builder("b")
                .required(false)
                .desc("Batch size")
                .longOpt("size")
                .build();
        Option dataSourceUrl = Option.builder("-D" + AbstractDao.DATABASE_URL_KEY)
                .required(false)
                .desc("Database url")
                .build();
        Option dataSourceUser = Option.builder("-D" + AbstractDao.DATABASE_USER_KEY)
                .required(false)
                .desc("Database user")
                .build();
        Option dataSourcePassword = Option.builder("-D" + AbstractDao.DATABASE_PASSWORD_KEY)
                .required(false)
                .desc("Database password")
                .build();
        Options options = new Options();
        options.addOption(inputOption);
        options.addOption(typeOption);
        options.addOption(errorOption);
        options.addOption(batchSizeOption);
        options.addOption(dataSourceUrl);
        options.addOption(dataSourceUser);
        options.addOption(dataSourcePassword);
        CommandLineParser parser = new DefaultParser();
        try {
            parser.parse(options, args);
        } catch (ParseException e) {
            displayUsage(options);
            return;
        }

        invoke(options);
    }

    private static void invoke(Options options) throws IOException {
        Option typeOption = options.getOption("type");
        Option sizeOption = options.getOption("size");
        Option inputOption = options.getOption("input");
        ErrorHandler errorHandler = new ErrorHandler() {
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
        if (sizeOption.getValue() != null) {
            try {
                batchSize = Integer.parseInt(sizeOption.getValue());
            } catch (NumberFormatException e) {
                displayUsage(options);
                return;
            }
        }
        LoadListener<Customer> loadListener = new CustomerBatchManager(errorHandler, batchSize);
        Loader<Customer> loader;
        if ("csv".equalsIgnoreCase(typeOption.getValue())) {
            loader = new CustomerCsvLoader(loadListener);
        } else if ("xml".equalsIgnoreCase(typeOption.getValue())) {
            loader = new CustomerXmlLoader(loadListener);
        } else {
            displayUsage(options);
            return;
        }
        try (FileInputStream input = new FileInputStream(inputOption.getValue())) {
            loader.parseInput(input);
        }
    }

    private static void displayUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java --jar contacts-loader.jar", options);
    }
}
