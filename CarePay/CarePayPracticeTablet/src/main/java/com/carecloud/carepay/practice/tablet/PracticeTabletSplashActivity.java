package com.carecloud.carepay.practice.tablet;

import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/12/17
 */
public class PracticeTabletSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String newRelicId = BuildConfig.NEW_RELIC_ID;
        NewRelic.withApplicationToken(newRelicId).start(this.getApplication());

        MixPanelUtil.logEvent("Practice App Start");
    }
}
