package com.carecloud.carepaylibray.payments.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.adapter.CreditCardsAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.CreditCardModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethodType;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentType;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/28/17.
 */

public class ChooseCreditCardFragment extends BaseDialogFragment {

    private Button nextButton;
    private Activity activity;
    private ListView creditCardsListView;

    private int selectedCreditCard = -1;
    protected PaymentsModel paymentsModel;
    protected double amountToMakePayment;

    protected String titleLabel;
    private PaymentNavigationCallback callback;

    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = new ArrayList<>();

    /**
     *
     * @param paymentsDTO the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param amount the amount
     * @return an instance of PracticeChooseCreditCardFragment
     */
    public static ChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO, String selectedPaymentMethodLabel, double amount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        ChooseCreditCardFragment chooseCreditCardFragment = new ChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("attached context must implement PaymentNavigationCallback");
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
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_credit_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        activity = getActivity();
        setupTitleViews(view);
        initializeViews(view);
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(titleLabel);
            SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(activity, R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.onBackPressed();
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

        creditCardsListView = (ListView) view.findViewById(R.id.list_credit_cards);
        final CreditCardsAdapter creditCardsAdapter = new CreditCardsAdapter(getContext(), creditCardList);
        creditCardsListView.setAdapter(creditCardsAdapter);
        creditCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCreditCard = position;
                creditCardsAdapter.setSelectedItem(position);
                creditCardsAdapter.notifyDataSetChanged();
                nextButton.setEnabled(true);
            }
        });

//        if (getDialog() != null) {
//            //limit width of listview
//            ViewGroup.LayoutParams layoutParams = creditCardsListView.getLayoutParams();
//            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * .5);
//            creditCardsListView.setLayoutParams(layoutParams);
//        }

    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedCreditCard > -1) {
                PaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                if (postModel != null && postModel.getAmount() > 0) {
                    processPayment(postModel);
                } else {
                    processPayment();
                }
            }
        }
    };

    private void processPayment() {
        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setType(PaymentType.credit_card);
        paymentObject.setExecution(PaymentExecution.papi);
        paymentObject.setAmount(amountToMakePayment);

        PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();
        if (papiPaymentMethod != null) {
            paymentObject.setPapiPaymentMethod(papiPaymentMethod);
        } else {
            paymentObject.setCreditCard(getCreditCardModel());
        }

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
        PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();
        CreditCardModel creditCardModel = getCreditCardModel();

        for (PaymentObject paymentObject : postModel.getPaymentObjects()) {
            paymentObject.setType(PaymentType.credit_card);
            paymentObject.setExecution(PaymentExecution.papi);

            if (papiPaymentMethod != null) {
                paymentObject.setPapiPaymentMethod(papiPaymentMethod);
            } else {
                paymentObject.setCreditCard(creditCardModel);
            }
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
        queries.put("practice_mgmt", metadata.getPracticeMgmt());
        queries.put("practice_id", metadata.getPracticeId());
        queries.put("patient_id", metadata.getPatientId());


        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

    }

    private CreditCardModel getCreditCardModel() {
        PaymentCreditCardsPayloadDTO creditCardPayload = creditCardList.get(selectedCreditCard).getPayload();

        CreditCardModel creditCardModel = new CreditCardModel();
        creditCardModel.setCardType(creditCardPayload.getCardType());
        creditCardModel.setCardNumber(creditCardPayload.getCardNumber());
        creditCardModel.setExpiryDate(creditCardPayload.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(creditCardPayload.getNameOnCard());
        creditCardModel.setToken(creditCardPayload.getToken());
        creditCardModel.setCvv(creditCardPayload.getCvv());

        PaymentsCreditCardBillingInformationDTO billingInformation = new PaymentsCreditCardBillingInformationDTO();
        billingInformation.setSameAsPatient(true);
        creditCardModel.setBillingInformation(billingInformation);

        return creditCardModel;
    }

    private PapiPaymentMethod getPapiPaymentMethod() {
        PaymentCreditCardsPayloadDTO creditCardPayload = creditCardList.get(selectedCreditCard).getPayload();
        if (creditCardPayload.getCreditCardsId() == null) {
            return null;
        }

        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPapiPaymentMethodType(PapiPaymentMethodType.card);
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
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            System.out.print(exceptionMessage);
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


}