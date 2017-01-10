package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.patient.payment.adapter.PaymentHistoryDetailAdapter;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.BaseAmountInfoDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prem_mourya on 10/6/2016.
 */

public class PaymentAmountInfoDialog extends BaseAmountInfoDialog {

    private JSONObject jsonObject;
    private Context context;
    private View rootView;
    private LinearLayout addChildDynamicLayout;
    private CarePayTextView previousTitleTextView;
    private CarePayTextView previousBalanceAmountTextView;
    private Button payNowButton;
    PaymentsPatientBalancessDTO paymentsPatientBalancessDTO;
    private PaymentsModel paymentDTO;
    PaymentsModel pmodel;


    /**
     *
     *  @param context context
     * @param pmodel payment model
     */
    public PaymentAmountInfoDialog(Context context, PaymentsPatientBalancessDTO model, PaymentsModel pmodel) {
        super(context, model);
        this.context = context;
        this.pmodel = pmodel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rootView = getRootView();
        this.addChildDynamicLayout = (LinearLayout) this.rootView.findViewById(R.id.addChildDynamicLayout);
        onInitialization();
    }

    private void onInitialization() {
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_payment_info, null);
        previousTitleTextView = (CarePayTextView) childActionView.findViewById(R.id.patientTitleTextView);
        previousBalanceAmountTextView = (CarePayTextView) childActionView.findViewById(R.id.patientBalanceAmountTextView);
        payNowButton = (Button) childActionView.findViewById(R.id.payNowButton);

        onSettingStyle();
        onSetListener();
        //payNowButton.setText(model.getPaymentsMetadata().getPaymentsLabel().getPaymentDetailsPayNow());
        this.addChildDynamicLayout.addView(childActionView);
        String amount= StringUtil.getFormattedBalanceAmount(Double.parseDouble(model.getPendingRepsonsibility()));
        paymentAmountTextView.setText(amount);
        previousBalanceAmountTextView.setText(amount);

        RecyclerView detailView =
                (RecyclerView) childActionView.findViewById(com.carecloud.carepay.patient.R.id.detailBalanceRecView);
        PaymentHistoryDetailAdapter paymentHistoryDetailAdapter  = new PaymentHistoryDetailAdapter(context, model.getBalances());
        detailView.setLayoutManager(new LinearLayoutManager(context));
        detailView.setAdapter(paymentHistoryDetailAdapter);
    }

    private void onSettingStyle() {
        previousTitleTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
        previousBalanceAmountTextView.setTextColor(ContextCompat.getColor(context,R.color.textview_default_textcolor));
    }

    private void onSetListener() {
        payNowButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.payNowButton) {
            onPayNow();
        }
    }

    private void onPayNow() {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String jsonString = gson.toJson(pmodel);
        bundle.putString(PatientNavigationHelper.class.getSimpleName(), jsonString);
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(PatientNavigationHelper.class.getSimpleName(), bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


}
