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

package com.carecloud.carepaylibray.cognito;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

public class CognitoAppHelper {
    // App settings

    private static List<String>        attributeDisplaySeq;
    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static CognitoAppHelper    cognitoAppHelper;
    private static CognitoUserPool     userPool;
    private static String              user;
    private static CognitoDevice       newDevice;
    private static int                 itemCount;

    // Change the next three lines of code to run this demo on your user pool

    /**
     * Add your pool id here
     */
    private static final String userPoolId = "us-east-1_m9M7XF4pZ"; /*"us-west-2_0eHuDp72i";*/

    /**
     * Add you app id
     */
    private static final String clientId = "33hd7aq7r1uk5net1q7kt2p7jr"; /*"71le76qt8rcpbqo682qb7j07q0";*/

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
    private static final String clientSecret = null; /* "j9n2l7ul6jnrq68hb0c0dc4oea8i44ifm5jmictv9eisk711f67";*/

    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
    private static final Regions cognitoRegion = Regions.US_EAST_1; /* Regions.US_WEST_2;*/

    // User details from the service
    private static CognitoUserSession currSession;
    private static CognitoUserDetails userDetails;

    // User details to display - they are the current values, including any local modification
    private static boolean phoneVerified;
    private static boolean emailVerified;

    private static boolean phoneAvailable;
    private static boolean emailAvailable;

    private static Set<String> currUserAttributes;

    public static void init(Context context) {
        setData();

        if (cognitoAppHelper != null && userPool != null) {
            return;
        }

        if (cognitoAppHelper == null) {
            cognitoAppHelper = new CognitoAppHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);

        }

        phoneVerified = false;
        phoneAvailable = false;
        emailVerified = false;
        emailAvailable = false;

        currUserAttributes = new HashSet<>();
        newDevice = null;
    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    public static void setCurrSession(CognitoUserSession session) {
        currSession = session;
    }

    public static CognitoUserSession getCurrSession() {
        return currSession;
    }

    public static void setUserDetails(CognitoUserDetails details) {
        userDetails = details;
        refreshWithSync();
    }

    public static String getCurrUser() {
        return user;
    }

    public static void setUser(String newUser) {
        user = newUser;
    }

    public static boolean isPhoneVerified() {
        return phoneVerified;
    }

    public static boolean isEmailVerified() {
        return emailVerified;
    }

    public static boolean isPhoneAvailable() {
        return phoneAvailable;
    }

    public static boolean isEmailAvailable() {
        return emailAvailable;
    }

    public static String formatException(Exception exception) {
        String formattedString = "Internal Error";
        Log.e("App Error", exception.toString());
        Log.getStackTraceString(exception);

        String temp = exception.getMessage();

        if (temp != null && temp.length() > 0) {
            formattedString = temp.split("\\(")[0];
            if (temp != null && temp.length() > 0) {
                return formattedString;
            }
        }

        return formattedString;
    }

    public static int getItemCount() {
        return itemCount;
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    private static void setData() {
        // Set attribute display sequence
        attributeDisplaySeq = new ArrayList<String>();
        attributeDisplaySeq.add("given_name");
        attributeDisplaySeq.add("middle_name");
        attributeDisplaySeq.add("family_name");
        attributeDisplaySeq.add("nickname");
        attributeDisplaySeq.add("phone_number");
        attributeDisplaySeq.add("email");

        signUpFieldsC2O = new HashMap<String, String>();
        signUpFieldsC2O.put("Given name", "given_name");
        signUpFieldsC2O.put("Family name", "family_name");
        signUpFieldsC2O.put("Nick name", "nickname");
        signUpFieldsC2O.put("Phone number", "phone_number");
        signUpFieldsC2O.put("Phone number verified", "phone_number_verified");
        signUpFieldsC2O.put("Email verified", "email_verified");
        signUpFieldsC2O.put("Email", "email");
        signUpFieldsC2O.put("Middle name", "middle_name");

        signUpFieldsO2C = new HashMap<String, String>();
        signUpFieldsO2C.put("given_name", "Given name");
        signUpFieldsO2C.put("family_name", "Family name");
        signUpFieldsO2C.put("nickname", "Nick name");
        signUpFieldsO2C.put("phone_number", "Phone number");
        signUpFieldsO2C.put("phone_number_verified", "Phone number verified");
        signUpFieldsO2C.put("email_verified", "Email verified");
        signUpFieldsO2C.put("email", "Email");
        signUpFieldsO2C.put("middle_name", "Middle name");

    }

    private static void refreshWithSync() {
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
     * @param context The context
     * @param username The user name
     * @param password The password
     * @param progressBar Optional progress to show
     * @param successCallback Optional callback executed after a successful signin
     */
    public static void signIn(final Context context,
                              String username,
                              final String password,
                              final ProgressBar progressBar,
                              final CognitoActionCallback successCallback) {
        if(progressBar != null) { // show progress if there is one
            progressBar.setVisibility(View.VISIBLE);
        }

        // create the authentication handler
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
                Log.v(LOG_TAG, "Auth Success");

                CognitoAppHelper.setCurrSession(cognitoUserSession);
                CognitoAppHelper.newDevice(device);
                if(progressBar != null) { // hide the progress (if any)
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if(successCallback != null) {
                    successCallback.executeAction();
                }
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
                Locale.setDefault(Locale.getDefault());
                getUserAuthentication(authenticationContinuation, username, password);
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            }

            @Override
            public void onFailure(Exception e) {
                if(progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                SystemUtil.showDialogMessage(context,
                                             "Sign-in failed",
                                             "Invalid user id or password");// TODO: 9/21/2016 prepare for translation if kept
                Log.e(LOG_TAG, CognitoAppHelper.formatException(e));
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                // TODO change the place holder
            }
        };
        // set the username
        CognitoAppHelper.setUser(username);
        // perform the authentication
        CognitoAppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
    }

    /**
     * Helper for the authentivation handler
     * @param continuation Current continuation factor if authentication is done by mutiple-factors
     * @param username The user name (eg email)
     * @param password The password
     */
    private static void getUserAuthentication(AuthenticationContinuation continuation, String username, String password) {
        if (username != null) {
            CognitoAppHelper.setUser(username);
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    /**
     * @param successAction The action to be executed on user found
     * @return Whether the current user has is signed in
     */
    public static boolean findCurrentUser(final CognitoActionCallback successAction) {
        AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                CognitoAppHelper.setCurrSession(userSession);
                CognitoAppHelper.newDevice(newDevice);

                if(successAction != null) {
                    successAction.executeAction();
                }
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
                Log.v(LOG_TAG, "getAuthenticationDetails()");
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Log.v(LOG_TAG, "getMFACode()");
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.v(LOG_TAG, "authenticationChallenge()");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e(LOG_TAG, exception.getLocalizedMessage());
            }
        };

        CognitoUser user = CognitoAppHelper.getPool().getCurrentUser();
        String userName = user.getUserId();
        if(userName != null) {
            CognitoAppHelper.setUser(userName);
            user.getSessionInBackground(authenticationHandler);
            return true;
        }
        return false;
    }
}