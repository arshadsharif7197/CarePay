package com.carecloud.carepaylibray.session;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.carecloud.carepaylibray.CarePayApplication;
import com.google.common.util.concurrent.ListenableFuture;

public abstract class SessionWorker extends ListenableWorker {


    protected static final long PATIENT_SESSION_TIMEOUT = 1000 * 60 * 9;
    protected static final long PRACTICE_SESSION_TIMEOUT = 1000 * 60 * 2; //old logic
    public static Handler handler;
    public Runnable timeOutRunnable;
    protected long sessionTimeout;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public SessionWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        HandlerThread handlerThread = new HandlerThread("Session Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        timeOutRunnable = () -> {
            if (((CarePayApplication) getApplicationContext()).isForeground()) {
                callWarningActivity();
            } else {
                logout();
            }
        };
    }


    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        setUpHandler();
        return CallbackToFutureAdapter.getFuture(completer -> {
            return completer;
        });
    }

    protected void setUpHandler() {
        if (timeOutRunnable != null) {
            handler.removeCallbacks(timeOutRunnable);
        }
        handler.postDelayed(timeOutRunnable, sessionTimeout);
    }

    protected abstract void callWarningActivity();

    protected abstract void logout();
}
