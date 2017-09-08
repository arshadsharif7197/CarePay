package com.carecloud.carepay.practice.library.checkin.dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * @author pjohnson
 */
public class HomeAlertDialogFragment extends BaseDialogFragment {


    private HomeAlertInterface callback;

    public HomeAlertDialogFragment() {
        // Required empty public constructor
    }

    public static HomeAlertDialogFragment newInstance() {
        return new HomeAlertDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_alert_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button yesButton = (Button) view.findViewById(R.id.button_right_action);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onAcceptExit();
            }
        });
        Button noButton = (Button) view.findViewById(R.id.button_left_action);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setCallback(HomeAlertInterface homeAlertInterface) {
        this.callback = homeAlertInterface;
    }

    public interface HomeAlertInterface {
        void onAcceptExit();
    }
}
