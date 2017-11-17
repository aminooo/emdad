package com.upsym.emdad.earthquake;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orhanobut.logger.Logger;

/**
 * Created by Karo on 11/16/2017.
 */

public class ServerUtil {
    Context context;

    public ServerUtil(Context context) {
        this.context = context;
    }

    public void loginStepOne(String mobile, String emdad, FutureCallback<JsonObject> futureCallback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("emdadi_code", emdad);
        jsonObject.addProperty("lat", 0);
        jsonObject.addProperty("long", 0);

        Ion.with(context)
                .load("http://emdad.upsym.com/api/login")
                .setTimeout(120000)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public void loginStepTwo(String code, String token, FutureCallback<JsonObject> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token",token);
        jsonObject.addProperty("confirm_code", code);
        Ion.with(context)
                .load("http://emdad.upsym.com/api/confirm")
                .setTimeout(120000)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(callback);
    }

    public void requestedDataFromServer(String token, FutureCallback<JsonObject> futureCallback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token",token);
        Ion.with(context)
                .load("http://emdad.upsym.com/api/get_requests")
                .setTimeout(120000)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(futureCallback);
    }

    public void sendNewHelpRequest(String token, JsonArray jsonObject, FutureCallback<JsonObject> callback) {
        JsonObject json = new JsonObject();
        json.addProperty("token", token);
        json.add("request", jsonObject);
        Logger.json(json.toString());
        Ion.with(context)
                .load("http://emdad.upsym.com/api/add_request")
                .setTimeout(120000)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(callback);
    }
}
