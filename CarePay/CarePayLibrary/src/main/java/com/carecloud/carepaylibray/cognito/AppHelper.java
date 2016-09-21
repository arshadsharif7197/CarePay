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
import android.graphics.Color;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.AttributeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppHelper {
    // App settings

    private static List<String>        attributeDisplaySeq;
    private static Map<String, String> signUpFieldsC2O;
    private static Map<String, String> signUpFieldsO2C;

    private static AppHelper       appHelper;
    private static CognitoUserPool userPool;
    private static String          user;
    private static CognitoDevice   newDevice;

    private static CognitoUserAttributes attributesChanged;
    private static List<AttributeType>   attributesToDelete;

    private static List<ItemToDisplay> currDisplayedItems;
    private static int                 itemCount;

    private static List<ItemToDisplay> trustedDevices;
    private static int                 trustedDevicesCount;
    private static List<CognitoDevice> deviceDetails;
    private static CognitoDevice       thisDevice;
    private static boolean             thisDeviceTrustState;

    // Change the next three lines of code to run this demo on your user pool

    /**
     * Add your pool id here
     */
    private static final String userPoolId = "us-west-2_0eHuDp72i";

    /**
     * Add you app id
     */
    private static final String clientId = "71le76qt8rcpbqo682qb7j07q0";

    /**
     * App secret associated with your app id - if the App id does not have an associated App secret,
     * set the App secret to null.
     * e.g. clientSecret = null;
     */
    private static final String clientSecret = "j9n2l7ul6jnrq68hb0c0dc4oea8i44ifm5jmictv9eisk711f67";

    /**
     * Set Your User Pools region.
     * e.g. if your user pools are in US East (N Virginia) then set cognitoRegion = Regions.US_EAST_1.
     */
    private static final Regions cognitoRegion = Regions.US_WEST_2;

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

        if (appHelper != null && userPool != null) {
            return;
        }

        if (appHelper == null) {
            appHelper = new AppHelper();
        }

        if (userPool == null) {

            // Create a user pool with default ClientConfiguration
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cognitoRegion);

            // This will also work
            /*
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            AmazonCognitoIdentityProvider cipClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), clientConfiguration);
            cipClient.setRegion(Region.getRegion(cognitoRegion));
            userPool = new CognitoUserPool(context, userPoolId, clientId, clientSecret, cipClient);
            */
        }

        phoneVerified = false;
        phoneAvailable = false;
        emailVerified = false;
        emailAvailable = false;

        currUserAttributes = new HashSet<String>();
        currDisplayedItems = new ArrayList<ItemToDisplay>();
        trustedDevices = new ArrayList<ItemToDisplay>();
        newDevice = null;
        thisDevice = null;
        thisDeviceTrustState = false;
    }

    public static CognitoUserPool getPool() {
        return userPool;
    }

    public static Map<String, String> getSignUpFieldsC2O() {
        return signUpFieldsC2O;
    }

    public static Map<String, String> getSignUpFieldsO2C() {
        return signUpFieldsO2C;
    }

    public static List<String> getAttributeDisplaySeq() {
        return attributeDisplaySeq;
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

    public static CognitoUserDetails getUserDetails() {
        return userDetails;
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

    public static void setPhoneVerified(boolean phoneVerif) {
        phoneVerified = phoneVerif;
    }

    public static void setEmailVerified(boolean emailVerif) {
        emailVerified = emailVerif;
    }

    public static void setPhoneAvailable(boolean phoneAvail) {
        phoneAvailable = phoneAvail;
    }

    public static void setEmailAvailable(boolean emailAvail) {
        emailAvailable = emailAvail;
    }

    public static void clearCurrUserAttributes() {
        currUserAttributes.clear();
    }

    public static void addCurrUserattribute(String attribute) {
        currUserAttributes.add(attribute);
    }

    public static List<String> getNewAvailableOptions() {
        List<String> newOption = new ArrayList<String>();
        for (String attribute : attributeDisplaySeq) {
            if (!(currUserAttributes.contains(attribute))) {
                newOption.add(attribute);
            }
        }
        return newOption;
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

    public static int getDevicesCount() {
        return trustedDevicesCount;
    }

    public static ItemToDisplay getItemForDisplay(int position) {
        return currDisplayedItems.get(position);
    }

    public static ItemToDisplay getDeviceForDisplay(int position) {
        if (position >= trustedDevices.size()) {
            return new ItemToDisplay(" ", " ", " ", Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"), 0, null);
        }
        return trustedDevices.get(position);
    }

    public static void newDevice(CognitoDevice device) {
        newDevice = device;
    }

    public static void setDevicesForDisplay(List<CognitoDevice> devicesList) {
        trustedDevicesCount = 0;
        thisDeviceTrustState = false;
        deviceDetails = devicesList;
        trustedDevices = new ArrayList<ItemToDisplay>();
        for (CognitoDevice device : devicesList) {
            if (thisDevice != null && thisDevice.getDeviceKey().equals(device.getDeviceKey())) {
                thisDeviceTrustState = true;
            } else {
                ItemToDisplay item = new ItemToDisplay("", device.getDeviceName(), device.getCreateDate().toString(), Color.BLACK, Color.DKGRAY, Color.parseColor("#329AD6"), 0, null);
                item.setDataDrawable("checked");
                trustedDevices.add(item);
                trustedDevicesCount++;
            }
        }
    }

    public static CognitoDevice getDeviceDetail(int position) {
        if (position <= trustedDevicesCount) {
            return deviceDetails.get(position);
        } else {
            return null;
        }
    }

    public static CognitoDevice getNewDevice() {
        return newDevice;
    }

    public static CognitoDevice getThisDevice() {
        return thisDevice;
    }

    public static void setThisDevice(CognitoDevice device) {
        thisDevice = device;
    }

    public static boolean getThisDeviceTrustState() {
        return thisDeviceTrustState;
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

        currDisplayedItems = new ArrayList<ItemToDisplay>();
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

        // Arrange the input attributes per the display sequence
        Set<String> keySet = new HashSet<>(tempKeys);
        for (String det : attributeDisplaySeq) {
            if (keySet.contains(det)) {
                // Adding items to display list in the required sequence

                ItemToDisplay item = new ItemToDisplay(signUpFieldsO2C.get(det), tempValues.get(tempKeys.indexOf(det)), "",
                                                       Color.BLACK, Color.DKGRAY, Color.parseColor("#37A51C"),
                                                       0, null);

                if (det.contains("email")) {
                    if (emailVerified) {
                        item.setDataDrawable("checked");
                        item.setMessageText("Email verified");
                    } else {
                        item.setDataDrawable("not_checked");
                        item.setMessageText("Email not verified");
                        item.setMessageColor(Color.parseColor("#E94700"));
                    }
                }

                if (det.contains("phone_number")) {
                    if (phoneVerified) {
                        item.setDataDrawable("checked");
                        item.setMessageText("Phone number verified");
                    } else {
                        item.setDataDrawable("not_checked");
                        item.setMessageText("Phone number not verified");
                        item.setMessageColor(Color.parseColor("#E94700"));
                    }
                }

                currDisplayedItems.add(item);
                currUserAttributes.add(det);
                itemCount++;
            }
        }
    }

    private static void modifyAttribute(String attributeName, String newValue) {
        //

    }

    private static void deleteAttribute(String attributeName) {

    }

    /**
     * Authentication related item to display
     */
    private static class ItemToDisplay {

        // Text for display
        private String labelText;
        private String dataText;
        private String messageText;

        // Display text colors
        private int labelColor;
        private int dataColor;
        private int messageColor;

        // Data box background
        private int dataBackground;

        // Data box drawable
        private String dataDrawable;

        // Constructor
        protected ItemToDisplay(String labelText, String dataText, String messageText,
                                int labelColor, int dataColor, int messageColor,
                                int dataBackground, String dataDrawable) {
            this.labelText = labelText;
            this.dataText = dataText;
            this.messageText = messageText;
            this.labelColor = labelColor;
            this.dataColor = dataColor;
            this.messageColor = messageColor;
            this.dataBackground = dataBackground;
            this.dataDrawable = dataDrawable;
        }

        public String getLabelText() {
            return labelText;
        }

        public void setLabelText(String labelText) {
            this.labelText = labelText;
        }

        public String getDataText() {
            return dataText;
        }

        public void setDataText(String dataText) {
            this.dataText = dataText;
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public int getLabelColor() {
            return labelColor;
        }

        public void setLabelColor(int labelColor) {
            this.labelColor = labelColor;
        }

        public int getDataColor() {
            return dataColor;
        }

        public void setDataColor(int dataColor) {
            this.dataColor = dataColor;
        }

        public int getMessageColor() {
            return messageColor;
        }

        public void setMessageColor(int messageColor) {
            this.messageColor = messageColor;
        }

        public int getDataBackground() {
            return dataBackground;
        }

        public void setDataBackground(int dataBackground) {
            this.dataBackground = dataBackground;
        }

        public String getDataDrawable() {
            return dataDrawable;
        }

        public void setDataDrawable(String dataDrawable) {
            this.dataDrawable = dataDrawable;
        }

        @Override
        public String toString() {
            return "ItemToDisplay{" +
                    "labelText='" + labelText + '\'' +
                    ", dataText='" + dataText + '\'' +
                    ", messageText='" + messageText + '\'' +
                    ", labelColor=" + labelColor +
                    ", dataColor=" + dataColor +
                    ", messageColor=" + messageColor +
                    ", dataBackground=" + dataBackground +
                    ", dataDrawable='" + dataDrawable + '\'' +
                    '}';
        }
    }
}