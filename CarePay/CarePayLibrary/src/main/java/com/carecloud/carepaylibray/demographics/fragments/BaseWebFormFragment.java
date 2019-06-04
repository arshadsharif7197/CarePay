package com.carecloud.carepaylibray.demographics.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewDatabase;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.KeyboardWatcher;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.WebViewKeyboardAdjuster;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.marcok.stepprogressbar.StepProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public abstract class BaseWebFormFragment extends BaseCheckinFragment {

    private static final String LOG_TAG = "BaseWebFormFragment";
    private WebView webView;
    private ProgressBar progressBar;
    private Button nextButton;
    private TextView header;
    private StepProgressBar progressIndicator;

    private int totalForms;
    private int displayedFormsIndex;
    protected List<PracticeForm> formsList;

    protected List<JsonObject> jsonFormSaveResponseArray = new ArrayList<>();
    private CheckinFlowCallback callback;
    protected int pageScrollOffsetPercentage;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_form, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);
        showProgressDialog();

        nextButton = view.findViewById(R.id.consentButtonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    showScrollAlert();
                    return true;
                }
                return false;
            }
        });
        enableNextButton(false);

        progressIndicator = view.findViewById(R.id.progress_indicator);
        if (totalForms > 1) {
            progressIndicator.setVisibility(View.VISIBLE);
            progressIndicator.setNumDots(totalForms);
        } else {
            progressIndicator.setVisibility(View.GONE);
        }

        webView = view.findViewById(R.id.activity_main_webview_consent);
        progressBar = view.findViewById(R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

        if (!getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PATIENT)) {
            WebViewKeyboardAdjuster adjuster = new WebViewKeyboardAdjuster(view, (int) getResources().getDimension(R.dimen.checkinNavBarOpenOffset));
            new KeyboardWatcher(getActivity().findViewById(android.R.id.content), false, adjuster);
        }
    }

    private void showScrollAlert() {
        new CustomMessageToast(getActivity(), "Please review the entire form to continue.",
                CustomMessageToast.NOTIFICATION_TYPE_WARNING).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        callback.setCheckinFlow(getCheckinFlowState(), totalForms, displayedFormsIndex + 1);
    }

    protected void setHeader(String text) {
        header.setText(text);
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
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


        header = view.findViewById(R.id.toolbar_title);
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
        settings.setSaveFormData(false);
        settings.setDomStorageEnabled(true);

        WebViewDatabase.getInstance(getContext()).clearFormData();

        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
//        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);


        //show progress bar
        webView.setWebViewClient(new BaseWebClient());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
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
        webView.loadUrl("javascript:window." + function + "()");
    }

    protected void loadFormUrl(String formString, String function) {
        if (!isAdded()) {
            hideProgressDialog();
            return;
        }
        showProgressDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:window." + function + "('" + formString + "')", null);
        } else {
            webView.loadUrl("javascript:window." + function + "('" + formString + "')");
        }
        progressIndicator.setCurrentProgressDot(displayedFormsIndex);

        callback.setCheckinFlow(getCheckinFlowState(), totalForms, displayedFormsIndex + 1);//adjust for zero index
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
                    enableNextButton(true);
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
                    if (formsList != null) {
                        jsonResponse.addProperty("version",
                                formsList.get(getDisplayedFormsIndex()).getMetadata().getVersion());
                    }
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
            if (context != null) {
                Toast.makeText(context, message, (lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
            }
        }

        /**
         * called from interface when form html has been replaced
         */
        @JavascriptInterface
        public void loadedForm() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableNextButton(true);
                                hideProgressDialog();
                            }
                        });
                    }
                }
            }, 500);

        }

        /**
         * called from interface when intake form html has been replaced
         */
        @JavascriptInterface
        public void loadedIntake() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enableNextButton(pageScrollOffsetPercentage > 98);
                                hideProgressDialog();
                            }
                        });
                    }
                }
            }, 500);
        }

        @JavascriptInterface
        public void scroll_form(String scrollPercentage) {
            try {
                JSONObject json = new JSONObject(scrollPercentage);
                pageScrollOffsetPercentage = json.getInt("pageScrollOffsetPercentage");
                if (pageScrollOffsetPercentage > 98) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            formScrolledToBottom();
                        }
                    });
                }
            } catch (JSONException e) {
                pageScrollOffsetPercentage = 0;
                e.printStackTrace();
            }
        }

    }

    protected abstract void formScrolledToBottom();

    private void getNextStep() {
        enableNextButton(false);
        if (displayedFormsIndex < totalForms - 1) {
            ++displayedFormsIndex;
            displayNextForm();

        } else {
            submitAllForms();
        }

    }


    @Override
    public boolean navigateBack() {
        if (totalForms > 1 && displayedFormsIndex > 0) {
            --displayedFormsIndex;
            displayNextForm();
            return true;
        }
        return false;
    }

    protected WorkflowServiceCallback getUpdateFormCallBack(final String formTypes) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                onUpdate(callback, workflowDTO);
                enableNextButton(true);

                String[] params = {getString(R.string.param_forms_count), getString(R.string.param_forms_type)};
                Object[] values = {totalForms, formTypes};
                MixPanelUtil.logEvent(getString(R.string.event_checkin_forms), params, values);

                if (getString(R.string.forms_type_consent).equals(formTypes)) {
                    MixPanelUtil.endTimer(getString(R.string.timer_consent_forms));
                } else if (getString(R.string.forms_type_intake).equals(formTypes)) {
                    MixPanelUtil.endTimer(getString(R.string.timer_intake_forms));
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                enableNextButton(true);
                hideProgressDialog();
                if (isAdded()) {
                    showErrorNotification(exceptionMessage);
                    Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
                }
            }
        };
    }


    class BaseWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            hideProgressDialog();
            enableNextButton(true);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            hideProgressDialog();
            enableNextButton(true);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            hideProgressDialog();
            enableNextButton(true);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            hideProgressDialog();
            enableNextButton(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("formIndex", getDisplayedFormsIndex());
        Gson gson = new Gson();
        outState.putString("formResponses", gson.toJson(jsonFormSaveResponseArray));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            int formIndex = savedInstanceState.getInt("formIndex", 0);
            setDisplayedFormsIndex(formIndex);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<JsonObject>>() {
            }.getType();
            jsonFormSaveResponseArray = gson.fromJson(savedInstanceState.getString("formResponses"), listType);
        }
    }

    protected void enableNextButton(boolean isEnabled) {
        if (nextButton != null) {
            nextButton.setSelected(isEnabled);
            nextButton.setClickable(isEnabled);
        }
    }
}