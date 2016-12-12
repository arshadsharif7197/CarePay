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
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPayloadCreditCardTypesDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCreditCardFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup chooseCreditCardRadioGroup;
    private Button nextButton;
    private Activity activity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;

    private PaymentsModel paymentsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String titleLabel = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            titleLabel = arguments.getString(CarePayConstants.PAYMENT_METHOD_BUNDLE);
            paymentsModel = (PaymentsModel) arguments.getSerializable(CarePayConstants.INTAKE_BUNDLE);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_credit_card, container, false);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(titleLabel);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        initializeViews(view);
        return view;
    }

    private RadioButton getCreditCardRadioButton(String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.payment_credit_card_button_selector, 0, R.drawable.check_box_intake, 0);
        radioButtonView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initializeViews(View view) {
        chooseCreditCardRadioGroup = (RadioGroup) view.findViewById(R.id.chooseCreditCardRadioGroup);
        chooseCreditCardRadioGroup.setOnCheckedChangeListener(this);

        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setVisibility(View.INVISIBLE);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        if (paymentsModel != null) {
            PaymentsPatientsCreditCardsPayloadDTO patientCreditCards = paymentsModel.getPaymentPayload().getPatientCreditCards();

            if (patientCreditCards != null) {
                List<PaymentCreditCardsPayloadDTO> creditCardList = patientCreditCards.getPayload();

                for (int i = 0; i < creditCardList.size(); i++) {
                    PaymentCreditCardsPayloadDTO creditCardItem = creditCardList.get(i);
                    String creditCardName = getCreditCardName(creditCardItem.getCardType());
                    chooseCreditCardRadioGroup.addView(getCreditCardRadioButton(
                            StringUtil.getEncodedCardNumber(creditCardName,
                                    creditCardItem.getCardNumber()), i), radioGroupLayoutParam);

                    View dividerLineView = new View(activity);
                    dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                    ));

                    dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
                    chooseCreditCardRadioGroup.addView(dividerLineView);
                    onSetRadioButtonRegularTypeFace();
                }
            }

            PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            nextButton.setText(paymentsLabel.getPaymentNextButton());
            addNewCardButton.setText(paymentsLabel.getPaymentAddNewCreditCardButton());
        }
    }

    private String getCreditCardName(String cardType) {
        if (paymentsModel != null) {
            List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType
                    = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getCreditCardType();

            if (creditCardType != null && creditCardType.size() > 0) {
                for (int i = 0; i < creditCardType.size(); i++) {
                    PaymentsSettingsPayloadCreditCardTypesDTO creditCardTypesDTO = creditCardType.get(i);
                    if (creditCardTypesDTO.getType().equalsIgnoreCase(cardType)) {
                        return creditCardTypesDTO.getLabel();
                    }
                }
            }
        }
        return cardType;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        nextButton.setVisibility(View.VISIBLE);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < chooseCreditCardRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.activity, (RadioButton)
                        chooseCreditCardRadioGroup.getChildAt(i));
                ((RadioButton) chooseCreditCardRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.blue_cerulian));
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // ToDo : Make payment using selected Credit Card action here - to be done
        }
    };

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
            AddNewCreditCardFragment fragment = (AddNewCreditCardFragment)
                fragmentmanager.findFragmentByTag(AddNewCreditCardFragment.class.getSimpleName());
            if (fragment == null) {
                fragment = new AddNewCreditCardFragment();
            }
            Bundle args = new Bundle();
            args.putSerializable(CarePayConstants.INTAKE_BUNDLE,
                    paymentsModel);
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
            fragmentTransaction.addToBackStack(AddNewCreditCardFragment.class.getSimpleName());
            fragmentTransaction.commit();
        }
    };
}
