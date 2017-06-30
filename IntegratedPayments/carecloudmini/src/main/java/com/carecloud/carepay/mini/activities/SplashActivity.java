package com.carecloud.carepay.mini.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.mini.R;

/**
 * Created by lmenendez on 6/20/17
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(SplashActivity.this, RegistrationActivity.class);
                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            }
        }, 1000);
    }
}
