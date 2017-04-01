package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 3/24/17.
 */

public class PaymentConfirmationFragment extends BaseDialogFragment {

    private PaymentNavigationCallback callback;
    private PaymentsModel paymentsModel;
    private PatientPaymentPayload patientPaymentPayload;
    private PatientBalanceDTO balance;

    NumberFormat currencyFormatter;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args!=null){
            Gson gson = new Gson();
            String paymentPayload = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentPayload, PaymentsModel.class);
            patientPaymentPayload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload().get(0);
            balance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        }
        currencyFormatter = NumberFormat.getCurrencyInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(dismissPopupListener);

        View closeButton = view.findViewById(R.id.dialog_close_header);
        if(closeButton!=null){
            closeButton.setOnClickListener(dismissPopupListener);
        }

        TextView type = (TextView) view.findViewById(R.id.payment_confirm_type_value);
        type.setText(patientPaymentPayload.getType());

        TextView method = (TextView) view.findViewById(R.id.payment_confirm_method_value);
        method.setText(patientPaymentPayload.getMethod());

        TextView total = (TextView) view.findViewById(R.id.payment_confirm_total_value);
        total.setText(currencyFormatter.format(patientPaymentPayload.getTotal()));

        TextView confirmation = (TextView) view.findViewById(R.id.payment_confirm_value);
        confirmation.setText(patientPaymentPayload.getConfirmation());

        DateUtil dateUtil = DateUtil.getInstance();
        dateUtil.setDateRaw(patientPaymentPayload.getDate());
        TextView date = (TextView) view.findViewById(R.id.payment_confirm_date);
        date.setText(dateUtil.toStringWithFormatMmSlashDdSlashYyyy());

        TextView practice = (TextView) view.findViewById(R.id.payment_confirm_practice_name);
        practice.setText(paymentsModel.getPaymentPayload().getUserPractices().get(0).getPracticeName());
    }

    private View.OnClickListener dismissPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            Gson gson = new Gson();
            String patientBalance = gson.toJson(balance);
            UpdatePatientBalancesDTO updatePatientBalance = gson.fromJson(patientBalance, UpdatePatientBalancesDTO.class);
            callback.completePaymentProcess(updatePatientBalance);
        }
    };

}