package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.payments.adapter.ExistingChargesItemAdapter;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeEditOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeOneTimePaymentFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmenendez on 1/31/18
 */
public class PracticePaymentPlanDetailsDialogFragment extends BaseDialogFragment {

    private PaymentPlanDTO paymentPlan;
    private boolean isPaymentPlanCompleted;
    private PaymentPlanEditInterface callback;
    private PaymentsModel paymentsModel;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the Payment Plan Dto
     * @param isCompleted
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PracticePaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanDTO paymentPlanDTO,
                                                                       boolean isCompleted) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean("isCompleted", isCompleted);

        PracticePaymentPlanDetailsDialogFragment fragment = new PracticePaymentPlanDetailsDialogFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentPlanEditInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        paymentPlan = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, getArguments());
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, getArguments());
        isPaymentPlanCompleted = getArguments().getBoolean("isCompleted", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_payment_plan_detail, container,
                false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view, Label.getLabel("payment_payment_plan_details_txt"));
        setUpExistingCharges(view, paymentPlan.getPayload().getLineItems());
        setInfo(view);
        setupButtons(view);
    }

    protected void setupToolbar(View view, String titleString) {
        Toolbar toolbar = view.findViewById(R.id.payment_toolbar);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.toolbar_title);
            title.setText(titleString);
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void setUpExistingCharges(View view, List<PaymentPlanLineItem> paymentPlanDetails) {
        RecyclerView existingChargesRecycler = view.findViewById(R.id.existingChargesRecycler);
        existingChargesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ExistingChargesItemAdapter adapter = new ExistingChargesItemAdapter(paymentPlanDetails);
        existingChargesRecycler.setAdapter(adapter);
    }

    private void setInfo(View view) {

        TextView paymentPlanNameTextView = view.findViewById(R.id.paymentPlanNameTextView);
        paymentPlanNameTextView.setText(paymentPlan.getPayload().getDescription());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        TextView paymentPlanValueTextView = view.findViewById(R.id.paymentPlanValueTextView);
        paymentPlanValueTextView.setText(currencyFormatter.format(paymentPlan.getPayload().getAmount()));

        String paymentAmount = currencyFormatter.format(paymentPlan.getPayload()
                .getPaymentPlanDetails().getAmount()) +
                paymentPlan.getPayload().getPaymentPlanDetails().getFrequencyString();
        TextView paymentAmountTextView = view.findViewById(R.id.paymentAmountTextView);
        paymentAmountTextView.setText(paymentAmount);

        String paymentsMadeOf = Label.getLabel("payment_plan_payments_made_value");
        int paymentCount = paymentPlan.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        int installmentTotal = paymentPlan.getPayload().getPaymentPlanDetails().getInstallments();
        int oneTimePayments = paymentPlan.getPayload().getPaymentPlanDetails()
                .getOneTimePayments().size();
        StringBuilder paymentsMadeBuilder = new StringBuilder().append(String.format(paymentsMadeOf, paymentCount, installmentTotal));
        if (oneTimePayments > 0) {
            paymentsMadeBuilder.append(" + ")
                    .append(oneTimePayments)
                    .append(" ")
                    .append(Label.getLabel("payment_history_detail_extra"));
        }
        TextView installmentCountTextView = view.findViewById(R.id.paymentsInstallmentsCount);
        installmentCountTextView.setText(paymentsMadeBuilder.toString());

        TextView nextInstallmentTextView = view.findViewById(R.id.nexyPaymentDate);
        nextInstallmentTextView.setText(getNextDate(paymentPlan.getPayload()));

        double totalAmount = paymentPlan.getPayload().getAmount();
        TextView planTotalTextView = view.findViewById(R.id.transaction_total);
        planTotalTextView.setText(currencyFormatter.format(totalAmount));

        double amountPaid = paymentPlan.getPayload().getAmountPaid();
        TextView balanceTextView = view.findViewById(R.id.planBalance);
        balanceTextView.setText(currencyFormatter.format(totalAmount - amountPaid));

        final ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload().findScheduledPayment(paymentPlan);
        if (scheduledPayment != null
                && paymentPlan.getPayload().getPaymentPlanDetails().getPaymentPlanStatus()
                .equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
            ScheduledPaymentPayload scheduledPayload = scheduledPayment.getPayload();

            View scheduledPaymentLayout = view.findViewById(R.id.scheduledPaymentLayout);
            scheduledPaymentLayout.setVisibility(View.VISIBLE);

            String scheduledPaymentMessageString = String.format(Label.getLabel("payment.oneTimePayment.scheduled.details"),
                    currencyFormatter.format(scheduledPayload.getAmount()),
                    DateUtil.getInstance().setDateRaw(scheduledPayload.getPaymentDate()).toStringWithFormatMmSlashDdSlashYyyy());

            TextView scheduledPaymentMessage = view.findViewById(R.id.scheduledPaymentMessage);
            scheduledPaymentMessage.setText(scheduledPaymentMessageString);

            View editScheduledPayment = view.findViewById(R.id.editScheduledPaymentButton);
            editScheduledPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEditScheduledPaymentDialog(scheduledPayment);
                }
            });
        }

        if (paymentPlan.getPayload().getPaymentPlanDetails().isCancelled()) {
            setUpViewForCanceledPayment(view, currencyFormatter);
        }
    }

    private void showEditScheduledPaymentDialog(ScheduledPaymentModel scheduledPayment) {
        PracticeEditOneTimePaymentFragment fragment = PracticeEditOneTimePaymentFragment
                .newInstance(paymentsModel, 0, paymentPlan, scheduledPayment);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    private void setUpViewForCanceledPayment(View view, NumberFormat currencyFormatter) {
        view.findViewById(R.id.cancelledInfoContainer).setVisibility(View.VISIBLE);
        view.findViewById(R.id.amountPaidContainer).setVisibility(View.VISIBLE);
        view.findViewById(R.id.nextPaymentContainer).setVisibility(View.GONE);
        view.findViewById(R.id.planBalanceContainer).setVisibility(View.GONE);
        view.findViewById(R.id.transactionTotalContainer).setVisibility(View.GONE);
        view.findViewById(R.id.planBalanceSeparator).setVisibility(View.GONE);
        view.findViewById(R.id.transactionSeparator).setVisibility(View.GONE);
        TextView amountPaidTextView = view.findViewById(R.id.amountPaidTextView);
        amountPaidTextView.setText(currencyFormatter.format(paymentPlan.getPayload().getAmountPaid()));
        TextView cancelledInfoTextView = view.findViewById(R.id.cancelledInfoTextView);
        cancelledInfoTextView.setText(String
                .format(Label.getLabel("payment.paymentPlanDetail.cancelInfo.label.canceledOn"),
                        DateUtil.getInstance().setDateRaw(paymentPlan.getMetadata().getUpdatedDate())
                                .getDateAsMonthLiteralDayOrdinalYear()));
    }

    private void setupButtons(View view) {
        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPaymentPlanDialog();
            }
        });
        Button oneTimePaymentButton = view.findViewById(R.id.OneTimePaymentButton);
        oneTimePaymentButton.setSelected(true);
        oneTimePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOneTimePaymentDialog();
            }
        });
        if (((BasePracticeActivity) getActivity()).getApplicationMode().getApplicationType()
                == ApplicationMode.ApplicationType.PRACTICE) {
            oneTimePaymentButton.setEnabled(paymentsModel.getPaymentPayload().getUserAuthModel()
                    .getUserAuthPermissions().canMakePayment);
            oneTimePaymentButton.setSelected(paymentsModel.getPaymentPayload().getUserAuthModel()
                    .getUserAuthPermissions().canMakePayment);
        }
        if (isPaymentPlanCompleted) {
            editButton.setVisibility(View.GONE);
            oneTimePaymentButton.setVisibility(View.GONE);
        }
    }

    private void showEditPaymentPlanDialog() {
        PracticeModePaymentPlanEditFragment fragment = PracticeModePaymentPlanEditFragment
                .newInstance(paymentsModel, paymentPlan);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    private void showOneTimePaymentDialog() {
        PracticeOneTimePaymentFragment fragment = PracticeOneTimePaymentFragment.newInstance(paymentsModel,
                0, paymentPlan);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    private String getNextDate(PaymentPlanPayloadDTO planPayload) {
        int drawDay;
        Calendar calendar = Calendar.getInstance();
        if (planPayload.getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            drawDay = planPayload.getPaymentPlanDetails().getDayOfMonth();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (currentDay > drawDay) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, drawDay);
        } else {
            int dayOfWeek = planPayload.getPaymentPlanDetails().getDayOfWeek() + 1; //Monday ==2
            if (calendar.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
                calendar.add(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
            } else {
                calendar.add(Calendar.DAY_OF_WEEK, dayOfWeek - calendar.get(Calendar.DAY_OF_WEEK));
            }
            drawDay = calendar.get(Calendar.DAY_OF_MONTH);
        }

        ApplicationPreferences preferences = ((ISession) getActivity()).getApplicationPreferences();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM ", new Locale(preferences.getUserLanguage()));
        return dateFormat.format(calendar.getTime()) + StringUtil.getOrdinal(preferences.getUserLanguage(), drawDay);
    }
}
