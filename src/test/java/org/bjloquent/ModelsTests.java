package org.bjloquent;

import org.bjloquent.models.ModelWithCustomTableName;
import org.bjloquent.models.Player;
import org.bjloquent.models.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ModelsTests {
    private final DBConfig dbConfig = new DBConfig() {
        @Override
        public DatabaseType getDatabaseType() {
            return DatabaseType.MARIADB;
        }

        @Override
        public String getHostName() {
            return "localhost";
        }

        @Override
        public String getPortNumber() {
            return "3306";
        }

        @Override
        public String getDatabaseName() {
            return "jloquent";
        }

        @Override
        public String getUsername() {
            return "jLoquentUser";
        }

        @Override
        public String getPassword() {
            return "secret";
        }
    };

    @org.junit.jupiter.api.Test
    public void testConnection() {
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        Connection connection = connector.open();
        assertNotNull(connection);
        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testInsertIdIntegerPrimaryKey() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert a new user
        User user = new User();
        user.setName("Gordon Freeman");
        user.setEmail("gordon.freeman@blackmesa.us");
        user.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        user.setJoinedDate(timestamp);

        user.create();

        // Now we can check if the user was inserted
        String selectUserSql = "SELECT * FROM users WHERE id = " + user.getId();
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            while (resultSet.next()) {
                assertEquals(user.getName(), resultSet.getString("name"));
                assertEquals(user.getEmail(), resultSet.getString("email"));
                assertEquals(user.getPassword(), resultSet.getString("password"));
                assertEquals(user.getJoinedDate(), resultSet.getTimestamp("joinedDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testInsertIdStringPrimaryKey() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(255) NOT NULL, name VARCHAR(255), joinedDate TIMESTAMP, score INT, PRIMARY KEY (uuid))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert a new user
        Player player = new Player();
        StringBuilder uuid = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                uuid.append(new java.util.Random().nextInt(10));
            }
            if (i < 3) {
                uuid.append("-");
            }
        }
        player.setUuid(uuid.toString());
        player.setName("Gordon Freeman");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        player.setJoinedDate(timestamp);

        player.setScore(100);

        player.create();

        // Now we can check if the user was inserted
        String selectUserSql = "SELECT * FROM players WHERE uuid = '" + player.getUuid() + "'";
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            while (resultSet.next()) {
                assertEquals(player.getUuid(), resultSet.getString("uuid"));
                assertEquals(player.getName(), resultSet.getString("name"));
                assertEquals(player.getJoinedDate(), resultSet.getTimestamp("joinedDate"));
                assertEquals(player.getScore(), resultSet.getInt("score"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE players";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testUpdateIdIntegerPrimaryKey() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert a new user
        User user = new User();
        user.setName("Gordon Freeman");
        user.setEmail("gordon.freeman@blackmesa.us");
        user.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        user.setJoinedDate(timestamp);

        user.create();

        // Now we can update the user
        user.setName("Alyx Vance");
        user.setEmail("alyx.vance@blackmesa.us");
        user.setPassword("654321");
        user.save();

        // Now we can check if the user was inserted
        String selectUserSql = "SELECT * FROM users WHERE id = " + user.getId();
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            while (resultSet.next()) {
                assertEquals(user.getName(), resultSet.getString("name"));
                assertEquals(user.getEmail(), resultSet.getString("email"));
                assertEquals(user.getPassword(), resultSet.getString("password"));
                assertEquals(user.getJoinedDate(), resultSet.getTimestamp("joinedDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testUpdateIdStringPrimaryKey() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS players (uuid VARCHAR(255) NOT NULL, name VARCHAR(255), joinedDate TIMESTAMP, score INT, PRIMARY KEY (uuid))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert a new user
        Player player = new Player();
        StringBuilder uuid = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                uuid.append(new java.util.Random().nextInt(10));
            }
            if (i < 3) {
                uuid.append("-");
            }
        }
        player.setUuid(uuid.toString());
        player.setName("Gordon Freeman");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        player.setJoinedDate(timestamp);

        player.setScore(100);

        player.create();

        // Now we can update the user
        player.setName("Alyx Vance");
        player.setScore(200);
        player.save();

        // Now we can check if the user was inserted
        String selectUserSql = "SELECT * FROM players WHERE uuid = '" + player.getUuid() + "'";
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            while (resultSet.next()) {
                assertEquals(player.getUuid(), resultSet.getString("uuid"));
                assertEquals(player.getName(), resultSet.getString("name"));
                assertEquals(player.getJoinedDate(), resultSet.getTimestamp("joinedDate"));
                assertEquals(player.getScore(), resultSet.getInt("score"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE players";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testDeleteIdIntegerPrimaryKey() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert a new user
        User user = new User();
        user.setName("Gordon Freeman");
        user.setEmail("gordon.freeman@blackmesa.us");
        user.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        user.setJoinedDate(timestamp);

        user.create();

        // Now we can delete the user
        user.delete();

        // Now we can check if the user was deleted
        String selectUserSql = "SELECT * FROM users WHERE id = " + user.getId();
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            assertFalse(resultSet.next());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    /**
     * This test will create a table with a custom name, and regroup insert and update tests.
     */
    @org.junit.jupiter.api.Test
    public void testModelWithCustomTableName() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createCustomTableName = "CREATE TABLE IF NOT EXISTS custom_table_name (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createCustomTableName);

        // Now we can insert a new user
        ModelWithCustomTableName user = new ModelWithCustomTableName();
        user.setName("Gordon Freeman");
        user.setEmail("gordon.freeman@blackmesa.us");
        user.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        user.setJoinedDate(timestamp);

        user.create();

        // Now we can update the user
        user.setName("Alyx Vance");
        user.setEmail("alyx.vance@blackmesa.us");
        user.setPassword("654321");
        user.save();

        // Now we can check if the user was inserted
        String selectUserSql = "SELECT * FROM custom_table_name WHERE id = " + user.getId();
        ResultSet resultSet = connector.executeQuery(selectUserSql);

        assertNotNull(resultSet);
        try {
            while (resultSet.next()) {
                assertEquals(user.getName(), resultSet.getString("name"));
                assertEquals(user.getEmail(), resultSet.getString("email"));
                assertEquals(user.getPassword(), resultSet.getString("password"));
                assertEquals(user.getJoinedDate(), resultSet.getTimestamp("joinedDate"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE custom_table_name";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testCreateAndFindModelObject() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert two new user
        User firstUser = new User();
        firstUser.setName("Gordon Freeman");
        firstUser.setEmail("gordon.freeman@blackmesa.us");
        firstUser.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        firstUser.setJoinedDate(timestamp);

        firstUser.create();

        User secondUser = new User();
        secondUser.setName("Alyx Vance");
        secondUser.setEmail("alyx.vance@blackmesa.us");
        secondUser.setPassword("654321");
        secondUser.setJoinedDate(timestamp);

        secondUser.create();

        // Now we can check if the user was inserted
        User firstUserFound = new User();
        firstUserFound.find(firstUser.getId());

        assertEquals(firstUser.getId(), firstUserFound.getId());
        assertEquals(firstUser.getName(), firstUserFound.getName());
        assertEquals(firstUser.getEmail(), firstUserFound.getEmail());
        assertEquals(firstUser.getPassword(), firstUserFound.getPassword());
        assertEquals(firstUser.getJoinedDate(), firstUserFound.getJoinedDate());

        User secondUserFound = new User();
        secondUserFound.find(secondUser.getId());

        assertEquals(secondUser.getId(), secondUserFound.getId());
        assertEquals(secondUser.getName(), secondUserFound.getName());
        assertEquals(secondUser.getEmail(), secondUserFound.getEmail());
        assertEquals(secondUser.getPassword(), secondUserFound.getPassword());
        assertEquals(secondUser.getJoinedDate(), secondUserFound.getJoinedDate());

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testWhereSingleCondition() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert two new user
        User firstUser = new User();
        firstUser.setName("Gordon Freeman");
        firstUser.setEmail("gordon.freeman@blackmesa.us");
        firstUser.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        firstUser.setJoinedDate(timestamp);

        firstUser.create();

        User secondUser = new User();
        secondUser.setName("Alyx Vance");
        secondUser.setEmail("alyx.vance@blackmesa.us");
        secondUser.setPassword("654321");
        secondUser.setJoinedDate(timestamp);

        secondUser.create();

        // Now we can check if the user was inserted
        User dummyUserInstance = new User();
        List<Model> users = dummyUserInstance.where("name", "Gordon Freeman");

        assertEquals(1, users.size());

        User firstUserFound = (User) users.get(0);
        assertEquals(firstUser.getId(), firstUserFound.getId());
        assertEquals(firstUser.getName(), firstUserFound.getName());

        // Now we want to find all the users that have "Vance" in their name
        dummyUserInstance = new User();
        users = dummyUserInstance.where("name", "LIKE", "%Vance%");
        assertEquals(1, users.size());

        User secondUserFound = (User) users.get(0);
        assertEquals(secondUser.getId(), secondUserFound.getId());
        assertEquals(secondUser.getName(), secondUserFound.getName());

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }

    @org.junit.jupiter.api.Test
    public void testWhereMultipleConditions() {
        // bjLoquent don't have yet a way to create a table
        // so we need to create it manually
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, name VARCHAR(255), email VARCHAR(255), password VARCHAR(255), joinedDate TIMESTAMP, PRIMARY KEY (id))";
        Connector connector = Connector.getInstance();
        connector.setDBConfig(dbConfig);
        connector.execute(createUserTableSql);

        // Now we can insert two new user
        User firstUser = new User();
        firstUser.setName("Gordon Freeman");
        firstUser.setEmail("gordon.freeman@blackmesa.us");
        firstUser.setPassword("123456");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // We need to round the timestamp to seconds because it seems that the milliseconds are not supported
        timestamp.setNanos(0);
        firstUser.setJoinedDate(timestamp);

        firstUser.create();

        User secondUser = new User();
        secondUser.setName("Alyx Vance");
        secondUser.setEmail("alyx.vance@blackmesa.us");
        secondUser.setPassword("654321");
        secondUser.setJoinedDate(timestamp);

        secondUser.create();

        User thirdUser = new User();
        thirdUser.setName("Eli Vance");
        thirdUser.setEmail("eli.vance@blackmesa.us");
        thirdUser.setPassword("hackme");
        thirdUser.setJoinedDate(timestamp);

        thirdUser.create();

        User fourthUser = new User();
        fourthUser.setName("Barney Calhoun");
        fourthUser.setEmail("barney.clahourn@blackmesa.us");
        fourthUser.setPassword("aGoodBeer");
        fourthUser.setJoinedDate(timestamp);

        fourthUser.create();

        // Now we want to find all the users that have "Vance" in their name and that works at Black Mesa
        User dummyUserInstance = new User();
        List<Model> users = dummyUserInstance.multipleWhere(
                new String[]{"name", "email"},
                new String[]{"LIKE", "LIKE"},
                new String[]{"%Vance%", "%blackmesa%"}
        );

        assertEquals(2, users.size());

        User firstUserFound = (User) users.get(0);
        assertEquals(secondUser.getId(), firstUserFound.getId());
        assertEquals(secondUser.getName(), firstUserFound.getName());

        User secondUserFound = (User) users.get(1);
        assertEquals(thirdUser.getId(), secondUserFound.getId());
        assertEquals(thirdUser.getName(), secondUserFound.getName());

        // Finally we can drop the table
        String dropUserTableSql = "DROP TABLE users";
        connector.execute(dropUserTableSql);

        connector.close();
    }
}
