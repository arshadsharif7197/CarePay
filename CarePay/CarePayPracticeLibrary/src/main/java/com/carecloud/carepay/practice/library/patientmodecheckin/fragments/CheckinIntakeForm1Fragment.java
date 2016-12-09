package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.intake.models.IntakeFormPayloadModel;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.intake.models.LabelModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity.SUBFLOW_CONSENT;
import static com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity.SUBFLOW_INTAKE;


/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinIntakeForm1Fragment extends BaseCheckinFragment {

    private Button continueButton;

    private WebView mWebView;
    private TextView headerTitleTextView;
    private IntakeResponseModel inTakeForm;
    private Toolbar intakeFormsToolbar;
    private LabelModel labelsModel;
    private Button nextButton;
    private StepProgressBar mStepProgressBar;
    public String TAG = CheckinIntakeForm1Fragment.class.getSimpleName();
    private View view;
    private int formIndex;
    private int formTotalIntakesForms;
    private int formCurrentIntakesForm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_checkin_intake_form1, container, false);


        return view;
    }


    /**
     * Call to intake forms in order to get Findings if any.
     */
    public void getIntakeFormData() {


        Map<String, String> header = new HashMap<>();

        header.put("patient_id", inTakeForm.getPayload().getFindings().getMetadata().getPatientId());
        header.put("practice_id", inTakeForm.getPayload().getFindings().getMetadata().getPracticeId());
        header.put("appointment_id", inTakeForm.getPayload().getFindings().getMetadata().getAppointmentId());
        header.put("practice_mgmt", inTakeForm.getPayload().getFindings().getMetadata().getPracticeMgmt());

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

    /**
     * Initialize objects after getting intake form data.
     */
    public void initForm() {
        labelsModel = inTakeForm.getMetadata().getLabel();

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.intakeBtnNext);
        nextButton.setEnabled(true);

        formCurrentIntakesForm = 1;

        //call javascript to show next intake form.
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formCurrentIntakesForm >= (formTotalIntakesForms - 1)) {
                    nextButton.setText(labelsModel.getFinishQuestionsButtonText());
                } else {
                    nextButton.setText(labelsModel.getNextQuestionButtonText());
                }

                nextIntakeFormDisplayed();
            }
        });
        if (formCurrentIntakesForm >= (formTotalIntakesForms - 1)) {
            nextButton.setText(labelsModel.getFinishQuestionsButtonText());
        }else{
            nextButton.setText(labelsModel.getNextQuestionButtonText());
        }
        //init webview
        mWebView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview);
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
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "IntakeInterface");
        mWebView.loadUrl("file:///android_asset/intake-forms-webview/web-view.html");


    }

    /**
     * Call java script functions to change intake form index
     */
    public void nextIntakeFormDisplayed() {
        if (formIndex < ((PatientModeCheckinActivity) getActivity()).getNumIntakeForms()) {
            ++formIndex;
            ((PatientModeCheckinActivity) getActivity()).setIntakeFormIndex(formIndex);
            flowStateInfo.fragmentIndex = formIndex;
            ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
            ((PatientModeCheckinActivity) getActivity()).setConsentFormIndex(formIndex);

        }
        ++formCurrentIntakesForm;
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
            PracticeNavigationHelper.getInstance().navigateToWorkflow(getActivity(), workflowDTO);


        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(getActivity(), getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String intakeFormDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        Gson gson = new Gson();
        inTakeForm = gson.fromJson(intakeFormDTOString, IntakeResponseModel.class);
        getIntakeFormData();
        formTotalIntakesForms = inTakeForm.getPayload().getIntakeForms().size();
        ((PatientModeCheckinActivity) getActivity()).setNumIntakeForms(inTakeForm.getPayload().getIntakeForms().size());
        flowStateInfo = new PatientModeCheckinActivity.FlowStateInfo(SUBFLOW_INTAKE,
                formIndex,
                ((PatientModeCheckinActivity) getActivity()).getNumIntakeForms());

    }

    @Override
    public void onStart() {
        super.onStart();
        formIndex = ((PatientModeCheckinActivity) getActivity()).getIntakeFormIndex();
        flowStateInfo.fragmentIndex = formIndex;
        ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
    }
}
