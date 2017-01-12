package com.carecloud.carepay.practice.library.payments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

public class ResponsibilityDialog extends Dialog {

    private Context context;
    private PaymentsModel paymentsModel;
    private int selectedIndex;

    /**
     * Constructor
     * @param context context
     * @param paymentsModel paymentsModel
     * @param selectedIndex selectedIndex
     */
    public ResponsibilityDialog(Context context, PaymentsModel paymentsModel, int selectedIndex) {
        super(context);
        this.context = context;
        this.paymentsModel = paymentsModel;
        this.selectedIndex = selectedIndex;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_responsibility);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.53);
        getWindow().setAttributes(params);

        onInitialization();
    }

    @SuppressLint("InflateParams")
    private void onInitialization() {
        PaymentsPatientBalancessDTO paymentsPatient = paymentsModel.getPaymentPayload().getPatientBalances().get(selectedIndex);

        DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = paymentsPatient.getDemographics().getPayload().getPersonalDetails();
        ((TextView) findViewById(R.id.patient_full_name)).setText(personalDetails.getFirstName() + " " + personalDetails.getLastName());
//        ((TextView) findViewById(R.id.patient_profile_photo)).setText(StringUtil
//                .onShortDrName(personalDetails.getFirstName() + " " + personalDetails.getLastName()));
        ((TextView) findViewById(R.id.patient_provider_name)).setText(StringUtil.getLabelForView(""));

        PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
        List<PatienceBalanceDTO> balances = paymentsPatient.getBalances();
        if (balances != null && balances.size() > 0) {
            ScrollView amountDetails = (ScrollView) findViewById(R.id.payment_responsibility_balance_details);

            double totalAmount = 0;
            List<PatiencePayloadDTO> payload = balances.get(0).getPayload();
            for (int i = 0; i < payload.size(); i++) {
                PatiencePayloadDTO patiencePayload = payload.get(i);
                LinearLayout chargeRow = (LinearLayout) getLayoutInflater().inflate(R.layout.payment_charges_row, null);
                ((TextView) chargeRow.findViewById(R.id.payment_charges_label)).setText(patiencePayload.getType());

                TextView detailsView = (TextView) chargeRow.findViewById(R.id.payment_charges_details);
                detailsView.setText(paymentsLabel.getPracticePaymentsDetailDialogLabel());
                detailsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                totalAmount += patiencePayload.getAmount();
                ((TextView) chargeRow.findViewById(R.id.payment_charges_amount)).setText(
                        StringUtil.getFormattedBalanceAmount(totalAmount));
                amountDetails.addView(chargeRow);

                if (i == 0) {
                    detailsView.setVisibility(View.VISIBLE);
                }
            }

            ((TextView) findViewById(R.id.payment_responsibility_balance)).setText(
                    paymentsLabel.getPracticePaymentsDetailDialogBalance() + ": "
                            + StringUtil.getFormattedBalanceAmount(totalAmount));

            Button paymentPlan = (Button) findViewById(R.id.payment_plan_button);
            paymentPlan.setText(paymentsLabel.getPracticePaymentsDetailDialogPaymentPlan());
            SystemUtil.setGothamRoundedMediumTypeface(context, paymentPlan);
            paymentPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            Button paymentPay = (Button) findViewById(R.id.payment_pay_button);
            paymentPay.setText(paymentsLabel.getPracticePaymentsDetailDialogPay());
            SystemUtil.setGothamRoundedMediumTypeface(context, paymentPay);
            paymentPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            (findViewById(R.id.payment_responsibility_close_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            ((TextView) findViewById(R.id.payment_responsibility_close_label))
                    .setText(paymentsLabel.getPracticePaymentsDetailDialogCloseButton());
        }
    }
}
