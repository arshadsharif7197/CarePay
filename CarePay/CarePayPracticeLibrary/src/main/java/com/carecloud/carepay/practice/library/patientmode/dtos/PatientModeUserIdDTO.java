
package com.carecloud.carepay.practice.library.patientmode.dtos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeUserIdDTO {

    @SerializedName("check_username_in_cognito")
    @Expose
    private String checkUsernameInCognito;
    @SerializedName("check_user_id_practices")
    @Expose
    private List<PatientModeCheckUserIdDTO> checkUserIdPractices = null;

    public String getCheckUsernameInCognito() {
        return checkUsernameInCognito;
    }

    public void setCheckUsernameInCognito(String checkUsernameInCognito) {
        this.checkUsernameInCognito = checkUsernameInCognito;
    }

    public List<PatientModeCheckUserIdDTO> getCheckUserIdPractices() {
        return checkUserIdPractices;
    }

    public void setCheckUserIdPractices(List<PatientModeCheckUserIdDTO> checkUserIdPractices) {
        this.checkUserIdPractices = checkUserIdPractices;
    }

}
