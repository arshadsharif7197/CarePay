/*
 *  Copyright 2013-2016 Amazon.com,
 *  Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Amazon Software License (the "License").
 *  You may not use this file except in compliance with the
 *  License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 *  or in the "license" file accompanying this file. This file is
 *  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, express or implied. See the License
 *  for the specific language governing permissions and
 *  limitations under the License.
 */

package com.carecloud.carepay.service.library.cognito;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;

public class AppAuthorizationHelper {

    private String accessToken;
    private String refreshToken;
    private String idToken;
    private String practiceUser;
    private String patientUser;
    private TransitionDTO refreshTransition;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    /**
     * @param idToken ID token
     */
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    /**
     * @param authTokens new auth tokens
     */
    public void setAuthorizationTokens(UnifiedAuthenticationTokens authTokens) {
        if (authTokens.getIdToken() != null) {
            setIdToken(authTokens.getIdToken());
        }
        if (authTokens.getAccessToken() != null) {
            setAccessToken(authTokens.getAccessToken());
        }
        if (authTokens.getRefreshToken() != null) {
            setRefreshToken(authTokens.getRefreshToken());
        }
    }

    /**
     * Gives current user
     *
     * @return current user
     */
    public String getCurrUser() {
        if (applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            return patientUser;
        }
        return practiceUser;
    }

    /**
     * Set current user
     *
     * @param newUser user
     */
    public void setUser(String newUser) {
        if (applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            patientUser = newUser;
        } else {
            patientUser = null;
            practiceUser = newUser;
        }
    }

    /**
     * Get Patient user
     *
     * @return patient user
     */
    public String getPatientUser() {
        return patientUser;
    }

    public TransitionDTO getRefreshTransition() {
        return refreshTransition;
    }

    public void setRefreshTransition(TransitionDTO refreshTransition) {
        this.refreshTransition = refreshTransition;
    }

    private ApplicationMode applicationMode;

    /**
     * initialize cognito from the application
     * default value assign for variables
     */
    public AppAuthorizationHelper(ApplicationMode applicationMode) {
        this.applicationMode = applicationMode;
    }
}