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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowState;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public class CheckinConsentForm1Fragment extends BaseCheckinFragment {

    private WebView webView;
    private ProgressBar progressBar;
    private Button nextButton;
    private RatingBar progressIndicator;

//    private int formIndex;
    private int totalForms;
    private int displayedFormsIndex;

    private ConsentFormDTO consentFormDTO;
    private List<String> jsonFormSaveResponseArray = new ArrayList<>();
    private List<PracticeForm> consentFormList;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String jsonString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        consentFormDTO = gson.fromJson(jsonString, ConsentFormDTO.class);
        consentFormList = consentFormDTO.getMetadata().getDataModels().getPracticeForms();
        totalForms = consentFormList.size();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkin_consent_form_dynamic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);

        flowCallback.setCheckinFlow(CheckinFlowState.CONSENT, totalForms, displayedFormsIndex);

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.consentButtonNext);
        nextButton.setEnabled(consentFormList.isEmpty());
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        setNextButtonText();

        progressIndicator = (RatingBar) view.findViewById(R.id.progress_indicator);
        if(totalForms>1) {
            progressIndicator.setVisibility(View.VISIBLE);
            progressIndicator.setNumStars(totalForms);
        }else{
            progressIndicator.setVisibility(View.GONE);
        }

        webView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview_consent);
        progressBar = (ProgressBar) view.findViewById(com.carecloud.carepaylibrary.R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

    }

    private void setNextButtonText(){
        String text;

        if (displayedFormsIndex < (totalForms - 1)) {
            text = consentFormDTO.getMetadata().getLabel().getNextFormButtonText();
        } else {
            text = consentFormDTO.getMetadata().getLabel().getFinishFormButtonText();
        }

        nextButton.setText(StringUtil.getLabelForView(text));

    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        if(getDialog()==null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

        TextView header = (TextView) view.findViewById(R.id.consent_header);
        header.setText(StringUtil.getLabelForView(consentFormDTO.getMetadata().getLabel().getConsentFormHeading()));
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
        webView.loadUrl("file:///android_asset/carecloud-forms/app/index.html");
    }


    /**
     * call Interface to load next consent forms
     */
    public void displayNextForm() {
        if (displayedFormsIndex < totalForms) {

            String payload = consentFormList.get(displayedFormsIndex).getPayload().toString();//.replaceAll("\'", Matcher.quoteReplacement("\\\'"));
            webView.loadUrl("javascript:angular.element(document.getElementsByClassName('forms')).scope().load_form(JSON.parse('" + payload + "'))");
            nextButton.setEnabled(true);
            progressIndicator.setRating(displayedFormsIndex+1);//adjust for zero index

            flowCallback.setCheckinFlow(CheckinFlowState.CONSENT, totalForms, displayedFormsIndex+1);//adjust for zero index
        }
    }

    /**
     * Call java script functions to validate consent form on screen
     */
    public void validateForm() {

        webView.loadUrl("javascript:angular.element(document.getElementsByClassName('forms')).scope().save_form()");


    }

    /**
     * Received calls from javascript functions.
     */
    public class WebViewJavaScriptInterface {

        private Context context;

        public WebViewJavaScriptInterface(Context context) {
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
                    nextButton.setClickable(true);
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
                    jsonFormSaveResponseArray.add(response);

                    if (displayedFormsIndex < totalForms-1) {
                        ++displayedFormsIndex;

                        setNextButtonText();

                        displayNextForm();

                    } else {
                        submitAllForms();
                    }


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


    private void submitAllForms() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeMgmt());
        queries.put("practice_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeId());
        queries.put("appointment_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());
        queries.put("patient_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPatientId());


        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonFormSaveResponseArray);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        getWorkflowServiceHelper().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
    }


    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
//            nextButton.setClickable(false);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
//            nextButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
//            nextButton.setClickable(true);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public boolean navigateBack(){
        if(totalForms>1 && displayedFormsIndex > 0){
            --displayedFormsIndex;
            setNextButtonText();
            displayNextForm();
            return true;
        }
        return false;
    }

}