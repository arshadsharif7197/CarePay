package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customdialogs.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsRegularPaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.ResponsibilityType;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ResponsibilityFragmentDialog extends BaseDialogFragment implements PaymentLineItemsListAdapter.PaymentLineItemCallback {

    private String leftLabel;
    private String rightLabel;
    private PaymentsModel paymentsModel;
    private PatientBalanceDTO patientBalance;
    private PayResponsibilityCallback callback;
    @Nullable private PaymentConfirmationInterface payInfoCallback;
    private double owedAmount = 0;
    private ResponsibilityHeaderModel headerModel;

    @Override
    protected String getCancelString() {
        return null;
    }

    @Override
    protected int getCancelImageResource() {
        return com.carecloud.carepaylibrary.R.drawable.icn_close;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_dialog_payment_responsibility;
    }

    @Override
    protected boolean getCancelable() {
        return false;
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        callback.onDetailItemClick(paymentsModel, paymentLineItem);
        dismiss();
    }

    /**
     * @param paymentsModel the payment model
     * @param leftLabel     the label of the left bottom button
     * @param rightLabel    the label of the right bottom button
     * @return new instance of a ResponsibilityFragmentDialog
     */
    public static ResponsibilityFragmentDialog newInstance(PaymentsModel paymentsModel,
                                                           String leftLabel, String rightLabel,
                                                           ResponsibilityHeaderModel headerModel) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, headerModel);
        args.putString("leftLabel", leftLabel);
        args.putString("rightLabel", rightLabel);

        ResponsibilityFragmentDialog dialog = new ResponsibilityFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, arguments);
        headerModel = DtoHelper.getConvertedDTO(ResponsibilityHeaderModel.class, arguments);
        patientBalance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        leftLabel = arguments.getString("leftLabel");
        rightLabel = arguments.getString("rightLabel");
        handleException();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitialization(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PayResponsibilityCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement PayResponsibilityCallback");
        }

        try{
            payInfoCallback = (PaymentConfirmationInterface) context;
        }catch (ClassCastException cce){
            //this callback is optional and expected only in patient mode
            cce.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private void handleException() {
        Thread thread = Thread.currentThread();
        thread.setDefaultUncaughtExceptionHandler(new SystemUtil());
    }

    private void onInitialization(View view) {
        initializeHeader(view);
        initializeBody(view);
        initializeFooter(view);
    }

    private void initializeHeader(View view) {
        ((TextView) view.findViewById(R.id.patient_full_name)).setText(headerModel.getHeaderFullTitle());
        ImageView profilePhoto = (ImageView) view.findViewById(R.id.patient_profile_photo);
        final TextView shortName = (TextView) view.findViewById(R.id.patient_profile_short_name);
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

            ImageView bgImage = (ImageView) view.findViewById(R.id.profile_bg_image);


            Picasso.with(getContext()).load(photoUrl)
                    .fit()
                    .transform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.TOP))
                    .into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
        }
        TextView patientProviderNameTextView = (TextView) view.findViewById(R.id.patient_provider_name);
        patientProviderNameTextView.setText(headerModel.getHeaderSubtitle());
    }

    private void initializeBody(View view) {
        if (null != patientBalance && null != patientBalance.getBalances() && !patientBalance.getBalances().isEmpty()) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.rowLayout);
            List<PendingBalanceDTO> balances = patientBalance.getBalances();
            initializeOwedAmount(balances);
            if (owedAmount > 0) {
                initializePaymentLines(view, balances);
                linearLayout.setVisibility(View.GONE);
            } else{
                linearLayout.setVisibility(View.VISIBLE);
                TextView paymentDetailLabel =(TextView) view.findViewById(R.id.itemNameLabel);
                TextView paymentDetailAmount =(TextView) view.findViewById(R.id.itemAmountLabel);

                paymentDetailLabel.setText(Label.getLabel("payment_details_patient_balance_label"));
                paymentDetailAmount.setText("$0.00");

            }
        }

        initializeOwedAmountTextView(view);
    }

    private void initializePaymentLines(View view, List<PendingBalanceDTO> balances) {
        RecyclerView amountDetails = (RecyclerView) view.findViewById(R.id.payment_responsibility_balance_details);
        amountDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(getContext(), getAllPendingBalancePayloads(balances), this);
        amountDetails.setAdapter(adapter);
    }

    protected List<PendingBalancePayloadDTO> getAllPendingBalancePayloads(List<PendingBalanceDTO> pendingBalances){
        List<PendingBalancePayloadDTO> pendingBalancePayloads = new ArrayList<>();
        for(PendingBalanceDTO pendingBalance : pendingBalances){
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
        ((TextView) view.findViewById(R.id.payment_responsibility_balance)).setText(text);
    }

    private void initializeFooter(View view) {
        Button leftButton = (Button) view.findViewById(R.id.payment_plan_button);

        if (null == leftLabel) {
            leftButton.setVisibility(View.GONE);
        } else {
            leftButton.setText(leftLabel);
            leftButton.setVisibility(isPartialPayAvailable(owedAmount)?View.VISIBLE:View.GONE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != callback) {
                        callback.onLeftActionTapped(paymentsModel, owedAmount);
                    }
                    dismiss();
                }
            });
        }

        Button rightButton = (Button) view.findViewById(R.id.payment_pay_button);
        rightButton.setText(rightLabel);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != callback) {
                    createPaymentModel(owedAmount);
                    callback.onRightActionTapped(paymentsModel, owedAmount);
                }

                dismiss();
            }
        });
    }

    public interface PayResponsibilityCallback {

        void onLeftActionTapped(PaymentsModel paymentsModel, double owedAmount);

        void onRightActionTapped(PaymentsModel paymentsModel, double amount);

        void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem);
    }

    private void createPaymentModel(double payAmount){
        PaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if( postModel == null){
            postModel = new PaymentPostModel();
        }
        postModel.setAmount(payAmount);

        List<PendingBalancePayloadDTO> responsibilityTypes = getPendingResponsibilityTypes();
        for(PendingBalancePayloadDTO responsibility : responsibilityTypes){
            if(payAmount > 0D) {
                double itemAmount;
                if (payAmount >= responsibility.getAmount()) {
                    itemAmount = responsibility.getAmount();
                } else {
                    itemAmount = payAmount;
                }
                payAmount = (double) Math.round((payAmount - itemAmount) * 100) / 100;

                PaymentObject paymentObject = new PaymentObject();
                paymentObject.setAmount(itemAmount);

                if(payInfoCallback != null) {
                    AppointmentDTO appointmentDTO = payInfoCallback.getAppointment();
                    if (appointmentDTO != null) {
                        paymentObject.setProviderID(appointmentDTO.getPayload().getProvider().getGuid());
                        paymentObject.setLocationID(appointmentDTO.getPayload().getLocation().getGuid());
                    }
                }

                switch (responsibility.getType()){
                    case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        paymentObject.setResponsibilityType(ResponsibilityType.co_insurance);
                        break;
                    case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        paymentObject.setResponsibilityType(ResponsibilityType.deductable);
                        break;
                    case PendingBalancePayloadDTO.CO_PAY_TYPE:
                    default:
                        paymentObject.setResponsibilityType(ResponsibilityType.co_pay);
                        break;
                }
                postModel.addPaymentMethod(paymentObject);
            }
        }

        if(payAmount > 0){//payment is greater than any responsibility types
            PaymentObject paymentObject = new PaymentObject();
            paymentObject.setAmount(payAmount);
            paymentObject.setDescription("Unapplied Amount");

            postModel.addPaymentMethod(paymentObject);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private List<PendingBalancePayloadDTO> getPendingResponsibilityTypes(){
        List<PendingBalancePayloadDTO> responsibilityTypes = new ArrayList<>();
        for(PatientBalanceDTO patientBalanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()){
                for(PendingBalancePayloadDTO pendingBalancePayloadDTO : pendingBalanceDTO.getPayload()){
                    switch (pendingBalancePayloadDTO.getType()){
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

    protected boolean isPartialPayAvailable(double balance){
        PaymentsSettingsRegularPaymentsDTO regularPaymentsDTO = paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getRegularPayments();
        if(regularPaymentsDTO.isAllowPartialPayments()){
            double minBalance = regularPaymentsDTO.getPartialPaymentsThreshold();
            return balance >= minBalance;
        }

        return false;
    }

}
