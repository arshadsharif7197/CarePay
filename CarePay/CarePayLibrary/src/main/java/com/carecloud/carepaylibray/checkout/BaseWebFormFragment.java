package com.carecloud.carepaylibray.checkout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.utils.KeyboardWatcher;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.WebViewKeyboardAdjuster;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.List;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public abstract class BaseWebFormFragment extends BaseFragment {

    private static final String LOG_TAG = "BaseWebFormFragment";
    private WebView webView;
    private ProgressBar progressBar;
    protected Button nextButton;
    protected List<ConsentFormUserResponseDTO> filledForms;
    private TextView header;
    private StepProgressBar progressIndicator;

    private int totalForms;
    private int displayedFormsIndex;
    protected String lastFormButtonLabel;
    protected List<PracticeForm> formsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        inflateToolbarViews(view);
        showProgressDialog();
        nextButton = view.findViewById(R.id.consentButtonNext);
        nextButton.setOnClickListener(view1 -> validateForm());
        enableNextButton(false);
        nextButton.setText(Label.getLabel("checkout_forms_done"));

        progressIndicator = view.findViewById(R.id.progress_indicator);
        if (totalForms > 1) {
            progressIndicator.setVisibility(View.VISIBLE);
            progressIndicator.setNumDots(totalForms);

            nextButton.setText(Label.getLabel("next_form_button_text"));
        } else {
            nextButton.setText(lastFormButtonLabel);
            progressIndicator.setVisibility(View.GONE);
        }

        webView = view.findViewById(R.id.activity_main_webview_consent);
        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                ((CarePayApplication) getActivity().getApplicationContext()).restartSession(getActivity());
            }
            return false;
        });
        progressBar = view.findViewById(R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

        if (!getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PATIENT)) {
            WebViewKeyboardAdjuster adjuster = new WebViewKeyboardAdjuster(view, (int) getResources().getDimension(R.dimen.checkinNavBarOpenOffset));
            new KeyboardWatcher(getActivity().findViewById(android.R.id.content), false, adjuster);
        }
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
        toolbar.setNavigationOnClickListener(view1 -> {
            SystemUtil.hideSoftKeyboard(getActivity());
            getActivity().onBackPressed();
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

        WebViewDatabase.getInstance(getContext()).clearFormData();

        //speed webview loading
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


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
        webView.loadUrl(getBaseUrl());
    }


    /**
     * call Interface to load next consent forms
     *
     * @param filledForms
     */
    protected abstract void displayNextForm(List<ConsentFormUserResponseDTO> filledForms);

    protected abstract String getBaseUrl();

    protected abstract void saveForm(JsonObject jsonResponse);

    protected abstract void submitAllForms();

    protected abstract void validateForm();

    protected void validateForm(String function) {
        webView.loadUrl("javascript:window." + function + "()");
    }

    protected void loadFormUrl(String formString, String function) {
        showProgressDialog();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("javascript:window." + function + "('" + formString + "')", null);
        } else {
            webView.loadUrl("javascript:window." + function + "('" + formString + "')");
        }
        if (displayedFormsIndex > -1 && progressIndicator.getNumDots() > displayedFormsIndex) {
            progressIndicator.setCurrentProgressDot(displayedFormsIndex);
        }
        if (getDisplayedFormsIndex() == getTotalForms() - 1) {
            //last form
            nextButton.setText(lastFormButtonLabel);
        } else {
            nextButton.setText(Label.getLabel("next_form_button_text"));
        }
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
            getActivity().runOnUiThread(() -> {
                displayNextForm(filledForms);
                enableNextButton(true);
            });

        }


        /**
         * called from interface once consent form is ready to be saved
         */
        @JavascriptInterface
        public void savedForm(final String response) {
            Log.d(LOG_TAG, "FORMS SAVED -" + response);
            getActivity().runOnUiThread(() -> {
                JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();
                jsonResponse.addProperty("version",
                        formsList.get(getDisplayedFormsIndex()).getMetadata().getVersion());
                saveForm(jsonResponse);
                getNextStep();
            });

        }

        /**
         * called from interface once intake form is ready to be saved
         */
        @JavascriptInterface
        public void savedIntake(final String response) {
            Log.d(LOG_TAG, "FORMS SAVED -" + response);
            getActivity().runOnUiThread(() -> {
                JsonObject jsonResponse = new JsonParser().parse(response).getAsJsonObject();

                saveForm(jsonResponse);

                getNextStep();

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
            new Handler().postDelayed(() -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        webView.scrollTo(0, 0);
                        enableNextButton(true);
                        hideProgressDialog();
                    });
                }
            }, 500);
        }

    }

    private void getNextStep() {
        enableNextButton(false);
        if (displayedFormsIndex < totalForms - 1) {
            ++displayedFormsIndex;
            displayNextForm(filledForms);

        } else {
            submitAllForms();
        }

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

    protected void enableNextButton(boolean isEnabled) {
        if (nextButton != null) {
            nextButton.setSelected(isEnabled);
            nextButton.setClickable(isEnabled);
        }
    }
}