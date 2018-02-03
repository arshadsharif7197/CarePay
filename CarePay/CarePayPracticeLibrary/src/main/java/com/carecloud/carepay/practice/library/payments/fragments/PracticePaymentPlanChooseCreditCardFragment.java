package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanChooseCreditCardFragment extends PracticeChooseCreditCardFragment {

    private PaymentPlanInterface callback;
    protected PaymentPlanPostModel paymentPlanPostModel;

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanPostModel       the post model for the plan
     * @return an instance of PracticeChooseCreditCardFragment
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
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setText(Label.getLabel("payment_plan_continue"));

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
            callback.onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel);
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

                paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
                paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                callback.onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
                dismiss();
            }
        }
    };


}
