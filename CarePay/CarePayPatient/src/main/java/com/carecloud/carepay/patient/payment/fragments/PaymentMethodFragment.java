package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.PaymentResponsibilityModel;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.androidpay.EnvData;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
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
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.google.gson.Gson;

import java.util.ArrayList;
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
    private List<PaymentsMethodsDTO> paymentMethodsList = null;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;
    private String paymentChangeMethodString;
    private String paymentFailedErrorString;

    private ProgressBar _paymentMethodFragmentProgressBar;
    private GoogleApiClient _GoogleApiClient;
    private Boolean _isProgressBarVisible = false;
    private SupportWalletFragment _walletFragment;

    private ScrollView scrollviewChoices;

    private List lineItems;
    private boolean isAndroidPayReady;

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
        scrollviewChoices = (ScrollView) view.findViewById(R.id.scrollview_choices);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String pametsDtoString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentsDTO = gson.fromJson(pametsDtoString, PaymentsModel.class);
        }
        paymentMethodsList = paymentsDTO.getPaymentPayload().getPaymentSettings().getPayload().getPaymentMethods();

        getLabels();
        initializeViews(view);
        if (_GoogleApiClient == null) {
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
        disconnectGoogleAPI();
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnectGoogleAPI();
    }

    private void disconnectGoogleAPI() {

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
                                isAndroidPayReady = true;
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

        addAndroidPayPaymentMethod();
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


        for (int i = 0; i < paymentMethodsList.size(); i++) {
            addPaymentMethodOptionView(i);
        }
    }


    private void addAndroidPayPaymentMethod() {
        if (isAndroidPayReady) {
            PaymentsMethodsDTO androidPayPaymentMethod = new PaymentsMethodsDTO();
            androidPayPaymentMethod.setLabel(PaymentConstants.ANDROID_PAY);
            androidPayPaymentMethod.setType(CarePayConstants.TYPE_ANDROID_PAY);
            paymentMethodsList.add(androidPayPaymentMethod);
            addPaymentMethodOptionView(paymentMethodsList.size() - 1);
        }

    }


    private void addPaymentMethodOptionView(int i) {
        paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentMethodsList.get(i).getType(), paymentMethodsList.get(i).getLabel(), i),
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
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    }

    private View.OnClickListener createPaymentPlanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (paymentsDTO != null) {
                double previousBalance = 0;
                double coPay = 0;
                List<PaymentPatientBalancesPayloadDTO> paymentMethodsList
                        = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getPayload();

                for (PaymentPatientBalancesPayloadDTO payment : paymentMethodsList) {
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

                    Bundle args = new Bundle();
                    Gson gson = new Gson();
                    String paymentsDTOString = gson.toJson(paymentsDTO);
                    args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                            paymentsDTOString);
                    fragment.setArguments(args);
                    PaymentResponsibilityModel paymentModel = PaymentResponsibilityModel.getInstance();
                    paymentModel.setArguments(args);

                    if (getActivity() instanceof PaymentActivity) {
                        ((PaymentActivity) getActivity()).navigateToFragment(fragment, true);
                    } else if (getActivity() instanceof DemographicsSettingsActivity) {
                        ((DemographicsSettingsActivity) getActivity()).navigateToFragment(fragment, true);
                    } else if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
                        ((ViewPaymentBalanceHistoryActivity) getActivity()).navigateToFragment(fragment, true);
                    }

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
                    new LargeAlertDialog(getActivity(), dialogTitle, dialogText, R.color.lightningyellow, R.drawable.icn_notification_basic, new LargeAlertDialog.LargeAlertInterface() {
                        @Override
                        public void onActionButton() {
                        }
                    }).show();
                    break;

                case CarePayConstants.TYPE_CREDIT_CARD:

                    TransitionDTO transitionDTO = paymentsDTO.getPaymentsMetadata()
                            .getPaymentsLinks().getPaymentsCreditCards();
                    WorkflowServiceHelper.getInstance().execute(transitionDTO, getCreditCardsCallback);
                    break;

                default:
                    break;
            }
        }
    };

    WorkflowServiceCallback getCreditCardsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            PaymentsModel paymentsModel = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);

            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
            Fragment fragment;

            if(paymentsModel!=null && paymentsModel.getPaymentPayload().getPatientCreditCards()!=null
                    && paymentsModel.getPaymentPayload().getPatientCreditCards().size()>0){
                fragment = (ChooseCreditCardFragment) fragmentmanager
                        .findFragmentByTag(ChooseCreditCardFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new ChooseCreditCardFragment();
                }
            } else {
                fragment = (AddNewCreditCardFragment) fragmentmanager
                        .findFragmentByTag(AddNewCreditCardFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new AddNewCreditCardFragment();
                }
            }

            Bundle args = new Bundle();
            args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
            args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, getArguments()
                    .getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE));
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsDTO));
            args.putString(CarePayConstants.INTAKE_BUNDLE, workflowDTO.toString());
            fragment.setArguments(args);

            if (getActivity() instanceof PaymentActivity) {
                ((PaymentActivity) getActivity()).navigateToFragment(fragment, true);
            } else if (getActivity() instanceof DemographicsSettingsActivity) {
                ((DemographicsSettingsActivity) getActivity()).navigateToFragment(fragment, true);
            } else if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
                ((ViewPaymentBalanceHistoryActivity) getActivity()).navigateToFragment(fragment, true);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
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

    //    Android Pay

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

        for (int i = 0; i < paymentMethodsList.size(); i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentMethodsList.get(i).getLabel())) {
                if (selectedRadioButton.getText().toString().equalsIgnoreCase(PaymentConstants.ANDROID_PAY)) {
                    paymentChoiceButton.setVisibility(View.GONE);
                    setLineItems(paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getPayload());
                    createAndAddWalletFragment(paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getPendingRepsonsibility());// getPayload().get(0).getTotal());
                    //scrollviewChoices.fullScroll(View.FOCUS_DOWN);

                } else {

                    if (paymentChoiceButton.getVisibility() == View.GONE) {
                        paymentChoiceButton.setVisibility(View.VISIBLE);
                    }

                    if (_walletFragment != null) {
                        removeWalletFragment();
                    }
                    selectedPaymentMethod = selectedRadioButton.getText().toString();
                    paymentChoiceButton.setText(paymentMethodsList.get(i).getButtonLabel());
                    paymentChoiceButton.setTag(paymentMethodsList.get(i).getType());
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CASH)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.overlay_green));
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CREDIT_CARD)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        paymentChoiceButton.setText(paymentsDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentChooseCreditCardButton());
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CHECK)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_GIFT_CARD)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_PAYPAL)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.overlay_green));
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_HSA)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    }
                    if (paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_FSA)) {
                        paymentChoiceButton.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    }
                }

            }

        }
    }


    /**
     * Create the wallet fragment. This will create the "Buy with Android Pay" button.
     *
     * @param totalPrice Amount received from the user
     */
    private void createAndAddWalletFragment(String totalPrice) {
        WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                .setBuyButtonHeight(WalletFragmentStyle.Dimension.UNIT_DIP, CarePayConstants.ANDROID_PAY_BUTTON_HEIGHT)
                .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT)
                .setBuyButtonText(WalletFragmentStyle.BuyButtonText.LOGO_ONLY)
                .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
                .setMaskedWalletDetailsBackgroundColor(R.color.android_pay_background_color);

        WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                .setFragmentStyle(walletFragmentStyle)
                .setTheme(WalletConstants.THEME_DARK)
                .setMode(WalletFragmentMode.BUY_BUTTON)
                .build();
        _walletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

        // Now initialize the Wallet Fragment
        MaskedWalletRequest maskedWalletRequest = createMaskedWalletRequest(totalPrice);

        String accountName = getString(com.carecloud.carepay.patient.R.string.account_name);

        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_MASKED_WALLET)
                .setAccountName(accountName);
        _walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        getFragmentManager().beginTransaction()
                .replace(com.carecloud.carepay.patient.R.id.dynamic_wallet_button_fragment, _walletFragment)
                .commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollviewChoices.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 300);

    }


    /**
     * Create the Masked Wallet request. Note that the Tokenization Type is set to
     * {@code NETWORK_TOKEN} and the {@code publicKey} parameter is set to the public key
     * that was created by First Data.
     *
     * @param totalPrice The amount the user entered
     * @return A Masked Wallet request object
     */
    private MaskedWalletRequest createMaskedWalletRequest(String totalPrice) {
        MaskedWalletRequest.Builder builder = MaskedWalletRequest.newBuilder()
                .setMerchantName(PaymentConstants.MERCHANT_NAME)
                .setPhoneNumberRequired(true)
                .setShippingAddressRequired(true)
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setEstimatedTotalPrice(totalPrice)
//                 Create a Cart with the current line items. Provide all the information
//                 available up to this point with estimates for shipping and tax included.
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                        .setTotalPrice(totalPrice)
                        .setLineItems(getLineItems())
                        .build());

        //  Set tokenization type and First Data issued public key
        PaymentMethodTokenizationParameters mPaymentMethodParameters = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.NETWORK_TOKEN)
                .addParameter("publicKey", EnvData.getProperties("CERT").getPublicKey())
                .build();
        builder.setPaymentMethodTokenizationParameters(mPaymentMethodParameters);
        return builder.build();
    }

    /**
     * Create a fake line item list. Set the amount to the one received from the user.
     *
     * @return List of line items
     */
    public List getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<PaymentPatientBalancesPayloadDTO> balances) {
        List<LineItem> list = new ArrayList<LineItem>();

        PaymentResponsibilityModel paymentModel = PaymentResponsibilityModel.getInstance();
        paymentModel.balancesList = new ArrayList();

        for (PaymentPatientBalancesPayloadDTO balance : balances) {
            list.add(LineItem.newBuilder()
                    .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                    .setDescription(balance.getBalanceType())
                    .setQuantity("1")
                    .setTotalPrice(balance.getTotal())
                    .setUnitPrice(balance.getTotal())
                    .build());
            paymentModel.balance1 = balance.getTotal();
            paymentModel.balancesList.add(paymentModel.balance1);


        }

        this.lineItems = list;
    }

    private void removeWalletFragment() {
        getFragmentManager().beginTransaction()
                .remove(_walletFragment)
                .commit();

        _walletFragment = null;
    }
}
