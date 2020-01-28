package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2020-01-28.
 */
public class PrepaymentPracticeAddNewCreditCardFragment extends PracticeAddNewCreditCardFragment {

    private AppointmentPrepaymentCallback appointmentPrepaymentCallback;

    public static PrepaymentPracticeAddNewCreditCardFragment newInstance(PaymentsModel paymentsDTO, double amount) {
        Bundle args = new Bundle();
        PrepaymentPracticeAddNewCreditCardFragment fragment = new PrepaymentPracticeAddNewCreditCardFragment();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppointmentPrepaymentCallback) {
            appointmentPrepaymentCallback = (AppointmentPrepaymentCallback) context;
        }
    }

    @Override
    protected void onFailureAction(ServerErrorDTO serverErrorDto) {
        if ("504".equals(serverErrorDto.getMessage().getBody().getError().getErrorCode())) {
            appointmentPrepaymentCallback.onPrepaymentFailed();
        }
    }
}
