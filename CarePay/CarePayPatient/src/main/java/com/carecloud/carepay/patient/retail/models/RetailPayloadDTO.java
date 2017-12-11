package com.carecloud.carepay.patient.retail.models;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailPayloadDTO {

    @SerializedName("demographics")
    private DemographicPayloadInfoDTO demographicDTO = new DemographicPayloadInfoDTO();

    @SerializedName("practice_patient_ids")
    private List<RetailPracticeDTO> retailPracticeList = new ArrayList<>();

    @SerializedName("practice_information")
    private List<UserPracticeDTO> userPractices = new ArrayList<>();

    @SerializedName("payment_settings")
    private List<PaymentsPayloadSettingsDTO> paymentSettings = new ArrayList<>();

    @SerializedName("patient_credit_cards")
    private List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards = new ArrayList<>();

    public DemographicPayloadInfoDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicPayloadInfoDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<RetailPracticeDTO> getRetailPracticeList() {
        return retailPracticeList;
    }

    public void setRetailPracticeList(List<RetailPracticeDTO> retailPracticeList) {
        this.retailPracticeList = retailPracticeList;
    }

    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public List<PaymentsPayloadSettingsDTO> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<PaymentsPayloadSettingsDTO> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }

    public List<PaymentsPatientsCreditCardsPayloadListDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    public void setPatientCreditCards(List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }
}
