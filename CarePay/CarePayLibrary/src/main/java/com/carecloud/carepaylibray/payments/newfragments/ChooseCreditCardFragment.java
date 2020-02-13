package com.carecloud.carepaylibray.payments.newfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.PaymentsViewModel;
import com.carecloud.carepaylibray.payments.adapter.CreditCardsListAdapter;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 2/28/17
 */

public class ChooseCreditCardFragment extends BaseDialogFragment implements CreditCardsListAdapter.CreditCardSelectionListener {

    protected Button nextButton;

    protected PaymentCreditCardsPayloadDTO selectedCreditCard;
    protected PaymentsModel paymentsModel;
    protected double amountToMakePayment;

    protected String titleLabel;
    protected FragmentActivityInterface callback;

    private List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = new ArrayList<>();
    protected boolean onlySelectMode;
    private PaymentsViewModel viewModel;
    private String practiceId;


    /**
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param amount                     the amount
     * @return an instance of PracticeChooseCreditCardFragment
     */
    public static ChooseCreditCardFragment newInstance(String practiceId,
                                                       String selectedPaymentMethodLabel,
                                                       double amount) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PRACTICE_ID, practiceId);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        ChooseCreditCardFragment chooseCreditCardFragment = new ChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        viewModel = new ViewModelProvider(getActivity()).get(PaymentsViewModel.class);
        paymentsModel = viewModel.getPaymentsModel();
        setUpViewModel();

        Bundle arguments = getArguments();
        if (arguments != null) {
            titleLabel = arguments.getString(CarePayConstants.PAYMENT_METHOD_BUNDLE);
            amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            onlySelectMode = arguments.getBoolean(CarePayConstants.ONLY_SELECT_MODE);
            practiceId = arguments.getString(CarePayConstants.PRACTICE_ID);

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
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    private void setUpViewModel() {
        viewModel.getMakePaymentObservable().observe(this, paymentsModel1 -> {
            nextButton.setEnabled(true);
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                logEventPaymentFailedMixPanel();
            } else {
                logEventPaymentCompleteMixPanel();
            }

            if (getDialog() != null) {
                dismiss();
            }
            showConfirmation(paymentsModel1);
        });

        viewModel.getMakePaymentErrorObservable().observe(this, aVoid -> {
            nextButton.setEnabled(true);
            logEventPaymentFailedMixPanel();
        });
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(titleLabel);
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(view12 -> getActivity().onBackPressed());
            } else {
                View close = view.findViewById(R.id.closeViewLayout);
                if (close != null) {
                    close.setOnClickListener(view1 -> cancel());
                }
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    private void initializeViews(View view) {
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setEnabled(false);
        if (onlySelectMode) {
            nextButton.setText(Label.getLabel("common.button.continue"));
        }

        Button addNewCardButton = view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(v -> showAddCard(amountToMakePayment, paymentsModel));

        RecyclerView creditCardsRecyclerView = view.findViewById(R.id.list_credit_cards);
        creditCardsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CreditCardsListAdapter creditCardsListAdapter = new CreditCardsListAdapter(getContext(),
                creditCardList, this, true);
        creditCardsRecyclerView.setAdapter(creditCardsListAdapter);
        for (PaymentsPatientsCreditCardsPayloadListDTO creditCard : creditCardList) {
            if (creditCard.getPayload().isDefault()) {
                onCreditCardItemSelected(creditCard.getPayload());
                break;
            }
        }

    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedCreditCard != null) {
                if (onlySelectMode) {
                    //TODO: check this when working on select mode
//                    callback.onCreditCardSelected(selectedCreditCard);
                } else {
                    nextButton.setEnabled(false);
                    IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                    if (postModel != null && postModel.getAmount() > 0) {
                        processPayment(postModel);
                    } else {
                        processPayment();
                    }
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
        if (viewModel.getAppointment().getPayload().getId() != null
                && postModelMetadata.getAppointmentRequestDTO() == null) {
            postModelMetadata.setAppointmentId(viewModel.getAppointment().getPayload().getId());
        }

        viewModel.postPaymentFromChooseMethod(postModel, practiceId);

        logPaymentStartedEventMixPanel();
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
        postModelMetadata.setAppointmentId(viewModel.getAppointment().getPayload().getId());

        viewModel.postPaymentFromChooseMethod(postModel, practiceId);

        logPaymentStartedEventMixPanel();
    }

    protected PapiPaymentMethod getPapiPaymentMethod() {
        if (selectedCreditCard == null) {
            return null;
        }

        PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_CARD);
        papiPaymentMethod.setPapiPaymentID(selectedCreditCard.getCreditCardsId());

        return papiPaymentMethod;
    }

    protected IntegratedPaymentCardData getCreditCardModel() {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(selectedCreditCard.getCardType());
        creditCardModel.setCardNumber(selectedCreditCard.getCardNumber());
        creditCardModel.setExpiryDate(selectedCreditCard.getExpireDt().replaceAll("/", ""));
        creditCardModel.setNameOnCard(selectedCreditCard.getNameOnCard());
        creditCardModel.setToken(selectedCreditCard.getToken());

        return creditCardModel;
    }

    protected void showAddCard(double amountToMakePayment, PaymentsModel paymentsModel) {
//        callback.showAddCard(amountToMakePayment, paymentsModel);
        Fragment fragment = AddNewCreditCardFragment.newInstance(practiceId, amountToMakePayment);
        callback.addFragment(fragment, true);
    }

    @Override
    public void onCreditCardItemSelected(PaymentCreditCardsPayloadDTO creditCard) {
        selectedCreditCard = creditCard;
        Date expDate = DateUtil.getInstance().setDateRaw(creditCard.getExpireDt()).getDate();
        expDate = DateUtil.getLastDayOfMonth(expDate);
        expDate = DateUtil.getLastHourOfDay(expDate);
        nextButton.setEnabled(!expDate.before(new Date()));
    }

    protected void showConfirmation(PaymentsModel paymentsModel) {
//        callback.showPaymentConfirmation(workflowDTO);

//        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty()
                && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            viewModel.getErrorMessage().postValue(builder.toString());
        } else {
            String paymentType = Label.getLabel("appointment.confirmationScreen.type.label.paymentType");
            if (!StringUtil.isNullOrEmpty(paymentsModel.getPaymentPayload().getPatientPayments()
                    .getPayload().getMetadata().getCancellationReasonId())) {
                paymentType = Label.getLabel("appointment.confirmationScreen.paymentType.label.cancellationType");
            }
            viewModel.setPaymentsModel(paymentsModel);
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                    .newInstance(paymentType, Label.getLabel("add_appointment_back_to_appointments_button"));
            callback.displayDialogFragment(confirmationFragment, false);

            if (paymentType.equals(Label.getLabel("appointment.confirmationScreen.type.label.paymentType"))) {
                //this is a prepayment
                MixPanelUtil.incrementPeopleProperty(getString(R.string.count_prepayments_completed), 1);
            }
        }
    }

    private void logPaymentStartedEventMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_started), params, values);
    }

    private void logEventPaymentCompleteMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_complete), params, values);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_completed), 1);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.total_payments_amount), amountToMakePayment);
    }

    private void logEventPaymentFailedMixPanel() {
        String[] params = {getString(R.string.param_payment_amount), getString(R.string.param_payment_type)};
        Object[] values = {amountToMakePayment, getString(R.string.payment_card_on_file)};
        MixPanelUtil.logEvent(getString(R.string.event_payment_failed), params, values);
    }

}