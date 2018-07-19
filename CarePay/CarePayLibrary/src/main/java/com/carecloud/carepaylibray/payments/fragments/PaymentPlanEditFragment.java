package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.payments.utils.CreditCardUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.PayeezyRequestTask;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.payeezy.sdk.payeezytokenised.TransactionDataProvider;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pjohnson on 9/02/18.
 */

public class PaymentPlanEditFragment extends PaymentPlanFragment
        implements PayeezyRequestTask.AuthorizeCreditCardCallback,
        BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    protected PaymentPlanDTO paymentPlanDTO;
    private PaymentCreditCardsPayloadDTO creditCard;
    private boolean canEditPaymentPlan;
    protected PaymentPlanEditInterface callback;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the plan to be edited
     * @return an PaymentPlanFragment instance with the payment plan data filled for editing a payment plan
     */
    public static PaymentPlanEditFragment newInstance(PaymentsModel paymentsModel,
                                                      PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PaymentPlanEditFragment fragment = new PaymentPlanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        paymentPlanAmount = paymentPlanDTO.getPayload().getAmount();
        dateOptions = generateDateOptions();
        paymentDateOption = dateOptions.get(0);
        getPaymentPlanSettings(paymentPlanDTO.getMetadata().getPracticeId());
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        canEditPaymentPlan = checkCanEditPaymentPlan(paymentPlanDTO.getMetadata().getPracticeId());
        if(!canEditPaymentPlan){
            setValidationRules();
        }
    }


    @Override
    public void onViewCreated(final View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setUpUI(view);
    }

    private void setUpUI(final View view) {
        planNameEditText.setText(paymentPlanDTO.getPayload().getDescription());
        planNameEditText.getOnFocusChangeListener().onFocusChange(planNameEditText, true);

        paymentDateEditText.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth()));

        numberPaymentsEditText.setText(String.valueOf(paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments()));
        numberPaymentsEditText.getOnFocusChangeListener().onFocusChange(numberPaymentsEditText, true);

        monthlyPaymentEditText.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount()));
        createPlanButton.setText(Label.getLabel("save_button_label"));
        createPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(true)) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                    if (creditCard!=null && creditCard.getCreditCardsId() == null) {
                        authorizeCreditCard();
                    } else {
                        updatePaymentPlan();
                    }
                }
            }
        });
        TextView headerMessage = (TextView) view.findViewById(R.id.headerMessage);
        if (headerMessage != null) {
                headerMessage.setVisibility(View.GONE);
        }
        View addExistingPlan = view.findViewById(R.id.payment_plan_add_existing);
        if (addExistingPlan != null) {
            addExistingPlan.setVisibility(View.GONE);
        }
        setUpPaymentMethodLabel(view);
        isCalculatingTime = false;
        isCalculatingAmount = false;
        if(!canEditPaymentPlan){
            TextView parameters = (TextView) view.findViewById(R.id.paymentPlanParametersTextView);
            parameters.setText(Label.getLabel("payment_plan_edit_fields_disabled"));
            disableFields();
        }
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
        TextInputLayout paymentMethodInputLayout = (TextInputLayout) view.findViewById(R.id.paymentMethodInputLayout);
        EditText paymentMethodEditText = (EditText) view.findViewById(R.id.creditCardNumberTextView);
        paymentMethodEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(paymentMethodInputLayout, null));
        if (creditCard != null) {
            setCreditCardInfo(creditCard, paymentMethodEditText);
        }
        paymentMethodEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEditPaymentPlanPaymentMethod(paymentsModel);
            }
        });
    }

    private void setCreditCardInfo(PaymentCreditCardsPayloadDTO creditCard,
                                   EditText paymentMethodEditText) {

        String paymentMethodMessage = creditCard.getToken() != null ? CreditCardUtil.getCreditCardType(creditCard.getToken()) : null;
        if (paymentMethodMessage == null) {
            paymentMethodMessage = creditCard.getCardType();
        }
        paymentMethodEditText.setText(String.format("%s *** %s", paymentMethodMessage, creditCard.getCardNumber()));
        paymentMethodEditText.getOnFocusChangeListener().onFocusChange(paymentMethodEditText, true);

    }

    public void replacePaymentMethod(PaymentCreditCardsPayloadDTO creditCard) {
        EditText paymentMethodTextView = (EditText) getView().findViewById(R.id.creditCardNumberTextView);
        setCreditCardInfo(creditCard, paymentMethodTextView);
        this.creditCard = creditCard;
    }

    private void updatePaymentPlan() {
        PaymentPlanPostModel postModel = new PaymentPlanPostModel();
        postModel.setAmount(paymentPlanAmount);
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
                showProgressDialog();
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

        @IntegratedPaymentCardData.TokenizationService String tokenizationService = creditCardsPayloadDTO.getTokenizationService().toString();
        creditCardModel.setTokenizationService(tokenizationService);

        return creditCardModel;
    }

    private void authorizeCreditCard() {
        String currency = "USD";
        String cvv = creditCard.getCvv();
        String expiryDate = creditCard.getExpireDt();
        String name = creditCard.getNameOnCard();
        String cardType = creditCard.getCardType();
        String number = creditCard.getCompleteNumber();

        try {
            showProgressDialog();
            MerchantServiceMetadataDTO merchantServiceDTO = null;
            for (MerchantServicesDTO merchantService : paymentsModel.getPaymentPayload().getMerchantServices()) {
                if (merchantService.getName().toLowerCase().contains("payeezy")) {
                    merchantServiceDTO = merchantService.getMetadata();
                    break;
                }
            }

            String tokenUrl = merchantServiceDTO.getBaseUrl() + merchantServiceDTO.getUrlPath();
            if (!tokenUrl.endsWith("?")) {
                tokenUrl += "?";
            }

            TransactionDataProvider.tokenUrl = tokenUrl;
            TransactionDataProvider.appIdCert = merchantServiceDTO.getApiKey();
            TransactionDataProvider.secureIdCert = merchantServiceDTO.getApiSecret();
            TransactionDataProvider.tokenCert = merchantServiceDTO.getMasterMerchantToken();
            TransactionDataProvider.trTokenInt = merchantServiceDTO.getMasterTaToken();
            TransactionDataProvider.jsSecurityKey = merchantServiceDTO.getMasterJsSecurityKey();
            TransactionDataProvider.taToken = merchantServiceDTO.getMasterTaToken();

            String tokenType = merchantServiceDTO.getTokenType();
            String tokenAuth = merchantServiceDTO.getTokenizationAuth();
            PayeezyRequestTask requestTask = new PayeezyRequestTask(getContext(), this);
            requestTask.execute("gettokenvisa", tokenAuth, "", currency, tokenType, cardType, name,
                    number, expiryDate, cvv);
            System.out.println("first authorize call end");
        } catch (Exception e) {
            hideDialog();
            System.out.println(e.getMessage());
        }
        System.out.println("authorize call end");
    }

    @Override
    public void onAuthorizeCreditCard(String resString) {
        String valueString = "value";
        if (resString != null && resString.contains(valueString)) {

            String group1 = "(\\\"value\\\":\")";
            String group2 = "(\\d+)";
            String regex = group1 + group2;
            String tokenValue = null;
            Matcher matcher = Pattern.compile(regex).matcher(resString);
            if (matcher.find()) {
                tokenValue = matcher.group().replaceAll(group1, "");
            }

            if (tokenValue != null && tokenValue.matches(group2)) {
                creditCard.setToken(tokenValue);
                onAuthorizeCreditCardSuccess();
            } else {
                onAuthorizeCreditCardFailed();
            }

        } else {
            onAuthorizeCreditCardFailed();
        }
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
        SystemUtil.showErrorToast(getContext(), "Choose a different payment method");
    }

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCard);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PaymentsModel paymentsDto = new Gson().fromJson(workflowDTO.toString(), PaymentsModel.class);
            if (callback.getDto() instanceof PaymentsModel) {
                ((PaymentsModel) callback.getDto()).getPaymentPayload().setPatientCreditCards(paymentsDto.getPaymentPayload().getPatientCreditCards());
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

    private boolean checkCanEditPaymentPlan(String practiceId) {
        for (PaymentsPayloadSettingsDTO settings : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (settings.getMetadata().getPracticeId().equals(practiceId)) {
                for (PaymentSettingsBalanceRangeRule rules : settings.getPayload().getPaymentPlans().getBalanceRangeRules()) {
                    if (rules.getMaxBalance().getValue() >= paymentPlanDTO.getPayload().getAmount()
                            && rules.getMinBalance().getValue() <= paymentPlanDTO.getPayload().getAmount()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void disableFields(){
        numberPaymentsEditText.setEnabled(false);
        numberPaymentsEditText.setFocusable(false);
        numberPaymentsEditText.setFocusableInTouchMode(false);

        monthlyPaymentEditText.setEnabled(false);
        monthlyPaymentEditText.setFocusable(false);
        monthlyPaymentEditText.setFocusableInTouchMode(false);
    }

    private void setValidationRules(){
        paymentPlanBalanceRules.getMaxBalance().setValue(paymentPlanAmount);
        paymentPlanBalanceRules.getMinBalance().setValue(paymentPlanAmount);
        paymentPlanBalanceRules.getMaxDuration().setValue(paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments());
        paymentPlanBalanceRules.getMinPaymentRequired().setValue(paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount());
    }

}
