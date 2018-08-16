package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanPaymentMethodFragment extends PaymentMethodFragment {
    public static final String KEY_DATE = "date";

    private PaymentPlanCreateInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;
    private Date paymentDate;

    /**
     * @param paymentsModel        the payments DTO
     * @param paymentPlanPostModel post model for payment plan to execute
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
     * @param paymentPlanDTO existing payment plan to make payment for
     * @param onlySelectMode onlySelectMode
     * @param paymentDate    payment Date
     * @return an instance of PaymentPlanPaymentMethodFragment
     */
    public static PaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO,
                                                               boolean onlySelectMode,
                                                               Date paymentDate){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        if(paymentDate != null) {
            DateUtil.getInstance().setDate(paymentDate);
            args.putString(KEY_DATE, DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        PaymentPlanPaymentMethodFragment fragment = new PaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  the payments DTO
     * @param paymentPlanDTO existing payment plan to make payment for
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
        if(dateString != null){
            DateUtil.getInstance().setDateRaw(dateString);
            paymentDate = DateUtil.getInstance().getDate();
        }
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
        for (PaymentsPayloadSettingsDTO paymentSetting : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (paymentSetting.getMetadata().getPracticeId().equals(userPracticeDTO.getPracticeId()) &&
                    paymentSetting.getMetadata().getPracticeMgmt().equals(userPracticeDTO.getPracticeMgmt())) {
                return paymentSetting.getPayload().getPaymentPlans().getPaymentMethods();
            }
        }
        return paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getPaymentPlans().getPaymentMethods();
    }

}
