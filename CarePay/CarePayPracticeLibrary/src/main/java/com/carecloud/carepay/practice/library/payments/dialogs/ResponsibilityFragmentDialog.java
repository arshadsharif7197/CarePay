package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.HeaderModel;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class ResponsibilityFragmentDialog extends BaseDialogFragment implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    private static final String TAG = "ResponsibilityDialog";

    private String leftLabel;
    private String rightLabel;
    private PaymentsModel paymentsModel;
    private PatientBalanceDTO patientBalance;
    private PayResponsibilityCallback callback;
    private double owedAmount = 0;
    private HeaderModel headerModel;

    @Override
    protected String getCancelString() {
        return null;
    }

    @Override
    protected int getCancelImageResource() {
        return com.carecloud.carepaylibrary.R.drawable.icn_close;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_responsibility;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        callback.onDetailItemClick(paymentsModel, paymentLineItem);
    }

    /**
     * @param paymentsModel the payment model
     * @param leftLabel     the label of the left bottom button
     * @param rightLabel    the label of the right bottom button
     * @return new instance of a ResponsibilityFragmentDialog
     */
    public static ResponsibilityFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           String leftLabel, String rightLabel,
                                                           HeaderModel headerModel) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, headerModel);
        args.putString("leftLabel", leftLabel);
        args.putString("rightLabel", rightLabel);

        ResponsibilityFragmentDialog dialog = new ResponsibilityFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
        headerModel = DtoHelper.getConvertedDTO(HeaderModel.class, arguments);
        patientBalance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        leftLabel = arguments.getString("leftLabel");
        rightLabel = arguments.getString("rightLabel");
        handleException();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PayResponsibilityCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PayResponsibilityCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread thread = Thread.currentThread();
        thread.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    private void onInitialization(View view) {
        initializeHeader(view);
        initializeBody(view);
        initializeFooter(view);
    }

    private void initializeHeader(View view) {
        ((TextView) view.findViewById(R.id.patient_full_name)).setText(headerModel.getHeaderFullTitle());
        ImageView profilePhoto = (ImageView) view.findViewById(R.id.patient_profile_photo);
        final TextView shortName = (TextView) view.findViewById(R.id.patient_profile_short_name);
        shortName.setText(headerModel.getHeaderShortTitle());

        String photoUrl = headerModel.getHeaderPhotoUrl();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    shortName.setText(headerModel.getHeaderShortTitle());
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform()).resize(88, 88).into(profilePhoto);

            RequestCreator load = builder.build().load(photoUrl);
            ImageView bgImage = (ImageView) view.findViewById(R.id.profile_bg_image);
            load.fit().into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
        }
        TextView patientProviderNameTextView = (TextView) view.findViewById(R.id.patient_provider_name);
        patientProviderNameTextView.setText(headerModel.getHeaderSubtitle());
    }

    private void initializeBody(View view) {
        if (null != patientBalance && null != patientBalance.getBalances() && !patientBalance.getBalances().isEmpty()) {
            List<PendingBalanceDTO> balances = patientBalance.getBalances();
            initializeOwedAmount(balances);
            if (owedAmount > 0) {
                initializePaymentLines(view, balances);
            }
        }
        initializeOwedAmountTextView(view);
    }

    private void initializePaymentLines(View view, List<PendingBalanceDTO> balances) {
        RecyclerView amountDetails = (RecyclerView) view.findViewById(R.id.payment_responsibility_balance_details);
        amountDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(getContext(), balances, this);
        amountDetails.setAdapter(adapter);
    }

    private void initializeOwedAmount(List<PendingBalanceDTO> balances) {
        for (PendingBalanceDTO patiencePayload : balances) {
            if (!patiencePayload.getPayload().isEmpty()) {
                owedAmount += patiencePayload.getPayload().get(0).getAmount();
            }
        }
    }

    private void initializeOwedAmountTextView(View view) {
        String text = Label.getLabel("practice_payments_detail_dialog_balance") + ": " +
                StringUtil.getFormattedBalanceAmount(owedAmount);
        ((TextView) view.findViewById(R.id.payment_responsibility_balance)).setText(text);
    }

    private void initializeFooter(View view) {
        Button leftButton = (Button) view.findViewById(R.id.payment_plan_button);

        if (null == leftLabel) {
            leftButton.setVisibility(View.GONE);
        } else {
            leftButton.setText(leftLabel);
            SystemUtil.setGothamRoundedMediumTypeface(getContext(), leftButton);
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

        Button rightButton = (Button) view.findViewById(R.id.payment_pay_button);
        rightButton.setText(rightLabel);
        SystemUtil.setGothamRoundedMediumTypeface(getContext(), rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    callback.onRightActionTapped(paymentsModel, owedAmount);
                }

                dismiss();
            }
        });
    }

    public interface PayResponsibilityCallback {

        void onLeftActionTapped();

        void onRightActionTapped(PaymentsModel paymentsModel, double amount);

        void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem);
    }

}
