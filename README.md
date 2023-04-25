<p align="center">
  <img src="art/logo.png" alt="bjLoquent">
</p>

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Better jLoquent
A Java ORM based on [jLoquent](https://github.com/derickfelix/jloquent), inspired by [Laravel Eloquent](https://laravel.com/docs/eloquent).

**License**    
This library is under the [MIT](https://github.com/derickfelix/jloquent/blob/master/LICENSE) license.

## How to use it
bjLoquent is actually not so different of jLoquent. **You should check the JUnit test file to see how it works.** But here is a quick example.

Firstly, in order to use it you have to create a `Configuration` class, just like this.

**Configuration.java**

```java
package com.yourpackage;

import org.bjloquent.DBConfig;
import org.bjloquent.DatabaseType;

public class Configuration implements DBConfig {

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public String getHostName() {
        return "databaseHostname";
    }

    @Override
    public String getPortNumber() {
        return "databasePort";
    }

    @Override
    public String getDatabaseName() {
        return "databaseName";
    }

    @Override
    public String getUsername() {
        return "databaseUsername";
    }

    @Override
    public String getPassword() {
        return "yourPassword";
    }
}
```

Next to that, you should have created some models. I actually don't know how works migrations on jLoquent, and if they works (that is planned for later).
**Every model has to be named in cingular**, bjLoquent will name it in plurial in the database.

To make a model, you have to define a primary key. But if the primary key is an integer called `id`, you don't have to specify it name. Here is an example:

**User.java**

```java
package com.yourpackage;

import org.bjloquent.Model;

import java.sql.Timestamp;

public class User extends Model {
    private int id;
    private String name;
    private String email;
    private String password;
    private Timestamp joinedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }
}
```
Conversely, if your primary key is not called id, you will need to specify it just like in the model below.

**Player.java** (with string UUID)

```java
package com.yourpackage;

import org.bjloquent.Model;

import java.sql.Timestamp;

public class Player extends Model {
    private String uuid;
    private String name;
    private Timestamp joinedDate;
    private int score;

    public Player() {
        // We specify
        super.primaryKey = "uuid";
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
```
And here is an example of Main:

**Main.java**

```java
package com.yourpackage;

import org.bjloquent.Connector;
import org.bjloquent.DBConfig;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Connector connector = Connector.getInstance();
        DBConfig config = new Configuration();
        connector.setDBConfig(config);
        connector.open();

        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("123456");
        user.setJoinedDate(new java.sql.Timestamp(System.currentTimeMillis()));

        user.create();
        System.out.println("ID: " + user.getId());

        user.setName("Modified John Doe");
        user.save();

        Player player = new Player();
        String uuid = "";
        for (int i = 0; i < 4; i++) {
            uuid += String.valueOf((new Random().nextInt(10)));
        }
        player.setUuid(uuid);
        player.setName("John Doe");
        player.setJoinedDate(new java.sql.Timestamp(System.currentTimeMillis()));
        player.setScore(100);

        System.out.println("UUID: " + player.getUuid());

        player.create();

        player.setScore(200);
        player.setName("Modified John Doe");
        player.save();

        connector.close();
    }
}
```