package com.carecloud.carepay.service.library;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016.
 */
public class CarePayConstants {

    // All Shared Preferences key Constants
    public static final String PREFERENCE_CAREPAY = "Preference_CarePay";

    public static final String PREFERENCE_USER_SELECTED_LANGUAGE = "user_selected_language";

    public static final String PREFERENCE_PRACTICE_SELECTED_LANGUAGE = "practice_user_selected_language";


    // Default data

    public static final String DEFAULT_LANGUAGE = "English";

    //Appointments Constants
    public static final String PENDING      = "P";
    public static final String CHECKED_IN   = "I";
    public static final String CANCELLED    = "C";
    public static final String REQUESTED    = "R";

    public static final String ATTR_APPOINTMENTS = "appointments";
    public static final String ATTR_APPT_ID= "appointment_id";
    public static final String ATTR_TIME = "time";
    public static final String DAY_UPCOMING = "UPCOMING";
    public static final String DAY_TODAY = "TODAY";
    public static final String DAY_OVER = "OVER";
    public static final String ATTR_PHYSICIAN = "physician";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_UTC = "UTC";

    public static final int SIGNATURE_REQ_CODE = 100;
    public static final String FORM_DATA  = "formdata";
    public static final String CHECKED_IN_APPOINTMENT_BUNDLE  = "Checked_in_appointment_bundle";
    public static final String INTAKE_BUNDLE  = "intake_bundle";
    public static final String COPAY  = "Insurance";
    public static final String ACCOUNT  = "Account";
    public static final String PREVIOUS_BALANCE  = "Patient";
    public static final String DOLLAR  = "$";
    public static final String RESPONSIBILITY_FORMATTER  = "#0.00";

    public static final String PREF_LAST_REMINDER_POPUP_APPT_ID = "last_reminder_popup_appt_id";
    public static final String DEFAULT_STRING_PREFERENCES = "-";
    public static final long APPOINTMENT_CANCEL_TIME_IN_MINUTES = 1440;
    public static final long APPOINTMENT_REMINDER_TIME_IN_MINUTES = 120;
    public static final long CUSTOM_POPUP_AUTO_DISMISS_DURATION = 5000;

    public static final String PAYMENT_AMOUNT_BUNDLE = "total_amount_pay";
    public static final String PAYMENT_METHOD_BUNDLE = "payment_method";
    public static final String PAYMENT_PAYLOAD_BUNDLE = "payment_payload_bundle";
    public static final String APPOINTMENT_INFO_BUNDLE = "appointment_info_bundle";
    public static final String ADD_APPOINTMENT_BUNDLE = "add_appointment_bundle";
    public static final String ADD_APPOINTMENT_PROVIDERS_BUNDLE = "providers_info_bundle";
    public static final String ADD_APPOINTMENT_VISIT_TYPE_BUNDLE = "visit_type_info_bundle";
    public static final String ADD_APPOINTMENT_AVAILABILITY_BUNDLE = "appointment_availability_bundle";
    public static final String ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE = "appointment_resource_to_schedule_bundle";
    public static final String ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE = "add_appointment_calendar_start_date_bundle";
    public static final String ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE = "add_appointment_calendar_end_date_bundle";
    public static final String ADD_APPOINTMENT_PATIENT_ID = "add_appointment_patient_id";
    public static final String PAYMENT_CREDIT_CARD_INFO = "payment_credit_card_info";
    public static final String TAB_SECTION_NUMBER = "section_number";

    public static final String APPOINTMENT_DATE_TIME_FORMAT            = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String RAW_DATE_FORMAT_FOR_TESTS               = "yyyy-MM-dd'T'HH:mm:ssX";
    public static final String RAW_DATE_FORMAT_FOR_CALENDAR_DATE_RANGE = "EEE, MMM d, ''yy";
    public static final String NOT_DEFINED                             = "Not Defined";

    public static final String APPOINTMENT_FILTER_DATE_FORMAT            = "yyyy-MM-dd";
    public static final String APPOINTMENT_HEADER_DATE_FORMAT            = "MM-dd-yyyy";
    public static final String PRACTICE_APP_MODE_DEFAULT_PIN = "1234";
    public static final String ZERO_BALANCE  = "$0.00";

    public static final String TYPE_CASH  = "cash";
    public static final String TYPE_CREDIT_CARD  = "credit_card";
    public static final String TYPE_CHECK  = "check";
    public static final String TYPE_GIFT_CARD  = "gift_card";
    public static final String TYPE_PAYPAL  = "paypal";
    public static final String TYPE_HSA  = "hsa";
    public static final String TYPE_FSA  = "fsa";
    public static final String TYPE_ANDROID_PAY  = "android_pay";

    public static final int PAYMENT_PLAN_REQUIRED_BALANCE = 20;
    public static final String APPOINTMENTS_STATUS_COMPLETED = "completed";

    public static final String CLOVER_DEVICE = "Clover";
    public static final String DEMOGRAPHICS_SETTINGS_BUNDLE  = "demographics_settings_bundle";

}
