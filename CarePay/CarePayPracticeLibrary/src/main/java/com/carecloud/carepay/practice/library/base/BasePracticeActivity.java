package com.carecloud.carepay.practice.library.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.customdialog.IConfirmPracticeAppPin;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;

import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePracticeActivity extends BaseActivity
        implements IConfirmPracticeAppPin {

    private long lastFullScreenSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE ?
                R.style.PracticeModeActivity : R.style.PatientModeActivity);
        setSystemUiVisibility();
        setNavigationBarVisibility();
        Log.d("New Relic", getClass().getName());
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            final View rootView = findViewById(android.R.id.content);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    long now = System.currentTimeMillis();
                    if(now - lastFullScreenSet > 1000) {
                        Log.d("Base", "Display Full Screen");
                        onProgressDialogCancel();
                        lastFullScreenSet = now;
                    }
                }
            });
        }
    }

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {

    }

    protected boolean setTextViewById(int id, String text) {
        View view = findViewById(id);
        if (null == view || !(view instanceof TextView)) {
            return false;
        }

        ((TextView) view).setText(text);

        return true;
    }

    public boolean showViewById(int id) {
        return setVisibilityById(id, View.VISIBLE);
    }

    public boolean disappearViewById(int id) {
        return setVisibilityById(id, View.GONE);
    }

    public boolean hideViewById(int id) {
        return setVisibilityById(id, View.INVISIBLE);
    }

    private boolean setVisibilityById(int id, int visibility) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }
        view.setVisibility(visibility);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    processExternalPayment(PaymentExecution.clover, data);
                } else {
                    processExternalPaymentFailure(PaymentExecution.clover, resultCode);
                }

                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected void processExternalPayment(PaymentExecution paymentExecution, Intent data) {
        throw new NotImplementedException("Process external payment has not been implemented by " + getClass().getName());
    }

    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {

    }

    protected WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            View home = findViewById(R.id.btnHome);
            if (home != null) {
                home.setEnabled(true);
            }
            getAppAuthorizationHelper().setUser(null);
            finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            View home = findViewById(R.id.btnHome);
            if (home != null) {
                home.setEnabled(true);
            }
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    protected void goToHome(TransitionDTO logOutDto) {
        getWorkflowServiceHelper().execute(logOutDto, homeCall);
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }

    /**
     * @param transition   the transition (url) for calling the service
     * @param languageCode the language code (eg. "en", "es")
     * @param headers      any additional header
     */
    public void changeLanguage(TransitionDTO transition, String languageCode, Map<String, String> headers) {
        changeLanguage(transition, languageCode, headers, null);
    }

    /**
     * @param transition   the transition (url) for calling the service
     * @param languageCode the language code (eg. "en", "es")
     * @param headers      any additional header
     * @param callback     an additional callback
     */
    public void changeLanguage(TransitionDTO transition, String languageCode, Map<String, String> headers,
                                  SimpleCallback callback) {
        Map<String, String> query = new HashMap<>();
        query.put("language", languageCode);
        getWorkflowServiceHelper().execute(transition, getLanguageCallback(callback), null, query, headers);
    }

    @Override
    protected void onProgressDialogCancel(){
        setSystemUiVisibility();
        setNavigationBarVisibility();
    }

    private WorkflowServiceCallback getLanguageCallback(final SimpleCallback callback) {

        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                if (workflowDTO.getPayload().has("language_metadata")) {
                    String prefix = CarePayConstants.PATIENT_MODE_LABELS_PREFIX;
                    if (!getApplicationMode().getApplicationType()
                            .equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)) {
                        getWorkflowServiceHelper().saveLabels(workflowDTO.getPayload()
                                .getAsJsonObject("language_metadata").getAsJsonObject("metadata")
                                .getAsJsonObject("labels"));
                        getWorkflowServiceHelper().saveLabels(workflowDTO.getPayload()
                                .getAsJsonObject("language_metadata").getAsJsonObject("metadata")
                                .getAsJsonObject("labels"), prefix);
                    } else {
                        getWorkflowServiceHelper().saveLabels(workflowDTO.getPayload()
                                .getAsJsonObject("language_metadata").getAsJsonObject("metadata")
                                .getAsJsonObject("labels"), prefix);
                    }
                    getApplicationPreferences().setUserLanguage(workflowDTO.getPayload()
                            .getAsJsonObject("language_metadata").get("code").getAsString());
                }
                if (callback != null) {
                    callback.callback();
                    hideProgressDialog();
                } else {
                    recreate();
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    public interface SimpleCallback {
        void callback();
    }
}
