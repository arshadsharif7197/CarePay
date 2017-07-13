package com.carecloud.carepay.mini.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;

/**
 * Created by lmenendez on 6/20/17
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_splash);

        final boolean isDeviceRegistered = ((ApplicationHelper) getApplication()).getApplicationPreferences().isDeviceRegistered();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class intentClass;
                if(!isDeviceRegistered){
                    intentClass = RegistrationActivity.class;
                }else{
                    intentClass = WelcomeActivity.class;
                }

                Intent main = new Intent(SplashActivity.this, intentClass);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            }
        }, 1000);
    }
}
