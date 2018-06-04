package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 1/31/18
 */

public class PracticePaymentPlanDetailsDialogFragment extends PaymentPlanDetailsDialogFragment {

    private DialogInterface.OnDismissListener dismissListener;

    /**
     * @param paymentsModel      the payment model
     * @param paymentPlanDTO     the Payment Plan Dto
     * @param enablePayNowButton
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PracticePaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanDTO paymentPlanDTO,
                                                                       boolean enablePayNowButton) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean("enablePayNowButton", enablePayNowButton);

        PracticePaymentPlanDetailsDialogFragment dialog = new PracticePaymentPlanDetailsDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected void onInitialization(View view) {
        super.onInitialization(view);
        //handle cancel button
        View closeButton = view.findViewById(R.id.closeViewLayout);
        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dismissListener != null) {
                        dismissListener.onDismiss(getDialog());
                    }
                    dismiss();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            closeView.setOnClickListener(this);
        }
        TextView closeText = (TextView) view.findViewById(R.id.closeTextView);
        if (closeText != null) {
            closeText.setText(getCancelString());
        }
        ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel_img);
        if (cancelImage != null) {
            cancelImage.setImageResource(getCancelImageResource());
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (((BasePracticeActivity) getActivity()).getApplicationMode().getApplicationType()
                == ApplicationMode.ApplicationType.PRACTICE) {
            payButton.setEnabled(paymentReceiptModel.getPaymentPayload().getUserAuthModel()
                    .getUserAuthPermissions().canMakePayment
                    && getArguments().getBoolean("enablePayNowButton", true));
        }
    }

    @Override
    protected int getCancelImageResource() {
        if (dismissListener != null) {
            return R.drawable.icn_arrow_left;
        }
        return R.drawable.icn_close;
    }

    /**
     * Set dismiss listener
     *
     * @param dismissListener dismiss listener
     */
    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

}
