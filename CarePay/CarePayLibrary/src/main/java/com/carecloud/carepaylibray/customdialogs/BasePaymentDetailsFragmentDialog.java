package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

public abstract class BasePaymentDetailsFragmentDialog extends BaseDialogFragment implements View.OnClickListener {

    protected PaymentsModel paymentReceiptModel;
    protected PendingBalancePayloadDTO paymentPayload;
    protected PaymentNavigationCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    protected void attachCallback(Context context){
        try {
            if(context instanceof PaymentViewHandler){
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                callback = (PaymentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(callback==null){
            attachCallback(getContext());
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
    }

    protected abstract void onInitialization(View view);

    @Override
    protected String getCancelString() {
        return null;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    protected void onDialogCancel(){
        dismiss();
        callback.startPaymentProcess(paymentReceiptModel);
    }
}
