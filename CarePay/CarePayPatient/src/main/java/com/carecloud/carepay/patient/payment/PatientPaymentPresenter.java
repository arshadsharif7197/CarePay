package com.carecloud.carepay.patient.payment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentExceptionDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

/**
 * Created by lmenendez on 5/18/17
 */

public class PatientPaymentPresenter extends PaymentPresenter {

    public PatientPaymentPresenter(PaymentViewHandler viewHandler, PaymentsModel paymentsModel, String patientId) {
        super(viewHandler, paymentsModel, patientId);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        ResponsibilityFragment responsibilityFragment = ResponsibilityFragment.newInstance(paymentsModel, null, true);
        viewHandler.navigateToFragment(responsibilityFragment, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {
        new PartialPaymentDialog(viewHandler.getContext(), paymentsModel).show();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        viewHandler.navigateToFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount), true);
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
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        Fragment fragment = new AddNewCreditCardFragment();
        fragment.setArguments(args);
        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        viewHandler.exitPaymentProcess(false);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        viewHandler.exitPaymentProcess(true);
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()){
            if(userPracticeDTO.getPatientId()!=null && userPracticeDTO.getPatientId().equals(patientId)){
                return userPracticeDTO;
            }
        }
        return null;
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
        PaymentPlanFragment fragment = new PaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        viewHandler.navigateToFragment(fragment, true);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        PatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
        if(payload.getPaymentExceptions()!=null && !payload.getPaymentExceptions().isEmpty() && payload.getTotal()==0D){
            StringBuilder builder = new StringBuilder();
            for(PaymentExceptionDTO paymentException : payload.getPaymentExceptions()){
                builder.append(paymentException.getMessage());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            ((ISession)viewHandler.getContext()).showErrorNotification(builder.toString());
        }else {
            Bundle args = new Bundle();
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

            PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
            confirmationFragment.setArguments(args);
            viewHandler.displayDialogFragment(confirmationFragment, false);
        }
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }
}
