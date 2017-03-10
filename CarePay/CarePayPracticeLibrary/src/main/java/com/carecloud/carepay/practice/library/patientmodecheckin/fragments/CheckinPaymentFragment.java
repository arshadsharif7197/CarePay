package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinPaymentFragment extends BaseFragment {

    private Button payTotalButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_payment, container, false);

        payTotalButton = (Button) view.findViewById(R.id.checkinPaymentTotalClickable);
        payTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToHome();
            }
        });
        return view;

    }

    private void goBackToHome() {
        getActivity().finish();
    }
}
