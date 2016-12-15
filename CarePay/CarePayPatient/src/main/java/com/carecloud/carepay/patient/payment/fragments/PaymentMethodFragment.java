package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentMethodFragment extends Fragment implements RadioGroup.OnCheckedChangeListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PaymentMethodFragment";
    private RadioGroup paymentMethodRadioGroup;
    private Button paymentChoiceButton;
    private Activity activity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private String selectedPaymentMethod;
    //private int[] paymentMethodsDrawableArray;
    private PaymentsModel paymentsDTO;
    private String dialogTitle;
    private String dialogText;
    private List<PaymentsMethodsDTO> paymentList = null;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;
    private String paymentChangeMethodString;
    private String paymentFailedErrorString;

    private ProgressBar _paymentMethodFragmentProgressBar;
    private GoogleApiClient _GoogleApiClient;
    private Boolean _isProgressBarVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        activity = getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        //title.setText(titlePaymentMethodString);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        _paymentMethodFragmentProgressBar = (ProgressBar) view.findViewById(R.id.paymentMethodFragmentProgressBar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        Bundle bundle = getArguments();
        if (bundle != null) {
            paymentsDTO = (PaymentsModel) bundle.getSerializable(CarePayConstants.INTAKE_BUNDLE);
        }
        paymentList = paymentsDTO.getPaymentPayload().getPaymentSettings().getPayload().getPaymentMethods();

        getLabels();
        initializeViews(view);
        if(_GoogleApiClient == null)
        {
            setGoogleApiClient();
        }
        isAndroidPayReadyToUse();
        title.setText(titlePaymentMethodString);
        toolbar.setTitle(titlePaymentMethodString);
        return view;
    }

    private void setGoogleApiClient() {
        // [START basic_google_api_client]
        _GoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                        .build())
                .enableAutoManage(getActivity(), this)
                .build();
        // [END basic_google_api_client]

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _GoogleApiClient.stopAutoManage(getActivity());
        _GoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        Toast.makeText(getActivity(), "Google Play Services error", Toast.LENGTH_SHORT).show();
    }

    private RadioButton getPaymentMethodRadioButton(String cardType, String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
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
        cardTypeMap.put(CarePayConstants.TYPE_ANDROID_PAY, R.drawable.payment_android_button_selector);

        if (cardTypeMap.get(cardType) != null) {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    cardTypeMap.get(cardType), 0, R.drawable.check_box_intake, 0);
        } else {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    cardTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD), 0, R.drawable.check_box_intake, 0);
        }
        radioButtonView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.radio_button_selector));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }


    private void isAndroidPayReadyToUse() {
        showOrHideProgressDialog();
        IsReadyToPayRequest req = IsReadyToPayRequest.newBuilder()
                .addAllowedCardNetwork(WalletConstants.CardNetwork.MASTERCARD)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.VISA)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.AMEX)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.DISCOVER)
                .build();

        Wallet.Payments.isReadyToPay(_GoogleApiClient, req).setResultCallback(
                new ResultCallback<BooleanResult>() {
                    @Override
                    public void onResult(@NonNull BooleanResult booleanResult) {
                        showOrHideProgressDialog();
                        if (booleanResult.getStatus().isSuccess()) {
                            if (booleanResult.getValue()) {
                                showOrHideProgressDialog();
                                addAndroidPayPaymentMethod();
                            } else {

                                showOrHideProgressDialog();
                            }
                        } else {
                            // Error making isReadyToPay call
                            Log.e(TAG, "isReadyToPay:" + booleanResult.getStatus());
                            showOrHideProgressDialog();
                        }
                    }
                });

    }

    private void initializeViews(View view) {
        paymentMethodRadioGroup = (RadioGroup) view.findViewById(R.id.paymentMethodsRadioGroup);
        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        Button createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        paymentChoiceButton.setEnabled(false);
        paymentChoiceButton.setText(paymentChooseMethodString);
        createPaymentPlanButton.setText(paymentCreatePlanString);

        for (int i = 0; i < paymentList.size(); i++) {
            addPaymentMethodOptionView(i);
        }
    }


    private void addAndroidPayPaymentMethod() {
        PaymentsMethodsDTO androidPayPaymentMethod = new PaymentsMethodsDTO();
        androidPayPaymentMethod.setLabel(PaymentConstants.ANDROID_PAY);
        androidPayPaymentMethod.setType(CarePayConstants.TYPE_ANDROID_PAY);
        paymentList.add(androidPayPaymentMethod);
        addPaymentMethodOptionView(paymentList.size() - 1);
    }


    private void addPaymentMethodOptionView(int i) {
        paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentList.get(i).getType(), paymentList.get(i).getLabel(), i),
                radioGroupLayoutParam);

        View dividerLineView = new View(activity);
        dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
        ));
        dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
        paymentMethodRadioGroup.addView(dividerLineView);
        onSetRadioButtonRegularTypeFace();
    }

    private void showOrHideProgressDialog() {

        if (_isProgressBarVisible) {
            _paymentMethodFragmentProgressBar.setVisibility(View.GONE);
            _isProgressBarVisible = false;
        } else {
            _paymentMethodFragmentProgressBar.setVisibility(View.VISIBLE);
            _isProgressBarVisible = true;
        }

    }

    /**
     * If user chooses Android Pay as the option then hide
     * the paymentChoiceButton and show Android Pay button.
     **/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);

        for (int i = 0; i < paymentList.size(); i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentList.get(i).getLabel())) {
                if (selectedRadioButton.getText().toString().equalsIgnoreCase(PaymentConstants.ANDROID_PAY)) {
                    paymentChoiceButton.setVisibility(View.GONE);
                } else {

                    if (paymentChoiceButton.getVisibility() == View.GONE) {
                        paymentChoiceButton.setVisibility(View.VISIBLE);
                    }
                    selectedPaymentMethod = selectedRadioButton.getText().toString();
                    paymentChoiceButton.setText(paymentList.get(i).getButtonLabel());
                    paymentChoiceButton.setTag(paymentList.get(i).getType());
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
            if (paymentsDTO != null) {
                double previousBalance = 0;
                double coPay = 0;
                List<PaymentPatientBalancesPayloadDTO> paymentList
                        = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getPayload();

                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PREVIOUS_BALANCE)) {
                        previousBalance = Double.parseDouble(payment.getTotal());
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        coPay = Double.parseDouble(payment.getTotal());
                    }
                }

                // Balance of at least $20
                if ((previousBalance + coPay) > CarePayConstants.PAYMENT_PLAN_REQUIRED_BALANCE) {
                    FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                    PaymentPlanFragment fragment = (PaymentPlanFragment) fragmentmanager
                            .findFragmentByTag(PaymentPlanFragment.class.getSimpleName());
                    if (fragment == null) {
                        fragment = new PaymentPlanFragment();
                    }

                    Bundle arguments = getArguments();
                    Bundle args = new Bundle();
                    args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                            arguments.getSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO));
                    fragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                    fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
                    fragmentTransaction.addToBackStack(PaymentPlanFragment.class.getSimpleName());
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getActivity(), paymentsDTO.getPaymentsMetadata().getPaymentsLabel()
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
                    new LargeAlertDialog(getActivity(), dialogTitle, dialogText,R.color.lightningyellow, R.drawable.icn_notification_basic, new LargeAlertDialog.LargeAlertInterface() {
                        @Override
                        public void onActionButton() {
                        }
                    }).show();
                    break;

                case CarePayConstants.TYPE_CREDIT_CARD:
                    FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                    ChooseCreditCardFragment fragment = (ChooseCreditCardFragment) fragmentmanager
                            .findFragmentByTag(ChooseCreditCardFragment.class.getSimpleName());
                    if (fragment == null) {
                        fragment = new ChooseCreditCardFragment();
                    }

                    Bundle args = new Bundle();
                    args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
                    args.putSerializable(CarePayConstants.INTAKE_BUNDLE,
                            paymentsDTO);
                    fragment.setArguments(args);

                    FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                    fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
                    fragmentTransaction.addToBackStack(ChooseCreditCardFragment.class.getSimpleName());
                    fragmentTransaction.commit();
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
        if (paymentsDTO != null) {
            PaymentsMetadataModel paymentsMetadataModel = paymentsDTO.getPaymentsMetadata();
            if (paymentsMetadataModel != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataModel.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    dialogTitle = paymentsLabelsDTO.getPaymentSeeFrontDeskButton();
                    dialogText = paymentsLabelsDTO.getPaymentBackButton();
                    titlePaymentMethodString = paymentsLabelsDTO.getPaymentMethodTitle();
                    paymentChooseMethodString = paymentsLabelsDTO.getPaymentChooseMethodButton();
                    paymentCreatePlanString = paymentsLabelsDTO.getPaymentCreatePlanButton();
                    paymentChangeMethodString = paymentsLabelsDTO.getPaymentChangeMethodButton();
                    paymentFailedErrorString = paymentsLabelsDTO.getPaymentFailedErrorMessage();
                }
            }
        }
    }
}
