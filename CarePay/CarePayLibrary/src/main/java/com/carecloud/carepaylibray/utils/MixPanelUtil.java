package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.support.annotation.NonNull;

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
    public static void logEvent(String eventName, String parameter, Object value){
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
    public static void logEvent(String eventName, String[] parameters, Object[] values) {
        try {
            int min = Math.min(parameters.length, values.length);
            JSONObject object = new JSONObject();
            for(int i=0; i<min; i++) {
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
            mixpanel.getPeople().identify(userId+"");
            if(demographics != null) {
                mixpanel.getPeople().set(context.getString(R.string.people_gender), demographics.getPersonalDetails().getGender());
                mixpanel.getPeople().set(context.getString(R.string.people_dob), demographics.getPersonalDetails().getDateOfBirth());
                mixpanel.getPeople().set(context.getString(R.string.people_ethnicity), demographics.getPersonalDetails().getEthnicity());
                mixpanel.getPeople().set(context.getString(R.string.people_race), demographics.getPersonalDetails().getPrimaryRace());
                mixpanel.getPeople().set(context.getString(R.string.people_language), demographics.getPersonalDetails().getPreferredLanguage());
            }
        }
    }

    public static void setDemographics(Context context, DemographicPayloadDTO demographics){
        if(!isDebug && mixpanel!=null && demographics != null) {
            mixpanel.getPeople().set(context.getString(R.string.people_gender), demographics.getPersonalDetails().getGender());
            mixpanel.getPeople().set(context.getString(R.string.people_dob), demographics.getPersonalDetails().getDateOfBirth());
            mixpanel.getPeople().set(context.getString(R.string.people_ethnicity), demographics.getPersonalDetails().getEthnicity());
            mixpanel.getPeople().set(context.getString(R.string.people_race), demographics.getPersonalDetails().getPrimaryRace());
            mixpanel.getPeople().set(context.getString(R.string.people_language), demographics.getPersonalDetails().getPreferredLanguage());
        }
    }

    /**
     * Utility to start event timing
     * @param trackingEvent event name
     */
    public static void startTimer(String trackingEvent){
        if(!isDebug && mixpanel!=null) {
            mixpanel.timeEvent(trackingEvent);
        }
    }

    /**
     * Utility to end event timing - Event Start and end names must match exactly
     * @param trackingEvent event name
     */
    public static void endTimer(String trackingEvent){
        if(!isDebug && mixpanel!=null) {
            mixpanel.track(trackingEvent);
        }
    }


    /**
     * Utility to track a custom Property for a user
     * @param property property name
     * @param value value
     */
    public static void addCustomPeopleProperty(String property, Object value) {
        if (!isDebug && mixpanel != null) {
            mixpanel.getPeople().set(property, value);
        }
    }


    /**
     * Utility to track multiple custom properties for a user
     * @param properties array of properties
     * @param values array of values
     */
    public static void addCustomPeopleProperties(@NonNull String[] properties, @NonNull Object[] values){
        if (!isDebug && mixpanel != null) {
            int min = Math.min(properties.length, values.length);
            for(int i = 0; i<min; i++){
                mixpanel.getPeople().set(properties[i], values[i]);
            }
        }
    }
}
