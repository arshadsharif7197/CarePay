package com.carecloud.carepay.patient.myhealth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author pjohnson on 2019-08-12.
 */
public class BaseViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

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
}
