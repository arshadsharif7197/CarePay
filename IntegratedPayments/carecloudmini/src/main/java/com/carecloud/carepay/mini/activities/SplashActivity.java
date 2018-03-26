package com.carecloud.carepay.mini.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.carecloud.carepay.mini.BuildConfig;
import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.services.QueueUploadService;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by lmenendez on 6/20/17
 */

public class SplashActivity extends FullScreenActivity {

    boolean isDeviceRegistered;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        String newRelicId = BuildConfig.NEW_RELIC_ID;
        NewRelic.withApplicationToken(newRelicId).start(this.getApplication());
    }

    @Override
    protected void onResume(){
        super.onResume();

        isDeviceRegistered = ((ApplicationHelper) getApplication()).getApplicationPreferences().isDeviceRegistered();


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
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onStop(){
        if(isDeviceRegistered) {
            Intent queueService = new Intent(this, QueueUploadService.class);
            startService(queueService);
        }
        super.onStop();
    }
}
