package com.carecloud.carepay.practice.library.payments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.XPatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.XPendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.XPendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class ResponsibilityDialog extends Dialog {

    private static final String TAG = "ResponsibilityDialog";

    private Context context;
    private final String leftLabel;
    private final String rightLabel;
    private PaymentsModel paymentsModel;
    private XPatientBalanceDTO patientPayments;
    private PayResponsibilityCallback callback;
    private PaymentNavigationCallback navigationCallback;
    private double owedAmount = 0;

    /**
     * Constructor
     * @param context context
     * @param leftLabel label for left button
     * @param rightLabel label for right button
     * @param paymentsModel Payments Model
     * @param patientPayments XPatientBalanceDTO
     */
    public ResponsibilityDialog(Context context,
                                String leftLabel,
                                String rightLabel,
                                PaymentsModel paymentsModel,
                                XPatientBalanceDTO patientPayments,
                                PayResponsibilityCallback callback) {
        super(context);
        this.context = context;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
        this.paymentsModel = paymentsModel;
        this.patientPayments = patientPayments;
        this.callback = callback;

        setupNavigationCallback();
    }

    private void setupNavigationCallback(){
        try{
            navigationCallback = (PaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Provided context must implement PaymentsNavigationCallback");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_payment_responsibility);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        onInitialization();
        handleException();
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread t = Thread.currentThread();
        t.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    @SuppressLint("InflateParams")
    private void onInitialization() {
        initializeHeader();

        PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
        initializeBody(paymentsLabel);

        findViewById(R.id.payment_responsibility_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        initializeFooter();
    }

    private void initializeHeader() {
        final PatientModel patientModel = getPatientModel();
        if (null == patientModel) {
            Log.e(TAG, "Patient not found");
            return;
        }

        ((TextView) findViewById(R.id.patient_full_name)).setText(patientModel.getFullName());

        ImageView profilePhoto = (ImageView) findViewById(R.id.patient_profile_photo);
        final TextView shortName = (TextView) findViewById(R.id.patient_profile_short_name);
        shortName.setText(patientModel.getShortName());

        String photoUrl = patientModel.getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    shortName.setText(patientModel.getShortName());
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform()).resize(88, 88).into(profilePhoto);

            RequestCreator load = builder.build().load(photoUrl);
            ImageView bgImage = (ImageView) findViewById(R.id.profile_bg_image);
            load.fit().into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
        }
    }

    private PatientModel getPatientModel() {
        if (null != patientPayments) {
            return patientPayments.getDemographics().getPayload().getPersonalDetails();
        }

        if (!paymentsModel.getPaymentPayload().getPatients().isEmpty()) {
            return paymentsModel.getPaymentPayload().getPatients().get(0);
        }

        return null;
    }

    private void initializeBody(PaymentsLabelDTO labels) {
        if (null != patientPayments && null != patientPayments.getBalances() && !patientPayments.getBalances().isEmpty()) {
            List<XPendingBalanceDTO> balances = patientPayments.getBalances();

            initializeOwedAmount(balances);

            if (owedAmount > 0) {
                initializePaymentLines(balances);
            }

            initializePatientProvider(balances);
        }

        initializeOwedAmountTextView(labels);
    }

    private void initializePaymentLines(List<XPendingBalanceDTO> balances) {
        RecyclerView amountDetails = (RecyclerView) findViewById(R.id.payment_responsibility_balance_details);
        amountDetails.setLayoutManager(new LinearLayoutManager(context));

        String detailsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPracticePaymentsDetailDialogLabel();
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(context, paymentsModel, balances, navigationCallback, detailsLabel, dismissDetailsListener);
        amountDetails.setAdapter(adapter);
    }

    private void initializePatientProvider(List<XPendingBalanceDTO> balances) {
        String provider;

        List<XPendingBalancePayloadDTO> XPendingBalancePayloadDTOList = balances.get(0).getPayload();
        if (!XPendingBalancePayloadDTOList.isEmpty() && !XPendingBalancePayloadDTOList.get(0).getDetails().isEmpty()) {

            provider = XPendingBalancePayloadDTOList.get(0).getDetails().get(0).getProvider().getName();

        } else {

            provider = getProviderName(balances.get(0).getMetadata().getPatientId());

        }

        TextView patientProviderNameTextView = (TextView) findViewById(R.id.patient_provider_name);
        patientProviderNameTextView.setText(provider);
    }

    private void initializeOwedAmount(List<XPendingBalanceDTO> balances) {
        for (XPendingBalanceDTO patiencePayload : balances) {
            if (!patiencePayload.getPayload().isEmpty()) {
                owedAmount += patiencePayload.getPayload().get(0).getAmount();
            }
        }
    }

    private void initializeOwedAmountTextView(PaymentsLabelDTO labels) {
        String text = labels.getPracticePaymentsDetailDialogBalance() + ": " +
                StringUtil.getFormattedBalanceAmount(owedAmount);

        ((TextView) findViewById(R.id.payment_responsibility_balance)).setText(text);
    }

    private OnDismissListener dismissDetailsListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            ResponsibilityDialog.this.dismiss();
        }
    };

    private void initializeFooter() {
        Button leftButton = (Button) findViewById(R.id.payment_plan_button);

        if (null == leftLabel) {
            leftButton.setVisibility(View.GONE);
        } else {
            leftButton.setText(leftLabel);
            SystemUtil.setGothamRoundedMediumTypeface(context, leftButton);
            leftButton.setEnabled(paymentsModel.getPaymentsMetadata().hasPaymentPlan());
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != callback) {
                        callback.onLeftActionTapped();
                    }

                    dismiss();
                }
            });
        }

        Button rightButton = (Button) findViewById(R.id.payment_pay_button);
        rightButton.setText(rightLabel);
        SystemUtil.setGothamRoundedMediumTypeface(context, rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onRightActionTapped(owedAmount);
                }

                dismiss();
            }
        });
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

        return "";
    }

    public interface PayResponsibilityCallback{

        void onLeftActionTapped();

        void onRightActionTapped(double amount);
    }
}
