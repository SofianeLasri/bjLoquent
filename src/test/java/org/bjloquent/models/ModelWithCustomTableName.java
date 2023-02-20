package org.bjloquent.models;

import org.bjloquent.Model;

import java.sql.Timestamp;

public class ModelWithCustomTableName extends Model {
    private int id;
    private String name;
    private String email;
    private String password;
    private Timestamp joinedDate;

    public ModelWithCustomTableName() {
        super.tableName = "custom_table_name";
    }

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
