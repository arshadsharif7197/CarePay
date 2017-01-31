package com.carecloud.carepay.patient.consentforms;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentForm1Fragment;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentForm2Fragment;
import com.carecloud.carepay.patient.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormMetadataDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppoPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormAppointmentsPayloadDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;



import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


public class ConsentActivity extends BasePatientActivity implements IFragmentCallback {


    private ConsentFormLabelsDTO consentFormLabelsDTO;

    private AppointmentsPayloadDTO appointmentsPayloadDTO;
    private AppointmentsResultModel appointmentsResultModel;
    private ConsentFormAppointmentsPayloadDTO consentFormAppointmentsPayloadDTO;
    private ConsentFormAppoPayloadDTO consentFormAppoPayloadDTO;

    private String authorizationTitle;
    private String medicareTitle;
    private String hippaTitle;
    private String hippaDescription;
    private String signAuthLabel;
    private String signMedicareLabel;
    private String signHippaLabel;
    private ConsentFormDTO consentFormDTO;
    private ConsentFormMetadataDTO consentFormMetadataDTO;
    private ConsentFormPayloadDTO consentFormPayloadDTO;
    private TextView title;
    private FormId showingForm = FormId.FORM1;
    private View indicator0;
    private View indicator1;
    private View indicator2;
    private String legalFirstNameLabel;
    private String legalLastNameLabel;
    private String clearSignLabel;
    private String beforeSignWarning;
    private String unabletoSignLabel;
    private String signButtonLabel;
    private String patientSignLabel;
    private String legalsignLabel;
    private String readCarefullySign;
    private String consentMainTitle;
    private String medicareDescription;
    private String medicareForm;
    private String providerName = " ";
    private String patientFirstName = " ";
    private String patientLastName = " ";
    private String authorizationDescription1;
    private String authorizationDescription2;
    private String authForm;
    static int numberofforms = 0;

    private WebView webView;
    private ProgressBar progressBar;
    private Button nextButton;
    private StepProgressBar stepProgressBar;
    private int totalForms;
    private int indexForms;
    private String[] jsonAnswers;

