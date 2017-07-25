package com.carecloud.carepaylibray.checkout;

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
import android.webkit.WebViewDatabase;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.KeyboardWatcher;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.WebViewKeyboardAdjuster;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.marcok.stepprogressbar.StepProgressBar;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Edited by lmenendez on 3/5/2017.
 */

public abstract class BaseWebFormFragment extends BaseFragment {

    private WebView webView;
    private ProgressBar progressBar;
    protected Button nextButton;
    private TextView header;
    private StepProgressBar progressIndicator;

    private int totalForms;
    private int displayedFormsIndex;
    protected String lastFormButtonLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_form, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);

        nextButton = (Button) view.findViewById(R.id.consentButtonNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });
        nextButton.setEnabled(false);
        nextButton.setText(Label.getLabel("checkout_forms_done"));

        progressIndicator = (StepProgressBar) view.findViewById(R.id.progress_indicator);
        if (totalForms > 1) {
            progressIndicator.setVisibility(View.VISIBLE);
            progressIndicator.setNumDots(totalForms);

            nextButton.setText(Label.getLabel("next_form_button_text"));
        } else {
            progressIndicator.setVisibility(View.GONE);
        }

        webView = (WebView) view.findViewById(R.id.activity_main_webview_consent);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarConsent);
        progressBar.setVisibility(View.VISIBLE);
        initWebView();

        WebViewKeyboardAdjuster adjuster = new WebViewKeyboardAdjuster(view, (int) getResources().getDimension(R.dimen.checkinNavBarOpenOffset));
        new KeyboardWatcher(getActivity().findViewById(android.R.id.content), false, adjuster);
    }

    protected void setHeader(String text) {
        header.setText(text);
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });


        header = (TextView) view.findViewById(R.id.toolbar_title);
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
        webView.setWebViewClient(new WebViewClient());
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
     */
    protected abstract void displayNextForm();

    protected abstract String getBaseUrl();

    protected abstract void saveForm(JsonObject jsonResponse);

    protected abstract void submitAllForms();

    protected abstract void validateForm();

    protected void validateForm(String function) {
        webView.loadUrl("javascript:window." + function + "()");
    }

    protected void loadFormUrl(String formString, String function) {
        webView.loadUrl("javascript:window." + function + "('" + formString + "')");
        progressIndicator.setCurrentProgressDot(displayedFormsIndex);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayNextForm();
                    nextButton.setEnabled(true);
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
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nextButton.setEnabled(true);
                }
            });
        }

    }

    private void getNextStep() {
        if (displayedFormsIndex < totalForms - 1) {
            ++displayedFormsIndex;
            nextButton.setEnabled(false);
            displayNextForm();

        } else {
            submitAllForms();
        }

    }
}