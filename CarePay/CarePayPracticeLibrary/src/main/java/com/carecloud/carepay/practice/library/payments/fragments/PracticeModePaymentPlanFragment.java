package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentPlanItemAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.payeeze.PayeezyCall;
import com.carecloud.carepaylibray.payeeze.model.CreditCard;
import com.carecloud.carepaylibray.payments.adapter.CreditCardsListAdapter;
import com.carecloud.carepaylibray.payments.fragments.BaseAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 4/17/18
 */

public class PracticeModePaymentPlanFragment extends PaymentPlanFragment
        implements CreditCardsListAdapter.CreditCardSelectionListener,
        PaymentPlanItemAdapter.PaymentPlanItemInterface,
        BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    protected PaymentCreditCardsPayloadDTO selectedCreditCard;
    protected List<BalanceItemDTO> balanceItems = new ArrayList<>();
    protected TextView paymentValueTextView;
    private PaymentPlanItemAdapter adapter;
    protected double totalBalance;
    protected double minAmount = 0.1;
    private CreditCardsListAdapter creditCardsListAdapter;
    private Button addNewCardButton;

    public static PracticeModePaymentPlanFragment newInstance(PaymentsModel paymentsModel,
                                                              PendingBalanceDTO selectedBalance) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);

        PracticeModePaymentPlanFragment fragment = new PracticeModePaymentPlanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        applyRangeRules = false;
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, getArguments());
        selectedBalance = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        paymentPlanAmount = 0.00;
        return inflater.inflate(R.layout.fragment_practice_create_payment_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupToolbar(view, Label.getLabel("payment.createPaymentPlan.title.label.title"));
        setUpAmounts(view);
        balanceItems = filterItems();
        setUpItems(view, balanceItems);
        setupButtons(view);
        setupFields(view);
        setUpCreditCards(view);
        isCalculatingTime = false;
    }

    protected void setupToolbar(View view, String titleString) {
        Toolbar toolbar = view.findViewById(R.id.payment_toolbar);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.toolbar_title);
            title.setText(titleString);
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(v -> cancel());
    }

    protected void setUpAmounts(View view) {
        double unAppliedCredit = selectedBalance.getPayload().get(0).getUnappliedCredit() * -1;
        TextView unAppliedValueTextView = view.findViewById(R.id.unAppliedValueTextView);
        unAppliedValueTextView.setText(currencyFormatter.format(unAppliedCredit));
        if (unAppliedCredit < 0.01) {
            unAppliedValueTextView.setVisibility(View.GONE);
            view.findViewById(R.id.unAppliedColon).setVisibility(View.GONE);
            view.findViewById(R.id.unAppliedLabel).setVisibility(View.GONE);
            view.findViewById(R.id.balanceLabelSeparator).setVisibility(View.GONE);
        }

        TextView balanceValueTextView = view.findViewById(R.id.balanceValueTextView);
        totalBalance = getTotalBalance(selectedBalance);
        balanceValueTextView.setText(currencyFormatter.format(totalBalance));

        paymentValueTextView = view.findViewById(R.id.paymentValueTextView);
        paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
    }

    protected void setUpItems(View view, List<BalanceItemDTO> items) {
        RecyclerView itemsRecycler = view.findViewById(R.id.itemRecycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentPlanItemAdapter(items);
        adapter.setCallback(this);
        itemsRecycler.setAdapter(adapter);
    }

    @Override
    protected void setupButtons(final View view) {
        super.setupButtons(view);
        createPlanButton.setOnClickListener(v -> {
            if (validateFields(true)) {
                SystemUtil.hideSoftKeyboard(getContext(), view);
                if (selectedCreditCard != null && selectedCreditCard.getCreditCardsId() == null) {
                    authorizeCreditCard();
                } else {
                    mainButtonAction();
                }
            }
        });
        addNewCardButton = view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(v -> onAddPaymentPlanCard(paymentsModel, null, true));
        if (paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            addNewCardButton.setText(Label.getLabel("payment_new_credit_card"));
        }
    }

    protected void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                        PaymentPlanDTO paymentPlanDTO,
                                        boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, (PaymentPlanDTO) null, onlySelectMode);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, false);
        hideDialog();
    }

    protected void mainButtonAction() {
        createPaymentPlanPostModel(true);
    }

    private double getTotalBalance(PendingBalanceDTO selectedBalance) {
        return SystemUtil.safeSubtract(selectedBalance.getPayload().get(0).getAmount(),
                selectedBalance.getPayload().get(0).getPaymentPlansAmount());
    }

    protected List<BalanceItemDTO> filterItems() {
        balanceItems = getLineItems();
        List<BalanceItemDTO> filteredList = new ArrayList<>();
        for (BalanceItemDTO balanceItem : balanceItems) {
            String balanceItemId = String.valueOf(balanceItem.getId());
            balanceItem.setAmountInPaymentPlan(0.0);
            for (PaymentPlanDTO paymentPlan : paymentsModel.getPaymentPayload().getActivePlans(practiceId)) {
                for (PaymentPlanLineItem lineItem : paymentPlan.getPayload().getLineItems()) {
                    if (balanceItemId.equals(lineItem.getTypeId())) {
                        if (balanceItem.getAmountInPaymentPlan() > 0.0) {
                            balanceItem.setInMoreThanOnePaymentPlan(true);
                        }
                        balanceItem.setAmountInPaymentPlan(SystemUtil.safeSubtract(
                                SystemUtil.safeAdd(balanceItem.getAmountInPaymentPlan(), lineItem.getAmount()),
                                lineItem.getAmountPaid()));
                    }
                }
            }
            if ((balanceItem.getBalance() > 0)
                    && (balanceItem.getBalance() != balanceItem.getAmountInPaymentPlan())) {
                filteredList.add(balanceItem);
            }
        }
        return filteredList;
    }

    protected List<BalanceItemDTO> getLineItems() {
        List<BalanceItemDTO> balanceItems = new ArrayList<>();
        for (PendingBalancePayloadDTO pendingBalancePayload : selectedBalance.getPayload()) {
            switch (pendingBalancePayload.getType()) {
                case PendingBalancePayloadDTO.CO_PAY_TYPE:
                case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE: {
                    double amount = pendingBalancePayload.getAmount();
                    BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
                    balanceItemDTO.setBalance(amount);
                    balanceItemDTO.setAmount(amount);
                    balanceItemDTO.setDescription(pendingBalancePayload.getType());

                    balanceItems.add(balanceItemDTO);
                    break;
                }
                default: {
                    balanceItems.addAll(pendingBalancePayload.getDetails());
                }
            }
        }
        return balanceItems;
    }

    protected void setUpCreditCards(View view) {
        RecyclerView creditCardsRecyclerView = view.findViewById(R.id.creditCardRecycler);
        creditCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = paymentsModel
                .getPaymentPayload().getPatientCreditCards();
        if (creditCardList != null) {
            selectDefaultCreditCard(creditCardList);
        }
        creditCardsListAdapter = new CreditCardsListAdapter(getContext(),
                creditCardList, this, true);
        creditCardsListAdapter.setSelectedCreditCard(selectedCreditCard);
        creditCardsRecyclerView.setAdapter(creditCardsListAdapter);
    }

    protected void selectDefaultCreditCard(List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList) {
        for (PaymentsPatientsCreditCardsPayloadListDTO creditCard : creditCardList) {
            if (creditCard.getPayload().isDefault()) {
                selectedCreditCard = creditCard.getPayload();
            }
        }
    }

    @Override
    protected void setupHeader(View view) {
    }

    @Override
    protected boolean enableAddToExisting() {
        return false;
    }

    @Override
    protected boolean validateFields(boolean isUserInteraction) {
        int paymentDay = 0;

        if (paymentPlanAmount <= minAmount) {
            return false;
        }

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

        String monthOrWeek = interval == PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS
                ? Label.getLabel("pluralRule.many.month") : Label.getLabel("pluralRule.many.week");
        if (StringUtil.isNullOrEmpty(installmentsEditText.getText().toString())) {
            if (isUserInteraction) {
                setError(installmentsInputLayout, Label.getLabel("validation_required_field")
                        , isUserInteraction);
                return false;
            } else {
                clearError(installmentsInputLayout);
                return false;
            }
        } else if (installments < 2) {
            setError(installmentsInputLayout,
                    String.format(Label.getLabel("payment_plan_min_months_error_temporal"),
                            monthOrWeek,
                            String.valueOf(2))
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
                return false;
            }
        } else {
            clearError(R.id.paymentAmountInputLayout);
        }

        if (selectedCreditCard == null) {
            return false;
        }

        Date expDate = DateUtil.getInstance().setDateRaw(selectedCreditCard.getExpireDt()).getDate();
        expDate = DateUtil.getLastDayOfMonth(expDate);
        expDate = DateUtil.getLastHourOfDay(expDate);
        return !expDate.before(new Date());
    }

    @Override
    public void onCreditCardItemSelected(PaymentCreditCardsPayloadDTO creditCard) {
        selectedCreditCard = creditCard;
        enableCreatePlanButton();
    }

    private static double round(double amount) {
        return (double) Math.round(amount * 100) / 100;
    }

    @Override
    public boolean onItemChecked(BalanceItemDTO item, boolean checked) {
        if (checked) {
            double amountSelected = SystemUtil.safeSubtract(item.getBalance(),
                    item.getAmountInPaymentPlan());
            if (SystemUtil.safeAdd(amountSelected, paymentPlanAmount) > totalBalance) {
                showSelectAmountFragment(item, totalBalance);
                return false;
            }
            item.setAmountSelected(amountSelected);
            paymentPlanAmount = SystemUtil.safeAdd(paymentPlanAmount, item.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
        } else {
            paymentPlanAmount = SystemUtil.safeSubtract(paymentPlanAmount, item.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
            item.setAmountSelected(0.00);
        }
        refreshNumberOfPayments(installmentsEditText.getText().toString());
        return true;
    }

    @Override
    public void onAddToPlanClicked(BalanceItemDTO item) {
        showSelectAmountFragment(item, totalBalance);
    }

    @Override
    protected List<PaymentPlanLineItem> getPaymentPlanLineItems() {
        List<PaymentPlanLineItem> paymentPlanItems = new ArrayList<>();
        for (BalanceItemDTO item : balanceItems) {
            if (item.getAmountSelected() > 0.0) {
                PaymentPlanLineItem lineItem = new PaymentPlanLineItem();
                lineItem.setDescription(item.getDescription());
                lineItem.setType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                lineItem.setTypeId(item.getId().toString());
                lineItem.setAmount(item.getAmountSelected());
                paymentPlanItems.add(lineItem);
            }
        }
        return paymentPlanItems;
    }

    protected void showSelectAmountFragment(final BalanceItemDTO itemDTO, double total) {
        hideDialog();
        SelectAmountFragment fragment = SelectAmountFragment
                .newInstance(Math
                        .min(SystemUtil.safeSubtract(itemDTO.getBalance(), itemDTO.getAmountInPaymentPlan()),
                                SystemUtil.safeSubtract(total,
                                        SystemUtil.safeSubtract(paymentPlanAmount, itemDTO.getAmountSelected()))));
        fragment.setCallback(amount -> {
            paymentPlanAmount = SystemUtil.safeSubtract(paymentPlanAmount, itemDTO.getAmountSelected());
            itemDTO.setAmountSelected(amount);
            itemDTO.setSelected(true);
            paymentPlanAmount = SystemUtil.safeAdd(paymentPlanAmount, itemDTO.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
            adapter.notifyDataSetChanged();
            refreshNumberOfPayments(String.valueOf(installments));
            enableCreatePlanButton();
        });
        fragment.setOnCancelListener(dialog -> showDialog());
        callback.displayDialogFragment(fragment, true);

        /*   String tag = fragment.getClass().getName();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        fragment.setCallback(amount -> {
            paymentPlanAmount = SystemUtil.safeSubtract(paymentPlanAmount, itemDTO.getAmountSelected());
            itemDTO.setAmountSelected(amount);
            itemDTO.setSelected(true);
            paymentPlanAmount = SystemUtil.safeAdd(paymentPlanAmount, itemDTO.getAmountSelected());
            paymentValueTextView.setText(currencyFormatter.format(paymentPlanAmount));
            adapter.notifyDataSetChanged();
            refreshNumberOfPayments(String.valueOf(installments));
            enableCreatePlanButton();
        });
        fragment.show(ft, tag);*/
    }

    @Override
    protected void createPaymentPlanNextStep(PaymentPlanPostModel postModel) {
        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        if (selectedCreditCard.getCreditCardsId() == null) {
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel(selectedCreditCard));
        } else {
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
            papiPaymentMethod.setPapiPaymentID(selectedCreditCard.getCreditCardsId());
        }
        postModel.setPapiPaymentMethod(papiPaymentMethod);
        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        callCreatePaymentPlanService(postModel);
    }

    private void callCreatePaymentPlanService(PaymentPlanPostModel postModel) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getCreatePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", postModel.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", postModel.getMetadata().getPracticeId());
        queryMap.put("patient_id", postModel.getMetadata().getPatientId());


        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                dismiss();
                callback.onSubmitPaymentPlan(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                SystemUtil.showErrorToast(getContext(), exceptionMessage);

            }
        }, new Gson().toJson(postModel), queryMap);
    }

    protected IntegratedPaymentCardData getCreditCardModel(PaymentCreditCardsPayloadDTO creditCardsPayloadDTO) {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setSaveCard(creditCardsPayloadDTO.isSaveCardOnFile());
        creditCardModel.setDefault(creditCardsPayloadDTO.isDefaultCardChecked());
        creditCardModel.setCvv(creditCardsPayloadDTO.getCvv());
        creditCardModel.setCard_number(creditCardsPayloadDTO.getCompleteNumber());

        @IntegratedPaymentCardData.TokenizationService String tokenizationService = creditCardsPayloadDTO
                .getTokenizationService().toString();
        creditCardModel.setTokenizationService(tokenizationService);

        return creditCardModel;
    }

    protected void authorizeCreditCard() {
        String pm = getApplicationPreferences().getStartPracticeManagement();
        if (pm != null && pm.equalsIgnoreCase(Defs.START_PM_TALKEHR)) {
            // No need to Tokenize with Payeezy call
            selectedCreditCard.setToken(selectedCreditCard.getCompleteNumber());
            onAuthorizeCreditCardSuccess();
            return;
        }
        String cvv = selectedCreditCard.getCvv();
        String expiryDate = selectedCreditCard.getExpireDt();
        String name = selectedCreditCard.getNameOnCard();
        String cardType = selectedCreditCard.getCardType();
        String number = selectedCreditCard.getCompleteNumber();

        MerchantServiceMetadataDTO merchantServiceDTO = null;
        for (MerchantServicesDTO merchantService : paymentsModel.getPaymentPayload().getMerchantServices()) {
            if (merchantService.getName().toLowerCase().contains("payeezy")) {
                merchantServiceDTO = merchantService.getMetadata();
                break;
            }
        }

        CreditCard creditCard = new CreditCard();
        creditCard.setCardHolderName(name);
        creditCard.setCardNumber(number);
        creditCard.setCvv(cvv);
        creditCard.setExpDate(expiryDate);
        creditCard.setType(cardType);

        showProgressDialog();
        PayeezyCall payeezyCall = new PayeezyCall();
        payeezyCall.doCall(creditCard, merchantServiceDTO, tokenizeResponse -> {
            if (tokenizeResponse != null) {
                if (tokenizeResponse.getToken() != null) {
                    selectedCreditCard.setToken(tokenizeResponse.getToken().getValue());
                    onAuthorizeCreditCardSuccess();
                } else {
                    onAuthorizeCreditCardFailed();
                }
            } else {
                onAuthorizeCreditCardFailed();
            }
        });
    }

    @Override
    public void onAuthorizeCreditCardSuccess() {
        if (selectedCreditCard.isSaveCardOnFile()) {
            addNewCreditCardCall();
        } else {
            mainButtonAction();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        hideProgressDialog();
        SystemUtil.showErrorToast(getContext(), "Choose a different payment method");
    }

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(selectedCreditCard);
        Map<String, String> queryMap = new HashMap<>();
        PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
        queryMap.put("practice_mgmt", metadata.getPracticeMgmt());
        queryMap.put("practice_id", metadata.getPracticeId());
        queryMap.put("patient_id", metadata.getPatientId());

        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, queryMap,
                getWorkflowServiceHelper().getPreferredLanguageHeader());
    }

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            mainButtonAction();
            MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showErrorToast(getContext(), exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    public void replacePaymentMethod(PaymentCreditCardsPayloadDTO creditCardPayload) {
        if (creditCardPayload.isDefault()) {
            for (PaymentsPatientsCreditCardsPayloadListDTO creditCard : paymentsModel
                    .getPaymentPayload().getPatientCreditCards()) {
                creditCard.getPayload().setDefault(false);
            }
        }
        PaymentsPatientsCreditCardsPayloadListDTO card = new PaymentsPatientsCreditCardsPayloadListDTO();
        card.setPayload(creditCardPayload);
        paymentsModel.getPaymentPayload().getPatientCreditCards().add(card);
        creditCardsListAdapter.setSelectedCreditCard(creditCardPayload);
        creditCardsListAdapter.notifyDataSetChanged();
        selectedCreditCard = creditCardPayload;
        enableCreatePlanButton();
        if (addNewCardButton == null) {
            addNewCardButton = getView().findViewById(R.id.addNewCardButton);
        }
        addNewCardButton.setText(Label.getLabel("payment_add_new_credit_card_button"));
    }
}
