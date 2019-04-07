package com.google.cloud.android.speech;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.cloud.android.speech.data.SessionHandler;
import com.google.cloud.android.speech.models.User;

public class splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        int secondsDelayed = 2;
        final SessionHandler session = new SessionHandler(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(session.isLoggedIn())
                {
                 /* si está authenticado ir al launcer */
                    Intent launcher = new Intent(getApplicationContext(),menu_main_activity.class);
                    startActivity(launcher);
                    finish();

                }else{
                    Intent login = new Intent(getApplicationContext(),login_activity.class);
                    startActivity(login);
                    finish();
                }


            }
        },secondsDelayed*1000);

    }
}
