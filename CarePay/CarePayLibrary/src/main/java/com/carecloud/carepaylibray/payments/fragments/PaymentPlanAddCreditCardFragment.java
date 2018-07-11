package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanAddCreditCardFragment extends AddNewCreditCardFragment {
    protected static final String KEY_DATE = "date";

    private PaymentPlanCreateInterface callback;
    protected PaymentPlanPostModel paymentPlanPostModel;
    protected PaymentPlanDTO paymentPlanDTO;
    protected LargeAlertDialog.LargeAlertInterface largeAlertInterface;

    private Date paymentDate;

    /**
     * @param paymentsModel        payment model
     * @param paymentPlanPostModel payment plan post model
     * @param onlySelectMode       select mode
     * @return new instance
     */
    public static PaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel, boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        PaymentPlanAddCreditCardFragment fragment = new PaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  payment model
     * @param paymentPlanDTO payment plan details
     * @param onlySelectMode select mode
     * @return new instance
     */
    public static PaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode) {
        return newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, null);
    }

    /**
     * @param paymentsModel  payment model
     * @param paymentPlanDTO payment plan details
     * @param onlySelectMode select mode
     * @param paymentDate    payment date
     * @return new instance
     */
    public static PaymentPlanAddCreditCardFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode,
                                                               Date paymentDate) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        if(paymentDate != null) {
            DateUtil.getInstance().setDate(paymentDate);
            args.putString(KEY_DATE, DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        PaymentPlanAddCreditCardFragment fragment = new PaymentPlanAddCreditCardFragment();
        fragment.setArguments(args);
        return fragment;
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
            throw new ClassCastException("Attached context must implement PaymentConfirmationInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);

        String dateString = args.getString(KEY_DATE);
        if(dateString != null){
            DateUtil.getInstance().setDateRaw(dateString);
            paymentDate = DateUtil.getInstance().getDate();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        saveCardOnFileCheckBox.setChecked(true);
        saveCardOnFileCheckBox.setEnabled(false);
        if (paymentPlanPostModel != null || onlySelectMode) {
            nextButton.setText(Label.getLabel("payment_plan_continue"));
        }
    }

    @Override
    protected void makePaymentCall() {
        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
        papiPaymentMethod.setCardData(getCreditCardModel());

        if (paymentPlanPostModel != null) {
            paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
            paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

            callback.onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
        }

        if (paymentPlanDTO != null) {
            IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
            postModel.setPapiPaymentMethod(papiPaymentMethod);
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

            if(paymentDate != null){
                DateUtil.getInstance().setDate(paymentDate);
                postModel.setPaymentDate(DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
            }
            makePlanPayment();
        }
    }

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

        if(paymentDate != null){
            getWorkflowServiceHelper().execute(transitionDTO, schedulePaymentCallback, paymentModelJson, queries, header);
        }else {
            getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);
        }
    }

    @Override
    protected void authorizeOrSelectCreditCard() {
        if (onlySelectMode) {
            creditCardsPayloadDTO.setCompleteNumber(creditCardNoEditText.getText().toString().replace(" ", "").trim());
            creditCardsPayloadDTO.setDefault(setAsDefaultCheckBox.isChecked());
            creditCardsPayloadDTO.setSaveCardOnFile(saveCardOnFileCheckBox.isChecked());
            callback.onCreditCardSelected(creditCardsPayloadDTO);
        } else {
            authorizeCreditCard();
        }
    }

    private WorkflowServiceCallback schedulePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            ((OneTimePaymentInterface)callback).showScheduledPaymentConfirmation(workflowDTO);
            if (getDialog() != null) {
                dismiss();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    protected void showConfirmation(WorkflowDTO workflowDTO){
        ((OneTimePaymentInterface)callback).showPaymentConfirmation(workflowDTO, true);
    }

    public void setChangePaymentMethodListener(LargeAlertDialog.LargeAlertInterface largeAlertInterface) {
        this.largeAlertInterface = largeAlertInterface;
    }

    @Override
    protected LargeAlertDialog.LargeAlertInterface getLargeAlertInterface() {
        if (largeAlertInterface != null) {
            getActivity().getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return largeAlertInterface;
        } else {
            return super.getLargeAlertInterface();
        }
    }

}
