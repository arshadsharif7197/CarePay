package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
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
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinIntakeForm1Fragment extends Fragment {
    private Button continueButton;

    private WebView mWebView;
    private TextView headerTitleTextView;
    private IntakeResponseModel inTakeForm;
    private Toolbar intakeFormsToolbar;
    private LabelModel labelsModel;
    private Button nextButton;
    private StepProgressBar mStepProgressBar;
    public String TAG = CheckinIntakeForm1Fragment.class.getSimpleName();
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_checkin_intake_form1, container, false);

//        getIntakeFormData();

        continueButton = (Button) view.findViewById(R.id.checkinIntakeForm1ContinueClickable);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinIntakeForm2Fragment fragment = new CheckinIntakeForm2Fragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).changeCounterOfForm(PatientModeCheckinActivity.SUBFLOW_INTAKE, 2,
                                                                                PatientModeCheckinActivity.NUM_INTAKE_FORMS);
            }
        });


        return view;
    }




    public void getIntakeFormData(){

        Map<String, String> header = new HashMap<>();
        header.put("patient_id", "cd5bc403-4bfe-4d60-ae2d-99e26d4fd4a2");//inTakeForm.getPayload().getFindings().getMetadata().getPatientId());//cd5bc403-4bfe-4d60-ae2d-99e26d4fd4a2
        header.put("practice_id", "77b81aa8-1155-4da7-9fd9-2f6967b09a93");//inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());//77b81aa8-1155-4da7-9fd9-2f6967b09a93
        header.put("appointment_id", "050bd799-de01-4692-a950-10d12d20dd2e");// inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());//050bd799-de01-4692-a950-10d12d20dd2e
        header.put("practice_mgmt", "carecloud");// inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());//carecloud
        WorkflowServiceHelper.getInstance().execute(inTakeForm.getMetadata().getLinks().getIntake(), intakeFormCallback, header);

    }

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
            SystemUtil.showDialogMessage(getActivity(), getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    public void initForm() {
        labelsModel = inTakeForm.getMetadata().getLabel();

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.intakeBtnNext);
        nextButton.setEnabled(true);
        nextButton.setText(labelsModel.getNextQuestionButtonText());

        //mStepProgressBar = (StepProgressBar) view.findViewById(com.carecloud.carepaylibrary.R.id.stepProgressBarIntake2);
        mStepProgressBar.setCumulativeDots(true);
        mStepProgressBar.setNumDots(inTakeForm.getPayload().getIntakeForms().size());

        //toolbar for intake form
        intakeFormsToolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.intakeToolbar);
        intakeFormsToolbar.setTitle("");
        intakeFormsToolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), com.carecloud.carepaylibrary.R.drawable.icn_patient_mode_nav_back));

        //setSupportActionBar(intakeFormsToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        headerTitleTextView = (TextView) intakeFormsToolbar.findViewById(com.carecloud.carepaylibrary.R.id.intakeToolbarTitle);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), headerTitleTextView);
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
                }else{
                    nextButton.setText(labelsModel.getNextQuestionButtonText());
                }
                nextIntakeFormDisplayed();
            }
        });


        //init webview
        mWebView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //load pages and links inside webview
        mWebView.setWebViewClient(new WebViewClient());
        //Interface that receive calls from javascript
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "IntakeInterface");
        mWebView.loadUrl("file:///android_asset/intake-forms-webview/web-view.html");

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

        // Should return a collection of intake objects received from backend i.e. payload.intake_forms
        @JavascriptInterface
        public String getForms() {
            List<IntakeFormPayloadModel> myPaylod = inTakeForm.getPayload().getIntakeForms();
            return new Gson().toJson(myPaylod);
        }

        // Should return answers XML received from backend i.e. payload.findings.payload.findings
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

        //inTakeForm.getPayload().getFindings().getPayload().setFindings(jsonAnswers);
        // String body = gson.toJson(inTakeForm);
        // UpdateIntakeModel transitionUpdateIntake =  inTakeForm.getMetadata().getTransitions().getUpdateIntake();

        queryString.put("appointment_id", inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());//050bd799-de01-4692-a950-10d12d20dd2e
        queryString.put("practice_id", inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());
        queryString.put("practice_mgmt", inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());
        queryString.put("patient_id", inTakeForm.getPayload().getFindings().getMetadata().getPatientId());//cd5bc403-4bfe-4d60-ae2d-99e26d4fd4a2
        queryString.put("findings_id", inTakeForm.getPayload().getFindings().getMetadata().getFindingsId());


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
            //PatientNavigationHelper.getInstance(InTakeActivityWebView.this).navigateToWorkflow(workflowDTO);
            SystemUtil.showDialogMessage(getActivity(), getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), "SUCCESS");

        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(getActivity(), getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };






}
