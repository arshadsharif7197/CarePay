package com.carecloud.carepay.patient.intakeforms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.eligibility.activities.EligibilityActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.intake.models.IntakeFormPayloadModel;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.intake.models.LabelModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Display intake web forms on local assets
 */
public class InTakeWebViewActivity extends BasePatientActivity {

    private WebView mWebView;
    private TextView headerTitleTextView;
    private IntakeResponseModel inTakeForm;
    private Toolbar intakeFormsToolbar;
    private LabelModel labelsModel;
    private Button nextButton;
    private StepProgressBar mStepProgressBar;
    public String TAG = InTakeWebViewActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inTakeForm = getConvertedDTO(IntakeResponseModel.class);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_in_take_web_view);
        getIntakeFormData();
        //initForm();
    }


    @Override
    public void onBackPressed() {

        if (mStepProgressBar.getCurrentProgressDot() > 0) {
            mStepProgressBar.previous();
            headerTitleTextView.setText(String.format(labelsModel.getIntakeFormHeading(), mStepProgressBar.getCurrentProgressDot() + 1, mStepProgressBar.getNumDots()));
            previousIntakeForm();
        } else {
            super.onBackPressed();
        }
    }


    private void getIntakeFormData() {

        Map<String, String> header = new HashMap<>();

        header.put("patient_id", inTakeForm.getPayload().getFindings().getMetadata().getPatientId());
        header.put("practice_id", inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());
        header.put("appointment_id", inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());
        header.put("practice_mgmt", inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());

        WorkflowServiceHelper.getInstance().execute(inTakeForm.getMetadata().getLinks().getIntake(), intakeFormCallback, header);
    }


    public void initForm() {
        labelsModel = inTakeForm.getMetadata().getLabel();

        nextButton = (Button) findViewById(com.carecloud.carepaylibrary.R.id.intakeBtnNext);
        nextButton.setEnabled(true);
        nextButton.setText(labelsModel.getNextQuestionButtonText());
        //dynamic step progres bar
        mStepProgressBar = (StepProgressBar) findViewById(com.carecloud.carepaylibrary.R.id.stepProgressBar2);
        mStepProgressBar.setCumulativeDots(true);
        mStepProgressBar.setNumDots(inTakeForm.getPayload().getIntakeForms().size());

        //toolbar for intake form
        intakeFormsToolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.intakeToolbar);
        intakeFormsToolbar.setTitle("");
        intakeFormsToolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                com.carecloud.carepaylibrary.R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(intakeFormsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        headerTitleTextView = (TextView) intakeFormsToolbar.findViewById(com.carecloud.carepaylibrary.R.id.intakeToolbarTitle);
        SystemUtil.setGothamRoundedMediumTypeface(this, headerTitleTextView);
        headerTitleTextView.setText(String.format(labelsModel.getIntakeFormHeading(), mStepProgressBar.getCurrentProgressDot() + 1, mStepProgressBar.getNumDots()));


        //call javascript to show next intake form.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStepProgressBar.getCurrentProgressDot() < mStepProgressBar.getNumDots() - 1) {
                    mStepProgressBar.next();
                    headerTitleTextView.setText(String.format(labelsModel.getIntakeFormHeading(), mStepProgressBar.getCurrentProgressDot() + 1, mStepProgressBar.getNumDots()));
                }

                if (mStepProgressBar.getCurrentProgressDot() == mStepProgressBar.getNumDots() - 1) {
                    nextButton.setText(labelsModel.getFinishQuestionsButtonText());
                } else {
                    nextButton.setText(labelsModel.getNextQuestionButtonText());
                }
                nextIntakeFormDisplayed();
            }
        });


        //init webview
        mWebView = (WebView) findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview);
        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19){
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //load pages and links inside webview
        mWebView.setWebViewClient(new WebViewClient());
        //Interface that receive calls from javascript
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "IntakeInterface");
        mWebView.loadUrl("file:///android_asset/intake-forms-webview/web-view.html");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    /**
     * Call java script functions to change intake form index
     */
    public void nextIntakeFormDisplayed() {
        mWebView.loadUrl("javascript:nextIntakeForm()");
    }

    /**
     * Call java script functions
     */
    public void previousIntakeForm() {
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

        // return a collection of intake objects received from backend i.e. payload.intake_forms
        @JavascriptInterface
        public String getForms() {
            List<IntakeFormPayloadModel> myPaylod = inTakeForm.getPayload().getIntakeForms();
            return new Gson().toJson(myPaylod);
        }

        // return answers XML received from backend i.e. payload.findings.payload.findings
        @JavascriptInterface
        public String getAnswers() {
            return inTakeForm.getPayload().getFindings().getPayload().getFindings();
        }


        @JavascriptInterface
        public Object getIntakes() {
            return null;
        }

        // Should accept a collection of XML encoded answers.
        @JavascriptInterface
        public void saveAnswers(String intakeFormModelList) {

            updateIntakeForm(intakeFormModelList);

        }

        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong) {
            Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
        }

    }


    public void updateIntakeForm(String jsonAnswers) {

        Map<String, String> queryString = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        header.put("transition", "true");

        queryString.put("appointment_id", inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());
        queryString.put("practice_id", inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());
        queryString.put("practice_mgmt", inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());
        queryString.put("patient_id", inTakeForm.getPayload().getFindings().getMetadata().getPatientId());
        //retrofit is not taking null for query parameters
        queryString.put("findings_id", inTakeForm.getPayload().getFindings().getMetadata().getFindingsId()==null?"":inTakeForm.getPayload().getFindings().getMetadata().getFindingsId());

        WorkflowServiceHelper.getInstance().execute(inTakeForm.getMetadata().getTransitions().getUpdateIntake(), updateIntakeFormCallBack, jsonAnswers, queryString, header);

    }

    /**
     * CallBack to update/submit call intake forms
     */
    private WorkflowServiceCallback updateIntakeFormCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(InTakeWebViewActivity.this).navigateToWorkflow(workflowDTO);

            //Intent intent = new Intent(InTakeWebViewActivity.this, EligibilityActivity.class);
            //startActivity(intent);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(InTakeWebViewActivity.this, getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * CallBack to load intake forms call intake forms
     */
    WorkflowServiceCallback intakeFormCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            inTakeForm = new Gson().fromJson(workflowDTO.toString(), IntakeResponseModel.class);
            initForm();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(InTakeWebViewActivity.this, getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}
