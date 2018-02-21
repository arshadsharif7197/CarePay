package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO.PATIENT_BALANCE;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentPlanFragment extends BasePaymentDialogFragment implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    protected PaymentsModel paymentsModel;
    protected PendingBalanceDTO selectedBalance;
    protected PaymentPlanInterface callback;

    private PaymentSettingsBalanceRangeRule paymentPlanBalanceRules = new PaymentSettingsBalanceRangeRule();

    protected NumberFormat currencyFormatter;
    protected double paymentPlanAmount;

    protected TextView createPlanButton;
    protected EditText planName;
    protected EditText paymentDate;
    protected EditText numberPayments;
    protected EditText monthlyPayment;
    private TextView lastPaymentMessage;

    protected List<DemographicsOption> dateOptions;
    protected DemographicsOption paymentDateOption;
    protected double monthlyPaymentAmount;
    protected int monthlyPaymentCount;

    private boolean isCalculatingAmount = false;
    private boolean isCalclatingTime = false;

    /**
     * @param paymentsModel   the payment model
     * @param selectedBalance the selected balance
     * @return an empty PaymentPlanFragment instance for creating a new payment plan
     */
    public static PaymentPlanFragment newInstance(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PaymentPlanFragment fragment = new PaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                callback = (PaymentPlanInterface) context;
            }
        } catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentPlanInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        paymentPlanAmount = calculateTotalAmount(selectedBalance);
        dateOptions = generateDateOptions();
        paymentDateOption = dateOptions.get(0);
        if (selectedBalance != null) {
            getPaymentPlanSettings(selectedBalance.getMetadata().getPracticeId());
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupToolBar(view);
        setupHeader(view);
        setupFields(view);
        setupButtons(view);
        setAdapter(view);
    }

    protected void setupToolBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDismissPaymentPlan(paymentsModel);
            }
        });
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("payment_plan_heading"));

    }

    protected void setupHeader(View view) {
        TextView total = (TextView) view.findViewById(R.id.payment_plan_total);
        total.setText(currencyFormatter.format(paymentPlanAmount));

        TextView parameters = (TextView) view.findViewById(R.id.payment_plan_parameters);
        if (parameters != null) {
            parameters.setText(String.format(Locale.US, Label.getLabel("payment_plan_parameters"),
                    paymentPlanBalanceRules.getMaxDuration().getValue(),
                    currencyFormatter.format(paymentPlanBalanceRules.getMinPaymentRequired().getValue())));
        }
    }

    protected void setupFields(View view) {
        planName = (EditText) view.findViewById(R.id.paymentPlanName);
        TextInputLayout planNameInputLayout = (TextInputLayout) view.findViewById(R.id.paymentPlanNameInputLayout);
        planName.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(planNameInputLayout, null));
        View planNameOptional = view.findViewById(R.id.paymentPlanNameOptional);
        planNameOptional.setVisibility(View.VISIBLE);
        planName.addTextChangedListener(getOptionalViewTextWatcher(planNameOptional));

        paymentDate = (EditText) view.findViewById(R.id.paymentDrawDay);
        TextInputLayout drawDayInputLayout = (TextInputLayout) view.findViewById(R.id.paymentDrawDayInputLayout);
        paymentDate.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(drawDayInputLayout, null));
        paymentDate.setText(paymentDateOption.getLabel());
        paymentDate.getOnFocusChangeListener().onFocusChange(paymentDate, true);
        paymentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog(getContext(), dateOptions, Label.getLabel("payment_day_of_the_month"), new ValueInputCallback() {
                    @Override
                    public void onValueInput(String input) {
                        paymentDate.setText(input);
                    }
                });
            }
        });

        numberPayments = (EditText) view.findViewById(R.id.paymentMonthCount);
        TextInputLayout numberPaymentsInputLayout = (TextInputLayout) view.findViewById(R.id.paymentMonthCountInputLayout);
        numberPayments.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(numberPaymentsInputLayout, null));
        numberPayments.addTextChangedListener(getRequiredTextWatcher(numberPaymentsInputLayout, new ValueInputCallback() {
            @Override
            public void onValueInput(String input) {
                if(isCalclatingTime){
                    isCalclatingTime = false;
                    return;
                }
                isCalculatingAmount = true;
                try {
                    monthlyPaymentCount = Integer.parseInt(input);
                    monthlyPaymentAmount = calculateMonthlyPayment(monthlyPaymentCount);
                    if (monthlyPayment.getOnFocusChangeListener() != null) {
                        monthlyPayment.getOnFocusChangeListener().onFocusChange(monthlyPayment, true);
                    }
                    monthlyPayment.setText(currencyFormatter.format(monthlyPaymentAmount));
                    setLastPaymentMessage(monthlyPaymentAmount);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }));

        monthlyPayment = (EditText) view.findViewById(R.id.paymentAmount);
        TextInputLayout paymentAmountInputLayout = (TextInputLayout) view.findViewById(R.id.paymentAmountInputLayout);
        monthlyPayment.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(paymentAmountInputLayout, currencyFocusChangeListener));
        monthlyPayment.addTextChangedListener(getRequiredTextWatcher(paymentAmountInputLayout, new ValueInputCallback() {
            @Override
            public void onValueInput(String input) {
                if(isCalculatingAmount){
                    isCalculatingAmount = false;
                    return;
                }
                isCalclatingTime = true;
                try {
                    monthlyPaymentAmount = Double.parseDouble(input);
                    monthlyPaymentAmount = Math.round(monthlyPaymentAmount * 100) / 100D;//make sure we don't consider eztra decimals here.. these will get formatted out
                    monthlyPaymentCount = calculatePaymentCount(monthlyPaymentAmount);
                    if (numberPayments.getOnFocusChangeListener() != null) {
                        numberPayments.getOnFocusChangeListener().onFocusChange(numberPayments, true);
                    }
                    numberPayments.setText(String.valueOf(monthlyPaymentCount));
                    setLastPaymentMessage(monthlyPaymentAmount);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }));


        lastPaymentMessage = (TextView) view.findViewById(R.id.lastPaymentMessage);
        lastPaymentMessage.setVisibility(View.INVISIBLE);
    }

    private void setupButtons(View view) {
        if (selectedBalance != null) {
            View addToExisting = view.findViewById(R.id.payment_plan_add_existing);
            if (hasExistingPlans() && canAddToExisting()) {
                addToExisting.setVisibility(View.VISIBLE);
            } else {
                addToExisting.setVisibility(View.GONE);
            }

            addToExisting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBalanceToExisting();
                }
            });
        }
        createPlanButton = (TextView) view.findViewById(R.id.create_payment_plan_button);
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPaymentPlan();
                SystemUtil.hideSoftKeyboard(getContext(), view);
            }
        });

        enableCreatePlanButton();
    }

    private void setAdapter(View view) {
        RecyclerView balanceRecycler = (RecyclerView) view.findViewById(R.id.balance_recycler);
        if (balanceRecycler != null && selectedBalance != null) {
            balanceRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(), selectedBalance.getPayload(), this);
            balanceRecycler.setAdapter(adapter);
        }
    }

    private void enableCreatePlanButton() {
        createPlanButton.setEnabled(validateFields(false));
    }

    protected double calculateTotalAmount(PendingBalanceDTO selectedBalance) {
        double totalAmount = 0;
        if (selectedBalance != null) {
            for (PendingBalancePayloadDTO balancePayloadDTO : selectedBalance.getPayload()) {
                if (StringUtil.isNullOrEmpty(balancePayloadDTO.getType()) || balancePayloadDTO.getType().equals(PATIENT_BALANCE)) {//prevent responsibility types from being added
                    totalAmount += balancePayloadDTO.getAmount();
                }
            }
        }
        return totalAmount;
    }

    private double calculateMonthlyPayment(int numberPayments) {
        double paymentAmount = paymentPlanAmount / numberPayments;
        return Math.ceil(paymentAmount * 100) / 100D;
    }

    private int calculatePaymentCount(double monthlyAmount) {
        double numberPayments = paymentPlanAmount / monthlyAmount;
        return (int) Math.ceil(numberPayments);
    }

    private void setLastPaymentMessage(double paymentAmount) {
        double remainder = (paymentPlanAmount * 100) % (paymentAmount * 100);
        if (remainder != 0) {
            double amount = Math.ceil(remainder) / 100D;
            lastPaymentMessage.setText(Label.getLabel("payment_last_adjustment_text") + " " + currencyFormatter.format(amount));
            lastPaymentMessage.setVisibility(View.VISIBLE);
        } else {
            lastPaymentMessage.setVisibility(View.INVISIBLE);
        }
    }

    protected void getPaymentPlanSettings(String practiceId) {
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()) {
                    double minAmount = balanceRangeRule.getMinBalance().getValue();
                    double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                    if (paymentPlanAmount >= minAmount && paymentPlanAmount <= maxAmount &&
                            minAmount > paymentPlanBalanceRules.getMinBalance().getValue()) {
                        paymentPlanBalanceRules = balanceRangeRule;
                    }
                }
                return;
            }
        }
    }

    protected List<DemographicsOption> generateDateOptions() {
        List<DemographicsOption> optionList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            DemographicsOption option = new DemographicsOption();
            option.setLabel(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(), i + 1));
            option.setName(String.valueOf(i + 1));

            optionList.add(option);
        }
        return optionList;
    }

    protected boolean validateFields(boolean isUserInteraction) {
        int paymentDay = 0;
        try {
            paymentDay = Integer.parseInt(paymentDateOption.getName());
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        if (paymentDay < 1 || paymentDay > 30) {
            if (isUserInteraction) {
                setError(R.id.paymentDrawDayInputLayout, Label.getLabel("validation_required_field"));
            }
            return false;
        } else {
            clearError(R.id.paymentDrawDayInputLayout);
        }

        if (monthlyPaymentCount < 2) {
            if (isUserInteraction) {
                setError(R.id.paymentMonthCountInputLayout,
                        String.format(Label.getLabel("payment_plan_min_months_error"),
                                String.valueOf(2)));
            }
            return false;
        } else {
            clearError(R.id.paymentMonthCountInputLayout);
        }

        if (monthlyPaymentCount > paymentPlanBalanceRules.getMaxDuration().getValue()) {
            if (isUserInteraction) {
                setError(R.id.paymentMonthCountInputLayout,
                        String.format(Label.getLabel("payment_plan_max_months_error"),
                                String.valueOf(paymentPlanBalanceRules.getMaxDuration().getValue())));
            }
            return false;
        } else {
            clearError(R.id.paymentMonthCountInputLayout);
        }

        if (monthlyPaymentAmount < paymentPlanBalanceRules.getMinPaymentRequired().getValue()) {
            if (isUserInteraction) {
                setError(R.id.paymentAmountInputLayout,
                        String.format(Label.getLabel("payment_plan_min_amount_error"),
                                currencyFormatter.format(paymentPlanBalanceRules.getMinPaymentRequired().getValue())));
            }
            return false;
        } else {
            clearError(R.id.paymentAmountInputLayout);
        }

        if (monthlyPaymentAmount > paymentPlanBalanceRules.getMaxBalance().getValue()) {
            if (isUserInteraction) {
                setError(R.id.paymentAmountInputLayout,
                        String.format(Label.getLabel("payment_plan_max_amount_error"),
                                currencyFormatter.format(paymentPlanBalanceRules.getMaxBalance().getValue())));
            }
            return false;
        } else {
            clearError(R.id.paymentAmountInputLayout);
        }


        return true;
    }

    protected void addBalanceToExisting(){
        callback.onAddBalanceToExitingPlan(paymentsModel, selectedBalance);
    }

    protected void createPaymentPlan() {
        if (validateFields(false)) {
            PaymentPlanPostModel postModel = new PaymentPlanPostModel();
            postModel.setMetadata(selectedBalance.getMetadata());
            postModel.setAmount(paymentPlanAmount);
            postModel.setDescription(planName.getText().toString());
            postModel.setLineItems(getPaymentPlanLineItems());

            PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
            paymentPlanModel.setAmount(monthlyPaymentAmount);
            paymentPlanModel.setFrequencyCode(PaymentPlanModel.FREQUENCY_MONTHLY);
            paymentPlanModel.setInstallments(monthlyPaymentCount);
            paymentPlanModel.setEnabled(true);

            try {
                paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            postModel.setPaymentPlanModel(paymentPlanModel);

            callback.onStartPaymentPlan(paymentsModel, postModel);
        }
    }

    protected List<PaymentPlanLineItem> getPaymentPlanLineItems() {
        double amountHolder = paymentPlanAmount;
        List<PaymentPlanLineItem> lineItems = new ArrayList<>();
        for (PendingBalancePayloadDTO pendingBalance : selectedBalance.getPayload()) {
            if (StringUtil.isNullOrEmpty(pendingBalance.getType()) || pendingBalance.getType().equals(PATIENT_BALANCE)) {//ignore responsibility types
                for (BalanceItemDTO balanceItem : pendingBalance.getDetails()) {
                    if (amountHolder <= 0) {
                        break;
                    }

                    PaymentPlanLineItem lineItem = new PaymentPlanLineItem();
                    lineItem.setDescription(balanceItem.getDescription());
                    lineItem.setType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                    lineItem.setTypeId(balanceItem.getId().toString());

                    if (amountHolder >= balanceItem.getAmount()) {
                        lineItem.setAmount(balanceItem.getAmount());
                        amountHolder -= balanceItem.getAmount();
                    } else {
                        lineItem.setAmount(amountHolder);
                        amountHolder = 0;
                    }

                    lineItems.add(lineItem);
                }
            }
        }
        return lineItems;
    }

    private void setError(int id, String error) {
        if (getView() != null) {
            TextInputLayout inputLayout = (TextInputLayout) getView().findViewById(id);
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(error);
        }
    }

    private void clearError(int id) {
        if (getView() != null) {
            TextInputLayout inputLayout = (TextInputLayout) getView().findViewById(id);
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }
    }

    private boolean hasExistingPlans() {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        for (PaymentPlanDTO paymentPlanDTO : paymentsModel.getPaymentPayload().getPatientPaymentPlans()) {
            if (paymentPlanDTO.getMetadata().getPracticeId() != null &&
                    paymentPlanDTO.getMetadata().getPracticeId().equals(practiceId)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAddToExisting() {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (settingsDTO.getMetadata().getPracticeId() != null &&
                    settingsDTO.getMetadata().getPracticeId().equals(practiceId)) {
                return settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting();
            }
        }
        return false;
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        callback.displayBalanceDetails(paymentsModel, paymentLineItem, selectedBalance);
    }

    private TextWatcher getOptionalViewTextWatcher(final View optionalView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    optionalView.setVisibility(View.VISIBLE);
                } else {
                    optionalView.setVisibility(View.GONE);
                }
                enableCreatePlanButton();
            }
        };
    }

    private TextWatcher getRequiredTextWatcher(final TextInputLayout inputLayout,
                                               final ValueInputCallback valueInputCallback) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString();
                if (StringUtil.isNullOrEmpty(input)) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                    valueInputCallback.onValueInput(input);
                    enableCreatePlanButton();
                }
            }
        };
    }

    private View.OnFocusChangeListener currencyFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(isCalculatingAmount || isCalclatingTime){
                return;
            }
            TextView textView = (TextView) view;
            isCalculatingAmount = true;
            if (!StringUtil.isNullOrEmpty(textView.getText().toString())) {
                if (hasFocus) {
                    try {
                        Number number = currencyFormatter.parse(textView.getText().toString());
                        textView.setText(String.valueOf(number.doubleValue()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        textView.setText(currencyFormatter.format(Double.parseDouble(textView.getText().toString())));
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
            }
            isCalculatingAmount = false;
        }
    };

    private void showChooseDialog(Context context,
                                  List<DemographicsOption> options,
                                  String title,
                                  final ValueInputCallback valueInputCallback) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // add cancel button
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        dialog.setView(customView);
        TextView titleTextView = (TextView) customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        // create the adapter
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        // set item click listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                DemographicsOption selectedOption = (DemographicsOption) adapterView.getAdapter()
                        .getItem(position);
                paymentDateOption = selectedOption;
                valueInputCallback.onValueInput(selectedOption.getLabel());
                alert.dismiss();
                enableCreatePlanButton();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    private interface ValueInputCallback {
        void onValueInput(String input);
    }

}
