package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCompletedInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanMetadataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
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
    public @interface ConfirmationMode {
    }

    protected static final String KEY_MODE = "mode";

    private PaymentPlanCompletedInterface callback;
    private WorkflowDTO workflowDTO;
    private PaymentPlanPayloadDTO paymentPlanPayloadDTO;
    private PaymentPlanMetadataDTO paymentPlanMetadataDTO;
    private NumberFormat currencyFormatter;
    protected @ConfirmationMode
    int mode;
    private UserPracticeDTO userPracticeDTO;

    public static PaymentPlanConfirmationFragment newInstance(WorkflowDTO workflowDTO,
                                                              UserPracticeDTO userPracticeDTO,
                                                              @ConfirmationMode int mode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, workflowDTO);
        DtoHelper.bundleDto(args, userPracticeDTO);
        args.putInt(KEY_MODE, mode);

        PaymentPlanConfirmationFragment fragment = new PaymentPlanConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCompletedInterface) ((PaymentViewHandler) context)
                        .getPaymentPresenter();
            } else {
                callback = (PaymentPlanCompletedInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentPlanCompletedInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle args = getArguments();
        if (args != null) {
            mode = args.getInt(KEY_MODE, MODE_CREATE);
            workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, args);
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            //should only be one when plan is just created
            PaymentPlanDTO paymentPlanDTO = paymentsModel.getPaymentPayload()
                    .getPatientPaymentPlans().get(0);
            paymentPlanPayloadDTO = paymentPlanDTO.getPayload();
            paymentPlanMetadataDTO = paymentPlanDTO.getMetadata();
            userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);

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
        installments.setText(String.valueOf(paymentPlanPayloadDTO
                .getPaymentPlanDetails().getInstallments()));

        String paymentAmountString = currencyFormatter.format(paymentPlanPayloadDTO
                .getPaymentPlanDetails().getAmount()) +
                paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyString();
        TextView paymentAmount = (TextView) view.findViewById(R.id.payment_confirm_payment_value);
        paymentAmount.setText(paymentAmountString);

        TextView dueDate = (TextView) view.findViewById(R.id.payment_confirm_due_value);
        String dueDateString;
        if (paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            dueDateString = StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                    paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfMonth());
        } else {
            dueDateString = StringUtil
                    .getDayOfTheWeek(paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfWeek());
        }
        dueDate.setText(dueDateString);

        TextView title = (TextView) view.findViewById(R.id.payment_confirm_message);
        title.setText(getMessageLabel());

        TextView confirmation = (TextView) view.findViewById(R.id.payment_confirm_value);
        confirmation.setText(paymentPlanMetadataDTO.getIndex().getConfirmation());

        TextView practiceNameTextView = (TextView) view.findViewById(R.id.payment_confirm_practice_name);
        if (practiceNameTextView != null) {
            String practiceName = userPracticeDTO.getPracticeName();
            practiceNameTextView.setText(practiceName);
        }

        logPaymentPlanEvent();
    }

    private View.OnClickListener dismissPopupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
            callback.completePaymentPlanProcess(workflowDTO);
        }
    };

    protected String getMessageLabel() {
        switch (mode) {
            case MODE_ADD:
                return Label.getLabel("payment_plan_success_add_short");
            case MODE_EDIT:
                return Label.getLabel("payment_plan_success_edit_short");
            case MODE_CREATE:
            default:
                return Label.getLabel("payment_plan_success_create_short");
        }
    }

    private void logPaymentPlanEvent() {
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_payment_plan_id),
                getString(R.string.param_payment_plan_amount),
                getString(R.string.param_payment_plan_frequency),
                getString(R.string.param_payment_plan_payment),
                getString(R.string.param_payment_plan_day),
                getString(R.string.param_payment_plan_installments)};

        String dueDateString;
        if (paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            dueDateString = StringUtil.getOrdinal("en",
                    paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfMonth());
        } else {
            dueDateString = StringUtil
                    .getDayOfTheWeek(paymentPlanPayloadDTO.getPaymentPlanDetails().getDayOfWeek());
        }

        Object[] values = {
                paymentPlanMetadataDTO.getPracticeId(),
                paymentPlanMetadataDTO.getPaymentPlanId(),
                paymentPlanPayloadDTO.getAmount(),
                paymentPlanPayloadDTO.getPaymentPlanDetails().getFrequencyString(),
                paymentPlanPayloadDTO.getPaymentPlanDetails().getAmount(),
                dueDateString,
                paymentPlanPayloadDTO.getPaymentPlanDetails().getInstallments()};

        switch (mode) {
            case MODE_EDIT:
                MixPanelUtil.logEvent(getString(R.string.event_paymentplan_edited), params, values);
                break;
            case MODE_ADD:
            case MODE_CREATE:
            default:
                MixPanelUtil.logEvent(getString(R.string.event_paymentplan_submitted), params, values);
        }
    }


}
