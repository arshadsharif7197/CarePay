package com.carecloud.carepaylibray.payeeze;

import android.util.Log;

import com.carecloud.carepay.service.library.interceptors.JSONFormattedLoggingInterceptor;
import com.carecloud.carepaylibrary.BuildConfig;
import com.carecloud.carepaylibray.payeeze.model.CreditCard;
import com.carecloud.carepaylibray.payeeze.model.TokenizeBody;
import com.carecloud.carepaylibray.payeeze.model.TokenizeResponse;
import com.carecloud.carepaylibray.payments.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author pjohnson on 2019-06-21.
 */
public class CallPayeezy {

    private static final String NONCE = "nonce";
    private static final String APIKEY = "apikey";
    private static final String APISECRET = "pzsecret";
    private static final String TOKEN = "token";
    private static final String TIMESTAMP = "timestamp";
    private static final String AUTHORIZE = "Authorization";
    private static final String PAYLOAD = "payload";

    public interface AuthorizeCreditCardCallback {
        void onAuthorizeCreditCard(TokenizeResponse response);
    }

    public void doCall(CreditCard creditCard, MerchantServiceMetadataDTO merchantServiceDTO,
                       final AuthorizeCreditCardCallback callback) {

        TokenizeBody tokenizeBody = new TokenizeBody();
        tokenizeBody.setAuth(merchantServiceDTO.getTokenizationAuth());
        tokenizeBody.setType(merchantServiceDTO.getTokenType());
        tokenizeBody.setTaToken(merchantServiceDTO.getMasterTaToken());
        tokenizeBody.setCreditCard(creditCard);

        Gson gson = new Gson();
        Map<String, String> headers = getSecurityKeys(merchantServiceDTO.getApiKey(),
                merchantServiceDTO.getMasterMerchantToken(),
                merchantServiceDTO.getApiSecret(), gson.toJson(tokenizeBody));
        String tokenizationPostPath = merchantServiceDTO.getUrlPostPath();
        if (StringUtil.isNullOrEmpty(tokenizationPostPath)) {
            tokenizationPostPath = "/v1/transactions/tokens";
        }
        String tokenizationUrl = String.format("%s%s", merchantServiceDTO.getBaseUrl(), tokenizationPostPath);
        Retrofit retrofit = getRetrofitService(merchantServiceDTO.getBaseUrl());
        Call<TokenizeResponse> call = retrofit.create(PayeezyService.class)
                .tokenizeCard(tokenizationUrl, tokenizeBody, headers);
        call.enqueue(new Callback<TokenizeResponse>() {
            @Override
            public void onResponse(Call<TokenizeResponse> call, Response<TokenizeResponse> response) {
                callback.onAuthorizeCreditCard(response.body());
            }

            @Override
            public void onFailure(Call<TokenizeResponse> call, Throwable t) {
                Log.e("Breeze", t.getMessage());
            }
        });

    }

    private Retrofit getRetrofitService(String baseUrl) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new JSONFormattedLoggingInterceptor());
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

    private Map<String, String> getSecurityKeys(String appId, String token, String secureId, String payLoad) {

        Map<String, String> returnMap = new HashMap<>();
        long nonce;
        try {

            nonce = Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong());

            returnMap.put(NONCE, Long.toString(nonce));
            returnMap.put(APIKEY, appId);
            returnMap.put(TIMESTAMP, Long.toString(System.currentTimeMillis()));
            returnMap.put(TOKEN, token);
            returnMap.put(APISECRET, secureId);
            returnMap.put(PAYLOAD, payLoad);
            returnMap.put(AUTHORIZE, getMacValue(returnMap));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    private String getMacValue(Map<String, String> data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        String apiSecret = data.get(APISECRET);
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
        byte[] macHash = mac.doFinal(bufferData.getBytes("UTF-8"));

        return android.util.Base64.encodeToString(toHex(macHash), android.util.Base64.NO_WRAP);
    }

    private byte[] toHex(byte[] arr) {
        String hex = byteArrayToHex(arr);
        return hex.getBytes();
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
