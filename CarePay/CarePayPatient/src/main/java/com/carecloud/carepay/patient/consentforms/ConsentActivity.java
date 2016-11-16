package com.carecloud.carepay.patient.consentforms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentForm1Fragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentForm2Fragment;
import com.carecloud.carepay.patient.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepay.patient.intakeforms.activities.InTakeActivity;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormMetadataDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppoPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.constants.CarePayConstants;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import com.carecloud.carepaylibray.utils.DateUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

import com.google.gson.Gson;
import java.util.Locale;

public class ConsentActivity extends BasePatientActivity implements IFragmentCallback {


    private ConsentFormLabelsDTO consentFormLabelsDTO;

    private AppointmentsPayloadDTO appointmentsPayloadDTO;
    private AppointmentsResultModel appointmentsResultModel;
    private ConsentFormAppointmentsPayloadDTO consentFormAppointmentsPayloadDTO;
    private ConsentFormAppoPayloadDTO consentFormAppoPayloadDTO;

    private ConsentFormDTO consentFormDTO;
    private ConsentFormMetadataDTO consentFormMetadataDTO;
    private ConsentFormPayloadDTO consentFormPayloadDTO;
    private TextView title;
    private FormId showingForm = FormId.FORM1;
    private View indicator0;
    private View indicator1;
    private View indicator2;

    private String readCarefullySign;
    private String consentMainTitle;
    private String medicareDescription;
    private String medicareForm;
    private String providerName = " ";
    private String patientFirstName = " ";
    private String patientLastName = " ";
    private String authorizationDescription1;
    private String authorizationDescription2;
    private String authForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consentFormDTO = getConvertedDTO(ConsentFormDTO.class);
        setContentView(R.layout.consent_activity_layout);
        Intent intent = getIntent();
        if (intent.hasExtra("consentform_model")) {
            String consentFormDTOString = intent.getStringExtra("consentform_model");
            Gson gson = new Gson();
            consentFormDTO = gson.fromJson(consentFormDTOString, ConsentFormDTO.class);
        }
        indicator0 = findViewById(R.id.indicator0);
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        setGothamRoundedMediumTypeface(this, title);

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);

        getConsentFormInformation();
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, SignatureActivity.class);
        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
        intent.putExtra("consentform", showingForm);
        if (showingForm == FormId.FORM1) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());

        } else if (showingForm == FormId.FORM2) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
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
                    replaceFragment(fragment, true);
                } else {
                    startActivity(new Intent(ConsentActivity.this, InTakeActivity.class));
                    finish();
                }
            }
        }
    }

    private void getConsentFormInformation() {

        if (consentFormDTO != null) {
            consentFormMetadataDTO = consentFormDTO.getMetadata();
            consentFormPayloadDTO = consentFormDTO.getConsentFormPayloadDTO();
            consentFormAppointmentsPayloadDTO = consentFormPayloadDTO.getConsentFormAppointmentPayload().get(0);
            consentFormAppoPayloadDTO = consentFormAppointmentsPayloadDTO.getAppointmentPayload();
            patientFirstName = consentFormAppoPayloadDTO.getAppointmentPatient().getFirstName();
            patientLastName = consentFormAppoPayloadDTO.getAppointmentPatient().getLastName();
            providerName = consentFormAppoPayloadDTO.getAppoPayloadProvider().getName();

            if (consentFormMetadataDTO != null) {
                consentFormLabelsDTO = consentFormMetadataDTO.getLabel();

                if (consentFormLabelsDTO != null) {
                    medicareDescription = consentFormLabelsDTO.getConsentForMedicareText();
                    readCarefullySign = consentFormLabelsDTO.getConsentReadCarefullyWarning();
                    authorizationDescription1 = consentFormLabelsDTO.getAuthorizationGrantText();
                    authorizationDescription2 = consentFormLabelsDTO.getAuthorizationLegalText();
                    consentMainTitle = consentFormLabelsDTO.getConsentMainTitle();
                    formbuilder();

                    replaceFragment(getConsentForm(), false);
                    Log.d(LOG_TAG, "consent form information");
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
        firstSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ConsentActivity.this, R.color.blue_cerulian)), 0, patientFirstName.length(), 0);
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
            updateTitle(FormId.FORM1);
            return consentForm1Fragment;
        } else if (showingForm == FormId.FORM2) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form2"));
            ConsentForm2Fragment consentForm2Fragment = new ConsentForm2Fragment();
            consentForm2Fragment.setArguments(bundle);
            updateTitle(FormId.FORM2);
            return consentForm2Fragment;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form3"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            updateTitle(FormId.FORM3);
            return consentForm1Fragment;
        }
    }

    /**
     * Updates the title to show the form number and the corresponding bulleted tab
     *
     * @param currentForm The id of the current form
     */
    private void updateTitle(FormId currentForm) {
        switch (currentForm) {
            case FORM1:
                title.setText(consentMainTitle + "1 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_gray);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM2:
                title.setText(consentMainTitle + " 2 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM3:
                title.setText(consentMainTitle + " 3 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_blue);
                break;
            default:
                break;
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

    @Override
    public void onBackPressed() {
        showingForm = showingForm.prev();
        if (showingForm != FormId.NONE) {
            updateTitle(showingForm);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
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

}