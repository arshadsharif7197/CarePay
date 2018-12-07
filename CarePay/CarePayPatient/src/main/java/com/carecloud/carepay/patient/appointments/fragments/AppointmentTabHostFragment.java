package com.carecloud.carepay.patient.appointments.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.patient.payment.adapters.PaymentsSectionsPagerAdapter;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by jorge on 29/12/16
 */
public class AppointmentTabHostFragment extends BaseFragment {
    public static final int PAGE_CURRENT = 0;
    public static final int PAGE_HISTORY = 1;
    private static final String KEY_INDEX = "index";

    int requestedPage;
    private AppointmentViewHandler callback;

    /**
     * @return a new instance of PaymentBalanceHistoryFragment
     */
    public static AppointmentTabHostFragment newInstance(int displayPage) {
        AppointmentTabHostFragment fragment = new AppointmentTabHostFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_INDEX, displayPage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentViewHandler) {
            callback = (AppointmentViewHandler) context;
        } else {
            throw new ClassCastException("Activity must implement AppointmentViewHandler");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        requestedPage = args.getInt(KEY_INDEX, PAGE_CURRENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_tab_host, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager(view);
    }

    private void setupViewPager(View balanceHistoryView) {
        ViewPager viewPager = balanceHistoryView.findViewById(R.id.appointmentsPager);

        TabLayout tabs = balanceHistoryView.findViewById(R.id.appointmentTabs);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
        tabs.setTabTextColors(Color.LTGRAY, Color.WHITE);
        tabs.setupWithViewPager(viewPager);

        setShadow(tabs);

        PaymentsSectionsPagerAdapter adapter = new PaymentsSectionsPagerAdapter(getChildFragmentManager());
        AppointmentsListFragment pendingPaymentsFragment = AppointmentsListFragment
                .newInstance(((PatientAppointmentPresenter) callback.getAppointmentPresenter())
                        .getMainAppointmentDto());
        AppointmentHistoryFragment paymentHistoryFragment = AppointmentHistoryFragment.newInstance();

        String currentTabTitle = Label.getLabel("appointments.list.tab.title.current");
        String historyTabTitle = Label.getLabel("appointments.list.tab.title.history");

        adapter.addFragment(pendingPaymentsFragment, currentTabTitle);
        adapter.addFragment(paymentHistoryFragment, historyTabTitle);
        viewPager.setAdapter(adapter);

        TabLayout.Tab currentTab = tabs.getTabAt(0);
        if (currentTab != null) {
            currentTab.setCustomView(R.layout.page_tab_layout);
            setTabTitle(currentTab, currentTabTitle);
        }
        TabLayout.Tab historyTab = tabs.getTabAt(1);
        if (historyTab != null) {
            historyTab.setCustomView(R.layout.page_tab_layout);
            setTabTitle(historyTab, historyTabTitle);
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
        if (getActivity() instanceof AppointmentsActivity) {
            AppointmentsActivity activity = (AppointmentsActivity) this.getActivity();
            activity.displayToolbar(true, null);

            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setElevation(0);
            }

        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

}
