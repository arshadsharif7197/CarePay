package com.carecloud.carepaylibray.demographics.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
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
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    continueWorkflow();
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
        startUrl.buildUpon().appendQueryParameter("return_url", returnUrl);

        webView.clearCache(true);
        webView.clearHistory();
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

        TransitionDTO transitionDTO = thirdPartyWorkflow.getMetadata().getPaymentsTransitions().getContinueTransition();
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
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
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
                    nextButton.setEnabled(true);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            lastUrl = url;
            refreshLayout.setRefreshing(false);
        }
    }

}
