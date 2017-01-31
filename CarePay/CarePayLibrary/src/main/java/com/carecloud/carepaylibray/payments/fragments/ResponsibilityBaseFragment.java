package com.carecloud.carepaylibray.payments.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.google.gson.Gson;

import java.util.List;

public abstract class ResponsibilityBaseFragment extends BaseCheckinFragment implements PaymentDetailsDialog.PayNowClickListener {

    protected static final String LOG_TAG = ResponsibilityBaseFragment.class.getSimpleName();
    protected AppCompatActivity appCompatActivity;
//    protected String copayStr = "";
//    protected String previousBalanceStr = "";

    protected PaymentsModel paymentDTO = null;
    protected String totalResponsibilityString;
    protected String paymentDetailsString;
    protected String previousBalanceString;
    protected String insuranceCopayString;
    protected String payTotalAmountString;
    protected String payPartialAmountString;
    protected String paymentsTitleString;
    protected String payLaterString;
    protected double total;
    protected String paymentInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    protected void fillDetailAdapter(View view, List<PatiencePayloadDTO> paymentList){
        RecyclerView paymentDetailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.responsibility_line_item_recycle_view));
        paymentDetailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(), paymentDTO, paymentList, this);
        paymentDetailsListRecyclerView.setAdapter(adapter);
    }

    protected void getPaymentInformation() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            String paymentsDTOString = arguments.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);
        }
    }

    /**
     * payment labels
     */
    protected void getPaymentLabels() {
        if (paymentDTO != null) {
            PaymentsMetadataModel paymentsMetadataDTO = paymentDTO.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    paymentDetailsString = paymentsLabelsDTO.getPaymentResponsibilityDetails();
                    totalResponsibilityString = paymentsLabelsDTO.getPaymentTotalResponsibility();
                    previousBalanceString = paymentsLabelsDTO.getPaymentPreviousBalance();
                    insuranceCopayString = paymentsLabelsDTO.getPaymentInsuranceCopay();

                    payTotalAmountString = paymentsLabelsDTO.getPaymentPayTotalAmountButton();
                    payPartialAmountString = paymentsLabelsDTO.getPaymentPartialAmountButton();
                    payLaterString = paymentsLabelsDTO.getPaymentResponsibilityPayLater();
                    paymentsTitleString = paymentsLabelsDTO.getPaymentButtonLabel();
                }
            }
        }
    }

    @Override
    public void onPayNowButtonClicked() {
        doPayment();
    }

    protected abstract void doPayment();

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }
}