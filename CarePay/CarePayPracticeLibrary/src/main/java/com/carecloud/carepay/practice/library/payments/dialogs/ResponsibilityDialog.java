package com.carecloud.carepay.practice.library.payments.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItemMetadata;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

public class ResponsibilityDialog extends Dialog {

    private Context context;
    private PaymentsModel paymentsModel;
    private PaymentsPatientBalancessDTO patientPayments;
    private List<PatienceBalanceDTO> balances;
    private PayResponsibilityCallback callback;

    /**
     * Constructor
     * @param context context
     * @param paymentsModel paymentsModel
     * @param patientPayments PaymentsPatientBalancessDTO
     */
    public ResponsibilityDialog(Context context, PaymentsModel paymentsModel, PaymentsPatientBalancessDTO patientPayments, PayResponsibilityCallback callback) {
        super(context);
        this.context = context;
        this.paymentsModel = paymentsModel;
        this.patientPayments = patientPayments;
        this.callback = callback;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_responsibility);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.53);
//        getWindow().setAttributes(params);

        onInitialization();
        handleException();
    }

    private void handleException() {
        Thread t = Thread.currentThread();
        t.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    @SuppressLint("InflateParams")
    private void onInitialization() {
        final DemographicsSettingsPersonalDetailsPayloadDTO personalDetails = patientPayments.getDemographics().getPayload().getPersonalDetails();
        ((TextView) findViewById(R.id.patient_full_name)).setText(personalDetails.getFirstName() + " " + personalDetails.getLastName());

        ImageView profilePhoto = (ImageView) findViewById(R.id.patient_profile_photo);
        final TextView shortName = (TextView) findViewById(R.id.patient_profile_short_name);
        shortName.setText(StringUtil.onShortDrName(personalDetails.getFirstName() + " " + personalDetails.getLastName()));

        String photoUrl = personalDetails.getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    shortName.setText(StringUtil.onShortDrName(personalDetails.getFirstName() + " " + personalDetails.getLastName()));
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform()).resize(88, 88).into(profilePhoto);

            RequestCreator load = builder.build().load(photoUrl);
            ImageView bgImage = (ImageView) findViewById(R.id.profile_bg_image);
            load.fit().into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
        }

        PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
        balances = patientPayments.getBalances();
        if (balances != null && balances.size() > 0) {
            ScrollView amountDetails = (ScrollView) findViewById(R.id.payment_responsibility_balance_details);//TODO this needs to be in an adapter

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

                if (i == 0 && payload.size() > 1) {
                    detailsView.setVisibility(View.VISIBLE);
                }
            }

            ((TextView) findViewById(R.id.payment_responsibility_balance)).setText(
                    paymentsLabel.getPracticePaymentsDetailDialogBalance() + ": "
                            + StringUtil.getFormattedBalanceAmount(totalAmount));

            Button paymentPlanButton = (Button) findViewById(R.id.payment_plan_button);
            paymentPlanButton.setText(paymentsLabel.getPracticePaymentsDetailDialogPaymentPlan());
            SystemUtil.setGothamRoundedMediumTypeface(context, paymentPlanButton);
            paymentPlanButton.setEnabled(paymentsModel.getPaymentsMetadata().hasPaymentPlan());
            paymentPlanButton.setOnClickListener(new View.OnClickListener() {
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
                    if (HttpConstants.getDeviceInformation().getDeviceType().equals("Clover")) {
                        setCloverPayment();
                    }
                }
            });

            findViewById(R.id.payment_responsibility_close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            ((TextView) findViewById(R.id.payment_responsibility_close_label))
                    .setText(paymentsLabel.getPracticePaymentsDetailDialogCloseButton());

            ((TextView) findViewById(R.id.patient_provider_name))
                    .setText(getProviderName(balances.get(0).getMetadata().getPatientId()));
        }
    }

    private String getProviderName(String patientId) {
        if (!StringUtil.isNullOrEmpty(patientId)) {
            List<ProviderIndexDTO> providerIndex = paymentsModel.getPaymentPayload().getProviderIndex();

            for (ProviderIndexDTO providerIndexDTO : providerIndex) {
                List<String> patientIds = providerIndexDTO.getPatientIds();

                for (String id : patientIds) {
                    if (id.equalsIgnoreCase(patientId)) {
                        return providerIndexDTO.getName();
                    }
                }
            }
        }

        return StringUtil.getLabelForView("");
    }

    private void setCloverPayment()
    {
        Gson gson = new Gson();
        String patientPaymentMetaDataString = gson.toJson(balances.get(0).getMetadata());
        String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
        Intent intent = new Intent();
        intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_METADATA, patientPaymentMetaDataString);
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, balances.get(0).getPayload().get(0).getAmount());
        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);
        List<PaymentLineItem> paymentLineItems = new ArrayList<>();

        for(PatienceBalanceDTO balance : balances) {

            PaymentLineItem paymentLineItem = new PaymentLineItem();
            paymentLineItem.setAmount(balance.getPayload().get(0).getAmount());
            paymentLineItem.setDescription(balance.getPayload().get(0).getType());

            PaymentLineItemMetadata metadata = new PaymentLineItemMetadata();
            metadata.setPatientID(balance.getMetadata().getPatientId());
            metadata.setPracticeID(balance.getMetadata().getPracticeId());
//                metadata.setProviderID(balance.getMetadata().getProviderID()); //TODO this is missing in the DTO
//                metadata.setLocationID(balance.getMetadata().getLocationID()); //TODO this is missing in the DTO

            paymentLineItems.add(paymentLineItem);

        }

        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));

        ((Activity)context).startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());
//        getContext().startActivity(intent, new Bundle());
        dismiss();
    }


    public interface PayResponsibilityCallback{

        void payFullResponsibility(PatienceBalanceDTO balance);

        void payPartialResponsibility(PatienceBalanceDTO balance, double amount);
    }

}
