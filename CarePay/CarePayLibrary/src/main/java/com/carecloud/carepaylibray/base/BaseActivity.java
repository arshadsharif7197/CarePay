package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private Dialog progressDialog;
    private CustomPopupNotification errorNotification;
    private boolean isVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.setFactory2(new CarePayLayoutInflaterFactory(this));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppAuthorizationHelper();
        isVisible = true;
        final View rootView = findViewById(android.R.id.content);
        rootView.setSoundEffectsEnabled(false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(BaseActivity.this);
            }
        });

    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        return ((IApplicationSession) getApplication()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((IApplicationSession) getApplication()).getWorkflowServiceHelper();
    }

    public AppAuthorizationHelper getAppAuthorizationHelper() {
        return ((IApplicationSession) getApplication()).getAppAuthorizationHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getApplication()).getApplicationMode();
    }

    public Context getContext() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        if (!isVisible()) {
            return;
        }

        if (null != progressDialog && progressDialog.isShowing()) {
            return;
        }

        if (null == progressDialog) {
            boolean isPracticeAppPatientMode = getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
            progressDialog = new ProgressDialogUtil(isPracticeAppPatientMode, this);
        }

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void showErrorNotification(String errorMessage) {
        if (!isVisible()) {
            return;
        }

        if (null != errorNotification && errorNotification.isShowing()) {
            return;
        }
        try {
            if (null == errorNotification) {

                if(!StringUtil.isNullOrEmpty(errorMessage)){
                    errorNotification = new CustomPopupNotification(getContext(), getCurrentFocus(), getWindow(), errorMessage, CustomPopupNotification.TYPE_ERROR_NOTIFICATION, errorNotificationSwipeListener());
                }else{
                    errorNotification = new CustomPopupNotification(getContext(), getCurrentFocus(), getWindow(), CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE, CustomPopupNotification.TYPE_ERROR_NOTIFICATION, errorNotificationSwipeListener());
                }

            }
            errorNotification.showPopWindow();
        } catch (Exception e) {
                Log.e("Base Activity", e.getMessage());
        }
    }

    /**
     * Gets cancel reason appointment dialog listener.
     *
     * @return the cancel reason appointment dialog listener
     */
    public CustomPopupNotification.CustomPopupNotificationListener errorNotificationSwipeListener() {
        return new CustomPopupNotification.CustomPopupNotificationListener() {
            @Override
            public void onSwipe(String swipeDirection) {
                hideErrorNotification();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
            }
        };
    }

    @Override
    public void hideErrorNotification() {
        if (null != errorNotification && errorNotification.isShowing()) {
            errorNotification.dismiss();
            errorNotification = null;
        }
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public <S> S getConvertedDTO(Class<S> dtoClass) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return null;
        }

        Object rawWorkflowDTO = bundle.get(WorkflowDTO.class.getSimpleName());
        if (rawWorkflowDTO == null) {
            return null;
        }

        Gson gson = new Gson();
        try {
            long id = (Long) rawWorkflowDTO;
            WorkflowDTO workflowDTO = new WorkflowDTO(WorkFlowRecord.findById(WorkFlowRecord.class, id));

            if (dtoClass.equals(WorkflowDTO.class)) {
                return Primitives.wrap(dtoClass).cast(workflowDTO);
            }

            return gson.fromJson(workflowDTO.toString(), dtoClass);
        } catch (ClassCastException e) {
            // Object is set as String and not in DB
            return gson.fromJson(bundle.getString(WorkflowDTO.class.getSimpleName()), dtoClass);
        }
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

        if (!StringUtil.isNullOrEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, dtoClass);
        }
        return null;
    }

    public abstract void navigateToWorkflow(WorkflowDTO workflowDTO);

}
