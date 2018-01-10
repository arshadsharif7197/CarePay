package com.carecloud.carepay.patient.retail.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.retail.interfaces.RetailInterface;
import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;
import com.carecloud.carepay.patient.retail.models.sso.Person;
import com.carecloud.carepay.patient.retail.models.sso.Profile;
import com.carecloud.carepay.patient.retail.models.sso.SsoModel;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lmenendez on 2/8/17
 */

public class RetailFragment extends BaseFragment {
    private static final String KEY_SHOW_TOOLBAR = "show_toolbar";
    private static final String BASE_HTML = "<!DOCTYPE html><html><body>%s</body></html>";

    private static final String QUERY_ORDER = "transaction_id";
    private static final String QUERY_STORE = "store_id";
    private static final String QUERY_AMOUNT = "amount";

    private RetailModel retailModel;
    private RetailPracticeDTO retailPractice;
    private UserPracticeDTO userPracticeDTO;
    private boolean showToolbar = true;
    private RetailInterface callback;

    private WebView shoppingWebView;
    private String returnUrl = null;
    private String loadedUrl;

    public static RetailFragment newInstance(RetailModel retailModel, RetailPracticeDTO retailPractice, UserPracticeDTO userPracticeDTO, boolean showToolbar){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, retailModel);
        DtoHelper.bundleDto(args, retailPractice);
        DtoHelper.bundleDto(args, userPracticeDTO);
        args.putBoolean(KEY_SHOW_TOOLBAR, showToolbar);

        RetailFragment fragment = new RetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (RetailInterface) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement RetailInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        setRetainInstance(true);
        Bundle args = getArguments();
        retailModel = DtoHelper.getConvertedDTO(RetailModel.class, args);
        retailPractice = DtoHelper.getConvertedDTO(RetailPracticeDTO.class, args);
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        showToolbar = args.getBoolean(KEY_SHOW_TOOLBAR, true);

