package com.carecloud.carepaylibray.payments.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.carecloud.carepaylibray.payments.models.PaymentsModel;

public class PatientResponsibilityViewModel extends ViewModel {
    private MutableLiveData<PaymentsModel> paymentsModel = new MutableLiveData<>();
    private PaymentsModel paymentsModelData;

    public void setPaymentsModel(PaymentsModel paymentsModel) {
        paymentsModelData = paymentsModel;
        this.paymentsModel.setValue(paymentsModel);
    }

    public LiveData<PaymentsModel> getPaymentsModel() {
        return paymentsModel;
    }

    public PaymentsModel getPaymentsModelData() {
        return paymentsModelData;
    }
}
