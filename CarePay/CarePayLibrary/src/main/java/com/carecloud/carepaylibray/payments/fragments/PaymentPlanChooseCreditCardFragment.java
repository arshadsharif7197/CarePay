package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
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
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanChooseCreditCardFragment extends ChooseCreditCardFragment {

    private PaymentPlanCreateInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;


    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanPostModel       the post model for the plan
     * @param onlySelectMode
     * @return an instance of PaymentPlanChooseCreditCardFragment
     */
    public static PaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                  String selectedPaymentMethodLabel,
                                                                  PaymentPlanPostModel paymentPlanPostModel,
                                                                  boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanDTO             payment plan details
     * @param onlySelectMode
     * @return an instance of PaymentPlanChooseCreditCardFragment
     */
    public static PaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                  String selectedPaymentMethodLabel,
                                                                  PaymentPlanDTO paymentPlanDTO,
                                                                  boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }


    @Override
    protected void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentPlanCreateInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentPlanCreateInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("attached context must implement ChooseCreditCardInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        onlySelectMode = args.getBoolean(CarePayConstants.ONLY_SELECT_MODE);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        nextButton.setOnClickListener(nextButtonListener);
        if (paymentPlanPostModel != null) {
            nextButton.setText(Label.getLabel("payment_plan_continue"));
        }
    }

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (paymentPlanPostModel != null) {
                callback.onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
            }
            if (paymentPlanDTO != null && callback instanceof OneTimePaymentInterface) {
                ((OneTimePaymentInterface) callback).onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode);
            }
            if (getDialog() != null) {
                dismiss();
            }
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedCreditCard > -1) {
                if (onlySelectMode) {
                    callback.onCreditCardSelected(creditCardList.get(selectedCreditCard).getPayload());
                } else {
                    PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();

                    if (papiPaymentMethod == null) {
                        papiPaymentMethod = new PapiPaymentMethod();
                        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                        papiPaymentMethod.setCardData(getCreditCardModel());
                    }

                    if (paymentPlanPostModel != null) {
                        paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
                        paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                        callback.onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
                    }

                    if (paymentPlanDTO != null) {
                        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                        postModel.setPapiPaymentMethod(papiPaymentMethod);
                        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                        makePlanPayment();
                    }
                }
            }
        }
    };

    private void makePlanPayment() {
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
