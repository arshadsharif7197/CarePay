package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/22/17.
 */

public class PaymentUpdateBalanceDTO {

    @SerializedName("patient_payments")
    private UpdatePatientPaymentsDTO updatePatientPaymentsDTO;

    @SerializedName("patient_balances")
    private UpdatePatientBalancesDTO updatePatientBalancesDTO;

    public UpdatePatientPaymentsDTO getUpdatePatientPaymentsDTO() {
        return updatePatientPaymentsDTO;
    }

    public void setUpdatePatientPaymentsDTO(UpdatePatientPaymentsDTO updatePatientPaymentsDTO) {
        this.updatePatientPaymentsDTO = updatePatientPaymentsDTO;
    }

    public UpdatePatientBalancesDTO getUpdatePatientBalancesDTO() {
        return updatePatientBalancesDTO;
    }

    public void setUpdatePatientBalancesDTO(UpdatePatientBalancesDTO updatePatientBalancesDTO) {
        this.updatePatientBalancesDTO = updatePatientBalancesDTO;
    }
}
