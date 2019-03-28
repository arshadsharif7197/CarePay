package com.carecloud.carepay.practice.library.payments.dialogs;

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
import com.carecloud.carepay.practice.library.payments.adapter.ExistingChargesItemAdapter;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModePaymentPlanFragment;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author pjohnson on 24/07/18.
 */
public class PracticeModePaymentPlanEditFragment extends PracticeModePaymentPlanFragment {

    private Button editPaymentPlanButton;
    private PaymentPlanDTO paymentPlanDTO;
    private PaymentPlanEditInterface callback;

    public static PracticeModePaymentPlanEditFragment newInstance(PaymentsModel paymentsModel,
                                                                  PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PracticeModePaymentPlanEditFragment fragment = new PracticeModePaymentPlanEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof PaymentPlanEditInterface) {
                callback = (PaymentPlanEditInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, getArguments());
        practiceId = paymentPlanDTO.getMetadata().getPracticeId();
        paymentPlanAmount = SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(),
                paymentPlanDTO.getPayload().getAmountPaid());
        super.onCreate(icicle);
        selectedBalance = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            frequencyOption = frequencyOptions.get(0);
        } else {
            frequencyOption = frequencyOptions.get(1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_mode_edit_payment_plan, container, false);
    }

    @Override
    protected void setupToolbar(View view, String titleString) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(Label.getLabel("payment_plan_edit_heading"));
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    protected void setupHeader(View view) {
    }

    @Override
    protected void setUpAmounts(View view) {
        TextView paymentPlanNameTextView = (TextView) view.findViewById(R.id.paymentPlanNameTextView);
        paymentPlanNameTextView.setText(paymentPlanDTO.getPayload().getDescription());

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        TextView paymentPlanValueTextView = (TextView) view.findViewById(R.id.paymentPlanValueTextView);
        paymentPlanValueTextView.setText(currencyFormatter.format(paymentPlanAmount));

        TextView paymentAmountTextView = (TextView) view.findViewById(R.id.paymentAmountTextView);
        String paymentAmount = currencyFormatter.format(paymentPlanDTO.getPayload()
                .getPaymentPlanDetails().getAmount()) +
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyString();
        paymentAmountTextView.setText(paymentAmount);
    }

    @Override
    protected void setupFields(View view) {
        super.setupFields(view);
        planNameEditText.setText(paymentPlanDTO.getPayload().getDescription());
        planNameEditText.getOnFocusChangeListener().onFocusChange(planNameEditText, true);
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            paymentDateEditText.setText(StringUtil.getOrdinal(getApplicationPreferences().getUserLanguage(),
                    paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth()));
        } else {
            paymentDateEditText.setText(StringUtil
                    .getDayOfTheWeek(paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfWeek()));
        }

        installments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() -
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        installmentsEditText.setText(String.valueOf(installments));
        installmentsEditText.getOnFocusChangeListener().onFocusChange(installmentsEditText, true);

        amounthPayment = paymentPlanDTO.getPayload().getPaymentPlanDetails().getAmount();
        amountPaymentEditText.setText(currencyFormatter.format(amounthPayment));

    }

    @Override
    protected void setupButtons(final View view) {
        editPaymentPlanButton = (Button) view.findViewById(R.id.editPaymentPlanButton);
        editPaymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields(true)) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                    if (selectedCreditCard != null && selectedCreditCard.getCreditCardsId() == null) {
                        authorizeCreditCard();
                    } else {
                        mainButtonAction();
                    }
                }
            }
        });
        Button cancelPaymentPlanButton = (Button) view.findViewById(R.id.cancelPaymentPlanButton);
        boolean deletePaymentPlan = false;
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanHistoryList().isEmpty()) {
            deletePaymentPlan = true;
            cancelPaymentPlanButton.setText(Label.getLabel("payment.editPaymentPlan.delete.button.label"));
        }
        final boolean finalDeletePaymentPlan = deletePaymentPlan;
        cancelPaymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PaymentPlanEditInterface) callback).showCancelPaymentPlanConfirmDialog(new ConfirmationCallback() {
                    @Override
                    public void onConfirm() {
                        cancelPaymentPlan(finalDeletePaymentPlan);
                    }
                }, finalDeletePaymentPlan);
            }
        });

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddPaymentPlanCard(paymentsModel, null, true);
            }
        });
    }

    @Override
    protected void setUpItems(View view, List<BalanceItemDTO> items) {
        RecyclerView itemsRecycler = (RecyclerView) view.findViewById(R.id.itemRecycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ExistingChargesItemAdapter adapter = new ExistingChargesItemAdapter(paymentPlanDTO.getPayload().getLineItems());
        itemsRecycler.setAdapter(adapter);
    }

    @Override
    protected Button getActionButton() {
        return editPaymentPlanButton;
    }

    private void cancelPaymentPlan(final boolean isDeleted) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getDeletePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payment_plans_cancelled), 1);
                dismiss();
                callback.onPaymentPlanCanceled(workflowDTO, isDeleted);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                SystemUtil.showErrorToast(getContext(), exceptionMessage);
            }
        }, queryMap);
    }


    protected void updatePaymentPlan() {
        PaymentPlanPostModel postModel = new PaymentPlanPostModel();
        postModel.setAmount(paymentPlanDTO.getPayload().getAmount());
        postModel.setExecution(paymentPlanDTO.getPayload().getExecution());
        postModel.setLineItems(paymentPlanDTO.getPayload().getLineItems());
        postModel.setDescription(planNameEditText.getText().toString());

        if (selectedCreditCard != null) {
            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            if (selectedCreditCard.getCreditCardsId() == null) {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                papiPaymentMethod.setCardData(getCreditCardModel(selectedCreditCard));
            } else {
                papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
                papiPaymentMethod.setPapiPaymentID(selectedCreditCard.getCreditCardsId());
            }
            postModel.setPapiPaymentMethod(papiPaymentMethod);
        } else {
            postModel.setPapiPaymentMethod(paymentPlanDTO.getPayload().getPaymentMethod());
        }

        PaymentPlanModel paymentPlanModel = new PaymentPlanModel();
        paymentPlanModel.setAmount(amounthPayment);
        paymentPlanModel.setFrequencyCode(frequencyOption.getName());
        paymentPlanModel.setInstallments((int) SystemUtil.safeAdd(installments,
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size()));
        paymentPlanModel.setEnabled(true);

        if (frequencyOption.getName().equals(PaymentPlanModel.FREQUENCY_MONTHLY)) {
            try {
                paymentPlanModel.setDayOfMonth(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        } else {
            try {
                paymentPlanModel.setDayOfWeek(Integer.parseInt(paymentDateOption.getName()));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        postModel.setPaymentPlanModel(paymentPlanModel);

        callEditPaymentPlanService(postModel);
    }

    private void callEditPaymentPlanService(PaymentPlanPostModel postModel) {
        TransitionDTO updatePaymentTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getUpdatePaymentPlan();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queryMap.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());


        getWorkflowServiceHelper().execute(updatePaymentTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                dismiss();
                callback.onPaymentPlanEdited(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                SystemUtil.showErrorToast(getContext(), exceptionMessage);

            }
        }, new Gson().toJson(postModel), queryMap);
    }

    @Override
    protected void mainButtonAction() {
        updatePaymentPlan();
    }

    @Override
    protected void selectDefaultCreditCard(List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList) {
        if (paymentPlanDTO.getPayload().getPaymentMethod().getPaymentMethodType()
                .equals(PapiPaymentMethod.PAYMENT_METHOD_CARD)) {
            for (PaymentsPatientsCreditCardsPayloadListDTO creditCard : creditCardList) {
                if (creditCard.getPayload().getCreditCardsId()
                        .equals(paymentPlanDTO.getPayload().getPaymentMethod().getPapiPaymentID())) {
                    selectedCreditCard = creditCard.getPayload();
                    return;
                }
            }
        } else {
            super.selectDefaultCreditCard(creditCardList);
        }
    }
}
