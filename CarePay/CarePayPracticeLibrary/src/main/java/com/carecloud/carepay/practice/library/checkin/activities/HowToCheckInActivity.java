package com.carecloud.carepay.practice.library.checkin.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.dtos.QRCodeScanResultDTO;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedBookButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumButton;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.qrcodescanner.ScannerActivity;
import com.carecloud.carepaylibray.qrcodescanner.ScannerQRActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
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
    private CustomGothamRoundedBookButton   manualSearchButton;
    private ProgressDialog                  dialog;


    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int QR_SCAN_REQUEST_CODE =0;
    private static final int QR_RESULT_CODE_TABLET=1;
    private static final int CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);

        setContentView(R.layout.activity_how_to_check_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setNavigationBarVisibility();


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
            /*Map<String, String> queryMap = new HashMap<>();
            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());

            Map<String, String> headers = new HashMap<>();
            headers.put("transition", "true");
            TransitionDTO transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getLogin();
            WorkflowServiceHelper.getInstance().execute(transitionDTO, patientModeSignInCallback, queryMap, headers);*/
            Intent intent = new Intent(HowToCheckInActivity.this, SigninActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(getApplicationContext().getClass().getSimpleName(), signinPatientModeDTO.toString());
            intent.putExtras(bundle);
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
            Bundle bundle = new Bundle();
            bundle.putSerializable(getApplicationContext().getClass().getSimpleName(), signinPatientModeDTO.toString());
            intent.putExtras(bundle);
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
            onBackPressed();
        }
    };

    private void populateWithLabels() {
        SigninPatientModeLabelsDTO signinPatientModeLabels = signinPatientModeDTO.getMetadata().getLabels();
        goBackButton.setText(signinPatientModeLabels.getSiginHowCheckInGoBack());
        howToCheckInTextView.setText(signinPatientModeLabels.getSigninHowWantCheckIn());
        carePayLoginButton.setText(signinPatientModeLabels.getSigninHowCheckInCarepayLogin());
        scanQRCodeButton.setText(signinPatientModeLabels.getSiginHowCheckInScanQrCode());
        manualSearchButton.setText(signinPatientModeLabels.getSiginHowCheckInManualSearch());
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
            SystemUtil.showFaultDialog(HowToCheckInActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
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
            //startActivity(intent);
            startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
        } else {
            launchActivity();
        }
    }

    /**
     * on ActivityResult method
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param intent      result intent
     */
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode== QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                /*Process scanned QR code*/
                String scanResult = intent.getStringExtra("SCAN_RESULT");
                processScannedQRCOde(scanResult);
            } catch (JsonSyntaxException ex) {
                SystemUtil.showDialogMessage(this,
                        signinPatientModeDTO.getMetadata().getLabels().getInvalidQRCodeTitle(),
                        signinPatientModeDTO.getMetadata().getLabels().getInvalidQRCodeMessage());
                dismissDialog();
            }
        }
    }

    /**
     *
     * @param qrCodeData
     *  the QR code data
     */
    private void processScannedQRCOde(String qrCodeData) {
        instantiateProgressDialog();
        Gson gson = new Gson();
        QRCodeScanResultDTO scanQRCodeResultDTO = gson.fromJson(qrCodeData, QRCodeScanResultDTO.class);

        if(scanQRCodeResultDTO!=null && scanQRCodeResultDTO.getPracticeMgmt()
                .equals(ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt())
                && scanQRCodeResultDTO.getPracticeId()
                .equals(ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId())){
            CognitoAppHelper.setUser(scanQRCodeResultDTO.getUserName());
           // ApplicationMode.getInstance().getUserPracticeDTO().setUserName(scanQRCodeResultDTO.getUserName());
            Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put("appointment_id", scanQRCodeResultDTO.getAppointmentId());
            WorkflowServiceHelper.getInstance().execute(signinPatientModeDTO.getMetadata()
                            .getTransitions().getAction(), appointmentCallBack, queryMap);

        }else{
            SystemUtil.showDialogMessage(this,"Invalid QR Code","This QR code is not valid for this practice.");
        }
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     *//*
    private Map<String, String> getQueryParam(QueryStrings queryStrings, ScanQRCodeResultDTO scanQRCodeResultDTO) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getAppointmentId().getName(), scanQRCodeResultDTO.getAppointmentId());
        queryMap.put(queryStrings.getPracticeManagement().getName(), scanQRCodeResultDTO.getPracticeManagement());
        queryMap.put(queryStrings.getPracticeId().getName(), scanQRCodeResultDTO.getPracticeId());

        return queryMap;
    }*/

    /**
     * Call back for appointment API.
     */
    WorkflowServiceCallback appointmentCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(HowToCheckInActivity.this,
                    workflowDTO);
            dismissDialog();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(HowToCheckInActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * Method to show progress dialog.
     */
    private void instantiateProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(signinPatientModeDTO.getMetadata().getLabels().getLoadingMessage());
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Method to dismiss progress dialog.
     */
    private void dismissDialog() {
        if(dialog != null){
            dialog.dismiss();
        }
    }

    /**
     * Launch activity for scanning qr code if permission is granted
     * */
    public void launchActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, ScannerQRActivity.class);
            startActivityForResult(intent,QR_SCAN_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int grantResults[]) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, ScannerQRActivity.class);
                        startActivityForResult(intent,QR_SCAN_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                return;
        }
    }

}
