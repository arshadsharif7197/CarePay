package com.carecloud.carepaylibray.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.base.IApplicationSession;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

public class PaymentDetailsDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;
    private PendingBalancePayloadDTO paymentPayload;
    private PaymentNavigationCallback callback;
    private double size;
    private OnDismissListener payDismissListener;

    /**
     * Constructor.
     *
     * @param context        context
     * @param paymentPayload model
     * @param callback       callback
     */
    public PaymentDetailsDialog(Context context,
                                PaymentsModel paymentReceiptModel,
                                PendingBalancePayloadDTO paymentPayload,
                                PaymentNavigationCallback callback,
                                OnDismissListener payDismissListener) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
        this.paymentPayload = paymentPayload;
        this.callback = callback;
        this.payDismissListener = payDismissListener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_details);
        onInitialization();
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (size > 0) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * size);
            getWindow().setAttributes(params);
        }
    }

    private void onInitialization() {

        Button payNowButton = (Button) findViewById(R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(this);

        if (paymentReceiptModel != null) {
            String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
            ((TextView) findViewById(R.id.payment_details_total_paid)).setText(totalAmount);
            ((TextView) findViewById(R.id.payment_receipt_title)).setText(Label.getLabel("payment_receipt_title"));
            ((TextView) findViewById(R.id.payment_receipt_total_label)).setText(Label.getLabel("payment_details_patient_balance_label"));
            ((TextView) findViewById(R.id.payment_receipt_total_value)).setText(totalAmount);
            ((TextView) findViewById(R.id.avTextView)).setText(StringUtil.getShortName(Label.getLabel("payment_receipt_title")));

            payNowButton.setText(Label.getLabel("payment_details_pay_now"));

            ImageView dialogCloseHeader;

            ApplicationMode.ApplicationType appMode = ((IApplicationSession) context).getApplicationMode().getApplicationType();
            if (appMode == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE || appMode == ApplicationMode.ApplicationType.PRACTICE) {

                dialogCloseHeader = (ImageView) findViewById(R.id.payment_close_button);
                if (dialogCloseHeader != null) {
                    dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });

                    findViewById(R.id.payment_close_Layout).setVisibility(View.VISIBLE);
                }
            } else {
                dialogCloseHeader = (ImageView) findViewById(R.id.dialog_close_header);
                if (dialogCloseHeader != null) {
                    dialogCloseHeader.setVisibility(View.VISIBLE);
                    dialogCloseHeader.setOnClickListener(this);
                }
                size = 0.90;
            }
        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(context, paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_close_header) {
            cancel();
        } else if (viewId == R.id.payment_details_pay_now_button) {
            callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
            dismiss();
            if (payDismissListener != null) {
                payDismissListener.onDismiss(this);
            }
        }
    }

}
