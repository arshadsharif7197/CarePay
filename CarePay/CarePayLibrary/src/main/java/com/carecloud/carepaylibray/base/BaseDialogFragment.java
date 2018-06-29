package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by cocampo on 2/6/17
 */

public abstract class BaseDialogFragment extends DialogFragment implements ISession {
    private static final int FULLSCREEN_VALUE = 0x10000000;

    private Dialog dialog;
    private boolean isPracticeAppPatientMode;
    private boolean isPracticeAppPracticeMode;

    private DialogInterface.OnDismissListener onDismissListener;
    private DialogInterface.OnCancelListener onCancelListener;

    private long lastFullScreenSet;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        ApplicationMode.ApplicationType applicationType = ((ISession) getActivity()).getApplicationMode().getApplicationType();
        isPracticeAppPatientMode = applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
        isPracticeAppPracticeMode = applicationType == ApplicationMode.ApplicationType.PRACTICE;
        setNewRelicInteraction(getClass().getName());
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);

        if (isPracticeAppPatientMode) {
            setNavigationBarVisibility();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null) {
            View decorView = dialog.getWindow().getDecorView();
            hideKeyboardOnViewTouch(decorView);
            if(isPracticeAppPatientMode){
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        long now = System.currentTimeMillis();
                        if(now - lastFullScreenSet > 1000) {
                            Log.d("Base", "Hide Nav Bar");
                            setNavigationBarVisibility();
                            lastFullScreenSet = now;
                        }
                    }
                });
            }
        }
        setLastInteraction(System.currentTimeMillis());
    }

    @Override
    public int getTheme() {
        return R.style.Base_Dialog_MinWidth;
    }

    /**
     * Set a listener when the dialog is dimissed. Will be ignored if fragment is not shown as a dialog
     *
     * @param dismissListener listener
     */
    public void setOnDismissListener(Dialog.OnDismissListener dismissListener) {
        this.onDismissListener = dismissListener;
    }

    /**
     * Set a listener when the dialog is canceled. Will be ignored if fragment is not shown as a dialog
     *
     * @param cancelListener listener
     */
    public void setOnCancelListener(Dialog.OnCancelListener cancelListener) {
        this.onCancelListener = cancelListener;
    }

    public void cancel(){
        if (onCancelListener != null && getDialog() != null) {
            onCancelListener.onCancel(getDialog());
        }
        dismiss();
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

    protected void hideKeyboardOnViewTouch(View view) {
        if ((isPracticeAppPatientMode || isPracticeAppPracticeMode) && view != null) {
            view.setSoundEffectsEnabled(false);
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        SystemUtil.hideSoftKeyboard(getContext(), view);
                    }
                    return false;
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
    public void onAtomicRestart() {
        ((IApplicationSession) getActivity()).onAtomicRestart();
    }

    @Override
    public void setLastInteraction(long systemTime) {
        ((IApplicationSession) getActivity()).setLastInteraction(systemTime);
    }

    @Override
    public long getLastInteraction() {
        return ((IApplicationSession) getActivity()).getLastInteraction();
    }

    @Override
    public void setNewRelicInteraction(String interactionName) {
        ((IApplicationSession) getActivity()).setNewRelicInteraction(interactionName);
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
        if (getDialog() != null) {
            CustomMessageToast toast = new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR);
            toast.show();
        } else {
            ISession session = (ISession) getActivity();
            if (null != session) {
                session.showErrorNotification(errorMessage);
            }
        }
    }

    @Override
    public void hideErrorNotification() {
        ISession session = (ISession) getActivity();
        if (null != session) {
            session.hideErrorNotification();
        }
    }

    /**
     * Display Previously hidden fragment
     */
    public void showDialog() {
        if (getDialog() != null) {
            getDialog().show();
        }
    }

    /**
     * hide dialog fragment without dismissing
     */
    public void hideDialog() {
        if (getDialog() != null) {
            getDialog().hide();
        }
    }

    @Override
    public void setNavigationBarVisibility() {
        if(getDialog().getWindow() != null) {
            View decorView = getDialog().getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | FULLSCREEN_VALUE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
