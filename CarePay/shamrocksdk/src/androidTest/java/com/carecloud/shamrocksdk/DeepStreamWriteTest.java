package com.carecloud.shamrocksdk;

import android.content.Context;
import android.content.SharedPreferences;


import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.carecloud.shamrocksdk.connections.DeviceConnection;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.constants.AppConstants;
import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.activities.CloverPaymentActivity;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.clover.sdk.v3.payments.Payment;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.deepstream.Record;


import static android.content.Context.MODE_PRIVATE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DeepStreamWriteTest {

    private final String AUTH = Constants.AUTH_DEV;
    private final String API_KEY = Constants.API_KEY_DEV;
    private final String DEEP_STREAM = Constants.DEEP_STREAM_DEV;
    private final String BASE_URL = Constants.BASE_URL_DEV;
    private final String DEVICE_ID = Constants.DEVICE_ID_DEV;
    private final String PAYMENT_REQUEST = Constants.PAYMENT_REQUEST_DEV;
    private final String WIPE_PAYMENT = Constants.WIPE_PAYMENT_DEV;


    @Test
    public void testDeepStreamSet() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        setAuthToken(AUTH, appContext);

        ShamrockSdk.init(API_KEY, DEEP_STREAM, BASE_URL);
        connectDevice(appContext, DEVICE_ID, new ConnectionCallback() {
            @Override
            public void startDeviceConnection() {

            }

            @Override
            public void onDeviceConnected(Device device) {
                assertEquals(DEVICE_ID, device.getDeviceId());
                InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                    @Override
                    public void run() {
                        testSetRecord(250);
                    }
                });
            }

            @Override
            public void onConnectionFailure(String errorMessage) {

            }

            @Override
            public void onDeviceDisconnected(@Nullable Device device) {

            }
        });

    }

    private void setAuthToken(String authToken, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstants.DEEPSTREAM_AUTH_KEY, authToken);
        editor.commit();
    }

    private void connectDevice(Context context, String deviceId, ConnectionCallback connectionCallback){
        DeviceConnection.connect(context, deviceId, connectionCallback, new ConnectionActionCallback() {
            @Override
            public void onConnectionError(String deviceName, String errorCode, String errorMessage) {

            }

            @Override
            public void onConnectionDestroyed(String deviceName) {

            }

            @Override
            public void onConnectionUpdate(String deviceName, Device device) {

            }

            @Override
            public void onConnectionUpdateFail(String deviceName, JsonElement recordObject) {

            }
        });
    }

    private void testSetRecord(int attempts){
        Record record = DevicePayment.getPaymentRecord(PAYMENT_REQUEST);
        PaymentRequest paymentRequest = DevicePayment.getPaymentAck(PAYMENT_REQUEST);

        CloverPaymentActivity cloverPaymentActivity = new CloverPaymentActivity();
        cloverPaymentActivity.testSetStatic(record, new PaymentActionCallback() {
            @Override
            public void onPaymentStarted(String paymentRequestId) {

            }

            @Override
            public void onPaymentComplete(String paymentRequestId, JsonElement requestObject) {
                PaymentRequest ackRequest = DevicePayment.getPaymentAck(paymentRequestId);
                assertNotNull(ackRequest);
                assertNotNull(ackRequest.getTransactionResponse());
            }

            @Override
            public void onPaymentCompleteWithError(String paymentRequestId, JsonElement paymentPayload, String errorMessage) {
                assertFalse(true);
            }

            @Override
            public void onPaymentCanceled(String paymentRequestId, String message) {

            }

            @Override
            public void onPaymentFailed(String paymentRequestId, String message) {

            }

            @Override
            public void onConnectionFailed(String message) {

            }
        });

        Gson gson = new Gson();
        Payment payment = gson.fromJson(Constants.PAYMENT_JSON, Payment.class);

        for (int i=0; i<attempts; i++) {
            cloverPaymentActivity.testDeepStreamSet(payment, paymentRequest);

            PaymentRequest wipeRequest = gson.fromJson(WIPE_PAYMENT, PaymentRequest.class);
            record.set(gson.toJsonTree(wipeRequest));
            PaymentRequest ackRequest = DevicePayment.getPaymentAck(PAYMENT_REQUEST);
            assertNotNull(ackRequest);
            assertNull(ackRequest.getTransactionResponse());

        }

    }

    private static class Constants {
        private static final String PAYMENT_JSON = "{\n" +
                "            \"result\": \"SUCCESS\",\n" +
                "            \"createdTime\": 1514443441163,\n" +
                "            \"taxRates\": {\n" +
                "              \"elements\": []\n" +
                "            },\n" +
                "            \"offline\": false,\n" +
                "            \"cardTransaction\": {\n" +
                "              \"cardholderName\": \"MARA P GUZMAN\",\n" +
                "              \"authCode\": \"530533\",\n" +
                "              \"entryType\": \"EMV_CONTACT\",\n" +
                "              \"token\": \"8120397032934287\",\n" +
                "              \"extra\": {\n" +
                "                \"applicationLabel\": \"5553204445424954\",\n" +
                "                \"applicationIdentifier\": \"A0000000980840\",\n" +
                "                \"authorizingNetworkName\": \"ACCEL\",\n" +
                "                \"cvmResult\": \"ONLINE_PIN\",\n" +
                "                \"routingIndicator\": \"D\"\n" +
                "              },\n" +
                "              \"state\": \"CLOSED\",\n" +
                "              \"referenceId\": \"736200503511\",\n" +
                "              \"last4\": \"4287\",\n" +
                "              \"type\": \"AUTH\",\n" +
                "              \"cardType\": \"VISA\"\n" +
                "            },\n" +
                "            \"id\": \"6HCCMBK3R093T\",\n" +
                "            \"amount\": 3900,\n" +
                "            \"taxAmount\": 0,\n" +
                "            \"order\": {\n" +
                "              \"id\": \"8KTAEGNH7QW6E\"\n" +
                "            },\n" +
                "            \"tender\": {\n" +
                "              \"id\": \"EDDG7YA642QVM\",\n" +
                "              \"enabled\": true,\n" +
                "              \"visible\": false,\n" +
                "              \"labelKey\": \"com.clover.tender.debit_card\",\n" +
                "              \"label\": \"Debit Card\",\n" +
                "              \"opensCashDrawer\": false,\n" +
                "              \"editable\": false\n" +
                "            },\n" +
                "            \"employee\": {\n" +
                "              \"id\": \"8D9VN3TE9QVCP\"\n" +
                "            }\n" +
                "          }\n";

        private static final String WIPE_PAYMENT_DEV = "{  \n" +
                "   \"execution\":\"clover\",\n" +
                "   \"payment_method\":null,\n" +
                "   \"line_items\":[  \n" +
                "      {  \n" +
                "         \"type\":\"unapplied\",\n" +
                "         \"description\":\"Unapplied Payment\",\n" +
                "         \"amount\":10,\n" +
                "         \"papi_processed\":false,\n" +
                "         \"type_id\":null,\n" +
                "         \"provider_id\":\"db719498-1f8c-45b4-bee7-778c1e4c2eec\",\n" +
                "         \"location_id\":\"1d5b98f3-2e8b-4928-9828-44277a2738d1\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"amount\":10,\n" +
                "   \"payment_profile_id\":\"a92b35a3-bc31-44f5-a984-6a3f6bc03372\",\n" +
                "   \"transaction_response\":null,\n" +
                "   \"metadata\":{  \n" +
                "      \"user_id\":\"a8075508-317b-4714-828e-8872c46fd313\",\n" +
                "      \"effective_date\":\"Tue Feb 20 2018 21:53:17 GMT-0500 (EST)\",\n" +
                "      \"business_entity_id\":\"77b81aa8-1155-4da7-9fd9-2f6967b09a93\",\n" +
                "      \"patient_id\":\"b7f957ca-22a6-4979-b731-38e3c332cb95\"\n" +
                "   },\n" +
                "   \"comments\":\"\",\n" +
                "   \"organization_id\":\"cffc3d2a-df7a-4209-9696-188f2ac199be\",\n" +
                "   \"deepstream_record_id\":\"payment_request/8e9a10e12dff4672b1f344aa73e336e0\",\n" +
                "   \"state\":\"Initialized\",\n" +
                "   \"created_at\":\"2018-02-21T02:53:17.764Z\",\n" +
                "   \"updated_at\":\"2018-02-21T02:53:17.969Z\"\n" +
                "}";

        private static final String WIPE_PAYMENT_PROD = "{  \n" +
                "   \"comments\":\"\",\n" +
                "   \"execution\":\"clover\",\n" +
                "   \"metadata\":{  \n" +
                "      \"patient_id\":\"c8ce543f-40b7-46ed-a5bb-5d6f928951ad\",\n" +
                "      \"user_id\":\"76a66b9c-916b-44ad-a6d2-46de2e373a99\",\n" +
                "      \"business_entity_id\":\"ac6e00ee-5a80-49bf-8c67-f3f2218e6437\",\n" +
                "      \"effective_date\":\"Wed Feb 21 12:37:09 GMT-0500 2018\"\n" +
                "   },\n" +
                "   \"organization_id\":\"574b3556-76e5-43bf-be97-0a379d54453b\",\n" +
                "   \"line_items\":[  \n" +
                "      {  \n" +
                "         \"type\":\"unapplied\",\n" +
                "         \"description\":\"Unapplied Payment\",\n" +
                "         \"location_id\":\"3d3900e4-ba7f-405e-874c-ba8a652bb3f0\",\n" +
                "         \"provider_id\":\"a5868d9b-c887-437a-8abf-7cd9ab42aac1\",\n" +
                "         \"amount\":10.0,\n" +
                "         \"type_id\":0\n" +
                "      }\n" +
                "   ],\n" +
                "   \"payment_profile_id\":\"1a48a184-d6ef-4b59-99e9-2a2940973373\",\n" +
                "   \"state\":\"Waiting\",\n" +
                "   \"amount\":10.0\n" +
                "}";

        private static final String AUTH_PROD = "AQIDAHiA1KUjRgU9NifWfQZ4NIQ9zX/7s8dXkoNEskI+6bqZ3QHOebn4oTGp\n" +
                "F3No1AeWSDk3AAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEH\n" +
                "ATAeBglghkgBZQMEAS4wEQQMvoMAbCU1HKtNh6JCAgEQgDsAHWwzg82bQy5U\n" +
                "eZUIwTe8KiDKWM0yZSd+mfRmGifZY1ZU5vMYMjbTkzJd9yqX1OWglKrCYIq3\n" +
                "LLm0kQ==\n";

        private static final String AUTH_DEV = "AQIDAHipiN3SJ/bPmWUH9GmeylZNfT0Rnu59g93qgY8n6Ph1wgEouy32g+MZ\n" +
                "NFL0VDfiKI0gAAAAfjB8BgkqhkiG9w0BBwagbzBtAgEAMGgGCSqGSIb3DQEH\n" +
                "ATAeBglghkgBZQMEAS4wEQQM5Q9g+n3PANjafaNBAgEQgDvIBE6ab5R5YnY4\n" +
                "lRbnoyNwDgBy+K6OvHVUJRIZh+74L4a7f/EZ4HHOpLeGBL5MQyerRS9hE4D1\n" +
                "1GdONw==\n";

        private static final String API_KEY_DEV = "jLA75b9L3I5r5lkuqMZQF2v41kyPBMLZ29OvGciV";

        private static final String API_KEY_PROD = "m4I4rb4DW5469Jzn1Glev3kKl6RvWT6o8guq2NA9";

        private static final String DEEP_STREAM_DEV = "wss://deepstream.development.carecloud.com:443";

        private static final String DEEP_STREAM_PROD = "wss://deepstream.carecloud.com:443";

        private static final String BASE_URL_DEV = "https://payments.development.carecloud.com/";

        private static final String BASE_URL_PROD = "https://payments.carecloud.com/";

        private static final String DEVICE_ID_DEV = "f382b563-56f0-47e9-ba9d-6993fcdb3a37";

        private static final String DEVICE_ID_PROD = "6f8c5d26-607d-46e4-9c75-697460261eb3";

        private static final String PAYMENT_REQUEST_DEV = "payment_request/8e9a10e12dff4672b1f344aa73e336e0";

        private static final String PAYMENT_REQUEST_PROD = "payment_request/ac5804eab6024218b3cbde38db7d82ab";
    }
}
