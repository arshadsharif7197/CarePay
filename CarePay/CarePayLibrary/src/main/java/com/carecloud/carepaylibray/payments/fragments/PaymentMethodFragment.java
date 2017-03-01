package com.carecloud.carepaylibray.payments.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.adapter.PaymentMethodAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lmenendez on 2/27/17.
 */

public abstract class PaymentMethodFragment extends BaseFragment /*implements RadioGroup.OnCheckedChangeListener*/ {

    public static final String TAG = PaymentMethodFragment.class.getSimpleName();

    private Activity activity;
    private Button paymentChoiceButton;
    private RadioGroup paymentMethodRadioGroup;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private ListView paymentMethodList;

    protected PaymentsModel paymentsModel;
    protected List<PaymentsMethodsDTO> paymentMethodsList = new ArrayList<>();
    protected String selectedPaymentMethod;
    protected HashMap<String, Integer> paymentTypeMap;

    private String dialogTitle;
    private String dialogText;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;
    private String paymentChangeMethodString;
    private String paymentFailedErrorString;

    private PaymentNavigationCallback callback;

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle);//make sure all implementations create a proper view


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement PaymentMethocActionCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String paymentInfo = bundle.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);
            paymentMethodsList = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getRegularPayments().getPaymentMethods();
            getLabels();
        }

        initPaymentTypeMap();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        activity = getActivity();

        setupTitleViews(view);

        initializeViews(view);

    }

    private void setupTitleViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar!=null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            toolbar.setTitle("");
            toolbar.setNavigationIcon(ContextCompat.getDrawable(activity, R.drawable.icn_patient_mode_nav_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
            title.setText(titlePaymentMethodString);
        }else {
            TextView title = (TextView) view.findViewById(R.id.paymentMethodTitleLabel);
            if(title!=null) {
                title.setText(titlePaymentMethodString);
            }
        }
    }

    private void initializeViews(View view) {
/*
        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);


        paymentMethodRadioGroup = (RadioGroup) view.findViewById(R.id.paymentMethodsRadioGroup);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);
*/
        Button createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        createPaymentPlanButton.setText(paymentCreatePlanString);
        createPaymentPlanButton.setEnabled(false);//TODO enable this when ready to support payment plans

        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        paymentChoiceButton.setEnabled(false);
        paymentChoiceButton.setText(paymentChooseMethodString);


/*
        for (int i = 0; i < paymentMethodsList.size(); i++) {
            addPaymentMethodOptionView(i);
        }

*/

        paymentMethodList = (ListView) view.findViewById(R.id.list_payment_types);
        final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList, paymentTypeMap);

        paymentMethodList.setAdapter(paymentMethodAdapter);
        paymentMethodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PaymentsMethodsDTO paymentMethod = paymentMethodsList.get(position);
                selectedPaymentMethod = paymentMethod.getLabel();
                paymentMethodAdapter.setSelectedItem(position);
                paymentMethodAdapter.notifyDataSetChanged();

                paymentChoiceButton.setText(paymentMethod.getButtonLabel());
                paymentChoiceButton.setTag(paymentMethod.getType());
                paymentChoiceButton.setEnabled(true);

            }
        });

    }

    private void getLabels() {
        if (paymentsModel != null) {
            PaymentsMetadataModel paymentsMetadataModel = paymentsModel.getPaymentsMetadata();
            if (paymentsMetadataModel != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataModel.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    dialogTitle = paymentsLabelsDTO.getPaymentSeeFrontDeskButton();
                    dialogText = paymentsLabelsDTO.getPaymentBackButton();
                    titlePaymentMethodString = paymentsLabelsDTO.getPaymentMethodTitle();
                    paymentChooseMethodString = paymentsLabelsDTO.getPaymentChooseMethodButton();
                    paymentCreatePlanString = paymentsLabelsDTO.getPaymentCreatePlanButton();
                    paymentChangeMethodString = paymentsLabelsDTO.getPaymentChangeMethodButton();
                    paymentFailedErrorString = paymentsLabelsDTO.getPaymentFailedErrorMessage();
                }
            }
        }
    }

    private void initPaymentTypeMap(){
        // Initialize HashMap.
        paymentTypeMap = new HashMap<>();
        paymentTypeMap.put(CarePayConstants.TYPE_CASH, R.drawable.payment_cash_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CREDIT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_CHECK, R.drawable.payment_check_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_GIFT_CARD, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_PAYPAL, R.drawable.payment_paypal_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_HSA, R.drawable.payment_credit_card_button_selector);
        paymentTypeMap.put(CarePayConstants.TYPE_FSA, R.drawable.payment_credit_card_button_selector);
    }


    protected void handlePaymentButton(String type, double amount){
        switch (type) {
            case CarePayConstants.TYPE_CASH:
                new LargeAlertDialog(getActivity(), dialogTitle, dialogText, R.color.lightningyellow, R.drawable.icn_notification_basic, new LargeAlertDialog.LargeAlertInterface() {
                    @Override
                    public void onActionButton() {
                    }
                }).show();
                break;

            case CarePayConstants.TYPE_CREDIT_CARD:
                callback.onPaymentMethodAction(selectedPaymentMethod, amount);
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
                List<PaymentPatientBalancesPayloadDTO> paymentList = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getPayload();

                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
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
            String type = (String) view.getTag();
            Bundle args = getArguments();
            double amount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);

            handlePaymentButton(type, amount);
        }
    };

    public ListView getPaymentMethodList() {
        return paymentMethodList;
    }


}
