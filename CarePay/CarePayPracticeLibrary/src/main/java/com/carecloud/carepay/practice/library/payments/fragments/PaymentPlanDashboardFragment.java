package com.carecloud.carepay.practice.library.payments.fragments;

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
import com.carecloud.carepay.practice.library.payments.adapter.PaymentPlanDashboardAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticePaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
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
    private PaymentPlanCreateInterface callback;
    private boolean hasBalanceForPaymentPlan;

    private RecyclerView currentPaymentPlansRecycler;
    private RecyclerView completedPaymentPlansRecycler;
    private RecyclerView canceledPaymentPlansRecycler;

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
            callback = (PaymentPlanCreateInterface) context;
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
        setCanceledPaymentPlans(view);
        setUpButtons(view);

        View emptyPlansLayout = view.findViewById(R.id.empty_plans_layout);
        if (completedPaymentPlansRecycler.getAdapter().getItemCount() == 0
                && currentPaymentPlansRecycler.getAdapter().getItemCount() == 0
                && canceledPaymentPlansRecycler.getAdapter().getItemCount() == 0) {
            emptyPlansLayout.setVisibility(View.VISIBLE);
        } else {
            emptyPlansLayout.setVisibility(View.GONE);
        }
    }

    private void setupToolbar(View view, String titleString) {
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

    protected void setCurrentPaymentPlans(View view) {
        currentPaymentPlansRecycler = view.findViewById(R.id.currentPaymentPlansRecycler);
        currentPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        PaymentPlanDetailsDTO.STATUS_PROCESSING), paymentsModel,
                false, hasBalanceForPaymentPlan);
        adapter.setCallback(this);
        currentPaymentPlansRecycler.setAdapter(adapter);
    }

    private void setCompletedPaymentPlans(View view) {
        completedPaymentPlansRecycler = view.findViewById(R.id.completedPaymentPlansRecycler);
        completedPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        PaymentPlanDetailsDTO.STATUS_COMPLETED), paymentsModel,
                true, false);
        adapter.setCallback(this);
        completedPaymentPlansRecycler.setAdapter(adapter);

        View completedLabel = view.findViewById(R.id.completedLabel);
        completedLabel.setVisibility(completedPaymentPlansRecycler.getAdapter().getItemCount() == 0 ?
                View.GONE : View.VISIBLE);
    }

    private void setCanceledPaymentPlans(View view) {
        canceledPaymentPlansRecycler = view.findViewById(R.id.canceledPaymentPlansRecycler);
        canceledPaymentPlansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanDashboardAdapter adapter = new PaymentPlanDashboardAdapter(
                getPaymentPlansFiltered(paymentsModel.getPaymentPayload().getPatientPaymentPlans(),
                        PaymentPlanDetailsDTO.STATUS_CANCELLED), paymentsModel,
                true, false);
        adapter.setCallback(this);
        canceledPaymentPlansRecycler.setAdapter(adapter);

        View completedLabel = view.findViewById(R.id.canceledLabel);
        completedLabel.setVisibility(canceledPaymentPlansRecycler.getAdapter().getItemCount() == 0 ?
                View.GONE : View.VISIBLE);
    }

    private List<PaymentPlanDTO> getPaymentPlansFiltered(List<PaymentPlanDTO> patientPaymentPlans,
                                                         String type) {
        List<PaymentPlanDTO> filteredPayments = new ArrayList<>();
        for (PaymentPlanDTO paymentPlan : patientPaymentPlans) {
            if (paymentPlan.getPayload().getPaymentPlanDetails().getPaymentPlanStatus().equals(type)) {
                filteredPayments.add(paymentPlan);
            }
        }
        return filteredPayments;
    }

    private void setUpButtons(View view) {
        Button createPaymentPlanButton = view.findViewById(R.id.createPaymentPlanButton);
        Button createNewPlanButton = view.findViewById(R.id.createNewPlanButton);

        View.OnClickListener createListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentPlanCreationScreen();
            }
        };

        createPaymentPlanButton.setOnClickListener(createListener);
        createPaymentPlanButton.setEnabled(userHasPermissionsToCreatePaymentPlan() && hasBalanceForPaymentPlan);
        createNewPlanButton.setOnClickListener(createListener);
        createNewPlanButton.setEnabled(userHasPermissionsToCreatePaymentPlan() && hasBalanceForPaymentPlan);

    }

    private void showPaymentPlanCreationScreen() {
        PendingBalanceDTO selectedBalance = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);
        PracticeModePaymentPlanFragment fragment = PracticeModePaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        callback.displayDialogFragment(fragment, false);
        logMixPanelPaymentPlanStartedEvent(selectedBalance);
        hideDialog();
    }

    private void logMixPanelPaymentPlanStartedEvent(PendingBalanceDTO selectedBalance) {
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount()
                        - selectedBalance.getPayload().get(0).getPaymentPlansAmount(), false};
        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
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
        PracticeModeAddToExistingPaymentPlanFragment fragment = PracticeModeAddToExistingPaymentPlanFragment
                .newInstance(paymentsModel, paymentPlan);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        callback.displayDialogFragment(fragment, true);
        hideDialog();

        logMixPanelAddToBalanceEvent();
    }

    private void logMixPanelAddToBalanceEvent() {
        PendingBalanceDTO selectedBalance = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount() - selectedBalance.getPayload().get(0).getPaymentPlansAmount(),
                true};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    @Override
    public void onDetailClicked(PaymentPlanDTO paymentPlan, boolean completed) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlan, completed);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
