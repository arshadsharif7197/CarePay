package com.carecloud.carepay.service.library;

/**
 * Created by Jahirul Bhuiyan on 8/24/2016
 */
public class CarePayConstants {

    private CarePayConstants() {
    }

    // Default data

    public static final String DEFAULT_LANGUAGE = "English";

    // Generic Constants
    public static final int HOME_PRESSED = 999;

    //Appointments Constants
    public static final String PENDING = "P";
    public static final String CHECKED_IN = "I";
    public static final String CANCELLED = "C";
    public static final String REQUESTED = "R";
    public static final String CHECKING_IN = "CI";
    public static final String CHECKED_OUT = "O";
    public static final String CHECKING_OUT = "CO";
    public static final String BILLED = "B";
    public static final String MANUALLY_BILLED = "M";
    public static final String IN_PROGRESS_IN_ROOM = "PR";
    public static final String IN_PROGRESS_OUT_ROOM = "PH";

    public static final String ATTR_APPOINTMENTS = "appointments";
    public static final String ATTR_APPT_ID = "appointment_id";
    public static final String ATTR_TIME = "time";
    public static final String DAY_UPCOMING = "UPCOMING";
    public static final String DAY_TODAY = "TODAY";
    public static final String DAY_OVER = "OVER";
    public static final String ATTR_PHYSICIAN = "physician";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_UTC = "UTC";

    public static final int SIGNATURE_REQ_CODE = 100;
    public static final int NO_INDEX = -1;
    public static final String FORM_DATA = "formdata";
    public static final String CHECKED_IN_APPOINTMENT_BUNDLE = "Checked_in_appointment_bundle";
    public static final String INTAKE_BUNDLE = "intake_bundle";
    public static final String COPAY = "Insurance";
    public static final String INSURANCE_COPAY = "Insurance CoPay";
    public static final String ACCOUNT = "Account";
    public static final String PREVIOUS_BALANCE = "Patient";
    public static final String PATIENT_BALANCE = "Patient Balance";
    public static final String DOLLAR = "$";
    public static final String RESPONSIBILITY_FORMATTER = "#0.00";

    public static final String PREF_LAST_REMINDER_POPUP_APPT_ID = "last_reminder_popup_appt_id";
    public static final String DEFAULT_STRING_PREFERENCES = "-";
    public static final long APPOINTMENT_CANCEL_TIME_IN_MINUTES = 1440;
    public static final long APPOINTMENT_REMINDER_TIME_IN_MINUTES = 120;
    public static final long CUSTOM_POPUP_AUTO_DISMISS_DURATION = 5000;

    public static final String PRACTICE_SELECTION_BUNDLE = "practice_selection_bundle";
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
    public static final int MAX_INSURANCE_DOC = 3;

    public static final String RAW_DATE_FORMAT_FOR_TESTS = "yyyy-MM-dd'T'HH:mm:ssX";
    public static final String RAW_DATE_FORMAT_FOR_CALENDAR_DATE_RANGE = "EEE, MMM d, ''yy";
    public static final String NOT_DEFINED = "Not Defined";

    public static final String PRACTICE_APP_MODE_DEFAULT_PIN = "1234";
    public static final String ZERO_BALANCE = "$0.00";

    public static final String TYPE_CASH = "cash";
    public static final String TYPE_CREDIT_CARD = "credit_card";
    public static final String TYPE_CHECK = "check";
    public static final String TYPE_GIFT_CARD = "gift_card";
    public static final String TYPE_PAYPAL = "paypal";
    public static final String TYPE_HSA = "hsa";
    public static final String TYPE_FSA = "fsa";
    public static final String TYPE_ANDROID_PAY = "android_pay";
    public static final String TYPE_PAYMENT_PLAN = "pay_using_payment_plan";

    public static final int PAYMENT_PLAN_REQUIRED_BALANCE = 20;
    public static final String APPOINTMENTS_STATUS_COMPLETED = "completed";

