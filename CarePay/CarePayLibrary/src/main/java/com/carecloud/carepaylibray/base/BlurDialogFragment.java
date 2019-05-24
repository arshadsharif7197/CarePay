package com.carecloud.carepaylibray.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

/**
 * @author pjohnson on 2019-05-22.
 */
public abstract class BlurDialogFragment extends DialogFragment {
    public static final float DOWN_SCALE_FACTOR = 16.0F;
    public static final int BLUR_RADIUS = 8;

    private BlurDialogEngine mBlurEngine = null;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
//        setUpBlur();
    }

    private void setUpBlur() {
        mBlurEngine = new BlurDialogEngine(getActivity());
        mBlurEngine.setBlurRadius(BLUR_RADIUS);
        mBlurEngine.setDownScaleFactor(DOWN_SCALE_FACTOR);
        mBlurEngine.debug(false);
        mBlurEngine.setBlurActionBar(false);
        mBlurEngine.setUseRenderScript(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBlurEngine != null) {
            mBlurEngine.onResume(getRetainInstance());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mBlurEngine != null) {
            mBlurEngine.onDismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mBlurEngine != null) {
            mBlurEngine.onDetach();
        }
    }
}
