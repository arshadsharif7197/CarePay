package com.carecloud.carepay.practice.clover;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/6/17.
 */

public class CloverSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(

                "AA35b4f8a06cf80fa183849e8159ae58f2a9c8a0b0"
        ).start(this.getApplication());

    }
}
