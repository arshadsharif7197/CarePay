package com.carecloud.carepaylibray.payments.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/28/17
 */

public class PartialPaymentDialog extends Dialog implements View.OnClickListener, TextWatcher {
    private Context context;

    private EditText amountText;
    private TextView partialPaymentTotalAmountTitle;
    private TextView amountSymbol;
    private Button payPartialButton;

    private double fullAmount = 0.00;
    private PaymentsModel paymentsDTO;
    private double minimumPayment;
    private PendingBalanceDTO selectedBalance;

    private double insuranceCopay;
    private double patientBalance;
    private boolean amountChangeFlag = true;
    private String balanceBeforeTextChange;

    private PaymentConfirmationInterface payListener;

    /**
     * Contructor
     *
     * @param context     context must implement PayNowClickListener
     * @param paymentsDTO payment model
     */
    public PartialPaymentDialog(Context context, PaymentsModel paymentsDTO, PendingBalanceDTO selectedBalance) {
        super(context);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
        this.selectedBalance = selectedBalance;

        try {
            if (context instanceof PaymentViewHandler) {
                payListener = ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                payListener = (PaymentConfirmationInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Dialog Context must implement PaymentInterface for callback");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_partial_payment);
        setCancelable(false);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
            getWindow().setAttributes(params);
        }
        initViews();
    }

    private void initViews() {
        amountText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        partialPaymentTotalAmountTitle = (TextView) findViewById(R.id.partialPaymentTotalAmountTitle);
        payPartialButton = (Button) findViewById(R.id.payPartialButton);
        amountSymbol = (TextView) findViewById(R.id.amountSymbolTextView);

        amountText.addTextChangedListener(this);
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);

        String symbol = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        amountSymbol.setText(symbol);

        View closeView = findViewById(com.carecloud.carepaylibrary.R.id.closeViewLayout);
        closeView.setOnClickListener(this);

        calculateFullAmount(partialPaymentTotalAmountTitle);

        if(selectedBalance == null){
            selectedBalance = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);
        }
        minimumPayment = getMinimumPayment(selectedBalance.getMetadata().getPracticeId());
        if(minimumPayment > 0){
            TextView header = (TextView) findViewById(R.id.partialPaymentHeader);
            String headerText = Label.getLabel("payment_partial_minimum_amount") + NumberFormat.getCurrencyInstance().format(minimumPayment);
            header.setText(headerText);
        }
        SystemUtil.showSoftKeyboard(context);
        amountText.requestFocus();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.closeViewLayout) {
            SystemUtil.hideSoftKeyboard(context, view);
            cancel();
        } else if (viewId == R.id.payPartialButton) {
            double amount = Double.parseDouble(amountText.getText().toString());
            if(amount > fullAmount){
                String errorMessage = Label.getLabel("payment_partial_max_error") + NumberFormat.getCurrencyInstance().format(fullAmount);
                CustomMessageToast toast = new CustomMessageToast(context, errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR);
                toast.show();
                return;
            }
            if(minimumPayment > 0 && amount < minimumPayment){
                String errorMessage = Label.getLabel("payment_partial_minimum_error") + NumberFormat.getCurrencyInstance().format(minimumPayment);
                CustomMessageToast toast = new CustomMessageToast(context, errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR);
                toast.show();
                return;
            }
            SystemUtil.hideSoftKeyboard(context, view);
            onPaymentClick(amountText);
        }
    }

    @Override
    public void afterTextChanged(Editable str) {
        if (str.length() > 0) {
            amountSymbol.setTextColor(ContextCompat.getColor(context, R.color.white));
            amountText.setHint(null);
        } else {
            amountSymbol.setTextColor(ContextCompat.getColor(context, R.color.white_transparent));
            amountText.setHint("0.00");
        }
        if (str.length() > 7) {
            amountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
            amountSymbol.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        } else if (str.length() > 5) {
            amountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
            amountSymbol.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        } else {
            amountText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            amountSymbol.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence str, int start, int count, int after) {
        //amountSymbol.setTextColor(context.getResources().getColor(R.color.white));
        balanceBeforeTextChange = str.toString();
    }

    @Override
    public void onTextChanged(CharSequence str, int start, int before, int count) {
        String amountEditText = amountText.getText().toString();
        try {
            if (amountChangeFlag) {
                // flag to avoid the onTextChanged listener call after setText manipulated number
                amountChangeFlag = false;
                //when deleting a decimal point, delete any non-integral portion of the number
                if (balanceBeforeTextChange.contains(".") && !amountEditText.contains(".")) {
                    amountEditText = balanceBeforeTextChange.substring(0, balanceBeforeTextChange.indexOf("."));
                    amountText.setText(amountEditText);
                    amountText.setSelection(amountEditText.length());
                } else
                    // user cannot enter amount less than 1
                    if (amountEditText.equalsIgnoreCase(".") || Double.parseDouble(amountEditText) < 1) {
                        amountEditText = "0";
                        amountText.setText(amountEditText);
                    } else
                        // Only when user enters dot, we should show the decimal values as 00
                        if (amountEditText.endsWith(".")) {
                            amountEditText = amountEditText + "00";
                            amountText.setText(amountEditText);
                            amountText.setSelection(amountEditText.length() - 2);
                            // When user removes dot, we should show the integer before that
                        } else if (amountEditText.endsWith(".0")) {
                            amountText.setText(amountEditText.substring(0, amountEditText.length() - 2));
                            amountText.setSelection(amountText.length());
                            // When user enters number, we should simply append the number entered
                            // Also adjusting the cursor position after removing DOT and appending number
                        } else {
                            if (amountEditText.contains(".") && amountEditText.length() - 3 > amountEditText.indexOf(".")) {
                                //amountText.setText(new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER).format(Double.parseDouble(amountEditText)));
                                amountEditText = amountEditText.substring(0, amountEditText.indexOf(".") + 3);
                                amountText.setText(amountEditText);
                            } else {
                                if (amountEditText.length() > 8) {
                                    amountEditText = amountEditText.substring(0, 8);
                                }
                                if (amountEditText.startsWith("0")){
                                    amountEditText=amountEditText.substring(1);
                                }
                                amountText.setText(amountEditText);
                            }
                            if (amountText.getText().toString().endsWith("0") && amountEditText.contains(".")) {
                                amountText.setSelection(amountText.length() - 1);
                            } else {
                                amountText.setSelection(amountText.length());
                            }
                        }
            } else {
                if (!amountEditText.contains(".")){
                    amountText.setSelection(amountText.length());
                }
                amountChangeFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Calculating the remaining amount after entering partial payment amount
        onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle);
    }

    private void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle) {
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double amountPay = Double.parseDouble(amountEditText);
            if ((amountPay > 0) && (amountPay <= fullAmount)) {
                payPartialButton.setEnabled(true);
            } else {
                payPartialButton.setEnabled(false);
            }
            partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount((double) Math.round((fullAmount - amountPay) * 100) / 100));
        } else {
            //amountSymbol.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
        }
    }

    private void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);
            payListener.onPayButtonClicked(amount, paymentsDTO);
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private void calculateFullAmount(TextView partialPaymentTotalAmountTitle) {
        if (paymentsDTO != null && !paymentsDTO.getPaymentPayload().getPatientBalances().isEmpty()) {
            List<PendingBalancePayloadDTO> paymentList = selectedBalance.getPayload();

            if (paymentList != null && paymentList.size() > 0) {
                for (PendingBalancePayloadDTO payment : paymentList) {
                    if (payment.getType().equalsIgnoreCase(CarePayConstants.PATIENT_BALANCE)) {
                        patientBalance = payment.getAmount();
                    } else if (payment.getType().equalsIgnoreCase(CarePayConstants.INSURANCE_COPAY)) {
                        insuranceCopay = payment.getAmount();
                    }
                }

                fullAmount = patientBalance + insuranceCopay;
                partialPaymentTotalAmountTitle.setText(Label.getLabel("payment_pending_text") + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
            }
        }
    }

    private void createPaymentModel(double payAmount) {
        IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(payAmount);

        List<PendingBalancePayloadDTO> responsibilityTypes = getPendingResponsibilityTypes();
        for (PendingBalancePayloadDTO responsibility : responsibilityTypes) {
            if (payAmount > 0D) {
                double itemAmount;
                if (payAmount >= responsibility.getAmount()) {
                    itemAmount = responsibility.getAmount();
                } else {
                    itemAmount = payAmount;
                }
                payAmount = (double) Math.round((payAmount - itemAmount) * 100) / 100;

                IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
                paymentLineItem.setAmount(itemAmount);

                AppointmentDTO appointmentDTO = payListener.getAppointment();
                if (appointmentDTO != null) {
                    paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                    paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                }

                switch (responsibility.getType()){
                    case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COINSURANCE);
                        break;
                    case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_DEDUCTABLE);
                        break;
                    case PendingBalancePayloadDTO.CO_PAY_TYPE:
                    default:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COPAY);
                        break;
                }

                postModel.addLineItem(paymentLineItem);

            }
        }

        if (payAmount > 0) {//payment is greater than any responsibility types

            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(payAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            postModel.addLineItem(paymentLineItem);
        }

        paymentsDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<PendingBalancePayloadDTO> getPendingResponsibilityTypes() {
        List<PendingBalancePayloadDTO> responsibilityTypes = new ArrayList<>();
        for (PatientBalanceDTO patientBalanceDTO : paymentsDTO.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                for (PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()) {
                    switch (pendingBalancePayloadDTO.getType()) {
                        case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:
                        case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                            responsibilityTypes.add(pendingBalancePayloadDTO);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return responsibilityTypes;
    }

    private double getMinimumPayment(String practiceId){
        if(practiceId != null) {
            for (PaymentsPayloadSettingsDTO payloadSettingsDTO : paymentsDTO.getPaymentPayload().getPaymentSettings()) {
                if (practiceId.equals(payloadSettingsDTO.getMetadata().getPracticeId())) {
                    return payloadSettingsDTO.getPayload().getRegularPayments().getMinimumPartialPaymentAmount();
                }
            }
        }
        return 0D;
    }

}