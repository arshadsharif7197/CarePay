package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.adapter.PaymentMethodAdapter;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lmenendez on 2/27/17.
 */

public abstract class PaymentMethodFragment extends BaseDialogFragment {

    public static final String TAG = PaymentMethodFragment.class.getSimpleName();

    private Button paymentChoiceButton;
    protected ListView paymentMethodList;

    protected PaymentsModel paymentsModel;
    protected List<PatientBalanceDTO> paymentList = new ArrayList<>();
    protected List<PaymentsMethodsDTO> paymentMethodsList = new ArrayList<>();
    protected HashMap<String, Integer> paymentTypeMap;

    private PaymentNavigationCallback callback;

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle);//make sure all implementations create a proper view


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethocActionCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String paymentInfo = bundle.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);
            if (!paymentsModel.getPaymentPayload().getPaymentSettings().isEmpty()) {
                paymentMethodsList = paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getPayload().getRegularPayments().getPaymentMethods();//todo need to lookup appropriate settings for prctice id on selected balance
            }
            paymentList = paymentsModel.getPaymentPayload().getPatientBalances();
        }

        initPaymentTypeMap();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    protected void setupTitleViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("payment_method_title"));
            toolbar.setTitle("");
        }
    }

    private void initializeViews(View view) {
        Button createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        createPaymentPlanButton.setText(Label.getLabel("payment_create_plan_text"));
        createPaymentPlanButton.setEnabled(false);//TODO enable this when ready to support payment plans

        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        paymentChoiceButton.setEnabled(false);
        paymentChoiceButton.setText(Label.getLabel("payment_chooce_method"));


        paymentMethodList = (ListView) view.findViewById(R.id.list_payment_types);
        final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList, paymentTypeMap);

        paymentMethodList.setAdapter(paymentMethodAdapter);
        paymentMethodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PaymentsMethodsDTO paymentMethod = paymentMethodsList.get(position);
                paymentMethodAdapter.setSelectedItem(position);
                paymentMethodAdapter.notifyDataSetChanged();

                paymentChoiceButton.setText(paymentMethod.getButtonLabel());
                paymentChoiceButton.setTag(paymentMethod);
                paymentChoiceButton.setEnabled(true);

            }
        });

    }

    private void initPaymentTypeMap() {
        // Initialize HashMap.
        paymentTypeMap = new HashMap<>();
        paymentTypeMap.put(CarePayConstants.TYPE_CASH, R.drawable.payment_cash_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CREDIT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CHECK, R.drawable.payment_check_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_GIFT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_PAYPAL, R.drawable.payment_paypal_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_HSA, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_FSA, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_PAYMENT_PLAN, R.drawable.payment_credit_card_button_selector);
    }


    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount) {
        switch (paymentMethod.getType()) {
            case CarePayConstants.TYPE_CASH:
                new LargeAlertDialog(getActivity(), Label.getLabel("payment_see_front_desk_button"),
                        Label.getLabel("payment_back_button"), R.color.lightningyellow, R.drawable.icn_notification_basic, new LargeAlertDialog.LargeAlertInterface() {
                    @Override
                    public void onActionButton() {
                    }
                }).show();
                break;

            case CarePayConstants.TYPE_CREDIT_CARD:
                callback.onPaymentMethodAction(paymentMethod, amount, paymentsModel);
                break;

            default:
                break;
        }

    }

    private View.OnClickListener createPaymentPlanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (paymentsModel != null) {

                double previousBalance = 0;
                double coPay = 0;

                for (PaymentPatientBalancesPayloadDTO payment : paymentList.get(0).getPayload()) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PREVIOUS_BALANCE)) {
                        previousBalance = Double.parseDouble(payment.getTotal());
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        coPay = Double.parseDouble(payment.getTotal());
                    }
                }

                if ((previousBalance + coPay) > 0) {
                    callback.onPaymentPlanAction();
                }
            }
        }
    };

    private View.OnClickListener paymentChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PaymentsMethodsDTO paymentMethod = (PaymentsMethodsDTO) view.getTag();
            Bundle args = getArguments();
            double amount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);

            handlePaymentButton(paymentMethod, amount);
        }
    };

    public ListView getPaymentMethodList() {
        return paymentMethodList;
    }


}
