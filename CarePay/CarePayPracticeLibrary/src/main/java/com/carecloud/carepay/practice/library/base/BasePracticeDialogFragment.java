package com.carecloud.carepay.practice.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;

/**
 * Created by lmenendez on 1/25/18
 */

public abstract class BasePracticeDialogFragment extends BaseDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View closeView = view.findViewById(R.id.closeViewLayout);
        if(closeView != null) {
            closeView.setOnClickListener(this);
        }
        TextView closeText = (TextView) view.findViewById(R.id.closeTextView);
        if(closeText != null) {
            closeText.setText(getCancelString());
        }
        ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel_img);
        if(cancelImage != null) {
            cancelImage.setImageResource(getCancelImageResource());
        }

        return view;
    }

}
