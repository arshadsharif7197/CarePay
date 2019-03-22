package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentDetailInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

public abstract class BasePaymentDetailsFragmentDialog extends BaseDialogFragment {

    protected PaymentsModel paymentReceiptModel;
    protected PendingBalancePayloadDTO paymentPayload;
    protected PaymentDetailInterface callback;

    private DialogInterface.OnDismissListener dismissListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                callback = (PaymentDetailInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PaymentDetailInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        if (getDialog() != null && dismissListener != null) {
            getDialog().setOnDismissListener(dismissListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentPayload = DtoHelper.getConvertedDTO(PendingBalancePayloadDTO.class, getArguments());
        paymentReceiptModel = DtoHelper.getConvertedDTO(PaymentsModel.class, getArguments());
    }


//    @Override
//    protected void onDialogCancel() {
//        dismiss();
//        if(dismissListener == null) {
//            callback.onDetailCancelClicked(paymentReceiptModel);
//        }
//    }

    /**
     * Set Dismiss Listener when dialog is shown
     *
     * @param dismissListener dismissListener
     */
    public void addOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
        if (getDialog() != null) {
            getDialog().setOnDismissListener(dismissListener);
        }
    }

}
