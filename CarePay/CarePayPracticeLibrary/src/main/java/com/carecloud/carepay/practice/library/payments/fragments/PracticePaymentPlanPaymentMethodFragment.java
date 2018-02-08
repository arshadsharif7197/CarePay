package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.List;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanPaymentMethodFragment extends PracticePaymentMethodDialogFragment {

    private PaymentPlanInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;

    /**
     * @param paymentsModel the payments DTO
     * @param paymentPlanPostModel post model for payment plan to execute
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PracticePaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PracticePaymentPlanPaymentMethodFragment fragment = new PracticePaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel the payments DTO
     * @param paymentPlanDTO existing payment plan to make payment for
     * @return an instance of PracticePaymentPlanPaymentMethodFragment
     */
    public static PracticePaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PracticePaymentPlanPaymentMethodFragment fragment = new PracticePaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            }else if (context instanceof AppointmentViewHandler){
                callback = (PaymentPlanInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }


    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        switch (paymentMethod.getType()) {
            case CarePayConstants.TYPE_CASH:
                super.handlePaymentButton(paymentMethod, amount);
                break;
            case CarePayConstants.TYPE_CREDIT_CARD:
                if(paymentPlanPostModel != null) {
                    callback.onSelectPaymentPlanMethod(paymentMethod, paymentsModel, paymentPlanPostModel);
                }
                if(paymentPlanDTO != null){
                    callback.onSelectPaymentPlanMethod(paymentMethod, paymentsModel, paymentPlanDTO);
                }
                logPaymentMethodSelection(getString(R.string.payment_credit_card));
                dismiss();
                break;
            default:
        }
    }

    @Override
    protected List<PaymentsMethodsDTO> getPaymentMethodList() {
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        for(PaymentsPayloadSettingsDTO paymentSetting : paymentsModel.getPaymentPayload().getPaymentSettings()){
            if(paymentSetting.getMetadata().getPracticeId().equals(userPracticeDTO.getPracticeId()) &&
                    paymentSetting.getMetadata().getPracticeMgmt().equals(userPracticeDTO.getPracticeMgmt())){
                return paymentSetting.getPayload().getPaymentPlans().getPaymentMethods();
            }
        }
        return paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getPaymentPlans().getPaymentMethods();
    }

}
