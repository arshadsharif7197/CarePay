package com.carecloud.carepay.patient.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 10/9/17
 */

public class ConfirmExitDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    public interface ExitConfirmationCallback{
        void onExit();
    }

    private ExitConfirmationCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (ExitConfirmationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement ExitConfirmationCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.dialog_confirm_exit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View close = view.findViewById(R.id.dialogCloseHeaderImageView);
        close.setOnClickListener(this);

        View cancel = view.findViewById(R.id.button_no);
        cancel.setOnClickListener(this);

        View proceed = view.findViewById(R.id.button_yes);
        proceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        if(view.getId() == R.id.button_yes){
            callback.onExit();
        }
    }
}
