package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.NumberFormat;
import java.util.Locale;

public class ResponsibilityFragment extends ResponsibilityBaseFragment {

    private PendingBalanceDTO selectedBalance;
    private PaymentFragmentActivityInterface toolbarCallback;
    private NumberFormat currencyFormat;

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO,
                                                     PendingBalanceDTO selectedBalance,
                                                     boolean payLaterButtonVisibility) {
        return newInstance(paymentsDTO, selectedBalance, payLaterButtonVisibility, null);
    }

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO,
                                                     PendingBalanceDTO selectedBalance,
                                                     boolean payLaterButtonVisibility,
                                                     String title) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        if (selectedBalance != null) {
            DtoHelper.bundleDto(args, selectedBalance);
        }
        args.putBoolean("payLaterButtonVisibility", payLaterButtonVisibility);
        if (title != null) {
            args.putString("title", title);
        }
        ResponsibilityFragment fragment = new ResponsibilityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            toolbarCallback = (PaymentFragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            //this calback is optional
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPaymentInformation();
        hasPermissionToViewBalanceDetails = paymentDTO.getPaymentPayload()
                .canViewBalanceDetails(selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_responsibility, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle("");
        getPaymentLabels();
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        if (getArguments().getString("title") != null) {
            paymentsTitleString = getArguments().getString("title");
        }
        title.setText(paymentsTitleString);

        if (toolbarCallback != null) {
            toolbarCallback.displayToolbar(false, null);
        }


        total = 0;
        fillDetailAdapter(view, selectedBalance.getPayload());
        for (PendingBalancePayloadDTO payment : selectedBalance.getPayload()) {
            total = SystemUtil.safeAdd(total, payment.getAmount());
            if (!payment.getType().equals(PendingBalancePayloadDTO.PATIENT_BALANCE)) {
                //not an amount that can be added to a plan
                nonBalanceTotal = SystemUtil.safeAdd(nonBalanceTotal, payment.getAmount());
            }
        }
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        TextView responseTotal = view.findViewById(R.id.respons_total);
        responseTotal.setText(currencyFormat.format(total));

        setUpBottomSheet(view);

    }

    protected void setUpBottomSheet(final View view) {
        View payTotalAmountContainer = view.findViewById(R.id.payTotalAmountContainer);
        View partialPaymentContainer = view.findViewById(R.id.partialPaymentContainer);
        View payLaterContainer = view.findViewById(R.id.payLaterContainer);
        payLaterContainer.setVisibility(getArguments().getBoolean("payLaterButtonVisibility") ? View.VISIBLE : View.GONE);
        payLaterContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPayLaterClicked(selectedBalance);
            }
        });

        if (total > 0) {
            payTotalAmountContainer.setClickable(true);
            payTotalAmountContainer.setEnabled(true);
            partialPaymentContainer.setEnabled(true);
            partialPaymentContainer.setEnabled(true);
            payLaterContainer.setEnabled(true);
            payLaterContainer.setEnabled(true);

//            payTotalAmountContainer.setTextColor(Color.WHITE);
            int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
//            partialPaymentContainer.setTextColor(color);
//            payLaterContainer.setTextColor(color);
        }


        payTotalAmountContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPayment();
            }
        });

        partialPaymentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPartialPaymentClicked(total, selectedBalance);
            }
        });

        partialPaymentContainer.setVisibility(isPartialPayAvailable(selectedBalance.getMetadata()
                .getPracticeId(), total) ? View.VISIBLE : View.GONE);

        boolean canMakePayments = paymentDTO.getPaymentPayload()
                .canMakePayments(selectedBalance.getMetadata().getPracticeId());
        if (!canMakePayments) {
            payTotalAmountContainer.setVisibility(View.GONE);
            partialPaymentContainer.setVisibility(View.GONE);
        }

        boolean paymentPlanEnabled = !paymentDTO.getPaymentPayload().isPaymentPlanCreated() &&
                isPaymentPlanAvailable(selectedBalance.getMetadata().getPracticeId(), total);
        View paymentPlanContainer = view.findViewById(R.id.paymentPlanContainer);
        paymentPlanContainer.setVisibility(paymentPlanEnabled ? View.VISIBLE : View.GONE);
        paymentPlanContainer.setEnabled(paymentPlanEnabled);
        paymentPlanContainer.setClickable(paymentPlanEnabled);
        paymentPlanContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPaymentPlanAction(paymentDTO);
            }
        });
        if (mustAddToExisting) {
            TextView paymentPlanTextView = paymentPlanContainer.findViewById(R.id.paymentPlanTextView);
            paymentPlanTextView.setText(Label.getLabel("payment_plan_add_existing"));
        }

        final View shadow = view.findViewById(R.id.shadow);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                shadow.setAlpha(slideOffset);
            }
        });
        Button consolidatedPaymentButton = view.findViewById(R.id.consolidatedPaymentButton);
        consolidatedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        TextView totalPatientResponsibilityValue = view.findViewById(R.id.totalPatientResponsibilityValue);
        totalPatientResponsibilityValue.setText(currencyFormat.format(total));
    }

    @Override
    protected void getPaymentInformation() {
        super.getPaymentInformation();
        Bundle args = getArguments();
        if (args != null) {
            selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        }
        if (selectedBalance == null) {
            selectedBalance = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);
        }
    }

    protected void doPayment() {
        createPaymentModel(total);
        actionCallback.onPayButtonClicked(total, paymentDTO);
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        actionCallback.displayBalanceDetails(paymentDTO, paymentLineItem, selectedBalance);
    }


}