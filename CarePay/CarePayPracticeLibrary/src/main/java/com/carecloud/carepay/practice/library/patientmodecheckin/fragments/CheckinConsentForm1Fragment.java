package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.IPracticeSession;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity.SUBFLOW_CONSENT;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinConsentForm1Fragment extends BaseCheckinFragment {


    private Button signConsentFormButton;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private Button signButton;
    private IFragmentCallback fragmentCallback;
    private ScrollView consentFormScrollView;
    private ConsentFormLabelsDTO consentFormLabelsDTO;
    private ConsentFormDTO consentFormDTO;
    private LinearLayout mainContainer;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View clickListener) {
            if (clickListener.getId() == com.carecloud.carepaylibrary.R.id.signButton && fragmentCallback != null) {
                fragmentCallback.signButtonClicked();
            }
        }
    };
    private int formIndex;

    private WebView webView;
    private ProgressBar progressBar;
    private Button nextButton;
    private int totalForms;
    private int indexForms;
    private String[] jsonAnswers;
    private String[] jsonResponse;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkin_consent_form_dynamic, container, false);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String jsonString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        consentFormDTO = gson.fromJson(jsonString, ConsentFormDTO.class);
        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.consentButtonNext);
        nextButton.setEnabled(false);
        nextButton.setText(consentFormDTO.getMetadata().getLabel().getNextFormButtonText());
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        totalForms = consentFormDTO.getMetadata().getDataModels().getPracticeForms() != null ? consentFormDTO.getMetadata().getDataModels().getPracticeForms().size() : 0;
        jsonAnswers = new String[totalForms];
        jsonResponse = new String[totalForms];

        if (indexForms == (totalForms - 1)) {
            nextButton.setText(consentFormDTO.getMetadata().getLabel().getFinishFormButtonText());
        } else {
            nextButton.setText(consentFormDTO.getMetadata().getLabel().getNextFormButtonText());
        }


        formIndex = ((PatientModeCheckinActivity) getActivity()).getConsentFormIndex();
        flowStateInfo = new FlowStateInfo(SUBFLOW_CONSENT,
                formIndex, totalForms);

        ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);



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


        webView = (WebView) view.findViewById(com.carecloud.carepaylibrary.R.id.activity_main_webview_consent);
        progressBar = (ProgressBar) view.findViewById(com.carecloud.carepaylibrary.R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

        return view;
    }


    /**
     * Init web view
     */
    public void initWebView() {


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
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        //named interface that receive calls from javascript
        webView.addJavascriptInterface(new WebViewJavaScriptInterface(getActivity()), "FormInterface");
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
     * call next form to be displaying with delay
     */
    public void callForm() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextFormDisplayed();
            }
        }, 1000);
    }

    /**
     * call Interface to load next consent forms
     */
    public void nextFormDisplayed() {
        if (indexForms < totalForms) {

            String payload = jsonAnswers[indexForms];
            webView.loadUrl("javascript:angular.element(document.getElementsByClassName('forms')).scope().load_form(JSON.parse('" + payload + "'))");
            nextButton.setEnabled(true);
            ++indexForms;
            flowStateInfo.fragmentIndex = indexForms;
            ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
            ((PatientModeCheckinActivity) getActivity()).setConsentFormIndex(indexForms);

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

            String payload = jsonAnswers[indexForms];
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextFormDisplayed();
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

                    if (indexForms < totalForms) {

                        jsonResponse[indexForms - 1] = response;

                        if (indexForms == (totalForms - 1)) {
                            nextButton.setText(consentFormDTO.getMetadata().getLabel().getFinishFormButtonText());
                        } else {
                            nextButton.setText(consentFormDTO.getMetadata().getLabel().getNextFormButtonText());
                        }

                        nextFormDisplayed();


                    } else {
                        jsonResponse[(indexForms-1)] = response;

                        navigateToNext();
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


    private void navigateToNext() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeMgmt());
        queries.put("practice_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getPracticeId());
        queries.put("appointment_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());
        queries.put("patient_id", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getAppointmentId());


        Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
        header.put("transition", "true");
        header.put("username_patient", consentFormDTO.getConsentFormPayloadDTO().getConsentFormAppointmentPayload().get(0).getAppointmentMetadata().getUsername());

        Gson gson = new Gson();
        String body = gson.toJson(jsonResponse);
        TransitionDTO transitionDTO = consentFormDTO.getMetadata().getTransitions().getUpdateConsent();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, updateconsentformCallBack, body, queries, header);
    }


    private WorkflowServiceCallback updateconsentformCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
            nextButton.setClickable(false);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            Activity activity = getActivity();
            IPracticeSession practiceSession = (IPracticeSession) activity;
            practiceSession.getPracticeNavigationHelper().navigateToWorkflow(workflowDTO);
            nextButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            SystemUtil.showDefaultFailureDialog(getActivity());
            nextButton.setClickable(true);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PatientModeCheckinActivity) {
            Activity activity = (Activity) context;
            try {
                fragmentCallback = (IFragmentCallback) activity;
            } catch (Exception e) {
                Log.e("Error", " Exception");
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signConsentFormButton != null) {
            signConsentFormButton.setOnClickListener(clickListener);
        }

    }

    private void setEnableNextButtonOnFullScroll() {
        // enable next button on scrolling all the way to the bottom

        if (consentFormScrollView.getScrollY() == 0) {
            signConsentFormButton.setEnabled(true);
        } else {

            consentFormScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    View view = consentFormScrollView.getChildAt(consentFormScrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (consentFormScrollView.getHeight() + consentFormScrollView.getScrollY()));

                    if (diff == 0) {
                        signConsentFormButton.setEnabled(true);
                    }
                }
            });

        }
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.titleTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.descriptionTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.contentTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.dateTv));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}