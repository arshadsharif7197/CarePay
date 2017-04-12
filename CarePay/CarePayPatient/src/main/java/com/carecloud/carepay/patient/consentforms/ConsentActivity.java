package com.carecloud.carepay.patient.consentforms;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.marcok.stepprogressbar.StepProgressBar;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class ConsentActivity extends BasePatientActivity {

    private ConsentFormDTO consentFormDTO;
    private TextView title;
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

        try {
            WorkflowDTO consentPayload = getConvertedDTO(WorkflowDTO.class);
            JsonArray jsonArray = consentPayload.getMetadata().getAsJsonObject("data_models").getAsJsonArray("practice_forms");
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject payload = ((JsonObject) jsonArray.get(i)).getAsJsonObject("payload");
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
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_nav_back));
        setSupportActionBar(toolbar);
        initWebView();
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

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        Gson gson = new Gson();
        String body = gson.toJson(jsonResponse);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        getWorkflowServiceHelper().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
    }

    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
            nextButton.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.getInstance(ConsentActivity.this).navigateToWorkflow(workflowDTO);
            nextButton.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            nextButton.setClickable(true);
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }



}