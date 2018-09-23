package com.carecloud.carepay.mini.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.metadata.DeviceMetadata;
import com.carecloud.carepay.mini.models.queue.QueuePaymentRecord;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.services.QueueUploadService;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceCallback;
import com.carecloud.carepay.mini.utils.ChipInterceptorUtil;
import com.carecloud.carepay.mini.utils.Defs;
import com.carecloud.carepay.mini.utils.JsonHelper;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;
import com.carecloud.shamrocksdk.connections.DeviceConnection;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.connections.models.defs.DeviceDef;
import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.DeviceRefund;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.interfaces.RefundRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.RefundRequest;
import com.carecloud.shamrocksdk.utils.AuthorizationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.newrelic.agent.android.NewRelic;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lmenendez on 7/12/17
 */

public class WelcomeActivity extends FullScreenActivity {
    private static final String TAG = WelcomeActivity.class.getName();
    private static final int MAX_RETRIES = 2;
    private static final int CONNECTION_RETRY_DELAY = 1000;
    private static final int PAYMENT_COMPLETE_RESET = 1000 * 3;
    private static final int POST_RETRY_DELAY = 1000 * 10;
    private static final int DEVICE_KEEP_ALIVE_PERIOD = 1000 * 30;
    private static final int MAX_FAIL_COUNT = 15;

    private ApplicationHelper applicationHelper;
    private TextView message;
    private Handler handler;

    private Device connectedDevice;
    private DeviceMetadata deviceMetadata;

    private int paymentAttempt = 0;
    private boolean isDisconnecting = false;
    private boolean isResumed = false;
    private boolean hasNetworkFailed = false;

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    private int failCount = 0;

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        applicationHelper = (ApplicationHelper) getApplication();
        handler = new Handler();

        setContentView(R.layout.activity_welcome);

        message = (TextView) findViewById(R.id.welcome_message);
        TextView environment = (TextView) findViewById(R.id.environment_label);
        environment.setText(HttpConstants.getEnvironment());

