package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 3/30/18
 */

public class PaymentPlanConfirmationFragment extends BasePaymentDialogFragment {
    public static final int MODE_CREATE = 0x111;
    public static final int MODE_EDIT = 0x112;
    public static final int MODE_ADD = 0x113;

    @IntDef({MODE_CREATE, MODE_EDIT, MODE_ADD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConfirmationMode {}

    private static final String KEY_MODE = "mode";

    private PaymentPlanCompletedInterface callback;
    private PaymentsModel paymentsModel;
    private WorkflowDTO workflowDTO;
    private PaymentPlanPayloadDTO paymentPlanPayloadDTO;
    private PaymentPlanMetadataDTO paymentPlanMetadataDTO;
    private NumberFormat currencyFormatter;
    private @ConfirmationMode int mode;

    public static PaymentPlanConfirmationFragment newInstance(WorkflowDTO workflowDTO, @ConfirmationMode int mode){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, workflowDTO);
        args.putInt(KEY_MODE, mode);

        PaymentPlanConfirmationFragment fragment = new PaymentPlanConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void attachCallback(Context context){
        try{
            callback = (PaymentPlanCompletedInterface) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement PaymentPlanCompletedInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if (args != null) {
            mode = args.getInt(KEY_MODE, MODE_CREATE);
            workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, args);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            PaymentPlanDTO paymentPlanDTO = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0);//should only be one when plan is just created
            paymentPlanPayloadDTO = paymentPlanDTO.getPayload();
            paymentPlanMetadataDTO = paymentPlanDTO.getMetadata();
        }
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_plan_confirmation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        View okButton = view.findViewById(R.id.button_ok);
        okButton.setOnClickListener(dismissPopupListener);

        View closeButton = view.findViewById(R.id.dialog_close_header);
        if (closeButton != null) {
            closeButton.setOnClickListener(dismissPopupListener);
        }

        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        TextView date = (TextView) view.findViewById(R.id.payment_confirm_date);
        date.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView totalAmount = (TextView) view.findViewById(R.id.payment_confirm_amount_value);
        totalAmount.setText(currencyFormatter.format(paymentPlanPayloadDTO.getAmount()));

        TextView installments = (TextView) view.findViewById(R.id.payment_confirm_installments_value);
        installments.setText(String.valueOf(paymentPlanPayloadDTO.getPaymentPlanDetails().getInstallments()));

        String paymentAmountString = currencyFormatter.format(paymentPlanPayloadDTO.getPaymentPlanDetails().getAmount()) +
                paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyString();
        TextView paymentAmount = (TextView) view.findViewById(R.id.payment_confirm_payment_value);
        paymentAmount.setText(paymentAmountString);

        TextView dueDate = (TextView) view.findViewById(R.id.payment_confirm_due_value);
        dueDate.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfMonth()));

        TextView title = (TextView) view.findViewById(R.id.payment_confirm_message);
        title.setText(getMessageLabel());

        TextView confirmation = (TextView) view.findViewById(R.id.payment_confirm_value);
        confirmation.setText(paymentPlanMetadataDTO.getIndex().getConfirmation());

    }

    private View.OnClickListener dismissPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            callback.completePaymentPlanProcess(workflowDTO);
        }
    };

    private String getMessageLabel(){
        switch (mode){
            case MODE_ADD:
                return Label.getLabel("payment_plan_success_add");
            case MODE_EDIT:
                return Label.getLabel("payment_plan_success_edit");
            case MODE_CREATE:
            default:
                return Label.getLabel("payment_plan_success_create");
        }
    }


}
