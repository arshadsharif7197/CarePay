package com.carecloud.carepay.practice.clover;

import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/6/17
 */

public class CloverSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String newRelicId = BuildConfig.NEW_RELIC_ID;
        NewRelic.withApplicationToken(newRelicId).start(this.getApplication());

        MixPanelUtil.logEvent("Clover App Start");
    }

    @Override
    public void onStop(){
        Intent intent = new Intent(getContext(), CloverQueueUploadService.class);
        startService(intent);

        super.onStop();
    }

}
