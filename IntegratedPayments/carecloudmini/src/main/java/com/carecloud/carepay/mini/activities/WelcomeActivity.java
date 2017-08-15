package com.carecloud.carepay.mini.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceCallback;
import com.carecloud.carepay.mini.utils.Defs;
import com.carecloud.carepay.mini.utils.JsonHelper;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;
import com.carecloud.shamrocksdk.connections.DeviceConnection;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.StreamRecord;
import com.carecloud.shamrocksdk.utils.AuthorizationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lmenendez on 7/12/17
 */

public class WelcomeActivity extends FullScreenActivity {
    private static final String TAG = WelcomeActivity.class.getName();
    private static final int CONNECTION_RETRY_DELAY = 1000 * 5;
    private static final int PAYMENT_COMPLETE_RESET = 1000 * 3;

    private ApplicationHelper applicationHelper;
    private TextView message;
    private Handler handler;

    private Device connectedDevice;

    private int paymentAttempt = 0;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        applicationHelper = (ApplicationHelper) getApplication();
        handler = new Handler();

        setContentView(R.layout.activity_welcome);
        setPracticeDetails();

        message = (TextView) findViewById(R.id.welcome_message);
    }

    @Override
    protected void onStart(){
        super.onStart();
        connectDevice();
    }

    @Override
    protected void onStop(){
        if(connectedDevice == null || !connectedDevice.isProcessing()) {
            disconnectDevice();
        }
        super.onStop();
    }

    private void setPracticeDetails(){
        View practiceInitialsLayout = findViewById(R.id.practice_initials_layout);
        View carecloudLogoLayout = findViewById(R.id.carecloud_logo_layout);
        View practiceLogoLayout = findViewById(R.id.practice_logo_layout);
        TextView practiceInitials = (TextView) findViewById(R.id.practice_initials_name);
        ImageView practiceLogo = (ImageView) findViewById(R.id.practice_logo);

        practiceInitialsLayout.setVisibility(View.GONE);
        carecloudLogoLayout.setVisibility(View.GONE);
        practiceLogoLayout.setVisibility(View.GONE);

        UserPracticeDTO selectedPractice = applicationHelper.getApplicationPreferences().getUserPracticeDTO();
        @Defs.ImageStyles int selectedImageStyle = applicationHelper.getApplicationPreferences().getImageStyle();
        switch (selectedImageStyle) {
            case Defs.IMAGE_STYLE_PRACTICE_INITIALS: {
                practiceInitialsLayout.setVisibility(View.VISIBLE);

                if (selectedPractice.getPracticeInitials() != null) {
                    practiceInitials.setText(selectedPractice.getPracticeInitials());
                } else {
                    practiceInitials.setText(StringUtil.getShortName(selectedPractice.getPracticeName()));
                }
                break;
            }
            case Defs.IMAGE_STYLE_PRACTICE_LOGO: {
                practiceLogoLayout.setVisibility(View.VISIBLE);
                String imageUrl = selectedPractice.getPracticePhoto();
                Picasso.with(this)
                        .load(imageUrl)
                        .resize(400, 400)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .placeholder(R.drawable.practice_no_image_bkg)
                        .into(practiceLogo);
                break;
            }
            case Defs.IMAGE_STYLE_CARECLOUD_LOGO:
            default:{
                carecloudLogoLayout.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private void connectDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String id = applicationHelper.getApplicationPreferences().getDeviceId();
                DeviceConnection.connect(WelcomeActivity.this, id, connectionCallback, connectionActionCallback);
            }
        });
    }

    private void disconnectDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String id = applicationHelper.getApplicationPreferences().getDeviceId();
                DeviceConnection.disconnect(WelcomeActivity.this, id);
            }
        });
    }

    private void updateConnectedDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(connectedDevice != null) {
                    String id = applicationHelper.getApplicationPreferences().getDeviceId();
                    if(!DeviceConnection.updateConnection(WelcomeActivity.this, id, connectedDevice)){
                        connectDevice();
                    }
                }
            }
        });
    }

    private void updateMessage(final String messageText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message.setText(messageText);
            }
        });
    }

    private void showErrorToast(final String errorText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomErrorToast.showWithMessage(WelcomeActivity.this, errorText);
            }
        });
    }

    private void startPaymentRequest(final String paymentRequestId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DevicePayment.handlePaymentRequest(WelcomeActivity.this, paymentRequestId, paymentRequestCallback, paymentActionCallback);
            }
        });
    }

    private void releasePaymentRequest(final String paymentRequestId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedDevice.setState(Device.STATE_READY);
                connectedDevice.setProcessing(false);
                connectedDevice.setPaymentRequestId(null);
                updateConnectedDevice();
                if(paymentRequestId != null) {
                    DevicePayment.releasePaymentRequest(WelcomeActivity.this, paymentRequestId);
                }
            }
        });
    }

    private ConnectionCallback connectionCallback = new ConnectionCallback() {
        @Override
        public void onPreExecute() {
            updateMessage(getString(R.string.welcome_connecting));
        }

        @Override
        public void onPostExecute(Device device) {
            updateMessage(getString(R.string.welcome_waiting));
            connectedDevice = device;
        }

        @Override
        public void onFailure(String errorMessage) {
            updateMessage(errorMessage);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectDevice();
                }
            }, CONNECTION_RETRY_DELAY);
        }
    };

    private ConnectionActionCallback connectionActionCallback = new ConnectionActionCallback() {
        @Override
        public void onConnectionError(String deviceName, String errorCode, String errorMessage) {
            Log.d(TAG, "Device connection error: "+errorMessage);
        }

        @Override
        public void onConnectionDestroyed(String deviceName) {
            Log.d(TAG, "Device connection destroyed");
            connectDevice();
        }

        @Override
        public void onConnectionUpdate(String deviceName, Device device) {
            Log.d(TAG, "Received Connection update");
            String paymentRequestId = device.getPaymentRequestId();
            Log.d(TAG, "Payment Request: "+paymentRequestId);
            if(connectedDevice != null){

                switch (connectedDevice.getState()){
                    case Device.STATE_READY: //Device is ready to process payments
                        if(paymentRequestId == null ) {
                            updateMessage(getString(R.string.welcome_waiting));
                            return;
                        }
                        if(connectedDevice.getPaymentRequestId() == null) {
                            //this is a net new payment request
                            Log.d(TAG, "Device is ready & payment request received, update to in-use");
                            connectedDevice = device;
                            connectedDevice.setState(Device.STATE_IN_USE);

                            updateConnectedDevice();

                        }else if(!paymentRequestId.equals(connectedDevice.getPaymentRequestId())){
                            Log.d(TAG, "Cannot process this request device is in use");
                            //this should be an error cause the device should be updated to in use
                            showErrorToast(getString(R.string.error_device_in_use));
                        }

                        break;
                    case Device.STATE_IN_USE:
                        if(connectedDevice.getPaymentRequestId() == null){
                            Log.d(TAG, "Error state, device is in use but no request id, reset device state");
                            //this device should not be in use without a payment request
                            connectedDevice.setState(Device.STATE_READY);
                            updateConnectedDevice();
                            return;
                        }
                        if(paymentRequestId!=null && !connectedDevice.getPaymentRequestId().equals(paymentRequestId)){
                            Log.d(TAG, "Cannot process this request device is in use");
                            //this is an error because device should be processing a transaction already
                            showErrorToast(getString(R.string.error_device_in_use));
                        }else if(!connectedDevice.isProcessing()){
                            //start processing payment
                            Log.d(TAG, "start processing payment request");
                            Log.d(TAG, "update device state");

                            updateMessage(getString(R.string.welcome_processing));

                            startPaymentRequest(paymentRequestId);
                            connectedDevice.setProcessing(true);
                        }
                        break;
                    case Device.STATE_OFFLINE:
                    default:
                        connectDevice();
                        break;
                }
            }else {
                Log.d(TAG, "setting new device ");
                connectedDevice = device;
            }
        }

        @Override
        public void onConnectionUpdateFail(String deviceName, JsonElement recordObject) {
            Log.d(TAG, "Connection update failed for device: "+deviceName);
            updateConnectedDevice();
        }
    };

    private PaymentActionCallback paymentActionCallback = new PaymentActionCallback() {
        @Override
        public void onPaymentStarted(String paymentRequestId) {
            Log.d(TAG, "Payment started for: "+paymentRequestId);
        }

        @Override
        public void onPaymentComplete(String paymentRequestId) {
            Log.d(TAG, "Payment completed for: "+paymentRequestId);

            postPaymentRequest(paymentRequestId);
        }

        @Override
        public void onPaymentCanceled(String paymentRequestId, String message) {
            Log.d(TAG, "Payment canceled for: "+paymentRequestId);
            releasePaymentRequest(paymentRequestId);
        }

        @Override
        public void onPaymentFailed(String paymentRequestId, String message) {
            Log.d(TAG, "Payment failed for: "+paymentRequestId);
            Log.d(TAG, message);
            releasePaymentRequest(paymentRequestId);
        }

    };

    private PaymentRequestCallback paymentRequestCallback = new PaymentRequestCallback() {
        @Override
        public void onPaymentRequestUpdate(String paymentRequestId, PaymentRequest paymentRequest) {
            Log.d(TAG, "Payment Request Update received for: "+paymentRequestId);
            Gson gson = new Gson();
            Log.d(TAG, JsonHelper.getJSONFormattedString(gson.toJson(paymentRequest)));
        }

        @Override
        public void onPaymentRequestUpdateFail(String paymentRequestId, JsonElement recordObject) {
            Log.d(TAG, "Payment Reques Update FAILED for: "+paymentRequestId);
            Log.d(TAG, JsonHelper.getJSONFormattedString(recordObject.toString()));

        }

        @Override
        public void onPaymentConnectionFailure(String message) {
            Log.d(TAG, message);
            if(connectedDevice != null){
                String paymentRequestId = connectedDevice.getPaymentRequestId();
                releasePaymentRequest(paymentRequestId);
            }else {
                Log.d(TAG, "Reconnect device");
                connectDevice();//reset the connection
            }
        }

        @Override
        public void onPaymentRequestDestroyed(String paymentRequestId) {
            Log.d(TAG, "Payment Destroyed: "+paymentRequestId);
            if(connectedDevice != null){
                releasePaymentRequest(paymentRequestId);
            }else {
                connectDevice();//reset the connection
            }
        }
    };

    private void postPaymentRequest(String paymentRequestId){
        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setDeepstreamRecordId(paymentRequestId);

        Gson gson = new Gson();
        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");
        getRestHelper().executePostPayment(getPostPaymentCallback(paymentRequestId), token, gson.toJson(streamRecord));
    }

    private RestCallServiceCallback getPostPaymentCallback(final String paymentRequestId) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                paymentAttempt++;
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                updateMessage(getString(R.string.welcome_complete));
                resetDevice(paymentRequestId);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMessage(getString(R.string.welcome_waiting));
                    }
                }, PAYMENT_COMPLETE_RESET);

            }

            @Override
            public void onFailure(String errorMessage) {
                CustomErrorToast.showWithMessage(WelcomeActivity.this, errorMessage);
                if(shouldRetry(errorMessage)) {
                    updateMessage(String.format(getString(R.string.welcome_retrying), paymentAttempt + 1));
                    postPaymentRequest(paymentRequestId);
                }else{
                    resetDevice(paymentRequestId);
                    updateMessage(getString(R.string.welcome_waiting));
                }
            }
        };
    }

    private boolean shouldRetry(String errorMessage){
        return !errorMessage.contains("payment request has already been completed");
    }

    private void resetDevice(String paymentRequestId){
        releasePaymentRequest(paymentRequestId);
        paymentAttempt = 0;
    }

}
