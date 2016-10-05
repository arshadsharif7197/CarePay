package com.carecloud.carepaylibray.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;
/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentMethodFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{

    private RadioButton creditCardRadioButton, cashRadioButton, checkRadioButton, paypalRadioButton, applePayRadioButton;
    private RadioGroup paymentMethodRadioGroup;
    private Button paymentChoiceButton, makePartialPaymentButton;
    private Activity mActivity;

    public PaymentMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        mActivity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(R.string.payment_method);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initilizeViews(view);
        return view;
    }

    private void initilizeViews(View view) {
        paymentMethodRadioGroup = (RadioGroup)view.findViewById(R.id.paymentMethodsRadioGroup);
        creditCardRadioButton = (RadioButton)view.findViewById(R.id.creditCardRadioButton);
        cashRadioButton = (RadioButton)view.findViewById(R.id.cashRadioButton);
        checkRadioButton = (RadioButton)view.findViewById(R.id.checkRadioButton);
        paypalRadioButton = (RadioButton)view.findViewById(R.id.paypalRadioButton);
        applePayRadioButton = (RadioButton)view.findViewById(R.id.applePayRadioButton);
        paymentChoiceButton = (Button)view.findViewById(R.id.paymentChoiceButton);
        makePartialPaymentButton = (Button)view.findViewById(R.id.makePartialPaymentButton);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        makePartialPaymentButton.setOnClickListener(makePartialPaymentButtonListener);
        paymentChoiceButton.setEnabled(false);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        onSetRadioButtonSemiBoldTypeFace((RadioButton) group.findViewById(checkedId));

        if(checkedId == R.id.creditCardRadioButton){
            paymentChoiceButton.setText(R.string.choose_credit_card);

        } else if(checkedId == R.id.cashRadioButton){
            paymentChoiceButton.setText(R.string.cash);

        } else if(checkedId == R.id.checkRadioButton){
            paymentChoiceButton.setText(R.string.scan_check);

        } else if(checkedId == R.id.paypalRadioButton){
            paymentChoiceButton.setText(R.string.pay_using_paypal);

        } else if(checkedId == R.id.applePayRadioButton){
            paymentChoiceButton.setText(R.string.pay_using_apple_pay);

        }
    }

    private void onSetRadioButtonRegularTypeFace(){
        SystemUtil.setProximaNovaRegularTypeface(this.mActivity,creditCardRadioButton);
        SystemUtil.setProximaNovaRegularTypeface(this.mActivity,cashRadioButton);
        SystemUtil.setProximaNovaRegularTypeface(this.mActivity,checkRadioButton);
        SystemUtil.setProximaNovaRegularTypeface(this.mActivity,paypalRadioButton);
        SystemUtil.setProximaNovaRegularTypeface(this.mActivity,applePayRadioButton);
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton){
        SystemUtil.setProximaNovaSemiboldTypeface(this.mActivity,radioButton);
    }

    private View.OnClickListener makePartialPaymentButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener paymentChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}