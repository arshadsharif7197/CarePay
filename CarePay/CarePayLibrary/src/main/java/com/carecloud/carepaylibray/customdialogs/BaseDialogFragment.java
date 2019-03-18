package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.SystemUtil;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

public abstract class BaseDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final int FULLSCREEN_VALUE = 0x10000000;

    private Dialog dialog;
    private View view;
    private boolean isPracticeAppPatientMode;
    private BlurDialogEngine mBlurEngine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_dialog_fragment, container, false);

        isPracticeAppPatientMode = ((ISession) getActivity()).getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
        if (isPracticeAppPatientMode) {
            setNavigationBarVisibility();
        }

        setCancelable(getCancelable());

        // Set content
        View contentView = inflater.inflate(getContentLayout(), null);
        ((FrameLayout) view.findViewById(R.id.base_dialog_content_layout)).addView(contentView);

        hideKeyboardOnViewTouch(view);
        return view;
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
        setUpBlur();
    }

    private void setUpBlur() {
        mBlurEngine = new BlurDialogEngine(getActivity());
        mBlurEngine.setBlurRadius(com.carecloud.carepaylibray.base.BaseDialogFragment.BLUR_RADIUS);
        mBlurEngine.setDownScaleFactor(com.carecloud.carepaylibray.base.BaseDialogFragment.DOWN_SCALE_FACTOR);
        mBlurEngine.debug(false);
        mBlurEngine.setBlurActionBar(true);
        mBlurEngine.setUseRenderScript(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null) {
            View decorView = dialog.getWindow().getDecorView();
            hideKeyboardOnViewTouch(decorView);
        }
        if (mBlurEngine != null) {
            mBlurEngine.onResume(getRetainInstance());
        }
        ((ISession) getActivity()).setLastInteraction(System.currentTimeMillis());
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (mBlurEngine != null) {
            mBlurEngine.onDismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBlurEngine != null) {
            mBlurEngine.onDetach();
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (R.id.closeViewLayout == viewId) {
            onDialogCancel();
        }
    }

    protected void hideKeyboardOnViewTouch(View view) {
        if (isPracticeAppPatientMode && view != null) {
            view.setSoundEffectsEnabled(false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                }
            });
        }

    }

    // if caller want to change on cancel then override this method in extended class
    protected void onDialogCancel() {
        dismiss();
    }

    protected abstract String getCancelString();

    protected abstract int getCancelImageResource();

    protected abstract int getContentLayout();

    protected abstract boolean getCancelable();

    protected View findViewById(int id) {
        return view.findViewById(id);
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
