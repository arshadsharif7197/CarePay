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

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.regions.Regions;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.CognitoDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppAuthorizationHelper {
    private static final int MAX_RETRIES = 4;
    private static int retryCount;



    //////////////////////////////////////////////////////////////////////////////////
    //                      New Unified Authorization Handling                      //
    //////////////////////////////////////////////////////////////////////////////////

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
    public void setAuthorizationTokens(UnifiedAuthenticationTokens authTokens){
        if(authTokens.getIdToken()!=null) {
            setIdToken(authTokens.getIdToken());
        }
        if(authTokens.getAccessToken()!=null) {
            setAccessToken(authTokens.getAccessToken());
        }
        if(authTokens.getRefreshToken()!=null) {
            setRefreshToken(authTokens.getRefreshToken());
        }
    }

    /**
     * Gives current user
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
     * @return patient user
     */
    public String getPatientUser(){
        return patientUser;
    }

    public TransitionDTO getRefreshTransition() {
        return refreshTransition;
    }

    public void setRefreshTransition(TransitionDTO refreshTransition) {
        this.refreshTransition = refreshTransition;
    }


    //////////////////////////////////////////////////////////////////////////////////
    //                      Old Cognito Authorization Handling                      //
    //////////////////////////////////////////////////////////////////////////////////

    // App settings

    private List<String>        attributeDisplaySeq;
    private Map<String, String> signUpFieldsC2O;
    private Map<String, String> signUpFieldsO2C;

    private ApplicationMode applicationMode;
    private CognitoUserPool  userPool;

    private CognitoDevice    newDevice;
    private int              itemCount;

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
    private final String clientSecret = null; /* "j9n2l7ul6jnrq68hb0c0dc4oea8i44ifm5jmictv9eisk711f67";*/

    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
    private static final Regions cognitoRegion = Regions.US_EAST_1; /* Regions.US_WEST_2;*/

    // User details from the service
    private CognitoUserSession currSession;
    private CognitoUserDetails userDetails;

    // User details to display - they are the current values, including any local modification
    private boolean phoneVerified;
    private boolean emailVerified;

    private boolean phoneAvailable;
    private boolean emailAvailable;

    private Set<String> currUserAttributes;

    /**
     * initialize cognito from the application
     * default value assign for variables
     * @param context the context
     */
    public AppAuthorizationHelper(Context context, ApplicationMode applicationMode) {
        this.applicationMode = applicationMode;
        setData();

        // Create a user pool with default ClientConfiguration
        CognitoDTO cognitoDTO = applicationMode.getCognitoDTO();
        if (cognitoDTO != null) {
            userPool = new CognitoUserPool(context,
                    cognitoDTO.getUserPoolId(),
                    cognitoDTO.getClientId(),
                    clientSecret,
                    cognitoRegion);
        }

        phoneVerified = false;
        phoneAvailable = false;
        emailVerified = false;
        emailAvailable = false;

        currUserAttributes = new HashSet<>();
        newDevice = null;
    }

    public CognitoUserPool getPool() {
        return userPool;
    }

    public Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    private void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public CognitoUserSession getCurrSession() {
        return currSession;
    }

    /**
     * @param exception object
     * @return formatted exception
     */
    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error", exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if (temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if (temp.length() > 0) {
                return formattedString;
            }
        }

        return formattedString;
    }

    public int getItemCount() {
        return itemCount;
    }

    private void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    private void setData() {
        // Set attribute display sequence
        attributeDisplaySeq = new ArrayList<>();
        attributeDisplaySeq.add("given_name");
        attributeDisplaySeq.add("middle_name");
        attributeDisplaySeq.add("family_name");
        attributeDisplaySeq.add("nickname");
        attributeDisplaySeq.add("phone_number");
        attributeDisplaySeq.add("email");

        signUpFieldsC2O = new HashMap<>();
        signUpFieldsC2O.put("Given name", "given_name");
        signUpFieldsC2O.put("Family name", "family_name");
        signUpFieldsC2O.put("Nick name", "nickname");
        signUpFieldsC2O.put("Phone number", "phone_number");
        signUpFieldsC2O.put("Phone number verified", "phone_number_verified");
        signUpFieldsC2O.put("Email verified", "email_verified");
        signUpFieldsC2O.put("Email", "email");
        signUpFieldsC2O.put("Middle name", "middle_name");

        signUpFieldsO2C = new HashMap<>();
        signUpFieldsO2C.put("given_name", "Given name");
        signUpFieldsO2C.put("family_name", "Family name");
        signUpFieldsO2C.put("nickname", "Nick name");
        signUpFieldsO2C.put("phone_number", "Phone number");
        signUpFieldsO2C.put("phone_number_verified", "Phone number verified");
        signUpFieldsO2C.put("email_verified", "Email verified");
        signUpFieldsO2C.put("email", "Email");
        signUpFieldsO2C.put("middle_name", "Middle name");

    }

    private void refreshWithSync() {
        // This will refresh the current items to display list with the attributes fetched from service
        List<String> tempKeys = new ArrayList<>();
        List<String> tempValues = new ArrayList<>();

        emailVerified = false;
        phoneVerified = false;

        emailAvailable = false;
        phoneAvailable = false;

        currUserAttributes.clear();
        itemCount = 0;

        for (Map.Entry<String, String> attr : userDetails.getAttributes().getAttributes().entrySet()) {

            tempKeys.add(attr.getKey());
            tempValues.add(attr.getValue());

            if (attr.getKey().contains("email_verified")) {
                emailVerified = attr.getValue().contains("true");
            } else if (attr.getKey().contains("phone_number_verified")) {
                phoneVerified = attr.getValue().contains("true");
            }

            if (attr.getKey().equals("email")) {
                emailAvailable = true;
            } else if (attr.getKey().equals("phone_number")) {
                phoneAvailable = true;
            }
        }
    }

    /**
     * Sign in utility
     *
     * @param username        The user name
     * @param password        The password
     * @param successCallback callback to be execruted on completion
     */
    public void signIn(String username,
                       final String password,
                       final CognitoActionCallback successCallback) {
        AuthenticationHandler authenticationHandler = executeUserAuthentication(password, successCallback);
        // set the username
        setUser(username);
        // perform the authentication
        getPool().getUser(username).getSessionInBackground(authenticationHandler);
        retryCount = 0;
    }

    /**
     * @param successAction The action to be executed on user found
     * @return Whether the current user has is signed in
     */
    public boolean refreshToken(final CognitoActionCallback successAction) {
        if(retryCount > MAX_RETRIES){
            return false;
        }
        retryCount++;
        AuthenticationHandler authenticationHandler = executeUserAuthentication("", successAction);
        CognitoUser user = getPool().getCurrentUser();
        String userName = user.getUserId();
        if (null == userName) {
            return false;
        }
        setUser(userName);
        user.getSessionInBackground(authenticationHandler);

        return true;
    }

    private AuthenticationHandler executeUserAuthentication(final String password, final CognitoActionCallback successCallback) {
        if (successCallback != null) {
            successCallback.onBeforeLogin();
        }
        // create the authentication handler
        return new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
                Log.v("AppAuthorizationHelper", "Auth Success");
                setCurrSession(cognitoUserSession);
                newDevice(device);
                if (successCallback != null) {
                    successCallback.onLoginSuccess();
                }
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
                if (!password.isEmpty()) {
                    getUserAuthentication(authenticationContinuation, username, password);
                }
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("AppAuthorizationHelper", formatException(exception));
                if (successCallback != null) {
                    successCallback.onLoginFailure(formatException(exception));
                }
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                // TODO change the place holder
            }
        };
    }

    /**
     * Helper for the authentivation handler
     *
     * @param continuation Current continuation factor if authentication is done by mutiple-factors
     * @param username     The user name (eg email)
     * @param password     The password
     */
    private void getUserAuthentication(AuthenticationContinuation continuation, String username, String password) {
        if (username != null) {
            setUser(username);
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }
}