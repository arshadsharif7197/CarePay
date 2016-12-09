package com.carecloud.carepay.practice.library.payments.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.BaseCheckinFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends BaseCheckinFragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup paymentMethodRadioGroup;
    private Button createPaymentPlanButton;
    private Button paymentChoiceButton;
    private Activity activity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;
    private String selectedPaymentMethod;
    private String[] paymentMethodsArray;
    private String[] createPaymentMethodButtonCaptionArray;
    private int[] paymentMethodsDrawableArray;
    private PaymentsModel paymentsModel;
    private PaymentsMetadataModel paymentsMetadataModel;
    private PaymentsLabelDTO paymentsLabelsDTO;
    private String dialogTitle;
    private String dialogText;
    private List<PaymentsMethodsDTO> paymentList;
    private String titlePaymentMethodString;
    private String paymentChooseMethodString;
    private String paymentCreatePlanString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        activity = getActivity();
        Gson gson = new Gson();
        Bundle arguments = getArguments();
        String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
        paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        paymentList = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getPaymentMethods();

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);
        getLabels();
        title.setText(titlePaymentMethodString);
        toolbar.setTitle(titlePaymentMethodString);

        initilizeViews(view);

        return view;
    }

    private RadioButton getPaymentMethodRadioButton(String cardType, String cardInfo, int index) {

        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(android.R.color.transparent);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        // Initialize HashMap.
        HashMap<String, Integer> cardTypeMap = new HashMap<>();
        cardTypeMap.put(CarePayConstants.TYPE_CASH, R.drawable.payment_cash_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_CREDIT_CARD, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_CHECK, R.drawable.payment_check_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_GIFT_CARD, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_PAYPAL, R.drawable.payment_paypal_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_HSA, R.drawable.payment_credit_card_button_selector);
        cardTypeMap.put(CarePayConstants.TYPE_FSA, R.drawable.payment_credit_card_button_selector);

        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                cardTypeMap.get(cardType), 0, R.drawable.check_box_intake, 0);

        radioButtonView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.radio_button_selector));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initilizeViews(View view) {
        paymentMethodRadioGroup = (RadioGroup) view.findViewById(R.id.paymentMethodsRadioGroup);
        paymentChoiceButton = (Button) view.findViewById(R.id.paymentChoiceButton);
        createPaymentPlanButton = (Button) view.findViewById(R.id.createPaymentPlanButton);
        paymentMethodRadioGroup.setOnCheckedChangeListener(this);
        paymentChoiceButton.setOnClickListener(paymentChoiceButtonListener);
        createPaymentPlanButton.setOnClickListener(createPaymentPlanButtonListener);
        paymentChoiceButton.setText(paymentChooseMethodString);
        createPaymentPlanButton.setText(paymentCreatePlanString);
        paymentChoiceButton.setEnabled(false);

        for (int i = 0; i < paymentList.size(); i++) {
          paymentMethodRadioGroup.addView(getPaymentMethodRadioButton(paymentList.get(i).getType(), paymentList.get(i).getLabel(), i),
                    radioGroupLayoutParam);

            View dividerLineView = new View(activity);
            dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1
            ));
            dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
            paymentMethodRadioGroup.addView(dividerLineView);
            onSetRadioButtonRegularTypeFace();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        paymentChoiceButton.setEnabled(true);
        onSetRadioButtonRegularTypeFace();
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);

        for (int i = 0; i < paymentList.size(); i++) {
            if (selectedRadioButton.getText().toString().equalsIgnoreCase(paymentList.get(i).getLabel())) {
                selectedPaymentMethod = selectedRadioButton.getText().toString();
                paymentChoiceButton.setText(paymentList.get(i).getButtonLabel());
                paymentChoiceButton.setTag(checkedId);
            }
        }
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < paymentMethodRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.activity,
                        (RadioButton) paymentMethodRadioGroup.getChildAt(i));
                ((RadioButton) paymentMethodRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.blue_cerulian));
    }

    private View.OnClickListener createPaymentPlanButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // ToDo : Create payment plan action here - to be done
        }
    };

    private View.OnClickListener paymentChoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getLabels();

          /*  if (paymentChoiceButton.getText().equals(getString(R.string.choose_credit_card))) {
                PatientChooseCreditCardFragment fragment = new PatientChooseCreditCardFragment();

                Gson gson = new Gson();
                Bundle arguments = getArguments();
                String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
                PaymentsModel paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);

                Bundle args = new Bundle();
                args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
                args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsModel);
                fragment.setArguments(args);

                ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
            }*/
            int position=(Integer)view.getTag();
            switch(position){
                case 0:
                    new LargeAlertDialog(getActivity(), dialogTitle, dialogText, new LargeAlertDialog.LargeAlertInterface(){
                        @Override
                        public void onActionButton() {
                        }
                    }).show();
                    break;
                case 1:
                    PatientChooseCreditCardFragment fragment = new PatientChooseCreditCardFragment();

                    Gson gson = new Gson();
                    Bundle arguments = getArguments();
                    String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
                    PaymentsModel paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);

                    Bundle args = new Bundle();
                    args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
                    args.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsModel);
                    fragment.setArguments(args);

                    ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                    break;

                default:
                    return;
            }
        }
    };

    /**
     *  partial payment labels
     */
    public void getLabels() {
        if (paymentsModel != null) {
            paymentsMetadataModel = paymentsModel.getPaymentsMetadata();
            if (paymentsMetadataModel != null) {
                paymentsLabelsDTO = paymentsMetadataModel.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    dialogTitle = paymentsLabelsDTO.getPaymentSeeFrontDeskButton();
                    dialogText= paymentsLabelsDTO.getPaymentBackButton();
                    titlePaymentMethodString = paymentsLabelsDTO.getPaymentMethodTitle();
                    paymentChooseMethodString = paymentsLabelsDTO.getPaymentChooseMethodButton();
                    paymentCreatePlanString =  paymentsLabelsDTO.getPaymentCreatePlanButton();
                }
            }
        }
    }
}
