package com.upsym.emdad.earthquake.main;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;
import com.upsym.emdad.earthquake.database.EventPoint;
import com.upsym.emdad.earthquake.database.ValidPackage;

import java.util.List;

/**
 * Created by Karo on 11/16/2017.
 */

public interface MainPresenter {
    void onCenterClick();

    void saveNewEvent(LatLng center, String title, String address, int count, int id);

    void onMapReady();

    LiveData<List<EventPoint>> getMarkerData();

    EventPoint getEventPoint(Long aLong);

    List<ValidPackage> getValidPackages();

    void onRefreshClick();

}
