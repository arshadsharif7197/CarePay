package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.dialogs.ChooseCreditCardDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.BasePaymentDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPayloadPlansDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentPlanFragment extends BasePaymentDialogFragment {

    private static final String LOG_TAG = PaymentPlanFragment.class.getSimpleName();

    private int minNumberOfMonths = 2;
    private int maxNumberOfMonths = 120;
    private int minMonthlyPayment = 10;
    private double maxMonthlyPayment = 9999999999.99;

    private PaymentsModel paymentsModel;

    private Button createPlanButton;
    private TextView addedCreditCard;
    private TextView addChangeCreditCard;
    private EditText paymentPlanName;
    private EditText paymentPlanMonthNo;
    private EditText paymentPlanMonthlyPayment;
    private TextInputLayout paymentPlanMonthInput;
    private TextInputLayout paymentPlanMonthlyInput;

    private double totalBalance;
    private boolean isEmptyMonthNo = true;
    private boolean isEmptyMonthlyPayment = true;
    private boolean isEmptyCreditCard = true;
    private boolean isMNEdited;
    private boolean isMPEdited;
    private PaymentNavigationCallback callback;


    @Override
    protected void attachCallback(Context context) {
        try{
            if(context instanceof PaymentViewHandler){
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                callback = (PaymentNavigationCallback) context;
            }
        }catch(ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(callback == null){
            attachCallback(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_plan, container, false);

        paymentPlanName = (EditText) view.findViewById(R.id.payment_plan_name_edit);
        paymentPlanMonthNo = (EditText) view.findViewById(R.id.payment_plan_month_no_edit);
        paymentPlanMonthlyPayment = (EditText) view.findViewById(R.id.payment_plan_monthly_payment_edit);
        createPlanButton = (Button) view.findViewById(R.id.create_payment_plan_button);

        addedCreditCard = (TextView) view.findViewById(R.id.payment_plan_credit_card);
        addChangeCreditCard = (TextView) view.findViewById(R.id.payment_plan_credit_card_button);
        addChangeCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (isEmptyCreditCard) {
                    //Goto Add credit card screen
                    addNewCreditCard();
                } else {
                    //Goto Credit card screen to Choose
                    ChooseCreditCardDialog creditCardDialog = new ChooseCreditCardDialog(
                            getActivity(), paymentsModel, creditCardClickListener);
                    creditCardDialog.show();
                }
            }
        });

        Gson gson = new Gson();
        String paymentsDTOString = getArguments().getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
        paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);

        if (paymentsModel != null) {
            setLabels();
            setEditTexts(view);
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("payment_plan_heading"));

        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Spinner monthDays = (Spinner) view.findViewById(R.id.payment_plan_month_day);
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < 31; ) {
            items.add("" + ++i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthDays.setAdapter(adapter);
        monthDays.setOnItemSelectedListener(itemSelectedListener);

        String copayStr = "";
        String previousBalanceStr = "";

        if (paymentsModel != null) {
            List<PaymentPatientBalancesPayloadDTO> paymentList
                    = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getPayload();

            if (paymentList != null && paymentList.size() > 1) {
                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PREVIOUS_BALANCE)) {
                        previousBalanceStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        copayStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    totalBalance = copay + previousBalance;

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                    ((TextView) view.findViewById(R.id.payment_plan_total)).setText(CarePayConstants.DOLLAR.concat(formatter.format(totalBalance)));
                    ((TextView) view.findViewById(R.id.payment_plan_prev_balance)).setText(CarePayConstants.DOLLAR.concat(previousBalanceStr));
                    ((TextView) view.findViewById(R.id.payment_plan_copay)).setText(CarePayConstants.DOLLAR.concat(copayStr));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }

        return view;
    }

    private void enableCreatePlanButton() {
        if (paymentPlanMonthNo.getText().length() > 0
                && paymentPlanMonthlyPayment.getText().length() > 0
                && !isEmptyCreditCard) {
            createPlanButton.setEnabled(true);
        }
    }

    private void setEditTexts(View view) {
        TextInputLayout paymentPlanNameInput = (TextInputLayout) view.findViewById(R.id.payment_plan_name_input);
        String nameLabel = Label.getLabel("payment_plan_name");
        paymentPlanNameInput.setTag(nameLabel);
        paymentPlanName.setHint(nameLabel);
        paymentPlanName.setTag(paymentPlanNameInput);

        paymentPlanMonthInput = (TextInputLayout) view.findViewById(R.id.payment_plan_month_no_input);
        String monthNumber = Label.getLabel("payment_number_of_months");
        paymentPlanMonthInput.setTag(monthNumber);
        paymentPlanMonthNo.setHint(monthNumber);
        paymentPlanMonthNo.setTag(paymentPlanMonthInput);

        paymentPlanMonthlyInput = (TextInputLayout) view.findViewById(R.id.payment_plan_monthly_payment_input);
        String monthlyPayment = Label.getLabel("payment_monthly_payment");
        paymentPlanMonthlyInput.setTag(monthlyPayment);
        paymentPlanMonthlyPayment.setHint(monthlyPayment);
        paymentPlanMonthlyPayment.setTag(paymentPlanMonthlyInput);

        setTextListeners();
        setChangeFocusListeners();

        paymentPlanName.clearFocus();
        paymentPlanMonthNo.clearFocus();
        paymentPlanMonthlyPayment.clearFocus();
    }

    private void setLabels() {
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                createPaymentPlan();
            }
        });

        List<PaymentsPatientsCreditCardsPayloadListDTO> payload = paymentsModel.getPaymentPayload().getPatientCreditCards();
        if (payload.size() > 0) {
            if (payload != null && payload.size() > 0) {
                // Get default credit card
                PaymentCreditCardsPayloadDTO creditCard = payload.get(0).getPayload();
                String creditCardName = creditCard.getCardType();
                addedCreditCard.setText(StringUtil.getEncodedCardNumber(
                        creditCardName, creditCard.getCardNumber()));
                addedCreditCard.setVisibility(View.VISIBLE);

                addChangeCreditCard.setText(Label.getLabel("payment_change_card"));
                isEmptyCreditCard = false;
            } else {
                isEmptyCreditCard = true;
                addChangeCreditCard.setText(Label.getLabel("payment_add_card_button"));
            }
        } else {
            isEmptyCreditCard = true;
            addChangeCreditCard.setText(Label.getLabel("payment_add_card_button"));
        }

        addChangeCreditCard.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
    }

    ChooseCreditCardDialog.ChooseCreditCardClickListener creditCardClickListener
            = new ChooseCreditCardDialog.ChooseCreditCardClickListener() {

        @Override
        public void onCreditCardSelection(int selectedIndex) {
            List<PaymentsPatientsCreditCardsPayloadListDTO> payload
                    = paymentsModel.getPaymentPayload().getPatientCreditCards();

            if (payload != null && payload.size() > 0) {
                // Get default credit card
                PaymentCreditCardsPayloadDTO creditCard = payload.get(selectedIndex).getPayload();
                String creditCardName = creditCard.getCardType();
                addedCreditCard.setText(StringUtil.getEncodedCardNumber(creditCardName, creditCard.getCardNumber()));
                addedCreditCard.setVisibility(View.VISIBLE);
                addChangeCreditCard.setText(Label.getLabel("payment_change_card"));
                isEmptyCreditCard = false;
            }
        }
    };

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = (String) parent.getItemAtPosition(position);
            String suffix;
            switch (item) {
                case "1":
                case "21":
                case "31":
                    suffix = "st";
                    break;
                case "2":
                case "22":
                    suffix = "nd";
                    break;
                case "3":
                case "23":
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }

            TextView selectedItem = (TextView) parent.getChildAt(0);
            if (selectedItem != null) {
                selectedItem.setText(item.concat(suffix));
                selectedItem.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), selectedItem);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

