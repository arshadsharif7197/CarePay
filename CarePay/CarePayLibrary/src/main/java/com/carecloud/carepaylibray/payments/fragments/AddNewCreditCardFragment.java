package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.CreditCardModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentType;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 3/1/17.
 */
public class AddNewCreditCardFragment extends BaseAddCreditCardFragment implements BaseAddCreditCardFragment.IAuthoriseCreditCardResponse {

    @Override
    protected void attachCallback(Context context) {
        try {
            if(context instanceof PaymentViewHandler){
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            }else {
                callback = (PaymentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(callback == null){
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
            addressPayloadDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getAddress();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setAuthorizeCallback(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        title.setText(Label.getLabel("payment_new_credit_card"));
        nextButton.setText(Label.getLabel("payment_details_pay_now"));
    }


    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            Log.d("addNewCreditCard", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            makePaymentCall();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showErrorToast(getContext(), CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            Log.d("makePaymentCallback", "=========================>\nworkflowDTO=" + workflowDTO.toString());
            callback.showPaymentConfirmation(workflowDTO);
            if(getDialog()!=null){
                dismiss();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            SystemUtil.showErrorToast(getContext(), CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void addNewCreditCardCall() {
        Gson gson = new Gson();
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();
        String body = gson.toJson(creditCardsPayloadDTO);
        getWorkflowServiceHelper().execute(transitionDTO, addNewCreditCardCallback, body, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }


    private void makePaymentCall() {
        if (amountToMakePayment == 0) {
            return;//TODO something else needs to happen here...
        }

        PaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel != null && postModel.getAmount() > 0) {
            processPayment(postModel);
        } else {
            processPayment();
        }
    }

    private void processPayment() {

        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setType(PaymentType.credit_card);
        paymentObject.setExecution(PaymentExecution.papi);
        paymentObject.setAmount(amountToMakePayment);
        paymentObject.setCreditCard(getCreditCardModel());

        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.setAmount(amountToMakePayment);
        paymentPostModel.addPaymentMethod(paymentObject);

        Gson gson = new Gson();
        if (paymentPostModel.isPaymentModelValid()) {
            postPayment(gson.toJson(paymentPostModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }

    }

    private void processPayment(PaymentPostModel postModel) {
        CreditCardModel creditCardModel = getCreditCardModel();
        for (PaymentObject paymentObject : postModel.getPaymentObjects()) {
            paymentObject.setType(PaymentType.credit_card);
            paymentObject.setExecution(PaymentExecution.papi);
            paymentObject.setCreditCard(creditCardModel);
        }

        Gson gson = new Gson();
        if (postModel.isPaymentModelValid()) {
            postPayment(gson.toJson(postModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void postPayment(String paymentModelJson) {
        PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
        Map<String, String> queries = new HashMap<>();
        queries.put("patient_id", metadata.getPatientId());


        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

    }

    private CreditCardModel getCreditCardModel() {
        CreditCardModel creditCardModel = new CreditCardModel();
        creditCardModel.setCardType(creditCardsPayloadDTO.getCardType());
        creditCardModel.setCardNumber(creditCardsPayloadDTO.getCardNumber());
        creditCardModel.setExpiryDate(creditCardsPayloadDTO.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardsPayloadDTO.getNameOnCard());
        creditCardModel.setToken(creditCardsPayloadDTO.getToken());
        creditCardModel.setCvv(creditCardsPayloadDTO.getCvv());
        creditCardModel.setSaveCard(saveCardOnFileCheckBox.isChecked());
        creditCardModel.setDefault(setAsDefaultCheckBox.isChecked());
        creditCardModel.setTokenizationService(creditCardsPayloadDTO.getTokenizationService());

        creditCardModel.setBillingInformation(billingInformationDTO);
        return creditCardModel;
    }


    @Override
    public void onAuthorizeCreditCardSuccess() {
        if (saveCardOnFileCheckBox.isChecked()) {
            addNewCreditCardCall();
        } else {
            makePaymentCall();
        }
    }

    @Override
    public void onAuthorizeCreditCardFailed() {
        new LargeAlertDialog(getActivity(), Label.getLabel("payment_failed_error"), Label.getLabel("payment_change_payment_label"), R.color.Feldgrau, R.drawable.icn_card_error, getLargeAlertInterface()).show();
    }

    protected LargeAlertDialog.LargeAlertInterface getLargeAlertInterface(){
        return new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
                dismiss();
            }
        };
    }

}
