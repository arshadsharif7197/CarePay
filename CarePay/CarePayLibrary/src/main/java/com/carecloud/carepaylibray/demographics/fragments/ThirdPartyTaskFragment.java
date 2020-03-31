package com.carecloud.carepaylibray.demographics.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.third_party.models.ThirdPartyTask;
import com.carecloud.carepaylibray.third_party.models.ThirdPartyWorkflowDto;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 23/04/18.
 */
public class ThirdPartyTaskFragment extends BaseCheckinFragment {

    private ThirdPartyWorkflowDto thirdPartyWorkflow;

    private String lastUrl;
    private String returnUrl;
    private CheckinFlowCallback callback;

    private SwipeRefreshLayout refreshLayout;
    private WebView webView;
    private View nextButton;

    private boolean returnUrlCalled = false;


    public static ThirdPartyTaskFragment newInstance(ThirdPartyWorkflowDto thirdPartyWorkflow) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, thirdPartyWorkflow);

        ThirdPartyTaskFragment fragment = new ThirdPartyTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (CheckinFlowCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
        }
    }

    @Override
    public DTO getDto() {
        return thirdPartyWorkflow;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setRetainInstance(true);
        Bundle args = getArguments();
        thirdPartyWorkflow = DtoHelper.getConvertedDTO(ThirdPartyWorkflowDto.class, args);
        callback.setCheckinFlow(CheckinFlowState.INTAKE, 0, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_third_party_task, container, false);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        if (thirdPartyWorkflow != null) {
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //reloading causes us to loose sso
                    webView.loadUrl(lastUrl);
                }
            });

            nextButton = view.findViewById(R.id.consentButtonNext);
            if(thirdPartyWorkflow.getPayload().getThirdPartyProcess().getTask().handlesNext()){
                nextButton.setVisibility(View.GONE);
            }
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(returnUrlCalled) {
                        continueWorkflow();
                    }else{
                        //TODO prompt before continuing maybe??
                        continueWorkflow();
                    }
                }
            });
            nextButton.setEnabled(false);


            webView = (WebView) view.findViewById(R.id.taskWebView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setAllowFileAccess(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            webView.setWebViewClient(new TaskViewClient());

            startWebView();
            inflateToolbarViews(view);
        }
    }

    private void startWebView() {
        ThirdPartyTask task = thirdPartyWorkflow.getPayload().getThirdPartyProcess().getTask();
        returnUrl = task.getReturnUrl();
        Uri startUrl = Uri.parse(task.getAccessUrl());
        startUrl = startUrl.buildUpon()
                .appendQueryParameter("return_url", returnUrl)
                .build();

        webView.clearCache(true);
        webView.clearHistory();
        Log.d("start url", startUrl.toString());
        webView.loadUrl(startUrl.toString());
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });


        TextView header = (TextView) view.findViewById(R.id.toolbar_title);
        header.setText(thirdPartyWorkflow.getPayload().getThirdPartyProcess().getTask().getHost());
    }

    public boolean handleBackButton() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean navigateBack() {
        return handleBackButton();
    }

    private void continueWorkflow() {
        ThirdPartyTask task = thirdPartyWorkflow.getPayload().getThirdPartyProcess().getTask();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", task.getPracticeMgmt());
        queryMap.put("practice_id", task.getPracticeId());
        queryMap.put("patient_id", task.getPatientId());
        queryMap.put("appointment_id", task.getAppointmentId());

        TransitionDTO transitionDTO = thirdPartyWorkflow.getMetadata().getThirdPartyPayload().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap);

    }

    private WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onUpdate(callback, workflowDTO);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    protected class TaskViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (url != null) {
                Uri uri = Uri.parse(url);
                String query = uri.getQuery();
                if (query == null) {
                    query = "";
                }
                String baseUrl = url.replace(query, "").replace("?", "");
                if (returnUrl.equals(baseUrl)) {
                    returnUrlCalled = true;
                    if(thirdPartyWorkflow.getPayload().getThirdPartyProcess().getTask().handlesNext()){
                        continueWorkflow();
                    }else {
                        nextButton.setEnabled(true);
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            Log.d("Loaded URL", url);
            lastUrl = url;
            refreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }

    }

}
