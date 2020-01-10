package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;

/**
 * Created by Jahirul Bhuiyan on 11/16/2016.
 * user for store application variable like user type, congito user pool
 * Singleton class
 */

public class ApplicationMode {

    public enum ApplicationType {
        PATIENT, PRACTICE, PRACTICE_PATIENT_MODE
    }

    private ApplicationType applicationType;

    // use for setting practice management information
    private UserPracticeDTO userPracticeDTO;
    private String patientId;

    public UserPracticeDTO getUserPracticeDTO() {
        return userPracticeDTO;
    }

    /**
     * @param appAuthorizationHelper auth helper
     * @param userPracticeDTO        practice user DTO
     */
    public void setUserPracticeDTO(AppAuthorizationHelper appAuthorizationHelper, UserPracticeDTO userPracticeDTO) {
        this.userPracticeDTO = userPracticeDTO;
        if (userPracticeDTO != null) {
            this.userPracticeDTO.setUserName(appAuthorizationHelper.getCurrUser());
        }
    }

    /**
     * Clears the current practice DTO, should be called when logging out of app
     */
    public void clearUserPracticeDTO() {
        this.userPracticeDTO = null;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
        Label.setApplicationType(applicationType);
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
