package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
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
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
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
        return inflater.inflate(R.layout.practice_payment_plan_detail_fragment, container, false);
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
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(titleString);
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setUpExistingCharges(View view, List<PaymentPlanLineItem> paymentPlanDetails) {
        RecyclerView existingChargesRecycler = (RecyclerView) view.findViewById(R.id.existingChargesRecycler);
        existingChargesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ExistingChargesItemAdapter adapter = new ExistingChargesItemAdapter(paymentPlanDetails);
        existingChargesRecycler.setAdapter(adapter);
    }

    private void setInfo(View view) {

        TextView paymentPlanNameTextView = (TextView) view.findViewById(R.id.paymentPlanNameTextView);
        paymentPlanNameTextView.setText(paymentPlan.getPayload().getDescription());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        TextView paymentPlanValueTextView = (TextView) view.findViewById(R.id.paymentPlanValueTextView);
        paymentPlanValueTextView.setText(currencyFormatter.format(paymentPlan.getPayload().getAmount()));

        TextView paymentAmountTextView = (TextView) view.findViewById(R.id.paymentAmountTextView);
        paymentAmountTextView.setText(currencyFormatter.format(paymentPlan.getPayload()
                .getPaymentPlanDetails().getAmount()));

        int paymentCount = paymentPlan.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        int installmentTotal = paymentPlan.getPayload().getPaymentPlanDetails().getInstallments();
        TextView installmentCountTextView = (TextView) view.findViewById(R.id.paymentsInstallmentsCount);
        installmentCountTextView.setText(String.format(Label.getLabel("payment_plan_payments_made_value"),
                paymentCount, installmentTotal));

        TextView nextInstallmentTextView = (TextView) view.findViewById(R.id.nexyPaymentDate);
        nextInstallmentTextView.setText(getNextDate(paymentPlan.getPayload()));

        double totalAmount = paymentPlan.getPayload().getAmount();
        TextView planTotalTextView = (TextView) view.findViewById(R.id.transaction_total);
        planTotalTextView.setText(currencyFormatter.format(totalAmount));

        double amountPaid = paymentPlan.getPayload().getAmountPaid();
        TextView balanceTextView = (TextView) view.findViewById(R.id.planBalance);
        balanceTextView.setText(currencyFormatter.format(totalAmount - amountPaid));
    }

    private void setupButtons(View view) {
        Button editButton = (Button) view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEditPaymentPlan(paymentsModel, paymentPlan);
                dismiss();
            }
        });
        Button oneTimePaymentButton = (Button) view.findViewById(R.id.OneTimePaymentButton);
        oneTimePaymentButton.setSelected(true);
        oneTimePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onStartOneTimePayment(paymentsModel, paymentPlan);
                dismiss();
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

    private String getNextDate(PaymentPlanPayloadDTO planPayload) {
        int drawDay = planPayload.getPaymentPlanDetails().getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (currentDay > drawDay) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, drawDay);

        ApplicationPreferences preferences = ((ISession) getActivity()).getApplicationPreferences();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM ", new Locale(preferences.getUserLanguage()));
        return dateFormat.format(calendar.getTime()) + StringUtil.getOrdinal(preferences.getUserLanguage(), drawDay);
    }
}
