package com.carecloud.carepay.practice.tablet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.splash.SplashActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.newrelic.agent.android.NewRelic;

/**
 * Created by kkannan on 4/12/17.
 */
public class PracticeTabletSplashActivity extends SplashActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String newRelicId = BuildConfig.NEW_RELIC_ID;
        NewRelic.withApplicationToken(newRelicId).start(this.getApplication());

        MixPanelUtil.logEvent("Practice App Start");
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Intent intent = new Intent(PracticeTabletSplashActivity.this, PracticeTabletSplashActivity.class);
                intent.putExtra(CarePayConstants.CRASH, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(PracticeTabletSplashActivity.this.getBaseContext(),
                        0, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) PracticeTabletSplashActivity.this.getBaseContext()
                        .getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                finishAffinity();
                System.exit(2);
            }
        });
    }
}
