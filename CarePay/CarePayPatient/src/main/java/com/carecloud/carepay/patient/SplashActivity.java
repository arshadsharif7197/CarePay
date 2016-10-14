package com.carecloud.carepay.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.carecloud.carepaylibray.activities.LibraryMainActivity;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the patient app
 * Applied logic:
 * If application language not yet selected navigate to language selection screen
 * if user authentication not found navigate to login screen
 * else navigate to appointment screen
 * */

public class SplashActivity extends Activity {

    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //setTheme(R.style.AppThemeNoActionBar);
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO: Need to apply navigation logic
            if(msg.what==STOPSPLASH){
                Intent intent = new Intent(SplashActivity.this, LibraryMainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
            super.handleMessage(msg);
        }

    };
}
