package com.carecloud.carepaylibray.customdialogs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

public abstract class BaseDialogFragment extends DialogFragment implements View.OnClickListener {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_dialog_fragment, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setCancelable(getCancelable());

        view.findViewById(R.id.closeViewLayout).setOnClickListener(this);
        ((CarePayTextView) view.findViewById(R.id.closeTextView)).setText(getCancelString());
        ((ImageView) view.findViewById(R.id.cancel_img)).setImageResource(getCancelImageResource());

        // Set content
        View contentView = inflater.inflate(getContentLayout(), null);
        ((FrameLayout) view.findViewById(R.id.base_dialog_content_layout)).addView(contentView);

        return view;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (R.id.closeViewLayout == viewId) {
            onDialogCancel();
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

    protected TextView setTextViewById(int id, String text) {
        View view = findViewById(id);
        if (null == view || !(view instanceof TextView)) {
            return null;
        }

        TextView textView = (TextView) view;
        textView.setText(text);

        return textView;
    }

    private View findViewById(int id) {
        return view.findViewById(id);
    }
}
