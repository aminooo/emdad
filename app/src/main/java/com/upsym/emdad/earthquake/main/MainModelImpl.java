package com.upsym.emdad.earthquake.main;

import android.arch.lifecycle.LiveData;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.orhanobut.logger.Logger;
import com.upsym.emdad.earthquake.GCMService;
import com.upsym.emdad.earthquake.ServerUtil;
import com.upsym.emdad.earthquake.database.DatabaseUtil;
import com.upsym.emdad.earthquake.database.EventPoint;
import com.upsym.emdad.earthquake.database.ValidPackage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * Created by Karo on 11/16/2017.
 */

class MainModelImpl implements MainModel{
    private DatabaseUtil database;
    private ServerUtil server;
    private GcmNetworkManager mGcmNetworkManager;

    ArrayList<ValidPackage> packages;

    MainModelImpl(DatabaseUtil database, ServerUtil serverUtil, GcmNetworkManager mGcmNetworkManager) {
        this.database = database;
        this.server = serverUtil;
        this.mGcmNetworkManager = mGcmNetworkManager;
        requestedDataFromServer();
    }

    @Override
    public void saveNewEvent(LatLng center, String title, String address, int count, int packageId) {
        final EventPoint eventPoint = new EventPoint(database.getEventPointDao().getMinId());
        eventPoint.setAddress(address);
        eventPoint.setCreatedTime(Calendar.getInstance().getTimeInMillis());
        eventPoint.setUpdatedTime(Calendar.getInstance().getTimeInMillis());
        eventPoint.setTitle(title);
        eventPoint.setLat(center.latitude);
        eventPoint.setLng(center.longitude);
        eventPoint.setContentPerPackage(database.getValidPackageDao().getAll().get(packageId).getContentPerPackage());
        eventPoint.setCount(count);
        eventPoint.setPackageId(database.getValidPackageDao().getAll().get(packageId).getId());
        database.getEventPointDao().insert(eventPoint);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("lat", center.latitude);
        jsonObject.addProperty("long", center.longitude);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("address", address);
        jsonObject.addProperty("count", count);
        jsonObject.addProperty("package_id", database.getValidPackageDao().getAll().get(packageId).getId());
        jsonObject.addProperty("deliver_to", "karo");

        JsonArray array = new JsonArray();
        array.add(jsonObject);
        server.sendNewHelpRequest(database.getPersonDao().getPerson().getToken(), array, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if(result != null){
                    Logger.json(result.toString());
                    if(result.has("code") && result.get("code").getAsInt() == 200) {
                        requestedDataFromServer(eventPoint);
                        return;
                    }
                } else {
                    Logger.e(e.toString());
                }
                Task task = new OneoffTask.Builder()
                        .setService(GCMService.class)
                        .setExecutionWindow(0, 30)
                        .setTag("send_data_to_server")
                        .setUpdateCurrent(false)
                        .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                        .setRequiresCharging(false)
                        .build();
                mGcmNetworkManager.schedule(task);
            }
        });
    }


    @Override
    public void requestedDataFromServer() {
        requestedDataFromServer(null);
    }

    private void requestedDataFromServer(final EventPoint eventPoint) {
        server.requestedDataFromServer(database.getPersonDao().getPerson().getToken(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if(eventPoint != null){
                    database.getEventPointDao().delete(eventPoint);
                }
                if(result != null){
                    Logger.json(result.toString());
                    if(result.has("code") && result.get("code").getAsInt() == 200){
                        packages = new Gson().fromJson(result.get("package_type"), new TypeToken<ArrayList<ValidPackage>>(){}.getType());
                        database.getValidPackageDao().insertAll(packages);

                        List<EventPoint> requests = new Gson()
                                .fromJson(result.get("requested"), new TypeToken<List<EventPoint>>(){}.getType());
                        database.getEventPointDao().insertAll(requests);
                        return;
                    }
                } else {
                    Logger.e(e.toString());
                }

            }
        });
    }

    @Override
    public LiveData<List<EventPoint>> getMarkerData() {
        return database.getEventPointDao().getAllLiveData();
    }

    @Override
    public EventPoint getEventPoint(Long aLong) {
        return database.getEventPointDao().getEvent(aLong);
    }

    @Override
    public List<ValidPackage> getValidPackages() {
        return database.getValidPackageDao().getAll();
    }

}
