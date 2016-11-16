package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

/**
 * Created by Jahirul Bhuiyan on 11/16/2016.
 * user for store application variable like user type, congito user pool 
 */

public class ApplicationMode {

    public enum ApplicationType {
        PATIENT, PRACTICE, PRACTICE_PATIENT_MODE
    }

    private static ApplicationMode instance;
    private ApplicationType applicationType;
    private CognitoDTO cognitoDTO;

    // use for seting practice maganement information
    private UserPracticeDTO userPracticeDTO;

    public UserPracticeDTO getUserPracticeDTO() {
        return userPracticeDTO;
    }

    public void setUserPracticeDTO(UserPracticeDTO userPracticeDTO) {
        this.userPracticeDTO = userPracticeDTO;
        this.userPracticeDTO.setPracticeUser(CognitoAppHelper.getCurrUser());
    }


    private ApplicationMode() {
    }

    public static ApplicationMode getInstance() {
        if (instance == null) {
            instance = new ApplicationMode();
        }
        return instance;
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


}
