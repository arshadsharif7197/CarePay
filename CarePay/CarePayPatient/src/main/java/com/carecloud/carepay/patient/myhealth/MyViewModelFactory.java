package com.carecloud.carepay.patient.myhealth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * @author pjohnson on 2019-08-12.
 */
public class MyViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;


    public MyViewModelFactory(Application application) {
        mApplication = application;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MyHealthViewModel(mApplication);
    }
}
