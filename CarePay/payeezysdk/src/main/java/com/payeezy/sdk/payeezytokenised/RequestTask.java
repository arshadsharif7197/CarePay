package com.payeezy.sdk.payeezytokenised;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.payeezy.client.FirstAPIClientV2Helper;
import com.payeezy.client.domain.v2.TransactionResponse;
import com.payeezy.client.domain.v2.UserTransactionResponse;

@SuppressLint("DefaultLocale")
public class RequestTask extends AsyncTask<String, String, String> {

    private FirstAPIClientV2Helper clientHelper = new FirstAPIClientV2Helper();

    public RequestTask() {
    }

    private String statusString = "";
    private String splitter = "~~~~~~~~";


    @Override
    protected String doInBackground(String... uri) {

        //For Token Generation using POST method-08-11
//        if (uri[0].toLowerCase().equalsIgnoreCase("posttokenvisa")) {
//            callGenerateTokenVisaPostToken(uri);
//        }
        //Added for GETgettoken aug 3rd
        if (uri[0].toLowerCase().equalsIgnoreCase("gettokenvisa")) {
            callGenerateGETTokenVisaGetToken(uri);
        }
//        if (uri[0].toLowerCase().equalsIgnoreCase("getauthorisetoken")) {
//            callAuthorizePurchaseVisaGetGetToken(uri, TransactionTypePrimarySecondary.AUTHORISE);
//        }
//
//
//        if (uri[0].toLowerCase().equalsIgnoreCase("getpurchasetoken")) {
//            callAuthorizePurchaseVisaGetGetToken(uri, TransactionTypePrimarySecondary.PURCHASE);
//        }
//
//        if (uri[0].toLowerCase().equalsIgnoreCase("getauthcapturetoken")) {
//            callAuthorizePurchaseVisaGetGetToken(uri, TransactionTypePrimarySecondary.AUTHORISE);
//            callCaptureRefundVoidVisaGetGetToken(TransactionTypePrimarySecondary.CAPTURE);
//        }
//
//        if (uri[0].toLowerCase().equalsIgnoreCase("getauthvoidtoken")) {
//            callAuthorizePurchaseVisaGetGetToken(uri, TransactionTypePrimarySecondary.AUTHORISE);
//            callCaptureRefundVoidVisaGetGetToken(TransactionTypePrimarySecondary.VOID);
//        }
//        if (uri[0].toLowerCase().equalsIgnoreCase("getpurchaserefundtoken")) {
//            callAuthorizePurchaseVisaGetGetToken(uri, TransactionTypePrimarySecondary.PURCHASE);
//            callCaptureRefundVoidVisaGetGetToken(TransactionTypePrimarySecondary.REFUND);
//        }


        return statusString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("Button Authorize Clicked" + result);
    }

    /********************GET TOKEN *******************************************/


    public void initialize() {
        clientHelper.setAppId(TransactionDataProvider.appIdCert);
        clientHelper.setSecuredSecret(TransactionDataProvider.secureIdCert);
        clientHelper.setToken(TransactionDataProvider.tokenCert);
        clientHelper.setTrToken(TransactionDataProvider.trTokenInt);
        clientHelper.setUrl(TransactionDataProvider.urlCert);
        clientHelper.setTaToken(TransactionDataProvider.taToken);

    }

    //Method added for GET token August 3rd//Do not delete
    public void callGenerateGETTokenVisaGetToken(String[] uri) {
        try {
            initialize();
            clientHelper.setTokenurl(TransactionDataProvider.tokenUrl);
            clientHelper.setJsSecurityKey(TransactionDataProvider.jsSecurityKey);

            String url = clientHelper.getTokenurl() + "ta_token=" + clientHelper.getTaToken() + "&auth=" + uri[1] + "&apikey=" + clientHelper.getAppId() + "&js_security_key=" +
                    clientHelper.getJsSecurityKey() + "&callback=" + uri[2] + "&currency=" + uri[3] + "&type=" + uri[4] + "&credit_card.type=" + uri[5]
                    + "&credit_card.cardholder_name=" + uri[6] + "&credit_card.card_number=" + uri[7] + "&credit_card.exp_date=" + uri[8]
                    + "&credit_card.cvv=" + uri[9];


            TransactionResponse response = clientHelper.doPrimaryTransactionGET(url);

            System.out.println("Token gen=" + TransactionResponse.getToken().getTokenData().getValue());

            statusString = "gettoken called    token generated ==";
            statusString = statusString + TransactionResponse.getToken().getTokenData().getValue();
            statusString = statusString + ((UserTransactionResponse) response).getResponseString() + splitter;

            System.out.println("Response : " + response.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            statusString = statusString + "Exception :" + e.getMessage() + splitter;
        }
    }

}