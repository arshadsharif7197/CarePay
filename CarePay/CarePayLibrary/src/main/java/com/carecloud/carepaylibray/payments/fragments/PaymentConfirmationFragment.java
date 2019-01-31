package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentCompletedInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_ACCOUNT;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_CARD;
import static com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 3/24/17
 */

public class PaymentConfirmationFragment extends BasePaymentDialogFragment {
    private static final String KEY_ONE_TIME_PAYMENT = "one_time_payment";
    private static final String KEY_PAYMENT_TYPE = "paymentType";
    private static final String KEY_BUTTON_LABEL = "buttonLabel";

    private PaymentCompletedInterface callback;
    private PaymentsModel paymentsModel;
    private WorkflowDTO workflowDTO;
    private IntegratedPatientPaymentPayload patientPaymentPayload;

    private NumberFormat currencyFormatter;

    public static PaymentConfirmationFragment newInstance(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, workflowDTO);
        args.putBoolean(KEY_ONE_TIME_PAYMENT, isOneTimePayment);
        PaymentConfirmationFragment fragment = new PaymentConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentConfirmationFragment newInstance(WorkflowDTO workflowDTO) {
        return newInstance(workflowDTO, false);
    }

    public static PaymentConfirmationFragment newInstance(WorkflowDTO workflowDTO,
                                                          String paymentType,
                                                          String buttonLabel) {
        PaymentConfirmationFragment fragment = newInstance(workflowDTO);
        fragment.getArguments().putString(KEY_PAYMENT_TYPE, paymentType);
        fragment.getArguments().putString(KEY_BUTTON_LABEL, buttonLabel);
        return fragment;
    }


    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
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
            workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, args);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            patientPaymentPayload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Button okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(dismissPopupListener);
        if (getArguments().getString(KEY_BUTTON_LABEL) != null) {
            okButton.setText(getArguments().getString(KEY_BUTTON_LABEL));
        }

//        View closeButton = view.findViewById(R.id.dialog_close_header);
//        if (closeButton != null) {
//            closeButton.setOnClickListener(dismissPopupListener);
//        }

        if (getArguments().getString(KEY_PAYMENT_TYPE) != null) {
            TextView typeTextView = view.findViewById(R.id.payment_confirm_type_value);
            typeTextView.setText(getArguments().getString(KEY_PAYMENT_TYPE));
        }


        TextView methodTextView = view.findViewById(R.id.payment_confirm_method_value);
        methodTextView.setText(getPaymentMethod(patientPaymentPayload));

        TextView totalTextView = view.findViewById(R.id.payment_confirm_total_value);
        totalTextView.setText(currencyFormatter.format(patientPaymentPayload.getTotalPaid()));

        TextView confirmationTextView = view.findViewById(R.id.payment_confirm_value);
        confirmationTextView.setText(patientPaymentPayload.getConfirmation());

        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        TextView date = view.findViewById(R.id.payment_confirm_date);
        date.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView practice = view.findViewById(R.id.payment_confirm_practice_name);
        String practiceName = getPracticeName(patientPaymentPayload.getMetadata().getBusinessEntityId());
        practice.setText(practiceName);

        if(getArguments().getBoolean(KEY_ONE_TIME_PAYMENT, false)){
            practice.setText(Label.getLabel("payment_queued_patient"));
        }

        //todo display possible errors

    }

    private View.OnClickListener dismissPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            callback.completePaymentProcess(workflowDTO);
        }
    };


    /**
     * Get Display label for payment method
     *
     * @param patientPaymentPayload payload
     * @return label
     */
    public static String getPaymentMethod(IntegratedPatientPaymentPayload patientPaymentPayload) {
        if(patientPaymentPayload.getPaymentMethod() == null){
            return Label.getLabel("payment_method_creditcard");
        }
        switch (patientPaymentPayload.getPaymentMethod().getPaymentMethodType()) {
            case PAYMENT_METHOD_ACCOUNT:
                return Label.getLabel("payment_method_account");
            case PAYMENT_METHOD_CARD:
            case PAYMENT_METHOD_NEW_CARD:
            default:
                if(patientPaymentPayload.getExecution().equals(IntegratedPaymentPostModel.EXECUTION_ANDROID)){
                    return CarePayConstants.TYPE_GOOGLE_PAY;
                }
                return Label.getLabel("payment_method_creditcard");
        }
    }

    private String getPracticeName(String practiceId) {
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO.getPracticeName();
            }
        }
        return null;
    }

}
