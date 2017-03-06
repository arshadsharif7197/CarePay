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
import android.view.WindowManager;
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
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.intake.models.IntakeFormPayloadModel;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.intake.models.LabelModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public class CheckinIntakeForm1Fragment extends BaseCheckinFragment {

    private WebView mWebView;
    private ProgressBar progressBar;
    private Button nextButton;
    private RatingBar progressIndicator;

    private int totalForms;
    private int displayedFormsIndex;

    private IntakeResponseModel inTakeForm;
    private LabelModel labelsModel;
    private View view;


    private CheckinFlowCallback flowCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            flowCallback = (CheckinFlowCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String intakeFormDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        inTakeForm = gson.fromJson(intakeFormDTOString, IntakeResponseModel.class);
        totalForms = inTakeForm.getPayload().getIntakeForms().size();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        return inflater.inflate(R.layout.fragment_checkin_intake_form1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);

        displayedFormsIndex = 0;
        flowCallback.setCheckinFlow(CheckinFlowState.INTAKE, totalForms, displayedFormsIndex);

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.intakeBtnNext);
        nextButton.setEnabled(totalForms < 1);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayNextForm();
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
        progressIndicator.setRating(displayedFormsIndex+1);//adjust for zero index


        mWebView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview);
        progressBar = (ProgressBar)view.findViewById(com.carecloud.carepaylibrary.R.id.signupProgressBarIntake);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();
    }

    private void setNextButtonText(){
        String text;

        if (displayedFormsIndex < (totalForms - 1)) {
            text = inTakeForm.getMetadata().getLabel().getNextQuestionButtonText();
        } else {
            text = inTakeForm.getMetadata().getLabel().getFinishQuestionsButtonText();
        }

        nextButton.setText(StringUtil.getLabelForView(text));

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

        TextView header = (TextView) view.findViewById(R.id.consent_header);
        header.setText(StringUtil.getLabelForView(inTakeForm.getMetadata().getLabel().getIntakeFormHeading()));
    }

    /**
     * Initialize web view
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);

        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19){
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        //load pages and links inside webview
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
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

        //Interface that receive calls from javascript
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "IntakeInterface");
        mWebView.loadUrl("file:///android_asset/intake-forms-webview/web-view.html");

    }


    /**
     * Call java script functions to change intake form index
     */
    public void displayNextForm() {
        if (displayedFormsIndex < totalForms) {
            updateFormCounter(1);
        }
        setNextButtonText();
        mWebView.loadUrl("javascript:nextIntakeForm()");
    }

    private void updateFormCounter(int increment){
        displayedFormsIndex+=increment;

        progressIndicator.setRating(displayedFormsIndex);
        flowCallback.setCheckinFlow(CheckinFlowState.INTAKE, totalForms, displayedFormsIndex);

    }

    /**
     * Call java script functions
     */
    public void displayPreviousForm() {
//        mWebView.loadUrl("javascript:displayPreviousForm()");
        mWebView.loadUrl("javascript:previousIntakeForm()");
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
         * Callback for webview to request forms
         * @return Should return a collection of intake objects received from backend i.e. payload.intake_forms

         */
        @JavascriptInterface
        public String getForms() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (displayedFormsIndex < totalForms) {
                        updateFormCounter(1);
                    }
                }
            });
            List<IntakeFormPayloadModel> myPaylod = inTakeForm.getPayload().getIntakeForms();
            return new Gson().toJson(myPaylod);
        }

        /**
         * Callback for webview to request forms answers to prefill form data
         * @return Should return answers XML received from backend i.e. payload.findings.payload.findings
         */
        @JavascriptInterface
        public String getAnswers() {
            return inTakeForm.getPayload().getFindings().getPayload().getFindings();
        }


        @JavascriptInterface
        public Object getIntakes() {
            return null;
        }

        /**
         * Submit answers from web view to endpoint update_intake
         * */
        @JavascriptInterface
        public void saveAnswers(String intakeFormModelList) {
            updateIntakeForm(intakeFormModelList);
        }

        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong) {
            Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }
    }

    /**
     * Update intakes forms based on answers.
     */
    public void updateIntakeForm(String jsonAnswers) {

        Map<String, String> queryString = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        header.put("transition", "true");

        queryString.put("appointment_id", inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());
        queryString.put("practice_id", inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());
        queryString.put("practice_mgmt", inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());
        queryString.put("patient_id", inTakeForm.getPayload().getFindings().getMetadata().getPatientId());
        //retrofit is not taking null as query parameters
        queryString.put("findings_id", inTakeForm.getPayload().getFindings().getMetadata().getFindingsId()==null?"":inTakeForm.getPayload().getFindings().getMetadata().getFindingsId());

        getWorkflowServiceHelper().execute(inTakeForm.getMetadata().getTransitions().getUpdateIntake(), updateIntakeFormCallBack, jsonAnswers, queryString, header);

    }



    /**
     * CallBack to update/submit call intake forms
     */
    private WorkflowServiceCallback updateIntakeFormCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public boolean navigateBack(){
        if(totalForms>1 && displayedFormsIndex>1){
            updateFormCounter(-1);
            setNextButtonText();
            displayPreviousForm();
            return true;
        }
        return false;
    }

}
