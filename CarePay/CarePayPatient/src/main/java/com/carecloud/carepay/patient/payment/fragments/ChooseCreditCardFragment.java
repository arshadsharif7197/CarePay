package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.carecloud.carepay.patient.demographics.activities.DemographicsSettingsActivity;
import com.carecloud.carepay.patient.payment.PaymentActivity;
import com.carecloud.carepay.patient.payment.activities.ViewPaymentBalanceHistoryActivity;
import com.carecloud.carepay.patient.payment.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseCreditCardFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup chooseCreditCardRadioGroup;
    private Button nextButton;
    private Activity activity;
    private RadioGroup.LayoutParams radioGroupLayoutParam;

    private int selectedCreditCard;
    private PaymentsModel paymentsModel;
    private PaymentsModel intakePaymentModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String titleLabel = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            titleLabel = arguments.getString(CarePayConstants.PAYMENT_METHOD_BUNDLE);
            Gson gson = new Gson();
            String paymentsDTOString = arguments.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentsModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);

            paymentsDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            intakePaymentModel = gson.fromJson(paymentsDTOString, PaymentsModel.class);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_credit_card, container, false);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(titleLabel);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");

        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        radioGroupLayoutParam = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin);
        radioGroupLayoutParam.setMargins(margin, margin, margin, margin);

        initializeViews(view);
        return view;
    }

    private RadioButton getCreditCardRadioButton(String cardInfo, int index) {
        RadioButton radioButtonView = new RadioButton(activity);
        radioButtonView.setId(index);
        radioButtonView.setButtonDrawable(null);
        radioButtonView.setBackground(null);
        radioButtonView.setText(cardInfo);
        radioButtonView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.payment_credit_card_button_selector, 0, R.drawable.check_box_intake, 0);
        radioButtonView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        radioButtonView.setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
        radioButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.payment_method_layout_label_text_size));
        radioButtonView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.payment_method_layout_checkbox_margin));

        return radioButtonView;
    }

    private void initializeViews(View view) {
        chooseCreditCardRadioGroup = (RadioGroup) view.findViewById(R.id.chooseCreditCardRadioGroup);
        chooseCreditCardRadioGroup.setOnCheckedChangeListener(this);

        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setVisibility(View.INVISIBLE);

        Button addNewCardButton = (Button) view.findViewById(R.id.addNewCardButton);
        addNewCardButton.setOnClickListener(addNewCardButtonListener);

        if (paymentsModel != null) {
            List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = paymentsModel.getPaymentPayload().getPatientCreditCards();

            for (int i = 0; i < creditCardList.size(); i++) {
                PaymentCreditCardsPayloadDTO creditCardItem = creditCardList.get(i).getPayload();
                String creditCardName = creditCardItem.getCardType();
                chooseCreditCardRadioGroup.addView(getCreditCardRadioButton(
                        StringUtil.getEncodedCardNumber(creditCardName,
                                creditCardItem.getCardNumber()), i), radioGroupLayoutParam);

                View dividerLineView = new View(activity);
                dividerLineView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                ));

                dividerLineView.setBackgroundColor(ContextCompat.getColor(activity, R.color.cadet_gray));
                chooseCreditCardRadioGroup.addView(dividerLineView);
                onSetRadioButtonRegularTypeFace();
            }


            PaymentsLabelDTO paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            nextButton.setText(paymentsLabel.getPaymentPayText());
            addNewCardButton.setText(paymentsLabel.getPaymentAddNewCreditCardButton());
        }
    }

