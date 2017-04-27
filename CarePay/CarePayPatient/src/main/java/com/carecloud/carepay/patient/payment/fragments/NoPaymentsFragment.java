package com.carecloud.carepay.patient.payment.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoPaymentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoPaymentsFragment extends BaseFragment {

    public NoPaymentsFragment() {
        // Required empty public constructor
    }

    public static NoPaymentsFragment newInstance() {
        NoPaymentsFragment fragment = new NoPaymentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_payments, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
