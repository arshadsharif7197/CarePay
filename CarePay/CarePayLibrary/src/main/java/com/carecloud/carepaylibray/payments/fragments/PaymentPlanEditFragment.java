package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsToggleOption;
import com.carecloud.carepaylibray.payeeze.PayeezyCall;
import com.carecloud.carepaylibray.payeeze.model.CreditCard;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.payments.utils.CreditCardUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 9/02/18.
 */

public abstract class PaymentPlanEditFragment extends PaymentPlanFragment
        implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    protected PaymentPlanDTO paymentPlanDTO;
    protected PaymentCreditCardsPayloadDTO creditCard;
    private boolean canEditPaymentPlan;
    protected PaymentPlanEditInterface callback;
    protected Button editPaymentPlanButton;
    protected EditText paymentMethodEditText;

    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanEditInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                callback = (PaymentPlanEditInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        Bundle args = getArguments();
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        practiceId = paymentPlanDTO.getMetadata().getPracticeId();
        super.onCreate(icicle);
        setInterval();
        paymentPlanBalanceRules = getPaymentPlanSettings(interval);
        canEditPaymentPlan = frequencyOption.isEnabled();
        if (!canEditPaymentPlan) {
            stubRangeRules();
        }
    }

    @Override
    protected double calculateTotalAmount(double amount) {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(),
                paymentPlanDTO.getPayload().getAmountPaid());
    }

    private void setInterval() {
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS;
        } else {
            interval = PaymentSettingsBalanceRangeRule.INTERVAL_WEEKS;
        }
    }


    @Override
    public void onViewCreated(final View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setUpUI(view);
        enableCreatePlanButton();
    }

    private void setUpUI(final View view) {
        planNameEditText.setText(paymentPlanDTO.getPayload().getDescription());
        planNameEditText.getOnFocusChangeListener().onFocusChange(planNameEditText, true);

        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            paymentDateEditText.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                    paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth()));
        } else {
            paymentDateEditText.setText(StringUtil
                    .getDayOfTheWeek(paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfWeek()));
        }

        TextView headerMessage = view.findViewById(R.id.headerMessage);
        if (headerMessage != null) {
            headerMessage.setVisibility(View.GONE);
        }
        View addExistingPlan = view.findViewById(R.id.payment_plan_add_existing);
        if (addExistingPlan != null) {
            addExistingPlan.setVisibility(View.GONE);
        }
        setUpPaymentMethodLabel(view);

        resetPlanParameters();

        toggleFields(canEditPaymentPlan);

    }

    private void resetPlanParameters() {
        installments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() -
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        installmentsEditText.setText(String.valueOf(installments));
        installmentsEditText.getOnFocusChangeListener().onFocusChange(installmentsEditText, true);

        amounthPayment = paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount();
        amountPaymentEditText.setText(currencyFormatter.format(amounthPayment));
        isCalculatingTime = false;
        isCalculatingAmount = false;
    }

    @Override
    protected void setupButtons(final View view) {
        editPaymentPlanButton = view.findViewById(R.id.editPaymentPlanButton);
        super.setupButtons(view);
        createPlanButton.setVisibility(View.GONE);
        view.findViewById(R.id.editButtonsLayout).setVisibility(View.VISIBLE);
        editPaymentPlanButton.setOnClickListener(view1 -> {
            if (validateFields(true)) {
                SystemUtil.hideSoftKeyboard(getContext(), view1);
                showProgressDialog();

                if (creditCard != null && creditCard.getCreditCardsId() == null) {
                    authorizeCreditCard();
                } else {
                    updatePaymentPlan();
                }
            }
        });
        Button cancelPaymentPlanButton = view.findViewById(R.id.cancelPaymentPlanButton);
        boolean deletePaymentPlan = false;
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanHistoryList().isEmpty()) {
            deletePaymentPlan = true;
            cancelPaymentPlanButton.setText(Label.getLabel("payment.editPaymentPlan.delete.button.label"));
        }
        final boolean finalDeletePaymentPlan = deletePaymentPlan;
        cancelPaymentPlanButton.setOnClickListener(v -> showCancelPaymentPlanConfirmDialog(finalDeletePaymentPlan));
        cancelPaymentPlanButton.setVisibility(canCancelPlan(paymentPlanDTO.getMetadata().getPracticeId())
                ? View.VISIBLE : View.GONE);
        enableCreatePlanButton();
    }

    @Override
    protected void enableCreatePlanButton() {
        boolean isEnabled = validateFields(false);
        getActionButton().setSelected(isEnabled);
        getActionButton().setClickable(isEnabled);
    }

    protected void showCancelPaymentPlanConfirmDialog(final boolean deletePaymentPlan) {
        callback.showCancelPaymentPlanConfirmDialog(() -> cancelPaymentPlan(deletePaymentPlan), deletePaymentPlan);
    }

    protected void cancelPaymentPlan(final boolean deletePaymentPlan) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getDeletePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payment_plans_cancelled), 1);
                dismiss();
                callback.onPaymentPlanCanceled(workflowDTO, deletePaymentPlan);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                SystemUtil.showErrorToast(getContext(), exceptionMessage);

            }
        }, queryMap);
    }

    private void setUpPaymentMethodLabel(View view) {
        PaymentCreditCardsPayloadDTO creditCard = null;
        for (PaymentsPatientsCreditCardsPayloadListDTO creditCardModel :
                paymentsModel.getPaymentPayload().getPatientCreditCards()) {
            if (paymentPlanDTO.getPayload().getPaymentMethod().getPapiPaymentID()
                    .equals(creditCardModel.getPayload().getCreditCardsId())) {
                creditCard = creditCardModel.getPayload();
                break;
            }
        }
        View paymentMethodContainer = view.findViewById(R.id.paymentMethodContainer);
        paymentMethodContainer.setVisibility(View.VISIBLE);
        TextInputLayout paymentMethodInputLayout = view.findViewById(R.id.paymentMethodInputLayout);
        paymentMethodEditText = view.findViewById(R.id.creditCardNumberTextView);
        paymentMethodEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(paymentMethodInputLayout, null));
        if (creditCard != null) {
            setCreditCardInfo(creditCard, paymentMethodEditText);
        }
        paymentMethodEditText.setOnClickListener(v -> {
            // hide keyboard
            SystemUtil.hideSoftKeyboard(getActivity());
            onEditPaymentPlanPaymentMethod(paymentsModel, paymentPlanDTO);
        });
    }

    protected abstract void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    private void setCreditCardInfo(PaymentCreditCardsPayloadDTO creditCard,
                                   EditText paymentMethodEditText) {

        String paymentMethodMessage = creditCard.getToken() != null ? CreditCardUtil
                .getCreditCardType(creditCard.getToken()) : null;
        if (paymentMethodMessage == null) {
            paymentMethodMessage = creditCard.getCardType();
        }
        paymentMethodEditText.setText(String.format("%s ****%s", paymentMethodMessage, creditCard.getCardNumber()));
        paymentMethodEditText.getOnFocusChangeListener().onFocusChange(paymentMethodEditText, true);

    }

    public void replacePaymentMethod(PaymentCreditCardsPayloadDTO creditCard) {
        setCreditCardInfo(creditCard, paymentMethodEditText);
        this.creditCard = creditCard;
        showDialog();
        enableCreatePlanButton();
    }

    protected void updatePaymentPlan() {
        PaymentPlanPostModel postModel = new PaymentPlanPostModel();
        postModel.setAmount(paymentPlanDTO.getPayload().getAmount());
        postModel.setExecution(paymentPlanDTO.getPayload().getExecution());
        postModel.setLineItems(paymentPlanDTO.getPayload().getLineItems());
        postModel.setDescription(planNameEditText.getText().toString());

        if (creditCard != null) {
            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            if (creditCard.getCreditCardsId() == null) {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                papiPaymentMethod.setCardData(getCreditCardModel(creditCard));
            } else {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
                papiPaymentMethod.setPapiPaymentID(creditCard.getCreditCardsId());
            }
            postModel.setPapiPaymentMethod(papiPaymentMethod);
        } else {
            postModel.setPapiPaymentMethod(paymentPlanDTO.getPayload().getPaymentMethod());
        }

        PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
        paymentPlanModel.setAmount(amounthPayment);
        paymentPlanModel.setFrequencyCode(frequencyOption.getName());
        paymentPlanModel.setInstallments((int) SystemUtil.safeAdd(installments,
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size()));
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

        callEditPaymentPlanService(postModel);
    }

    private void callEditPaymentPlanService(PaymentPlanPostModel postModel) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getUpdatePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());


        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
