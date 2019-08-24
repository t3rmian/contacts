package io.github.t3rmian.contacts.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class AbstractDAO {

    private static final Properties properties;
    private static final String DATABASE_URL_KEY = "io.github.t3rmian.contacts.dao.url";
    private static final String DATABASE_USER_KEY = "io.github.t3rmian.contacts.dao.user";
    private static final String DATABASE_PASSWORD = "io.github.t3rmian.contacts.dao.password";

    static {
        try (InputStream inputStream = AbstractDAO.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty(DATABASE_URL_KEY),
                properties.getProperty(DATABASE_USER_KEY),
                properties.getProperty(DATABASE_PASSWORD)
        );
    }
}
