package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.adapter.CreditCardsListAdapter;
import com.carecloud.carepaylibray.payments.interfaces.ChooseCreditCardInterface;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/28/17
 */

public class ChooseCreditCardFragment extends BasePaymentDialogFragment implements CreditCardsListAdapter.CreditCardSelectionListener {

    private Button nextButton;
    private RecyclerView creditCardsRecyclerView;

    private int selectedCreditCard = -1;
    protected PaymentsModel paymentsModel;
    private UserPracticeDTO userPracticeDTO;
    protected double amountToMakePayment;

    protected String titleLabel;
    protected ChooseCreditCardInterface callback;

    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = new ArrayList<>();


    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param amount                     the amount
     * @return an instance of PracticeChooseCreditCardFragment
     */
    public static ChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                       String selectedPaymentMethodLabel,
                                                       double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        ChooseCreditCardFragment chooseCreditCardFragment = new ChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (ChooseCreditCardInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (ChooseCreditCardInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("attached context must implement ChooseCreditCardInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle arguments = getArguments();
        if (arguments != null) {
            titleLabel = arguments.getString(CarePayConstants.PAYMENT_METHOD_BUNDLE);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
            amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);

            if (paymentsModel != null) {
                creditCardList = paymentsModel.getPaymentPayload().getPatientCreditCards();
                userPracticeDTO = callback.getPracticeInfo(paymentsModel);
            }

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_credit_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(titleLabel);
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });
            } else {
                View close = view.findViewById(R.id.closeViewLayout);
                if (close != null) {
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
                        }
                    });
                }
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private void initializeViews(View view) {
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setEnabled(false);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        creditCardsRecyclerView = (RecyclerView) view.findViewById(R.id.list_credit_cards);
        creditCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        final CreditCardsListAdapter creditCardsListAdapter = new CreditCardsListAdapter(getContext(), creditCardList, this);
        creditCardsRecyclerView.setAdapter(creditCardsListAdapter);

    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedCreditCard > -1) {
                IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                if (postModel != null && postModel.getAmount() > 0) {
                    processPayment(postModel);
                } else {
                    processPayment();
                }
            }
        }
    };

    private void processPayment(IntegratedPaymentPostModel postModel) {
        PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();

        if (papiPaymentMethod == null) {
            papiPaymentMethod = new PapiPaymentMethod();
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel());
        }

        postModel.setPapiPaymentMethod(papiPaymentMethod);
        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

        IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
        if (callback.getAppointmentId() != null && postModelMetadata.getAppointmentRequestDTO() == null) {
            postModelMetadata.setAppointmentId(callback.getAppointmentId());
        }

        Gson gson = new Gson();
        if (postModel.isPaymentModelValid()) {
            postPayment(gson.toJson(postModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void processPayment() {
        PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();

        if (papiPaymentMethod == null) {
            papiPaymentMethod = new PapiPaymentMethod();
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel());
        }

        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amountToMakePayment);
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
        paymentLineItem.setDescription("Unapplied Amount");

        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        postModel.setPapiPaymentMethod(papiPaymentMethod);
        postModel.setAmount(amountToMakePayment);
        postModel.addLineItem(paymentLineItem);

        IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
        postModelMetadata.setAppointmentId(callback.getAppointmentId());

        Gson gson = new Gson();
        if (postModel.isPaymentModelValid()) {
            postPayment(gson.toJson(postModel));
        } else {
            Toast.makeText(getContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void postPayment(String paymentModelJson) {
        Map<String, String> queries = new HashMap<>();
        if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            queries.put("patient_id", userPracticeDTO.getPatientId());
        } else if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
        }
        if (callback.getAppointmentId() != null) {
            queries.put("appointment_id", callback.getAppointmentId());
        }

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_started), params, values);
    }

    private IntegratedPaymentCardData getCreditCardModel() {
        PaymentCreditCardsPayloadDTO creditCardPayload = creditCardList.get(selectedCreditCard).getPayload();

        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(creditCardPayload.getCardType());
        creditCardModel.setCardNumber(creditCardPayload.getCardNumber());
        creditCardModel.setExpiryDate(creditCardPayload.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardPayload.getNameOnCard());
        creditCardModel.setToken(creditCardPayload.getToken());

        return creditCardModel;
    }

    private PapiPaymentMethod getPapiPaymentMethod() {
        PaymentCreditCardsPayloadDTO creditCardPayload = creditCardList.get(selectedCreditCard).getPayload();
        if (creditCardPayload.getCreditCardsId() == null) {
            return null;
        }

        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
        papiPaymentMethod.setPapiPaymentID(creditCardPayload.getCreditCardsId());

        return papiPaymentMethod;
    }


    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.showPaymentConfirmation(workflowDTO);
            if (getDialog() != null) {
                dismiss();
            }

            String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
            Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
            MixPanelUtil.logEvent(getString(R.string.event_payment_complete), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_completed), 1);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.total_payments_amount), amountToMakePayment);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            System.out.print(exceptionMessage);

            String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
            Object[] values = {amountToMakePayment, getString(R.string.payment_new_card)};
            MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
        }
    };

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showAddCard(amountToMakePayment, paymentsModel);
            if (getDialog() != null) {
                dismiss();
            }
        }
    };

    @Override
    public void onCreditCardItemSelected(int position) {
        selectedCreditCard = position;
        CreditCardsListAdapter creditCardsListAdapter = (CreditCardsListAdapter) creditCardsRecyclerView.getAdapter();
        creditCardsListAdapter.setSelectedItem(position);
        nextButton.setEnabled(true);
    }

}