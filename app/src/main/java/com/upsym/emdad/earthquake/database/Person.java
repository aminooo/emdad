package com.upsym.emdad.earthquake.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 *
 * Created by Karo on 11/16/2017.
 */

@Entity(tableName = "person_tbl")
public class Person {
    @PrimaryKey
    @ColumnInfo(name = "u_id")
    long id = 1;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "family")
    String family;

    @ColumnInfo(name = "emdad_code")
    String emdadCode;

    @ColumnInfo(name = "last_lat")
    double lastLat;

    @ColumnInfo(name = "last_long")
    double lastLong;

    @ColumnInfo(name = "is_loged_in")
    boolean login;

    @ColumnInfo(name = "token")
    String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getEmdadCode() {
        return emdadCode;
    }

    public void setEmdadCode(String emdadCode) {
        this.emdadCode = emdadCode;
    }

    public double getLastLat() {
        return lastLat;
    }

    public void setLastLat(double lastLat) {
        this.lastLat = lastLat;
    }

    public double getLastLong() {
        return lastLong;
    }

    public void setLastLong(double lastLong) {
        this.lastLong = lastLong;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
