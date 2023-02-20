package org.bjloquent.models;

import org.bjloquent.Model;

import java.sql.Timestamp;

public class Player extends Model {
    private String uuid;
    private String name;
    private Timestamp joinedDate;
    private int score;

    public Player() {
        super.primaryKeyName = "uuid";
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
