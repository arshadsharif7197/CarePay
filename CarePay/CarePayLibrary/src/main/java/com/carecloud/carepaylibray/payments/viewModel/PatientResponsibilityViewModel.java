package com.carecloud.carepaylibray.payments.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

public class PatientResponsibilityViewModel extends ViewModel {
    private MutableLiveData<PaymentsModel> paymentsModel = new MutableLiveData<>();
    private PaymentsModel paymentsModelData;
    private DemographicDTO demographicDTOData;
    private MutableLiveData<DemographicDTO> demographicDTOMutableLiveData = new MutableLiveData<>();

    public void setPaymentsModel(PaymentsModel paymentsModel) {
        paymentsModelData = paymentsModel;
        this.paymentsModel.setValue(paymentsModel);
    }
    public void setDemographicDTOModel(DemographicDTO demographicDTO) {
        demographicDTOData = demographicDTO;
        this.demographicDTOMutableLiveData.setValue(demographicDTOData);
    }

    public LiveData<PaymentsModel> getPaymentsModel() {
        return paymentsModel;
    } public LiveData<DemographicDTO> getDemographicDTOModel() {
        return demographicDTOMutableLiveData;
    }

    public PaymentsModel getPaymentsModelData() {
        return paymentsModelData;
    }
    public DemographicDTO getDemographicDTOData() {
        return demographicDTOData;
    }
}

