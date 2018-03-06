package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanChooseCreditCardFragment extends PracticeChooseCreditCardFragment {

    private PaymentPlanInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanPostModel       the post model for the plan
     * @return an instance of PracticePaymentPlanChooseCreditCardFragment
     */
    public static PracticePaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                  String selectedPaymentMethodLabel,
                                                                  PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);


        PracticePaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PracticePaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanDTO             payment plan details
     * @return an instance of PracticePaymentPlanChooseCreditCardFragment
     */
    public static PracticePaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                  String selectedPaymentMethodLabel,
                                                                  PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);


        PracticePaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PracticePaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentPlanInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("attached context must implement ChooseCreditCardInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        nextButton.setOnClickListener(nextButtonListener);
        if(paymentPlanPostModel != null) {
            nextButton.setText(Label.getLabel("payment_plan_continue"));
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                callback.onStartPaymentPlan(paymentsModel, paymentPlanPostModel);
            }
        });
    }

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(paymentPlanPostModel != null) {
                callback.onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel);
            }
            if(paymentPlanDTO != null) {
                callback.onAddPaymentPlanCard(paymentsModel, paymentPlanDTO);
            }

            dismiss();
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedCreditCard > -1) {
                PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();

                if (papiPaymentMethod == null) {
                    papiPaymentMethod = new PapiPaymentMethod();
                    papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                    papiPaymentMethod.setCardData(getCreditCardModel());
                }

                if(paymentPlanPostModel != null) {
                    paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
                    paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                    callback.onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
                    dismiss();
                }

                if(paymentPlanDTO != null){
                    IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                    postModel.setPapiPaymentMethod(papiPaymentMethod);
                    postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                    makePlanPayment();
                }
            }
        }
    };

    private void makePlanPayment(){
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queries.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queries.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        String paymentModelJson = gson.toJson(paymentsModel.getPaymentPayload().getPaymentPostModel());
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePlanPayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);
    }


}