        setupNewRelic();
        setDeviceMetadata();

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectionStateChangedReceiver, intentFilter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        setPracticeDetails();
        if(connectedDevice == null || !connectedDevice.isProcessing()) {
            connectDevice();
            handler.removeCallbacksAndMessages(null);
            scheduleDeviceRefresh();
            //Acquire wakelock
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if(wakeLock == null) {
                wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
            }
            wakeLock.acquire();

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if(wifiLock == null){
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
            }
            wifiLock.acquire();
            ChipInterceptorUtil.getInstance().startIntercept(this,
                    ChipInterceptorUtil.CLOVER_CARD_INTERCEPT_ACTION);
        }

    }

    @Override
    protected void onStop(){
        if(connectedDevice == null || !connectedDevice.isProcessing()) {
            disconnectDevice();
            handler.removeCallbacks(deviceStateRefresh);

            if(wakeLock != null){
                wakeLock.release();
            }

            if(wifiLock != null){
                wifiLock.release();
            }
            ChipInterceptorUtil.getInstance().stopIntercept(this);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(connectionStateChangedReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        isResumed = false;
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
                File photoFile = new File(imageUrl);
                Picasso.with(this)
                        .load(photoFile)
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

        Log.d(TAG, selectedPractice.toString());
        Log.d(TAG, "Device Name: " + getApplicationHelper().getApplicationPreferences().getDeviceName());
        Log.d(TAG, "Location Id: " + getApplicationHelper().getApplicationPreferences().getLocationId());

        View settings = findViewById(R.id.button_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void connectDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isDisconnecting = false;
                String id = applicationHelper.getApplicationPreferences().getDeviceId();
                DeviceConnection.connect(WelcomeActivity.this, id, connectionCallback, connectionActionCallback);
            }
        });
    }

    private void disconnectDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isDisconnecting) {
                    isDisconnecting = true;
                    String id = applicationHelper.getApplicationPreferences().getDeviceId();
                    DeviceConnection.disconnect(WelcomeActivity.this, id, null);
                }
            }
        });
    }

    /**
     * This can be used to disconnect the device and expect that it will trigger a reconnect once the disconnect is complete
     * not currently being used in this implementation
     */
    private void reconnectDevice(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isDisconnecting) {
                    isDisconnecting = true;
                    String id = applicationHelper.getApplicationPreferences().getDeviceId();
                    DeviceConnection.disconnect(WelcomeActivity.this, id, connectionCallback);
                }
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

    private void startRefundRequest(final String refundRequestId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DeviceRefund.handleRefundRequest(WelcomeActivity.this, refundRequestId, refundRequestCallback, paymentActionCallback);
            }
        });
    }


    private void releasePaymentRequest(final String paymentRequestId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedDevice.setState(DeviceDef.STATE_READY);
                connectedDevice.setProcessing(false);
                connectedDevice.setPaymentRequestId(null);
                connectedDevice.setRefunding(false);
                updateConnectedDevice();
                if(paymentRequestId != null) {
                    DevicePayment.releasePaymentRequest(WelcomeActivity.this, paymentRequestId);
                }
            }
        });
    }

    private void releaseRefundRequest(final String refundRequestId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectedDevice.setState(DeviceDef.STATE_READY);
                connectedDevice.setProcessing(false);
                connectedDevice.setPaymentRequestId(null);
                updateConnectedDevice();
                if(refundRequestId != null) {
                    DeviceRefund.releaseRefundRequest(WelcomeActivity.this, refundRequestId);
                }
            }
        });
    }


    private ConnectionCallback connectionCallback = new ConnectionCallback() {
        @Override
        public void startDeviceConnection() {
            updateMessage(getString(R.string.welcome_connecting));
        }

        @Override
        public void onDeviceConnected(Device device) {
            updateMessage(getString(R.string.welcome_waiting));
            connectedDevice = device;
            connectedDevice.setMetadata(new Gson().toJsonTree(deviceMetadata));
            updateConnectedDevice();
            failCount = 0;
        }

        @Override
        public void onConnectionFailure(String errorMessage) {
            Log.d(TAG, "Connection Failure: " + errorMessage);

            if(!hasNetworkFailed && isResumed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectDevice();
                            }
                        }, CONNECTION_RETRY_DELAY * failCount);
                    }
                });
            }else{
                Looper.getMainLooper().getThread().interrupt();
                displayConnectionError();
            }

            if(failCount < MAX_FAIL_COUNT) {
                failCount++;
            }
        }

        @Override
        public void onDeviceDisconnected(Device device) {
            //reconnect if disconnected
            Log.d(TAG, "Device Disconnected");
            isDisconnecting = false;

            if(!hasNetworkFailed) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        connectDevice();
                    }
                }, CONNECTION_RETRY_DELAY);
            }
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
                    case DeviceDef.STATE_READY: //Device is ready to process payments
                        if(paymentRequestId == null ) {
                            if(!connectedDevice.getState().equals(device.getState())) {
                                updateConnectedDevice();
                            }
                            updateMessage(getString(R.string.welcome_waiting));
                            return;
                        }
                        if(connectedDevice.getPaymentRequestId() == null) {
                            //this is a net new payment request
                            Log.d(TAG, "Device is ready & payment request received, update to in-use");
                            connectedDevice = device;
                            connectedDevice.setState(DeviceDef.STATE_IN_USE);

                            updateConnectedDevice();

                        }else if(!paymentRequestId.equals(connectedDevice.getPaymentRequestId())){
                            Log.d(TAG, "Cannot process this request device is in use");
                            //this should be an error cause the device should be updated to in use
                            showErrorToast(getString(R.string.error_device_in_use));
                        }

                        break;
                    case DeviceDef.STATE_IN_USE:
                        if(connectedDevice.getPaymentRequestId() != null && !connectedDevice.getState().equals(device.getState()) ) {
                            updateConnectedDevice();
                        }
                        else if(connectedDevice.getPaymentRequestId() == null){
                            Log.d(TAG, "Error state, device is in use but no request id, reset device state");
                            //this device should not be in use without a payment request
                            connectedDevice.setState(DeviceDef.STATE_READY);
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

                            if(device.isRefunding()){
                                startRefundRequest(paymentRequestId);
                            }else {
                                startPaymentRequest(paymentRequestId);
                            }
                            connectedDevice.setProcessing(true);
                        }
                        break;
                    case DeviceDef.STATE_OFFLINE:
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
        public void onPaymentComplete(String paymentRequestId, JsonElement requestObject) {
            Log.d(TAG, "Payment completed for: "+paymentRequestId);

            if(connectedDevice.isRefunding()){
                postRefundRequest(paymentRequestId, requestObject);
            }else {
                postPaymentRequest(paymentRequestId, requestObject);
            }
        }

        @Override
        public void onPaymentCompleteWithError(String paymentRequestId, JsonElement paymentPayload, String errorMessage) {
            logNewRelicPaymentError(errorMessage, paymentPayload.toString());

            if(connectedDevice.isRefunding()){
                postRefundRequest(paymentRequestId, paymentPayload);
            }else {
                postPaymentRequest(paymentRequestId, paymentPayload);
            }

/*
            Gson gson = new Gson();
            QueueUnprocessedPaymentRecord unprocessedPaymentRecord = new QueueUnprocessedPaymentRecord();
            unprocessedPaymentRecord.setPaymentRequestId(paymentRequestId);
            unprocessedPaymentRecord.setRefund(connectedDevice.isRefunding());
            unprocessedPaymentRecord.setPayload(gson.toJson(paymentPayload));
            unprocessedPaymentRecord.save();

            launchQueueService();
            if(connectedDevice.isRefunding()){
                updateMessage(getString(R.string.welcome_complete_refund));
            }else {
                updateMessage(getString(R.string.welcome_complete));
            }
            resetDevice(paymentRequestId);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateMessage(getString(R.string.welcome_waiting));
                }
            }, PAYMENT_COMPLETE_RESET);
*/
        }

        @Override
        public void onPaymentCanceled(String paymentRequestId, String message) {
            Log.d(TAG, "Payment canceled for: "+paymentRequestId);
            releasePaymentRequest(paymentRequestId);
        }

        @Override
        public void onPaymentFailed(String paymentRequestId, String message) {
            String printMessage = "Payment failed for: "+paymentRequestId;
            logNewRelicPaymentError(message, printMessage);
            Log.d(TAG, printMessage);
            Log.d(TAG, message);
            showErrorToast(message);
            releasePaymentRequest(paymentRequestId);
        }

        @Override
        public void onConnectionFailed(String message) {
            connectionCallback.onConnectionFailure(message);
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
            Log.d(TAG, message!=null?message:"Connection Failed");
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

    private RefundRequestCallback refundRequestCallback = new RefundRequestCallback() {
        @Override
        public void onRefundRequestUpdate(String refundRequestId, RefundRequest refundRequest) {
            Log.d(TAG, "Refund Request Update received for: "+refundRequestId);
            Gson gson = new Gson();
            Log.d(TAG, JsonHelper.getJSONFormattedString(gson.toJson(refundRequest)));
        }

        @Override
        public void onRefundRequestUpdateFail(String refundRequestId, JsonElement recordObject) {
            Log.d(TAG, "Refund Request Update FAILED for: "+refundRequestId);
            Log.d(TAG, JsonHelper.getJSONFormattedString(recordObject.toString()));
        }

        @Override
        public void onRefundConnectionFailure(String message) {
            Log.d(TAG, message!=null?message:"Connection Failed");
            if(connectedDevice != null){
                String paymentRequestId = connectedDevice.getPaymentRequestId();
                releaseRefundRequest(paymentRequestId);
            }else {
                Log.d(TAG, "Reconnect device");
                connectDevice();//reset the connection
            }
        }

        @Override
        public void onRefundRequestDestroyed(String refundRequestId) {
            Log.d(TAG, "Payment Destroyed: "+refundRequestId);
            if(connectedDevice != null){
                releaseRefundRequest(refundRequestId);
            }else {
                connectDevice();//reset the connection
            }
        }
    };

    private void postPaymentRequest(String paymentRequestId, JsonElement requestObject){
        Gson gson = new Gson();
        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");
        getRestHelper().executePostPayment(getPostPaymentCallback(paymentRequestId, false, requestObject), token, gson.toJson(requestObject));
    }

    private void postRefundRequest(String paymentRequestId, JsonElement requestObject){
        Gson gson = new Gson();
        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");
        getRestHelper().executePostRefund(getPostPaymentCallback(paymentRequestId, true, requestObject), token, gson.toJson(requestObject));
    }


    private RestCallServiceCallback getPostPaymentCallback(final String paymentRequestId, final boolean isRefund, final JsonElement requestObject) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                paymentAttempt++;
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                if(isRefund){
                    updateMessage(getString(R.string.welcome_complete_refund));
                }else {
                    updateMessage(getString(R.string.welcome_complete));
                }
                resetDevice(paymentRequestId);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMessage(getString(R.string.welcome_waiting));
                    }
                }, PAYMENT_COMPLETE_RESET);

                //this will clean up any pending requests that may have been queued since now we have been able to successfully process a request
                launchQueueService();
            }

            @Override
            public void onFailure(String errorMessage) {
                logNewRelicPaymentError(errorMessage, requestObject.toString());
                if(shouldRetry(errorMessage)) {
                    CustomErrorToast.showWithMessage(WelcomeActivity.this, errorMessage);
                    updateMessage(String.format(getString(R.string.welcome_retrying), paymentAttempt + 1));
//                    if(verifyPersisted(errorMessage, paymentRequestId, isRefund, requestObject)){
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isRefund){
                                    postRefundRequest(paymentRequestId, requestObject);
                                }else {
                                    postPaymentRequest(paymentRequestId, requestObject);
                                }
                            }
                        }, paymentAttempt * POST_RETRY_DELAY);
