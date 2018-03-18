package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/22/17.
 */

public class PaymentUpdateBalanceDTO {

    @SerializedName("patient_payments")
    private UpdatePatientPaymentsDTO updatePatientPaymentsDTO = new UpdatePatientPaymentsDTO();

    @SerializedName("patient_balances")
    private List<UpdatePatientBalancesDTO> updatePatientBalancesDTO = new ArrayList<>();

    @SerializedName("appointments")
    private List<AppointmentDTO> updateAppointments = new ArrayList<>();

    public UpdatePatientPaymentsDTO getUpdatePatientPaymentsDTO() {
        return updatePatientPaymentsDTO;
    }

    public void setUpdatePatientPaymentsDTO(UpdatePatientPaymentsDTO updatePatientPaymentsDTO) {
        this.updatePatientPaymentsDTO = updatePatientPaymentsDTO;
    }

    public List<UpdatePatientBalancesDTO> getUpdatePatientBalancesDTO() {
        return updatePatientBalancesDTO;
    }

    public void setUpdatePatientBalancesDTO(List<UpdatePatientBalancesDTO> updatePatientBalancesDTO) {
        this.updatePatientBalancesDTO = updatePatientBalancesDTO;
    }

    public List<AppointmentDTO> getUpdateAppointments() {
        return updateAppointments;
    }

    public void setUpdateAppointments(List<AppointmentDTO> updateAppointments) {
        this.updateAppointments = updateAppointments;
    }
}
