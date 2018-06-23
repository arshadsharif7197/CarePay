package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

public class ConfirmCashDialogFragment extends BaseDialogFragment {

    private ConfirmCashCallback confirmCashCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.dialog_confirm_cash, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View close = view.findViewById(R.id.closeViewLayout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        View pay = view.findViewById(R.id.confirm_button);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmCashCallback != null){
                    confirmCashCallback.onConfirmCash();
                }
                dismiss();
            }
        });
    }

    public void setConfirmCashCallback(ConfirmCashCallback confirmCashCallback) {
        this.confirmCashCallback = confirmCashCallback;
    }

    public interface ConfirmCashCallback {
        void onConfirmCash();
    }
}
