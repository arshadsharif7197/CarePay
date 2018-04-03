package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentHistoryCallback;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.List;

/**
 * Created by lmenendez on 2/15/18
 */

public class PracticePaymentPlanListFragment extends ValidPlansFragment {
    private static final String KEY_PRACTICE_ID = "practice_id";

    private String practiceId;
    private PracticePaymentHistoryCallback callback;

    private DialogInterface.OnDismissListener dismissListener;


    public static PracticePaymentPlanListFragment newInstance(PaymentsModel paymentsModel, String practiceId){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putString(KEY_PRACTICE_ID, practiceId);

        PracticePaymentPlanListFragment fragment = new PracticePaymentPlanListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PracticePaymentHistoryCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PracticePaymentHistoryCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args != null){
            practiceId = args.getString(KEY_PRACTICE_ID);
        }
    }

    @Override
    protected void setupToolBar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_title);
        title.setText(Label.getLabel("payment_plan_active_plan"));

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dismissListener != null){
                    dismissListener.onDismiss(getDialog());
                }
                dismiss();
            }
        });

    }

    @Override
    public void onPaymentPlanItemSelected(PaymentPlanDTO paymentPlan) {
        callback.onPaymentPlanSelected(paymentsModel, paymentPlan);
        dismiss();
    }

    @Override
    protected List<PaymentPlanDTO> getPaymentPlansList(){
        return paymentsModel.getPaymentPayload().getFilteredPlans(practiceId);
    }

    /**
     * Set dismiss listener
     * @param dismissListener dismiss listener
     */
    public void setDismissListener(DialogInterface.OnDismissListener dismissListener){
        this.dismissListener = dismissListener;
    }

}
