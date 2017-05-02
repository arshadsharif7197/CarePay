package com.carecloud.carepay.patient.payment.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.adapters.PaymentsSectionsPagerAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by jorge on 29/12/16.
 */
public class PaymentBalanceHistoryFragment extends BaseFragment {

    /**
     *
     * @return a new instance of PaymentBalanceHistoryFragment
     */
    public static PaymentBalanceHistoryFragment newInstance() {
        PaymentBalanceHistoryFragment fragment = new PaymentBalanceHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_balance_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager(view);
    }

    private void setupViewPager(View balanceHistoryView) {
        ViewPager viewPager = (ViewPager) balanceHistoryView.findViewById(R.id.historyPager);

        TabLayout tabs = (TabLayout) balanceHistoryView.findViewById(R.id.balance_history_tabs);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
        tabs.setTabTextColors(Color.LTGRAY, Color.WHITE);
        tabs.setupWithViewPager(viewPager);

        if (ViewPaymentBalanceHistoryActivity.isPaymentDone()) {
            TabLayout.Tab tab = tabs.getTabAt(1);
            if (tab != null) {
                tab.select();
            }
            ViewPaymentBalanceHistoryActivity.setIsPaymentDone(false);
        }
        PaymentsSectionsPagerAdapter adapter = new PaymentsSectionsPagerAdapter(getChildFragmentManager());
        PatientPaymentHistoryFragment pendingPaymentsFragment = PatientPaymentHistoryFragment.newInstance(PatientPaymentHistoryFragment.SECTION_PENDING);
        PatientPaymentHistoryFragment paymentHistoryFragment = PatientPaymentHistoryFragment.newInstance(PatientPaymentHistoryFragment.SECTION_HISTORY);
        pendingPaymentsFragment.setTargetFragment(this, 1);
        paymentHistoryFragment.setTargetFragment(this, 1);

        String pendingTabTitle = Label.getLabel("payment_patient_balance_tab");
        String historyTabTitle = Label.getLabel("payment_patient_history_tab");

        adapter.addFragment(pendingPaymentsFragment, StringUtil.isNullOrEmpty(pendingTabTitle) ?
                CarePayConstants.NOT_DEFINED : pendingTabTitle);
        adapter.addFragment(paymentHistoryFragment, StringUtil.isNullOrEmpty(historyTabTitle) ?
                CarePayConstants.NOT_DEFINED : historyTabTitle);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
            ((ViewPaymentBalanceHistoryActivity) this.getActivity()).displayToolbar(true, null);
        }
    }

}
