package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
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
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanPaymentMethodFragment extends PracticePaymentMethodDialogFragment {
    private static final String KEY_DATE = "date";

    private PaymentPlanCreateInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;

    private Date paymentDate;

    /**
     * @param paymentsModel        the payments DTO
     * @param paymentPlanPostModel post model for payment plan to execute
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PracticePaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanPostModel);

        PracticePaymentPlanPaymentMethodFragment fragment = new PracticePaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  the payments DTO
     * @param paymentPlanDTO existing payment plan to make payment for
     * @param onlySelectMode select mode
     * @return an instance of PracticePaymentPlanPaymentMethodFragment
     */
    public static PracticePaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                                       PaymentPlanDTO paymentPlanDTO,
                                                                       boolean onlySelectMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);

        PracticePaymentPlanPaymentMethodFragment fragment = new PracticePaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param paymentsModel  the payments DTO
     * @param paymentPlanDTO existing payment plan to make payment for
     * @param onlySelectMode select mode
     * @param paymentDate    payment date
     * @return an instance of PracticePaymentPlanPaymentMethodFragment
     */
    public static PracticePaymentPlanPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
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

        PracticePaymentPlanPaymentMethodFragment fragment = new PracticePaymentPlanPaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
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
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        //hide swipe card because these cards are not reusable
        Button swipeCreditCarNowButton = view.findViewById(R.id.swipeCreditCarNowButton);
        View swipeCreditCardNowLayout = view.findViewById(R.id.swipeCreditCardNowLayout);
        if (swipeCreditCarNowButton.isEnabled() && paymentPlanDTO != null && (paymentDate == null || DateUtil.isToday(paymentDate))) {
            swipeCreditCardNowLayout.setVisibility(View.VISIBLE);
        } else {
            swipeCreditCardNowLayout.setVisibility(View.GONE);
        }

        //temp fix for hiding this until we can handle it properly for one-time payment TODO: CLOVERPAY remove this
        swipeCreditCardNowLayout.setVisibility(View.GONE);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
//                if (!onlySelectMode && onCancelListener == null) {
//                    dialogCallback.onDismissPaymentMethodDialog(paymentsModel);
//                }
            }
        });
    }

    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        switch (paymentMethod.getType()) {
            case CarePayConstants.TYPE_CASH:
                super.handlePaymentButton(paymentMethod, amount);
                break;
            case CarePayConstants.TYPE_CREDIT_CARD:
                if (paymentPlanPostModel != null) {
                    onSelectPaymentPlanMethod(paymentMethod, paymentsModel, paymentPlanPostModel, onlySelectMode);
                }
                if (paymentPlanDTO != null) {
                    onSelectPaymentPlanMethod(paymentMethod, paymentsModel,
                            paymentPlanDTO, onlySelectMode, paymentDate);
                }
                logPaymentMethodSelection(getString(R.string.payment_credit_card));
                break;
            default:
        }
    }

    private void onSelectPaymentPlanMethod(PaymentsMethodsDTO paymentMethod,
                                           PaymentsModel paymentsModel,
                                           PaymentPlanPostModel paymentPlanPostModel,
                                           boolean onlySelectMode) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, paymentMethod.getLabel(), paymentPlanPostModel);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
            hideDialog();
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    private void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                      final PaymentPlanPostModel paymentPlanPostModel,
                                      boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        fragment.setChangePaymentMethodListener(new LargeAlertDialogFragment.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                cancel();
            }
        });
        callback.displayDialogFragment(fragment, true);
        hideDialog();
    }

    private void onSelectPaymentPlanMethod(PaymentsMethodsDTO paymentMethod,
                                           PaymentsModel paymentsModel,
                                           PaymentPlanDTO paymentPlanDTO,
                                           boolean onlySelectMode,
                                           Date paymentDate) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, paymentMethod.getLabel(), paymentPlanDTO,
                            onlySelectMode, paymentDate);
            fragment.setOnCancelListener(onDialogCancelListener);
            callback.displayDialogFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        }
    }


    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode,
                                     final Date paymentDate) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setOnCancelListener(onDialogCancelListener);
        fragment.setChangePaymentMethodListener(new LargeAlertDialogFragment.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                cancel();
            }
        });
        callback.displayDialogFragment(fragment, false);
        hideDialog();
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