    private String[] jsonResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.consent_activity_layout_webview);
        consentFormDTO = getConvertedDTO(ConsentFormDTO.class);
        totalForms = consentFormDTO.getMetadata().getDataModels().getPracticeForms() != null ? consentFormDTO.getMetadata().getDataModels().getPracticeForms().size() : 0;
        jsonAnswers = new String[totalForms];
        jsonResponse = new String[totalForms];
        nextButton = (Button) findViewById(com.carecloud.carepaylibrary.R.id.consentButtonNext);
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        Bundle bundle = this.getIntent().getBundleExtra(PatientNavigationHelper.class.getSimpleName());
        String jsonString = bundle.getString(PatientNavigationHelper.class.getSimpleName());

        try {

            JSONObject consentPayload = new JSONObject(jsonString);
            JSONArray jsonArray = consentPayload.getJSONObject("metadata").getJSONObject("data_models").getJSONArray("practice_forms");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject payload = ((JSONObject) jsonArray.get(i)).getJSONObject("payload");
                String payloadString = payload.toString();
                payloadString = payloadString.replaceAll("\'", Matcher.quoteReplacement("\\\'"));
                jsonAnswers[i] = payloadString;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            nextButton.setEnabled(true);
        }


        stepProgressBar = (StepProgressBar) findViewById(com.carecloud.carepaylibrary.R.id.stepProgressBarConsent);
        stepProgressBar.setCumulativeDots(true);
        stepProgressBar.setNumDots(totalForms);
        progressBar = (ProgressBar) findViewById(com.carecloud.carepaylibrary.R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        if (stepProgressBar.getCurrentProgressDot() == stepProgressBar.getNumDots() - 1) {
            nextButton.setText(consentFormDTO.getMetadata().getLabel().getFinishFormButtonText());
        } else {
            nextButton.setText(consentFormDTO.getMetadata().getLabel().getNextFormButtonText());
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.consentform_toolbar);
        toolbar.setTitle("");
        title = (TextView) toolbar.findViewById(R.id.consentform_toolbar_title);
        title.setText(String.format(consentFormDTO.getMetadata().getLabel().getConsentFormHeading(), stepProgressBar.getCurrentProgressDot() + 1, stepProgressBar.getNumDots()));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);
        initWebView();
        numberofforms = SignatureActivity.numOfLaunches;
    }


    /**
     * Init web view
     */
    public void initWebView() {

        //init webview
        webView = (WebView) findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview_consent);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //load pages and links inside webview
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //Interface that receive calls from javascript
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(this), "FormInterface");
        //show progress bar
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                    nextButton.setEnabled(true);
                }
            }
        });
        webView.loadUrl("file:///android_asset/carecloud-forms/app/index.html");

    }

    /**
     * Checks for upcoming appointments
     */
    public void callForm() {
        // nextFormDisplayed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //  nextFormDisplayed();
            }
        }, 2000);
    }

    /**
     * Call java script functions to change intake form index
     */
    public void nextFormDisplayed() {


        if (indexForms < totalForms) {

            String payload = jsonAnswers[indexForms];
            webView.loadUrl("javascript:angular.element(document.getElementsByClassName('forms')).scope().load_form(JSON.parse('" + payload + "'))");
            nextButton.setEnabled(true);
            ++indexForms;
        } else {
            nextButton.setEnabled(true);
        }


    }

    /**
     * Call java script functions to validate consent form on screen
     */
    public void validateForm() {

        nextButton.setClickable(true);
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
         * start loading form
         */
        @JavascriptInterface
        public void webviewReady(String message) {
            Log.d(LOG_TAG, "FORMS SAVED -" + message);
            ConsentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextFormDisplayed();
                }
            });


        }

        /**
         * save form
         */
        @JavascriptInterface
        public void savedForm(final String response) {
            ConsentActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (indexForms < totalForms) {
                        jsonResponse[indexForms] = response;
                        if (stepProgressBar.getCurrentProgressDot() < stepProgressBar.getNumDots() - 1) {
                            stepProgressBar.next();
                            title.setText(String.format(consentFormDTO.getMetadata().getLabel().getConsentFormHeading(), stepProgressBar.getCurrentProgressDot() + 1, stepProgressBar.getNumDots()));
                        }

                        if (stepProgressBar.getCurrentProgressDot() == stepProgressBar.getNumDots() - 1) {
                            nextButton.setText(consentFormDTO.getMetadata().getLabel().getFinishFormButtonText());
                        } else {
                            nextButton.setText(consentFormDTO.getMetadata().getLabel().getNextFormButtonText());
                        }
                        nextFormDisplayed();
                    } else {
                        nextButton.setClickable(false);
                        jsonResponse[(indexForms-1)] = response;
                        navigateToNext();
                    }


                }
            });


        }

        /**
         * send message to user
         */
        @JavascriptInterface
        public void makeToast(String message, boolean lengthLong) {
            if(context!=null) {
                Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
            }
        }

    }


    private void navigateToNext() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeMgmt());
        queries.put("practice_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeId());
        queries.put("appointment_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());
        queries.put("patient_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPatientId());

        Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
        header.put("transition", "true");

        Gson gson = new Gson();
        String body = gson.toJson(jsonResponse);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
    }

    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            nextButton.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(ConsentActivity.this).navigateToWorkflow(workflowDTO);
            nextButton.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(ConsentActivity.this);
            nextButton.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, SignatureActivity.class);

        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
        intent.putExtra("consentform", showingForm);
        if (SignatureActivity.numOfLaunches == 2) {
            // pass the whole DTO
            Gson gson = new Gson();
            String consentformDTO = gson.toJson(consentFormDTO);
            intent.putExtra("consentform_model", consentformDTO);
        }
        if (showingForm == FormId.FORM1) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());
            intent.putExtra("Header_Title", signMedicareLabel);
            intent.putExtra("Subtitle", consentFormLabelsDTO.getConsentReadCarefullyWarning());
        } else if (showingForm == FormId.FORM2) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());

        } else {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignHipaaAgreementTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getConsentReadCarefullyWarning());
        }

        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CarePayConstants.SIGNATURE_REQ_CODE) {
            if (SignatureActivity.isBackButtonClicked) {
                SignatureActivity.isBackButtonClicked = false;
            } else {
                Fragment fragment = getNextConsentForm();
                if (fragment != null) {
                    replaceFragment(fragment, true);
                }
            }

        }
    }

    private void getConsentFormInformation() {

        if (consentFormDTO != null) {
            consentFormMetadataDTO = consentFormDTO.getMetadata();
            consentFormPayloadDTO = consentFormDTO.getConsentFormPayloadDTO();
            consentFormAppointmentsPayloadDTO = consentFormPayloadDTO.getConsentFormAppointmentPayload().get(0);
            consentFormAppoPayloadDTO = consentFormAppointmentsPayloadDTO.getAppointmentPayload();
            patientFirstName = consentFormAppoPayloadDTO.getAppointmentPatient().getFirstName();
            patientLastName = consentFormAppoPayloadDTO.getAppointmentPatient().getLastName();
            providerName = consentFormAppoPayloadDTO.getAppoPayloadProvider().getName();

            if (consentFormMetadataDTO != null) {
                consentFormLabelsDTO = consentFormMetadataDTO.getLabel();

                if (consentFormLabelsDTO != null) {
                    authorizationTitle = consentFormLabelsDTO.getAuthorizationFormTitle();
                    medicareTitle = consentFormLabelsDTO.getConsentForMedicareTitle();
                    hippaTitle = consentFormLabelsDTO.getHipaaAgreementTitle();
                    medicareDescription = consentFormLabelsDTO.getConsentForMedicareText();
                    readCarefullySign = consentFormLabelsDTO.getConsentReadCarefullyWarning();
                    authorizationDescription1 = consentFormLabelsDTO.getAuthorizationGrantText();
                    authorizationDescription2 = consentFormLabelsDTO.getAuthorizationLegalText();
                    hippaDescription = consentFormLabelsDTO.getHipaaConfidentialityAgreementText();
                    consentMainTitle = consentFormLabelsDTO.getConsentMainTitle();
                    signAuthLabel = consentFormLabelsDTO.getSignAuthorizationFormTitle();
                    signMedicareLabel = consentFormLabelsDTO.getSignConsentForMedicareTitle();
                    signHippaLabel = consentFormLabelsDTO.getSignHipaaAgreementTitle();
                    legalFirstNameLabel = consentFormLabelsDTO.getLegalFirstNameLabel();
                    legalLastNameLabel = consentFormLabelsDTO.getLegalLastNameLabel();
                    clearSignLabel = consentFormLabelsDTO.getSignClearButton();
                    signButtonLabel = consentFormLabelsDTO.getConfirmSignatureButton();
                    unabletoSignLabel = consentFormLabelsDTO.getUnableToSignText();
                    beforeSignWarning = consentFormLabelsDTO.getBeforeSignatureWarningText();
                    legalsignLabel = consentFormLabelsDTO.getLegalSignatureLabel();
                    patientSignLabel = consentFormLabelsDTO.getPatientSignatureHeading();

                    replaceFragment(getConsentForm(), false);
                    Log.d(LOG_TAG, "consent form information");
                }
            }


        }
    }


    private Fragment getConsentForm() {

        if (showingForm == FormId.FORM1) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form1"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            updateTitle(FormId.FORM1);
            return consentForm1Fragment;
        } else if (showingForm == FormId.FORM2) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form2"));
            ConsentForm2Fragment consentForm2Fragment = new ConsentForm2Fragment();
            consentForm2Fragment.setArguments(bundle);
            updateTitle(FormId.FORM2);
            return consentForm2Fragment;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getConsentFormData("form3"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            updateTitle(FormId.FORM3);
            return consentForm1Fragment;
        }
    }

    /**
     * Updates the title to show the form number and the corresponding bulleted tab
     *
     * @param currentForm The id of the current form
     */
    private void updateTitle(FormId currentForm) {
        switch (currentForm) {
            case FORM1:
                title.setText(consentMainTitle + " 1 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_gray);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM2:
                title.setText(consentMainTitle + " 2 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM3:
                title.setText(consentMainTitle + " 3 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_blue);
                break;
            default:
                break;
        }

    }

    private FormData getConsentFormData(String formName) {
        FormData formData = new FormData();
        DateUtil.getInstance().setToCurrent(); // set the date to current
        if (formName.equals("form1")) {
            formData.setTitle(consentFormLabelsDTO.getConsentForMedicareTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(medicareDescription);
            formData.setButtonLabel(consentFormLabelsDTO.getSignFormButton());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        } else if (formName.equals("form2")) {
            formData.setTitle(consentFormLabelsDTO.getAuthorizationFormTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(authorizationDescription1);
            formData.setContent2(authorizationDescription2);
            formData.setButtonLabel(consentFormLabelsDTO.getSignFormButton());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        } else { //form3
            formData.setTitle(consentFormLabelsDTO.getHipaaAgreementTitle());
            formData.setDescription(readCarefullySign);
            formData.setContent(consentFormLabelsDTO.getHipaaConfidentialityAgreementText());
            formData.setButtonLabel(consentFormLabelsDTO.getSignFormButton());
            formData.setDate(DateUtil.getInstance().getDateAsMonthLiteralDayOrdinalYear());
        }

        return formData;

    }

    private Fragment getNextConsentForm() {
        showingForm = showingForm.next();
        if (showingForm != FormId.NONE) {
            return getConsentForm();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        showingForm = showingForm.prev();
        if (showingForm != FormId.NONE) {
            updateTitle(showingForm);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public ConsentFormLabelsDTO getConsentFormLabelsDTO() {
        return consentFormLabelsDTO;
    }

    public void setConsentFormLabelsDTO(ConsentFormLabelsDTO consentFormLabelsDTO) {
        this.consentFormLabelsDTO = consentFormLabelsDTO;

    }

    public ConsentFormDTO getConsentFormDTO() {
        return consentFormDTO;
    }

    public void setConsentFormDTO(ConsentFormDTO consentFormDTO) {
        this.consentFormDTO = consentFormDTO;
    }

    public AppointmentsPayloadDTO getAppointmentsPayloadDTO() {
        return appointmentsPayloadDTO;
    }

    public void setAppointmentsPayloadDTO(AppointmentsPayloadDTO appointmentsPayloadDTO) {
        this.appointmentsPayloadDTO = appointmentsPayloadDTO;
    }

    /**
     * Enum to identify the forms
     */
    public enum FormId {
        FORM1, FORM2, FORM3, NONE;

        /**
         * Go to next
         */
        public FormId next() {
            if (this.equals(FORM1)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM3;
            }
            return NONE;
        }

        /**
         * Go to prev
         */
        public FormId prev() {
            if (this.equals(FORM3)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM1;
            }
            return NONE;
        }
    }

}