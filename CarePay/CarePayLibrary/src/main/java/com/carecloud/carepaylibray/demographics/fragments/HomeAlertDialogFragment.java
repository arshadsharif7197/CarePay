package com.carecloud.carepaylibray.demographics.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * @author pjohnson
 */
public class HomeAlertDialogFragment extends BaseDialogFragment {


    private HomeAlertInterface callback;

    public HomeAlertDialogFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param title the dialog title
     * @param message the dialog message
     * @return a new instance
     */
    public static HomeAlertDialogFragment newInstance(String title, String message) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        HomeAlertDialogFragment fragment = new HomeAlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_alert_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String title = getArguments().getString("title");
        if (title != null) {
            ((TextView) view.findViewById(R.id.dialogTitleTextView)).setText(title);
        }
        String message = getArguments().getString("message");
        if (message != null) {
            ((TextView) view.findViewById(R.id.dialogMessageTextView)).setText(title);
        }
        Button yesButton = (Button) view.findViewById(R.id.button_right_action);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onAcceptExit();
            }
        });
        View noButton = view.findViewById(R.id.button_left_action);
        if (noButton != null) {
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    public void setCallback(HomeAlertInterface homeAlertInterface) {
        this.callback = homeAlertInterface;
    }

    public interface HomeAlertInterface {
        void onAcceptExit();
    }
}
