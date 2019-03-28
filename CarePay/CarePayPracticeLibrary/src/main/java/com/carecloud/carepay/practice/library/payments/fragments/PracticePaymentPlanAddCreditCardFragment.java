package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Date;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanAddCreditCardFragment extends PaymentPlanAddCreditCardFragment {

    private PaymentPlanCreateInterface callback;

    /**
     * @param paymentsModel        payment model
     * @param paymentPlanPostModel payment plan post model
     * @return new instance
     */
    public static PracticePaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, paymentPlanPostModel.getAmount());
        PracticePaymentPlanAddCreditCardFragment fragment = new PracticePaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  payment model
     * @param paymentPlanDTO payment plan details
     * @param onlySelectMode select mode
     * @return new instance
     */
    public static PracticePaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanDTO paymentPlanDTO,
                                                                       boolean onlySelectMode) {
        return newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, null);
    }

    /**
     * @param paymentsModel  payment model
     * @param paymentPlanDTO payment plan details
     * @param onlySelectMode select mode
     * @param paymentDate    paymentDate
     * @return new instance
     */
    public static PracticePaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanDTO paymentPlanDTO,
                                                                       boolean onlySelectMode,
                                                                       Date paymentDate) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        if (paymentPlanDTO != null) {
            DtoHelper.bundleDto(args, paymentPlanDTO);
        }
        if (paymentDate != null) {
            DateUtil.getInstance().setDate(paymentDate);
            args.putString(KEY_DATE, DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PracticePaymentPlanAddCreditCardFragment fragment = new PracticePaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanCreateInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE) ||
                HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_2_DEVICE);
        Button swipeCardButton = view.findViewById(R.id.swipeCreditCarNowButton);
        if(isCloverDevice && paymentPlanDTO != null && (paymentDate == null || DateUtil.isToday(paymentDate))) {
            swipeCardButton.setVisibility(View.VISIBLE);
        } else {
            swipeCardButton.setVisibility(View.GONE);
        }

        //temp fix for hiding this until we can handle it properly for one-time payment TODO: CLOVERPAY remove this
        swipeCardButton.setVisibility(View.GONE);

    }

    @Override
    protected void authorizeOrSelectCreditCard() {
        if (onlySelectMode) {
            creditCardsPayloadDTO.setCompleteNumber(creditCardNoEditText.getText().toString().replace(" ", "").trim());
            creditCardsPayloadDTO.setDefault(setAsDefaultCheckBox.isChecked());
            creditCardsPayloadDTO.setSaveCardOnFile(saveCardOnFileCheckBox.isChecked());
            dismiss();
            callback.onCreditCardSelected(creditCardsPayloadDTO);
        } else {
            authorizeCreditCard();
        }
    }

    @Override
    protected LargeAlertDialogFragment.LargeAlertInterface getLargeAlertInterface() {
        if (largeAlertInterface != null) {
            dismiss();
            return largeAlertInterface;
        } else {
            return super.getLargeAlertInterface();
        }
    }

    @Override
    protected void showConfirmation(WorkflowDTO workflowDTO){
        ((OneTimePaymentInterface)callback).showPaymentConfirmation(workflowDTO, true);
    }

    @Override
    protected void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanTermsFragment fragment = PracticePaymentPlanTermsFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, false);
        hideDialog();
    }

}
