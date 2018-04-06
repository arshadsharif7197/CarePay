package com.carecloud.carepay.patient.retail.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.retail.interfaces.RetailInterface;
import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 2/8/17
 */

public class RetailFragment extends BaseFragment {
    private static final String KEY_SHOW_TOOLBAR = "show_toolbar";
    private static final String BASE_URL = "/shop-sso.html";

    private static final String QUERY_ORDER = "transaction_id";
    private static final String QUERY_STORE = "store_id";
    private static final String QUERY_AMOUNT = "amount";

    private RetailModel retailModel;
    private RetailPracticeDTO retailPractice;
    private UserPracticeDTO userPracticeDTO;
    private boolean showToolbar = true;
    private RetailInterface callback;

    private WebView shoppingWebView;
    private SwipeRefreshLayout refreshLayout;
    private Toolbar toolbar;
    private String returnUrl = null;
    private String lastUrl;

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
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //reloading causes us to loose sso
                    reloadSSO(lastUrl, false);
                }
            });
            WebView.setWebContentsDebuggingEnabled(true);
            WebSettings settings = shoppingWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            shoppingWebView.setWebViewClient(new RetailViewClient());
            shoppingWebView.addJavascriptInterface(new WebStoreInterface(), "StoreInterface");
            shoppingWebView.restoreState(callback.getWebViewBundle());
            callback.getWebViewBundle().clear();

            if(returnUrl != null){
                reloadSSO(returnUrl, false);
            }else if (lastUrl != null){
                reloadSSO(lastUrl, true);
            }else {
                loadRetailHome();
            }
        }
    }

    private void loadRetailHome(){
        Uri storeUrl = Uri.parse(HttpConstants.getFormsUrl()+BASE_URL);
        shoppingWebView.loadUrl(storeUrl.toString());
    }

    /**
     * Load payment redirect into existing webview
     * @param returnUrl returnUrl
     */
    public void loadPaymentRedirectUrl(String returnUrl){
        this.returnUrl = returnUrl;
    }

    private void initToolbar(View view){
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(userPracticeDTO.getPracticeName());
        if(showToolbar) {
            callback.displayToolbar(false);
            toolbar.setVisibility(View.VISIBLE);
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

    private void reloadSSO(String followUpUrl, boolean loadHome){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("store_only", "true");

        TransitionDTO transitionDTO = retailModel.getMetadata().getLinks().getRetail();
        getWorkflowServiceHelper().execute(transitionDTO, getRefreshSsoCallback(followUpUrl, loadHome), queryMap);

    }

    private WorkflowServiceCallback getRefreshSsoCallback(final String followUpUrl, final boolean loadHome) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                refreshLayout.setRefreshing(false);
                RetailModel retailModel = DtoHelper.getConvertedDTO(RetailModel.class, workflowDTO);
                retailPractice = retailModel.getPayload().getRetailPracticeList().get(0);
                shoppingWebView.loadUrl(followUpUrl);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                refreshLayout.setRefreshing(false);
                //forget it... just load
                shoppingWebView.loadUrl(followUpUrl);
            }
        };
    }

    private class WebStoreInterface{

        @JavascriptInterface
        public String getSSO(){
            return retailPractice.getStore().getSso().getSSO();
        }

        @JavascriptInterface
        public String getStore(){
            return retailPractice.getStore().getStoreId();
        }
    }


    private class RetailViewClient extends WebViewClient {
        private boolean launchPayments = false; //don't maintain the payments launch url
        private boolean loadedReturnUrl = false;//handle event where the payments return url has been loaded
        private String launchUrl = null;

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
        public void onPageFinished(WebView webView, String url){
            if(lastUrl == null || launchUrl == null){
                launchUrl = url;
            }else if(lastUrl.equals(url) && launchUrl.equals(url)){
                webView.clearHistory();
            }

            if(lastUrl != null){
                toolbar.setVisibility(View.VISIBLE);
                callback.displayToolbar(false);
            }
            if(!launchPayments) {
                lastUrl = url;
                Log.d("Retail WebView", lastUrl);
            }else {
                launchPayments = false;
            }
            if(loadedReturnUrl){
                webView.clearHistory();
            }
            if(url.equals(returnUrl)){
                Log.d("Retail WebView", returnUrl);
                loadedReturnUrl = true;
            }
            super.onPageFinished(webView, url);
            if(!showToolbar && !shoppingWebView.canGoBack()) {//check if this is the last in the stack
                toolbar.setVisibility(View.GONE);
                callback.displayToolbar(true);
            }

        }
    }

}
