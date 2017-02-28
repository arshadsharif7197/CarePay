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
import com.carecloud.carepaylibray.payments.adapter.PaymentMethodAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lmenendez on 2/27/17.
 */

public abstract class PaymentMethodFragment extends BaseFragment /*implements RadioGroup.OnCheckedChangeListener*/ {

    public interface PaymentMethodActionCallback{
        void onPaymentMethodAction(String selectedPaymentMethod, double amount);

        void onPaymentPlanAction();
    }

    public static final String TAG = PaymentMethodFragment.class.getSimpleName();

    private Activity activity;
    private Button paymentChoiceButton;
    private RadioGroup paymentMethodRadioGroup;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private ListView listView;

    protected PaymentsModel paymentsModel;
    protected List<PaymentsMethodsDTO> paymentMethodsList;
    protected String selectedPaymentMethod;
    protected HashMap<String, Integer> paymentTypeMap;

    private String dialogTitle;
    private String dialogText;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;
    private String paymentChangeMethodString;
    private String paymentFailedErrorString;

    private PaymentMethodActionCallback callback;

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle);//make sure all implementations create a proper view


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PaymentMethodActionCallback) context;
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

        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        paymentChoiceButton.setEnabled(false);
        paymentChoiceButton.setText(paymentChooseMethodString);


/*
        for (int i = 0; i < paymentMethodsList.size(); i++) {
            addPaymentMethodOptionView(i);
        }

*/

        listView = (ListView) view.findViewById(R.id.list_payment_types);
        final PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(getContext(), paymentMethodsList, paymentTypeMap);

        listView.setAdapter(paymentMethodAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

/*    protected void addPaymentMethodOptionView(int i) {//TODO this needs to be wrapped up in an adpter. This WILL cause memory leak
        paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentMethodsList.get(i).getType(), paymentMethodsList.get(i).getLabel(), i),
                radioGroupLayoutParam);

        View dividerLineView = new View(activity);
        dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
        ));
        dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
        paymentMethodRadioGroup.addView(dividerLineView);
        onSetRadioButtonRegularTypeFace();
    }


    private RadioButton getPaymentMethodRadioButton(String cardType, String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(android.R.color.transparent);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);


        if (paymentTypeMap.get(cardType) != null) {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    paymentTypeMap.get(cardType), 0, R.drawable.check_box_intake, 0);
        } else {
            radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                    paymentTypeMap.get(CarePayConstants.TYPE_CREDIT_CARD), 0, R.drawable.check_box_intake, 0);
        }

        radioButtonView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.radio_button_selector));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {//TODO patient app must implement for Android
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);
//        GradientDrawable shape = new GradientDrawable();//TODO this wans't actually being set anywhere, probably not necessary if we move to adapter with selected state drawables
//        shape.setCornerRadius(50.0f);

        for (int i = 0; i < paymentMethodsList.size(); i++) {//TODO clean this up.... set all unselected first then just set the one that is selected
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentMethodsList.get(i).getLabel())) {

                selectedPaymentMethod = selectedRadioButton.getText().toString();
                paymentChoiceButton.setText(paymentMethodsList.get(i).getButtonLabel());
                paymentChoiceButton.setTag(paymentMethodsList.get(i).getType());
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CASH)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.overlay_green));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayLabel());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CREDIT_CARD)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentChooseCreditCardButton());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_CHECK)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayLabel());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_GIFT_CARD)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayLabel());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_PAYPAL)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.overlay_green));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayPalLabel());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_HSA)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayLabel());
                }
                if(paymentMethodsList.get(i).getType().equalsIgnoreCase(CarePayConstants.TYPE_FSA)){
//                    shape.setColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    paymentChoiceButton.setText(paymentsModel.getPaymentsMetadata().getPaymentsLabel().getPaymentPayLabel());
                }
            }
        }

    }
*/

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

/*
    private void onSetRadioButtonRegularTypeFace() {//TODO needs to be a better way than itterating the views just to set style each time
        for (int i = 0; i < paymentMethodRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.activity,
                        (RadioButton) paymentMethodRadioGroup.getChildAt(i));
                ((RadioButton) paymentMethodRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
                ((RadioButton) paymentMethodRadioGroup.getChildAt(i))
                        .setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));

            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    }

*/

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

    public ListView getListView() {
        return listView;
    }


}
