package com.carecloud.carepay.patient.demographics.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.ConfirmationCallback;

/**
 * Created by lmenendez on 10/9/17
 */
public class ConfirmDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private ConfirmationCallback callback;

    public static ConfirmDialogFragment newInstance(String title, String subTitle,
                                                    String negativeButtonLabel, String positiveButtonLabel) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("subTitle", subTitle);
        args.putString("negativeButtonLabel", negativeButtonLabel);
        args.putString("positiveButtonLabel", positiveButtonLabel);
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance() {
        return new ConfirmDialogFragment();
    }

    public void setCallback(ConfirmationCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_confirm_exit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        View close = view.findViewById(R.id.dialogCloseHeaderImageView);
        close.setOnClickListener(this);

        Bundle args = getArguments() != null ? getArguments() : new Bundle();

        Button cancel = (Button) view.findViewById(R.id.button_no);
        if (args.getString("negativeButtonLabel") != null) {
            cancel.setText(args.getString("negativeButtonLabel"));
        }
        cancel.setOnClickListener(this);

        Button proceed = (Button) view.findViewById(R.id.button_yes);
        if (args.getString("positiveButtonLabel") != null) {
            proceed.setText(args.getString("positiveButtonLabel"));
        }

        TextView titleTextView = (TextView) view.findViewById(R.id.confirm_exit_title);
        if (args.getString("title") != null) {
            titleTextView.setText(args.getString("title"));
        }

        TextView subTitleTextView = (TextView) view.findViewById(R.id.confirm_exit_message);
        if (args.getString("subTitle") != null) {
            subTitleTextView.setText(args.getString("subTitle"));
        }

        proceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if (view.getId() == R.id.button_yes) {
            if (callback != null) {
                callback.onConfirm();
            }
        }
    }
}
