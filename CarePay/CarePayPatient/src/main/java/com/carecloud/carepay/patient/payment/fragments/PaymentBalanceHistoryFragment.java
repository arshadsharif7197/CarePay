package com.carecloud.carepay.patient.payment.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.patient.payment.interfaces.PaymentPatientInterface;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.adapters.PaymentsSectionsPagerAdapter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

/**
 * Created by jorge on 29/12/16.
 */
public class PaymentBalanceHistoryFragment extends BaseFragment implements PaymentPatientInterface {

    private View noPaymentsLayout;
    private View pagerLayout;
    int sectionNumber = -1;
    private PaymentNavigationCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View balanceHistoryView = inflater.inflate(R.layout.fragment_payment_balance_history, container, false);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String paymentsDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
        PaymentsModel paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);

        noPaymentsLayout = balanceHistoryView.findViewById(R.id.no_payment_layout);
        pagerLayout = balanceHistoryView.findViewById(R.id.payments_pager_layout);
        TextView noPaymentTitle = (TextView) balanceHistoryView.findViewById(R.id.no_payment_message_title);
        TextView noPaymentDesc = (TextView) balanceHistoryView.findViewById(R.id.no_payment_message_desc);

        noPaymentTitle.setText(Label.getLabel("no_payment_title"));
        noPaymentDesc.setText(Label.getLabel("no_payment_description"));

        setupViewPager(balanceHistoryView, paymentDTO);
        hideNoPaymentsLayout();

        return balanceHistoryView;
    }

    private void setupViewPager(View balanceHistoryView, PaymentsModel paymentDTO) {
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
        PatientPaymentHistoryFragment pendingPaymentsFragment = PatientPaymentHistoryFragment.newInstance(PatientPaymentHistoryFragment.SECTION_PENDING, paymentDTO);
        PatientPaymentHistoryFragment paymentHistoryFragment = PatientPaymentHistoryFragment.newInstance(PatientPaymentHistoryFragment.SECTION_HISTORY, paymentDTO);
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

    private void showNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.VISIBLE);
        pagerLayout.setVisibility(View.GONE);

    }

    private void hideNoPaymentsLayout() {
        noPaymentsLayout.setVisibility(View.GONE);
        pagerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoPaymentsLayout(int sectionNumber) {
        if (this.sectionNumber < 0) {//not yet set
            this.sectionNumber = sectionNumber;
        } else if (this.sectionNumber != sectionNumber) {//this is the second one so we should hide the pager
            showNoPaymentsLayout();
        }
    }

    @Override
    public void loadPaymentAmountScreen(PendingBalancePayloadDTO model, PaymentsModel paymentDTO) {
        String tag = PaymentDetailsFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentDTO, model);
        dialog.show(ft, tag);
    }

}
