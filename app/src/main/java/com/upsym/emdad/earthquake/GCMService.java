package com.upsym.emdad.earthquake;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orhanobut.logger.Logger;
import com.upsym.emdad.earthquake.database.DatabaseUtil;
import com.upsym.emdad.earthquake.database.EventPoint;
import com.upsym.emdad.earthquake.database.ValidPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karo on 11/17/2017.
 */

public class GCMService extends GcmTaskService {


    @Override
    public int onRunTask(TaskParams taskParams) {
        if("send_data_to_server".equals(taskParams.getTag())){
            final List<EventPoint> data = DatabaseUtil.getDatabase(this).getEventPointDao().getAllLocal();
            if(data == null || data.size() == 0){
                return 0;
            }
            JsonArray array = new JsonArray();
            for(EventPoint event : data){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("lat", event.getLat());
                jsonObject.addProperty("long", event.getLng());
                jsonObject.addProperty("title", event.getTitle());
                jsonObject.addProperty("address", event.getAddress());
                jsonObject.addProperty("count", event.getCount());
                jsonObject.addProperty("package_id", event.getPackageId());
                array.add(jsonObject);
            }
            ServerUtil server = new ServerUtil(getApplicationContext());
            server.sendNewHelpRequest(DatabaseUtil.getDatabase(getApplicationContext()).getPersonDao().getPerson().getToken(), array, new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    if(result != null){
                        Logger.json(result.toString());
                        if(result.has("code") && result.get("code").getAsInt() == 200){
//                            DatabaseUtil.getDatabase(GCMService.this).getEventPointDao().deleteAll(data);
                            requestedDataFromServer(data);
                        }
                    } else {
                        Logger.e(e.toString());
                        if(e instanceof JsonParseException){
                            DatabaseUtil.getDatabase(GCMService.this).getEventPointDao().deleteAll(data);
                        }
                    }
                }
            });

        }
        return 0;
    }

    private void requestedDataFromServer(final List<EventPoint> eventPoint) {
        ServerUtil server = new ServerUtil(getApplicationContext());
        final DatabaseUtil database = DatabaseUtil.getDatabase(getApplicationContext());
        server.requestedDataFromServer(database.getPersonDao().getPerson().getToken(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if(eventPoint != null){
                    database.getEventPointDao().deleteAll(eventPoint);
                }
                if(result != null){
                    Logger.json(result.toString());
                    if(result.has("code") && result.get("code").getAsInt() == 200){
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
}

