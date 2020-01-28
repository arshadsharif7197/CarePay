package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2020-01-28.
 */
public class PrepaymentAddNewCreditCardFragment extends AddNewCreditCardFragment {

    private AppointmentPrepaymentCallback appointmentPrepaymentCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckOutInterface) {
            appointmentPrepaymentCallback = (AppointmentPrepaymentCallback) context;
        }
    }

    public static PrepaymentAddNewCreditCardFragment newInstance(PaymentsModel paymentsDTO, double amount) {
        Bundle args = new Bundle();
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DtoHelper.bundleDto(args, paymentsDTO);
        PrepaymentAddNewCreditCardFragment addNewCreditCardFragment = new PrepaymentAddNewCreditCardFragment();
        addNewCreditCardFragment.setArguments(args);
        return addNewCreditCardFragment;
    }

    @Override
    protected void onFailureAction(ServerErrorDTO serverErrorDto) {
        if ("504".equals(serverErrorDto.getMessage().getBody().getError().getErrorCode())) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            appointmentPrepaymentCallback.onPrepaymentFailed();
        }
    }
}
