package com.upsym.emdad.earthquake.main;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.maps.model.LatLng;
import com.upsym.emdad.earthquake.ServerUtil;
import com.upsym.emdad.earthquake.database.DatabaseUtil;
import com.upsym.emdad.earthquake.database.EventPoint;
import com.upsym.emdad.earthquake.database.ValidPackage;

import java.util.List;

/**
 * Created by Karo on 11/16/2017.
 */

class MainPresenterImpl implements MainPresenter {
    private MainModel model;
    private MainView view;

    MainPresenterImpl(MainActivity activity) {
        model = new MainModelImpl(DatabaseUtil.getDatabase(activity), new ServerUtil(activity), GcmNetworkManager.getInstance(activity));
        view = activity;
    }


    @Override
    public void onCenterClick() {
        view.openNewEventDialog();
    }

    @Override
    public void saveNewEvent(LatLng center, String title, String address, int count, int id) {
        model.saveNewEvent(center, title, address, count, id);
    }


    @Override
    public void onMapReady() {
//        view.reloadMap(model.getMarkerData());
    }

    @Override
    public LiveData<List<EventPoint>> getMarkerData() {
        return model.getMarkerData();
    }

    @Override
    public EventPoint getEventPoint(Long aLong) {
        return model.getEventPoint(aLong);
    }

    @Override
    public List<ValidPackage> getValidPackages() {
        return model.getValidPackages();
    }
}
