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
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

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
    private boolean hasBalanceForPaymentPlan;

    private RecyclerView currentPaymentPlansRecycler;
    private RecyclerView completedPaymentPlansRecycler;

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
        hasBalanceForPaymentPlan = hasBalanceForPaymentPlan();
        setCurrentPaymentPlans(view);
        setCompletedPaymentPlans(view);
        setUpButtons(view);

        View emptyPlansLayout = view.findViewById(R.id.empty_plans_layout);
        if(completedPaymentPlansRecycler.getAdapter().getItemCount() == 0 &&
                currentPaymentPlansRecycler.getAdapter().getItemCount() == 0) {
            emptyPlansLayout.setVisibility(View.VISIBLE);
        }else{
            emptyPlansLayout.setVisibility(View.GONE);
        }
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
        currentPaymentPlansRecycler = (RecyclerView) view.findViewById(R.id.currentPaymentPlansRecycler);
        currentPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        false), paymentsModel, false, hasBalanceForPaymentPlan);
        adapter.setCallback(this);
        currentPaymentPlansRecycler.setAdapter(adapter);
    }

    private void setCompletedPaymentPlans(View view) {
        completedPaymentPlansRecycler = (RecyclerView) view.findViewById(R.id.completedPaymentPlansRecycler);
        completedPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        true), paymentsModel, true, false);
        adapter.setCallback(this);
        completedPaymentPlansRecycler.setAdapter(adapter);

        View completedLabel = view.findViewById(R.id.completedLabel);
        completedLabel.setVisibility(completedPaymentPlansRecycler.getAdapter().getItemCount() == 0 ?
                View.GONE : View.VISIBLE);
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
        Button createNewPlanButton = (Button) view.findViewById(R.id.createNewPlanButton);

        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPaymentPlanAction(paymentsModel);
                dismiss();
            }
        };

        createPaymentPlanButton.setOnClickListener(createListener);
        createPaymentPlanButton.setEnabled(userHasPermissionsToCreatePaymentPlan() && hasBalanceForPaymentPlan);
        createNewPlanButton.setOnClickListener(createListener);
        createNewPlanButton.setEnabled(userHasPermissionsToCreatePaymentPlan() && hasBalanceForPaymentPlan);

    }

    private boolean hasBalanceForPaymentPlan() {
        if (hasBalance()) {
            PendingBalancePayloadDTO balance = paymentsModel.getPaymentPayload().getPatientBalances()
                    .get(0).getBalances().get(0).getPayload().get(0);
            double remainingUnapplied = balance.getUnappliedCredit() * -1;
            double total = SystemUtil.safeAdd(remainingUnapplied, balance.getAmount());
            return SystemUtil.safeSubtract(total, balance.getPaymentPlansAmount()) > 0.0;
        }
        return false;
    }

    private boolean hasBalance() {
        return paymentsModel.getPaymentPayload().getPatientBalances().size() > 0
                && paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().size() > 0
                && paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload().size() > 0;
    }

    private boolean userHasPermissionsToCreatePaymentPlan() {
        return paymentsModel.getPaymentPayload()
                .getUserAuthModel().getUserAuthPermissions().canCreatePaymentPlan;
    }

    @Override
    public void onAddBalanceClicked(PaymentPlanDTO paymentPlan) {
        callback.onAddBalanceToExistingPlan(paymentsModel, paymentPlan);
        dismiss();
    }

    @Override
    public void onDetailClicked(PaymentPlanDTO paymentPlan, boolean completed) {
        callback.showPaymentPlanDetail(paymentsModel, paymentPlan, completed);
        dismiss();
    }
}
