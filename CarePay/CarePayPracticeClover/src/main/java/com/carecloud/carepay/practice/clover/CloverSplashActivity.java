package com.carecloud.carepay.practice.clover;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/6/17.
 */

public class CloverSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewRelic.withApplicationToken(
                getString(R.string.new_relic_application_token)
        ).start(this.getApplication());

        MixPanelUtil.logEvent("Clover App Start");
    }
}