//                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                dismiss();
                callback.onPaymentPlanEdited(workflowDTO);
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
            creditCard.setToken(creditCard.getCompleteNumber());
            onAuthorizeCreditCardSuccess();
            return;
        }
        String cvv = creditCard.getCvv();
        String expiryDate = creditCard.getExpireDt();
        String name = creditCard.getNameOnCard();
        String cardType = creditCard.getCardType();
        String number = creditCard.getCompleteNumber();

        MerchantServiceMetadataDTO merchantServiceDTO = null;
        for (MerchantServicesDTO merchantService : paymentsModel.getPaymentPayload().getMerchantServices()) {
            if (merchantService.getName().toLowerCase().contains("payeezy")) {
                merchantServiceDTO = merchantService.getMetadata();
                break;
            }
        }

        final CreditCard creditCardDto = new CreditCard();
        creditCardDto.setCardHolderName(name);
        creditCardDto.setCardNumber(number);
        creditCardDto.setCvv(cvv);
        creditCardDto.setExpDate(expiryDate);
        creditCardDto.setType(cardType);

        PayeezyCall payeezyCall = new PayeezyCall();
        payeezyCall.doCall(creditCardDto, merchantServiceDTO, tokenizeResponse -> {
//            hideProgressDialog();
            if (tokenizeResponse != null) {
                if (tokenizeResponse.getToken() != null) {
                    creditCard.setToken(tokenizeResponse.getToken().getValue());
                    onAuthorizeCreditCardSuccess();
                } else {
                    onAuthorizeCreditCardFailed();
                }
            } else {
                onAuthorizeCreditCardFailed();
            }
        });

        System.out.println("authorize call end");
    }

    @Override
    public void onAuthorizeCreditCardSuccess() {
        if (creditCard.isSaveCardOnFile()) {
            addNewCreditCardCall();
        } else {
            updatePaymentPlan();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        hideProgressDialog();
        SystemUtil.showErrorToast(getContext(), Label.getLabel("payment_invalid_cc_error_text"));
    }

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCard);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body,
                getWorkflowServiceHelper().getPreferredLanguageHeader());
    }

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
//            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
//            hideProgressDialog();
            PaymentsModel paymentsDto = new Gson().fromJson(workflowDTO.toString(), PaymentsModel.class);
            if (callback.getDto() instanceof PaymentsModel) {
                ((PaymentsModel) callback.getDto()).getPaymentPayload().setPatientCreditCards(paymentsDto
                        .getPaymentPayload().getPatientCreditCards());
            }
            updatePaymentPlan();
            MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showErrorToast(getContext(), exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private boolean canCancelPlan(String practiceId) {
        PaymentsPayloadSettingsDTO paymentSettings = paymentsModel.getPaymentPayload()
                .getPaymentSetting(practiceId);
        if (paymentSettings == null) {
            return false;
        }
        PaymentsSettingsPaymentPlansDTO paymentPlanSettings = paymentSettings.getPayload().getPaymentPlans();
        return paymentPlanSettings.isCanCancelPlan();
    }

    private void toggleFields(boolean enabled) {
        if (!enabled) {
            parametersTextView.setText(Label.getLabel("payment_plan_edit_fields_disabled"));
        }

        installmentsEditText.setEnabled(enabled);
        installmentsEditText.setFocusable(enabled);
        installmentsEditText.setFocusableInTouchMode(enabled);

        amountPaymentEditText.setEnabled(enabled);
        amountPaymentEditText.setFocusable(enabled);
        amountPaymentEditText.setFocusableInTouchMode(enabled);

    }

    private void stubRangeRules() {
        //stub rules when no active rule is found for editing
        if (paymentPlanBalanceRules == null) {
            paymentPlanBalanceRules = new PaymentSettingsBalanceRangeRule();
        }
        paymentPlanBalanceRules.getMaxBalance().setValue(paymentPlanAmount);
        paymentPlanBalanceRules.getMinBalance().setValue(paymentPlanAmount);
        paymentPlanBalanceRules.getMaxDuration().setValue(paymentPlanDTO.getPayload()
                .getPaymentPlanDetails().getInstallments());
        paymentPlanBalanceRules.getMinPaymentRequired().setValue(paymentPlanDTO.getPayload()
                .getPaymentPlanDetails().getAmount());
    }

    @Override
    protected Button getActionButton() {
        return editPaymentPlanButton;
    }

    @Override
    protected List<DemographicsToggleOption> generateFrequencyOptions(PaymentsSettingsPaymentPlansDTO paymentPlansRules) {
        List<DemographicsToggleOption> options = super.generateFrequencyOptions(paymentPlansRules);
        if (!frequencyOptionsContainsPaymentPlanFrequency(options)) {
            //add it as a disabled option
            frequencyOption = new DemographicsToggleOption();
            frequencyOption.setLabel(StringUtil.capitalize(paymentPlanDTO.getPayload()
                    .getPaymentPlanDetails().getFrequencyCode()));
            frequencyOption.setName(paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode());
            frequencyOption.setEnabled(paymentsModel.getPaymentPayload()
                    .hasApplicableRule(paymentPlansRules, paymentPlanAmount,
                            paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()));
            options.add(frequencyOption);
        } else {
            for (DemographicsToggleOption frequencyOption : options) {
                if (frequencyOption.getName()
                        .equals(paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode())) {
                    this.frequencyOption = frequencyOption;
                }
            }

        }

        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            dateOptions = generateDateOptions();
            paymentDateOption = dateOptions.get(paymentPlanDTO.getPayload()
                    .getPaymentPlanDetails().getDayOfMonth() - 1);
            selectedDateOptions = dateOptions;
        } else {
            dayOfWeekOptions = generateDayOptions();
            if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfWeek() > dayOfWeekOptions.size()) {
                paymentDateOption = dayOfWeekOptions.get(0);
            } else {
                paymentDateOption = dayOfWeekOptions.get(paymentPlanDTO.getPayload()
                        .getPaymentPlanDetails().getDayOfWeek());
            }
            selectedDateOptions = dayOfWeekOptions;
        }

        return options;
    }

    private boolean frequencyOptionsContainsPaymentPlanFrequency(List<DemographicsToggleOption> frequencyOptions) {
        for (DemographicsOption frequency : frequencyOptions) {
            if (frequency.getName().equals(paymentPlanDTO.getPayload()
                    .getPaymentPlanDetails().getFrequencyCode())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void manageFrequencyChange(DemographicsToggleOption option, boolean refresh) {
        super.manageFrequencyChange(option, refresh);
        toggleFields(option.isEnabled());
        enableCreatePlanButton();
        if (!option.isEnabled()) {
            stubRangeRules();
        }
        if (option.getName().equals(paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode())) {
            resetPlanParameters();
        }
    }

    @Override
    protected boolean validateFields(boolean isUserInteraction) {
        boolean passesStandardValidation = super.validateFields(isUserInteraction);

        boolean samePaymentDate;
        if (paymentDateOption.getName() != null && frequencyOption.getName().equals(paymentPlanDTO
                .getPayload().getPaymentPlanDetails().getFrequencyCode())) {
            if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                    .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
                samePaymentDate = paymentDateOption.getName().equals(dateOptions.get(paymentPlanDTO
                        .getPayload().getPaymentPlanDetails().getDayOfMonth() - 1).getName());
            } else {
                samePaymentDate = paymentDateOption.getName().equals(dayOfWeekOptions.get(paymentPlanDTO
                        .getPayload().getPaymentPlanDetails().getDayOfWeek()).getName());
            }
        } else {
            samePaymentDate = true;
        }

        boolean sameCard = creditCard != null && paymentPlanDTO.getPayload().getPaymentMethod().getPapiPaymentID()
                .equals(creditCard.getCreditCardsId());

        boolean sameName = planNameEditText.getText().toString().equals(paymentPlanDTO.getPayload().getDescription());

        boolean metadataUnModified = (samePaymentDate && creditCard == null &&
                (StringUtil.isNullOrEmpty(planNameEditText.getText().toString()) || sameName)) ||
                (samePaymentDate && sameName && sameCard);

        int completedInstallments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        boolean parametersModified = installments + completedInstallments != paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() ||
                amounthPayment != paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount();
        if (!parametersModified) {
            //clear any errors in these fields if parameters have not been modified
            clearError(R.id.paymentMonthCountInputLayout);
            clearError(R.id.paymentAmountInputLayout);
        }

        if (!parametersModified) {
            clearError(R.id.paymentMonthCountInputLayout);
            clearError(R.id.paymentAmountInputLayout);
        }

        return passesStandardValidation && parametersModified || (!metadataUnModified && !parametersModified);
    }

}
