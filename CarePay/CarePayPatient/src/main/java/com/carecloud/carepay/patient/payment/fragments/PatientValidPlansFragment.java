package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;

import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-05-03.
 */
public class PatientValidPlansFragment extends ValidPlansFragment {

    public static PatientValidPlansFragment newInstance(PaymentsModel paymentsModel,
                                                        PendingBalanceDTO selectedBalance,
                                                        double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        PatientValidPlansFragment fragment = new PatientValidPlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onBackNavigationClick() {
        onBackPressed();
    }
}
