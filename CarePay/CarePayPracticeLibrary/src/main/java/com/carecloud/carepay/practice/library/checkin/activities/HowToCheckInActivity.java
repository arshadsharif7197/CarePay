package com.carecloud.carepay.practice.library.checkin.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBookButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;

public class HowToCheckInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_check_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /*Initialise views*/
        initViews();
    }

    /**
     * Method to initialise view
     */
    void initViews(){
        CustomGothamRoundedMediumButton goBackButton = (CustomGothamRoundedMediumButton)
                findViewById(R.id.goBackButton);
        goBackButton.setText(getResources().getString(R.string.not_defined));
        goBackButton.setOnClickListener(goBackButtonListener);

        CustomGothamRoundedMediumLabel howToCheckInTextView = (CustomGothamRoundedMediumLabel)
                findViewById(R.id.howToCheckInTextView);
        howToCheckInTextView.setText(getResources().getString(R.string.not_defined));
        howToCheckInTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));

        CustomGothamRoundedBookButton carePayLoginButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.carePayLoginButton);
        carePayLoginButton.setText(getResources().getString(R.string.not_defined));
        carePayLoginButton.setOnClickListener(carePayLoginButtonListener);

        CustomGothamRoundedBookButton scanQRCodeButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.scanQRCodeButton);
        scanQRCodeButton.setText(getResources().getString(R.string.not_defined));
        scanQRCodeButton.setOnClickListener(scanQRCodeButtonListener);

        CustomGothamRoundedBookButton manualSearchButton = (CustomGothamRoundedBookButton)
                findViewById(R.id.manualSearchButton);
        manualSearchButton.setText(getResources().getString(R.string.not_defined));
        manualSearchButton.setOnClickListener(manualSearchButtonListener);

        CustomGothamRoundedMediumButton createCarePayAccountButton =
                (CustomGothamRoundedMediumButton) findViewById(R.id.createCarePayAccountButton);
        createCarePayAccountButton.setText(getResources().getString(R.string.not_defined));
        createCarePayAccountButton.setOnClickListener(createCarePayAccountButtonListener);

        ImageView homeImageView = (ImageView) findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(homeImageViewListener);
    }

    /**
     * Click listener for go back button
     */
    View.OnClickListener goBackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    /**
     * Click listener for CarePay Login button
     */
    View.OnClickListener carePayLoginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HowToCheckInActivity.this, SigninActivity.class);
            startActivity(intent);
        }
    };

    /**
     * Click listener for Scan QE Code button
     */
    View.OnClickListener scanQRCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(HowToCheckInActivity.this, "Scan QR Code Button clicked",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Click listener for Manual Search button
     */
    View.OnClickListener manualSearchButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(HowToCheckInActivity.this, "Manual Search Button clicked",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Click listener for Create CarePay Account button
     */
    View.OnClickListener createCarePayAccountButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(HowToCheckInActivity.this, "Create CarePay Account Button clicked",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Click listener for home icon
     */
    View.OnClickListener homeImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HowToCheckInActivity.this, CloverMainActivity.class);
            startActivity(intent);
        }
    };
}
