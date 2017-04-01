package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by cocampo on 2/6/17.
 */

public abstract class BaseDialogFragment extends DialogFragment implements ISession {
    private static final int FULLSCREEN_VALUE = 0x10000000;

    private Dialog dialog;
    private boolean isPracticeAppPatientMode;

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);

        isPracticeAppPatientMode = ((ISession) getActivity()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
        if (isPracticeAppPatientMode) {
            setNavigationBarVisibility();
        }
    }

    @Override
    public int getTheme(){
//        return android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth;
        return R.style.Base_Dialog_MinWidth;
    }

    /**
     * Set a listener when the dialog is dimissed. Will be ignored if fragment is not shown as a dialog
     * @param dismissListener listener
     */
    public void setOnDismissListener(Dialog.OnDismissListener dismissListener){
        if(dialog!=null) {
            dialog.setOnDismissListener(dismissListener);
        }
    }

    /**
     * Set a listener when the dialog is canceled. Will be ignored if fragment is not shown as a dialog
     * @param cancelListener listener
     */
    public void setOnCancelListener(Dialog.OnCancelListener cancelListener){
        if(dialog!=null) {
            dialog.setOnCancelListener(cancelListener);
        }
    }



    public boolean enableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    public boolean disableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    private boolean setEnabledViewById(int id, boolean enabled) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setEnabled(enabled);

        return true;
    }

    public boolean showViewById(int id) {
        return setVisibilityById(id, View.VISIBLE);
    }

    public boolean disappearViewById(int id) {
        return setVisibilityById(id, View.GONE);
    }

    private boolean setVisibilityById(int id, int visibility) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setVisibility(visibility);

        return true;
    }

    /**
     * @param id of the view to be found
     * @return the view or null if not found
     */
    public View findViewById(int id) {
        View rootView = getView();
        if (null == rootView) {
            return null;
        }

        return rootView.findViewById(id);
    }

    protected void hideKeyboardOnViewTouch(View view){
        if(isPracticeAppPatientMode){
            view.setSoundEffectsEnabled(false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                }
            });
        }

    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        return ((ISession) getActivity()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((ISession) getActivity()).getWorkflowServiceHelper();
    }

    @Override
    public AppAuthorizationHelper getAppAuthorizationHelper() {
        return ((IApplicationSession) getActivity()).getAppAuthorizationHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getActivity()).getApplicationMode();
    }

    @Override
    public void showProgressDialog() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.showProgressDialog();
        }
    }

    @Override
    public void hideProgressDialog() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.hideProgressDialog();
        }
    }

    @Override
    public void showErrorNotification(String errorMessage) {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.showErrorNotification(errorMessage);
        }
    }

    @Override
    public void hideErrorNotification() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.hideErrorNotification();
        }
    }

    protected void hideDefaultActionBar(){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    protected void showDefaultActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }
    }


    /**
     * Display Previously hidden fragment
     */
    public void showDialog(){
        if(getDialog()!=null){
            getDialog().show();
        }
    }

    /**
     * hide dialog fragment without dismissing
     */
    public void hideDialog(){
        if(getDialog()!=null){
            getDialog().hide();
        }
    }

    private void setNavigationBarVisibility() {

        View decorView = getDialog().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
    }

}