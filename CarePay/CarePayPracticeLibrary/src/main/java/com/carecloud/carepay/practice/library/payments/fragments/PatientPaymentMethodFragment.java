package com.carecloud.carepay.practice.library.payments.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.BaseCheckinFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends BaseCheckinFragment
        implements RadioGroup.OnCheckedChangeListener {

    private Activity activity;
    private Button paymentChoiceButton;
    private RadioGroup paymentMethodRadioGroup;
    private RadioGroup.LayoutParams radioGroupLayoutParam;

    private PaymentsModel paymentsModel;
    private List<PaymentsMethodsDTO> paymentList;

    private String dialogTitle;
    private String dialogText;
    private String selectedPaymentMethod;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

        Gson gson = new Gson();
        Bundle arguments = getArguments();
        String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
        paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);
        paymentList = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getPaymentMethods();

        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        title.setText(titlePaymentMethodString);
        toolbar.setTitle(titlePaymentMethodString);

        getLabels();
        initializeViews(view);

        return view;
    }

    private RadioButton getPaymentMethodRadioButton(String cardType, String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(android.R.color.transparent);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);

        // Initialize HashMap.
        HashMap<String, Integer> cardTypeMap = new HashMap<>();
        cardTypeMap.put(CarePayConstants.TYPE_CASH, R.drawable.payment_cash_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_CREDIT_CARD, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_CHECK, R.drawable.payment_check_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_GIFT_CARD, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_PAYPAL, R.drawable.payment_paypal_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_HSA, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_FSA, R.drawable.payment_credit_card_button_selector);

        if(cardTypeMap.get(cardType)!=null) {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    cardTypeMap.get(cardType), 0, R.drawable.check_box_intake, 0);
        } else {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    cardTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD), 0, R.drawable.check_box_intake, 0);
        }

        radioButtonView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.radio_button_selector));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initializeViews(View view) {
        paymentMethodRadioGroup = (RadioGroup) view.findViewById(R.id.paymentMethodsRadioGroup);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);

        Button createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        createPaymentPlanButton.setText(paymentCreatePlanString);

        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        paymentChoiceButton.setText(paymentChooseMethodString);
        paymentChoiceButton.setEnabled(false);

        for (int i = 0; i < paymentList.size(); i++) {
            paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentList.get(i).getType(),
                    paymentList.get(i).getLabel(), i), radioGroupLayoutParam);

            View dividerLineView = new View(activity);
            dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));

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

        for (int i = 0; i < paymentList.size(); i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentList.get(i).getLabel())) {
                selectedPaymentMethod = selectedRadioButton.getText().toString();
                paymentChoiceButton.setText(paymentList.get(i).getButtonLabel());
                paymentChoiceButton.setTag(checkedId);
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
            if (paymentsModel != null) {

                double previousBalance = 0;
                double coPay = 0;
                List<PaymentPatientBalancesPayloadDTO> paymentList
                        = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getPayload();

                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PREVIOUS_BALANCE)) {
                        previousBalance = Double.parseDouble(payment.getTotal());
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        coPay = Double.parseDouble(payment.getTotal());
                    }
                }

                // Balance of at least $20
                if ((previousBalance + coPay) > CarePayConstants.PAYMENT_PLAN_REQUIRED_BALANCE) {
                    PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

                    Gson gson = new Gson();
                    Bundle arguments = getArguments();
                    String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
                    PaymentsModel paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);

                    Bundle args = new Bundle();
                    args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsModel);
                    fragment.setArguments(args);

                    ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                } else {
                    Toast.makeText(getActivity(), paymentsModel.getPaymentsMetadata().getPaymentsLabel()
                            .getPaymentPlanCreateConditionError(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private View.OnClickListener paymentChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getLabels();

            int position = (Integer) view.getTag();
            switch (position) {
                case 0:
                    new LargeAlertDialog(getActivity(), dialogTitle, dialogText,
                            new LargeAlertDialog.LargeAlertInterface() {
                                @Override
                                public void onActionButton() {
                                }
                            }).show();
                    break;

                case 1:
                    PatientChooseCreditCardFragment fragment = new PatientChooseCreditCardFragment();

                    Gson gson = new Gson();
                    Bundle arguments = getArguments();
                    String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
                    PaymentsModel paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);

                    Bundle args = new Bundle();
                    args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
                    args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsModel);
                    fragment.setArguments(args);

                    ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * partial payment labels
     */
    public void getLabels() {
        if (paymentsModel != null) {
            PaymentsMetadataModel paymentsMetadataModel = paymentsModel.getPaymentsMetadata();

            if (paymentsMetadataModel != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataModel.getPaymentsLabel();

                if (paymentsLabelsDTO != null) {
                    dialogTitle = paymentsLabelsDTO.getPaymentSeeFrontDeskButton();
                    dialogText = paymentsLabelsDTO.getPaymentBackButton();
                    titlePaymentMethodString = paymentsLabelsDTO.getPaymentMethodTitle();
                    paymentChooseMethodString = paymentsLabelsDTO.getPaymentChooseMethodButton();
                    paymentCreatePlanString = paymentsLabelsDTO.getPaymentCreatePlanButton();
                }
            }
        }
    }
}

