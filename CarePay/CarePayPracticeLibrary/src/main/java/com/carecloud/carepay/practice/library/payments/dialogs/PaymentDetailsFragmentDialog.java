package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentItemsListAdapter;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PaymentDetailsFragmentDialog extends BasePaymentDetailsFragmentDialog {


    /**
     * @param paymentsModel  the payment model
     * @param paymentPayload the PendingBalancePayloadDTO
     * @return new instance of a PaymentDetailsFragmentDialog
     */
    public static PaymentDetailsFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           PendingBalancePayloadDTO paymentPayload,
                                                           boolean hideHeaderAndFooter) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPayload);
        args.putBoolean("hideHeaderAndFooter", hideHeaderAndFooter);

        PaymentDetailsFragmentDialog dialog = new PaymentDetailsFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            closeView.setOnClickListener(this);
        }
        TextView closeText = (TextView) view.findViewById(R.id.closeTextView);
        if (closeText != null) {
            closeText.setText(getCancelString());
        }
        ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel_img);
        if (cancelImage != null) {
            cancelImage.setImageResource(getCancelImageResource());
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
    }

    protected void onInitialization(final View view) {

        Button payNowButton = (Button) view.findViewById(R.id.payment_details_pay_now_button);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPayButtonClicked(paymentPayload.getAmount(), paymentReceiptModel);
                dismiss();
            }
        });

        if (getArguments().getBoolean("hideHeaderAndFooter", false)) {
            view.findViewById(R.id.profile_image_layout).setVisibility(View.GONE);
            view.findViewById(R.id.payButtonContainer).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.payment_receipt_details_view)
                    .setBackground(getResources().getDrawable(R.drawable.background_bottom_rounded_white_border));
            view.findViewById(R.id.payment_details_total_paid)
                    .setBackground(getResources().getDrawable(R.drawable.practice_mode_dialog_header_bg));
        }

        String totalAmount = StringUtil.getFormattedBalanceAmount(paymentPayload.getAmount());
        String amountBalanceLabel = Label.getLabel("payment_details_patient_balance_label");
        PatientModel personalDetails = paymentReceiptModel.getPaymentPayload().getPatientBalances()
                .get(0).getDemographics().getPayload().getPersonalDetails();
        final String name = personalDetails.getFirstName();
        final String lastName = personalDetails.getLastName();
        final ImageView profilePictureImageView = (ImageView) view.findViewById(R.id.patient_profile_photo);
        Picasso.with(getContext()).load(personalDetails.getProfilePhoto())
                .transform(new CircleImageTransform())
                .resize(60, 60)
                .into(profilePictureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        profilePictureImageView.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.avTextView).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        profilePictureImageView.setVisibility(View.GONE);
                        view.findViewById(R.id.avTextView).setVisibility(View.VISIBLE);
                    }
                });
        ((TextView) view.findViewById(R.id.patient_full_name)).setText(String.format("%s %s", name, lastName));
        ((TextView) view.findViewById(R.id.payment_details_total_paid))
                .setText(String.format("%s: %s", amountBalanceLabel, totalAmount));
        ((TextView) view.findViewById(R.id.avTextView))
                .setText(StringUtil.getShortName(name + " " + lastName));


        payNowButton.setText(Label.getLabel("payment_pay_total_button"));
        RecyclerView paymentDetailsRecyclerView = ((RecyclerView) view.findViewById(R.id.payment_receipt_details_view));
        paymentDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        PaymentItemsListAdapter adapter = new PaymentItemsListAdapter(getContext(), paymentPayload.getDetails());
        paymentDetailsRecyclerView.setAdapter(adapter);
    }

    @Override
    protected int getCancelImageResource() {
        return R.drawable.icn_arrow_left;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_details;
    }

}
