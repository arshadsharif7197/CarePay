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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextInputLayout;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO.PATIENT_BALANCE;

public class PaymentPlanFragment extends BasePaymentDialogFragment
        implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    protected static final String KEY_PLAN_AMOUNT = "plan_amount";
    protected PaymentsModel paymentsModel;
    protected PendingBalanceDTO selectedBalance;
    protected PaymentPlanCreateInterface callback;

    protected PaymentSettingsBalanceRangeRule paymentPlanBalanceRules = new PaymentSettingsBalanceRangeRule();

    protected NumberFormat currencyFormatter;
    protected double paymentPlanAmount;

    protected Button createPlanButton;
    protected EditText planNameEditText;
    protected EditText frequencyCodeEditText;
    private TextInputLayout paymentDayInputLayout;
    protected EditText paymentDateEditText;
    protected CarePayTextInputLayout installmentsInputLayout;
    protected EditText installmentsEditText;
    private CarePayTextInputLayout amountPaymentInputLayout;
    protected EditText amountPaymentEditText;
    private TextView lastPaymentMessage;

    protected List<DemographicsOption> frequencyOptions;
    protected List<DemographicsOption> dateOptions;
    protected List<DemographicsOption> dayOfWeekOptions;
    protected List<DemographicsOption> selectedDateOptions;
    protected DemographicsOption paymentDateOption;
    protected DemographicsOption frequencyOption;
    protected double amounthPayment;
    protected int installments;
    protected boolean applyRangeRules = true;

    protected boolean isCalculatingAmount = false;
    protected boolean isCalculatingTime = false;
    private String dialogTitle;
    private TextView parametersTextView;
    @PaymentSettingsBalanceRangeRule.IntervalRange
    protected String interval = PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS;
    protected String practiceId;

    /**
     * @param paymentsModel   the payment model
     * @param selectedBalance the selected balance
     * @return an empty PaymentPlanFragment instance for creating a new payment plan
     */
    public static PaymentPlanFragment newInstance(PaymentsModel paymentsModel,
                                                  PendingBalanceDTO selectedBalance,
                                                  double paymentPlanAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putDouble(KEY_PLAN_AMOUNT, paymentPlanAmount);

        PaymentPlanFragment fragment = new PaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                callback = (PaymentPlanCreateInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        //calculateTotalAmount(selectedBalance);
        paymentPlanAmount = calculateTotalAmount(args.getDouble(KEY_PLAN_AMOUNT, paymentPlanAmount));
        frequencyOptions = generateFrequencyOptions(paymentsModel.getPaymentPayload()
                .getPaymentSettings().get(0).getPayload().getPaymentPlans());
        if (selectedBalance != null) {
            practiceId = selectedBalance.getMetadata().getPracticeId();
            paymentPlanBalanceRules = getPaymentPlanSettings(null);
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
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

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
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
        TextView totalTextView = (TextView) view.findViewById(R.id.payment_plan_total);
        totalTextView.setText(currencyFormatter.format(paymentPlanAmount));

        parametersTextView = (TextView) view.findViewById(R.id.paymentPlanParametersTextView);
        if (parametersTextView != null && paymentPlanBalanceRules != null) {
            updatePaymentPlanParameters();
            parametersTextView.setVisibility(View.VISIBLE);
        }
    }

    private void updatePaymentPlanParameters() {
        String monthOrWeek = interval == PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS
                ? Label.getLabel("pluralRule.many.month") : Label.getLabel("pluralRule.many.week");
        String monthlyOrWeekly = interval == PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS
                ? Label.getLabel("payment.paymentPlan.frequency.option.monthly").toLowerCase()
                : Label.getLabel("payment.paymentPlan.frequency.option.weekly").toLowerCase();
        parametersTextView.setText(String.format(Locale.US, Label.getLabel("payment_plan_parameters_temporal"),
                paymentPlanBalanceRules.getMaxDuration().getValue(),
                monthOrWeek,
                monthlyOrWeekly,
                currencyFormatter.format(paymentPlanBalanceRules.getMinPaymentRequired().getValue())));
    }

    protected void setupFields(View view) {
        planNameEditText = (EditText) view.findViewById(R.id.paymentPlanName);
        TextInputLayout planNameInputLayout = (TextInputLayout) view.findViewById(R.id.paymentPlanNameInputLayout);
        planNameEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(planNameInputLayout, null));
        View planNameOptional = view.findViewById(R.id.paymentPlanNameOptional);
        planNameOptional.setVisibility(View.VISIBLE);
        planNameEditText.addTextChangedListener(getOptionalViewTextWatcher(planNameOptional));

        paymentDateEditText = (EditText) view.findViewById(R.id.paymentDrawDay);
        paymentDayInputLayout = (TextInputLayout) view.findViewById(R.id.paymentDrawDayInputLayout);
        paymentDateEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(paymentDayInputLayout, null));
        paymentDateEditText.setText(paymentDateOption.getLabel());
        paymentDateEditText.getOnFocusChangeListener().onFocusChange(paymentDateEditText, true);

        paymentDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog(getContext(), selectedDateOptions, dialogTitle,
                        new ValueOptionCallback() {
                            @Override
                            public void onValueOption(DemographicsOption option) {
                                paymentDateOption = option;
                                paymentDateEditText.setText(option.getLabel());
                            }
                        });
            }
        });

        frequencyCodeEditText = (EditText) view.findViewById(R.id.frequencyCodeEditText);
        TextInputLayout frequencyCodeInputLayout = (TextInputLayout) view.findViewById(R.id.frequencyCodeInputLayout);
        frequencyCodeEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(frequencyCodeInputLayout, null));
        frequencyCodeEditText.setText(frequencyOption.getLabel());
        frequencyCodeEditText.getOnFocusChangeListener().onFocusChange(frequencyCodeEditText, true);
        if (frequencyOptions.size() > 1) {
            frequencyCodeEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showChooseDialog(getContext(), frequencyOptions,
                            Label.getLabel("payment.paymentPlan.frequency.dialog.title"),
                            new ValueOptionCallback() {
                                @Override
                                public void onValueOption(DemographicsOption option) {
                                    if (!frequencyOption.getName().equals(option.getName())) {
                                        manageFrequencyChange(option, true);
                                    }

                                }
                            });
                }
            });
        } else {
            frequencyCodeEditText.setCompoundDrawables(null, null, null, null);
        }

        installmentsEditText = (EditText) view.findViewById(R.id.paymentMonthCount);
        installmentsInputLayout = (CarePayTextInputLayout) view
                .findViewById(R.id.paymentMonthCountInputLayout);
        installmentsInputLayout.setRequestFocusWhenError(false);
        installmentsEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(installmentsInputLayout, null));
        installmentsEditText.addTextChangedListener(getRequiredTextWatcher(installmentsInputLayout, new ValueInputCallback() {
            @Override
            public void onValueInput(String input) {
                if (!input.startsWith("0")) {
                    refreshNumberOfPayments(input);
                }
            }
        }));

        amountPaymentEditText = (EditText) view.findViewById(R.id.paymentAmount);
        amountPaymentInputLayout = (CarePayTextInputLayout) view
                .findViewById(R.id.paymentAmountInputLayout);
        amountPaymentInputLayout.setRequestFocusWhenError(false);
        amountPaymentEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(amountPaymentInputLayout, currencyFocusChangeListener));
        amountPaymentEditText.addTextChangedListener(getRequiredTextWatcher(amountPaymentInputLayout,
                new ValueInputCallback() {
                    @Override
                    public void onValueInput(String input) {
                        if (isCalculatingAmount) {
                            isCalculatingAmount = false;
                            return;
                        }
                        isCalculatingTime = true;
                        try {
                            amounthPayment = Double.parseDouble(input);
                            //make sure we don't consider extra decimals here.. these will get formatted out
                            amounthPayment = Math.round(amounthPayment * 100) / 100D;
                            installments = calculatePaymentCount(amounthPayment);
                            if (installments > 0) {
                                if (installmentsEditText.getOnFocusChangeListener() != null) {
                                    installmentsEditText.getOnFocusChangeListener()
                                            .onFocusChange(installmentsEditText, true);
                                }
                                installmentsEditText.setText(String.valueOf(installments));
                                setLastPaymentMessage(amounthPayment);
                            }
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                            isCalculatingTime = false;
                        }
                    }
                }));
        lastPaymentMessage = (TextView) view.findViewById(R.id.lastPaymentMessage);
        lastPaymentMessage.setVisibility(View.INVISIBLE);

        if (frequencyOption.getName().equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            paymentDayInputLayout.setHint(Label
                    .getLabel("payment.paymentPlan.frequency.monthly.hint"));
            installmentsInputLayout.setHint(Label.getLabel("payment_number_of_months"));
            amountPaymentInputLayout.setHint(Label.getLabel("payment_monthly_payment"));
            selectedDateOptions = dateOptions;
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS;
        } else {
            paymentDayInputLayout.setHint(Label
                    .getLabel("payment.paymentPlan.frequency.weekly.hint"));
            installmentsInputLayout.setHint(Label.getLabel("payment.paymentPlan.frequency.weekly.numberOfWeeks"));
            amountPaymentInputLayout.setHint(Label.getLabel("payment.paymentPlan.frequency.weekly.weeklyPayments"));
            selectedDateOptions = dayOfWeekOptions;
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_WEEKS;
        }
        updateHints();
    }

    protected void manageFrequencyChange(DemographicsOption option, boolean refresh) {
        frequencyOption = option;
        frequencyCodeEditText.setText(option.getLabel());
        if (option.getName().equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            paymentDateOption = dateOptions.get(0);
            dialogTitle = Label.getLabel("payment.paymentPlan.frequency.monthly.hint");
            paymentDayInputLayout.setHint(Label
                    .getLabel("payment.paymentPlan.frequency.monthly.hint"));
            installmentsInputLayout.setHint(Label.getLabel("payment_number_of_months"));
            amountPaymentInputLayout.setHint(Label.getLabel("payment_monthly_payment"));

            selectedDateOptions = dateOptions;
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS;
        } else {
            paymentDateOption = dayOfWeekOptions.get(0);
            dialogTitle = Label.getLabel("payment.paymentPlan.frequency.weekly.hint");
            paymentDayInputLayout.setHint(Label
                    .getLabel("payment.paymentPlan.frequency.weekly.hint"));
            installmentsInputLayout.setHint(Label.getLabel("payment.paymentPlan.frequency.weekly.numberOfWeeks"));
            amountPaymentInputLayout.setHint(Label.getLabel("payment.paymentPlan.frequency.weekly.weeklyPayments"));
            selectedDateOptions = dayOfWeekOptions;
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_WEEKS;
        }
        if (refresh) {
            resetInstallmentsAndAmountFields();
        }

        updateHints();
        if (applyRangeRules) {
            paymentPlanBalanceRules = getPaymentPlanSettings(interval);
        }
        if (parametersTextView != null) {
            updatePaymentPlanParameters();
        }
        paymentDateEditText.setText(paymentDateOption.getLabel());

    }

    private void resetInstallmentsAndAmountFields() {
        installmentsEditText.setText("");
        amountPaymentEditText.setText("");
        installments = 0;
        amounthPayment = 0;
        installmentsInputLayout.setErrorEnabled(false);
        amountPaymentInputLayout.setErrorEnabled(false);
    }

    private void updateHints() {
        installmentsEditText.setTag(null);
        installmentsEditText.getOnFocusChangeListener().onFocusChange(installmentsEditText,
                !StringUtil.isNullOrEmpty(installmentsEditText.getText().toString().trim()));
        paymentDateEditText.setTag(null);
        paymentDateEditText.getOnFocusChangeListener().onFocusChange(paymentDateEditText,
                !StringUtil.isNullOrEmpty(paymentDateEditText.getText().toString().trim()));
        amountPaymentEditText.setTag(null);
        amountPaymentEditText.getOnFocusChangeListener().onFocusChange(amountPaymentEditText,
                !StringUtil.isNullOrEmpty(amountPaymentEditText.getText().toString().trim()));
    }

    protected void refreshNumberOfPayments(String day) {
        if (isCalculatingTime) {
            isCalculatingTime = false;
            return;
        }
        isCalculatingAmount = true;
        try {
            installments = Integer.parseInt(day);
            amounthPayment = calculateMonthlyPayment(installments);
            if (amountPaymentEditText.getOnFocusChangeListener() != null) {
                amountPaymentEditText.getOnFocusChangeListener().onFocusChange(amountPaymentEditText, true);
            }
            amountPaymentEditText.setText(currencyFormatter.format(amounthPayment));
            setLastPaymentMessage(amounthPayment);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            isCalculatingAmount = false;
        }
    }

    protected void setupButtons(View view) {
        View addToExisting = view.findViewById(R.id.payment_plan_add_existing);
        if (selectedBalance != null && addToExisting != null) {
            if (enableAddToExisting()) {
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
        createPlanButton = (Button) view.findViewById(R.id.create_payment_plan_button);
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPaymentPlanPostModel(true);
                SystemUtil.hideSoftKeyboard(getContext(), view);
            }
        });
        createPlanButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    createPaymentPlanPostModel(true);
                    SystemUtil.hideSoftKeyboard(getContext(), buttonView);
                    return true;
                }
                return false;
            }
        });

        enableCreatePlanButton();
    }

    private void setAdapter(View view) {
        RecyclerView balanceRecycler = (RecyclerView) view.findViewById(R.id.balance_recycler);
        if (balanceRecycler != null && selectedBalance != null) {
            balanceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(),
                    selectedBalance.getPayload(), this);
            balanceRecycler.setAdapter(adapter);
        }
    }

    protected void enableCreatePlanButton() {
        boolean isEnabled = validateFields(false);
        getActionButton().setSelected(isEnabled);
        getActionButton().setClickable(isEnabled);
    }

    protected Button getActionButton() {
        return createPlanButton;
    }

    protected double calculateTotalAmount(double amount) {
        return amount;
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
            lastPaymentMessage.setText(Label.getLabel("payment_last_adjustment_text") + " "
                    + currencyFormatter.format(amount));
            lastPaymentMessage.setVisibility(View.VISIBLE);
        } else {
            lastPaymentMessage.setVisibility(View.INVISIBLE);
        }
    }

    protected PaymentSettingsBalanceRangeRule getPaymentPlanSettings(String interval) {
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                PaymentSettingsBalanceRangeRule temp = null;
                for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getPayload()
                        .getPaymentPlans().getBalanceRangeRules()) {
                    if ((interval == null) || interval.equals(balanceRangeRule.getMaxDuration().getInterval())) {
                        double minAmount = balanceRangeRule.getMinBalance().getValue();
                        double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                        double minTempValue = temp == null ? 0 : temp.getMinBalance().getValue();
                        if (paymentPlanAmount >= minAmount && paymentPlanAmount <= maxAmount &&
                                minAmount > minTempValue) {
                            temp = balanceRangeRule;
                        }
                    }
                }
                return temp;
            }
        }
        return null;
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

    protected List<DemographicsOption> generateDayOptions() {
        List<DemographicsOption> optionList = new ArrayList<>();
        DemographicsOption sunday = new DemographicsOption();
        sunday.setName(PaymentPlanModel.SUNDAY);
        sunday.setLabel(Label.getLabel("sunday"));
        DemographicsOption monday = new DemographicsOption();
        monday.setName(PaymentPlanModel.MONDAY);
        monday.setLabel(Label.getLabel("monday"));
        DemographicsOption tuesday = new DemographicsOption();
        tuesday.setName(PaymentPlanModel.TUESDAY);
        tuesday.setLabel(Label.getLabel("tuesday"));
        DemographicsOption wednesday = new DemographicsOption();
        wednesday.setName(PaymentPlanModel.WEDNESDAY);
        wednesday.setLabel(Label.getLabel("wednesday"));
        DemographicsOption thursday = new DemographicsOption();
        thursday.setName(PaymentPlanModel.THURSDAY);
        thursday.setLabel(Label.getLabel("thursday"));
        DemographicsOption friday = new DemographicsOption();
        friday.setName(PaymentPlanModel.FRIDAY);
        friday.setLabel(Label.getLabel("friday"));
        DemographicsOption saturday = new DemographicsOption();
        saturday.setName(PaymentPlanModel.SATURDAY);
        saturday.setLabel(Label.getLabel("saturday"));
        optionList.add(sunday);
        optionList.add(monday);
        optionList.add(tuesday);
        optionList.add(wednesday);
        optionList.add(thursday);
        optionList.add(friday);
        optionList.add(saturday);
        return optionList;
    }

    protected List<DemographicsOption> generateFrequencyOptions(PaymentsSettingsPaymentPlansDTO paymentPlansRules) {
        List<DemographicsOption> optionList = new ArrayList<>();
        if (selectedBalance != null) {
            practiceId = selectedBalance.getMetadata().getPracticeId();
        }
        PaymentSettingsBalanceRangeRule paymentSettings = getPaymentPlanSettings(PaymentSettingsBalanceRangeRule
                .INTERVAL_MONTHS);
        if ((paymentPlansRules.getFrequencyCode().getMonthly().isAllowed() && (paymentSettings != null))
                || !applyRangeRules) {
            DemographicsOption monthly = new DemographicsOption();
            monthly.setName(PaymentPlanModel.FREQUENCY_MONTHLY);
            monthly.setLabel(Label.getLabel("payment.paymentPlan.frequency.option.monthly"));
            optionList.add(monthly);
            dateOptions = generateDateOptions();
            paymentDateOption = dateOptions.get(0);
        }

        paymentSettings = getPaymentPlanSettings(PaymentSettingsBalanceRangeRule.INTERVAL_WEEKS);
        if ((paymentPlansRules.getFrequencyCode().getWeekly().isAllowed() && (paymentSettings != null))
                || !applyRangeRules) {
            DemographicsOption weekly = new DemographicsOption();
            weekly.setName(PaymentPlanModel.FREQUENCY_WEEKLY);
            weekly.setLabel(Label.getLabel("payment.paymentPlan.frequency.option.weekly"));
            optionList.add(weekly);
            dayOfWeekOptions = generateDayOptions();
            if (paymentDateOption == null) {
                paymentDateOption = dayOfWeekOptions.get(0);
            }
        }
        if (optionList.size() > 0) {
            frequencyOption = optionList.get(0);
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
                setError(R.id.paymentDrawDayInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
            }
            return false;
        } else {
            clearError(R.id.paymentDrawDayInputLayout);
        }

        if (paymentPlanBalanceRules != null) {
            String monthOrWeek = interval == PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS
                    ? Label.getLabel("pluralRule.many.month") : Label.getLabel("pluralRule.many.week");
            if (StringUtil.isNullOrEmpty(installmentsEditText.getText().toString())) {
                if (isUserInteraction) {
                    setError(installmentsInputLayout, Label.getLabel("validation_required_field")
                            , isUserInteraction);
                    return false;
                } else {
                    clearError(installmentsInputLayout);
                }
            } else if (installments < 2) {
                setError(installmentsInputLayout,
                        String.format(Label.getLabel("payment_plan_min_months_error_temporal"),
                                monthOrWeek,
                                String.valueOf(2))
                        , isUserInteraction);
                clearError(R.id.paymentAmountInputLayout);
                return false;
            } else if (installments > paymentPlanBalanceRules.getMaxDuration().getValue()) {

                setError(installmentsInputLayout,
                        String.format(Label.getLabel("payment_plan_max_months_error_temporal"),
                                monthOrWeek,
                                String.valueOf(paymentPlanBalanceRules.getMaxDuration().getValue()))
                        , isUserInteraction);
                clearError(R.id.paymentAmountInputLayout);
                return false;
            } else {
                clearError(installmentsInputLayout);
            }

            if (StringUtil.isNullOrEmpty(amountPaymentEditText.getText().toString())) {
                if (isUserInteraction) {
                    setError(R.id.paymentAmountInputLayout, Label.getLabel("validation_required_field")
                            , isUserInteraction);
                    return false;
                } else {
                    clearError(R.id.paymentAmountInputLayout);
                }
            } else if (amounthPayment < paymentPlanBalanceRules.getMinPaymentRequired().getValue()) {
                if (isUserInteraction) {
                    setError(R.id.paymentAmountInputLayout,
                            String.format(Label.getLabel("payment_plan_min_amount_error"),
                                    currencyFormatter.format(paymentPlanBalanceRules.getMinPaymentRequired().getValue()))
                            , isUserInteraction);
                }
                return false;
            } else {
                clearError(R.id.paymentAmountInputLayout);
            }

            if (amounthPayment > paymentPlanBalanceRules.getMaxBalance().getValue()) {
                if (isUserInteraction) {
                    setError(R.id.paymentAmountInputLayout,
                            String.format(Label.getLabel("payment_plan_max_amount_error"),
                                    currencyFormatter.format(paymentPlanBalanceRules.getMaxBalance().getValue()))
                            , isUserInteraction);
                }
                return false;
            } else {
                clearError(R.id.paymentAmountInputLayout);
            }
        }
        return true;
    }

    protected void addBalanceToExisting() {
        callback.onAddBalanceToExistingPlan(paymentsModel, selectedBalance, paymentPlanAmount);
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {practiceId, paymentPlanAmount, true};
        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    protected void createPaymentPlanPostModel(boolean userInteraction) {
        if (validateFields(userInteraction)) {
            PaymentPlanPostModel postModel = new PaymentPlanPostModel();
            postModel.setMetadata(selectedBalance.getMetadata());
            postModel.setAmount(paymentPlanAmount);
            postModel.setDescription(planNameEditText.getText().toString());
            postModel.setLineItems(getPaymentPlanLineItems());

            PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
            paymentPlanModel.setAmount(amounthPayment);
            paymentPlanModel.setFrequencyCode(frequencyOption.getName());
            paymentPlanModel.setInstallments(installments);
            paymentPlanModel.setEnabled(true);

            if (frequencyOption.getName().equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
                try {
                    paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            } else {
                try {
                    paymentPlanModel.setDayOfWeek(Integer.parseInt(paymentDateOption.getName()));
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }

            postModel.setPaymentPlanModel(paymentPlanModel);
            createPaymentPlanNextStep(postModel);
        }
    }

    protected void createPaymentPlanNextStep(PaymentPlanPostModel postModel) {
        callback.onStartPaymentPlan(paymentsModel, postModel);
    }

    protected List<PaymentPlanLineItem> getPaymentPlanLineItems() {
        double amountHolder = paymentPlanAmount;
        List<PaymentPlanLineItem> lineItems = new ArrayList<>();
        for (PendingBalancePayloadDTO pendingBalance : selectedBalance.getPayload()) {
            if (StringUtil.isNullOrEmpty(pendingBalance.getType())
                    || pendingBalance.getType().equals(PATIENT_BALANCE)) {//ignore responsibility types
                for (BalanceItemDTO balanceItem : pendingBalance.getDetails()) {
                    if (amountHolder <= 0) {
                        break;
                    }

                    PaymentPlanLineItem lineItem = new PaymentPlanLineItem();
                    lineItem.setDescription(balanceItem.getDescription());
                    lineItem.setType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                    lineItem.setTypeId(balanceItem.getId().toString());

                    if (balanceItem.getBalance() > 0) {
                        if (amountHolder >= balanceItem.getBalance()) {
                            lineItem.setAmount(balanceItem.getBalance());
                            amountHolder = SystemUtil.safeSubtract(amountHolder, balanceItem.getBalance());
                        } else {
                            lineItem.setAmount(amountHolder);
                            amountHolder = 0;
                        }

                        lineItems.add(lineItem);
                    }
                }
            }
        }
        return lineItems;
    }

    protected void setError(int id, String error, boolean requestFocus) {
        if (getView() != null) {
            TextInputLayout inputLayout = (TextInputLayout) getView().findViewById(id);
            setError(inputLayout, error, requestFocus);
        }
    }

    protected void setError(TextInputLayout inputLayout, String errorMessage, boolean requestFocus) {
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(errorMessage);
        if (requestFocus) {
            inputLayout.requestFocus();
        }

    }

    protected void clearError(int id) {
        if (getView() != null) {
            TextInputLayout inputLayout = (TextInputLayout) getView().findViewById(id);
            clearError(inputLayout);
        }
    }

    protected void clearError(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    private boolean hasExistingPlans() {
        for (PaymentPlanDTO paymentPlanDTO : paymentsModel.getPaymentPayload().getActivePlans(practiceId)) {
            if (paymentPlanDTO.getMetadata().getPracticeId() != null &&
                    paymentPlanDTO.getMetadata().getPracticeId().equals(practiceId)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAddToExisting() {
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (settingsDTO.getMetadata().getPracticeId() != null &&
                    settingsDTO.getMetadata().getPracticeId().equals(practiceId)) {
                return settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting();
            }
        }
        return false;
    }

    protected boolean enableAddToExisting() {
        return hasExistingPlans() && canAddToExisting()
                && !paymentsModel.getPaymentPayload().getValidPlans(practiceId,
                selectedBalance.getPayload().get(0).getAmount()).isEmpty();
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
                    setError(inputLayout, Label
                            .getLabel("demographics_required_validation_msg"), false);
                } else if (input.startsWith("0")) {
                    editable.clear();
                } else {
                    valueInputCallback.onValueInput(input);
                    enableCreatePlanButton();
                }
            }
        };
    }

    private View.OnFocusChangeListener currencyFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (isCalculatingAmount || isCalculatingTime) {
                return;
            }
            EditText editText = (EditText) view;
            isCalculatingAmount = true;
            if (!StringUtil.isNullOrEmpty(editText.getText().toString())) {
                if (hasFocus) {
                    try {
                        Number number = currencyFormatter.parse(editText.getText().toString());

                        editText.setText(String.valueOf(number.doubleValue()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        editText.setText(currencyFormatter.format(Double.parseDouble(editText.getText().toString())));
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
                                  final ValueOptionCallback valueInputCallback) {

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
                valueInputCallback.onValueOption(selectedOption);
                alert.dismiss();
                enableCreatePlanButton();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    private interface ValueInputCallback {
        void onValueInput(String input);
    }

    private interface ValueOptionCallback {
        void onValueOption(DemographicsOption option);
    }

}
