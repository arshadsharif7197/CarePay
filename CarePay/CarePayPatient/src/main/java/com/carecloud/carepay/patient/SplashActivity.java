package com.carecloud.carepay.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;

public class SplashActivity extends Activity {
    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setTheme(R.style.AppThemeNoActionBar);
        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    private Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOPSPLASH:
                    Intent intent = new Intent(SplashActivity.this, LibraryMainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }

    };
}
