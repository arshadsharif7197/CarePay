package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.androidpay.models.PayeezyAndroidPayResponse;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanPaymentMethodFragment extends PatientPaymentMethodFragment {
    public static final String KEY_DATE = "date";

    private PaymentPlanCreateInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;
    private Date paymentDate;

    /**
     * @param paymentsModel        the payments DTO
     * @param paymentPlanPostModel post model for payments plan to execute
     * @return an instance of PaymentPlanPaymentMethodFragment
     */
    public static PaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PaymentPlanPaymentMethodFragment fragment = new PaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  the payments DTO
     * @param paymentPlanDTO existing payments plan to make payments for
     * @param onlySelectMode onlySelectMode
     * @param paymentDate    payments Date
     * @return an instance of PaymentPlanPaymentMethodFragment
     */
    public static PaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode,
                                                               Date paymentDate) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        if (paymentDate != null) {
            DateUtil.getInstance().setDate(paymentDate);
            args.putString(KEY_DATE, DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        PaymentPlanPaymentMethodFragment fragment = new PaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  the payments DTO
     * @param paymentPlanDTO existing payments plan to make payments for
     * @param onlySelectMode onlySelectMode
     * @return an instance of PaymentPlanPaymentMethodFragment
     */
    public static PaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode) {
        return newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, null);
    }


    @Override
    public void attachCallback(Context context) {
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
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanPostModel = DtoHelper.getConvertedDTO(PaymentPlanPostModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);

        String dateString = args.getString(KEY_DATE);
        if (dateString != null) {
            DateUtil.getInstance().setDateRaw(dateString);
            paymentDate = DateUtil.getInstance().getDate();
        }
        if(paymentsModel.getPaymentPayload().getPaymentPostModel() != null) {
            amountToMakePayment = paymentsModel.getPaymentPayload().getPaymentPostModel().getAmount();
        }
        shouldInitAndroidPay = callback instanceof OneTimePaymentInterface &&
                paymentPlanDTO != null && paymentPlanDTO.getMetadata().getPaymentPlanId() != null &&
                (paymentDate == null || DateUtil.isToday(paymentDate)
                && amountToMakePayment > 0D);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        switch (paymentMethod.getType()) {
            case CarePayConstants.TYPE_CASH:
                super.handlePaymentButton(paymentMethod, amount);
                break;
            case CarePayConstants.TYPE_CREDIT_CARD:
                if (paymentPlanPostModel != null) {
                    callback.onSelectPaymentPlanMethod(paymentMethod, paymentsModel, paymentPlanPostModel, onlySelectMode);
                }
                if (paymentPlanDTO != null && callback instanceof OneTimePaymentInterface) {
                    ((OneTimePaymentInterface) callback).onSelectPaymentPlanMethod(paymentMethod, paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
                }
                logPaymentMethodSelection(getString(R.string.payment_credit_card));
                break;
            default:
        }
    }

    @Override
    protected List<PaymentsMethodsDTO> getPaymentMethodList() {
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        return paymentsModel.getPaymentPayload().getPaymentSetting(userPracticeDTO.getPracticeId())
                .getPayload().getPaymentPlans().getPaymentMethods();
    }

    protected void processPayment(JsonElement rawResponse, IntegratedPaymentPostModel postModel) {
        try {
            Gson gson = new Gson();
            PayeezyAndroidPayResponse androidPayResponse = gson.fromJson(rawResponse, PayeezyAndroidPayResponse.class);

            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel(androidPayResponse));

            postModel.setTransactionResponse(rawResponse.getAsJsonObject());
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_ANDROID);
            postModel.setPapiPaymentMethod(papiPaymentMethod);

            IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
            postModelMetadata.setAppointmentId(callback.getAppointmentId());

            makePlanPayment(postModel, rawResponse);
        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, rawResponse, jsx.getMessage());
        }
    }

    private void makePlanPayment(IntegratedPaymentPostModel postModel, JsonElement rawResponse) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queries.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queries.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        String paymentModelJson = gson.toJson(postModel);
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePlanPayment();

        getWorkflowServiceHelper().execute(transitionDTO, getMakePaymentCallback(rawResponse), paymentModelJson, queries, header);
    }

}
