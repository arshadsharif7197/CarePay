package com.carecloud.carepay.practice.library.payments.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;


public class PaymentTermsFragment extends BaseFragment {

    private TextView termsContentTextView;
    private Button agreeToPayButton;
    private ScrollView responsibilityTermsScrollView;
    private PaymentsModel paymentsModel;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            paymentsModel = (PaymentsModel) arguments.getSerializable(CarePayConstants.INTAKE_BUNDLE);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_responsibility_terms, container, false);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentTerms());
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        termsContentTextView = (TextView) view.findViewById(R.id.terms_content_textView);
        termsContentTextView.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentAgreeToPayTerms());
        responsibilityTermsScrollView = (ScrollView) view.findViewById(R.id.responsibility_terms_scrollView);
        agreeToPayButton = (Button) view.findViewById(R.id.agreeToPayButton);
        agreeToPayButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentAgreeAndPay());
        agreeToPayButton.setEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // enable next button on scrolling all the way to the bottom
        setEnableNextButtonOnFullScroll();
    }

    private void setEnableNextButtonOnFullScroll() {
        // enable next button on scrolling all the way to the bottom
        if (responsibilityTermsScrollView.getScrollY() == 0) {
            agreeToPayButton.setEnabled(true);
        } else {
            responsibilityTermsScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    View view = responsibilityTermsScrollView.getChildAt(responsibilityTermsScrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (responsibilityTermsScrollView.getHeight() + responsibilityTermsScrollView.getScrollY()));

                    if (diff == 0) {
                        agreeToPayButton.setEnabled(true);
                    }
                }
            });

        }
    }
}
