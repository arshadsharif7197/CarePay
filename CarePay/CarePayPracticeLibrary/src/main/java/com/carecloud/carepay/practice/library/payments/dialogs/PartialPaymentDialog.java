package com.carecloud.carepay.practice.library.payments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    private PaymentsModel paymentsDTO;
    private PaymentsLabelDTO paymentsLabelsDTO;
    private PaymentsMetadataModel paymentsMetadataDTO;
    private String paymentTitle;
    private String paymentPartialButton;
    private String copayStr = "";
    private String previousBalanceStr = "";
    private boolean amountChangeFlag=true;

    /**
     * @param context     The context
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
        amountSymbolTextView.setText(CarePayConstants.DOLLAR);
        amountSymbolTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
        partialPaymentPayingToday.setText(paymentTitle);
        partialPaymentPayingToday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        partialPaymentPayingToday.setTextColor(context.getResources().getColor(R.color.glitter));
        SystemUtil.setGothamRoundedMediumTypeface(context, enterPartialAmountEditText);
        if (paymentsDTO != null) {
            List<PaymentPatientBalancesPayloadDTO> paymentList = paymentsDTO.getPaymentPayload().getPatientBalances().get(1).getPayload();

            if (paymentList != null && paymentList.size() > 1) {
                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PATIENT)) {
                        copayStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        previousBalanceStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    double total = copay + previousBalance;

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                    fullAmount = total;
                    partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount(fullAmount));

                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }
        enterPartialAmountEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(Double.toString(fullAmount).length())});
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
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white));
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(amountChangeFlag){
            amountChangeFlag = false;
            String amountEditText = getFormatedNumber(enterPartialAmountEditText.getText().toString());
            enterPartialAmountEditText.setText(amountEditText);
            enterPartialAmountEditText.setSelection(amountEditText.length());
            onPendingAmountValidation(amountEditText);
        } else {
            amountChangeFlag = true;
        }
    }

    private String getFormatedNumber(String amountText){
        amountText = removeExtras(amountText);
        int amountLen = amountText.length();
        if (amountLen == 1){
            return "0.0"+amountText;
        } else if(amountLen == 2){
            return "0."+amountText;
        } else if(amountLen > 2){
            return amountText.substring(0,amountLen-2)+"."+amountText.substring(amountLen-2,amountLen);
        } else {
            return "";
        }
    }

    private String removeExtras(String number){
        number = number.replace(".","");
        if(number.startsWith("0")){
            number = number.substring(1);
            number = removeExtras(number);
        }
        return number;
    }

    private void onPendingAmountValidation(String amountEditText) {
        if (amountEditText != null && amountEditText.length() > 0) {
            if (amountEditText.length() == 1 && amountEditText.equalsIgnoreCase(".")) {
                amountEditText = "0.";
            }
            double amountPay = Double.parseDouble(amountEditText);
            if ((amountPay > 0) && (amountPay <= fullAmount)) {
                partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount((double) Math.round((fullAmount - amountPay) * 100) / 100));
                payPartialButton.setEnabled(true);
            } else {
                payPartialButton.setEnabled(false);
                partialPaymentTotalAmountTitle.setText(pendingAmountLabel + " " + StringUtil.getFormattedBalanceAmount(fullAmount));
            }
        } else {
            amountSymbolTextView.setTextColor(context.getResources().getColor(R.color.white_transparent));
            payPartialButton.setEnabled(false);
            partialPaymentTotalAmountTitle.setText(pendingAmountLabel+ " " + StringUtil.getFormattedBalanceAmount(fullAmount));
        }
    }

    private void onPaymentClick() {
        if (context instanceof PatientModeCheckinActivity) {

            FragmentManager fragmentmanager = ((PatientModeCheckinActivity) context).getSupportFragmentManager();
            PatientPaymentMethodFragment fragment = (PatientPaymentMethodFragment) fragmentmanager.findFragmentByTag(PatientPaymentMethodFragment.class.getSimpleName());
            if (fragment == null) {
                fragment = new PatientPaymentMethodFragment();
            }

            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.PARTIAL_PAYMENT_AMOUNT, enterPartialAmountEditText.getText().toString());
            bundle.putSerializable(CarePayConstants.INTAKE_BUNDLE,
                    paymentsDTO);
            fragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
            fragmentTransaction.replace(R.id.checkInContentHolderId, fragment);
            fragmentTransaction.addToBackStack(PatientPaymentMethodFragment.class.getSimpleName());
            fragmentTransaction.commit();
            this.dismiss();
        }
    }

    /**
     * partial payment labels
     */
    public void getPartialPaymentLabels() {
        if (paymentsDTO != null) {
            paymentsMetadataDTO = paymentsDTO.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    paymentTitle = paymentsLabelsDTO.getPaymentHowMuchText();
                    paymentPartialButton = paymentsLabelsDTO.getPaymentPartialAmountButton();
                    pendingAmountLabel = paymentsLabelsDTO.getPaymentPendingText();
                }
            }
        }
    }
}