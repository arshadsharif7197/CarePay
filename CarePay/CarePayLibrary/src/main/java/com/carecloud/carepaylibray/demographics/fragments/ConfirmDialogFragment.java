package com.carecloud.carepaylibray.demographics.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * @author pjohnson
 */
public class ConfirmDialogFragment extends BaseDialogFragment {


    private ConfirmationCallback callback;
    private boolean isNegativeAction;
    private boolean isTitleRequired;

    public ConfirmDialogFragment() {
        // Required empty public constructor
    }

    /**
     * @param title   the dialog title
     * @param message the dialog message
     * @return a new instance
     */
    public static ConfirmDialogFragment newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(String title,
                                                    String message,
                                                    String negativeButtonLabel,
                                                    String positiveButtonLabel) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("negativeButtonLabel", negativeButtonLabel);
        args.putString("positiveButtonLabel", positiveButtonLabel);
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(String title,
                                                    String message,
                                                    String positiveButtonLabel,
                                                    boolean userCancelable,
                                                    int customLayout) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("positiveButtonLabel", positiveButtonLabel);
        args.putBoolean("userCancelable", userCancelable);
        args.putInt("customLayout", customLayout);
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layout = getArguments().getInt("customLayout", 0);
        return inflater.inflate(layout != 0 ? layout : R.layout.fragment_home_alert_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        if (!StringUtil.isNullOrEmpty(title)) {
            ((TextView) view.findViewById(R.id.dialogTitleTextView)).setText(title);
        }
        if (!isTitleRequired) {
            view.findViewById(R.id.dialogTitleTextView).setVisibility(View.GONE);
        }

        String message = getArguments().getString("message");
        if (!StringUtil.isNullOrEmpty(message)) {
            ((TextView) view.findViewById(R.id.dialogMessageTextView)).setText(message);
        }
        Button yesButton = view.findViewById(R.id.button_right_action);
        String positiveButtonLabel = getArguments().getString("positiveButtonLabel");
        if (positiveButtonLabel != null) {
            yesButton.setText(positiveButtonLabel);
        }
        yesButton.setOnClickListener(view1 -> {
            dismiss();
            callback.onConfirm();
        });
        Button noButton = view.findViewById(R.id.button_left_action);
        String negativeButtonLabel = getArguments().getString("negativeButtonLabel");
        if (negativeButtonLabel != null) {
            noButton.setText(negativeButtonLabel);
        }
        if (noButton != null) {
            noButton.setOnClickListener(view12 -> cancel());
            if (isNegativeAction) {
                noButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_blue_border));
                noButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }
        }
        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            closeView.setOnClickListener(view13 -> cancel());
        }

        if (isNegativeAction) {
            yesButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_rounded_red_background));
            yesButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        boolean userCancelable = getArguments().getBoolean("userCancelable", true);
        if (!userCancelable) {
            if (noButton != null) {
                noButton.setVisibility(View.GONE);
            }
            if (closeView != null) {
                closeView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setCallback(ConfirmationCallback homeAlertInterface) {
        this.callback = homeAlertInterface;
    }

    public boolean isNegativeAction() {
        return isNegativeAction;
    }

    public void setNegativeAction(boolean negativeAction) {
        isNegativeAction = negativeAction;
    }

    public boolean isTitleRequired() {
        return isTitleRequired;
    }

    public void setTitleRequired(boolean titleRequired) {
        isTitleRequired = titleRequired;
    }
}
