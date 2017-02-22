package com.carecloud.carepay.patient.payment.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 29/12/16.
 */

public class PaymentBalanceHistoryFragment  extends BaseFragment {

    private View noPaymentsLayout;
    private View pagerLayout;
    private TextView noPaymentTitle;
    private TextView noPaymentDesc;
    private PaymentsModel paymentDTO;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View balanceHistoryView = inflater.inflate(R.layout.fragment_payment_balance_history, container, false);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String paymentsDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);

        noPaymentsLayout = balanceHistoryView.findViewById(R.id.no_payment_layout);
        pagerLayout = balanceHistoryView.findViewById(R.id.payments_pager_layout);
        noPaymentTitle = (TextView) balanceHistoryView.findViewById(R.id.no_payment_message_title);
        noPaymentDesc = (TextView) balanceHistoryView.findViewById(R.id.no_payment_message_desc);

        noPaymentTitle.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPaymentTitle());
        noPaymentDesc.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getNoPaymentDescription());

        setupViewPager(balanceHistoryView, paymentDTO);
        hideNoPaymentsLayout();

        return balanceHistoryView;
    }

    private void setupViewPager(View balanceHistoryView, PaymentsModel paymentDTO) {
        ViewPager viewPager = (ViewPager) balanceHistoryView.findViewById(R.id.historyPager);

        TabLayout tabs = (TabLayout) balanceHistoryView.findViewById (R.id.balance_history_tabs);
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
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        PaymentHistoryFragment pendingPaymentsFragment = PaymentHistoryFragment.newInstance(1, paymentDTO);
        PaymentHistoryFragment paymentHistoryFragment = PaymentHistoryFragment.newInstance(2, paymentDTO);

        pendingPaymentsFragment.setEmptyPaymentListCallback(emptyPaymentListCallback);
        paymentHistoryFragment.setEmptyPaymentListCallback(emptyPaymentListCallback);

        String pendingTabTitle = paymentDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentPatientBalanceTab();
        String historyTabTitle = paymentDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentPatientHistoryTab();

        adapter.addFragment(pendingPaymentsFragment, StringUtil.isNullOrEmpty(pendingTabTitle)?
                CarePayConstants.NOT_DEFINED : pendingTabTitle);
        adapter.addFragment(paymentHistoryFragment, StringUtil.isNullOrEmpty(historyTabTitle)?
                CarePayConstants.NOT_DEFINED : historyTabTitle);
        viewPager.setAdapter(adapter);

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
            ((ViewPaymentBalanceHistoryActivity)this.getActivity()).displayToolbar(true);
        }
    }

    private void showNoPaymentsLayout(){
        noPaymentsLayout.setVisibility(View.VISIBLE);
        pagerLayout.setVisibility(View.GONE);

    }

    private void hideNoPaymentsLayout(){
        noPaymentsLayout.setVisibility(View.GONE);
        pagerLayout.setVisibility(View.VISIBLE);
    }


    private PaymentHistoryFragment.EmptyPaymentListCallback emptyPaymentListCallback = new PaymentHistoryFragment.EmptyPaymentListCallback() {
        int sectionNumber = -1;
        @Override
        public void listIsEmpty(int sectionNumber) {
            if(this.sectionNumber<0) {//not yet set
                this.sectionNumber = sectionNumber;
            }else if(this.sectionNumber!=sectionNumber){//this is the second one so we should hide the pager
                showNoPaymentsLayout();
            }
        }
    };

}
