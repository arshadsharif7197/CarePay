package com.carecloud.carepay.practice.tablet;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/12/17.
 */

public class PracticeTabletSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(
            getString(R.string.new_relic_application_token)
        ).start(this.getApplication());

    }
}
