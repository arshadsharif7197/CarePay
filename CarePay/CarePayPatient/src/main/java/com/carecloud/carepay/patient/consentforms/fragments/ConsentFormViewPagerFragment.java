package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormPracticeFormInterface;
import com.carecloud.carepay.patient.payment.adapters.PaymentsSectionsPagerAdapter;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;

/**
 * @author pjohnson on 2/08/18.
 */
public class ConsentFormViewPagerFragment extends BaseFragment {

    public static final int PENDING_MODE = 100;
    public static final int HISTORIC_MODE = 101;

    private ConsentFormDTO consentFormDto;
    private ConsentFormPracticeFormInterface callback;
    private int selectedPracticeIndex;

    public static ConsentFormViewPagerFragment newInstance(int selectedProviderIndex) {
        Bundle args = new Bundle();
        args.putInt("selectedPracticeIndex", selectedProviderIndex);
        ConsentFormViewPagerFragment fragment = new ConsentFormViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConsentFormPracticeFormInterface) {
            callback = (ConsentFormPracticeFormInterface) context;
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
        selectedPracticeIndex = getArguments().getInt("selectedPracticeIndex");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserFormDTO userFormDto = consentFormDto.getPayload().getUserForms().get(selectedPracticeIndex);
        setUpToolbar(view, userFormDto);
        setupViewPager(view);
    }

    protected void setUpToolbar(View view, UserFormDTO practiceForms) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        String practiceName = "";
        for (UserPracticeDTO practiceInformation : consentFormDto.getPayload().getPracticesInformation()) {
            if (practiceForms.getMetadata().getPracticeId().equals(practiceInformation.getPracticeId())) {
                practiceName = practiceInformation.getPracticeName();
            }
        }
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(practiceName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        callback.setToolbar(toolbar);
    }

    private void setupViewPager(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.consentFormsPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.consentFormsTabs);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
        tabs.setTabTextColors(Color.LTGRAY, Color.WHITE);
        tabs.setupWithViewPager(viewPager);

        setShadow(tabs);
        ConsentFormPracticeFormsFragment pendingPaymentsFragment = ConsentFormPracticeFormsFragment
                .newInstance(selectedPracticeIndex, PENDING_MODE);
        ConsentFormPracticeFormsFragment paymentHistoryFragment = ConsentFormPracticeFormsFragment
                .newInstance(selectedPracticeIndex, HISTORIC_MODE);

        String pendingTabTitle = Label.getLabel("payment_patient_balance_tab");
        String historyTabTitle = Label.getLabel("payment_patient_history_tab");

        PaymentsSectionsPagerAdapter adapter = new PaymentsSectionsPagerAdapter(getChildFragmentManager());
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
    }

    private void setShadow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(getResources().getDimension(R.dimen.respons_toolbar_elevation));
        }
    }

    private void setTabTitle(TabLayout.Tab tab, String title) {
        TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
        textView.setText(title);
    }
}
