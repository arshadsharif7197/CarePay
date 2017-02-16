package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by cocampo on 2/6/17.
 */

public abstract class BaseDialogFragment extends DialogFragment {
    private Dialog dialog;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        dialog = getDialog();
        if(dialog!=null){
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public void setOnDismissListener(Dialog.OnDismissListener dismissListener){
        if(dialog!=null)
            dialog.setOnDismissListener(dismissListener);
    }

    public void setOnCancelListener(Dialog.OnCancelListener cancelListener){
        if(dialog!=null)
            dialog.setOnCancelListener(cancelListener);
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

    public String getTextForLabel(String text){
        if(StringUtil.isNullOrEmpty(text))
            return CarePayConstants.NOT_DEFINED;
        return text;
    }
}
