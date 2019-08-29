package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.adapter.PaymentPlanListAdapter;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by lmenendez on 2/12/18
 */

public class ValidPlansFragment extends BaseDialogFragment implements PaymentPlanListAdapter
        .OnPaymentPlanSelectedListener {
    protected static final String KEY_PLAN_AMOUNT = "plan_amount";

    protected PaymentPlanCreateInterface callback;
    protected PaymentsModel paymentsModel;
    protected PendingBalanceDTO selectedBalance;
    protected double paymentPlanAmount;


    public static ValidPlansFragment newInstance(PaymentsModel paymentsModel,
                                                 PendingBalanceDTO selectedBalance,
                                                 double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, selectedBalance);
        args.putDouble(KEY_PLAN_AMOUNT, amount);

        ValidPlansFragment fragment = new ValidPlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else {
                callback = (PaymentPlanCreateInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_active_plans, container, false);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        paymentPlanAmount = args.getDouble(KEY_PLAN_AMOUNT);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupToolBar(view);
        setAdapter(view);
        TextView amount = view.findViewById(R.id.payment_plan_amount);
        if (amount != null) {
            amount.setText(NumberFormat.getCurrencyInstance().format(paymentPlanAmount));
        }
    }

    protected void setupToolBar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackNavigationClick();
            }
        });
        toolbar.setTitle("");
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("payment_plan_active_plan"));

    }

    protected void onBackNavigationClick() {
        cancel();
    }

    protected void setAdapter(View view) {
        RecyclerView plansRecycler = view.findViewById(R.id.plans_recycler);
        plansRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentPlanListAdapter adapter = new PaymentPlanListAdapter(
                getActivity(), getPaymentPlansList(), this, paymentsModel);
        plansRecycler.setAdapter(adapter);

    }

    protected List<PaymentPlanDTO> getPaymentPlansList() {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        return paymentsModel.getPaymentPayload().getValidPlans(practiceId, paymentPlanAmount);
    }


    @Override
    public void onPaymentPlanItemSelected(PaymentPlanDTO paymentPlan) {
        AddExistingPaymentPlanFragment fragment = AddExistingPaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance, paymentPlan, paymentPlanAmount);
        callback.replaceFragment(fragment, true);
    }
}
