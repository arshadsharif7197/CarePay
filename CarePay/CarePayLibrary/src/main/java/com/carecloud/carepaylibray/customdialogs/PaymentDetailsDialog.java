package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;


public class PaymentDetailsDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;
    private PatiencePayloadDTO paymentPayload;
    private PayNowClickListener listener;
    private double size;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentPayload model
     * @param listener listener
     */
    public PaymentDetailsDialog(Context context, PaymentsModel paymentReceiptModel, PatiencePayloadDTO paymentPayload, PayNowClickListener listener) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
        this.paymentPayload = paymentPayload;
        this.listener = listener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_details);
        onInitialization();
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * size);
        getWindow().setAttributes(params);


    }

    private void onInitialization() {


        Button payNowButton = (Button) findViewById(com.carecloud.carepaylibrary.R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(this);

        if (paymentReceiptModel != null) {
            PaymentsLabelDTO paymentsLabel = paymentReceiptModel.getPaymentsMetadata().getPaymentsLabel();
            if (paymentsLabel != null) {
                String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
                ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_details_total_paid)).setText(totalAmount);

                ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_title))
                        .setText(paymentsLabel.getPaymentReceiptTitle());
                ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_total_label))
                        .setText(paymentsLabel.getPaymentDetailsPatientBalanceLabel());
                ((TextView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_total_value)).setText(totalAmount);

                payNowButton.setText(paymentsLabel.getPaymentDetailsPayNow());

                ImageView dialogCloseHeader;
                TextView closeLabel;
                if(ApplicationMode.getInstance().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE))
                {

                    dialogCloseHeader = (ImageView) findViewById(R.id.payment_close_button);
                    dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });

                    closeLabel=((TextView) findViewById(R.id.payment_close_label));
                    closeLabel.setText(paymentsLabel.getPaymentCloseButton());
                    findViewById(R.id.payment_close_Layout).setVisibility(View.VISIBLE);
                    size = 0.53;
                }else{
                    dialogCloseHeader = (ImageView) findViewById(com.carecloud.carepaylibrary.R.id.dialog_close_header);
                    dialogCloseHeader.setVisibility(View.VISIBLE);
                    dialogCloseHeader.setOnClickListener(this);
                    size = 0.90;
                }
            }
        }


        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) findViewById(com.carecloud.carepaylibrary.R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(context, paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == com.carecloud.carepaylibrary.R.id.dialog_close_header) {
            cancel();
        } else if (viewId == com.carecloud.carepaylibrary.R.id.payment_details_pay_now_button) {
            listener.onPayNowButtonClicked();
            dismiss();
        }
    }

    public interface PayNowClickListener {
        void onPayNowButtonClicked();
    }
}
