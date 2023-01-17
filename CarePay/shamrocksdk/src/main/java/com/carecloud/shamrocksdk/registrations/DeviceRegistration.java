package com.carecloud.shamrocksdk.registrations;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.carecloud.shamrocksdk.registrations.models.Authentication;
import com.carecloud.shamrocksdk.constants.AppConstants;
import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.registrations.interfaces.AccountInfoCallback;
import com.carecloud.shamrocksdk.registrations.interfaces.RegistrationCallback;
import com.carecloud.shamrocksdk.registrations.models.Device;
import com.carecloud.shamrocksdk.registrations.models.DeviceGroup;
import com.carecloud.shamrocksdk.registrations.models.Registration;
import com.carecloud.shamrocksdk.services.ServiceCallback;
import com.carecloud.shamrocksdk.services.ServiceHelper;
import com.carecloud.shamrocksdk.services.ServiceRequest;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;

/**
 * Handles Registering Clover Device with Shamrock Payments
 */
public class DeviceRegistration {

    /**
     * Register Clover Device on Shamrock Payments. Device should be fully set up with the following required fields
     * <ul>
     * <li>{@link Device#setOrganizationId(String) Organization Id}
     * <li>{@link Device#setSerialNumber(String) Serial Number}
     * <li>{@link Device#setDeviceName(String) Device Name}
     * </ul>
     * The device will be placed in the default group unless otherwise specified by also setting
     * <ul><li>{@link Device#setDeviceGroup(DeviceGroup) Device Group}</ul>
     *
     * @param device Device Object to register with Shamrock Payments
     * @param registrationCallback Device registration callback
     * @param context Context
     */
    public static void register(Device device, RegistrationCallback registrationCallback, Context context) {
        registrationCallback.onPreExecute();

        try {
            Map<String, String> header = new HashMap<>();
            header.put("transition", "true");
            ServiceHelper serviceHelper = new ServiceHelper();
            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setUrl(HttpConstants.getAPIBaseURL() + "device_registration");
            serviceRequest.setMethod(ServiceRequest.METHOD_POST);

            Gson gson = new Gson();
            String jsonString = gson.toJson(device);
            Map<String, String> queryMap = new HashMap<>();
            serviceHelper.execute(serviceRequest, registrationCallback(registrationCallback, context ), jsonString, queryMap, header);
        } catch (Exception e) {
            registrationCallback.onFailure("Registration Failed, Please retry");
            e.printStackTrace();
        }

    }

    /**
     * Get device Clover Account info for the Device
     * @param context context
     * @param accountInfoCallback account info callback to receive device info when ready
     */
    public static void getAccountInfo(Context context, AccountInfoCallback accountInfoCallback){
        Account account = CloverAccount.getAccount(context);
        authenticateCloverAccount(context, account, accountInfoCallback);
    }

    private static ServiceCallback registrationCallback(final RegistrationCallback callback, final Context context) {

        return new ServiceCallback() {
            @Override
            public void onPreExecute() {
            }


            @Override
            public void onPostExecute(JsonElement jsonElement) {

                Gson gson=new Gson();
                Registration registrationResult = gson.fromJson(jsonElement, Registration.class);
                Authentication deepStreamAUth = gson.fromJson(jsonElement, Authentication.class);

                callback.onPostExecute(registrationResult);

                setAuthToken(deepStreamAUth.getDeepStreamAuthToken(), context);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                callback.onFailure(exceptionMessage);
            }

        };
    }

    private static void setAuthToken(String authToken, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstants.DEEPSTREAM_AUTH_KEY, authToken);
        editor.commit();
    }


    private static void authenticateCloverAccount(final Context context, Account account, final AccountInfoCallback accountInfoCallback){
        new AsyncTask<Account, Integer, CloverAuth.AuthResult>(){

            @Override
            protected CloverAuth.AuthResult doInBackground(Account... accounts) {
                CloverAuth.AuthResult authResult = null;
                try {
                    authResult = CloverAuth.authenticate(context, accounts[0]);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return authResult;
            }

            @Override
            protected void onPostExecute(CloverAuth.AuthResult authResult){
                if(authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                    accountInfoCallback.onRetrieveAccountInfo(authResult);
                    accountInfoCallback.onRetrieveAppId(authResult.appId);
                    accountInfoCallback.onRetrieveAuthToken(authResult.authToken);
                    accountInfoCallback.onRetrieveMerchantId(authResult.merchantId);
                }else{
                    accountInfoCallback.onAccountConnectionFailure("Failed to authenticate Clover Account");
                }
            }

        }.execute(account);
    }
}
