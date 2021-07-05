package com.carecloud.carepaylibray.payments.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

public class PatientResponsibilityViewModel extends AndroidViewModel {
    private PaymentsModel paymentsModel;

    public PatientResponsibilityViewModel(@NonNull Application application) {
        super(application);

    }

    public void setPaymentsModel(PaymentsModel paymentsModel) {
        this.paymentsModel = paymentsModel;
    }

    public PaymentsModel getPaymentsModel() {
        return paymentsModel;
    }
}
