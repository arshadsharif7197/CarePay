package com.carecloud.carepaylibray.payments.newfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

/**
 * Created by lmenendez on 3/1/17
 */
public class AddNewCreditCardFragment extends BaseAddCreditCardFragment
        implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    private UserPracticeDTO userPracticeDTO;

    public static AddNewCreditCardFragment newInstance(String practiceId, double amount) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PRACTICE_ID, practiceId);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        AddNewCreditCardFragment addNewCreditCardFragment = new AddNewCreditCardFragment();
        addNewCreditCardFragment.setArguments(args);
        return addNewCreditCardFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentConfirmationInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentConfirmationInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
//            if (paymentsModel != null) {
            if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
                addressPayloadDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                        .getDemographics().getPayload().getAddress();
            }
            userPracticeDTO = viewModel.getPracticeInfo(arguments.getString(CarePayConstants.PRACTICE_ID));
//            }
        }
        setUpViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAuthorizeCallback(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        title.setText(Label.getLabel("payment_new_credit_card"));
        nextButton.setText(Label.getLabel("add_credit_card_save_button_label"));
    }

    private void setUpViewModel() {
        viewModel.getCreateCreditCardObservable().observe(getActivity(), aVoid -> {
            nextButton.setEnabled(true);
            makePaymentCall();
            MixPanelUtil.logEvent(getString(R.string.event_updated_credit_cards));
        });

        viewModel.getMakePaymentFromCreateCardObservable().observe(getActivity(), paymentsModel1 -> {
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                logEventPaymentFailedMixPanel();
                viewModel.setErrorMessage(payload.getProcessingErrors().get(0).getError());
                if (getDialog() != null) {
                    dismiss();
                }
            } else {
                if (getDialog() != null) {
                    dismiss();
                }
                logEventPaymentCompleteMixPanel();
//                showConfirmation(workflowDTO);
            }
        });
        viewModel.getPaymentErrorFromCreateCardObservable().observe(getActivity(),
                aVoid -> {
                    logEventPaymentFailedMixPanel();
                    nextButton.setEnabled(true);
                });
    }

    @Override
    public void onAuthorizeCreditCardSuccess() {
        nextButton.setEnabled(true);
        if (saveCardOnFileCheckBox.isChecked()) {
            viewModel.addNewCreditCardCall(creditCardsPayloadDTO);
        } else {
            makePaymentCall();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        nextButton.setEnabled(true);
        LargeAlertDialogFragment fragment = LargeAlertDialogFragment
                .newInstance(Label.getLabel("payment_failed_error"),
                        Label.getLabel("payment_change_payment_label"),
                        R.color.Feldgrau, R.drawable.icn_card_error, 18);
        fragment.setLargeAlertInterface(getLargeAlertInterface());
        fragment.show(getChildFragmentManager(), LargeAlertDialogFragment.class.getName());
    }

    protected void makePaymentCall() {
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel != null && postModel.getAmount() > 0) {
            processPayment(postModel);
        } else {
            processPayment();
        }
    }

    private void processPayment(IntegratedPaymentPostModel postModel) {
        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        postModel.setPapiPaymentMethod(papiPaymentMethod);

        IntegratedPaymentMetadata integratedPaymentMetadata = postModel.getMetadata();
        if (viewModel.getAppointment().getPayload().getId() != null
                && integratedPaymentMetadata.getAppointmentRequestDTO() == null) {
            integratedPaymentMetadata.setAppointmentId(viewModel.getAppointment().getPayload().getId());
        }

        viewModel.postPaymentFromNewCard(postModel, userPracticeDTO.getPracticeId());
        logPaymentStartedEventMixPanel();
    }

    private void processPayment() {
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amountToMakePayment);
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
        paymentLineItem.setDescription("Unapplied Amount");

        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        postModel.setPapiPaymentMethod(papiPaymentMethod);
        postModel.setAmount(amountToMakePayment);
        postModel.addLineItem(paymentLineItem);

        IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
        postModelMetadata.setAppointmentId(viewModel.getAppointment().getPayload().getId());

        viewModel.postPaymentFromNewCard(postModel, userPracticeDTO.getPracticeId());
        logPaymentStartedEventMixPanel();
    }

    protected IntegratedPaymentCardData getCreditCardModel() {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setSaveCard(saveCardOnFileCheckBox.isChecked());
        creditCardModel.setDefault(setAsDefaultCheckBox.isChecked());

        @IntegratedPaymentCardData.TokenizationService String tokenizationService = creditCardsPayloadDTO
                .getTokenizationService().toString();
        creditCardModel.setTokenizationService(tokenizationService);

        return creditCardModel;
    }

    protected LargeAlertDialogFragment.LargeAlertInterface getLargeAlertInterface() {
        return () -> {
            callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
            dismiss();
        };
    }

    protected void showConfirmation(WorkflowDTO workflowDTO) {
        callback.showPaymentConfirmation(workflowDTO);
    }

    private void logPaymentStartedEventMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_started), params, values);
    }

    private void logEventPaymentCompleteMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_complete), params, values);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_completed), 1);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.total_payments_amount), amountToMakePayment);
    }

    private void logEventPaymentFailedMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
    }

}
