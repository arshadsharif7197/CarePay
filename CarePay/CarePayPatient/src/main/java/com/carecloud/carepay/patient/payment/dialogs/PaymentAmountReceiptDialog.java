package com.carecloud.carepay.patient.payment.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.adapter.PaymentDetailsListAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentDetailsItemDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentAmountReceiptDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private PaymentsModel paymentReceiptModel;

    /**
     * Constructor.
     *
     * @param context             context
     * @param paymentReceiptModel model
     */
    public PaymentAmountReceiptDialog(Context context, PaymentsModel paymentReceiptModel) {
        super(context);
        this.context = context;
        this.paymentReceiptModel = paymentReceiptModel;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_amount_receipt);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);

        onInitialization();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_close_header) {
            cancel();
        } else if (viewId == R.id.payment_receipt_save_button) {
            onSaveButton();
        }
    }

    private void onInitialization() {
        ImageView dialogCloseHeader = (ImageView) findViewById(R.id.dialog_close_header);
        dialogCloseHeader.setOnClickListener(this);

        Button saveReceiptButton = (Button) findViewById(R.id.payment_receipt_save_button);
        saveReceiptButton.setOnClickListener(this);

        PaymentsLabelDTO paymentsLabel = paymentReceiptModel.getPaymentsMetadata().getPaymentsLabel();
        if (paymentsLabel != null) {
            ((TextView) findViewById(R.id.receipt_no_label)).setText(paymentsLabel.getPaymentReceiptNoLabel());
            ((TextView) findViewById(R.id.payment_receipt_type_label)).setText(paymentsLabel.getPaymentReceiptPaymentType());

            String receiptDate = DateUtil.getInstance().setDateRaw(new Date().toString()).getDateAsMMddyyyyWithSlash();
            ((TextView) findViewById(R.id.payment_receipt_date_label)).setText(receiptDate);

            ((TextView) findViewById(R.id.payment_receipt_title)).setText(paymentsLabel.getPaymentReceiptTitle());
            ((TextView) findViewById(R.id.payment_receipt_total_label)).setText(paymentsLabel.getPaymentReceiptTotalPaidLabel());
            ((TextView) findViewById(R.id.payment_receipt_total_value)).setText("");

            saveReceiptButton.setText(paymentsLabel.getPaymentReceiptSaveReceipt());
        }

        // Temporary added till endpoint gets ready
        String[] labels = {"Deluxe Frame", "Sph Cyl Bif Pl To 400", "Eye Exam. New Patient", "Refraction"};
        String[] values = {"$69.00", "$99.00", "$77.01", "$49.00"};

        List<PaymentDetailsItemDTO> detailsList = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            PaymentDetailsItemDTO paymentDetails = new PaymentDetailsItemDTO();
            paymentDetails.setLabel(labels[i]);
            paymentDetails.setValue(values[i]);
            detailsList.add(paymentDetails);
        }

        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        PaymentDetailsListAdapter adapter = new PaymentDetailsListAdapter(context, detailsList);
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    private void onSaveButton() {
        showPaymentHistory();
        dismiss();
    }

    private void showPaymentHistory() {
        PaymentPayloadMetaDataDTO metadata = paymentReceiptModel.getPaymentPayload()
                .getPatientPayments().getPayload().get(0).getMetadata();
        Map<String, String> queryString = new HashMap<>();
        queryString.put("practice_id", metadata.getPracticeId());
        queryString.put("practice_mgmt", metadata.getPracticeMgmt());
        queryString.put("patient_id", metadata.getPatientId());

        TransitionDTO transitionBalance = paymentReceiptModel.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
        WorkflowServiceHelper.getInstance().execute(transitionBalance, paymentsCallBack, queryString);
    }

    private WorkflowServiceCallback paymentsCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ViewPaymentBalanceHistoryActivity.setIsPaymentDone(true);
            PatientNavigationHelper.setAccessPaymentsBalances(true);
            PatientNavigationHelper.getInstance(context).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //SystemUtil.showDefaultFailureDialog(InTakeWebViewActivity.this);
            Log.e(context.getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
