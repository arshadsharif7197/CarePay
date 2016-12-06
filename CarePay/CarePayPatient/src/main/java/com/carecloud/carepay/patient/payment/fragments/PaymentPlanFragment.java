package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PaymentPlanFragment extends Fragment {

    private static final String LOG_TAG = PaymentPlanFragment.class.getSimpleName();
    private PaymentsLabelDTO paymentsLabel;

    private Button createPlanButton;
    private TextView addedCreditCard;
    private TextView addChangeCreditCard;
    private EditText paymentPlanName;
    private EditText paymentPlanMonthNo;
    private EditText paymentPlanMonthlyPayment;
    private TextInputLayout paymentPlanNameInput;
    private TextInputLayout paymentPlanMonthInput;
    private TextInputLayout paymentPlanMonthlyInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_plan, container, false);

        paymentPlanName = (EditText) view.findViewById(R.id.payment_plan_name_edit);
        paymentPlanMonthNo = (EditText) view.findViewById(R.id.payment_plan_month_no_edit);
        paymentPlanMonthlyPayment = (EditText) view.findViewById(R.id.payment_plan_monthly_payment_edit);
        createPlanButton = (Button) view.findViewById(R.id.create_payment_plan_button);

        addedCreditCard = (TextView) view.findViewById(R.id.payment_plan_credit_card);
        addChangeCreditCard = (TextView) view.findViewById(R.id.payment_plan_credit_card_button);
        addChangeCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                //Goto Credit card screen to add
                addedCreditCard.setText("VISA **** 1234");//Temporary added
                addedCreditCard.setVisibility(View.VISIBLE);
                addChangeCreditCard.setTextColor(getActivity().getResources().getColor(R.color.charcoal));
                addChangeCreditCard.setText(paymentsLabel.getPaymentChangeCard());
            }
        });

        Bundle arguments = getArguments();
        PaymentsModel paymentsModel = (PaymentsModel) arguments
                .getSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);

        if (paymentsModel != null) {
            paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            setLabels(view);
            setEditTexts(view);
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(paymentsLabel.getPaymentPlanHeading());

        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(),
                R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Spinner monthDays = (Spinner) view.findViewById(R.id.payment_plan_month_day);
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < 31; ) {
            items.add("" + ++i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthDays.setAdapter(adapter);
        monthDays.setOnItemSelectedListener(itemSelectedListener);

        String copayStr = "";
        String previousBalanceStr = "";

        if (paymentsModel != null) {
            List<PaymentPatientBalancesPayloadDTO> paymentList = paymentsModel.getPaymentPayload().getPatientBalances().get(1).getPayload();
            if (paymentList != null && paymentList.size() > 1) {
                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PATIENT)) {
                        previousBalanceStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        copayStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    double total = copay + previousBalance;

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                    ((TextView) view.findViewById(R.id.payment_plan_total)).setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                    ((TextView) view.findViewById(R.id.payment_plan_prev_balance)).setText(CarePayConstants.DOLLAR.concat(previousBalanceStr));
                    ((TextView) view.findViewById(R.id.payment_plan_copay)).setText(CarePayConstants.DOLLAR.concat(copayStr));
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }

        return view;
    }

    private void setEditTexts(View view) {
        paymentPlanNameInput = (TextInputLayout) view.findViewById(R.id.payment_plan_name_input);
        String nameLabel = paymentsLabel.getPaymentPlanName();
        paymentPlanNameInput.setTag(nameLabel);
        paymentPlanName.setHint(nameLabel);
        paymentPlanName.setTag(paymentPlanNameInput);

        paymentPlanMonthInput = (TextInputLayout) view.findViewById(R.id.payment_plan_month_no_input);
        String monthNumber = paymentsLabel.getPaymentNumberOfMonths();
        paymentPlanMonthInput.setTag(monthNumber);
        paymentPlanMonthNo.setHint(monthNumber);
        paymentPlanMonthNo.setTag(paymentPlanMonthInput);

        paymentPlanMonthlyInput = (TextInputLayout) view.findViewById(R.id.payment_plan_monthly_payment_input);
        String monthlyPayment = paymentsLabel.getPaymentMonthlyPayment();
        paymentPlanMonthlyInput.setTag(monthlyPayment);
        paymentPlanMonthlyPayment.setHint(monthlyPayment);
        paymentPlanMonthlyPayment.setTag(paymentPlanMonthlyInput);

        setChangeFocusListeners();

        paymentPlanName.clearFocus();
        paymentPlanMonthNo.clearFocus();
        paymentPlanMonthlyPayment.clearFocus();
    }

    private void setLabels(View view) {
        ((TextView) view.findViewById(R.id.payment_plan_optional_hint))
                .setText(paymentsLabel.getPaymentOptionalHint());
        ((TextView) view.findViewById(R.id.payment_plan_total_label))
                .setText(paymentsLabel.getPaymentLetsEstablishPaymentPlan());
        ((TextView) view.findViewById(R.id.payment_plan_month_day_label))
                .setText(paymentsLabel.getPaymentDayOfTheMonth());
        ((TextView) view.findViewById(R.id.payment_plan_prev_balance_label))
                .setText(paymentsLabel.getPaymentPreviousBalance());
        ((TextView) view.findViewById(R.id.payment_plan_copay_label))
                .setText(paymentsLabel.getPaymentInsuranceCopay());

        createPlanButton.setText(paymentsLabel.getPaymentCreatePlan());
        addChangeCreditCard.setText(paymentsLabel.getPaymentAddCardButton());
    }

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = (String) parent.getItemAtPosition(position);
            String suffix;
            switch (item) {
                case "1":
                case "21":
                case "31":
                    suffix = "st";
                    break;
                case "2":
                case "22":
                    suffix = "nd";
                    break;
                case "3":
                case "23":
                    suffix = "rd";
                    break;
                default:
                    suffix = "th";
                    break;
            }

            TextView selectedItem = (TextView) parent.getChildAt(0);
            selectedItem.setText(item.concat(suffix));
            selectedItem.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), selectedItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void setChangeFocusListeners() {
        paymentPlanName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        paymentPlanMonthNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        paymentPlanMonthlyPayment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }
}
