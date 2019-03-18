package com.carecloud.carepay.patient.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAmountDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.android.gms.wallet.MaskedWallet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 5/18/17
 */

public class PatientPaymentPresenter extends PaymentPresenter
        implements PatientPaymentMethodInterface, PaymentPlanCompletedInterface,
        PaymentPlanCreateInterface {
    private Fragment androidPayTargetFragment;
    private boolean paymentPlanCreated;

    public PatientPaymentPresenter(PaymentViewHandler viewHandler, PaymentsModel paymentsModel, String patientId) {
        super(viewHandler, paymentsModel, patientId);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        ResponsibilityFragment responsibilityFragment = ResponsibilityFragment.newInstance(paymentsModel, null, true);
        viewHandler.navigateToFragment(responsibilityFragment, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PartialPaymentDialog dialog=  PartialPaymentDialog.newInstance(paymentsModel, selectedBalance);
        viewHandler.displayDialogFragment(dialog,false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                getString(R.string.param_practice_id),
                selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        viewHandler.navigateToFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount, false), true);

        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };
        Object[] values = {amount,
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(viewHandler.getContext(), workflowDTO);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            viewHandler.navigateToFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsModel, amount);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        viewHandler.exitPaymentProcess(false, paymentPlanCreated, true);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        viewHandler.exitPaymentProcess(true, paymentPlanCreated, false);

        double amount = 0D;
        for(PendingBalancePayloadDTO balancePayloadDTO : pendingBalanceDTO.getPayload()){
            amount += balancePayloadDTO.getAmount();
        }

        MixPanelUtil.logEvent(getString(R.string.event_payment_skipped), getString(R.string.param_balance_amount), amount);
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        String practiceId = null;
        toplevel:
        for(PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()){
                if(patientId != null && patientId.equals(pendingBalanceDTO.getMetadata().getPatientId())){
                    practiceId = pendingBalanceDTO.getMetadata().getPracticeId();
                    break toplevel;
                }
            }
        }
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO;
            }
        }
        return new UserPracticeDTO();
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return viewHandler.getAppointmentId();
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return viewHandler.getAppointment();
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedBalancesItem = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);//this should be a safe assumption for checkin
        PendingBalanceDTO reducedBalancesItem = paymentsModel.getPaymentPayload().reduceBalanceItems(selectedBalancesItem, false);
        PaymentPlanAmountDialog dialog = PaymentPlanAmountDialog.newInstance(paymentsModel, reducedBalancesItem);
        viewHandler.displayDialogFragment(dialog, false);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {
        boolean addExisting = false;
        if(paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)){
            onAddBalanceToExistingPlan(paymentsModel, selectedBalance, amount);
            addExisting = true;
        } else {
            PaymentPlanFragment fragment = PaymentPlanFragment.newInstance(paymentsModel, selectedBalance, amount);
            viewHandler.navigateToFragment(fragment, true);
        }

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                addExisting};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            ((ISession) viewHandler.getContext()).showErrorNotification(builder.toString());
        } else {
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment.newInstance(workflowDTO);
            viewHandler.displayDialogFragment(confirmationFragment, false);
        }
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(viewHandler.getContext(), Label.getLabel("payment_queued_patient"), CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
        viewHandler.exitPaymentProcess(false, paymentPlanCreated, true);
    }

    @Override
    public void setAndroidPayTargetFragment(Fragment fragment) {
        androidPayTargetFragment = fragment;
    }

    @Override
    public Fragment getAndroidPayTargetFragment() {
        return androidPayTargetFragment;
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void createWalletFragment(MaskedWallet maskedWallet, Double amount) {
        viewHandler.navigateToFragment(AndroidPayDialogFragment.newInstance(maskedWallet, paymentsModel, amount), true);
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getString(int id){
        return viewHandler.getContext().getString(id);
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment.newInstance(paymentsModel, paymentPlanPostModel);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        ((Activity)viewHandler.getContext()).onBackPressed();
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod, PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PaymentPlanChooseCreditCardFragment fragment = PaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel, onlySelectMode);
            viewHandler.navigateToFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel, onlySelectMode);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanTermsFragment fragment = PaymentPlanTermsFragment.newInstance(paymentsModel, paymentPlanPostModel);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getPracticeInfo(paymentsModel),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        viewHandler.displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, double amount) {
        ValidPlansFragment fragment = ValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO selectedPlan, double amount) {
        AddExistingPaymentPlanFragment fragment = AddExistingPaymentPlanFragment.newInstance(paymentsModel, selectedBalance, selectedPlan, amount);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, selectedBalance, false);
        viewHandler.displayDialogFragment(dialog, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getPracticeInfo(paymentsModel),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        viewHandler.displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put("payment_plan", "true");

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        viewHandler.getISession().getWorkflowServiceHelper().execute(transition, completePlanCallback, queryMap);

    }

    private WorkflowServiceCallback completePlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            viewHandler.getISession().showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            viewHandler.getISession().hideProgressDialog();
            Bundle info = new Bundle();
            if(getAppointment() != null) {
                DtoHelper.bundleDto(info, getAppointment());
            }
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            paymentPlanCreated = paymentsModel.getPaymentPayload().isPaymentPlanCreated();
            if (workflowDTO.getState().equals(NavigationStateConstants.PAYMENTS)) {
                PatientNavigationHelper.navigateToWorkflow(viewHandler.getContext(), workflowDTO, info);
            } else {
                viewHandler.exitPaymentProcess(false, paymentPlanCreated, false);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            viewHandler.getISession().hideProgressDialog();
            viewHandler.getISession().showErrorNotification(exceptionMessage);
        }
    };

}
