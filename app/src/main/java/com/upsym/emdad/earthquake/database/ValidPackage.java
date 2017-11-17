package com.upsym.emdad.earthquake.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Karo on 11/16/2017.
 */

@Entity(tableName = "valid_package")
public class ValidPackage {

    @PrimaryKey @ColumnInfo(name = "u_id")
    long id;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "cpp")
    @SerializedName("content_per_package")
    String contentPerPackage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentPerPackage() {
        return contentPerPackage;
    }

    public void setContentPerPackage(String contentPerPackage) {
        this.contentPerPackage = contentPerPackage;
    }
}
