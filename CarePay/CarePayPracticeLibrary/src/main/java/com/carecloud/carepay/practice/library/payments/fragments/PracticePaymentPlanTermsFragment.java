package com.carecloud.carepay.practice.library.payments.fragments;


import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanTermsFragment extends PaymentPlanTermsFragment {

    /**
     * Constructor
     *
     * @param paymentsModel        payment model
     * @param paymentPlanPostModel post model
     * @return new instance
     */
    public static PracticePaymentPlanTermsFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PracticePaymentPlanTermsFragment fragment = new PracticePaymentPlanTermsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        View close = view.findViewById(R.id.closeViewLayout);
        if (close != null) {
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    callback.onStartPaymentPlan(paymentsModel, paymentPlanPostModel);
                }
            });
        }

    }

    protected void onPaymentPlanSubmitted(WorkflowDTO workflowDTO) {
        super.onPaymentPlanSubmitted(workflowDTO);
        dismiss();
    }

}