        initSsoPayload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }


    @Override
    public void onPause(){
        super.onPause();
        callback.getWebViewBundle().clear();
        shoppingWebView.saveState(callback.getWebViewBundle());
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);
        if(retailPractice != null) {

            shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
            WebView.setWebContentsDebuggingEnabled(true);
            WebSettings settings = shoppingWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            shoppingWebView.setWebViewClient(new RetailViewClient());
            shoppingWebView.addJavascriptInterface(new RetailJavascriptInterface(), "RetailInterface");


//            shoppingWebView.loadDataWithBaseURL("data:text/html", addCallback(retailPractice.getStore().getStoreHtml()), "text/html", "utf-8", "data:text/html");
//            shoppingWebView.loadData(getIframeHtml(retailPractice.getStore().getStoreHtml()), "text/html", "utf-8");


//            shoppingWebView.loadData(retailPractice.getStore().getStoreHtml(), "text/html", "utf-8");
//            shoppingWebView.loadDataWithBaseURL(HttpConstants.getFormsUrl(), retailPractice.getStore().getStoreHtml(), "text/html", "utf-8", HttpConstants.getFormsUrl());

            shoppingWebView.loadUrl("file://"+getHtmlFile(retailPractice.getStore().getStoreHtml()));

        }
        if(returnUrl != null){
            shoppingWebView.restoreState(callback.getWebViewBundle());
            shoppingWebView.loadUrl(returnUrl);
            return;
        }
        if(loadedUrl != null){
            shoppingWebView.restoreState(callback.getWebViewBundle());
            shoppingWebView.loadUrl(loadedUrl);
        }
    }

    /**
     * Load payment redirect into existing webview
     * @param returnUrl returnUrl
     */
    public void loadPaymentRedirectUrl(String returnUrl){
        this.returnUrl = returnUrl;
        shoppingWebView.loadUrl(returnUrl);
    }

    private void initToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if(showToolbar) {
            toolbar.setVisibility(View.VISIBLE);
            toolbar.setNavigationIcon(R.drawable.icn_nav_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(userPracticeDTO.getPracticeName());
        }else {
            toolbar.setVisibility(View.GONE);
        }
    }

    public boolean handleBackButton(){
        if(shoppingWebView.canGoBack()){
            shoppingWebView.goBack();
            return true;
        }
        return false;
    }

    private String getIframeHtml(String html){
//        html = html.replace("\"", "'");
        return "<style>body{margin:0;} iframe{width:100vw; height:100vh; margin:0; padding:0; border:none;}</style><iframe srcdoc='"+html+"' ></iframe>";
    }

    private String getHtmlFile(String html){
        String fullHtml = String.format(BASE_HTML, html);
        File htmlFile = new File(getContext().getExternalCacheDir(), "retail.html");
        FileWriter writer = null;
        try {
            if(htmlFile.exists()){
                htmlFile.delete();
            }

            htmlFile.createNewFile();
            writer = new FileWriter(htmlFile);
            writer.write(fullHtml);

        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            if(writer != null){
                try {
                    writer.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
        return htmlFile.getAbsolutePath();
    }


    private void startPaymentRequest(String storeId, String orderId, String amountString){
        try {
            double amount = Double.parseDouble(amountString);
            IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
            postModel.setAmount(amount);
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
            postModel.setOrderId(orderId);
            postModel.setStoreId(storeId);

            callback.getPaymentModel().getPaymentPayload().setPaymentPostModel(postModel);
            callback.onPayButtonClicked(amount, callback.getPaymentModel());

        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
            showErrorNotification("Payment Amount Error");
        }

    }



    private class RetailViewClient extends WebViewClient {
        private boolean launchPayments = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if(url != null) {
                Uri uri = Uri.parse(url);
                String query = uri.getQuery();
                if(query == null){
                    query = "";
                }
                String baseUrl = url.replace(query, "").replace("?", "");
                if (HttpConstants.getRetailPaymentsRedirectUrl().equals(baseUrl)) {
                    String orderId = uri.getQueryParameter(QUERY_ORDER);
                    String amountString = uri.getQueryParameter(QUERY_AMOUNT);
                    String storeId = uri.getQueryParameter(QUERY_STORE);
                    startPaymentRequest(storeId, orderId, amountString);
                    launchPayments = true;
                    return true;
                }
            }
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap favIcon){
            super.onPageStarted(webView, url, favIcon);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String url){
            return super.shouldInterceptRequest(webView, url);
        }

        @Override
        public void onPageFinished(WebView webView, String url){
            super.onPageFinished(webView, url);
            if(!launchPayments) {
                loadedUrl = url;
                Log.d("Retail WebView", loadedUrl);
            }else {
                launchPayments = false;
                webView.goBack();
            }
            if(url.equals(returnUrl)){
                Log.d("Retail WebView", returnUrl);
            }
        }
    }


    //region test javascript interface
    private String addCallback(String storeHtml){
        return storeHtml +
                "<script type=\"text/javascript\">Ecwid.OnPageLoad.add(" +
                "function(page){" +
                "RetailInterface.retailRedirect(window.location.href);" +
                "});" +
                "</script>";
    }

    private class RetailJavascriptInterface {

        @JavascriptInterface
        public void retailRedirect(final String url){
            if(loadedUrl!=null && loadedUrl.equals(url)){
                return;
            }
            shoppingWebView.post(new Runnable() {
                @Override
                public void run() {
                    shoppingWebView.loadDataWithBaseURL("data:text/html", addCallback(url), "text/html", "utf-8", "data:text/html");
                }
            });
        }
    }
    //endregion

    //region test sso generation
    private static final String APP_CLIENT_SECRET = "9xasKgFMZbDErsGgQeCN3EHawKeydaNW";
    private static final String APP_ID = "breeze-shopping";
    private String ssoProfile = "";
    private String storeId = "12522068";//hardcoded MyBreezeClinic StoreId


    private void initSsoPayload(){
        DemographicPayloadDTO demographics = retailModel.getPayload().getDemographicDTO().getPayload();
        Person person = new Person();
        person.setName(demographics.getPersonalDetails().getFullName());
        person.setStreet(demographics.getAddress().getAddress1());
        person.setCity(demographics.getAddress().getCity());
        person.setState(demographics.getAddress().getState());
        person.setPostalCode(demographics.getAddress().getZipcode());
        person.setPhone(demographics.getAddress().getPhone());

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
        return
                "<html>" +
                        "<div> " +
                        "<script type=\"text/javascript\" src=\"https://app.ecwid.com/script.js?"+storeId+"\" charset=\"utf-8\"></script>" +
                        "<script type=\"text/javascript\"> xProductBrowser(\"categoriesPerRow=1\",\"views=grid(60,1) list(60)\",\"categoryView=grid\",\"searchView=list\");</script>" +
                        "<script> var ecwid_sso_profile = '" + ssoProfile + "' </script>" +
                        "</div>" +
                        "</html>";
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
    //endregion

}
