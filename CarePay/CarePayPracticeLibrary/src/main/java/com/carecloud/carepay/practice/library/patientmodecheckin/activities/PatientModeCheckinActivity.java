package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinDemographicsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinInsurancesSummaryFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinPaymentFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;


/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */

public class PatientModeCheckinActivity extends BasePracticeActivity {

    private static final int NUM_OF_SUBFLOWS = 4;
    private DemographicDTO  demographicDTO;
    private TextView        demogrInsTitleTextView;
    private TextView        consentTitleTextView;
    private TextView        intakeTitleTextView;
    private TextView        paymentsTitleTextView;
    private CarePayTextView backButton;
    private ImageView       logoImageView;

    public final static int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static int SUBFLOW_CONSENT          = 1;
    public final static int SUBFLOW_INTAKE           = 2;
    public final static int SUBFLOW_PAYMENTS         = 3;
    private View[] sectionTitleTextViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);

        // TODO: 11/19/2016 uncomment
//        demographicDTO = getConvertedDTO(DemographicDTO.class);

        Gson gson = new Gson();
        demographicDTO = gson.fromJson(json, DemographicDTO.class);

        instantiateViewsRefs();
        initializeViews();

        // place the initial fragment
        CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        navigateToFragment(fragment, false);
        toggleHighlight(SUBFLOW_DEMOGRAPHICS_INS, true);
    }

    private void instantiateViewsRefs() {
        backButton = (CarePayTextView) findViewById(R.id.checkinBack);
        logoImageView = (ImageView) findViewById(R.id.checkinLogo);

        sectionTitleTextViews = new View[NUM_OF_SUBFLOWS];
        sectionTitleTextViews[SUBFLOW_DEMOGRAPHICS_INS] = demogrInsTitleTextView = (TextView) findViewById(R.id.checkinSectionDemogrInsTitle);
        sectionTitleTextViews[SUBFLOW_CONSENT] = consentTitleTextView = (TextView) findViewById(R.id.checkinSectionConsentFormsTitle);
        sectionTitleTextViews[SUBFLOW_INTAKE] = intakeTitleTextView = (TextView) findViewById(R.id.checkinSectionIntakeFormsTitle);
        sectionTitleTextViews[SUBFLOW_PAYMENTS] = paymentsTitleTextView = (TextView) findViewById(R.id.checkinSectionPaymentsTitle);
    }

    private void initializeViews() {
        toggleVisibleBackButton(false);
        // TODO: 11/19/2016 remove when tests complete
        intakeTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new CheckinIntakeForm1Fragment(), false);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
     * @param subflow     The subflow id:
     * (0 - demographics & insurance, 1 - consent, 2 - intake, 3 - payments)
     * @param highlight Whether is hightlight
     */
    public void toggleHighlight(int subflow, boolean highlight) {
        // limit case
        if(subflow == SUBFLOW_DEMOGRAPHICS_INS && !highlight) { // can't go before 'demographics'
            return;
        }
        // if highlight true, highlight current and reset previous if there is one
        TextView currentSection = (TextView) sectionTitleTextViews[subflow];
        TextView prevSection = subflow > SUBFLOW_DEMOGRAPHICS_INS ? (TextView) sectionTitleTextViews[subflow - 1] : null;
        if(highlight) {
            SystemUtil.setGothamRoundedBoldTypeface(this, currentSection);
            currentSection.setTextColor(ContextCompat.getColor(this, R.color.white));
            if(prevSection != null) {
                SystemUtil.setGothamRoundedLightTypeface(this, prevSection);
                prevSection.setTextColor(ContextCompat.getColor(this, R.color.white_opacity_60));
            }
        } else { // if highlight false, reset current and hightlight previous if there is one
            if(prevSection != null) {
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
     * @param formSubflow The sub-flow id
     * @param increment True for incrementation and false for decrementation
     */
    public void changeCounterOfForm(int formSubflow, boolean increment) {
        // if increment true, increment
        // if increment false, decrement
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        if(currentFragment instanceof CheckinPaymentFragment) {
            toggleHighlight(SUBFLOW_PAYMENTS, false);
        } else if(currentFragment instanceof CheckinIntakeForm1Fragment) {
            toggleHighlight(SUBFLOW_INTAKE, false); // un-highlight in take flow
        } else if(currentFragment instanceof CheckinConsentForm1Fragment) {
            toggleHighlight(SUBFLOW_CONSENT, false);
        } else if(currentFragment instanceof CheckinInsurancesSummaryFragment) {
            toggleVisibleBackButton(false);
        }
        super.onBackPressed();
    }

    // TODO: 11/19/2016 remove
    private String json = "{\n" +
            "  \"metadata\": {\n" +
            "    \"labels\": {\n" +
            "      \"demographics_update_button\": \"Update\",\n" +
            "      \"demographics_updates_section\": \"Updates\",\n" +
            "      \"demographics_update_profile_photo_link\": \"Update Profile Photo\",\n" +
            "      \"demographics_update_email_and_password_link\": \"Update Email & Password\",\n" +
            "      \"demographics_address_section\": \"Address\",\n" +
            "      \"demographics_details_section\": \"Details\",\n" +
            "      \"demographics_documents_section\": \"Documents\",\n" +
            "      \"demographics_add_another_insurance_link\": \"ADD ANOTHER\",\n" +
            "      \"demographics_next\": \"Next\",\n" +
            "      \"demographics_choose\": \"Choose\",\n" +
            "      \"demographics_cancel_label\": \"CANCEL\",\n" +
            "      \"demographics_address_header\": \"Let's get you set up!\",\n" +
            "      \"demographics_address_subheader\": \"First, tell us your address & contact information.\",\n" +
            "      \"demographics_required\": \"Required\",\n" +
            "      \"demographics_details_header\": \"Some details about you\",\n" +
            "      \"demographics_details_subheader\": \"How about uploading your photo?\",\n" +
            "      \"demographics_details_dob_hint\": \"MM/DD/YYYY\",\n" +
            "      \"demographics_details_capture_picture_caption\": \"Select or Take Picture\",\n" +
            "      \"demographics_details_recapture_picture_caption\": \"Change current photo\",\n" +
            "      \"demographics_details_allergies_section\": \"ALLERGIES YOU HAVE\",\n" +
            "      \"demographics_details_optional_hint\": \"Optional\",\n" +
            "      \"demographics_details_medications_section\": \"MEDICATIONS YOU'RE TAKING\",\n" +
            "      \"demographics_details_allergy\": \"Allergy\",\n" +
            "      \"demographics_details_medication\": \"Medications\",\n" +
            "      \"demographics_details_allergy_add_unlisted\": \"Add unlisted allergies\",\n" +
            "      \"demographics_details_medication_add_unlisted\": \"Add unlisted medications\",\n" +
            "      \"demographics_documents_header\": \"Documents, please\",\n" +
            "      \"demographics_documents_subheader\": \"The scan process is pretty straightforward\",\n" +
            "      \"demographics_documents_switch_insurance\": \"Do you have health insurance?\",\n" +
            "      \"demographics_documents_multiple_insurances\": \"Have multiple insurances?\",\n" +
            "      \"demographics_documents_scan_front\": \"SCAN FRONT\",\n" +
            "      \"demographics_documents_scan_back\": \"SCAN BACK\",\n" +
            "      \"demographics_documents_rescan_front\": \"RE-SCAN FRONT\",\n" +
            "      \"demographics_documents_rescan_back\": \"RE-SCAN BACK\",\n" +
            "      \"demographics_documents_title_select_state\": \"State\",\n" +
            "      \"demographics_documents_title_select_plan\": \"Plan\",\n" +
            "      \"demographics_documents_title_select_provider\": \"Provider\",\n" +
            "      \"demographics_documents_title_select_gender\": \"Gender\",\n" +
            "      \"demographics_documents_title_select_ethnicity\": \"Ethnicity\",\n" +
            "      \"demographics_documents_title_select_race\": \"Race\",\n" +
            "      \"demographics_documents_title_select_id_type\": \"Document Type\",\n" +
            "      \"demographics_documents_title_card_type\": \"Card Type\",\n" +
            "      \"demographics_allset_section\": \"All Set\",\n" +
            "      \"demographics_allset_header\": \"You're all set!\",\n" +
            "      \"demographics_allset_go_button\": \"GO TO CAREPAY\",\n" +
            "      \"demographics_documents_ins_type_label\": \"Type\",\n" +
            "      \"demographics_documents_choose_plan\": \"Choose Company First\",\n" +
            "      \"demographics_documents_remove\": \"Remove\",\n" +
            "      \"demographics_upload_profile\": \"Upload Profile Photo\",\n" +
            "      \"demographics_profile_instruction\": \"Drag the photo to place it where you want. You can also zoom it in and out.\",\n" +
            "      \"demographics_profile_upload\": \"UPLOAD\",\n" +
            "      \"demographics_profile_zoom_in\": \"Zoom In\",\n" +
            "      \"demographics_profile_zoom_out\": \"Zoom Out\"\n" +
            "    },\n" +
            "    \"links\": {\n" +
            "      \"self\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics/information\"\n" +
            "      },\n" +
            "      \"demographics\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\"\n" +
            "      },\n" +
            "      \"appointments\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/appointments\"\n" +
            "      },\n" +
            "      \"patient_balances\": {\n" +
            "        \"method\": \"GET\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/payments/patient_balances\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"transitions\": {\n" +
            "      \"confirm_demographics\": {\n" +
            "        \"method\": \"POST\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics/confirm\",\n" +
            "        \"data\": {\n" +
            "          \"link\": \"data_models/demographic\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"update_demographics\": {\n" +
            "        \"method\": \"POST\",\n" +
            "        \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics/update_demographics\",\n" +
            "        \"data\": {\n" +
            "          \"link\": \"data_models/demographic\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"data_models\": {\n" +
            "      \"demographic\": {\n" +
            "        \"address\": {\n" +
            "          \"label\": \"ADDRESS\",\n" +
            "          \"method\": {\n" +
            "            \"method\": \"PATCH\",\n" +
            "            \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\",\n" +
            "            \"allowed_ops\": [\n" +
            "              {\n" +
            "                \"op\": \"replace\",\n" +
            "                \"path\": \"/addresses\"\n" +
            "              }\n" +
            "            ],\n" +
            "            \"path\": \"/address\"\n" +
            "          },\n" +
            "          \"type\": \"object\",\n" +
            "          \"properties\": {\n" +
            "            \"phone\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Phone Number\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"pattern\",\n" +
            "                  \"value\": \"\\\\d{3}-\\\\d{3}-\\\\d{4}\",\n" +
            "                  \"error_message\": \"Invalid phone; must be ddd-ddd-dddd\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"zipcode\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Zip Code\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"pattern\",\n" +
            "                  \"value\": \"^[0-9]{5}(?:-[0-9]{4})?$\",\n" +
            "                  \"error_message\": \"Invalid zip; must be ddddd or ddddd-dddd\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"address1\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Address Line 1\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"address2\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Address Line 2\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"city\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"City\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"pattern\",\n" +
            "                  \"value\": \"[a-zA-Z -]{3,}\",\n" +
            "                  \"error_message\": \"At least three letters\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"state\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"State\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"is_in_options\",\n" +
            "                  \"error_message\": \"Invalid state\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"name\": \"AL\",\n" +
            "                  \"label\": \"AL\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"AK\",\n" +
            "                  \"label\": \"AK\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"AS\",\n" +
            "                  \"label\": \"AS\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"AZ\",\n" +
            "                  \"label\": \"AZ\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"AR\",\n" +
            "                  \"label\": \"AR\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"CA\",\n" +
            "                  \"label\": \"CA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"CO\",\n" +
            "                  \"label\": \"CO\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"CT\",\n" +
            "                  \"label\": \"CT\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"DE\",\n" +
            "                  \"label\": \"DE\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"DC\",\n" +
            "                  \"label\": \"DC\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"FL\",\n" +
            "                  \"label\": \"FL\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"GA\",\n" +
            "                  \"label\": \"GA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"GU\",\n" +
            "                  \"label\": \"GU\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"HI\",\n" +
            "                  \"label\": \"HI\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"ID\",\n" +
            "                  \"label\": \"ID\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"IL\",\n" +
            "                  \"label\": \"IL\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"IN\",\n" +
            "                  \"label\": \"IN\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"IA\",\n" +
            "                  \"label\": \"IA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"KS\",\n" +
            "                  \"label\": \"KS\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"KY\",\n" +
            "                  \"label\": \"KY\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"LA\",\n" +
            "                  \"label\": \"LA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"ME\",\n" +
            "                  \"label\": \"ME\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MH\",\n" +
            "                  \"label\": \"MH\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MD\",\n" +
            "                  \"label\": \"MD\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MA\",\n" +
            "                  \"label\": \"MA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MI\",\n" +
            "                  \"label\": \"MI\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MN\",\n" +
            "                  \"label\": \"MN\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MS\",\n" +
            "                  \"label\": \"MS\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MO\",\n" +
            "                  \"label\": \"MO\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"MT\",\n" +
            "                  \"label\": \"MT\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NE\",\n" +
            "                  \"label\": \"NE\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NV\",\n" +
            "                  \"label\": \"NV\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NH\",\n" +
            "                  \"label\": \"NH\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NJ\",\n" +
            "                  \"label\": \"NJ\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NM\",\n" +
            "                  \"label\": \"NM\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NY\",\n" +
            "                  \"label\": \"NY\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"NC\",\n" +
            "                  \"label\": \"NC\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"ND\",\n" +
            "                  \"label\": \"ND\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"OH\",\n" +
            "                  \"label\": \"OH\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"OK\",\n" +
            "                  \"label\": \"OK\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"OR\",\n" +
            "                  \"label\": \"OR\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"PW\",\n" +
            "                  \"label\": \"PW\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"PA\",\n" +
            "                  \"label\": \"PA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"PR\",\n" +
            "                  \"label\": \"PR\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"RI\",\n" +
            "                  \"label\": \"RI\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"SC\",\n" +
            "                  \"label\": \"SC\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"SD\",\n" +
            "                  \"label\": \"SD\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"TN\",\n" +
            "                  \"label\": \"TN\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"TX\",\n" +
            "                  \"label\": \"TX\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"UT\",\n" +
            "                  \"label\": \"UT\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"VT\",\n" +
            "                  \"label\": \"VT\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"VI\",\n" +
            "                  \"label\": \"VI\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"VA\",\n" +
            "                  \"label\": \"VA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"WA\",\n" +
            "                  \"label\": \"WA\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"WV\",\n" +
            "                  \"label\": \"WV\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"WI\",\n" +
            "                  \"label\": \"WI\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"WY\",\n" +
            "                  \"label\": \"WY\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"country\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Country\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"personal_details\": {\n" +
            "          \"type\": \"object\",\n" +
            "          \"label\": \"DETAILS\",\n" +
            "          \"validations\": [\n" +
            "            {\n" +
            "              \"type\": \"required\",\n" +
            "              \"value\": true,\n" +
            "              \"error_message\": \"This field is required\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"method\": {\n" +
            "            \"method\": \"PATCH\",\n" +
            "            \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\",\n" +
            "            \"allowed_ops\": [\n" +
            "              {\n" +
            "                \"op\": \"replace\",\n" +
            "                \"path\": \"/personal_details\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"properties\": {\n" +
            "            \"first_name\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"First Name\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": true,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"last_name\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Last Name\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": true,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"middle_name\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Middle Name\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"date_of_birth\": {\n" +
            "              \"type\": \"date\",\n" +
            "              \"label\": \"Date of birth\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"pattern\",\n" +
            "                  \"value\": \"^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$\",\n" +
            "                  \"error_message\": \"Invalid date; Must be MM/DD/YYYY and between 01/01/1901 and today\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"type\": \"required\",\n" +
            "                  \"value\": false,\n" +
            "                  \"error_message\": \"This field is required\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"gender\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Gender\",\n" +
            "              \"validations\": [\n" +
            "                {\n" +
            "                  \"type\": \"occurences\",\n" +
            "                  \"value\": {\n" +
            "                    \"min\": 1,\n" +
            "                    \"max\": 1\n" +
            "                  },\n" +
            "                  \"error_message\": \"Choose a gender\"\n" +
            "                }\n" +
            "              ],\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"label\": \"Male\",\n" +
            "                  \"name\": \"M\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"label\": \"Female\",\n" +
            "                  \"name\": \"F\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"primary_race\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Race\",\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"name\": \"american_indian\",\n" +
            "                  \"label\": \"American Indian\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"asian\",\n" +
            "                  \"label\": \"Asian\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"african_american\",\n" +
            "                  \"label\": \"African American\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"secondary_race\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Seconsdary Race\",\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"name\": \"american_indian\",\n" +
            "                  \"label\": \"American Indian\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"asian\",\n" +
            "                  \"label\": \"Asian\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"african_american\",\n" +
            "                  \"label\": \"African American\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"ethnicity\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Ethnicity\",\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"name\": \"hispanic\",\n" +
            "                  \"label\": \"Hispanic\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"preferred_language\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"Preferred Language\",\n" +
            "              \"options\": [\n" +
            "                {\n" +
            "                  \"name\": \"EN\",\n" +
            "                  \"label\": \"ENGLISH\"\n" +
            "                },\n" +
            "                {\n" +
            "                  \"name\": \"ES\",\n" +
            "                  \"label\": \"ESPANOL\"\n" +
            "                }\n" +
            "              ]\n" +
            "            },\n" +
            "            \"profile_photo\": {\n" +
            "              \"type\": \"string\",\n" +
            "              \"label\": \"profile_photo\"\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"identity_documents\": {\n" +
            "          \"type\": \"array\",\n" +
            "          \"method\": {\n" +
            "            \"method\": \"PATCH\",\n" +
            "            \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\",\n" +
            "            \"allowed_ops\": [\n" +
            "              {\n" +
            "                \"op\": \"add\",\n" +
            "                \"path\": \"/identity_document\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"op\": \"remove\",\n" +
            "                \"path\": \"/identity_document/{id}\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"op\": \"replace\",\n" +
            "                \"path\": \"/identity_document/{id}\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"properties\": {\n" +
            "            \"items\": {\n" +
            "              \"identity_document\": {\n" +
            "                \"type\": \"object\",\n" +
            "                \"properties\": {\n" +
            "                  \"identity_document_photos\": {\n" +
            "                    \"type\": \"array\",\n" +
            "                    \"label\": \"PHOTOS\",\n" +
            "                    \"properties\": {\n" +
            "                      \"items\": {\n" +
            "                        \"identity_document_photo\": {\n" +
            "                          \"type\": \"string\"\n" +
            "                        }\n" +
            "                      }\n" +
            "                    },\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"identity_document_number\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"NUMBER\",\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"identity_document_country\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Country\",\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"identity_document_state\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"State\",\n" +
            "                    \"options\": [\n" +
            "                      {\n" +
            "                        \"name\": \"AL\",\n" +
            "                        \"label\": \"AL\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"AK\",\n" +
            "                        \"label\": \"AK\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"AS\",\n" +
            "                        \"label\": \"AS\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"AZ\",\n" +
            "                        \"label\": \"AZ\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"AR\",\n" +
            "                        \"label\": \"AR\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"CA\",\n" +
            "                        \"label\": \"CA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"CO\",\n" +
            "                        \"label\": \"CO\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"CT\",\n" +
            "                        \"label\": \"CT\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"DE\",\n" +
            "                        \"label\": \"DE\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"DC\",\n" +
            "                        \"label\": \"DC\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"FL\",\n" +
            "                        \"label\": \"FL\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"GA\",\n" +
            "                        \"label\": \"GA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"GU\",\n" +
            "                        \"label\": \"GU\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"HI\",\n" +
            "                        \"label\": \"HI\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"ID\",\n" +
            "                        \"label\": \"ID\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"IL\",\n" +
            "                        \"label\": \"IL\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"IN\",\n" +
            "                        \"label\": \"IN\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"IA\",\n" +
            "                        \"label\": \"IA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"KS\",\n" +
            "                        \"label\": \"KS\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"KY\",\n" +
            "                        \"label\": \"KY\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"LA\",\n" +
            "                        \"label\": \"LA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"ME\",\n" +
            "                        \"label\": \"ME\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MH\",\n" +
            "                        \"label\": \"MH\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MD\",\n" +
            "                        \"label\": \"MD\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MA\",\n" +
            "                        \"label\": \"MA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MI\",\n" +
            "                        \"label\": \"MI\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MN\",\n" +
            "                        \"label\": \"MN\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MS\",\n" +
            "                        \"label\": \"MS\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MO\",\n" +
            "                        \"label\": \"MO\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"MT\",\n" +
            "                        \"label\": \"MT\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NE\",\n" +
            "                        \"label\": \"NE\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NV\",\n" +
            "                        \"label\": \"NV\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NH\",\n" +
            "                        \"label\": \"NH\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NJ\",\n" +
            "                        \"label\": \"NJ\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NM\",\n" +
            "                        \"label\": \"NM\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NY\",\n" +
            "                        \"label\": \"NY\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"NC\",\n" +
            "                        \"label\": \"NC\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"ND\",\n" +
            "                        \"label\": \"ND\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"OH\",\n" +
            "                        \"label\": \"OH\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"OK\",\n" +
            "                        \"label\": \"OK\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"OR\",\n" +
            "                        \"label\": \"OR\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"PW\",\n" +
            "                        \"label\": \"PW\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"PA\",\n" +
            "                        \"label\": \"PA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"PR\",\n" +
            "                        \"label\": \"PR\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"RI\",\n" +
            "                        \"label\": \"RI\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"SC\",\n" +
            "                        \"label\": \"SC\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"SD\",\n" +
            "                        \"label\": \"SD\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"TN\",\n" +
            "                        \"label\": \"TN\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"TX\",\n" +
            "                        \"label\": \"TX\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"UT\",\n" +
            "                        \"label\": \"UT\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"VT\",\n" +
            "                        \"label\": \"VT\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"VI\",\n" +
            "                        \"label\": \"VI\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"VA\",\n" +
            "                        \"label\": \"VA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"WA\",\n" +
            "                        \"label\": \"WA\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"WV\",\n" +
            "                        \"label\": \"WV\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"WI\",\n" +
            "                        \"label\": \"WI\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"WY\",\n" +
            "                        \"label\": \"WY\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"identity_document_type\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Document Type\",\n" +
            "                    \"options\": [\n" +
            "                      {\n" +
            "                        \"name\": \"license\",\n" +
            "                        \"label\": \"Driver's License\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"insurances\": {\n" +
            "          \"type\": \"array\",\n" +
            "          \"method\": {\n" +
            "            \"method\": \"PATCH\",\n" +
            "            \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\",\n" +
            "            \"allowed_ops\": [\n" +
            "              {\n" +
            "                \"op\": \"add\",\n" +
            "                \"path\": \"/insurances\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"op\": \"remove\",\n" +
            "                \"path\": \"/insurances/{id}\"\n" +
            "              },\n" +
            "              {\n" +
            "                \"op\": \"replace\",\n" +
            "                \"path\": \"/insurances/{id}\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"properties\": {\n" +
            "            \"items\": {\n" +
            "              \"insurance\": {\n" +
            "                \"type\": \"object\",\n" +
            "                \"label\": \"PHOTOS\",\n" +
            "                \"properties\": {\n" +
            "                  \"insurance_photos\": {\n" +
            "                    \"type\": \"array\",\n" +
            "                    \"properties\": {\n" +
            "                      \"items\": {\n" +
            "                        \"insurance_photo\": {\n" +
            "                          \"type\": \"string\"\n" +
            "                        }\n" +
            "                      }\n" +
            "                    },\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"insurance_provider\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Provider\",\n" +
            "                    \"options\": [\n" +
            "                      {\n" +
            "                        \"name\": \"blues\",\n" +
            "                        \"label\": \"Blue Shield\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"uhc\",\n" +
            "                        \"label\": \"UnitedHealth Group\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"aetna \",\n" +
            "                        \"label\": \"Aetna\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"cigna\",\n" +
            "                        \"label\": \"Cigna\"\n" +
            "                      }\n" +
            "                    ],\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": true,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"insurance_plan\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Plan\",\n" +
            "                    \"options\": [\n" +
            "                      {\n" +
            "                        \"name\": \"blues_ppo\",\n" +
            "                        \"label\": \"Blue Shield PPO\",\n" +
            "                        \"scope\": \"blues\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"blues_hmo\",\n" +
            "                        \"label\": \"Blue Shield HMO\",\n" +
            "                        \"scope\": \"blues\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"blues_hdhp\",\n" +
            "                        \"label\": \"Blue Shield HDHP\",\n" +
            "                        \"scope\": \"blues\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"uhc_ppo\",\n" +
            "                        \"label\": \"UnitedHealth PPO\",\n" +
            "                        \"scope\": \"uhc\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"uhc_hmo\",\n" +
            "                        \"label\": \"UnitedHealth HMO\",\n" +
            "                        \"scope\": \"uhc\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"aetna_ppo\",\n" +
            "                        \"label\": \"Aetna\",\n" +
            "                        \"scope\": \"aetna\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"aetna_hmo\",\n" +
            "                        \"label\": \"Aetna Plus\",\n" +
            "                        \"scope\": \"aetna\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"cigna_ppo\",\n" +
            "                        \"label\": \"Cigna One\",\n" +
            "                        \"scope\": \"cigna\"\n" +
            "                      }\n" +
            "                    ],\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"insurance_member_id\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Insurance Card Number\",\n" +
            "                    \"validations\": [\n" +
            "                      {\n" +
            "                        \"type\": \"required\",\n" +
            "                        \"value\": false,\n" +
            "                        \"error_message\": \"This field is required\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  },\n" +
            "                  \"insurance_type\": {\n" +
            "                    \"type\": \"string\",\n" +
            "                    \"label\": \"Type\",\n" +
            "                    \"options\": [\n" +
            "                      {\n" +
            "                        \"name\": \"primary\",\n" +
            "                        \"label\": \"Primary\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"secondary\",\n" +
            "                        \"label\": \"Secondary\"\n" +
            "                      },\n" +
            "                      {\n" +
            "                        \"name\": \"tertiary\",\n" +
            "                        \"label\": \"Tertiary\"\n" +
            "                      }\n" +
            "                    ]\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        },\n" +
            "        \"updates\": {\n" +
            "          \"type\": \"array\",\n" +
            "          \"properties\": {\n" +
            "            \"items\": {\n" +
            "              \"update\": {\n" +
            "                \"type\": \"string\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"method\": {\n" +
            "            \"method\": \"PATCH\",\n" +
            "            \"url\": \"https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/patient_checkin/demographics\",\n" +
            "            \"allowed_ops\": [\n" +
            "              {\n" +
            "                \"op\": \"replace\",\n" +
            "                \"path\": \"/updates\"\n" +
            "              }\n" +
            "            ]\n" +
            "          },\n" +
            "          \"label\": \"UPDATES\",\n" +
            "          \"options\": [\n" +
            "            {\n" +
            "              \"name\": \"mobile_notifications\",\n" +
            "              \"label\": \"MOBILE NOTIFICATIONS\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"email_notifications\",\n" +
            "              \"label\": \"EMAIL NOTIFICATIONS\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"newsletters\",\n" +
            "              \"label\": \"NEWS LETTERS\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"monthly_coupons\",\n" +
            "              \"label\": \"MONTHLY COUPONS\"\n" +
            "            },\n" +
            "            {\n" +
            "              \"name\": \"marketing_material\",\n" +
            "              \"label\": \"MARKETING MATERIAL\"\n" +
            "            }\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"payload\": {},\n" +
            "  \"state\": \"demographics\"\n" +
            "}";
}
