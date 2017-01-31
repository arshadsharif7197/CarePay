package com.carecloud.carepay.patient.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.List;



/**
 * Created by prem_mourya on 10/4/2016.
 */
public class PartialPaymentDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private Context context;
    //private JSONObject paymentModel;
    private EditText enterPartialAmountEditText;
    private CarePayTextView partialPaymentTotalAmountTitle;
    private CarePayTextView amountSymbolTextView;
    private CarePayTextView partialPaymentPayingToday;
    private Button payPartialButton;
    //changes are needed when model will come
    private double fullAmount = 0.00;
    private String pendingAmountLabel;
    private String amountSymbol = "$";
    private PaymentsModel paymentsDTO;
    private PaymentsLabelDTO paymentsLabelsDTO;
    private PaymentsMetadataModel paymentsMetadataDTO;
    private String paymentTitle;
    private String paymentPartialButton;
    private String paymentTotalButton;
    private double insuranceCopay;
    private double patientBalance;
    private boolean amountChangeFlag = true;
    private String balanceBeforeTextChange;

    /**
     *
     * @param context The context
     * @param paymentsDTO The payments DTO
     */
    public PartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_partial_payment);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);
        getPartialPaymentLabels();
        findViewById(R.id.dialogCloseImageView).setOnClickListener(this);
        enterPartialAmountEditText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        partialPaymentTotalAmountTitle = (CarePayTextView) findViewById(R.id.partialPaymentTotalAmountTitle);
        payPartialButton = (Button) findViewById(R.id.payPartialButton);
        amountSymbolTextView = (CarePayTextView) findViewById(R.id.amountSymbolTextView);
        partialPaymentPayingToday = (CarePayTextView) findViewById(R.id.partialPaymentPayingToday);
        enterPartialAmountEditText.addTextChangedListener(this);
        partialPaymentTotalAmountTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        partialPaymentTotalAmountTitle.setTextColor(context.getResources().getColor(R.color.payne_gray));
        payPartialButton.setText(paymentPartialButton);
        payPartialButton.setOnClickListener(this);
        payPartialButton.setEnabled(false);
        amountSymbolTextView.setText(amountSymbol);
        amountSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
        partialPaymentPayingToday.setText(paymentTitle);
        partialPaymentPayingToday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        partialPaymentPayingToday.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context, enterPartialAmountEditText);

        try {
            if (paymentsDTO != null) {
                List<PatiencePayloadDTO> paymentList
                        = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();

                if (paymentList != null && paymentList.size() > 0) {
                    for (PatiencePayloadDTO payment : paymentList) {
                        if (payment.getType().equalsIgnoreCase(CarePayConstants.PATIENT_BALANCE)) {
                            patientBalance = payment.getAmount();
                        } else if (payment.getType().equalsIgnoreCase(CarePayConstants.INSURANCE_COPAY)) {
                            insuranceCopay = payment.getAmount();
                        }
                    }

                    fullAmount = patientBalance+insuranceCopay;
                    partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.dialogCloseImageView) {
            cancel();
        } else if (viewId == R.id.payPartialButton) {
            onPaymentClick();
        }
    }

    @Override
    public void afterTextChanged(Editable str) {
    }

    @Override
    public void beforeTextChanged(CharSequence str, int start, int count, int after) {
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white));
        balanceBeforeTextChange = str.toString();
    }

    @Override
    public void onTextChanged(CharSequence str, int start, int before, int count) {
        String amountEditText = enterPartialAmountEditText.getText().toString();
        try {
            if (amountChangeFlag) {
                // flag to avoid the onTextChanged listener call after setText manipulated number
                amountChangeFlag = false;
                //when deleting a decimal point, delete any non-integral portion of the number
                if(balanceBeforeTextChange.contains(".") && !amountEditText.contains(".")) {
                    amountEditText = balanceBeforeTextChange.substring(0,balanceBeforeTextChange.indexOf("."));
                    enterPartialAmountEditText.setText(amountEditText);
                    enterPartialAmountEditText.setSelection(amountEditText.length());
                } else
                // user cannot enter amount less than 1
                if(amountEditText.equalsIgnoreCase(".") || Double.parseDouble(amountEditText)<1){
                    amountEditText = "0";
                    enterPartialAmountEditText.setText(amountEditText);
                } else
                // Only when user enters dot, we should show the decimal values as 00
                if (amountEditText.endsWith(".")) {
                    amountEditText = amountEditText + "00";
                    enterPartialAmountEditText.setText(amountEditText);
                    enterPartialAmountEditText.setSelection(amountEditText.length() - 2);
                    // When user removes dot, we should show the integer before that
                } else if (amountEditText.endsWith(".0")) {
                    enterPartialAmountEditText.setText(amountEditText.substring(0, amountEditText.length() - 2));
                    enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length());
                    // When user enters number, we should simply append the number entered
                    // Also adjusting the cursor position after removing DOT and appending number
                } else {
                    if (amountEditText.contains(".") && amountEditText.length()-3>amountEditText.indexOf(".")) {
                        //enterPartialAmountEditText.setText(new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER).format(Double.parseDouble(amountEditText)));
                        amountEditText = amountEditText.substring(0,amountEditText.indexOf(".")+3);
                        enterPartialAmountEditText.setText(amountEditText);
                    } else {
                        enterPartialAmountEditText.setText(amountEditText);
                    }
                    if (enterPartialAmountEditText.getText().toString().endsWith("0") && amountEditText.contains(".")) {
                        enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length() - 1);
                    } else {
                        enterPartialAmountEditText.setSelection(enterPartialAmountEditText.length());
                    }
                }
            } else {
                amountChangeFlag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Calculating the remaining amount after entering partial payment amount
        onPendingAmountValidation(amountEditText);
    }

    private void onPendingAmountValidation(String amountEditText) {
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
            partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount((double) Math.round((fullAmount - amountPay) * 100) / 100));
        } else {
            amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
        }
    }

    private void onPaymentClick() {
        if (context instanceof PaymentActivity) {

            FragmentManager fragmentmanager = ((PaymentActivity) context).getSupportFragmentManager();
            PaymentMethodFragment fragment = (PaymentMethodFragment) fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());
            if (fragment == null) {
                fragment = new PaymentMethodFragment();
            }

            Bundle arguments = ((PaymentActivity) context).getIntent().getBundleExtra(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String paymentsDTOString = gson.toJson(paymentsDTO);
            bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
//            bundle.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
//                    arguments.getSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO));
           // bundle.putSerializable(CarePayConstants.INTAKE_BUNDLE,
             //       paymentsDTO);
            bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, Double.parseDouble(enterPartialAmountEditText.getText().toString()));
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
            fragmentTransaction.addToBackStack(PaymentMethodFragment.class.getSimpleName());
            fragmentTransaction.commit();
            this.dismiss();
        }
    }

    /**
     *  partial payment labels
      */
    public void getPartialPaymentLabels() {
        if (paymentsDTO != null) {
            paymentsMetadataDTO = paymentsDTO.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    paymentTitle = paymentsLabelsDTO.getPaymentPartialAmountTitle();
                    paymentPartialButton = paymentsLabelsDTO.getPaymentPartialAmountButton();
                    paymentTotalButton = paymentsLabelsDTO.getPaymentPayTotalAmountButton();
                    pendingAmountLabel = paymentsLabelsDTO.getPaymentPendingText();

                }
            }
        }
    }
}