    public static final String DEMOGRAPHICS_SETTINGS_BUNDLE = "demographics_settings_bundle";
    public static final String DEMOGRAPHICS_ADDRESS_BUNDLE = "demographics_address_bundle";
    public static final String PAYEEZY_MERCHANT_SERVICE_BUNDLE = "payeezy_merchant_service_bundle";
    public static final String CREDIT_CARD_BUNDLE = "credit_card_bundle";

    public static final int ANDROID_PAY_BUTTON_HEIGHT = 58;
    public static final int NAVIGATION_ITEM_INDEX_APPOINTMENTS = 0;
    public static final int NAVIGATION_ITEM_INDEX_PAYMENTS = 1;
    public static final int NAVIGATION_ITEM_INDEX_PURCHASE = 2;
    public static final int NAVIGATION_ITEM_INDEX_NOTIFICATION = 3;


    public static final String MEDICATION_ALLERGIES_DTO_EXTRA = "medication_allergies_payload_extra";
    public static final String MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA = "medication_allergies_search_mode_extra";

    //Clover Constants
    public static final String CLOVER_DEVICE = "Clover";
    public static final String CLOVER_PAYMENT_INTENT = "com.carecloud.carepay.practice.clover.payments.CloverPaymentActivity";
    public static final String CLOVER_REFUND_INTENT = "com.carecloud.carepay.practice.clover.payments.CloverRefundActivity";
    public static final int CLOVER_PAYMENT_INTENT_REQUEST_CODE = 0x37;
    public static final String CLOVER_PAYMENT_AMOUNT = "clover_payment_amount";
    public static final String CLOVER_PAYMENT_METADATA = "clover_payment_metadata";
    public static final String CLOVER_PAYMENT_TRANSITION = "clover_payment_transition";
    public static final String CLOVER_PAYMENT_LINE_ITEMS = "clover_payment_line_items";
    public static final String CLOVER_PAYMENT_POST_MODEL = "clover_payment_post_model";
    public static final String CLOVER_PAYMENT_SUCCESS_INTENT_DATA = "clover_payment_success_intent_data";
    public static final String CLOVER_PAYMENT_TRANSACTION_RESPONSE = "clover_payment_transaction_response";
    public static final String CLOVER_PAYMENT_HISTORY_ITEM = "clover_payment_history_item";
    public static final String CLOVER_REFUND_INTENT_FLAG = "clover_refund_intent_flag";

    public static final String CLOVER_QUEUE_PAYMENT_TRANSITION = "clover_queue_payment_transition";
    public static final int PAYMENT_RETRY_PENDING_RESULT_CODE = 0x777;
    public static final int REFUND_RETRY_PENDING_RESULT_CODE = 0x888;

    //Shared Preference Keys
    public static final String KEY_PRACTICE_PATIENT_IDS = "practice_patient_ids";

    public static final String CONNECTION_ISSUE_ERROR_MESSAGE = "<b>Connection issue.</b> There was a problem with your request. Please try again later.";
    public static final String INVALID_LOGIN_ERROR_MESSAGE = "<b>Sign-in failed.</b> Invalid user id or password.";

    //FCM
    public static final String FCM_TOKEN = "fcmToken";
    public static final String PUSH_NOTIFICATION_ENABLED = "pushNotificationEnabled";

    //Checkout & Check-in
    public static final String APPOINTMENT_ID = "appointmentId";
    public static final String EXTRA_WORKFLOW = "workflow";
    public static final String EXTRA_HAS_PAYMENT = "hasPayment";
    public static final String EXTRA_APPOINTMENT_TRANSITIONS = "appointmentWorkflow";
    public static final String EXTRA_BUNDLE = "extra";


    public static final String OPEN_NOTIFICATIONS = "shouldOpenNotifications";

    //AdHoc
    public static final String ADHOC_FORMS = "adhocForms";

    //Patient Mode Login
    public static final String LOGIN_OPTION_QR = "login_option_qr";
    public static final String LOGIN_OPTION_SEARCH = "login_option_search";

    public static final String CRASH = "crash";

}
