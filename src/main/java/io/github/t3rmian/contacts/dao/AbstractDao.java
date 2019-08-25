package io.github.t3rmian.contacts.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractDao {

    static {
        try (InputStream inputStream = AbstractDao.class.getClassLoader().getResourceAsStream("data-source.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static final String DATABASE_URL_KEY = "datasource.url";
    public static final String DATABASE_USER_KEY = "datasource.user";
    public static final String DATABASE_PASSWORD_KEY = "datasource.password";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty(DATABASE_URL_KEY),
                System.getProperty(DATABASE_USER_KEY),
                System.getProperty(DATABASE_PASSWORD_KEY)
        );
    }
}
