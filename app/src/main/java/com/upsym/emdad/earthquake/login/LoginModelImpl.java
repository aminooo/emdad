package com.upsym.emdad.earthquake.login;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.orhanobut.logger.Logger;
import com.upsym.emdad.earthquake.ServerUtil;
import com.upsym.emdad.earthquake.database.DatabaseUtil;
import com.upsym.emdad.earthquake.database.Person;
import com.upsym.emdad.earthquake.login.events.StepOneErrorEvent;
import com.upsym.emdad.earthquake.login.events.StepOneOkEvent;
import com.upsym.emdad.earthquake.login.events.StepTwoErrorEvent;
import com.upsym.emdad.earthquake.login.events.StepTwoOkEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Karo on 11/16/2017.
 */

public class LoginModelImpl implements LoginModel {
    DatabaseUtil database;
    ServerUtil server;

    public LoginModelImpl(DatabaseUtil database, ServerUtil serverUtil) {
        this.database = database;
        this.server = serverUtil;

    }

    @Override
    public void stepOne(String mobile, final String emdad) {
        server.loginStepOne(mobile, emdad, new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (result != null) {
                    Logger.json(result.toString());
                    if(result.has("code") && result.get("code").getAsInt() == 200){
                        Person person = new Person();
                        person.setToken(result.get("token").getAsString());
                        person.setEmdadCode(emdad);
                        database.getPersonDao().insert(person);
                        EventBus.getDefault().postSticky(new StepOneOkEvent());
                    } else {
                        EventBus.getDefault().postSticky(new StepOneErrorEvent());
                    }
                } else {
                    Logger.e(e.toString());
                    EventBus.getDefault().postSticky(new StepOneErrorEvent());
                }
            }
        });
    }

    @Override
    public void stepTwo(String code) {
        server.loginStepTwo(code, database.getPersonDao().getPerson().getToken(), new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (result != null) {
                    Logger.json(result.toString());
                    if(result.has("code") && result.get("code").getAsInt() == 200){
                        Person person = database.getPersonDao().getPerson();
                        person.setLogin(true);
                        database.getPersonDao().insert(person);
                        EventBus.getDefault().postSticky(new StepTwoOkEvent());
                    } else {
                        EventBus.getDefault().postSticky(new StepTwoErrorEvent());
                    }
                } else {
                    EventBus.getDefault().postSticky(new StepTwoErrorEvent());
                }
            }
        });
    }
}
