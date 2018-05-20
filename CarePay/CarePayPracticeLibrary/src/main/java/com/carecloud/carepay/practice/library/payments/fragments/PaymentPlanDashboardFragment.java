package com.carecloud.carepay.practice.library.payments.fragments;

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
import com.carecloud.carepay.practice.library.payments.adapter.PaymentPlanDashboardAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.PaymentPlanDashboardInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 16/05/18.
 */
public class PaymentPlanDashboardFragment extends BaseDialogFragment
        implements PaymentPlanDashboardAdapter.PaymentPlanDashboardItemInterface {

    private PaymentsModel paymentsModel;
    public static final String PAYMENT_PLAN_COMPLETED = "completed";
    public static final String PAYMENT_PLAN_UNCOMPLETED = "processing";
    private PaymentPlanDashboardInterface callback;

    public static PaymentPlanDashboardFragment newInstance(PaymentsModel paymentsModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        PaymentPlanDashboardFragment fragment = new PaymentPlanDashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentPlanDashboardInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PracticePaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_plan_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view, String.format(Label
                        .getLabel("payment.paymentPlanDashboard.title.label.screenTitle"),
                String.format("%s %s",
                        StringUtil.capitalize(paymentsModel.getPaymentPayload().getPatientBalances()
                                .get(0).getDemographics().getPayload().getPersonalDetails().getFirstName()),
                        StringUtil.capitalize(paymentsModel.getPaymentPayload().getPatientBalances()
                                .get(0).getDemographics().getPayload().getPersonalDetails().getLastName()))));
        setCurrentPaymentPlans(view);
        setCompletedPaymentPlans(view);
        setUpButtons(view);
    }

    private void setupToolbar(View view, String titleString) {
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
                callback.onDismissPaymentPlan(paymentsModel);
            }
        });

    }

    protected void setCurrentPaymentPlans(View view) {
        RecyclerView currentPaymentPlansRecycler = (RecyclerView) view.findViewById(R.id.currentPaymentPlansRecycler);
        currentPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        false), false);
        adapter.setCallback(this);
        currentPaymentPlansRecycler.setAdapter(adapter);
    }

    private void setCompletedPaymentPlans(View view) {
        RecyclerView completedPaymentPlansRecycler = (RecyclerView) view.findViewById(R.id.completedPaymentPlansRecycler);
        completedPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        true), true);
        adapter.setCallback(this);
        completedPaymentPlansRecycler.setAdapter(adapter);
    }

    private List<PaymentPlanDTO> getPaymentPlansFiltered(List<PaymentPlanDTO> patientPaymentPlans,
                                                         boolean completed) {
        List<PaymentPlanDTO> filteredPayments = new ArrayList<>();
        String type = PAYMENT_PLAN_UNCOMPLETED;
        if (completed) {
            type = PAYMENT_PLAN_COMPLETED;
        }
        for (PaymentPlanDTO paymentPlan : patientPaymentPlans) {
            if (paymentPlan.getPayload().getPaymentPlanDetails().getPaymentPlanStatus().equals(type)) {
                filteredPayments.add(paymentPlan);
            }
        }
        return filteredPayments;
    }

    private void setUpButtons(View view) {
        Button createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        createPaymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPaymentPlanAction(paymentsModel);
            }
        });
    }

    @Override
    public void onAddBalanceClicked(PaymentPlanDTO paymentPlan) {
        callback.onAddBalanceToExistingPlan(paymentsModel, paymentPlan);
    }

    @Override
    public void onDetailClicked(PaymentPlanDTO paymentPlan) {
        callback.showPaymentPlanDetail(paymentsModel, paymentPlan);
    }
}
