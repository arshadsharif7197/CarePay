package com.carecloud.carepaylibray.payments.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.services.PaymentsService;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public abstract class ResponsibilityBaseFragment extends BaseCheckinFragment implements PaymentDetailsDialog.PayNowClickListener {

    protected static final String LOG_TAG = ResponsibilityBaseFragment.class.getSimpleName();
    protected AppCompatActivity appCompatActivity;
    protected String copayStr = "";
    protected String previousBalanceStr = "";

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }


    protected void getPaymentInformation() {
        PaymentsService paymentService = (new BaseServiceGenerator(getActivity()))
                .createService(PaymentsService.class);
        Call<PaymentsModel> call = paymentService.fetchPaymentInformation();
        call.enqueue(new Callback<PaymentsModel>() {
            @Override
            public void onResponse(Call<PaymentsModel> call, Response<PaymentsModel> response) {
                PaymentsModel paymentsDTO = response.body();
            }

            @Override
            public void onFailure(Call<PaymentsModel> call, Throwable throwable) {
                SystemUtil.showFaultDialog(getActivity());
                Log.e(getActivity().getString(R.string.alert_title_server_error), "");
            }
        });
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
                    paymentsTitleString = paymentsLabelsDTO.getDemographicsPaymentTitle();
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