package com.carecloud.carepay.practice.library.payments.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
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
    private PaymentsModel paymentsDTO;

    private Boolean isCloverDevice;
    private LinearLayout swipeCreditCardNowLayout ;
    private Button swipeCreditCarNowButton ;
    private TextView swipeCardSeparatorLabel ;
    private String swipeCardNowString;
    private String swipeCardSeparatorString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String paymentInfo = bundle.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);
            paymentList = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getPaymentMethods();

            paymentInfo = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentsDTO = gson.fromJson(paymentInfo, PaymentsModel.class);
        }
        View view = inflater.inflate(R.layout.fragment_payment_method_practice, container, false);
        isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals("Clover") ;
        TextView title = (TextView) view.findViewById(R.id.paymentMethodTitleLabel);

       // Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        //TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        //toolbar.setTitle("");
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        getLabels();
        initializeViews(view);
        title.setText(titlePaymentMethodString);

        //title.setText(titlePaymentMethodString);
        //toolbar.setTitle(titlePaymentMethodString);

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

        setSwipeCardNowVisibility(view);
    }

    private void setSwipeCardNowVisibility(View view)
    {
        swipeCreditCardNowLayout = (LinearLayout) view.findViewById(R.id.swipeCreditCardNowLayout);
        if(isCloverDevice)
        {
            swipeCreditCardNowLayout.setVisibility(View.VISIBLE);

            swipeCreditCarNowButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
            swipeCreditCarNowButton.setText(swipeCardNowString);
            swipeCreditCarNowButton.setOnClickListener(swipeCreditCarNowButtonClickListener);

            swipeCardSeparatorLabel = (TextView) view.findViewById(R.id.swipeCardSeparatorLabel);
            swipeCardSeparatorLabel.setText(swipeCardSeparatorString);
        }
        else
        {
            swipeCreditCardNowLayout.setVisibility(View.GONE);
        }

    }

    private View.OnClickListener swipeCreditCarNowButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setCloverPayment();
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(50.0f);

        for (int i = 0; i < paymentList.size(); i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentList.get(i).getLabel())) {
                selectedPaymentMethod = selectedRadioButton.getText().toString();
                paymentChoiceButton.setText(paymentList.get(i).getButtonLabel());
                paymentChoiceButton.setTag(paymentList.get(i).getType());
                if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CASH)){
                    shape.setColor(getActivity().getResources().getColor(R.color.overlay_green));
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CREDIT_CARD)){
                    shape.setColor(getActivity().getResources().getColor(R.color.bright_cerulean));
                    paymentChoiceButton.setText(paymentsDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentChooseCreditCardButton());
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CHECK)){
                    shape.setColor(getActivity().getResources().getColor(R.color.bright_cerulean));
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_GIFT_CARD)){
                    shape.setColor(getActivity().getResources().getColor(R.color.bright_cerulean));
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_PAYPAL)){
                    shape.setColor(getActivity().getResources().getColor(R.color.overlay_green));
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_HSA)){
                    shape.setColor(getActivity().getResources().getColor(R.color.bright_cerulean));
                }if(paymentList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_FSA)){
                    shape.setColor(getActivity().getResources().getColor(R.color.bright_cerulean));
                }
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
                ((RadioButton) paymentMethodRadioGroup.getChildAt(i))
                        .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));

            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.bright_cerulean));
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

                    Bundle arguments = getArguments();
                    String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);

                    Bundle args = new Bundle();
                    args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentInfo);
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

            String type = (String) view.getTag();
            switch (type) {
                case CarePayConstants.TYPE_CASH:
                    new LargeAlertDialog(getActivity(), dialogTitle, dialogText,R.color.lightningyellow,
                            R.drawable.icn_notification_basic, new LargeAlertDialog.LargeAlertInterface() {
                        @Override
                        public void onActionButton() {
                        }
                    }).show();
                    break;

                case CarePayConstants.TYPE_CREDIT_CARD:

                    Gson gson = new Gson();
                    Bundle args = new Bundle();
                    Fragment fragment;
                    if(paymentsModel.getPaymentPayload().getPatientCreditCards()!=null &&
                            paymentsModel.getPaymentPayload().getPatientCreditCards().size()>0){
                        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
                        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsDTO));
                        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, gson.toJson(paymentsModel));
                        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, getArguments()
                                .getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE));
                        fragment = new PatientChooseCreditCardFragment();
                    } else {
                        args.putString(CarePayConstants.INTAKE_BUNDLE, gson.toJson(paymentsModel));
                        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsDTO));
                        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE,  getArguments()
                                .getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE));
                        fragment = new PatientAddNewCreditCardFragment();
                    }

                    fragment.setArguments(args);
                    ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * payment labels
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
                    swipeCardNowString = paymentsLabelsDTO.getPaymentCloverSwipeNowButtonLabel();
                    swipeCardSeparatorString = paymentsLabelsDTO.getPaymentCloverSwipeNowSeparatorText();

                }
            }
        }
    }

    private void setCloverPayment()
    {
        try
        {
        PaymentsPatientBalancessDTO patientPayments = paymentsDTO.getPaymentPayload().getPatientBalances().get(0);
        Gson gson = new Gson();
        String patientPaymentMetaDataString = gson.toJson(patientPayments.getBalances().get(0).getMetadata());
        String paymentTransitionString = gson.toJson(paymentsDTO.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
        Intent intent = new Intent();
        intent.setAction("com.carecloud.carepay.practice.clover.payments.CloverPaymentActivity");
        intent.putExtra("PAYMENT_METADATA", patientPaymentMetaDataString);
        intent.putExtra("PAYMENT_AMOUNT", patientPayments.getBalances().get(0).getPayload().get(0).getAmount().doubleValue());
        intent.putExtra("PAYMENT_TRANSITION", paymentTransitionString);
        if(StringUtil.isNullOrEmpty(patientPayments.getBalances().get(0).getPayload().get(0).getType()))
        {
            intent.putExtra("ITEM_NAME", patientPayments.getBalances().get(0).getPayload().get(0).getType());
        }
        getContext().startActivity(intent, new Bundle());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}