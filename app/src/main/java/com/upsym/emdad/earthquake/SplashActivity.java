package com.upsym.emdad.earthquake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.upsym.emdad.earthquake.database.DatabaseUtil;
import com.upsym.emdad.earthquake.database.Person;
import com.upsym.emdad.earthquake.login.LoginActivity;
import com.upsym.emdad.earthquake.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Logger.addLogAdapter(new AndroidLogAdapter());

        Person person = DatabaseUtil.getDatabase(this).getPersonDao().getPerson();
        if(person == null){
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            if(person.isLogin()){
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        finish();
    }
}
