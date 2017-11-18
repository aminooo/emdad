package com.upsym.emdad.earthquake.main.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Karo on 11/16/2017.
 */

public class Item implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private String status;

    public Item(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public Item(double lat, double lng, String title, String snippet) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
    }

    public Item(double lat, double lng, String mTitle, String mSnippet, String status) {
        mPosition = new LatLng(lat, lng);
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
        this.status = status;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public String getStatus() {
        return status;
    }
}