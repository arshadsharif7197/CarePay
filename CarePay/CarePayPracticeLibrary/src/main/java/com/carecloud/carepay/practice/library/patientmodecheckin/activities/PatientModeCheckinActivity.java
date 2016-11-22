package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.ConsentForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.ConsentForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.FormData;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.SignatureActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinDemographicsRevFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinInsurancesSummaryFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinPaymentFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IFragmentCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormMetadataDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppoPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Locale;


/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */

public class PatientModeCheckinActivity extends BasePracticeActivity implements IFragmentCallback {

    private static final int NUM_OF_SUBFLOWS   = 4;
    public static final  int NUM_CONSENT_FORMS = 2;
    public static final  int NUM_INTAKE_FORMS  = 2;

    private DemographicDTO  demographicDTO;
    private CarePayTextView backButton;
    private ImageView       logoImageView;
    private ImageView       homeClickable;

    public final static int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static int SUBFLOW_CONSENT          = 1;
    public final static int SUBFLOW_INTAKE           = 2;
    public final static int SUBFLOW_PAYMENTS         = 3;
    private View[] sectionTitleTextViews;
    private String preposition = "of";
    private TextView consentCounterTextView;
    private TextView intakeCounterTextView;

    // Using for consent form
    // TODO : Will be create separate fragment in next sprint
    private ConsentFormLabelsDTO consentFormLabelsDTO;

    private AppointmentsPayloadDTO appointmentsPayloadDTO;
    private AppointmentsResultModel appointmentsResultModel;

    private String signMedicareLabel;
    private ConsentFormDTO consentFormDTO;
    private TextView title;
    private FormId showingForm = FormId.FORM1;
    private View indicator0;
    private View indicator1;
    private View indicator2;
    private String readCarefullySign;
    private String medicareDescription;
    private String medicareForm;
    private String providerName = " ";
    private String patientFirstName = " ";
    private String patientLastName = " ";
    private String authorizationDescription1;
    private String authorizationDescription2;
    private String authForm;


