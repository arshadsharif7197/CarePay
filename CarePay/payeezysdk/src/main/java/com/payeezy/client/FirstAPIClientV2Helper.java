package com.payeezy.client;


import android.annotation.SuppressLint;

import com.payeezy.client.domain.v2.Token;
import com.payeezy.client.domain.v2.TransactionResponse;
import com.payeezy.client.domain.v2.Transarmor;
import com.payeezy.client.domain.v2.UserTransactionResponse;

import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@SuppressLint("DefaultLocale")
public class FirstAPIClientV2Helper {

    private RestTemplate restTemplate;

    private String url;
    private String appId;
    private String securedSecret;
    private String token;
    private String trToken;
    private String merchantid;
    private String taToken;

    private String tokenurl;
    private String jsSecurityKey;

    @SuppressWarnings({"rawtypes", "deprecation"})
    public FirstAPIClientV2Helper() {
        // Create a new RestTemplate instance
        restTemplate = new RestTemplate();

        // Add the Jackson and String message converters
        StringHttpMessageConverter sconverter = new StringHttpMessageConverter();
        restTemplate.getMessageConverters().add(sconverter);
        restTemplate.getMessageConverters().add(new org.springframework.http.converter.xml.SourceHttpMessageConverter());
        restTemplate.getMessageConverters().add(new org.springframework.http.converter.FormHttpMessageConverter());
        org.springframework.http.converter.json.MappingJacksonHttpMessageConverter converter = new org.springframework.http.converter.json.MappingJacksonHttpMessageConverter();
        converter.getObjectMapper().configure(Feature.WRITE_NULL_MAP_VALUES, false);
        converter.getObjectMapper().configure(Feature.WRITE_NULL_PROPERTIES, false);
        converter.getObjectMapper().configure(Feature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);

        restTemplate.getMessageConverters().add(converter);

        restTemplate.setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory());
/*    	this.setUrl("https://api-cert.payeezy.com/v1");
    	this.setAppId("y6pWAJNyJyjGv66IsVuWnklkKUPFbb0a");
    	this.setSecuredSecret("86fbae7030253af3cd15faef2a1f4b67353e41fb6799f576b5093ae52901e6f7");
    	this.setToken("fdoa-a480ce8951daa73262734cf102641994c1e55e7cdf4c02b6");
    	this.setMerchantid("OGEzNGU3NjM0ODQyMTU3NzAxNDg0MjE4NDY4ZTAwMDA");*/
    }

    public String getMerchantid() {
        return merchantid;
    }

    public void setMerchantid(String merchantid) {
        this.merchantid = merchantid;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getAppId() {
        return appId;
    }


    public void setAppId(String appId) {
        this.appId = appId;
    }


    public String getSecuredSecret() {
        return securedSecret;
    }


    public void setSecuredSecret(String secureId) {
        this.securedSecret = secureId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTrToken() {
        return trToken;
    }

    public void setTrToken(String trToken) {
        this.trToken = trToken;
    }

    public String getTokenurl() {
        return tokenurl;
    }

    public void setTokenurl(String tokenurl) {
        this.tokenurl = tokenurl;
    }

    public String getJsSecurityKey() {
        return jsSecurityKey;
    }

    public void setJsSecurityKey(String jsSecurityKey) {
        this.jsSecurityKey = jsSecurityKey;
    }


    private static final String NONCE = "nonce";

    private static final String APIKEY = "apikey";
    private static final String APISECRET = "pzsecret";
    private static final String TOKEN = "token";
    private static final String TIMESTAMP = "timestamp";
    private static final String AUTHORIZE = "Authorization";
    private static final String PAYLOAD = "payload";

    private Map<String, String> getSecurityKeys(String payLoad) throws Exception {

        Map<String, String> returnMap = new HashMap<String, String>();
        long nonce;
        try {

            nonce = Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong());
            MessageLogger.logMessage(String.format("SecureRandom nonce:{}", nonce));

            returnMap.put(NONCE, Long.toString(nonce));
            returnMap.put(APIKEY, this.appId);
            returnMap.put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
            returnMap.put(TOKEN, this.token);
            returnMap.put(APISECRET, this.securedSecret);
            returnMap.put(PAYLOAD, payLoad);
            returnMap.put(AUTHORIZE, getMacValue(returnMap));
            return returnMap;

        } catch (NoSuchAlgorithmException e) {
            //log.error(e.getMessage(),e);
            MessageLogger.logMessage(e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getMacValue(Map<String, String> data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        String apiSecret = data.get(APISECRET);
        MessageLogger.logMessage(String.format("API_SECRET:{}", apiSecret));
        SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
        mac.init(secret_key);
        StringBuilder buff = new StringBuilder();
        buff.append(data.get(APIKEY))
                .append(data.get(NONCE))
                .append(data.get(TIMESTAMP));
        if (data.get(TOKEN) != null)
            buff.append(data.get(TOKEN));
        if (data.get(PAYLOAD) != null)
            buff.append(data.get(PAYLOAD));
        String bufferData = buff.toString();
        MessageLogger.logMessage(String.format(bufferData));
        byte[] macHash = mac.doFinal(bufferData.getBytes("UTF-8"));
        MessageLogger.logMessage(Integer.toString(macHash.length));
        MessageLogger.logMessage(String.format("MacHAsh:{}", Arrays.toString(macHash)));

        String authorizeString = android.util.Base64.encodeToString(toHex(macHash), android.util.Base64.NO_WRAP);

        MessageLogger.logMessage(String.format("Authorize: {}", authorizeString));
        return authorizeString;
    }

    public byte[] toHex(byte[] arr) {
        MessageLogger.logMessage(Integer.toString(arr.length));
        String hex = byteArrayToHex(arr);
        MessageLogger.logMessage(String.format("Apache common value:{}", hex));
        return hex.getBytes();
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    //new method for GET- 4th aug
    public TransactionResponse doPrimaryTransactionGET(String url) throws Exception {

        System.out.println("url=" + url);
        String response = restTemplate.getForObject(url, String.class);
        System.out.println("response=" + response);
        TransactionResponse r = GetTokenTransactionResponse(response.toString());
        return r;


    }

    //used for GET method
    private UserTransactionResponse GetTokenTransactionResponse(String obj) {
        UserTransactionResponse response = new UserTransactionResponse();
        response.setResponseString(obj);
        Token token = new Token();
        Transarmor tokenData = new Transarmor();
        token.setTokenData(tokenData);
        response.setToken(token);
        int beginIndex = 0;
        int endIndex = 0;

        String objstr = obj;
        boolean tokenResponse = false;
        objstr = objstr.trim();
        if (objstr.startsWith("Payeezy.callback")) {
            objstr = objstr.substring(19, objstr.length()); //("Payeezy.callback", "");
            objstr = objstr.trim();
            tokenResponse = true;
        }

        String[] responseData = objstr.split(",");

        for (int i = 0; i < responseData.length; i++) {
            String str = responseData[i];

            String[] dataVals = str.split("=");

            if (tokenResponse) {
                str = str.trim();
                dataVals = str.split(":");
            }
            if (str.contains(":")) {
                str = str.trim();
                dataVals = str.split(":");
            }
            if (dataVals.length >= 2) {
                dataVals[1] = dataVals[1].replace("{", "");
                dataVals[1] = dataVals[1].replace("}", "");
                dataVals[1] = dataVals[1].replace(":", "");
                dataVals[1] = dataVals[1].replace("\"", "");
                dataVals[1] = dataVals[1].replace("[", "");
            }
            if (dataVals.length >= 3) {
                dataVals[2] = dataVals[2].replace("{", "");
                dataVals[2] = dataVals[2].replace("}", "");
                dataVals[2] = dataVals[2].replace(":", "");
                dataVals[2] = dataVals[2].replace("\"", "");
                dataVals[2] = dataVals[2].replace("[", "");
            }

            if (dataVals[0].contains("results")) {
                String correlationID = dataVals[2];
                response.setCorrelationID(correlationID);
            }
            if (dataVals[0].contains("correlation_id")) {
                String correlationID = dataVals[1];
                response.setCorrelationID(correlationID);
            }
            if (dataVals[0].contains("status")) {
                if (tokenResponse) {
                    String status = dataVals[1];
                    try {
                        int stat = Integer.parseInt(status);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    if (status.length() < 6)  //if(stat>0)
                    {
                        response.setStatus(status);
                    }
                } else {
                    String status = dataVals[1];
                    response.setStatus(status);
                }
            }
            if (dataVals[0].contains("type")) {
                String type = dataVals[1];
                TransactionResponse.getToken().setToken_type(type);
            }
            if (dataVals[0].contains("token")) {
                String cardtype = dataVals[1];
                if (dataVals.length > 2) {
                    cardtype = dataVals[2];
                }
                TransactionResponse.getToken().getTokenData().setType(cardtype);
            }
            if (dataVals[0].contains("cardholder_name")) {
                String name = dataVals[1];
                TransactionResponse.getToken().getTokenData().setName(name);
            }

            if (dataVals[0].contains("exp_date")) {
                String expDate = dataVals[1];
                TransactionResponse.getToken().getTokenData().setExpiryDt(expDate);
            }
            if (dataVals[0].contains("value")) {
                //String value="2833693264441732";
                String value = dataVals[1];


                int indexOfOpenBracket = 0;
                int indexOfLastBracket = value.lastIndexOf(" ");

                System.out.println(value.substring(indexOfOpenBracket, indexOfLastBracket));
                value = value.substring(indexOfOpenBracket, indexOfLastBracket);
                System.out.println("tokenvalue after substr=" + value);
                value = value.trim();
                System.out.println("tokenvalue after trim=" + value);


                System.out.println("tokenvalue after trim=" + value);
                TransactionResponse.getToken().getTokenData().setValue(value);
            }

            if (dataVals[0].contains("transaction_id")) {
                String transactionId = dataVals[1];
                response.setTransactionId(transactionId);
            }
            if (dataVals[0].contains("transaction_tag")) {
                String transactionTag = dataVals[1];
                response.setTransactionTag(transactionTag);
            }
            if (dataVals[0].contains("amount")) {
                String amount = dataVals[1];
                response.setAmount(amount);
            }
            if (dataVals[0].contains("transaction_status")) {
                String transactionStatus = dataVals[1];
                response.setTransactionStatus(transactionStatus);
            }
            if (dataVals[0].contains("validation_status")) {
                String validation_status = dataVals[1];
                response.setValidationStatus(validation_status);
            }
            if (dataVals[0].contains("transaction_type")) {
                String transaction_type = dataVals[1];
                response.setTransactionType(transaction_type);
            }
            if (dataVals[0].contains("method")) {
                String method = dataVals[1];
                response.setMethod(method);
            }

        }
        return response;
    }

    public String getTaToken() {
        return taToken;
    }

    public void setTaToken(String taToken) {
        this.taToken = taToken;
    }
}
	