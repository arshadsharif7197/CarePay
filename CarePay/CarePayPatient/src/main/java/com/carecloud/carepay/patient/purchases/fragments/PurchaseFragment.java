package com.carecloud.carepay.patient.purchases.fragments;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.purchases.models.sso.Person;
import com.carecloud.carepay.patient.purchases.models.sso.Profile;
import com.carecloud.carepay.patient.purchases.models.sso.SsoModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
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

public class PurchaseFragment extends BaseFragment {

    private AppointmentsResultModel appointmentsResultModel;
    private String ssoProfile = "";

    private View noPurchaseLayout;// this should be available here to access it for show/hide from other methods
    private WebView shoppingWebView;


    public static PurchaseFragment newInstance(AppointmentsResultModel appointmentsResultModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentsResultModel);

        PurchaseFragment fragment = new PurchaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, args);

        initSsoPayload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        noPurchaseLayout = view.findViewById(R.id.no_purchase_layout);
        noPurchaseLayout.setVisibility(View.GONE);

        shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
        WebSettings settings = shoppingWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        shoppingWebView.loadData(getHtmlData(), "text/html", "utf-8");
    }

    public boolean handleBackButton(){
        if(shoppingWebView.canGoBack()){
            shoppingWebView.goBack();
            return true;
        }
        return false;
    }

    private void initSsoPayload(){
        DemographicPayloadDTO demographics = appointmentsResultModel.getPayload().getDemographicDTO().getPayload();
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
        ssoModel.setAppId("breeze-shopping");
        ssoModel.setUserId(getApplicationPreferences().getUserId());
        ssoModel.setProfile(profile);

        String ssoString = new Gson().toJson(ssoModel);
        String ssoEncoded = Base64.encodeToString(ssoString.getBytes(), Base64.NO_WRAP);

        long timeStamp = System.currentTimeMillis() / 1000;

        try {
            String signature = hmacSha1(ssoEncoded + " " + timeStamp, "9xasKgFMZbDErsGgQeCN3EHawKeydaNW");

            ssoProfile = ssoEncoded + " " +
                    signature + " " +
                    timeStamp;

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private String getHtmlData(){
        return "<div id=\"My Breeze Clinic\"></div>" +
                "<div> " +
                "<script type=\"text/javascript\" src=\"https://app.ecwid.com/script.js?12522068\" charset=\"utf-8\"></script>" +
                "<script type=\"text/javascript\"> xProductBrowser(\"categoriesPerRow=1\",\"views=grid(60,1) list(60)\",\"categoryView=grid\",\"searchView=list\",\"id=My Breeze Clinic\");</script>" +
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
