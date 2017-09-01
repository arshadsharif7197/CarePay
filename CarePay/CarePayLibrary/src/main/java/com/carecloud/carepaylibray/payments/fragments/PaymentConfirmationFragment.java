package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentCompletedInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_ACCOUNT;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_CARD;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 3/24/17
 */

public class PaymentConfirmationFragment extends BasePaymentDialogFragment {

    private PaymentCompletedInterface callback;
    private PaymentsModel paymentsModel;
    private WorkflowDTO workflowDTO;
    private IntegratedPatientPaymentPayload patientPaymentPayload;

    NumberFormat currencyFormatter;


    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            }else if (context instanceof AppointmentViewHandler){
                callback = (PaymentCompletedInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentCompletedInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle args = getArguments();
        if (args != null) {
            Gson gson = new Gson();
            String paymentPayload = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            workflowDTO = gson.fromJson(paymentPayload, WorkflowDTO.class);
            paymentsModel = gson.fromJson(paymentPayload, PaymentsModel.class);
            patientPaymentPayload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
        }
        currencyFormatter = NumberFormat.getCurrencyInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        View okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(dismissPopupListener);

        View closeButton = view.findViewById(R.id.dialog_close_header);
        if (closeButton != null) {
            closeButton.setOnClickListener(dismissPopupListener);
        }

        TextView type = (TextView) view.findViewById(R.id.payment_confirm_type_value);
        type.setText(Label.getLabel("payment_type_one_time"));

        TextView method = (TextView) view.findViewById(R.id.payment_confirm_method_value);
        method.setText(getPaymentMethod(patientPaymentPayload));

        TextView total = (TextView) view.findViewById(R.id.payment_confirm_total_value);
        total.setText(currencyFormatter.format(getTotalPaid(patientPaymentPayload)));

        TextView confirmation = (TextView) view.findViewById(R.id.payment_confirm_value);
        confirmation.setText(patientPaymentPayload.getPaymentId());

        DateUtil dateUtil = DateUtil.getInstance();
//        dateUtil.setDateRaw(patientPaymentPayload.getDate());
        TextView date = (TextView) view.findViewById(R.id.payment_confirm_date);
        date.setText(dateUtil.toStringWithFormatMmSlashDdSlashYyyy());

        TextView practice = (TextView) view.findViewById(R.id.payment_confirm_practice_name);
        practice.setText(paymentsModel.getPaymentPayload().getUserPractices().get(0).getPracticeName());

        //todo display possible errors

    }

    private View.OnClickListener dismissPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            callback.completePaymentProcess(workflowDTO);
        }
    };


    public static double getTotalPaid(IntegratedPatientPaymentPayload patientPaymentPayload){
        double total = 0D;
        for(IntegratedPatientPaymentLineItem lineItem : patientPaymentPayload.getLineItems()){
            if(lineItem.isProcessed()){
                total+=lineItem.getAmount();
            }
        }
        return total;
    }

    public static String getPaymentMethod(IntegratedPatientPaymentPayload patientPaymentPayload){
        switch (patientPaymentPayload.getPaymentMethod().getPaymentMethodType()){
            case PAYMENT_METHOD_ACCOUNT:
                return Label.getLabel("payment_method_account");
            case PAYMENT_METHOD_CARD:
            case PAYMENT_METHOD_NEW_CARD:
            default:
                return Label.getLabel("payment_method_creditcard");
        }
    }
}
