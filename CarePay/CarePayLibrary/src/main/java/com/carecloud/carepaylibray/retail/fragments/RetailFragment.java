package com.carecloud.carepaylibray.retail.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
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

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.retail.RetailModel;
import com.carecloud.carepaylibray.retail.RetailPracticeDTO;
import com.carecloud.carepaylibray.retail.interfaces.RetailInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 23/04/18.
 */
public class RetailFragment extends BaseFragment {

    protected static final String KEY_SHOW_TOOLBAR = "show_toolbar";
    protected static final String BASE_URL = "/shop-sso.html";
    protected static final String QUERY_ORDER = "transaction_id";
    protected static final String QUERY_STORE = "store_id";
    protected static final String QUERY_AMOUNT = "amount";

    protected RetailModel retailModel;
    protected RetailPracticeDTO retailPractice;
    protected UserPracticeDTO userPracticeDTO;
    protected WebView shoppingWebView;
    protected String lastUrl;
    protected String returnUrl = null;
    protected RetailInterface callback;
    protected SwipeRefreshLayout refreshLayout;
    protected Toolbar toolbar;
    protected boolean showToolbar = true;

    private boolean launchedPayments = false;

    public static RetailFragment newInstance(RetailModel retailModel, RetailPracticeDTO retailPractice,
                                             UserPracticeDTO userPracticeDTO,
                                             boolean showToolbar) {
        RetailFragment fragment = RetailFragment.newInstance(retailPractice, userPracticeDTO, showToolbar);
        DtoHelper.bundleDto(fragment.getArguments(), retailModel);
        return fragment;
    }

    public static RetailFragment newInstance(RetailPracticeDTO retailPractice,
                                             UserPracticeDTO userPracticeDTO,
                                             boolean showToolbar) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, retailPractice);
        DtoHelper.bundleDto(args, userPracticeDTO);
        args.putBoolean(KEY_SHOW_TOOLBAR, showToolbar);

        RetailFragment fragment = new RetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (RetailInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement RetailInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setRetainInstance(true);
        Bundle args = getArguments();
        retailModel = DtoHelper.getConvertedDTO(RetailModel.class, args);
        if (retailModel == null) {
            retailModel = (RetailModel) callback.getDto();
        }
        retailPractice = DtoHelper.getConvertedDTO(RetailPracticeDTO.class, args);
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        showToolbar = args.getBoolean(KEY_SHOW_TOOLBAR, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_retail, container, false);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        if (retailPractice != null) {
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            shoppingWebView = (WebView) view.findViewById(R.id.shoppingWebView);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //reloading causes us to loose sso
                    reloadSSO(lastUrl, false);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            WebSettings settings = shoppingWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            shoppingWebView.setWebViewClient(new RetailViewClient());
            shoppingWebView.addJavascriptInterface(new WebStoreInterface(), "StoreInterface");
            shoppingWebView.restoreState(callback.getWebViewBundle());
            callback.getWebViewBundle().clear();

            if (returnUrl != null) {
                reloadSSO(returnUrl, false);
            } else if (lastUrl != null && !launchedPayments) {
                reloadSSO(lastUrl, false);
            } else if (!launchedPayments) {
                loadRetailHome();
            }
            initToolbar(view);
            launchedPayments = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        callback.getWebViewBundle().clear();
        shoppingWebView.saveState(callback.getWebViewBundle());
    }

    private void reloadSSO(String followUpUrl, boolean skipUrlLoading) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("store_only", "true");

        TransitionDTO transitionDTO = retailModel.getMetadata().getLinks().getRetail();
        getWorkflowServiceHelper().execute(transitionDTO, getRefreshSsoCallback(followUpUrl, skipUrlLoading), queryMap);
    }

    private void loadRetailHome() {
        Uri storeUrl = Uri.parse(HttpConstants.getFormsUrl() + BASE_URL);
        shoppingWebView.clearCache(true);
        shoppingWebView.clearHistory();
        shoppingWebView.loadUrl(storeUrl.toString());
    }

    private void initToolbar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView toolbarTitle = ((TextView) view.findViewById(R.id.toolbar_title));
        if (toolbarTitle != null) {
            toolbarTitle.setText(userPracticeDTO.getPracticeName());
        } else {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(userPracticeDTO.getPracticeName());
        }
        if (showToolbar) {
            callback.displayToolbar(false);
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }

    protected class RetailViewClient extends WebViewClient {
        private boolean launchPayments = false; //don't maintain the payments launch url
        private boolean loadedReturnUrl = false;//handle event where the payments return url has been loaded
        private String launchUrl = null;


        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (url != null) {
                Uri uri = Uri.parse(url);
                String query = uri.getQuery();
                if (query == null) {
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
            return false;
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            if (lastUrl == null || launchUrl == null) {
                launchUrl = url;
            } else if (lastUrl.equals(url) && launchUrl.equals(url)) {
                webView.clearHistory();
            }
            if (lastUrl != null) {
                toolbar.setVisibility(View.VISIBLE);
                callback.displayToolbar(false);
            }
            if (!launchPayments) {
                lastUrl = url;
                Log.d("Retail WebView", lastUrl);
            } else {
                launchPayments = false;
                launchedPayments = true;//track if payments was launched for the webview reload
            }
            if (loadedReturnUrl) {
                webView.clearHistory();
                if(url.contains("#!/~/orderConfirmation")){//shopping session complete here
                    loadedReturnUrl = false;//to allow continue shopping after a payment was made
                    launchUrl = null;//reset launch url at this point
                }
            }
            if (url.equals(returnUrl)) {
                Log.d("Retail WebView", returnUrl);
                loadedReturnUrl = true;
            }
            if (!showToolbar && !shoppingWebView.canGoBack()) {//check if this is the last in the stack
                toolbar.setVisibility(View.GONE);
                callback.displayToolbar(true);
            }
            super.onPageFinished(webView, url);
        }
    }

    private void startPaymentRequest(String storeId, String orderId, String amountString) {
        try {
            double amount = Double.parseDouble(amountString);
            IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
            postModel.setAmount(amount);
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
            postModel.setOrderId(orderId);
            postModel.setStoreId(storeId);

            //this will give us a fresh sso in case they click back on payments
            reloadSSO(null, true);

            callback.getPaymentModel().getPaymentPayload().setPaymentPostModel(postModel);
            callback.onPayButtonClicked(amount, callback.getPaymentModel());

        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            showErrorNotification("Payment Amount Error");
        }

    }

    /**
     * Load payment redirect into existing webview
     *
     * @param returnUrl returnUrl
     */
    public void loadPaymentRedirectUrl(String returnUrl, boolean load) {
        this.returnUrl = returnUrl;
        if (load) {
            shoppingWebView.loadUrl(returnUrl);
        }
    }

    public boolean handleBackButton() {
        if (shoppingWebView.canGoBack()) {
            shoppingWebView.goBack();
            return true;
        }
        return false;
    }

    private WorkflowServiceCallback getRefreshSsoCallback(final String followUpUrl, final boolean skipUrlLoading) {
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
                if(!skipUrlLoading){
                    shoppingWebView.loadUrl(followUpUrl);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                refreshLayout.setRefreshing(false);
                //forget it... just load (xD)
                shoppingWebView.loadUrl(followUpUrl);
            }
        };
    }

    private class WebStoreInterface {

        @JavascriptInterface
        public String getSSO() {
            return retailPractice.getStore().getSso().getSSO();
        }

        @JavascriptInterface
        public String getStore() {
            return retailPractice.getStore().getStoreId();
        }
    }
}
