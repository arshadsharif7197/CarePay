package com.carecloud.carepaylibray.utils;

import android.content.Context;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibrary.BuildConfig;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lmenendez on 4/13/17
 */

public class MixPanelUtil {

    private static boolean isDebug = BuildConfig.DEBUG;

    private static MixpanelAPI mixpanel = HttpConstants.getMixpanelAPI();

    /**
     * Utility to log simple event to mixpanel
     * @param event event name
     */
    public static void logEvent(String event){
        if(!isDebug && mixpanel!=null) {
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
            if(!isDebug && mixpanel!=null) {
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
            if(!isDebug && mixpanel!=null) {
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
        if(!isDebug && mixpanel!=null) {
            mixpanel.getPeople().increment(property, value);
        }
    }

    /**
     * Utility to track user in mixpanel people
     * @param userId user id
     */
    public static void setUser(Context context, String userId, DemographicPayloadDTO demographics){
        if(!isDebug && mixpanel!=null) {
            mixpanel.identify(userId+"");
//            MixpanelAPI.People people = mixpanelAPI.getPeople();
            mixpanel.getPeople().identify(userId+"");
//            mixpanelAPI.getPeople().setPushRegistrationId(deviceID);
//            mixpanel.getPeople().set("$name", signInResponse.getPayload().getSignIn().getMetadata().getUsername()+"");
//            mixpanel.getPeople().set("$email", user.getEmail());
//            mixpanel.getPeople().set("$phone", user.getPhoneNumber());
            mixpanel.getPeople().set(context.getString(R.string.people_gender), demographics.getPersonalDetails().getGender());
            mixpanel.getPeople().set(context.getString(R.string.people_dob), demographics.getPersonalDetails().getDateOfBirth());
            mixpanel.getPeople().set(context.getString(R.string.people_ethnicity), demographics.getPersonalDetails().getEthnicity());
            mixpanel.getPeople().set(context.getString(R.string.people_race), demographics.getPersonalDetails().getPrimaryRace());
            mixpanel.getPeople().set(context.getString(R.string.people_language), demographics.getPersonalDetails().getPreferredLanguage());
        }
    }


}
