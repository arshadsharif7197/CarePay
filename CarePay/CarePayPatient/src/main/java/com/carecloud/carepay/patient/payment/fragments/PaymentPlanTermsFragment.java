package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.fragments.BasePaymentDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 1/24/18
 */

public class PaymentPlanTermsFragment extends BasePaymentDialogFragment {

    private PaymentsModel paymentsModel;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanInterface callback;


    public static PaymentPlanTermsFragment newInstance(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PaymentPlanTermsFragment fragment = new PaymentPlanTermsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void attachCallback(Context context) {
        try{
            if(context instanceof PaymentViewHandler){
                callback = (PaymentPlanInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                callback = (PaymentPlanInterface) context;
            }
        }catch(ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_plan_terms, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View createButton = view.findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPaymentPlan();
            }
        });

        TextView termsText = (TextView) view.findViewById(R.id.paymentPlanTermsText);
        termsText.setText(Label.getLabel("payment_agree_to_pay_terms"));
    }

    private void submitPaymentPlan(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanPostModel.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanPostModel.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanPostModel.getMetadata().getPatientId());

        Gson gson = new Gson();
        String payload = gson.toJson(paymentPlanPostModel);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getCreatePaymentPlan();
        getWorkflowServiceHelper().execute(transitionDTO, createPlanCallback, payload, queryMap);
    }

    private WorkflowServiceCallback createPlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.onSubmitPaymentPlan();//todo
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };



}
