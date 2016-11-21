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
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinDemographicsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinInsurancesSummaryFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm2Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinPaymentFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.Locale;


/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */

public class PatientModeCheckinActivity extends BasePracticeActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);

        demographicDTO = getConvertedDTO(DemographicDTO.class);

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
}
