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


    protected static final long PATIENT_SESSION_TIMEOUT = 1000 * 60 * 9;
    protected static final long PRACTICE_SESSION_TIMEOUT = 1000 * 60 * 3;
    private Handler handler;
    private Runnable timeOutRunnable;
    protected long sessionTimeout;

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

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        timeOutRunnable = () -> {
            if (((CarePayApplication) getApplicationContext()).isForeground()) {
                callWarningActivity();
            } else {
                //TODO handle background logout
//                setUpHandler();
            }
        };
    }

    protected void setUpHandler() {
        if (timeOutRunnable != null) {
            handler.removeCallbacks(timeOutRunnable);
        }
        handler.postDelayed(timeOutRunnable, sessionTimeout);
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
