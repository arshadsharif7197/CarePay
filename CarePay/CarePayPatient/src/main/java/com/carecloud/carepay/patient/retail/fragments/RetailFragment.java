package com.carecloud.carepay.patient.retail.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;
import com.carecloud.carepay.patient.retail.models.sso.Person;
import com.carecloud.carepay.patient.retail.models.sso.Profile;
import com.carecloud.carepay.patient.retail.models.sso.SsoModel;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lmenendez on 2/8/17
 */

public class RetailFragment extends BaseFragment {
    private static final String APP_CLIENT_SECRET = "9xasKgFMZbDErsGgQeCN3EHawKeydaNW";
    private static final String APP_ID = "breeze-shopping";

    private RetailModel retailModel;
    private RetailPracticeDTO retailPractice;

    private String ssoProfile = "";
    private String storeId = "12522068";//hardcoded MyBreezeClinic StoreId

    private WebView shoppingWebView;


    public static RetailFragment newInstance(RetailModel retailModel, RetailPracticeDTO retailPractice){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, retailModel);
        DtoHelper.bundleDto(args, retailPractice);

        RetailFragment fragment = new RetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        retailModel = DtoHelper.getConvertedDTO(RetailModel.class, args);
        retailPractice = DtoHelper.getConvertedDTO(RetailPracticeDTO.class, args);

        initSsoPayload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
        WebSettings settings = shoppingWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        if(retailPractice != null) {
            shoppingWebView.loadData(retailPractice.getStore().getStoreHtml(), "text/html", "utf-8");
        }
    }

    public boolean handleBackButton(){
        if(shoppingWebView.canGoBack()){
            shoppingWebView.goBack();
            return true;
        }
        return false;
    }

    private void initSsoPayload(){
        DemographicPayloadDTO demographics = retailModel.getPayload().getDemographicDTO().getPayload();
        Person person = new Person();
        person.setName(demographics.getPersonalDetails().getFullName());
        person.setStreet(demographics.getAddress().getAddress1());
        person.setCity(demographics.getAddress().getCity());
        person.setState(demographics.getAddress().getState());
        person.setPostalCode(demographics.getAddress().getZipcode());
        person.setPhone(demographics.getPersonalDetails().getPrimaryPhoneNumber());

        Profile profile = new Profile();
        profile.setEmail(getApplicationPreferences().getUserId());
        profile.setBillingPerson(person);

        SsoModel ssoModel = new SsoModel();
        ssoModel.setAppId(APP_ID);
        ssoModel.setUserId(getApplicationPreferences().getUserId());
        ssoModel.setProfile(profile);

        String ssoString = new Gson().toJson(ssoModel);
        String ssoEncoded = Base64.encodeToString(ssoString.getBytes(), Base64.NO_WRAP);

        long timeStamp = System.currentTimeMillis() / 1000;

        try {
            String signature = hmacSha1(ssoEncoded + " " + timeStamp, APP_CLIENT_SECRET);

            ssoProfile = ssoEncoded + " " +
                        signature + " " +
                        timeStamp;

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getHtmlData(){
        return  "<div> " +
                "<script type=\"text/javascript\" src=\"https://app.ecwid.com/script.js?"+storeId+"\" charset=\"utf-8\"></script>" +
                "<script type=\"text/javascript\"> xProductBrowser(\"categoriesPerRow=1\",\"views=grid(60,1) list(60)\",\"categoryView=grid\",\"searchView=list\");</script>" +
                "<script> var ecwid_sso_profile = '" + ssoProfile + "' </script>" +
                "</div>";
    }

    private static String hmacSha1(String value, String key)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] bytes = mac.doFinal(value.getBytes());
        return bytesToHex(bytes);
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
