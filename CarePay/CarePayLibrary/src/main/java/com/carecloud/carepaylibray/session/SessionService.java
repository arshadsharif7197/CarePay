package com.carecloud.carepaylibray.session;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.carecloud.carepaylibray.CarePayApplication;

/**
 * @author pjohnson on 2019-07-01.
 */
public abstract class SessionService extends Service {


    private static final long SESSION_TIMEOUT = 1000 * 65 *10;
    private Handler handler;
    private Runnable timeOutRunnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setUpHandler();
        return super.onStartCommand(intent, flags, startId);
    }

    protected void setUpHandler() {
        if (timeOutRunnable != null) {
            handler.removeCallbacks(timeOutRunnable);
        }
        handler.postDelayed(timeOutRunnable, SESSION_TIMEOUT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        timeOutRunnable = () -> {
            if (((CarePayApplication) getApplicationContext()).isForeground()) {
                callWarningActivity();
            } else {
                setUpHandler();
            }
        };
    }

    protected abstract void callWarningActivity();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timeOutRunnable != null) {
            handler.removeCallbacks(timeOutRunnable);
        }
    }
}
