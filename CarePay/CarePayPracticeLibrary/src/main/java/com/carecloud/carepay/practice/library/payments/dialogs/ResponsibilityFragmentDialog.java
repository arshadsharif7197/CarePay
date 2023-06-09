package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentSettingsBalanceRangeRule;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsRegularPaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ResponsibilityFragmentDialog extends BaseDialogFragment
        implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    private PatientBalanceDTO patientBalance;
    private PayResponsibilityCallback callback;
    @Nullable
    private PaymentConfirmationInterface payInfoCallback;
    private double owedAmount = 0;
    private ResponsibilityHeaderModel headerModel;

    private boolean mustAddToExisting = false;

    /**
     * @param paymentsModel the payment model
     * @return new instance of a ResponsibilityFragmentDialog
     */
    public static ResponsibilityFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           ResponsibilityHeaderModel headerModel,
                                                           PatientBalanceDTO selectedBalance) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
//        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, headerModel);
        DtoHelper.bundleDto(args, selectedBalance);

        ResponsibilityFragmentDialog dialog = new ResponsibilityFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PayResponsibilityCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PayResponsibilityCallback");
        }

        try {
            payInfoCallback = (PaymentConfirmationInterface) context;
        } catch (ClassCastException cce) {
            //this callback is optional and expected only in patient mode
            cce.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
//        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        headerModel = DtoHelper.getConvertedDTO(ResponsibilityHeaderModel.class, args);
        patientBalance = DtoHelper.getConvertedDTO(PatientBalanceDTO.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_dialog_payment_responsibility, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            closeView.setOnClickListener(v -> dismiss());
        }
    }


    private void onInitialization(View view) {
        initializeHeader(view);
        initializeBody(view);
        initializeFooter(view);
    }

    private void initializeHeader(View view) {
        TextView titleView = view.findViewById(R.id.patient_full_name);
        titleView.setText(headerModel.getHeaderFullTitle());
        TextView subtitleView = view.findViewById(R.id.patient_provider_name);
        subtitleView.setText(headerModel.getHeaderSubtitle());
        ImageView profilePhoto = view.findViewById(R.id.patient_profile_photo);
        final TextView shortName = view.findViewById(R.id.patient_profile_short_name);
        shortName.setText(headerModel.getHeaderShortTitle());

        String photoUrl = headerModel.getHeaderPhotoUrl();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(getContext()).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(88, 88)
                    .into(profilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            shortName.setText(headerModel.getHeaderShortTitle());
                        }
                    });

            profilePhoto.setVisibility(View.VISIBLE);
        }
    }

    private void initializeBody(View view) {
        if (null != patientBalance && null != patientBalance.getBalances() && !patientBalance.getBalances().isEmpty()) {
            View noBalancePlaceholder = view.findViewById(R.id.empty_balance_layout);
            List<PendingBalanceDTO> balances = patientBalance.getBalances();
            initializeOwedAmount(balances);
            if (owedAmount > 0) {
                initializePaymentLines(view, balances);
                noBalancePlaceholder.setVisibility(View.GONE);
            } else {
                noBalancePlaceholder.setVisibility(View.VISIBLE);
                TextView message = view.findViewById(R.id.no_payment_message);
                message.setText(null);
            }
        }

        initializeOwedAmountTextView(view);
    }

    private void initializePaymentLines(View view, List<PendingBalanceDTO> balances) {
        RecyclerView amountDetails = view.findViewById(R.id.payment_responsibility_balance_details);
        amountDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(getContext(),
                getAllPendingBalancePayloads(balances), this, true);
        amountDetails.setAdapter(adapter);
    }

    protected List<PendingBalancePayloadDTO> getAllPendingBalancePayloads(List<PendingBalanceDTO> pendingBalances) {
        List<PendingBalancePayloadDTO> pendingBalancePayloads = new ArrayList<>();
        for (PendingBalanceDTO pendingBalance : pendingBalances) {
            pendingBalancePayloads.addAll(pendingBalance.getPayload());
        }
        return pendingBalancePayloads;
    }


    private void initializeOwedAmount(List<PendingBalanceDTO> balances) {
        for (PendingBalanceDTO patiencePayload : balances) {
            if (!patiencePayload.getPayload().isEmpty()) {
                owedAmount += patiencePayload.getPayload().get(0).getAmount();
            }
        }
    }

    private void initializeOwedAmountTextView(View view) {
        String text = Label.getLabel("responsibility_balance_title") + ": " +
                StringUtil.getFormattedBalanceAmount(owedAmount);
        TextView balance = view.findViewById(R.id.payment_responsibility_balance);
        balance.setText(text);
    }

    private void initializeFooter(View view) {
        TextView leftButton = view.findViewById(R.id.payment_plan_button);
        if (leftButton != null) {
            leftButton.setVisibility(isPaymentPlanAvailable(owedAmount)
                    ? View.VISIBLE : View.GONE);
            leftButton.setOnClickListener(view1 -> {
                callback.onLeftActionTapped(paymentsModel, owedAmount);
                dismiss();
            });
            if (mustAddToExisting) {
                leftButton.setText(Label.getLabel("payment_plan_add_existing_short"));
            }
        }

        View middleButton = view.findViewById(R.id.partial_pay_button);
        if (middleButton != null) {
            middleButton.setVisibility(isPartialPayAvailable(owedAmount)
                    ? View.VISIBLE : View.GONE);
            middleButton.setOnClickListener(view12 -> {
                callback.onMiddleActionTapped(paymentsModel, owedAmount);
                dismiss();
            });
        }

        View rightButton = view.findViewById(R.id.payment_pay_button);
        if (rightButton != null) {
            rightButton.setOnClickListener(view13 -> {
                createPaymentModel(owedAmount);
                callback.onRightActionTapped(paymentsModel, owedAmount);
                dismiss();
            });
        }


    }

    public interface PayResponsibilityCallback {

        void onLeftActionTapped(PaymentsModel paymentsModel, double amount);

        void onRightActionTapped(PaymentsModel paymentsModel, double amount);

        void onMiddleActionTapped(PaymentsModel paymentsModel, double amount);

        void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);
    }

    private void createPaymentModel(double payAmount) {
        if (payInfoCallback == null) {
            return;
        }
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(payAmount);
        postModel.getLineItems().clear();

        List<PendingBalancePayloadDTO> responsibilityTypes = getPendingResponsibilityTypes();
        for (PendingBalancePayloadDTO responsibility : responsibilityTypes) {
            if (payAmount > 0D) {
                double itemAmount;
                if (payAmount >= responsibility.getAmount()) {
                    itemAmount = responsibility.getAmount();
                } else {
                    itemAmount = payAmount;
                }
                payAmount = (double) Math.round((payAmount - itemAmount) * 100) / 100;

                IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
                paymentLineItem.setAmount(itemAmount);

                AppointmentDTO appointmentDTO = payInfoCallback.getAppointment();
                if (appointmentDTO != null) {
                    paymentLineItem.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                    paymentLineItem.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                }

                switch (responsibility.getType()) {
                    case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COINSURANCE);
                        break;
                    case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_DEDUCTABLE);
                        break;
                    case PendingBalancePayloadDTO.CO_PAY_TYPE:
                    default:
                        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_COPAY);
                        break;
                }

                postModel.addLineItem(paymentLineItem);
            }
        }

        if (payAmount > 0) {//payment is greater than any responsibility types
            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(payAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            postModel.addLineItem(paymentLineItem);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<PendingBalancePayloadDTO> getPendingResponsibilityTypes() {
        List<PendingBalancePayloadDTO> responsibilityTypes = new ArrayList<>();
        for (PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                for (PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()) {
                    switch (pendingBalancePayloadDTO.getType()) {
                        case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:
                        case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                            responsibilityTypes.add(pendingBalancePayloadDTO);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return responsibilityTypes;
    }

    protected boolean isPartialPayAvailable(double balance) {
        PaymentsSettingsRegularPaymentsDTO regularPaymentsDTO = paymentsModel.getPaymentPayload()
                .getPaymentSettings().get(0).getPayload().getRegularPayments();
        if (regularPaymentsDTO.isAllowPartialPayments()) {
            double minBalance = regularPaymentsDTO.getPartialPaymentsThreshold();
            double minPayment = regularPaymentsDTO.getMinimumPartialPaymentAmount();
            return balance >= minBalance && balance >= minPayment;
        }

        return false;
    }

    protected boolean isPaymentPlanAvailable(double balance) {
        PaymentsPayloadSettingsDTO settingsDTO = paymentsModel.getPaymentPayload()
                .getPaymentSettings().get(0);
        PaymentsSettingsPaymentPlansDTO paymentPlanSettings = settingsDTO.getPayload().getPaymentPlans();
        if (!paymentPlanSettings.isPaymentPlansEnabled()) {
            return false;
        }

        String practiceId = settingsDTO.getMetadata().getPracticeId();
        double maxAllowablePayment = paymentsModel.getPaymentPayload().getMaximumAllowablePlanAmount(practiceId);
        if (maxAllowablePayment > balance) {
            maxAllowablePayment = balance;
        }
        for (PaymentSettingsBalanceRangeRule rule : paymentPlanSettings.getBalanceRangeRules()) {
            if (maxAllowablePayment >= rule.getMinBalance().getValue() &&
                    maxAllowablePayment <= rule.getMaxBalance().getValue()) {
                //found a valid rule that covers this balance
                if (paymentsModel.getPaymentPayload().getActivePlans(practiceId).isEmpty()) {
                    //don't already have an existing plan so this is the first plan
                    return true;
                } else if (paymentPlanSettings.isCanHaveMultiple()) {
                    // already have a plan so need to see if I can create a new one
                    return true;
                }
                break;//don't need to continue going through these rules
            }
        }

        //check if balance can be added to existing
        double minAllowablePayment = paymentsModel.getPaymentPayload().getMinimumAllowablePlanAmount(practiceId);
        if (minAllowablePayment > balance) {
            minAllowablePayment = balance;
        }
        if (paymentPlanSettings.isAddBalanceToExisting() &&
                !paymentsModel.getPaymentPayload().getValidPlans(practiceId, minAllowablePayment).isEmpty()) {
            mustAddToExisting = true;
            return true;
        }

        return false;
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, false);
        dialog.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(dialog, true);
        hideDialog();
    }
}
