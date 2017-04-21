package com.carecloud.carepaylibray.utils;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibrary.BuildConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lmenendez on 4/13/17.
 */

public class MixPanelUtil {

    public static boolean isDebug = BuildConfig.DEBUG;

    private static MixpanelAPI mixpanel = HttpConstants.getMixpanelAPI();

    /**
     * Utility to log simple event to mixpanel
     * @param event event name
     */
    public static void logEvent(String event){
        if(!isDebug) {
            mixpanel.track(event);
        }
    }

    /**
     * Utility to log event with parameter to mixpanel
     * @param eventName event name
     * @param parameter parameter name
     * @param value parameter value
     */
    public static void logEvents(String eventName, String parameter, Object value){
        try {
            JSONObject object = new JSONObject();
            object.put(parameter, value);
            if(!isDebug) {
                mixpanel.track(eventName, object);
            }
        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    /**
     * Utility to log event with multiple parameters to mixpanel
     * @param eventName event name
     * @param parameters list of parameter names
     * @param values list of parameter values
     */
    public static void logEvents(String eventName, String[] parameters, Object[] values) {
        try {
            JSONObject object = new JSONObject();
            for(int i=0; i<parameters.length; i++) {
                object.put(parameters[i], values[i]);
            }
            if(!isDebug) {
                mixpanel.track(eventName, object);
            }
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    /**
     * Utility to increment and track global property
     * @param property property name
     * @param value property value
     */
    public static void incrementPeopleProperty(String property, double value){
        if(!isDebug) {
            mixpanel.getPeople().increment(property, value);
        }
    }

    /**
     * Utility to track user in mixpanel people
     * @param signInResponse siginin dto
     * @param deviceID device token
     */
    public static void setUser(UnifiedSignInResponse signInResponse, String deviceID){
        if(!isDebug) {
            mixpanel.identify(signInResponse.getPayload().getSignIn().getMetadata().getUserId()+"");
//            MixpanelAPI.People people = mixpanelAPI.getPeople();
            mixpanel.getPeople().identify(signInResponse.getPayload().getSignIn().getMetadata().getUserId()+"");
//            mixpanelAPI.getPeople().setPushRegistrationId(deviceID);
            mixpanel.getPeople().set("$name", signInResponse.getPayload().getSignIn().getMetadata().getUsername()+"");
//            mixpanel.getPeople().set("$email", user.getEmail());
//            mixpanel.getPeople().set("$phone", user.getPhoneNumber());
            mixpanel.getPeople().set("Patient ID", signInResponse.getPayload().getSignIn().getMetadata().getPatientId()+"");
            mixpanel.getPeople().set("Practice ID", signInResponse.getPayload().getSignIn().getMetadata().getPracticeId()+"");
            mixpanel.getPeople().set("Practice Mgmt", signInResponse.getPayload().getSignIn().getMetadata().getPracticeMgmt()+"");
        }
    }


}
