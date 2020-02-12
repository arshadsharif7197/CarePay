package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.payments.adapter.PaymentMethodAdapter;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodInterface;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lmenendez on 2/27/17
 */

public abstract class PaymentMethodFragment extends BasePaymentDialogFragment {

    public static final String TAG = PaymentMethodFragment.class.getSimpleName();

    protected PaymentsModel paymentsModel;
    protected double amountToMakePayment;

    private List<PaymentsMethodsDTO> paymentMethodsList = new ArrayList<>();
    private HashMap<String, Integer> paymentTypeMap;

    protected PaymentMethodInterface callback;
    protected boolean onlySelectMode;

    public abstract View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle);//make sure all implementations create a proper view


    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof PaymentViewHandler) {
                callback = ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PaymentMethodInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PaymentMethodInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle bundle = getArguments();
        if (bundle != null) {
            amountToMakePayment = bundle.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, bundle);
            onlySelectMode = bundle.getBoolean(CarePayConstants.ONLY_SELECT_MODE);
            if (!paymentsModel.getPaymentPayload().getPaymentSettings().isEmpty()) {
                paymentMethodsList = getPaymentMethodList();
            }
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
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    protected void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("payment_method_title"));
            toolbar.setTitle("");
        }
    }

    private void initializeViews(View view) {
//        Button createPaymentPlanButton = view.findViewById(R.id.createPaymentPlanButton);
//        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
//        createPaymentPlanButton.setText(Label.getLabel("payment_create_plan_text"));
//        createPaymentPlanButton.setEnabled(false);//TODO enable this when ready to support payment plans

        ListView paymentMethodList = view.findViewById(R.id.list_payment_types);
        final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList);

        paymentMethodList.setAdapter(paymentMethodAdapter);
        paymentMethodList.setOnItemClickListener((parent, view1, position, id) -> {
            PaymentsMethodsDTO paymentMethod = paymentMethodsList.get(position);
            handlePaymentButton(paymentMethod, amountToMakePayment);
        });

    }

    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        switch (paymentMethod.getType()) {
            case CarePayConstants.TYPE_CASH:
                LargeAlertDialogFragment fragment = LargeAlertDialogFragment.newInstance(Label.getLabel("payment_cash_message"),
                        Label.getLabel("payment_ok"),
                        R.color.lemonGreen, R.drawable.icn_payment_cash_selected, 18);
                fragment.setLargeAlertInterface(() -> onPaymentCashFinished());
                fragment.show(getFragmentManager(), LargeAlertDialogFragment.class.getName());
                logPaymentMethodSelection(getString(R.string.payment_cash));
                break;

            case CarePayConstants.TYPE_CREDIT_CARD:
                onPaymentMethodAction(paymentMethod, amount, paymentsModel);
                logPaymentMethodSelection(getString(R.string.payment_credit_card));
                break;

            default:
                break;
        }

    }

    protected void onPaymentMethodAction(PaymentsMethodsDTO paymentMethod,
                                         double amount,
                                         PaymentsModel paymentsModel) {
        callback.onPaymentMethodAction(paymentMethod, amount, paymentsModel);
    }

    protected void onPaymentCashFinished() {
        //do nothing for patient app
    }

    protected List<PaymentsMethodsDTO> getPaymentMethodList() {
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        for (PaymentsPayloadSettingsDTO paymentSetting : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (paymentSetting.getMetadata().getPracticeId().equals(userPracticeDTO.getPracticeId()) &&
                    paymentSetting.getMetadata().getPracticeMgmt().equals(userPracticeDTO.getPracticeMgmt())) {
                return paymentSetting.getPayload().getRegularPayments().getPaymentMethods();
            }
        }
        return paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getRegularPayments().getPaymentMethods();
    }

    protected void logPaymentMethodSelection(String type) {
        MixPanelUtil.logEvent(getString(R.string.event_payment_method_selected), getString(R.string.param_payment_type), type);
    }

}
