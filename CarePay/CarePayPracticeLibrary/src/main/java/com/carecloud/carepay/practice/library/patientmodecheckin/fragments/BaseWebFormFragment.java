package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.base.PracticeNavigationStateConstants;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcok.stepprogressbar.StepProgressBar;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public abstract class BaseWebFormFragment extends BaseCheckinFragment {

    private WebView webView;
    private ProgressBar progressBar;
    private Button nextButton;
    private TextView header;
    private StepProgressBar progressIndicator;


    private int totalForms;
    private int displayedFormsIndex;


    private CheckinFlowCallback flowCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            flowCallback = (CheckinFlowCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkin_consent_form_dynamic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);

        flowCallback.setCheckinFlow(getCheckinFlowState(), totalForms, displayedFormsIndex);

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.consentButtonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });
        nextButton.setEnabled(false);

        progressIndicator = (StepProgressBar) view.findViewById(R.id.progress_indicator);
        if(totalForms>1) {
            progressIndicator.setVisibility(View.VISIBLE);
            progressIndicator.setNumDots(totalForms);
        }else{
            progressIndicator.setVisibility(View.GONE);
        }

        webView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview_consent);
        progressBar = (ProgressBar) view.findViewById(com.carecloud.carepaylibrary.R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

    }


    protected void setHeader(String text){
        header.setText(text);
    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        header = (TextView) view.findViewById(R.id.consent_header);
    }


    /**
     * Init web view
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);

        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
//        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);


        //show progress bar
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    nextButton.setEnabled(false);
                }
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    nextButton.setEnabled(true);
                }
            }
        });

        //named interface that receive calls from javascript
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "FormInterface");
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "IntakeInterface");
        webView.loadUrl(getBaseUrl());
    }


    /**
     * call Interface to load next consent forms
     */
    protected abstract void displayNextForm();

    protected abstract String getBaseUrl();

    protected abstract void saveForm(JsonObject jsonResponse);

    protected abstract void submitAllForms();

    protected abstract CheckinFlowState getCheckinFlowState();

    protected abstract void validateForm();

    protected void validateForm(String function) {

        webView.loadUrl("javascript:window."+function+"()");


    }

    protected void loadFormUrl(String formString, String function){
        webView.loadUrl("javascript:window."+function+"('"+formString+"')");
        nextButton.setEnabled(true);
        progressIndicator.setCurrentProgressDot(displayedFormsIndex);

        flowCallback.setCheckinFlow(getCheckinFlowState(), totalForms, displayedFormsIndex+1);//adjust for zero index
    }

    public void setTotalForms(int totalForms) {
        this.totalForms = totalForms;
    }

    public int getTotalForms() {
        return totalForms;
    }

    public int getDisplayedFormsIndex() {
        return displayedFormsIndex;
    }

    public void setDisplayedFormsIndex(int displayedFormsIndex) {
        this.displayedFormsIndex = displayedFormsIndex;
    }

    /**
     * Received calls from javascript functions.
     */
    class WebViewJavaScriptInterface {

        private Context context;

        WebViewJavaScriptInterface(Context context) {
            this.context = context;
        }


        /**
         * start displaying content once interface is ready
         */
        @JavascriptInterface
        public void webviewReady(String message) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayNextForm();
                    nextButton.setEnabled(true);
                }
            });

        }


        /**
         * called from interface once consent form is ready to be saved
         */
        @JavascriptInterface
        public void savedForm(final String response) {
            Log.d(LOG_TAG, "FORMS SAVED -" + response);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

                    saveForm(jsonResponse);

                    getNextStep();

                }
            });

        }

        /**
         * called from interface once intake form is ready to be saved
         */
        @JavascriptInterface
        public void savedIntake(final String response) {
            Log.d(LOG_TAG, "FORMS SAVED -" + response);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

                    saveForm(jsonResponse);

                    getNextStep();

                }
            });

        }

        /**
         * called from interface to send a user a message
         */
        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong) {
            if(context!=null) {
                Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
            }
        }

    }

    private void getNextStep(){
        if (displayedFormsIndex < totalForms-1) {
            ++displayedFormsIndex;

            displayNextForm();

        } else {
            submitAllForms();
        }

    }


    @Override
    public boolean navigateBack(){
        if(totalForms>1 && displayedFormsIndex > 0){
            --displayedFormsIndex;
            displayNextForm();
            return true;
        }
        return false;
    }

    protected WorkflowServiceCallback updateformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(final WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if(workflowDTO.getState().equals(PracticeNavigationStateConstants.PATIENT_APPOINTMENTS)){
                flowCallback.displayCheckinSuccess(workflowDTO);
            }else {
                PracticeNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


}