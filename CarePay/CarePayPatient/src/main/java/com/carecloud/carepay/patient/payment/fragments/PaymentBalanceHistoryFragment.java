package com.carecloud.carepay.patient.payment.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 29/12/16.
 */

public class PaymentBalanceHistoryFragment  extends Fragment  {

    private FragmentActivity context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View balanceHistoryView = inflater.inflate(R.layout.fragment_payment_balance_history, container, false);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String paymentsDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        PaymentsModel paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);

        ViewPager viewPager = (ViewPager) balanceHistoryView.findViewById(R.id.historyPager);
        setupViewPager(viewPager, paymentDTO);

        TabLayout tabs = (TabLayout) balanceHistoryView.findViewById (R.id.balance_history_tabs);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
        tabs.setupWithViewPager(viewPager);

        return balanceHistoryView;
    }

    private void setupViewPager(ViewPager viewPager, PaymentsModel paymentDTO) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(context.getSupportFragmentManager());
        adapter.addFragment(PaymentHistoryFragment.newInstance(1, paymentDTO), "PENDING");
        adapter.addFragment(PaymentHistoryFragment.newInstance(2, paymentDTO), "HISTORY");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        context=(FragmentActivity) activity;
        super.onAttach(activity);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
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
}
