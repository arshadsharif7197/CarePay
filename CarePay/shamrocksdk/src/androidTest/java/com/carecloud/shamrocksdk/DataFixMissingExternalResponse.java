package com.carecloud.shamrocksdk;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.carecloud.shamrocksdk.connections.DeviceConnection;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.constants.AppConstants;
import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.models.CardData;
import com.carecloud.shamrocksdk.payment.models.PaymentMethod;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.StreamRecord;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.carecloud.shamrocksdk.services.ServiceGenerator;
import com.carecloud.shamrocksdk.services.ServiceInterface;
import com.carecloud.shamrocksdk.services.ServiceRequest;
import com.clover.sdk.v3.base.Reference;
import com.clover.sdk.v3.payments.CardTransaction;
import com.clover.sdk.v3.payments.CardTransactionState;
import com.clover.sdk.v3.payments.CardTransactionType;
import com.clover.sdk.v3.payments.CardType;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Result;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.deepstream.Record;
import retrofit2.Call;
import retrofit2.Response;


import static android.content.Context.MODE_PRIVATE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by lmenendez on 2/25/18
 */

@RunWith(AndroidJUnit4.class)
public class DataFixMissingExternalResponse {

    private final String AUTH = Constants.AUTH_PROD;
    private final String API_KEY = Constants.API_KEY_PROD;
    private final String DEEP_STREAM = Constants.DEEP_STREAM_PROD;
    private final String BASE_URL = Constants.BASE_URL_PROD;
    private final String DEVICE_ID = Constants.DEVICE_ID_PROD;

    @Test
    public void fixMissingData(){
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
                List<ReconstructInfo> reconstructInfoList = parseReconstructionInfo();
                updateDeepStreamObjects(reconstructInfoList);
            }

            @Override
            public void onConnectionFailure(String errorMessage) {

            }

