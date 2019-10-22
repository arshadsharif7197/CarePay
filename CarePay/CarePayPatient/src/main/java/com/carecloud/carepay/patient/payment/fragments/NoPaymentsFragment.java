package com.carecloud.carepay.patient.payment.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    /**
     * @return a new instance of NoPaymentsFragment
     */
    public static NoPaymentsFragment newInstance(boolean noPermission) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("noPermission", noPermission);
        NoPaymentsFragment fragment = new NoPaymentsFragment();
        fragment.setArguments(bundle);
        return fragment;
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
        if (getArguments().getBoolean("noPermission", false)) {
            TextView titleTextView = view.findViewById(R.id.no_payment_message_title);
            titleTextView.setText(Label.getLabel("patient.delegation.delegates.permissions.label.noPermission"));
            view.findViewById(R.id.no_payment_message_desc).setVisibility(View.GONE);
        }
    }
}
