package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.BaseCheckinFragment;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.utils.CardPattern;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.payeezysdk.sdk.payeezydirecttransactions.RequestTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientAddNewCreditCardFragment extends BaseCheckinFragment implements
        SimpleDatePickerDialog.OnDateSetListener , RequestTask.AuthorizeCreditCardCallback {

    private TextInputLayout nameOnCardTextInputLayout;
    private TextInputLayout creditCardNoTextInput;
    private TextInputLayout verificationCodeTextInput;
    private TextInputLayout address1TextInput;
    private TextInputLayout address2TextInput;
    private TextInputLayout zipCodeTextInput;
    private TextInputLayout cityTextInput;
    private TextInputLayout stateTextInput;

    private EditText nameOnCardEditText;
    private EditText creditCardNoEditText;
    private CarePayTextView cardTypeTextView;
    private EditText verificationCodeEditText;
    private TextView expirationDateTextView;
    private TextView pickDateTextView;
    private CheckBox saveCardOnFileCheckBox;
    private CheckBox setAsDefaultCheckBox;

    private LinearLayout billingAddressLayout;
    private CarePayTextView billingAddressTextView;
    private CheckBox useProfileAddressCheckBox;

    private EditText address1EditText;
    private EditText address2EditText;
    private EditText zipCodeEditText;
    private EditText cityEditText;
    private EditText stateEditText;

    private Button nextButton;

    private static final char SPACE_CHAR = ' ';
    private PaymentsModel paymentsModel;
    private PaymentsLabelDTO paymentsLabelDTO;
    private PaymentsModel intakePaymentModel;
    private double amountToMakePayment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View addNewCreditCardView = inflater.inflate(com.carecloud.carepaylibrary.R.layout.fragment_add_new_credit_card,
                container, false);
        paymentsLabelDTO = new PaymentsLabelDTO();
        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentDTOString = arguments.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentsModel = gson.fromJson(paymentDTOString, PaymentsModel.class);

            paymentDTOString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            intakePaymentModel = gson.fromJson(paymentDTOString, PaymentsModel.class);
            //paymentsModel = (PaymentsModel) arguments.getSerializable(CarePayConstants.INTAKE_BUNDLE);
            paymentsLabelDTO = intakePaymentModel.getPaymentsMetadata().getPaymentsLabel();
            amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
        }
        Toolbar toolbar = (Toolbar) addNewCreditCardView.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.respons_toolbar_title);
        title.setText(paymentsLabelDTO.getPaymentNewCreditCard());
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initilizeViews(addNewCreditCardView);
        setTypefaces();
        setTextWatchers();
        return addNewCreditCardView;
    }

    private void setTextWatchers() {

        creditCardNoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                validateCreditCardDetails();
            }

            @Override
            public void afterTextChanged(Editable str) {
                // Remove all spacing char
                int pos = 0;
                while (true) {
                    if (pos >= str.length()) {
                        break;
                    }
                    if (SPACE_CHAR == str.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == str.length())) {
                        str.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= str.length()) {
                        break;
                    }
                    final char c = str.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        str.insert(pos, "" + SPACE_CHAR);
                    }
                    pos += 5;
                }
                String type = getCreditCardType(getCardNumber());
                if (!StringUtil.isNullOrEmpty(getCardNumber()) && type != null) {
                    cardTypeTextView.setVisibility(View.VISIBLE);
                    cardTypeTextView.setText(type);
                } else {
                    cardTypeTextView.setVisibility(View.GONE);
                }
            }
        });


        verificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence str, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {
                validateCreditCardDetails();
            }

            @Override
            public void afterTextChanged(Editable str) {

            }
        });
    }

    private String getCreditCardType(String cardNumber) {
        String type;
        if (cardNumber.startsWith("4") || cardNumber.matches(CardPattern.VISA)) {
            type = "Visa";
        } else if (cardNumber.matches(CardPattern.MASTERCARD_SHORTER) || cardNumber.matches(CardPattern.MASTERCARD_SHORT) || cardNumber.matches(CardPattern.MASTERCARD)) {
            type = "Mastercard";
        } else if (cardNumber.matches(CardPattern.AMERICAN_EXPRESS)) {
            type = "American Express";
        } else if (cardNumber.matches(CardPattern.DISCOVER_SHORT) || cardNumber.matches(CardPattern.DISCOVER)) {
            type = "Discover";
        } else if (cardNumber.matches(CardPattern.JCB_SHORT) || cardNumber.matches(CardPattern.JCB)) {
            type = "JCB";
        } else if (cardNumber.matches(CardPattern.DINERS_CLUB_SHORT) || cardNumber.matches(CardPattern.DINERS_CLUB)) {
            type = "Diners Club";
        } else {
            type = null;
        }
        return type;
    }

    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    public boolean isValid() {
        if (getCardNumber().matches(CardPattern.VISA_VALID)) {
            return true;
        }
        if (getCardNumber().matches(CardPattern.MASTERCARD_VALID)) {
            return true;
        }
        if (getCardNumber().matches(CardPattern.AMERICAN_EXPRESS_VALID)) {
            return true;
        }
        if (getCardNumber().matches(CardPattern.DISCOVER_VALID)) {
            return true;
        }
        if (getCardNumber().matches(CardPattern.DINERS_CLUB_VALID)) {
            return true;
        }
        if (getCardNumber().matches(CardPattern.JCB_VALID)) {
            return true;
        }
        return false;
    }

    public String getCardNumber() {
        return creditCardNoEditText.getText().toString().replace(" ", "").trim();
    }

    private void initilizeViews(View view) {
        creditCardNoTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.creditCardNoTextInputLayout);
        creditCardNoTextInput.setTag(paymentsLabelDTO.getPaymentCreditCardNumber());
        creditCardNoEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.creditCardNoEditText);
        creditCardNoEditText.setHint(paymentsLabelDTO.getPaymentCreditCardNumber());
        creditCardNoEditText.setTag(creditCardNoTextInput);

        cardTypeTextView = (CarePayTextView) view.findViewById(R.id.cardTypeTextView);

        nameOnCardTextInputLayout = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.nameOnCardTextInputLayout);
        nameOnCardTextInputLayout.setTag(paymentsLabelDTO.getPaymentNameOnCardText());
        nameOnCardEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.nameOnCardEditText);
        nameOnCardEditText.setHint(paymentsLabelDTO.getPaymentNameOnCardText());
        nameOnCardEditText.setTag(nameOnCardTextInputLayout);

        verificationCodeTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.verificationCodeTextInputLayout);
        verificationCodeTextInput.setTag(paymentsLabelDTO.getPaymentVerificationNumber());
        verificationCodeEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.verificationCodeEditText);
        verificationCodeEditText.setHint(paymentsLabelDTO.getPaymentVerificationNumber());
        verificationCodeEditText.setTag(verificationCodeTextInput);

        expirationDateTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.expirationDateTextView);
        expirationDateTextView.setText(paymentsLabelDTO.getPaymentExpirationDate());
        pickDateTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.pickDateTextView);
        pickDateTextView.setText(paymentsLabelDTO.getPaymentPickDate());
        pickDateTextView.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = (CheckBox) view.findViewById(com.carecloud.carepaylibrary.R.id.saveCardOnFileCheckBox);
        saveCardOnFileCheckBox.setText(paymentsLabelDTO.getPaymentSaveCardOnFile());
        saveCardOnFileCheckBox.setOnCheckedChangeListener(saveCardOnFileCheckBoxListener);

        setAsDefaultCheckBox = (CheckBox) view.findViewById(com.carecloud.carepaylibrary.R.id.setAsDefaultCheckBox);
        setAsDefaultCheckBox.setText(paymentsLabelDTO.getPaymentSetAsDefaultCreditCard());

        billingAddressLayout = (LinearLayout) view.findViewById(R.id.billingInformationLayout);
        billingAddressTextView = (CarePayTextView) view.findViewById(R.id.billingAddressTextView);
        billingAddressTextView.setText(paymentsLabelDTO.getPaymentBillingAddressText());
        useProfileAddressCheckBox = (CheckBox) view.findViewById(R.id.useProfileAddressCheckBox);
        useProfileAddressCheckBox.setText(paymentsLabelDTO.getPaymentUseProfileAddress());
        useProfileAddressCheckBox.setOnCheckedChangeListener(useProfileAddressListener);

        address1TextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.address1TextInputLayout);
        address1TextInput.setTag(paymentsLabelDTO.getPaymentAddressLine1Text());
        address1EditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.addressEditTextId);
        address1EditText.setHint(paymentsLabelDTO.getPaymentAddressLine1Text());
        address1EditText.setTag(address1TextInput);

        address2TextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.address2TextInputLayout);
        address2TextInput.setTag(paymentsLabelDTO.getPaymentAddressLine2Text());
        address2EditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.addressEditText2Id);
        address2EditText.setHint(paymentsLabelDTO.getPaymentAddressLine2Text());
        address2EditText.setTag(address2TextInput);

        zipCodeTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.zipCodeTextInputLayout);
        zipCodeTextInput.setTag(paymentsLabelDTO.getPaymentZipcode());
        zipCodeEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.zipCodeId);
        zipCodeEditText.setHint(paymentsLabelDTO.getPaymentZipcode());
        zipCodeEditText.setTag(zipCodeTextInput);

        cityTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.cityTextInputLayout);
        cityTextInput.setTag(paymentsLabelDTO.getPaymentCity());
        cityEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.cityId);
        cityEditText.setHint(paymentsLabelDTO.getPaymentCity());
        cityEditText.setTag(cityTextInput);

        stateTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.stateTextInputLayout);
        stateTextInput.setTag(paymentsLabelDTO.getPaymentState());
        stateEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.stateAutoCompleteTextView);
        stateEditText.setHint(paymentsLabelDTO.getPaymentState());
        stateEditText.setTag(stateTextInput);

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.nextButton);
        nextButton.setText(paymentsLabelDTO.getPaymentNextButton());
        nextButton.setOnClickListener(nextButtonListener);

        setChangeFocusListeners();
        setActionListeners();


        creditCardNoEditText.clearFocus();
        nameOnCardEditText.clearFocus();
        verificationCodeEditText.clearFocus();

        saveCardOnFileCheckBox.setChecked(false);
        setAsDefaultCheckBox.setChecked(false);
        billingAddressLayout.setVisibility(View.GONE);
        useProfileAddressCheckBox.setChecked(true);
        setAddressFiledsEnabled(false);
        nextButton.setEnabled(false);
        nextButton.setClickable(false);
    }

    private void setTypefaces() {
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), creditCardNoEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), verificationCodeEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), nameOnCardEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), expirationDateTextView);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), address1EditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), address2EditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), zipCodeEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), cityEditText);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), stateEditText);


        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), creditCardNoTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), nameOnCardTextInputLayout);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), verificationCodeTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), address1TextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), address2TextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), zipCodeTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), cityTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), stateTextInput);

        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), nextButton);

        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), saveCardOnFileCheckBox);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), setAsDefaultCheckBox);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), useProfileAddressCheckBox);
    }

    private void setChangeFocusListeners() {
        creditCardNoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
                if (flag) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, flag);
            }
        });
        nameOnCardEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
                if (flag) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, flag);
            }
        });
        verificationCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean flag) {
                if (flag) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, flag);
            }
        });
    }

    private void setActionListeners() {
        creditCardNoEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_NEXT) {
                    verificationCodeEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        nameOnCardEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_NEXT) {
                    creditCardNoEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        verificationCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent keyEvent) {
                if (action == EditorInfo.IME_ACTION_DONE) {
                    verificationCodeEditText.clearFocus();
                    pickDateTextView.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener saveCardOnFileCheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                billingAddressLayout.setVisibility(View.VISIBLE);
                useProfileAddressCheckBox.setChecked(true);
                setAddressFiledsEnabled(false);
            } else {
                billingAddressLayout.setVisibility(View.GONE);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener useProfileAddressListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                setAddressFiledsEnabled(false);
            } else {
                setAddressFiledsEnabled(true);
            }
        }
    };

    private void setAddressFiledsEnabled(boolean isEnabled) {
        address1EditText.setEnabled(isEnabled);
        address1EditText.setClickable(isEnabled);
        address2EditText.setEnabled(isEnabled);
        address2EditText.setClickable(isEnabled);
        zipCodeEditText.setEnabled(isEnabled);
        zipCodeEditText.setClickable(isEnabled);
        cityEditText.setEnabled(isEnabled);
        cityEditText.setClickable(isEnabled);
        stateEditText.setEnabled(isEnabled);
        stateEditText.setClickable(isEnabled);
    }

    private View.OnClickListener pickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displaySimpleDatePickerDialogFragment();
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            authorizeCreditCard();
        }
    };

    private WorkflowServiceCallback addNewCreditCardCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Log.d("addNewCreditCardk","=========================>\nworkflowDTO="+workflowDTO.toString());
            makePaymentCall("");
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Log.d("makePaymentCallback","=========================>\nworkflowDTO="+workflowDTO.toString());
            //((BasePracticeActivity)getActivity()).finish();
            // ToDo : Call for the payment receipt here
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onDateSet(int year, int monthOfYear) {
        pickDateTextView.setText(DateUtil.getInstance().formatMonthYear(year, monthOfYear));
        validateCreditCardDetails();
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        DateUtil instance = DateUtil.getInstance();
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(instance.getYear(),
                instance.getMonth());
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }

    private boolean validateCreditCardDetails() {
        if (!isValid()) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }

        if (!(nameOnCardEditText.getText().toString().trim().length() > 0)) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }

        if (!(verificationCodeEditText.getText().toString().length() > 0)) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
        if (!pickDateTextView.getText().toString().equalsIgnoreCase(paymentsLabelDTO.getPaymentPickDate())) {
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            return true;
        } else {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
    }

    private void authorizeCreditCard()
    {
        String amount="1";
        String currency="USD";
        String paymentMethod="credit_card";
        String cvv=verificationCodeEditText.getText().toString();
        String expiryDate=pickDateTextView.getText().toString();
        expiryDate=expiryDate.substring(0,2)+expiryDate.substring(expiryDate.length()-2);
        String Name=nameOnCardEditText.getText().toString();
        String Type=getCreditCardType(creditCardNoEditText.getText().toString());
        String number=creditCardNoEditText.getText().toString().trim().replaceAll(" ","");
        String state=stateEditText.getText().toString();
        String addressline1=address1EditText.getText().toString();
        String zip=zipCodeEditText.getText().toString();
        String country="US";
        String city=cityEditText.getText().toString();

        try
        {
            RequestTask rTask = new RequestTask(getActivity(),PatientAddNewCreditCardFragment.this);
            rTask.execute("authorize",amount,currency,paymentMethod,cvv,expiryDate,Name,Type,number,state,addressline1,zip,country,city);
            System.out.println("first authorize call end");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println("authorize call end");
    }

    @Override
    public void onAuthorizeCreditCard(String resString) {

        if(resString!=null && resString.length()>800){
            int startIndex = resString.indexOf("value");
            startIndex=resString.indexOf("=",startIndex+1);
            int endIndex = resString.indexOf(",", startIndex);
            String token_value = resString.substring(startIndex, endIndex);
            token_value=token_value.replace(" ", "");
            token_value=token_value.replace(":", "");
            token_value=token_value.replace("=", "");
            token_value=token_value.replace("}", "");

            if(saveCardOnFileCheckBox.isChecked()){
                makePaymentCall(token_value);
            } else {
                addNewCreditCardCall(token_value);
            }

        } else {
            SystemUtil.showDialogMessage(getActivity(),paymentsLabelDTO.getPaymentFailedErrorMessage(),"Failed to authorize Credit Card");
        }
    }

    private void addNewCreditCardCall(String token_value){

        PaymentCreditCardsPayloadDTO creditCardsPayloadDTO = new PaymentCreditCardsPayloadDTO();
        PaymentsCreditCardBillingInformationDTO billingInformation = new PaymentsCreditCardBillingInformationDTO();
        creditCardsPayloadDTO.setCardNumber(getCardNumber());
        creditCardsPayloadDTO.setNameOnCard(nameOnCardEditText.getText().toString().trim());
        creditCardsPayloadDTO.setCvv(verificationCodeEditText.getText().toString().trim());
        creditCardsPayloadDTO.setExpireDt(pickDateTextView.getText().toString().trim());
        creditCardsPayloadDTO.setCardType(getCreditCardType(getCardNumber()));
        creditCardsPayloadDTO.setToken(token_value);
        if (saveCardOnFileCheckBox.isChecked() && useProfileAddressCheckBox.isChecked()) {
            billingInformation.setLine1(address1EditText.getText().toString().trim());
            billingInformation.setLine2(address2EditText.getText().toString().trim());
            billingInformation.setZip(zipCodeEditText.getText().toString().trim());
            billingInformation.setCity(cityEditText.getText().toString().trim());
            billingInformation.setState(stateEditText.getText().toString().trim());
            creditCardsPayloadDTO.setBillingInformation(billingInformation);
        }

        Gson gson = new Gson();
        String body = gson.toJson(creditCardsPayloadDTO);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
        queryMap.put("practice_mgmt", intakePaymentModel.getPaymentPayload().getPaymentSettings().getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", intakePaymentModel.getPaymentPayload().getPaymentSettings().getMetadata().getPracticeId());
        queryMap.put("patient_id", intakePaymentModel.getPaymentPayload().getPatientBalances().get(0).getMetadata().getPatientId());
        TransitionDTO transitionDTO = intakePaymentModel.getPaymentsMetadata().getPaymentsTransitions().getAddCreditCard();

        WorkflowServiceHelper.getInstance().execute(transitionDTO, addNewCreditCardCallback, body, queryMap, WorkflowServiceHelper.getPreferredLanguageHeader());
    }

    private void makePaymentCall(String token_value) {

        JSONObject payload = new JSONObject();
        try {
            payload.put("amount", amountToMakePayment);
            JSONObject paymentMethod = new JSONObject();
            paymentMethod.put("amount", amountToMakePayment);
            JSONObject creditCard = new JSONObject();
            creditCard.put("save", saveCardOnFileCheckBox.isChecked());
            //creditCard.put("credit_card_id", creditCardPayload.getCreditCardsId());
            creditCard.put("card_type", getCreditCardType(getCardNumber()));
            creditCard.put("card_number", getCardNumber());
            creditCard.put("name_on_card", nameOnCardEditText.getText().toString().trim());
            creditCard.put("expire_dt", pickDateTextView.getText().toString().trim());
            creditCard.put("cvv", verificationCodeEditText.getText().toString().trim());
            creditCard.put("papi_pay", true);
            JSONObject billingInformation = new JSONObject();
            billingInformation.put("same_as_patient", useProfileAddressCheckBox.isChecked());
            creditCard.put("billing_information", billingInformation);
            paymentMethod.put("credit_card", creditCard);
            paymentMethod.put("type", "credit_card");
            JSONArray paymentMethods = new JSONArray();
            paymentMethods.put(paymentMethod);
            payload.put("payment_methods", paymentMethods);
            PaymentPayloadMetaDataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getMetadata();
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