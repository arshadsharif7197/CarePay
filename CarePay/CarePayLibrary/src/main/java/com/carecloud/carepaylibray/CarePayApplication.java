package com.carecloud.carepaylibray;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;

/**
 * Created by pjohnson on 15/03/17
 */

public abstract class CarePayApplication extends MultiDexApplication implements IApplicationSession, Application.ActivityLifecycleCallbacks {

    private ApplicationPreferences applicationPreferences;
    private WorkflowServiceHelper workflowServiceHelper;
    private AppAuthorizationHelper appAuthorizationHelper;
    public static PaymentsModel paymentsModel;
    public static PaymentPlanPostModel paymentPlanPostModel;
    public static AppointmentsResultModel appointmentsResultModel;
    public static PaymentPlanDTO paymentPlanDTO;

    private long lastInteraction;
    protected boolean isForeground;

    @Override
    public void onCreate() {
        super.onCreate();
        Platform.setPlatform(new AndroidPlatform(this));
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        if (applicationPreferences == null) {
            applicationPreferences = ApplicationPreferences.getInstance();
        }

        return applicationPreferences;
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        if (workflowServiceHelper == null) {
            workflowServiceHelper = new WorkflowServiceHelper(getApplicationPreferences(), getApplicationMode());
        }

        return workflowServiceHelper;
    }

    @Override
    public AppAuthorizationHelper getAppAuthorizationHelper() {
        if (appAuthorizationHelper == null) {
            appAuthorizationHelper = new AppAuthorizationHelper(getApplicationMode());
            getWorkflowServiceHelper().setAppAuthorizationHelper(appAuthorizationHelper);
        }

        return appAuthorizationHelper;
    }

    @Override
    public void onAtomicRestart() {
        workflowServiceHelper = null;
        appAuthorizationHelper = null;
        applicationPreferences = null;
    }

    public PaymentsModel getPaymentsModel() {
        return paymentsModel;
    }

    public void setPaymentsModel(PaymentsModel paymentsModel) {
        this.paymentsModel = paymentsModel;
    }

    public PaymentPlanPostModel getPaymentPlanPostModel() {
        return paymentPlanPostModel;
    }

    public void setPaymentPlanPostModel(PaymentPlanPostModel paymentPlanPostModel) {
        this.paymentPlanPostModel = paymentPlanPostModel;
    }

    public AppointmentsResultModel getAppointmentsResultModel() {
        return appointmentsResultModel;
    }

    public void setAppointmentsResultModel(AppointmentsResultModel appointmentsResultModel) {
        this.appointmentsResultModel = appointmentsResultModel;
    }

    public PaymentPlanDTO getPaymentPlanDTO() {
        return paymentPlanDTO;
    }

    public void setPaymentPlanDTO(PaymentPlanDTO paymentPlanDTO) {
        this.paymentPlanDTO = paymentPlanDTO;
    }

    @Override
    public void setLastInteraction(long systemTime) {
        this.lastInteraction = systemTime;
    }

    @Override
    public long getLastInteraction() {
        return this.lastInteraction;
    }

    public boolean isForeground() {
        return isForeground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public abstract void restartSession(Activity activity);
}
