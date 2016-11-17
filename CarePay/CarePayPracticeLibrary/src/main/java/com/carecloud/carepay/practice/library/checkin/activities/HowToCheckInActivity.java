package com.carecloud.carepay.practice.library.checkin.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.AppointmentsActivity;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.dtos.QueryStrings;
import com.carecloud.carepay.practice.library.checkin.dtos.ScanQRCodeResultDTO;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBookButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * Modified by Jahirul Bhuiyan on 11/14/2016.
 * This class using for scan QR code using Clover SDK
 * Implement BroadcastReceiver for clover BarcodeResult.INTENT_ACTION
 */

public class HowToCheckInActivity extends BasePracticeActivity {

    private SigninPatientModeDTO            signinPatientModeDTO;
    private CustomGothamRoundedMediumButton goBackButton;
    private CustomGothamRoundedMediumLabel  howToCheckInTextView;
    private CustomGothamRoundedBookButton   carePayLoginButton;
    private CustomGothamRoundedBookButton   scanQRCodeButton;
    private CustomGothamRoundedMediumButton createCarePayAccountButton;
    private CustomGothamRoundedBookButton   manualSearchButton;

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

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
    void initViews() {
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
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());

            Map<String, String> headers = new HashMap<>();
            headers.put("transition", "true");
            TransitionDTO transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getLogin();
            WorkflowServiceHelper.getInstance().execute(transitionDTO, patientModeSignInCallback, queryMap, headers);
            /*Intent intent = new Intent(HowToCheckInActivity.this, SigninActivity.class);
            startActivity(intent);*/
        }
    };

    /**
     * Click listener for Scan QE Code button
     */
    View.OnClickListener scanQRCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*To implement click event for Scan QR Code*/
            scanQR();
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

    WorkflowServiceCallback patientModeSignInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };

    /**
     * Start QR code scanner base on the device
     * if device is clover start CloverQRScannerActivity from clover application
     * CloverQRScannerActivity used clover sdk for QR scanner
     * for any other devices implement com.google.zxing.client.android.SCAN
     */
    public void scanQR() {
        if (HttpConstants.getDeviceInformation().getDeviceType().equals("Clover")) {
            Intent intent = new Intent();
            intent.setAction("com.carecloud.carepay.practice.clover.qrscanner.CloverQRScannerActivity");
            startActivity(intent);
        } else {
            try {
                //start the scanning activity from the com.google.zxing.client.android.SCAN intent
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                //on catch, show the download dialog
                showDialog(HowToCheckInActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
            }
        }
    }

    //alert dialog for downloadDialog if scanner app not found
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int listener) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    anfe.printStackTrace();
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int listener) {
            }
        });
        return downloadDialog.show();
    }

    /**
     * on ActivityResult method
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param intent      result intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
                toast.show();

                try {
                    processScannedQRCOde(contents);
                } catch (JsonSyntaxException ex) {
                    Toast.makeText(this, "Can not process this QR code. May be this QR code is invalid!", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     *
     * @param qrCodeData
     *  the QR code data
     */
    private void processScannedQRCOde(String qrCodeData) {
        Gson gson = new Gson();
        ScanQRCodeResultDTO scanQRCodeResultDTO = gson.fromJson(qrCodeData, ScanQRCodeResultDTO.class);

        JsonObject queryStringObject = signinPatientModeDTO.getMetadata().getTransitions()
                .getAction().getQueryString();

        QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

        Map<String, String> headers = new HashMap<>();
        headers.put("transition", "true");

        WorkflowServiceHelper.getInstance().execute(signinPatientModeDTO.getMetadata()
                .getTransitions().getAction(), appointmentCallBack,
                getQueryParam(queryStrings, scanQRCodeResultDTO));
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     */
    private Map<String, String> getQueryParam(QueryStrings queryStrings, ScanQRCodeResultDTO scanQRCodeResultDTO) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getAppointmentId().getName(), scanQRCodeResultDTO.getAppointmentId());
        queryMap.put(queryStrings.getPracticeManagement().getName(), scanQRCodeResultDTO.getPracticeManagement());
        queryMap.put(queryStrings.getPracticeId().getName(), scanQRCodeResultDTO.getPracticeId());

        return queryMap;
    }

    WorkflowServiceCallback appointmentCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(HowToCheckInActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(HowToCheckInActivity.this,
                    getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
