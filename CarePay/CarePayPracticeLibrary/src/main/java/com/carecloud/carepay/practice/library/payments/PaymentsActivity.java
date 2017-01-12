package com.carecloud.carepay.practice.library.payments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.payments.adapter.PracticePaymentsAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityDialog;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;

import java.util.List;
import java.util.Locale;

public class PaymentsActivity extends BasePracticeActivity {

    private PaymentsModel paymentsModel;
    private List<PaymentsPatientBalancessDTO> patientBalancesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_payment);

        paymentsModel = getConvertedDTO(PaymentsModel.class);

        setLabels();
        populateList();
    }

    private void setLabels() {
        if (paymentsModel != null) {
            PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            if (paymentsLabel != null) {
                ((CarePayTextView) findViewById(R.id.practice_payment_title)).setText(paymentsLabel.getPracticePaymentsHeader());
                ((CarePayTextView) findViewById(R.id.practice_payment_go_back)).setText(paymentsLabel.getPracticePaymentsBackLabel());
                ((CarePayTextView) findViewById(R.id.practice_payment_find_patient)).setText(paymentsLabel.getPracticePaymentsFindPatientLabel());
                ((CarePayTextView) findViewById(R.id.practice_payment_filter_label)).setText(paymentsLabel.getPracticePaymentsFilter());
                ((CarePayTextView) findViewById(R.id.practice_payment_in_office_label)).setText(paymentsLabel.getPracticePaymentsInOffice());
            }
        }
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null
                && paymentsModel.getPaymentPayload().getPatientBalances().size() > 0) {

            patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();
            PracticePaymentsAdapter paymentsAdapter = new PracticePaymentsAdapter(this, paymentsModel);

            RecyclerView inOfficeList = (RecyclerView) findViewById(R.id.practice_payment_in_office_list);
            inOfficeList.setHasFixedSize(true);
            inOfficeList.setLayoutManager(new GridLayoutManager(this, 2));
            inOfficeList.setAdapter(paymentsAdapter);

            ((CarePayTextView) findViewById(R.id.practice_payment_in_office_count))
                    .setText(String.format(Locale.getDefault(), "%1s", patientBalancesList.size()));
        }
    }

    public void onPatientItemClick(int selectedIndex) {
        ResponsibilityDialog responsibilityDialog = new ResponsibilityDialog(this, paymentsModel, selectedIndex);
        responsibilityDialog.show();
    }
}