//                    }
                }else{
                    if(paymentAttempt >= MAX_RETRIES){
//                        if(!verifyPersisted(errorMessage, paymentRequestId, isRefund, requestObject)) {
//                            Gson gson = new Gson();
//                            QueueUnprocessedPaymentRecord unprocessedPaymentRecord = new QueueUnprocessedPaymentRecord();
//                            unprocessedPaymentRecord.setPaymentRequestId(paymentRequestId);
//                            unprocessedPaymentRecord.setRefund(connectedDevice.isRefunding());
//                            unprocessedPaymentRecord.setPayload(gson.toJson(requestObject));
//                            unprocessedPaymentRecord.save();
//                        }else{
                            QueuePaymentRecord queuePaymentRecord = new QueuePaymentRecord();
                            queuePaymentRecord.setPaymentRequestId(paymentRequestId);
                            queuePaymentRecord.setRefund(isRefund);
                            queuePaymentRecord.setRequestObject(requestObject);
                            queuePaymentRecord.save();
//                        }
                        launchQueueService();
                    }
                    resetDevice(paymentRequestId);
                    updateMessage(getString(R.string.welcome_waiting));
                }
            }
        };
    }

    private void launchQueueService(){
        Intent queueService = new Intent(this, QueueUploadService.class);
        startService(queueService);
    }

    private boolean shouldRetry(String errorMessage){
        return !errorMessage.contains("payment request has already been completed") && paymentAttempt <= MAX_RETRIES;
    }

