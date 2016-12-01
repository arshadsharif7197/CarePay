package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentMethodFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup paymentMethodRadioGroup;
    private Button createPaymentPlanButton;
    private Button paymentChoiceButton;
    private Activity activity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private String[] paymentMethodsArray;
    private String[] createPaymentMethodButtonCaptionArray;
    private int[] paymentMethodsDrawableArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(R.string.payment_method);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        // TODO : Change the arrays to dynamic payment methods info from APIs
        paymentMethodsArray = new String[]{getString(R.string.credit_card), getString(R.string.cash),
                getString(R.string.check), getString(R.string.paypal), getString(R.string.apple_pay)};
        createPaymentMethodButtonCaptionArray = new String[]{getString(R.string.choose_credit_card),
                getString(R.string.cash), getString(R.string.scan_check),
                getString(R.string.pay_using_paypal), getString(R.string.pay_using_apple_pay)};
        paymentMethodsDrawableArray = new int[]{R.drawable.payment_credit_card_button_selector,
             R.drawable.payment_cash_button_selector, R.drawable.payment_check_button_selector,
             R.drawable.payment_paypal_button_selector, R.drawable.payment_apple_button_selector};

        initilizeViews(view);

        return view;
    }

    private RadioButton getPaymentMethodRadioButton(String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                paymentMethodsDrawableArray[index], 0, R.drawable.check_box_intake, 0);
        radioButtonView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.radio_button_selector));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initilizeViews(View view) {
        paymentMethodRadioGroup = (RadioGroup) view.findViewById(R.id.paymentMethodsRadioGroup);
        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        paymentChoiceButton.setEnabled(false);

        for (int i = 0; i < paymentMethodsArray.length; i++) {
            paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentMethodsArray[i], i),
                    radioGroupLayoutParam);

            View dividerLineView = new View(activity);
            dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
            ));
            dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
            paymentMethodRadioGroup.addView(dividerLineView);
            onSetRadioButtonRegularTypeFace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);

        for (int i = 0; i < paymentMethodsArray.length; i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentMethodsArray[i])) {
                paymentChoiceButton.setText(createPaymentMethodButtonCaptionArray[i]);
            }
        }
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < paymentMethodRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.activity,
                        (RadioButton) paymentMethodRadioGroup.getChildAt(i));
                ((RadioButton) paymentMethodRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.blue_cerulian));
    }

    private View.OnClickListener createPaymentPlanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // ToDo : Create payment plan action here - to be done
        }
    };

    private View.OnClickListener paymentChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (paymentChoiceButton.getText().equals(getString(R.string.choose_credit_card))) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                ChooseCreditCardFragment fragment = (ChooseCreditCardFragment) fragmentmanager
                        .findFragmentByTag(ChooseCreditCardFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new ChooseCreditCardFragment();
                }

                Bundle arguments = getArguments();
                Bundle args = new Bundle();
                args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                        arguments.getSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO));
                fragment.setArguments(args);

                FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
                fragmentTransaction.addToBackStack(ChooseCreditCardFragment.class.getSimpleName());
                fragmentTransaction.commit();

            }
        }
    };
}