package com.carecloud.carepaylibray.constants;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016.
 */
public class CarePayConstants {

    // All Shared Preferences key Constants
    public static final String PREFERENCE_CAREPAY = "Preference_CarePay";

    public static final String PREFERENCE_USER_SELECTED_LANGUAGE = "user_selected_language";

    // Default data

    public static final String DEFAULT_LANGUAGE = "English";

    //Appointments Constants
    public static final String ASSETS_JSON = "workflow.json";
    public static final String APPOINTMENT_JSON = "appointments.json";
    public static final String ATTR_RESPONSE = "response";
    public static final String ATTR_CAPTURE = "capture";
    public static final String ATTR_APPOINTMENTS = "appointments";
    public static final String ATTR_APPT_ID= "appointment_id";
    public static final String ATTR_TIME = "time";
    public static final String DAY_UPCOMING = "UPCOMING";
    public static final String DAY_TODAY = "TODAY";
    public static final String ATTR_PHYSICIAN = "physician";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_UTC = "UTC";

    public static final int SIGNATURE_REQ_CODE = 100;
    public static final String FORM_DATA  = "formdata";
    public static final String CHECKED_IN_APPOINTMENT_BUNDLE  = "Checked_in_appointment_bundle";
    public static final String INTAKE_BUNDLE  = "intake_bundle";
    public static final String COPAY  = "Copay";
    public static final String ACCOUNT  = "Account";
    public static final String DOLLAR  = "$";
    public static final String RESPONSIBILITY_FORMATTER  = "#0.00";

    public static final String PREF_LAST_REMINDER_POPUP_APPT_ID = "last_reminder_popup_appt_id";
    public static final String DEFAULT_STRING_PREFERENCES = "-";
    public static final long APPOINTMENT_REMINDER_TIME_IN_MINUTES = 120;
    public static final long CUSTOM_POPUP_AUTO_DISMISS_DURATION = 5000;

    public static final String TIME_FORMAT_AM_PM = "hh:mm a";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "EEE dd MMM hh:mm a";
    public static final String APPOINTMENT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String RAW_DATE_FORMAT_FOR_TESTS = "yyyy-MM-dd'T'HH:mm:ssX";
}
