package com.carecloud.carepaylibray.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepaylibray.CarePayApplication;

/**
 * @author pjohnson on 2019-08-12.
 */
public class BaseViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> skeleton = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public void setLoading(Boolean loading) {
        this.loading.setValue(loading);
    }

    public MutableLiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage.setValue(successMessage);
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.setValue(errorMessage);
    }

    public MutableLiveData<Boolean> getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(Boolean showSkeleton) {
        this.skeleton.setValue(showSkeleton);
    }

    protected WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((CarePayApplication) getApplication()).getWorkflowServiceHelper();
    }

    protected AppAuthorizationHelper getAppAuthorizationHelper() {
        return ((CarePayApplication) getApplication()).getAppAuthorizationHelper();
    }
}
