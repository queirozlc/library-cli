package com.faesa.librarycli.shared.infra.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public final class DatabaseProvider {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            var properties = new Properties();

            // load application.properties file
            try (var fileInputStream = new FileInputStream("src/main/resources/env.properties")) {
                properties.load(fileInputStream);

                // load the database driver
                Class.forName(properties.getProperty("DATABASE_DRIVER"));

                // get the connection
                connection = java.sql.DriverManager.getConnection(
                        properties.getProperty("DATABASE_URL"),
                        properties.getProperty("DATABASE_USERNAME"),
                        properties.getProperty("DATABASE_PASSWORD")
                );

                return connection;
            } catch (SQLException | ClassNotFoundException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
