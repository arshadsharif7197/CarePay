package com.carecloud.carepay.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.selectlanguage.SelectLangaugeActivity;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the patient app
 * Applied logic:
 * If application language not yet selected navigate to language selection screen
 * if user authentication not found navigate to login screen
 * else navigate to appointment screen
 */

public class SplashActivity extends Activity {

    private static final int STOPSPLASH = 0;
    private static final long SPLASHTIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //setTheme(R.style.AppThemeNoActionBar);
        Message msg = new Message();
        msg.what = STOPSPLASH;

        splashHandler.sendMessageDelayed(msg, SPLASHTIME);
    }

    public Handler splashHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean signedIn = CognitoAppHelper.findCurrentUser(null);
            if (msg.what == STOPSPLASH && !(ApplicationPreferences.Instance.getUserLanguage().equals("English"))) {
                Intent intent = new Intent(SplashActivity.this, SelectLangaugeActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            } else if (signedIn) {
                Intent intent = new Intent(SplashActivity.this, AppointmentsActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();

            } else if (msg.what == STOPSPLASH && (ApplicationPreferences.Instance.getUserLanguage().equals("English"))) {
                Intent intent = new Intent(SplashActivity.this, SigninSignupActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };
}