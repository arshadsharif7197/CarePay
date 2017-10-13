package com.carecloud.carepay.practice.library.checkin.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.dtos.QRCodeScanResultDTO;
import com.carecloud.carepay.practice.library.signin.SigninActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.qrcodescanner.ScannerQRActivity;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
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

    private SignInDTO signinPatientModeDTO;
    private ProgressDialog dialog;


    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int QR_SCAN_REQUEST_CODE = 0;
    private static final int QR_RESULT_CODE_TABLET = 1;
    private static final int CAMERA_PERMISSION = 1;

    private boolean showQROption = true;
    private boolean showSearchOption = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signinPatientModeDTO = getConvertedDTO(SignInDTO.class);

        setContentView(R.layout.activity_how_to_check_in);

        Bundle extraInfo = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        if(extraInfo != null) {
            showQROption = extraInfo.getBoolean(CarePayConstants.LOGIN_OPTION_QR, true);
            showSearchOption = extraInfo.getBoolean(CarePayConstants.LOGIN_OPTION_SEARCH, true);
        }

        initViews();

    }

    /**
     * Method to initialise view
     */
    void initViews() {
        Button goBackButton = (Button) findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(goBackButtonListener);

        TextView howToCheckInTextView = (TextView) findViewById(R.id.howToCheckInTextView);
        howToCheckInTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));

        View carePayLoginButton = findViewById(R.id.carePayLoginButton);
        carePayLoginButton.setOnClickListener(carePayLoginButtonListener);

        View scanQRCodeButton = findViewById(R.id.scanQRCodeButton);
        scanQRCodeButton.setOnClickListener(scanQRCodeButtonListener);
        scanQRCodeButton.setVisibility(showQROption ? View.VISIBLE : View.GONE);

        View manualSearchButton = findViewById(R.id.manualSearchButton);
        manualSearchButton.setOnClickListener(manualSearchButtonListener);
        manualSearchButton.setVisibility(showSearchOption ? View.VISIBLE : View.GONE);

        View homeImageView = findViewById(R.id.homeImageView);
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
            Bundle bundle = new Bundle();
            bundle.putSerializable(WorkflowDTO.class.getSimpleName(), signinPatientModeDTO.toString());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    };

    /**
     * Click listener for Scan QE Code button
     */
    View.OnClickListener scanQRCodeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*To implement click event for Scan QR Code*/
            launchScanningActivity();
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
            bundle.putSerializable(WorkflowDTO.class.getSimpleName(), signinPatientModeDTO.toString());
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
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

    /**
     * on ActivityResult method
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param intent      result intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                /*Process scanned QR code*/
                String scanResult = intent.getStringExtra("SCAN_RESULT");
                processScannedQRCOde(scanResult);
            } catch (JsonSyntaxException ex) {
                String errorMessage = Label.getLabel("invalid_qr_code_title") + ", "
                        + Label.getLabel("invalid_qr_code_message");
                SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), errorMessage);
                dismissDialog();
            }
        }
    }

    /**
     * @param qrCodeData the QR code data
     */
    private void processScannedQRCOde(String qrCodeData) {
        instantiateProgressDialog();
        Gson gson = new Gson();
        QRCodeScanResultDTO scanQRCodeResultDTO = gson.fromJson(qrCodeData, QRCodeScanResultDTO.class);

        if (scanQRCodeResultDTO != null && scanQRCodeResultDTO.getPracticeMgmt()
                .equals(getApplicationMode().getUserPracticeDTO().getPracticeMgmt())
                && scanQRCodeResultDTO.getPracticeId()
                .equals(getApplicationMode().getUserPracticeDTO().getPracticeId())) {

            getAppAuthorizationHelper().setUser(scanQRCodeResultDTO.getUserName());

            // getApplicationMode().getUserPracticeDTO().setUserName(scanQRCodeResultDTO.getUserName());
            Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put("appointment_id", scanQRCodeResultDTO.getAppointmentId());
            getWorkflowServiceHelper().execute(signinPatientModeDTO.getMetadata()
                    .getTransitions().getAction(), appointmentCallBack, queryMap);

        } else {
            String errorMessage = Label.getLabel("invalid_qr_code_title") + ", "
                    + Label.getLabel("invalid_qr_code_message");
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), errorMessage);
        }
    }

    /**
     * Call back for appointment API.
     */
    WorkflowServiceCallback appointmentCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            dismissDialog();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * Method to show progress dialog.
     */
    private void instantiateProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(Label.getLabel("loading_message"));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Method to dismiss progress dialog.
     */
    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Launch activity for scanning qr code if permission is granted
     */
    public void launchScanningActivity() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, ScannerQRActivity.class);
            startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, ScannerQRActivity.class);
                    startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                return;
        }
    }


}
