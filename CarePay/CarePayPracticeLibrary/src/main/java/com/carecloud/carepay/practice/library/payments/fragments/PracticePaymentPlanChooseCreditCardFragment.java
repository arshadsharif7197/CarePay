package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 2/2/18
 */

public class PracticePaymentPlanChooseCreditCardFragment extends PracticeChooseCreditCardFragment {
    private static final String KEY_DATE = "date";

    private PaymentPlanCreateInterface callback;
    private PaymentPlanPostModel paymentPlanPostModel;
    private PaymentPlanDTO paymentPlanDTO;

    private Date paymentDate;

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanPostModel       the post model for the plan
     * @return an instance of PracticePaymentPlanChooseCreditCardFragment
     */
    public static PracticePaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                          String selectedPaymentMethodLabel,
                                                                          PaymentPlanPostModel paymentPlanPostModel) {
        Bundle args = new Bundle();
//        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanPostModel);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);

        PracticePaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PracticePaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanDTO             payment plan details
     * @param onlySelectMode             select mode
     * @return an instance of PracticePaymentPlanChooseCreditCardFragment
     */
    public static PracticePaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                          String selectedPaymentMethodLabel,
                                                                          PaymentPlanDTO paymentPlanDTO,
                                                                          boolean onlySelectMode) {
        return newInstance(paymentsDTO, selectedPaymentMethodLabel, paymentPlanDTO, onlySelectMode, null);
    }

    /**
     * @param paymentsDTO                the payment model
     * @param selectedPaymentMethodLabel the selected payment method label
     * @param paymentPlanDTO             payment plan details
     * @param onlySelectMode             select mode
     * @param paymentDate                payment date
     * @return an instance of PracticePaymentPlanChooseCreditCardFragment
     */
    public static PracticePaymentPlanChooseCreditCardFragment newInstance(PaymentsModel paymentsDTO,
                                                                          String selectedPaymentMethodLabel,
                                                                          PaymentPlanDTO paymentPlanDTO,
                                                                          boolean onlySelectMode,
                                                                          Date paymentDate) {
        Bundle args = new Bundle();
//        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethodLabel);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);

        if (paymentDate != null) {
            DateUtil.getInstance().setDate(paymentDate);
            args.putString(KEY_DATE, DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
        }
        PracticePaymentPlanChooseCreditCardFragment chooseCreditCardFragment = new PracticePaymentPlanChooseCreditCardFragment();
        chooseCreditCardFragment.setArguments(args);
        return chooseCreditCardFragment;
    }


    @Override
    protected void attachCallback(Context context) {
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
            throw new ClassCastException("attached context must implement ChooseCreditCardInterface");
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
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        Button addNewCardButton = view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        nextButton.setOnClickListener(nextButtonListener);
        if (paymentPlanPostModel != null) {
            nextButton.setText(Label.getLabel("payment_plan_continue"));
        }

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE) ||
                HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_2_DEVICE);
        Button swipeCardButton = view.findViewById(R.id.swipeCreditCarNowButton);
        if (isCloverDevice && paymentPlanDTO != null && (paymentDate == null || DateUtil.isToday(paymentDate))) {
            swipeCardButton.setVisibility(View.VISIBLE);
        } else {
            swipeCardButton.setVisibility(View.GONE);
        }

        //temp fix for hiding this until we can handle it properly for one-time payment TODO: CLOVERPAY remove this
        swipeCardButton.setVisibility(View.GONE);

    }

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (paymentPlanPostModel != null) {
                onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
            }
            if (paymentPlanDTO != null) {
                onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
            }
        }
    };

    private void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                      PaymentPlanPostModel paymentPlanPostModel,
                                      boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment;
        if (!onlySelectMode && paymentPlanPostModel != null) {
            fragment = PracticePaymentPlanAddCreditCardFragment
                    .newInstance(paymentsModel, paymentPlanPostModel);
        } else {
            fragment = PracticePaymentPlanAddCreditCardFragment
                    .newInstance(paymentsModel, (PaymentPlanDTO) null, onlySelectMode);
        }
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, false);
        hideDialog();
    }

    private void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                      final PaymentPlanDTO paymentPlanDTO,
                                      boolean onlySelectMode,
                                      final Date paymentDate) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        fragment.setChangePaymentMethodListener(new LargeAlertDialogFragment.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
                callback.displayDialogFragment(fragment, false);
            }
        });
        callback.displayDialogFragment(fragment, false);
        hideDialog();
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (selectedCreditCard != null) {
                if (onlySelectMode) {
                    dismiss();
                    callback.onCreditCardSelected(selectedCreditCard);
                } else {
                    PapiPaymentMethod papiPaymentMethod = getPapiPaymentMethod();

                    if (papiPaymentMethod == null) {
                        papiPaymentMethod = new PapiPaymentMethod();
                        papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
                        papiPaymentMethod.setCardData(getCreditCardModel());
                    }

                    if (paymentPlanPostModel != null) {
                        paymentPlanPostModel.setPapiPaymentMethod(papiPaymentMethod);
                        paymentPlanPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);

                        onDisplayPaymentPlanTerms(paymentsModel, paymentPlanPostModel);
                    }

                    if (paymentPlanDTO != null) {
                        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
                        postModel.setPapiPaymentMethod(papiPaymentMethod);
                        postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
                        amountToMakePayment = postModel.getAmount();

                        if (paymentDate != null) {
                            DateUtil.getInstance().setDate(paymentDate);
                            postModel.setPaymentDate(DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());
                        }
                        makePlanPayment();
                    }
                }
            }
        }
    };

    private void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        hideProgressDialog();
        PracticePaymentPlanTermsFragment fragment = PracticePaymentPlanTermsFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        fragment.setOnCancelListener(onDialogCancelListener);
        callback.displayDialogFragment(fragment, false);
        hideDialog();
    }

    private void makePlanPayment() {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
        queries.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        queries.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
        queries.put("payment_plan_id", paymentPlanDTO.getMetadata().getPaymentPlanId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        String paymentModelJson = gson.toJson(paymentsModel.getPaymentPayload().getPaymentPostModel());
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePlanPayment();
        if (paymentDate != null) {
            getWorkflowServiceHelper().execute(transitionDTO, schedulePaymentCallback, paymentModelJson, queries, header);
        } else {
            getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);
        }
    }

    private WorkflowServiceCallback schedulePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            ((OneTimePaymentInterface) callback).showScheduledPaymentConfirmation(workflowDTO);
            if (getDialog() != null) {
                dismiss();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    protected void showConfirmation(WorkflowDTO workflowDTO) {
        ((OneTimePaymentInterface) callback).showPaymentConfirmation(workflowDTO, true);
    }

}