    // Intake Form
    private IntakeResponseModel inTakeForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);

        demographicDTO = getConvertedDTO(DemographicDTO.class);

        instantiateViewsRefs();

        initializeViews();

        // place the initial fragment
        CheckinDemographicsRevFragment fragment = new CheckinDemographicsRevFragment();
        navigateToFragment(fragment, false);
        toggleHighlight(SUBFLOW_DEMOGRAPHICS_INS, true);

        // Intake form Navigation TODO: will be managed by fragment
        registerReceiver(intakeFormReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(intakeFormReceiver);
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
            public void onClick(View v) {
                onBackPressed();
            }
        });

        homeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.checkInContentHolderId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    /**
     * Getter
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
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
     * Highlights the title of the current subflow
     *
     * @param subflow   The subflow id:
     *                  (0 - demographics & insurance, 1 - consent, 2 - intake, 3 - payments)
     * @param highlight Whether is hightlight
     */
    public void toggleHighlight(int subflow, boolean highlight) {
        // limit case
        if (subflow == SUBFLOW_DEMOGRAPHICS_INS && !highlight) { // can't go before 'demographics'
            return;
        }
        // if highlight true, highlight current and reset previous if there is one
        TextView currentSection = (TextView) sectionTitleTextViews[subflow];
        TextView prevSection = subflow > SUBFLOW_DEMOGRAPHICS_INS ? (TextView) sectionTitleTextViews[subflow - 1] : null;
        if (highlight) {
            SystemUtil.setGothamRoundedBoldTypeface(this, currentSection);
            currentSection.setTextColor(ContextCompat.getColor(this, R.color.white));
            if (prevSection != null) {
                SystemUtil.setGothamRoundedLightTypeface(this, prevSection);
                prevSection.setTextColor(ContextCompat.getColor(this, R.color.white_opacity_60));
            }
        } else { // if highlight false, reset current and hightlight previous if there is one
            if (prevSection != null) {
                SystemUtil.setGothamRoundedLightTypeface(this, currentSection);
                currentSection.setTextColor(ContextCompat.getColor(this, R.color.white_opacity_60));
                SystemUtil.setGothamRoundedBoldTypeface(this, prevSection);
                prevSection.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
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

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        if (currentFragment instanceof CheckinPaymentFragment) {
            toggleHighlight(SUBFLOW_PAYMENTS, false);
            toggleVisibleFormCounter(SUBFLOW_INTAKE, true);
        } else if (currentFragment instanceof CheckinIntakeForm1Fragment) {
            toggleHighlight(SUBFLOW_INTAKE, false); // un-highlight in take flow
            toggleVisibleFormCounter(SUBFLOW_INTAKE, false);
            toggleVisibleFormCounter(SUBFLOW_CONSENT, true);
        } else if (currentFragment instanceof CheckinIntakeForm2Fragment) {
            changeCounterOfForm(SUBFLOW_INTAKE, 1, NUM_INTAKE_FORMS);
        } else if (currentFragment instanceof CheckinConsentForm1Fragment) {
            toggleHighlight(SUBFLOW_CONSENT, false);
            toggleVisibleFormCounter(SUBFLOW_CONSENT, false);
        } else if (currentFragment instanceof CheckinConsentForm2Fragment) {
            changeCounterOfForm(SUBFLOW_CONSENT, 1, NUM_CONSENT_FORMS);
        } else if (currentFragment instanceof CheckinInsurancesSummaryFragment) {
            toggleVisibleBackButton(false);
        }
        super.onBackPressed();
    }
    /* ############# Consent Form TODO: will change to different fragment */

    /**
     * Consent form
     */
    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, SignatureActivity.class);

        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
        intent.putExtra("consentform", showingForm);
        if (SignatureActivity.numOfLaunches == 2) {
            // pass the whole DTO
            Gson gson = new Gson();
            String consentformDTO = gson.toJson(consentFormDTO);
            intent.putExtra("consentform_model", consentformDTO);
        }
        if (showingForm == FormId.FORM1) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());
            intent.putExtra("Header_Title", signMedicareLabel);
            intent.putExtra("Subtitle",consentFormLabelsDTO.getConsentReadCarefullyWarning());
        } else if (showingForm == FormId.FORM2) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
            intent.putExtra("Subtitle",consentFormLabelsDTO.getBeforeSignatureWarningText());

        } else {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignHipaaAgreementTitle());
        }

        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CarePayConstants.SIGNATURE_REQ_CODE) {
            if (SignatureActivity.isBackButtonClicked) {
                SignatureActivity.isBackButtonClicked = false;
            } else {
                Fragment fragment = getNextConsentForm();
                if (fragment != null) {
                    navigateToFragment(fragment, true);
                }
            }

        }
    }


    /**
     * Consent form navigation
     * @param workflowJson consent DTO
     */
    public void getConsentFormInformation(String workflowJson) {
        consentFormDTO=getConvertedDTO(ConsentFormDTO.class,workflowJson);
        if ( consentFormDTO != null) {
            ConsentFormMetadataDTO consentFormMetadataDTO = consentFormDTO.getMetadata();
            ConsentFormPayloadDTO consentFormPayloadDTO = consentFormDTO.getConsentFormPayloadDTO();
            ConsentFormAppointmentsPayloadDTO consentFormAppointmentsPayloadDTO = consentFormPayloadDTO.getConsentFormAppointmentPayload().get(0);
            ConsentFormAppoPayloadDTO consentFormAppoPayloadDTO = consentFormAppointmentsPayloadDTO.getAppointmentPayload();
            patientFirstName = consentFormAppoPayloadDTO.getAppointmentPatient().getFirstName();
            patientLastName = consentFormAppoPayloadDTO.getAppointmentPatient().getLastName();
            providerName = consentFormAppoPayloadDTO.getAppoPayloadProvider().getName();

            if (consentFormMetadataDTO != null) {
                consentFormLabelsDTO = consentFormMetadataDTO.getLabel();

                if (consentFormLabelsDTO != null) {
                    /*String authorizationTitle = consentFormLabelsDTO.getAuthorizationFormTitle();
                    String medicareTitle = consentFormLabelsDTO.getConsentForMedicareTitle();
                    String hippaTitle = consentFormLabelsDTO.getHipaaAgreementTitle();*/
                    medicareDescription = consentFormLabelsDTO.getConsentForMedicareText();
                    readCarefullySign = consentFormLabelsDTO.getConsentReadCarefullyWarning();
                    authorizationDescription1 = consentFormLabelsDTO.getAuthorizationGrantText();
                    authorizationDescription2 = consentFormLabelsDTO.getAuthorizationLegalText();
                    /*String hippaDescription = consentFormLabelsDTO.getHipaaConfidentialityAgreementText();
                    String consentMainTitle = consentFormLabelsDTO.getConsentMainTitle();
                    String signAuthLabel = consentFormLabelsDTO.getSignAuthorizationFormTitle();*/
                    signMedicareLabel = consentFormLabelsDTO.getSignConsentForMedicareTitle();
                    /*String signHippaLabel = consentFormLabelsDTO.getSignHipaaAgreementTitle();
                    String legalFirstNameLabel = consentFormLabelsDTO.getLegalFirstNameLabel();
                    String legalLastNameLabel = consentFormLabelsDTO.getLegalLastNameLabel();
                    String clearSignLabel = consentFormLabelsDTO.getSignClearButton();
                    String signButtonLabel = consentFormLabelsDTO.getConfirmSignatureButton();
                    String unabletoSignLabel = consentFormLabelsDTO.getUnableToSignText();
                    String beforeSignWarning = consentFormLabelsDTO.getBeforeSignatureWarningText();
                    String legalsignLabel = consentFormLabelsDTO.getLegalSignatureLabel();
                    String patientSignLabel = consentFormLabelsDTO.getPatientSignatureHeading();*/
                    formbuilder();

                    navigateToFragment(getConsentForm(), false);
                    Log.d(this.getClass().getSimpleName(), "consent form information");
                }
            }


        }
    }


    private void formbuilder() {
        // insert the patient's first and last names
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString firstSpannable = new SpannableString(patientFirstName);
        SpannableString lastSpannable = new SpannableString(patientFirstName);


        int indexFirstComma = medicareDescription.indexOf(',');
        String upToFirstCommaSubstring = medicareDescription.substring(0, indexFirstComma + 1);
        String fromSecCommaOnSubstring = medicareDescription.substring(medicareDescription.indexOf(',', indexFirstComma + 1), medicareDescription.length());
        firstSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_cerulian)), 0, patientFirstName.length(), 0);
        medicareForm = String.format(Locale.getDefault(), "%s %s %s%s", upToFirstCommaSubstring, firstSpannable, patientLastName, fromSecCommaOnSubstring);

        int indexFirstPercent = authorizationDescription2.indexOf('%');
        String upToFirspercentSubstring = authorizationDescription2.substring(0, indexFirstPercent);
        String fromSecpercentOnSubstring = authorizationDescription2.substring(authorizationDescription2.indexOf(',', indexFirstComma + 1), authorizationDescription2.length());
        authForm = String.format(Locale.getDefault(), "%s %s %s", upToFirspercentSubstring, providerName, fromSecpercentOnSubstring);
    }

    private Fragment getConsentForm() {

        if (showingForm == FormId.FORM1) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form1"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            //updateTitle(FormId.FORM1);
            return consentForm1Fragment;
        } else if (showingForm == FormId.FORM2) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form2"));
            ConsentForm2Fragment consentForm2Fragment = new ConsentForm2Fragment();
            consentForm2Fragment.setArguments(bundle);
            //updateTitle(FormId.FORM2);
            return consentForm2Fragment;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form3"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            //updateTitle(FormId.FORM3);
            return consentForm1Fragment;
        }
    }

    private FormData getConsentFormData(String formName) {
        FormData formData = new FormData();
        DateUtil.getInstance().setToCurrent(); // set the date to current
        if (formName.equals("form1")) {
            formData.setTitle(consentFormLabelsDTO.getConsentForMedicareTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(medicareForm);
            formData.setButtonLabel(consentFormLabelsDTO.getSignConsentForMedicareTitle().toUpperCase());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        } else if (formName.equals("form2")) {
            formData.setTitle(consentFormLabelsDTO.getAuthorizationFormTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(authorizationDescription1);
            formData.setContent2(authForm);
            formData.setButtonLabel(consentFormLabelsDTO.getSignAuthorizationFormTitle().toUpperCase());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        } else { //form3
            formData.setTitle(consentFormLabelsDTO.getHipaaAgreementTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(consentFormLabelsDTO.getHipaaConfidentialityAgreementText());
            formData.setButtonLabel(consentFormLabelsDTO.getSignHipaaAgreementTitle().toUpperCase());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        }

        return formData;

    }

    private Fragment getNextConsentForm() {
        showingForm = showingForm.next();
        if (showingForm != FormId.NONE) {
            return getConsentForm();
        }
        return null;
    }

    public ConsentFormLabelsDTO getConsentFormLabelsDTO() {
        return consentFormLabelsDTO;
    }

    public void setConsentFormLabelsDTO(ConsentFormLabelsDTO consentFormLabelsDTO) {
        this.consentFormLabelsDTO = consentFormLabelsDTO;

    }

    public ConsentFormDTO getConsentFormDTO() {
        return consentFormDTO;
    }

    public void setConsentFormDTO(ConsentFormDTO consentFormDTO) {
        this.consentFormDTO = consentFormDTO;
    }

    public AppointmentsPayloadDTO getAppointmentsPayloadDTO() {
        return appointmentsPayloadDTO;
    }

    public void setAppointmentsPayloadDTO(AppointmentsPayloadDTO appointmentsPayloadDTO) {
        this.appointmentsPayloadDTO = appointmentsPayloadDTO;
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


    /* ############# END Consent Form TODO: will change to different fragment */


    /**
     * IntakeForm Navigation
     */

    BroadcastReceiver intakeFormReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("INTAKE_WORKFLOW"))
            inTakeForm=getConvertedDTO(IntakeResponseModel.class,intent.getStringExtra("INTAKE_WORKFLOW"));
            CheckinIntakeForm1Fragment fragment = new CheckinIntakeForm1Fragment();
            navigateToFragment(fragment, false);
            // TODO: SAUL Create Intake Fragment
            //navigateToFragment("INTAKEFRAGMENT", false);


        }
    };

}
