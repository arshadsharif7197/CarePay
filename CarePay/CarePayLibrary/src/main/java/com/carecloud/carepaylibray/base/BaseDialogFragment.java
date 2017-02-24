package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by cocampo on 2/6/17.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    private Dialog dialog;


    private void initDialog(Dialog dialog){
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

        params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.88);
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, STYLE_NO_TITLE);
        this.dialog = getDialog();
        initDialog(getDialog());
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

    /**
     * Convinenece method for checking if label string is null
     * @param text label string from DTO
     * @return string if valid or undefined constant
     */
    public String getTextForLabel(String text){
        if(StringUtil.isNullOrEmpty(text)) {
            return CarePayConstants.NOT_DEFINED;
        }
        return text;
    }
}