/*    private boolean verifyPersisted(String errorMessage, final String requestId, boolean isRefunding, final JsonElement requestObject){
        if(errorMessage != null && errorMessage.contains("transaction response needed")){
            final Gson gson = new Gson();
            if(!isRefunding){
                PaymentRequest ackRequest = DevicePayment.getPaymentAck(requestId);
                if (ackRequest != null &&
                        (!ackRequest.getState().equals(StateDef.STATE_CAPTURED) ||
                                ackRequest.getTransactionResponse() == null ||
                                ackRequest.getPaymentMethod() == null ||
                                ackRequest.getPaymentMethod().getCardData() == null)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PaymentRequest paymentRequest = gson.fromJson(requestObject, PaymentRequest.class);//this is the expected payment request payload
                            DevicePayment.updatePaymentRequest(WelcomeActivity.this, requestId, paymentRequest, paymentRequestCallback);
                            postPaymentRequest(requestId, requestObject);
                        }
                    }, (paymentAttempt * POST_RETRY_DELAY * 2));//requires waiting double because Shamrock Payments is messing with the record
                }
            }else {
                RefundRequest ackRequest = DeviceRefund.getRefundAck(requestId);
                if (ackRequest != null &&
                        (!ackRequest.getState().equals(StateDef.STATE_CAPTURED) ||
                                ackRequest.getTransactionResponse() == null)) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RefundRequest refundRequest = gson.fromJson(requestObject, RefundRequest.class);//this is the expected refund request payload
                            DeviceRefund.updateRefundRequest(WelcomeActivity.this, requestId, refundRequest, refundRequestCallback);
                            postRefundRequest(requestId, requestObject);
                        }
                    }, (paymentAttempt * POST_RETRY_DELAY * 2));//requires waiting double because Shamrock Payments is messing with the record
                }
            }
            return false;
        }
        return true;
    }*/

    private void resetDevice(String paymentRequestId){
        releasePaymentRequest(paymentRequestId);
        paymentAttempt = 0;
//        reconnectDevice();
    }

    private BroadcastReceiver connectionStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()!= null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(isResumed && networkInfo != null && networkInfo.isConnected()){
                    hasNetworkFailed = false;
                    connectDevice();
                }else{
                    hasNetworkFailed = true;
                    displayConnectionError();
                    Log.w(TAG, "Activity is Resumed: "+isResumed);
                    Log.w(TAG, "Network Info: "+ (networkInfo != null? networkInfo.getState() : " NULL"));
                }
            }
        }
    };

    private void scheduleDeviceRefresh(){
        handler.postDelayed(deviceStateRefresh, DEVICE_KEEP_ALIVE_PERIOD);
    }

    private Runnable deviceStateRefresh = new Runnable() {
        @Override
        public void run() {
            if(!hasNetworkFailed && (connectedDevice == null || !connectedDevice.getState().equals(DeviceDef.STATE_IN_USE))){
                connectDevice();
            }else if(hasNetworkFailed){
                displayConnectionError();
            }
            scheduleDeviceRefresh();
        }
    };

    private void logNewRelicPaymentError(String errorMessage, Object payload){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Error Message", errorMessage);
        eventMap.put("Request Payload", payload);
        eventMap.put("DeepStream ID", connectedDevice.getPaymentRequestId());
        eventMap.put("Is Refunding", connectedDevice.isRefunding());
        eventMap.put("Device ID", connectedDevice.getDeviceId());
        eventMap.put("Device Name", connectedDevice.getName());

        String eventType = connectedDevice.isRefunding() ? "RefundRequestFail" : "PaymentRequestFail";

        NewRelic.recordCustomEvent(eventType, eventMap);

    }

    private void logNewRelicConnectionError(String errorMessage){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Error Message", errorMessage);
        eventMap.put("Device ID", connectedDevice.getDeviceId());
        eventMap.put("Device Name", connectedDevice.getName());
        eventMap.put("Device Record", connectedDevice);

        String eventType = "Connection Error";

        NewRelic.recordCustomEvent(eventType, eventMap);

    }


    private void setupNewRelic(){
        NewRelic.setUserId(applicationHelper.getApplicationPreferences().getDeviceId());
        NewRelic.setAttribute(getString(R.string.key_practice_id),
                applicationHelper.getApplicationPreferences().getPracticeId());
        NewRelic.setAttribute(getString(R.string.key_practice_name),
                applicationHelper.getApplicationPreferences().getUserPracticeDTO().getPracticeName());
        NewRelic.setAttribute(getString(R.string.key_location_id),
                applicationHelper.getApplicationPreferences().getLocationId());
        NewRelic.setAttribute(getString(R.string.key_device_name),
                applicationHelper.getApplicationPreferences().getDeviceName());
    }

    private void setDeviceMetadata(){
        deviceMetadata = new DeviceMetadata();
        deviceMetadata.setPracticeID(applicationHelper.getApplicationPreferences().getPracticeId());
        deviceMetadata.setPracticeName(applicationHelper.getApplicationPreferences().getUserPracticeDTO().getPracticeName());
        deviceMetadata.setLocationID(applicationHelper.getApplicationPreferences().getLocationId());
    }

    private void displayConnectionError(){
        String error;
        if(hasNetworkFailed){
            error = getString(R.string.welcome_network_error);
        }else if(!canConnectToInternet()){
            error = getString(R.string.welcome_internet_error);
        }else {
            error = getString(R.string.welcome_deepstream_error);
        }
        if(!message.getText().toString().equals(error)){
            //only log to new relic if the error is not already set to prevent duplicate logs
            // since this is called in several places
            logNewRelicConnectionError(error);
        }
        updateMessage(error);
    }

    private boolean canConnectToInternet(){
        try {
            String command = "ping -c 1 8.8.8.8";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }catch (IOException | InterruptedException ex){
            ex.printStackTrace();
        }
        return false;
    }
}
