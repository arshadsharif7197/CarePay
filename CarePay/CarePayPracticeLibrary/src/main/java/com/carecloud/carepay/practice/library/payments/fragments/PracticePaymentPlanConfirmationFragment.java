package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 4/12/18
 */

public class PracticePaymentPlanConfirmationFragment extends PaymentPlanConfirmationFragment {

    public static PracticePaymentPlanConfirmationFragment newInstance(WorkflowDTO workflowDTO, UserPracticeDTO userPracticeDTO, @ConfirmationMode int mode){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, workflowDTO);
        DtoHelper.bundleDto(args, userPracticeDTO);
        args.putInt(KEY_MODE, mode);

        PracticePaymentPlanConfirmationFragment fragment = new PracticePaymentPlanConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getMessageLabel(){
        switch (mode){
            case MODE_ADD:
                return Label.getLabel("payment_plan_success_add");
            case MODE_EDIT:
                return Label.getLabel("payment_plan_success_edit");
            case MODE_CREATE:
            default:
                return Label.getLabel("payment_plan_success_create");
        }
    }

}
