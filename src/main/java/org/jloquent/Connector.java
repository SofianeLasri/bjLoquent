/*
 * The MIT License
 *
 * Copyright 2018 Derick Felix.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jloquent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author derickfelix
 * @date Feb 24, 2018
 */
public class Connector {

    private static Connector connector;
    private Connection connection;
    private DBConfig config;

    private String jdbc_driver;
    private String type;

    public Connection open() {
        try {
            Class.forName(jdbc_driver);
            String url = type + config.getHostName() + ":" + config.getPortNumber() + "/" + config.getDatabaseName();
            connection = DriverManager.getConnection(url, config.getUsername(), config.getPassword());

        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to open connection", e);
        }

        return connection;
    }

    /**
     * Execute a statement and return the generated id
     * @param sql
     * @return Object id
     */
    public Object execute(String sql) {
        open();
        Object id = null;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                id = generatedKeys.getObject(1);
            }

        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to execute statement", e);
        }
        close();
        return id;
    }

    public ResultSet executeQuery(String sql) {
        open();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            return resultSet;
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to execute query", e);
        }
        close();

        return null;
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, "Failed to close connection", e);
        }
    }

    public Object getResult(ResultSet rs, String type, String column) throws SQLException {
        switch (type.toLowerCase()) {
            case "int":
            case "integer":
                return rs.getInt(column);
            case "double":
                return rs.getDouble(column);
            case "boolean":
                return rs.getBoolean(column);
            case "char":
            case "character":
                return rs.getString(column).charAt(0);
            case "array":
                return rs.getArray(column);
            case "string":
                return rs.getString(column);
        }
        return null;
    }

    public void setDBConfig(DBConfig config) {
        this.config = config;
        setDatabaseType();
    }

    public DBConfig getDBConfig() {
        return config;
    }

    private void setDatabaseType() {
        switch (config.getDatabaseType()) {
            case MYSQL:
                jdbc_driver = "com.mysql.cj.jdbc.Driver";
                type = "jdbc:mysql://";
                break;
            case POSTGRES:
                jdbc_driver = "org.postgresql.Driver";
                type = "jdbc:postgresql://";
                break;
            case MARIADB:
                jdbc_driver = "org.mariadb.jdbc.Driver";
                type = "jdbc:mariadb://";
                break;
            default:
                System.err.println("An error have occurred");
        }
    }

    public final static Connector getInstance() {
        if (connector == null) {
            connector = new Connector();
        }

        return connector;
    }
}
