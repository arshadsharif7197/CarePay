package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 1/31/18
 */

public class PracticePaymentPlanDetailsDialogFragment extends PaymentPlanDetailsDialogFragment {

    /**
     * @param paymentsModel      the payment model
     * @param paymentPlanDTO     the Payment Plan Dto
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PracticePaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PracticePaymentPlanDetailsDialogFragment dialog = new PracticePaymentPlanDetailsDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    protected void onInitialization(View view) {
        super.onInitialization(view);
        //handle cancel button

        View closeButton = view.findViewById(R.id.closeViewLayout);
        if(closeButton != null){
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        if(closeView != null) {
            closeView.setOnClickListener(this);
        }
        TextView closeText = (TextView) view.findViewById(R.id.closeTextView);
        if(closeText != null) {
            closeText.setText(getCancelString());
        }
        ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel_img);
        if(cancelImage != null) {
            cancelImage.setImageResource(getCancelImageResource());
        }

        return view;
    }


    @Override
    protected int getCancelImageResource() {
        return R.drawable.icn_close;
    }

}