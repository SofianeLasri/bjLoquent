package org.bjloquent.models;

import org.bjloquent.Model;

public class PlayerSetting extends Model {
    private String uuid;
    private String name;
    private String value;

    public PlayerSetting() {
        super.tableName = "players_settings";
        super.primaryKey = new String[]{"uuid", "name"};
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
