package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.FormData;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinDemographicsRevFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IFragmentCallback;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormMetadataDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */
public class PatientModeCheckinActivity extends BasePracticeActivity implements IFragmentCallback,
                                                                                DemographicsReviewLabelsHolder {

    private static final int HANDLE_GENERIC_FRAGMENT = 0;
    private static final int HANDLE_CHECKIN_INTAKE_FORM1_FRAGMENT = 1;
    private static final int HANDLE_CHECKIN_DEMOGRAPHICS_REV_FRAGMENT = 2;
    private static final int HANDLE_RESPONSIBILITY_FRAGMENT = 3;
    private static final int HANDLE_CHECKIN_CONSENT_FORM_FRAGMENT = 4;
    private static final int HANDLE_NEXT_CHECKIN_CONSENT_FORM_FRAGMENT = 5;

    public final static  int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static  int SUBFLOW_CONSENT          = 1;
    public final static  int SUBFLOW_INTAKE           = 2;
    public final static  int SUBFLOW_PAYMENTS         = 3;
    private static final int NUM_OF_SUBFLOWS          = 4;
    private              int numIntakeForms           = 3;
    private static final int numConsentForms          = 3;

    private DemographicDTO  demographicDTO;
    private CarePayTextView backButton;
    private ImageView       logoImageView;
    private ImageView       homeClickable;

    private View[] sectionTitleTextViews;
    private TextView consentCounterTextView;
    private TextView intakeCounterTextView;

    // Using for consent form
    // TODO : Will be create separate fragment in next sprint
    private ConsentFormLabelsDTO consentFormLabelsDTO;

    private AppointmentsPayloadDTO  appointmentsPayloadDTO;

    private ConsentFormDTO consentFormDTO;
    private FormId showingForm = FormId.FORM1;
    private String readCarefullySign;
    private String medicareDescription;
    private String authorizationDescription1;
    private String authorizationDescription2;

    private int consentFormIndex;
    private int intakeFormIndex = 1;


    // Intake Form
    private IntakeResponseModel intakeResponseModel;
    /**
     * IntakeForm Navigation
     */

    BroadcastReceiver intakeFormReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("INTAKE_WORKFLOW")) {
                intakeResponseModel = getConvertedDTO(IntakeResponseModel.class, intent.getStringExtra("INTAKE_WORKFLOW"));
            }
            Message message = handler.obtainMessage(HANDLE_CHECKIN_INTAKE_FORM1_FRAGMENT, intakeResponseModel);
            handler.sendMessage(message);
            // TODO: SAUL Create Intake Fragment
            //navigateToFragment("INTAKEFRAGMENT", false);
        }
    };
    private FlowStateInfo currentFlowStateInfo;

    private static class MyHandler extends Handler {
        private final WeakReference<PatientModeCheckinActivity> weakActivity;

        MyHandler(PatientModeCheckinActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            PatientModeCheckinActivity activity = weakActivity.get();
            if (null == activity) {
                return;
            }

            Bundle bundle = new Bundle();
            switch (message.what) {
                case HANDLE_CHECKIN_INTAKE_FORM1_FRAGMENT:
                    CheckinIntakeForm1Fragment checkinIntakeForm1Fragment = new CheckinIntakeForm1Fragment();
                    Gson gson = new Gson();
                    String intakeFormDTO = gson.toJson(message.obj);
                    bundle.putString(CarePayConstants.INTAKE_BUNDLE, intakeFormDTO);
                    checkinIntakeForm1Fragment.setArguments(bundle);
                    navigateToFragment(activity, checkinIntakeForm1Fragment, true);
                    break;
                case HANDLE_CHECKIN_DEMOGRAPHICS_REV_FRAGMENT:
                    CheckinDemographicsRevFragment checkinDemographicsRevFragment = new CheckinDemographicsRevFragment();
                    navigateToFragment(activity, checkinDemographicsRevFragment, false);
                    break;
                case HANDLE_RESPONSIBILITY_FRAGMENT:
                    String workflowJson = (String) message.obj;
                    ResponsibilityFragment responsibilityFragment = new ResponsibilityFragment();
                    bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowJson);
                    bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, workflowJson);
                    responsibilityFragment.setArguments(bundle);
                    navigateToFragment(activity, responsibilityFragment, true);
                    break;
                case HANDLE_CHECKIN_CONSENT_FORM_FRAGMENT:
                    Fragment consentForm = getConsentForm(activity, (String) message.obj);
                    navigateToFragment(activity, consentForm, true);
                    break;
                case HANDLE_NEXT_CHECKIN_CONSENT_FORM_FRAGMENT:
                    Fragment nextConsentForm = getNextConsentForm(activity);
                    if (nextConsentForm != null) {
                        navigateToFragment(activity, nextConsentForm, true);
                    }
                    break;
                default:
                    Fragment fragment = (Fragment) message.obj;
                    boolean addToBackStack = message.what == 1;
                    navigateToFragment(activity, fragment, addToBackStack);
                    break;
            }
        }

        /**
         * Helper method to replace fragments
         *
         * @param fragment       The fragment
         * @param addToBackStack Whether to add the transaction to back-stack
         */
        void navigateToFragment(PatientModeCheckinActivity activity, Fragment fragment, boolean addToBackStack) {
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.checkInContentHolderId, fragment, fragment.getClass().getSimpleName());
            if (addToBackStack) {
                transaction.addToBackStack(fragment.getClass().getName());
            }
            transaction.commitAllowingStateLoss();
        }

        private Fragment getNextConsentForm(PatientModeCheckinActivity activity) {
            activity.showingForm = activity.showingForm.next();
            if (activity.showingForm != FormId.NONE) {
                return getConsentForm(activity, "");
            }
            return null;
        }

        private Fragment getConsentForm(PatientModeCheckinActivity activity, String jsonResponse) {

            if (activity.showingForm == FormId.FORM1) {
                activity.consentFormIndex = 1;
                Bundle bundle = new Bundle();
                bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData(activity, "form1"));
                bundle.putString(CarePayConstants.INTAKE_BUNDLE, jsonResponse);
                CheckinConsentForm1Fragment consentForm1Fragment = new CheckinConsentForm1Fragment();
                consentForm1Fragment.setArguments(bundle);
                return consentForm1Fragment;
            } else if (activity.showingForm == FormId.FORM2) {
                activity.consentFormIndex = 2;
                Bundle bundle = new Bundle();
                bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData(activity, "form2"));
                CheckinConsentForm2Fragment consentForm2Fragment = new CheckinConsentForm2Fragment();
                consentForm2Fragment.setArguments(bundle);
                return consentForm2Fragment;
            } else {
                activity.consentFormIndex = 3;
                Bundle bundle = new Bundle();
                bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData(activity, "form3"));
                CheckinConsentForm1Fragment consentForm1Fragment = new CheckinConsentForm1Fragment();
                consentForm1Fragment.setArguments(bundle);
                return consentForm1Fragment;
            }
        }

        private FormData getConsentFormData(PatientModeCheckinActivity activity, String formName) {
            FormData formData = new FormData();
            DateUtil.getInstance().setToCurrent(); // set the date to current
            switch (formName) {
                case "form1":
                    formData.setTitle(activity.consentFormLabelsDTO.getConsentForMedicareTitle());
                    formData.setDescription(activity.readCarefullySign);
                    formData.setContent(activity.medicareDescription);
                    formData.setButtonLabel(activity.consentFormLabelsDTO.getSignFormButton());
                    formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
                    break;
                case "form2":
                    formData.setTitle(activity.consentFormLabelsDTO.getAuthorizationFormTitle());
                    formData.setDescription(activity.readCarefullySign);
                    formData.setContent(activity.authorizationDescription1);
                    formData.setContent2(activity.authorizationDescription2);
                    formData.setButtonLabel(activity.consentFormLabelsDTO.getSignFormButton());
                    formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
                    break;
                default:  //form3
                    formData.setTitle(activity.consentFormLabelsDTO.getHipaaAgreementTitle());
                    formData.setDescription(activity.readCarefullySign);
                    formData.setContent(activity.consentFormLabelsDTO.getHipaaConfidentialityAgreementText());
                    formData.setButtonLabel(activity.consentFormLabelsDTO.getSignFormButton());
                    formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
                    break;
            }

            return formData;

        }
    }

    private final MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);
        demographicDTO = getConvertedDTO(DemographicDTO.class);

        setNavigationBarVisibility();
        instantiateViewsRefs();
        initializeViews();
        // place the initial fragment
        handler.sendEmptyMessage(HANDLE_CHECKIN_DEMOGRAPHICS_REV_FRAGMENT);

        // Intake form Navigation TODO: will be managed by fragment
        registerReceiver(intakeFormReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(intakeFormReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void instantiateViewsRefs() {
        backButton = (CarePayTextView) findViewById(R.id.checkinBack);
        logoImageView = (ImageView) findViewById(R.id.checkinLogo);
        homeClickable = (ImageView) findViewById(R.id.checkinHomeClickable);

        sectionTitleTextViews = new View[NUM_OF_SUBFLOWS];
        sectionTitleTextViews[SUBFLOW_DEMOGRAPHICS_INS] = findViewById(R.id.checkinSectionDemogrInsTitle);
        sectionTitleTextViews[SUBFLOW_CONSENT] = findViewById(R.id.checkinSectionConsentFormsTitle);
        sectionTitleTextViews[SUBFLOW_INTAKE] = findViewById(R.id.checkinSectionIntakeFormsTitle);
        sectionTitleTextViews[SUBFLOW_PAYMENTS] = findViewById(R.id.checkinSectionPaymentsTitle);

        consentCounterTextView = (TextView) findViewById(R.id.checkinConsentFormCounterTextView);
        intakeCounterTextView = (TextView) findViewById(R.id.checkinIntakeFormCounterTextView);
    }

    private void initializeViews() {
        toggleVisibleBackButton(false);
        toggleVisibleFormCounter(SUBFLOW_CONSENT, false);
        toggleVisibleFormCounter(SUBFLOW_INTAKE, false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        homeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientModeCheckinActivity.this.finish();
            }
        });
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(final Fragment fragment, final boolean addToBackStack) {
        Message message = handler.obtainMessage(HANDLE_GENERIC_FRAGMENT, addToBackStack ? 1 : 0, 0, fragment);
        handler.sendMessage(message);
    }

    /**
     * Getter
     *
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }

    /**
     * Re-sets the global DTO from a string JSON
     *
     * @param jsonString The main DTO as string
     */
    public void resetDemographicDTO(String jsonString) {
        this.demographicDTO = getConvertedDTO(DemographicDTO.class, jsonString);
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        return demographicDTO.getMetadata().getLabels();
    }

    /**
     * Consent form
     */
    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, PracticeAppSignatureActivity.class);

        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
        intent.putExtra("consentform", showingForm);
        if (PracticeAppSignatureActivity.numOfLaunches == 2) {
            // pass the whole DTO
            Gson gson = new Gson();
            String consentformDTO = gson.toJson(consentFormDTO);
            intent.putExtra("consentform_model", consentformDTO);
        }
        if (showingForm == FormId.FORM1) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getConsentReadCarefullyWarning());
        } else if (showingForm == FormId.FORM2) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
        } else {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignHipaaAgreementTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
        }
        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CarePayConstants.SIGNATURE_REQ_CODE) {
            if (PracticeAppSignatureActivity.isBackButtonClicked) {
                PracticeAppSignatureActivity.isBackButtonClicked = false;
            } else {
                handler.sendEmptyMessage(HANDLE_NEXT_CHECKIN_CONSENT_FORM_FRAGMENT);
            }

        }
    }

    public int getNumIntakeForms() {
        return numIntakeForms;
    }

    public void setNumIntakeForms(int numIntakeForms) {
        this.numIntakeForms = numIntakeForms;
    }

    public int getNumConsentForms() {
        return numConsentForms;
    }

    ////////////////////////////
    // Consent form framework //
    ////////////////////////////

    /**
     * Consent form navigation
     *
     * @param workflowJson consent DTO
     */
    public void getConsentFormInformation(String workflowJson) {
        consentFormDTO = getConvertedDTO(ConsentFormDTO.class, workflowJson);
        if (consentFormDTO != null) {
            ConsentFormPayloadDTO consentFormPayloadDTO = consentFormDTO.getConsentFormPayloadDTO();
            ConsentFormAppointmentsPayloadDTO consentFormAppointmentsPayloadDTO = consentFormPayloadDTO.getConsentFormAppointmentPayload().get(0);

            ConsentFormMetadataDTO consentFormMetadataDTO = consentFormDTO.getMetadata();
            if (consentFormMetadataDTO != null) {
                consentFormLabelsDTO = consentFormMetadataDTO.getLabel();

                if (consentFormLabelsDTO != null) {
                    medicareDescription = consentFormLabelsDTO.getConsentForMedicareText();
                    readCarefullySign = consentFormLabelsDTO.getConsentReadCarefullyWarning();
                    authorizationDescription1 = consentFormLabelsDTO.getAuthorizationGrantText();
                    authorizationDescription2 = consentFormLabelsDTO.getAuthorizationLegalText();

                    Message message = handler.obtainMessage(HANDLE_CHECKIN_CONSENT_FORM_FRAGMENT, workflowJson);
                    handler.sendMessage(message);

                    Log.d(this.getClass().getSimpleName(), "consent form information");
                }
            }
        }
    }


    /**
     * Consent form navigation
     *
     * @param workflowJson intake DTO
     */
    public void getPaymentInformation(String workflowJson) {
        Message message = handler.obtainMessage(HANDLE_RESPONSIBILITY_FRAGMENT, workflowJson);
        handler.sendMessage(message);
    }

    public ConsentFormLabelsDTO getConsentFormLabelsDTO() {
        return consentFormLabelsDTO;
    }

    public ConsentFormDTO getConsentFormDTO() {
        return consentFormDTO;
    }

    public AppointmentsPayloadDTO getAppointmentsPayloadDTO() {
        return appointmentsPayloadDTO;
    }

    /**
     * Enum to identify the forms
     */
    public enum FormId {
        FORM1, FORM2, FORM3, NONE;

        /**
         * Go to next
         */
        public FormId next() {
            if (this.equals(FORM1)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM3;
            }
            return NONE;
        }

        /**
         * Go to prev
         */
        public FormId prev() {
            if (this.equals(FORM3)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM1;
            }
            return NONE;
        }
    }

    //////////////////////////////
    // navigation panel framework //
    //////////////////////////////

    /**
     * Highlights the title of the current subflow
     *
     * @param subflow   The subflow id:
     *                  (0 - demographics & insurance, 1 - consent, 2 - intake, 3 - payments)
     * @param highlight Whether is hightlight
     */
    public void toggleHighlight(int subflow, boolean highlight) {
        // if highlight true, highlight current and reset previous if there is one
        TextView currentSection = (TextView) sectionTitleTextViews[subflow];
        if (highlight) {
            SystemUtil.setGothamRoundedBoldTypeface(this, currentSection);
            currentSection.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else { // if highlight false, reset current and hightlight previous if there is one
            SystemUtil.setGothamRoundedLightTypeface(this, currentSection);
            currentSection.setTextColor(ContextCompat.getColor(this, R.color.white_opacity_60));
        }
    }

    /**
     * Increments/decrements the counter of the current form in a specific sub-flow
     * (0 - demographics & insurance, 1 - consent, 2 - intake, 3 - payments)
     *
     * @param formSubflow The sub-flow id
     * @param formIndex   The index
     * @param maxIndex    The num of forms
     */
    public void changeCounterOfForm(int formSubflow, int formIndex, int maxIndex) {
        // if increment true, increment
        TextView formCounterTextView = null;
        if (formSubflow == SUBFLOW_CONSENT) {
            formCounterTextView = consentCounterTextView;
        } else if (formSubflow == SUBFLOW_INTAKE) { // intake forms
            formCounterTextView = intakeCounterTextView;
        }
        // format the string
        String preposition = "of";
        String counterString = String.format(Locale.getDefault(), "%d %s %d", formIndex, preposition, maxIndex);
        formCounterTextView.setText(counterString);
    }

    /**
     * Toggle visible the form counter
     *
     * @param formSubflow THe form
     * @param visible     Whether visible
     */
    public void toggleVisibleFormCounter(int formSubflow, boolean visible) {
        TextView formCounterTextView = null;
        if (formSubflow == SUBFLOW_CONSENT) {
            formCounterTextView = consentCounterTextView;
        } else if (formSubflow == SUBFLOW_INTAKE) { // intake forms
            formCounterTextView = intakeCounterTextView;
        }
        formCounterTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Toogle visible the back button (and hides/shows accordingly the logo)
     *
     * @param isVisible Whether to toggle visible
     */
    public void toggleVisibleBackButton(boolean isVisible) {
        if (isVisible) {
            backButton.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update a section
     *
     * @param stateInfo The state of the flow
     */
    public void updateSection(FlowStateInfo stateInfo) {
        if (currentFlowStateInfo != null) {
            // reset the current state
            int subflow = currentFlowStateInfo.getSubflow();
            toggleHighlight(subflow, false);
            if (subflow == SUBFLOW_CONSENT || subflow == SUBFLOW_INTAKE) {
                toggleVisibleFormCounter(subflow, false);
            }
        }
        currentFlowStateInfo = stateInfo;
        if (currentFlowStateInfo != null) {
            // set the current state
            int subflow = currentFlowStateInfo.getSubflow();
            toggleHighlight(subflow, true);
            if (subflow == SUBFLOW_CONSENT || subflow == SUBFLOW_INTAKE) {
                toggleVisibleFormCounter(subflow, true);
                changeCounterOfForm(subflow, currentFlowStateInfo.fragmentIndex, currentFlowStateInfo.getMaxFragIndex());
            }
        }
    }

    public int getConsentFormIndex() {
        return consentFormIndex;
    }

    public void setConsentFormIndex(int consentFormIndex) {
        this.consentFormIndex = consentFormIndex;
    }

    public int getIntakeFormIndex() {
        return intakeFormIndex;
    }

    public void setIntakeFormIndex(int intakeFormIndex) {
        this.intakeFormIndex = intakeFormIndex;
    }

    @Override
    public void onBackPressed() {
        BaseCheckinFragment fragment = (BaseCheckinFragment) getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        currentFlowStateInfo = fragment.getFlowStateInfo();

        if (currentFlowStateInfo != null) {
            if (currentFlowStateInfo.getSubflow() == SUBFLOW_CONSENT) {
                Log.v("back", "consent: " + currentFlowStateInfo.fragmentIndex);
                consentFormIndex = currentFlowStateInfo.fragmentIndex;

                if (consentFormIndex == 1) {
                    showingForm = FormId.FORM1;
                } else {
                    showingForm = showingForm.prev();
                }
                super.onBackPressed();
            } else if (currentFlowStateInfo.getSubflow() == SUBFLOW_INTAKE) {
                Log.v("back", "intake: " + currentFlowStateInfo.fragmentIndex);
                currentFlowStateInfo.fragmentIndex = --intakeFormIndex;
                updateSection(currentFlowStateInfo);

                if (intakeFormIndex == 0) {
                    intakeFormIndex = 1;
                    super.onBackPressed();
                } else {
                    getFragmentManager().popBackStack();
                    CheckinIntakeForm1Fragment intakeForm1Fragment = (CheckinIntakeForm1Fragment)
                            getSupportFragmentManager().findFragmentByTag(CheckinIntakeForm1Fragment.class.getSimpleName());

                    if (intakeForm1Fragment != null) {
                        //intakeForm1Fragment.previousIntakeForm();
                    }
                }
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }



    /**
     * Launch intake forms
     * @param workflowJson workflowJson
     *
     */
    public void startIntakeForms(String workflowJson) {
        Intent intent = new Intent();
        intent.setAction("NEW_CHECKEDIN_NOTIFICATION");
        intent.putExtra("INTAKE_WORKFLOW", workflowJson);
        sendBroadcast(intent);
    }
}
