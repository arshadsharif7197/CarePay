package com.carecloud.carepaylibray.retail.models;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.dtos.BasePayloadDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PatientPaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailPayloadDTO extends BasePayloadDto {

    @SerializedName("languages")
    private List<OptionDTO> languages = new ArrayList<>();

    @SerializedName("practice_information")
    private List<UserPracticeDTO> practiceInformation = new ArrayList<>();

    @SerializedName("payment_settings")
    private List<PaymentsPayloadSettingsDTO> paymentSettings = new ArrayList<>();

    @SerializedName("demographics")
    private DemographicPayloadInfoDTO demographicDTO = new DemographicPayloadInfoDTO();

    @SerializedName("patient_credit_cards")
    private List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards = new ArrayList<>();

    @SerializedName("merchant_services")
    private List<MerchantServicesDTO> merchant_services = new ArrayList<>();

    @SerializedName("practice_patient_ids")
    private List<RetailPracticeDTO> retailPracticeList = new ArrayList<>();

    @SerializedName("patient_payments")
    private PatientPaymentsDTO patientPayments = new PatientPaymentsDTO();

    @SerializedName("return_url")
    private String returnUrl;

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

    public List<UserPracticeDTO> getPracticeInformation() {
        return practiceInformation;
    }

    public void setPracticeInformation(List<UserPracticeDTO> practiceInformation) {
        this.practiceInformation = practiceInformation;
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

    public PatientPaymentsDTO getPatientPayments() {
        return patientPayments;
    }

    public void setPatientPayments(PatientPaymentsDTO patientPayments) {
        this.patientPayments = patientPayments;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    public List<MerchantServicesDTO> getMerchant_services() {
        return merchant_services;
    }

    public void setMerchant_services(List<MerchantServicesDTO> merchant_services) {
        this.merchant_services = merchant_services;
    }
}
