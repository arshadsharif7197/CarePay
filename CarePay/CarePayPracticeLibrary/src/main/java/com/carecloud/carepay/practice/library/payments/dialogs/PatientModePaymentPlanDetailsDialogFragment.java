package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeEditOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeOneTimePaymentFragment;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 1/31/18
 */

public class PatientModePaymentPlanDetailsDialogFragment extends PaymentPlanDetailsDialogFragment {

    /**
     * @param paymentsModel      the payment model
     * @param paymentPlanDTO     the Payment Plan Dto
     * @param enablePayNowButton
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PatientModePaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel,
                                                                          PaymentPlanDTO paymentPlanDTO,
                                                                          boolean enablePayNowButton) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean("enablePayNowButton", enablePayNowButton);

        PatientModePaymentPlanDetailsDialogFragment dialog = new PatientModePaymentPlanDetailsDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View closeButton = view.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(view1 -> dismiss());
        }
        if (((BasePracticeActivity) getActivity()).getApplicationMode().getApplicationType()
                == ApplicationMode.ApplicationType.PRACTICE) {
            payButton.setEnabled(paymentReceiptModel.getPaymentPayload().getUserAuthModel()
                    .getUserAuthPermissions().canMakePayment
                    && getArguments().getBoolean("enablePayNowButton", true));
        }
    }

    @Override
    protected void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PracticeOneTimePaymentFragment fragment = PracticeOneTimePaymentFragment.newInstance(paymentsModel,
                0, paymentPlanDTO);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    protected void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanEditFragment fragment = PatientModePaymentPlanEditFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    protected void onStartEditScheduledPayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO,
                                               ScheduledPaymentModel scheduledPayment) {
        PracticeEditOneTimePaymentFragment fragment = PracticeEditOneTimePaymentFragment
                .newInstance(paymentsModel, 0, paymentPlanDTO, scheduledPayment);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

}
