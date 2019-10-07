package com.carecloud.carepay.patient.payment.fragments;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.adapters.PaymentsSectionsPagerAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by jorge on 29/12/16
 */
public class PaymentBalanceHistoryFragment extends BaseFragment {
    public static final int PAGE_BALANCES = 0;
    public static final int PAGE_HISTORY = 1;
    private static final String KEY_INDEX = "index";

    int requestedPage;

    /**
     * @return a new instance of PaymentBalanceHistoryFragment
     */
    public static PaymentBalanceHistoryFragment newInstance(int displayPage) {
        PaymentBalanceHistoryFragment fragment = new PaymentBalanceHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_INDEX, displayPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        requestedPage = args.getInt(KEY_INDEX, PAGE_BALANCES);
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
        ViewPager viewPager = balanceHistoryView.findViewById(R.id.historyPager);

        TabLayout tabs = balanceHistoryView.findViewById(R.id.balance_history_tabs);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
        tabs.setTabTextColors(Color.LTGRAY, Color.WHITE);
        tabs.setupWithViewPager(viewPager);

        setShadow(tabs);

        PaymentsSectionsPagerAdapter adapter = new PaymentsSectionsPagerAdapter(getChildFragmentManager());
        PatientPendingPaymentFragment pendingPaymentsFragment = new PatientPendingPaymentFragment();
        PatientPaymentHistoryFragment paymentHistoryFragment = new PatientPaymentHistoryFragment();

        String pendingTabTitle = Label.getLabel("payment_patient_balance_tab");
        String historyTabTitle = Label.getLabel("payment_patient_history_tab");

        adapter.addFragment(pendingPaymentsFragment, pendingTabTitle);
        adapter.addFragment(paymentHistoryFragment, historyTabTitle);
        viewPager.setAdapter(adapter);

        TabLayout.Tab pending = tabs.getTabAt(0);
        if (pending != null) {
            pending.setCustomView(R.layout.page_tab_layout);
            setTabTitle(pending, pendingTabTitle);
        }
        TabLayout.Tab history = tabs.getTabAt(1);
        if (history != null) {
            history.setCustomView(R.layout.page_tab_layout);
            setTabTitle(history, historyTabTitle);
        }

        viewPager.setCurrentItem(requestedPage);
    }

    private void setShadow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(getResources().getDimension(R.dimen.respons_toolbar_elevation));
        }
    }

    private void setTabTitle(TabLayout.Tab tab, String title) {
        TextView textView = tab.getCustomView().findViewById(R.id.tab_title);
        textView.setText(title);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
            ViewPaymentBalanceHistoryActivity activity = (ViewPaymentBalanceHistoryActivity) this.getActivity();
            activity.displayToolbar(true, null);

            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setElevation(0);
            }

        }
    }

}