//    private String getCreditCardName(String cardType) {
//        if (paymentsModel != null) {
//            List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType
//                    = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getCreditCardType();
//
//            if (creditCardType != null && creditCardType.size() > 0) {
//                for (int i = 0; i < creditCardType.size(); i++) {
//                    PaymentsSettingsPayloadCreditCardTypesDTO creditCardTypesDTO = creditCardType.get(i);
//                    if (creditCardTypesDTO.getType().equalsIgnoreCase(cardType)) {
//                        return creditCardTypesDTO.getLabel();
//                    }
//                }
//            }
//        }
//        return cardType;
//    }

    private void setTextListeners() {
        paymentPlanMonthNo.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isMNEdited && !isMPEdited) {
                    try {
                        String number = paymentPlanMonthNo.getText().toString();
                        isEmptyMonthNo = StringUtil.isNullOrEmpty(number);
                        if (!isEmptyMonthNo) {
                            paymentPlanMonthInput.setError(null);
                            paymentPlanMonthInput.setErrorEnabled(false);

                            int noOfMonths = Integer.parseInt(number);
                            paymentPlanMonthlyPayment.setText(String.format("%s",
                                    StringUtil.getFormattedBalanceAmount(totalBalance / noOfMonths)));
                            paymentPlanMonthlyPayment.requestFocus();
                        }
                    } catch (NumberFormatException nfe) {
                        Log.e(LOG_TAG, nfe.getMessage());
                    }
                }

                enableCreatePlanButton();
            }
        });

        paymentPlanMonthlyPayment.addTextChangedListener(new TextWatcher() {

            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (!charSequence.toString().equals(current)) {
                    paymentPlanMonthlyPayment.removeTextChangedListener(this);

                    String cleanString = charSequence.toString().replaceAll("[$,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    paymentPlanMonthlyPayment.setText(formatted);
                    paymentPlanMonthlyPayment.requestFocus();
                    paymentPlanMonthlyPayment.setSelection(formatted.length());
                    paymentPlanMonthlyPayment.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isMNEdited && isMPEdited) {
                    String monthlyPayment = paymentPlanMonthlyPayment.getText().toString();
                    isEmptyMonthlyPayment = StringUtil.isNullOrEmpty(monthlyPayment);
                    if (!isEmptyMonthlyPayment) {
                        paymentPlanMonthlyInput.setError(null);
                        paymentPlanMonthlyInput.setErrorEnabled(false);

                        double payment = Double.parseDouble(monthlyPayment.replaceAll("[$,]", ""));
                        int numberOfMonths = (int) Math.round(totalBalance / payment);
                        paymentPlanMonthNo.setText(String.format("%s", numberOfMonths));
                        paymentPlanMonthNo.requestFocus();
                    }
                }

                enableCreatePlanButton();
            }
        });
    }

    private void setChangeFocusListeners() {
        paymentPlanName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        paymentPlanMonthNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    isMNEdited = true;
                    isMPEdited = false;
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        paymentPlanMonthlyPayment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    isMNEdited = false;
                    isMPEdited = true;
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }

    private boolean allValid() {
        paymentPlanMonthInput.setErrorEnabled(true);
        paymentPlanMonthlyInput.setErrorEnabled(true);

        PaymentsSettingsPayloadPlansDTO paymentPlans = paymentsModel.getPaymentPayload()
                .getPaymentSettings().get(0).getPayload().getPaymentPlans();//todo need to lookup appropriate settings for prctice id on selected balance

        if (paymentPlans != null) {
            String minimumPayment = paymentPlans.getMinimumPayment();
            if (!StringUtil.isNullOrEmpty(minimumPayment)) {
                minMonthlyPayment = Integer.parseInt(minimumPayment);
            }

            String maximumPayment = paymentPlans.getMaximumPayment();
            if (!StringUtil.isNullOrEmpty(maximumPayment)) {
                maxMonthlyPayment = Double.parseDouble(maximumPayment);
            }

            String minimumCyclesMonths = paymentPlans.getMinimumCyclesMonths();
            if (!StringUtil.isNullOrEmpty(minimumCyclesMonths)) {
                minNumberOfMonths = Integer.parseInt(minimumCyclesMonths);
            }

            String maximumCyclesMonths = paymentPlans.getMaximumCyclesMonths();
            if (!StringUtil.isNullOrEmpty(maximumCyclesMonths)) {
                maxNumberOfMonths = Integer.parseInt(maximumCyclesMonths);
            }
        }

        try {
            int noOfMonths = Integer.parseInt(paymentPlanMonthNo.getText().toString());
            if (noOfMonths < minNumberOfMonths) {
                //minimum number of month should be 2
                paymentPlanMonthInput.setError(String.format(
                        Label.getLabel("payment_plan_min_months_error"), minNumberOfMonths));
                paymentPlanMonthNo.requestFocus();
                return false;
            } else if (noOfMonths > maxNumberOfMonths) {
                //maximum number of months should be 120 (10 yrs)
                paymentPlanMonthInput.setError(String.format(
                        Label.getLabel("payment_plan_max_months_error"), maxNumberOfMonths));
                paymentPlanMonthNo.requestFocus();
                return false;
            }

            double payment = Double.parseDouble(paymentPlanMonthlyPayment.getText().toString().replaceAll("[$,]", ""));
            if (payment < minMonthlyPayment) {
                //Minimum monthly payment amount = $10.00
                paymentPlanMonthlyInput.setError(String.format(
                        Label.getLabel("payment_plan_min_amount_error"),
                        StringUtil.getFormattedBalanceAmount(minMonthlyPayment)));
                paymentPlanMonthlyPayment.requestFocus();
                return false;
            } else if (payment > maxMonthlyPayment) {
                //maximum monthly payment amount = $9999999999.99
                paymentPlanMonthlyInput.setError(String.format(
                        Label.getLabel("payment_plan_max_amount_error"),
                        StringUtil.getFormattedBalanceAmount(maxMonthlyPayment)));
                paymentPlanMonthlyPayment.requestFocus();
                return false;
            }
        } catch (NumberFormatException nfe) {
            Log.e(LOG_TAG, nfe.getMessage());
            return false;
        }

        return true;
    }

    private void addNewCreditCard() {
        callback.showAddCard(0, paymentsModel);
//        FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
//        PatientAddNewCreditCardFragment fragment = (PatientAddNewCreditCardFragment)
//                fragmentmanager.findFragmentByTag(PatientAddNewCreditCardFragment.class.getSimpleName());
//        if (fragment == null) {
//            fragment = new PatientAddNewCreditCardFragment();
//        }
//
//        Bundle args = new Bundle();
//        Gson gson = new Gson();
//        args.putSerializable(CarePayConstants.INTAKE_BUNDLE, paymentsModel);
//        args.putString(CarePayConstants.PAYEEZY_MERCHANT_SERVICE_BUNDLE, gson.toJson(paymentsModel.getPaymentPayload().getPapiAccounts()));
//        fragment.setArguments(args);
//
//        if (getActivity() instanceof PaymentActivity) {
//            ((PaymentActivity) getActivity()).navigateToFragment(fragment, true);
//        } else if (getActivity() instanceof DemographicsSettingsActivity) {
//            ((DemographicsSettingsActivity) getActivity()).navigateToFragment(fragment, true);
//        } else if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
//            ((ViewPaymentBalanceHistoryActivity) getActivity()).navigateToFragment(fragment, true);
//        }
    }

    private void createPaymentPlan() {
        if (allValid()) {
            Gson gson = new Gson();
            Map<String, String> queries = new HashMap<>();
            JsonObject queryStringObject = paymentsModel.getPaymentsMetadata()
                    .getPaymentsTransitions().getAddPaymentPlan().getQueryString();
            QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

            queries.put(queryStrings.getPracticeMgmt().getName(),
                    paymentsModel.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeMgmt());
            queries.put(queryStrings.getPracticeId().getName(),
                    paymentsModel.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeId());
            queries.put(queryStrings.getPatientId().getName(),
                    paymentsModel.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPatientId());

            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddPaymentPlan();
            getWorkflowServiceHelper().execute(transitionDTO, createPlanCallback, queries);
        }
    }

    private WorkflowServiceCallback createPlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
