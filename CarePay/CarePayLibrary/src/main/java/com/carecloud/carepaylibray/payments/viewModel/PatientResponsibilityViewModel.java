package com.carecloud.carepaylibray.payments.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.medications.models.MedicationsOnlyResultModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

public class PatientResponsibilityViewModel extends ViewModel {
    private MutableLiveData<PaymentsModel> paymentsModel = new MutableLiveData<>();
    private PaymentsModel paymentsModelData;
    private DemographicDTO demographicDTOData;
    private MedicationsOnlyResultModel medicationsOnlyResultModel;
    private String jsonBody;
    public void setPaymentsModel(PaymentsModel paymentsModel) {
        paymentsModelData = paymentsModel;
        this.paymentsModel.setValue(paymentsModel);
    }
    public void setDemographicDTOModel(DemographicDTO demographicDTO) {
        demographicDTOData = demographicDTO;

    }

    public LiveData<PaymentsModel> getPaymentsModel() {
        return paymentsModel;
    }

    public PaymentsModel getPaymentsModelData() {
        return paymentsModelData;
    }
    public DemographicDTO getDemographicDTOData() {
        return demographicDTOData;
    }

    public void setAllergiesdata(MedicationsOnlyResultModel medicationsOnlyResultModel, String jsonBody) {
        this.medicationsOnlyResultModel=medicationsOnlyResultModel;
        this.jsonBody=jsonBody;

    }
    public MedicationsOnlyResultModel getMedicationOnlyModel(){
        return medicationsOnlyResultModel;
    }
    public String getJsonBody(){
        return jsonBody;
    }

}

