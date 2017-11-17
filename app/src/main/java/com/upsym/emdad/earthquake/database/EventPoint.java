package com.upsym.emdad.earthquake.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by Karo on 11/16/2017.
 */

@Entity(tableName = "point_event_tbl")
public class EventPoint {
    @PrimaryKey
    @ColumnInfo(name = "u_id")
    private long id;

    @SerializedName("latitude")
    @ColumnInfo(name = "lat")
    private double lat;

    @SerializedName("longitude")
    @ColumnInfo(name = "lng")
    private double lng;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "send_to_server")
    private String sendToServer;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_time")
    private long createdTime;

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_time")
    private long updatedTime;

    @ColumnInfo(name = "count")
    private int count;

    @SerializedName("content_per_package")
    @ColumnInfo(name = "cpp")
    private String contentPerPackage;

    @SerializedName("package_id")
    @ColumnInfo(name = "package_id")
    private long packageId;


    public EventPoint(long minId) {
        id = minId - 1;
        if(id >= 0){
            id = -1;
        }
    }

    public EventPoint() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendToServer() {
        return sendToServer;
    }

    public void setSendToServer(String sendToServer) {
        this.sendToServer = sendToServer;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContentPerPackage() {
        return contentPerPackage;
    }

    public void setContentPerPackage(String contentPerPackage) {
        this.contentPerPackage = contentPerPackage;
    }

    public void setPackageId(long packageId) {
        this.packageId = packageId;
    }

    public long getPackageId() {
        return packageId;
    }
}
