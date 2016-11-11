package com.carecloud.carepay.practice.library.checkin.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBookButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;

public class HowToCheckInActivity extends BasePracticeActivity {

    private SigninPatientModeDTO signinPatientModeDTO;
    private CustomGothamRoundedMediumButton goBackButton;
    private CustomGothamRoundedMediumLabel howToCheckInTextView;
    private CustomGothamRoundedBookButton carePayLoginButton;
    private  CustomGothamRoundedBookButton scanQRCodeButton;
    private  CustomGothamRoundedMediumButton createCarePayAccountButton;
    private CustomGothamRoundedBookButton manualSearchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);

        setContentView(R.layout.activity_how_to_check_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /*Initialise views*/
        initViews();
        populateWithLabels();
    }

    /**
     * Method to initialise view
     */
    void initViews(){
        goBackButton = (CustomGothamRoundedMediumButton)
                findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(goBackButtonListener);

        howToCheckInTextView = (CustomGothamRoundedMediumLabel)
                findViewById(R.id.howToCheckInTextView);
        howToCheckInTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));

        carePayLoginButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.carePayLoginButton);
        carePayLoginButton.setOnClickListener(carePayLoginButtonListener);

        scanQRCodeButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.scanQRCodeButton);
        scanQRCodeButton.setOnClickListener(scanQRCodeButtonListener);

        manualSearchButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.manualSearchButton);
        manualSearchButton.setOnClickListener(manualSearchButtonListener);

        createCarePayAccountButton =
                (CustomGothamRoundedMediumButton) findViewById(R.id.createCarePayAccountButton);
        createCarePayAccountButton.setOnClickListener(createCarePayAccountButtonListener);

        ImageView homeImageView = (ImageView) findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(homeImageViewListener);
    }

    /**
     * Click listener for go back button
     */
    View.OnClickListener goBackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    /**
     * Click listener for CarePay Login button
     */
    View.OnClickListener carePayLoginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HowToCheckInActivity.this, SigninActivity.class);
            startActivity(intent);
        }
    };

    /**
     * Click listener for Scan QE Code button
     */
    View.OnClickListener scanQRCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*To implement click event for Scan QR Code*/
        }
    };

    /**
     * Click listener for Manual Search button
     */
    View.OnClickListener manualSearchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*To implement click event for Manual Search */
            Intent intent = new Intent(HowToCheckInActivity.this, PersonalInformationActivity.class);
            startActivity(intent);
        }
    };

    /**
     * Click listener for Create CarePay Account button
     */
    View.OnClickListener createCarePayAccountButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*To implement click event for Create CarePay Account*/
        }
    };

    /**
     * Click listener for home icon
     */
    View.OnClickListener homeImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(HowToCheckInActivity.this, CloverMainActivity.class);
            startActivity(intent);
        }
    };

    private void populateWithLabels() {
        SigninPatientModeLabelsDTO signinPatientModeLabels = signinPatientModeDTO.getMetadata().getLabels();
        goBackButton.setText(signinPatientModeLabels.getSiginHowCheckInGoBack());
        howToCheckInTextView.setText(signinPatientModeLabels.getSigninHowWantCheckIn());
        carePayLoginButton.setText(signinPatientModeLabels.getSigninHowCheckInCarepayLogin());
        scanQRCodeButton.setText(signinPatientModeLabels.getSiginHowCheckInScanQrCode());
        manualSearchButton.setText(signinPatientModeLabels.getSiginHowCheckInManualSearch());
        createCarePayAccountButton.setText(signinPatientModeLabels.getSiginHowCheckInCreateCarepayAccount());
    }
}
