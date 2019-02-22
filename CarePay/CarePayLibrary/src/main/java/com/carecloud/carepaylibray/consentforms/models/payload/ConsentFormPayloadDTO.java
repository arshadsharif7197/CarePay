package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.dtos.BasePayloadDto;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/13/16.
 */

public class ConsentFormPayloadDTO extends BasePayloadDto {

    @SerializedName("appointments")
    @Expose
    private List<ConsentFormAppointmentsPayloadDTO> consentFormAppointmentPayload = new ArrayList<>();

    @Expose
    @SerializedName("patient_forms_response")
    private List<ConsentFormUserResponseDTO> responses = new ArrayList<>();

    @Expose
    @SerializedName("user_forms")
    private List<UserFormDTO> userForms = new ArrayList<>();

    @Expose
    @SerializedName("practice_information")
    private List<UserPracticeDTO> practicesInformation;

    public List<ConsentFormAppointmentsPayloadDTO> getConsentFormAppointmentPayload() {
        return consentFormAppointmentPayload;
    }

    public void setConsentFormAppointmentPayload(List<ConsentFormAppointmentsPayloadDTO> consentFormAppointmentPayload) {
        this.consentFormAppointmentPayload = consentFormAppointmentPayload;
    }

    public List<ConsentFormUserResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ConsentFormUserResponseDTO> responses) {
        this.responses = responses;
    }

    public List<UserPracticeDTO> getPracticesInformation() {
        return practicesInformation;
    }

    public void setPracticesInformation(List<UserPracticeDTO> practicesInformation) {
        this.practicesInformation = practicesInformation;
    }

    public List<UserFormDTO> getUserForms() {
        return userForms;
    }

    public void setUserForms(List<UserFormDTO> userForms) {
        this.userForms = userForms;
    }
}
