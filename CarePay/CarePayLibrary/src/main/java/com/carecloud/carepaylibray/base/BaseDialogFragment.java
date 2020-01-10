package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

public abstract class BaseDialogFragment extends BlurDialogFragment implements ISession {

    private static final int FULLSCREEN_VALUE = 0x10000000;
    private Dialog dialog;
    private boolean isPracticeAppPatientMode;
    private boolean isPracticeAppPracticeMode;

    private DialogInterface.OnDismissListener onDismissListener;
    protected DialogInterface.OnCancelListener onCancelListener;
    protected OnBackPressedInterface onBackPressedInterface;

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
        super.setupDialog(dialog, style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog = getDialog();
        setCancelable(false);

        if (isPracticeAppPatientMode) {
            setNavigationBarVisibility();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null) {
            View decorView = dialog.getWindow().getDecorView();
            hideKeyboardOnViewTouch(decorView);
            if (isPracticeAppPatientMode) {
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    long now = System.currentTimeMillis();
                    if (now - lastFullScreenSet > 1000) {
                        Log.d("Base", "Hide Nav Bar");
                        setNavigationBarVisibility();
                        lastFullScreenSet = now;
                    }
                });
            }
        }
        setLastInteraction(System.currentTimeMillis());
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
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

    public void cancel() {
        if (onCancelListener != null && getDialog() != null) {
            onCancelListener.onCancel(getDialog());
        }
        dismiss();
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
            view.setOnTouchListener((view1, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    SystemUtil.hideSoftKeyboard(getContext(), view1);
                }
                return false;
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

    public void showDialog(boolean showBlur) {
        if (getDialog() != null) {
            getDialog().show();
        }
        super.showDialog(showBlur);
    }

    /**
     * hide dialog fragment without dismissing
     */
    public void hideDialog() {
        hideDialog(false);
    }

    public void hideDialog(boolean hideBlur) {
        if (getDialog() != null) {
            getDialog().hide();
            super.hideDialog(hideBlur);
        }
    }

    @Override
    public void setNavigationBarVisibility() {
        if (getDialog().getWindow() != null) {
            View decorView = getDialog().getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | FULLSCREEN_VALUE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected DialogInterface.OnCancelListener onDialogCancelListener = dialogInterface -> showDialog();

    public void onBackPressed() {
        getActivity().onBackPressed();
        if (onBackPressedInterface != null) {
            onBackPressedInterface.onBackPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedInterface onBackPressedInterface) {
        this.onBackPressedInterface = onBackPressedInterface;
    }

    public interface OnBackPressedInterface {
        void onBackPressed();
    }
}
