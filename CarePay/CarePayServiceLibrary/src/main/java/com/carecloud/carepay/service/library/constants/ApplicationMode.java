package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

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
    private CognitoDTO cognitoDTO;

    // use for setting practice management information
    private UserPracticeDTO userPracticeDTO;
    private String patientId;

    public UserPracticeDTO getUserPracticeDTO() {
        return userPracticeDTO;
    }

    public void setUserPracticeDTO(AppAuthorizationHelper appAuthorizationHelper, UserPracticeDTO userPracticeDTO) {
        this.userPracticeDTO = userPracticeDTO;
        if(HttpConstants.isUseUnifiedAuth()){
            this.userPracticeDTO.setUserName(appAuthorizationHelper.getUserAlias());
        }else {
            this.userPracticeDTO.setUserName(appAuthorizationHelper.getCurrUser());
        }
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public CognitoDTO getCognitoDTO() {
        return cognitoDTO;
    }

    public void setCognitoDTO(CognitoDTO cognitoDTO) {
        this.cognitoDTO = cognitoDTO;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