//    private String getCreditCardName(String cardType) {
//        if (paymentsModel != null) {
//            List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType
//                    = paymentsModel.getPaymentPayload().getPaymentSettings().getPayload().getCreditCardType();
//
//            if (creditCardType != null && creditCardType.size() > 0) {
//                for (int i = 0; i < creditCardType.size(); i++) {
//                    PaymentsSettingsPayloadCreditCardTypesDTO creditCardTypesDTO = creditCardType.get(i);
//                    if (creditCardTypesDTO.getType().equalsIgnoreCase(cardType)) {
//                        return creditCardTypesDTO.getLabel();
//                    }
//                }
//            }
//        }
//        return cardType;
//    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        nextButton.setVisibility(View.VISIBLE);
        onSetRadioButtonRegularTypeFace();

        selectedCreditCard = checkedId;
        RadioButton selectedRadioButton = (RadioButton) group.findViewById(checkedId);
        onSetRadioButtonSemiBoldTypeFace(selectedRadioButton);
    }

    private void onSetRadioButtonRegularTypeFace() {
        for (int i = 0; i < chooseCreditCardRadioGroup.getChildCount(); i++) {
            if (i % 2 == 0) {
                SystemUtil.setProximaNovaRegularTypeface(this.activity, (RadioButton)
                        chooseCreditCardRadioGroup.getChildAt(i));
                ((RadioButton) chooseCreditCardRadioGroup.getChildAt(i))
                        .setTextColor(ContextCompat.getColor(activity, R.color.slateGray));
            }
        }
    }

    private void onSetRadioButtonSemiBoldTypeFace(RadioButton radioButton) {
        SystemUtil.setProximaNovaSemiboldTypeface(this.activity, radioButton);
        radioButton.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            List<PaymentsPatientsCreditCardsPayloadListDTO> creditCardList = paymentsModel.getPaymentPayload().getPatientCreditCards();

            if (creditCardList != null) {
                PaymentCreditCardsPayloadDTO creditCardPayload = creditCardList.get(selectedCreditCard).getPayload();

                JSONObject payload = new JSONObject();
                double totalAmountToPay = getArguments().getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);

                try {
                    payload.put("amount", totalAmountToPay);

                    JSONObject paymentMethod = new JSONObject();
                    paymentMethod.put("amount", totalAmountToPay);

                    JSONObject creditCard = new JSONObject();
                    creditCard.put("save", false);
                    creditCard.put("credit_card_id", creditCardPayload.getCreditCardsId());
                    creditCard.put("card_type", creditCardPayload.getCardType());
                    creditCard.put("card_number", creditCardPayload.getCardNumber());
                    creditCard.put("name_on_card", creditCardPayload.getNameOnCard());
                    creditCard.put("expire_dt", creditCardPayload.getExpireDt());
                    creditCard.put("cvv", creditCardPayload.getCvv());
                    creditCard.put("papi_pay", true);

                    JSONObject billingInformation = new JSONObject();
                    billingInformation.put("same_as_patient", true);
                    creditCard.put("billing_information", billingInformation);

                    paymentMethod.put("credit_card", creditCard);
                    paymentMethod.put("type", "credit_card");

                    JSONArray paymentMethods = new JSONArray();
                    paymentMethods.put(paymentMethod);
                    payload.put("payment_methods", paymentMethods);

                    PaymentPayloadMetaDataDTO metadata = intakePaymentModel.getPaymentPayload()
                            .getPatientBalances().get(0).getBalances().get(0).getMetadata();

                    Map<String, String> queries = new HashMap<>();
                    queries.put("practice_mgmt", metadata.getPracticeMgmt());
                    queries.put("practice_id", metadata.getPracticeId());
                    queries.put("patient_id", metadata.getPatientId());

                    Map<String, String> header = new HashMap<>();
                    header.put("transition", "true");

                    TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
                    WorkflowServiceHelper.getInstance().execute(transitionDTO, makePaymentCallback, payload.toString(), queries, header);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            Gson gson = new Gson();
            PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(getActivity(),
                    gson.fromJson(workflowDTO.toString(), PaymentsModel.class));
            receiptDialog.show();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            System.out.print(exceptionMessage);
        }
    };

    private View.OnClickListener addNewCardButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
            AddNewCreditCardFragment fragment = (AddNewCreditCardFragment)
                    fragmentmanager.findFragmentByTag(AddNewCreditCardFragment.class.getSimpleName());
            if (fragment == null) {
                fragment = new AddNewCreditCardFragment();
            }

            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            String paymentsDTOString = gson.toJson(paymentsModel);
            bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
            bundle.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(intakePaymentModel));
            bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, getArguments().getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE));
            fragment.setArguments(bundle);

            if (getActivity() instanceof PaymentActivity) {
                ((PaymentActivity) getActivity()).navigateToFragment(fragment, true);
            } else if (getActivity() instanceof DemographicsSettingsActivity) {
                ((DemographicsSettingsActivity) getActivity()).navigateToFragment(fragment, true);
            } else if (getActivity() instanceof ViewPaymentBalanceHistoryActivity) {
                ((ViewPaymentBalanceHistoryActivity) getActivity()).navigateToFragment(fragment, true);
            }
        }
    };
}