            @Override
            public void onDeviceDisconnected(@Nullable Device device) {

            }
        });

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

    private List<ReconstructInfo> parseReconstructionInfo(){
        List<ReconstructInfo> infoList = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = InstrumentationRegistry.getTargetContext().getAssets()
                    .open("AARA_Batch3.csv");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader lineReader = new BufferedReader(reader);

            String line = lineReader.readLine();
            while(line != null){
                String[] fields = line.split(",");
                ReconstructInfo info = new ReconstructInfo();
                if(fields.length >= 10){
                    info.patientId = fields[0];
                    info.businessEntityId = fields[1];
                    info.effectiveDate = fields[2];
                    info.amount = fields[3];
                    info.deepstreamId = fields[4];
                    info.paymentId = fields[5];
                    info.authCode = fields[6];
                    info.cardBrand = fields[7];
                    info.cardNumber = fields[8];
                    info.orderId = fields[9];

                    infoList.add(info);

                    line = lineReader.readLine();
                }
            }
            lineReader.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return infoList;
    }

    private void updateDeepStreamObjects(List<ReconstructInfo> reconstructInfoList){
        final Gson gson = new Gson();
        for(ReconstructInfo info : reconstructInfoList){
            Payment payment = new Payment();
            payment.setAmount((long) (Double.parseDouble(info.amount)*100));
            payment.setId(info.paymentId);
            payment.setResult(Result.SUCCESS);
            payment.setOffline(false);
            payment.setCreatedTime(System.currentTimeMillis());
            payment.setTaxAmount(0L);

            CardTransaction cardTransaction = new CardTransaction();
            cardTransaction.setAuthCode(info.authCode);
            cardTransaction.setCardholderName("N A");
            cardTransaction.setState(CardTransactionState.CLOSED);
            cardTransaction.setLast4(info.cardNumber);
            cardTransaction.setCardType(CardType.valueOf(info.cardBrand));
            cardTransaction.setType(CardTransactionType.AUTH);
            payment.setCardTransaction(cardTransaction);

            Reference order = new Reference();
            order.setId(info.orderId);
            payment.setOrder(order);

            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setType(PaymentMethod.PAYMENT_METHOD_NEW_CARD);

            CardData cardData = new CardData();
            cardData.setCardholderName("N A");
            cardData.setCardNumber(info.cardNumber);
            cardData.setCardType(info.cardBrand);
            cardData.setExpiration("1218");
            cardData.setTokenizationService("clover");
            cardData.setToken("na");
            paymentMethod.setCardData(cardData);


            Record paymentRecord = DevicePayment.getPaymentRecord(info.deepstreamId);
            assertNotNull(paymentRecord);
            FullPaymentRequest paymentRequestUpdate = gson.fromJson(paymentRecord.get(), FullPaymentRequest.class);
            if(paymentRequestUpdate.state.equals(StateDef.STATE_COMPLETED)){
                continue;
            }
            if(paymentRequestUpdate != null) {
                paymentRequestUpdate.transactionResponse = gson.fromJson(payment.getJSONObject().toString(), JsonObject.class);
                paymentRequestUpdate.paymentMethod = paymentMethod;
                paymentRequestUpdate.state = StateDef.STATE_RECORDING;
            }

            Log.i("DataFix", gson.toJson(paymentRequestUpdate));
            final String paymentRequestId = info.deepstreamId;
            paymentRecord.set(gson.toJsonTree(paymentRequestUpdate));
            paymentRecord.set("metadata.prepped_for_processing", false);

            PaymentRequest paymentRequestAck = DevicePayment.getPaymentAck(paymentRequestId);
            assertNotNull(paymentRequestUpdate);
            assertNotNull(paymentRequestAck);
            assertEquals(gson.toJson(paymentRequestUpdate.transactionResponse), gson.toJson(paymentRequestAck.getTransactionResponse()));
            postPaymentRequest(paymentRequestId);

        }
    }

    private void postPaymentRequest(String paymentRequestId){
        Map<String, String> headers = new HashMap<>();
        headers.put("x-token-type", "kms");
        headers.put("Authorization", AUTH.replace("\n", ""));
        headers.put("x-api-key", API_KEY);

        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setDeepstreamRecordId(paymentRequestId);

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setMethod("POST");
        serviceRequest.setUrl(BASE_URL + "carepay/payments");

        String jsonBody = new Gson().toJson(streamRecord);
        ServiceInterface workflowService = ServiceGenerator.getInstance().createService(ServiceInterface.class, headers);
        Call<JsonElement> call = workflowService.executePost(serviceRequest.getUrl(), jsonBody);
        try {
            Response<JsonElement> response = call.execute();
//            assertNotNull(response.body());
            if(response.body() == null) {
                Log.i("Data Errored: ", paymentRequestId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void setAuthToken(String authToken, Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(AppConstants.DEEPSTREAM_AUTH_KEY, authToken);
        editor.commit();
    }


    private static class Constants {
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
    }


    private static class ReconstructInfo{
        String patientId;
        String businessEntityId;
        String effectiveDate;
        String amount;
        String deepstreamId;
        String paymentId;
        String authCode;
        String cardBrand;
        String cardNumber;
        String orderId;
    }


    private static class FullPaymentRequest {
        @SerializedName("state")
        String state;

        @SerializedName("execution")
        String executionType;

        @SerializedName("organization_id")
        String organizationId;

        @SerializedName("payment_profile_id")
        String paymentProfileId;

        @SerializedName("amount")
        double amount;

        @SerializedName("payment_method")
        PaymentMethod paymentMethod;

        @SerializedName("provider_id")
        String providerId;

        @SerializedName("location_id")
        String locationId;

        @SerializedName("routeToken")
        String routeToken;

        @SerializedName("line_items")
        List<JsonElement> paymentLineItems = new ArrayList<>();

        @SerializedName("external_transaction_response")
        JsonElement transactionResponse;

        @SerializedName("metadata")
        JsonElement metadata;

        @SerializedName("comments")
        String comments;

        @SerializedName("deepstream_record_id")
        String deepStreamRecordId;

        @SerializedName("payment_captured")
        boolean paymentCaptured;

        @SerializedName("papi_charges")
        JsonElement papiCharges;

        @SerializedName("cc_credit_transactions")
        JsonElement creditTransactions;

        @SerializedName("papi_errors")
        JsonElement papiErrors;

        @SerializedName("cc_errors")
        JsonElement ccErrors;

        @SerializedName("queuing_errors")
        JsonElement queueingErrors;

        @SerializedName("request_errors")
        JsonElement requestErrors;

        @SerializedName("queued")
        boolean queued;

        @SerializedName("retries_exhausted")
        boolean retriesExhausted;

        @SerializedName("queuing_retries_exhausted")
        boolean queuingExhausted;

        @SerializedName("updated_at")
        String updated;

        @SerializedName("payment_group_id")
        String paymentGroupId;

        @SerializedName("current_request_error_id")
        String currentErrorId;

    }
}
