package com.carecloud.carepaylibray.payments.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPapiAccountsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPapiMetadataMerchantServiceDTO;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.TokenizationService;
import com.carecloud.carepaylibray.payments.utils.CardPattern;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.payeezysdk.sdk.payeezydirecttransactions.RequestTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartystreets.api.us_zipcode.City;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseAddCreditCardFragment extends BaseDialogFragment implements RequestTask.AuthorizeCreditCardCallback, SimpleDatePickerDialog.OnDateSetListener {

    public interface IAuthoriseCreditCardResponse {
        void onAuthorizeCreditCardSuccess();

        void onAuthorizeCreditCardFailed();
    }

    protected TextInputLayout nameOnCardTextInputLayout;
    protected TextInputLayout creditCardNoTextInput;
    protected TextInputLayout verificationCodeTextInput;
    protected TextInputLayout address1TextInput;
    protected TextInputLayout address2TextInput;
    protected TextInputLayout zipCodeTextInput;
    protected TextInputLayout cityTextInput;
    protected TextInputLayout stateTextInput;

    protected EditText nameOnCardEditText;
    protected EditText creditCardNoEditText;
    protected CarePayTextView cardTypeTextView;
    protected CarePayTextView billingAddressTextView;
    protected EditText verificationCodeEditText;
    protected TextView expirationDateTextView;
    protected TextView pickDateTextView;
    protected TextView title;
    protected CheckBox saveCardOnFileCheckBox;
    protected CheckBox setAsDefaultCheckBox;
    protected CheckBox useProfileAddressCheckBox;

    protected EditText address1EditText;
    protected EditText address2EditText;
    protected EditText zipCodeEditText;
    protected EditText cityEditText;
    protected AutoCompleteTextView stateAutoCompleteTextView;
    protected Button nextButton;

    private static final char SPACE_CHAR = ' ';
    private String stateAbbr = null;
    private City smartyStreetsResponse;
    protected double amountToMakePayment;
    protected DemographicAddressPayloadDTO addressPayloadDTO;
    private List<DemographicsSettingsPapiAccountsDTO> papiAccountsDTO;
    protected PaymentCreditCardsPayloadDTO creditCardsPayloadDTO;
    protected PaymentsCreditCardBillingInformationDTO billingInformationDTO;
    protected IAuthoriseCreditCardResponse authoriseCreditCardResponseCallback;

    protected PaymentNavigationCallback callback;
    protected PaymentsModel paymentsModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View addNewCreditCardView = inflater.inflate(com.carecloud.carepaylibrary.R.layout.fragment_add_new_credit_card,
                container, false);
        try {
            Bundle arguments = getArguments();
            if (arguments != null) {
                Gson gson = new Gson();
                String payloadString;
                if(addressPayloadDTO==null) {
                    payloadString = getApplicationPreferences().readStringFromSharedPref(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE);
                    addressPayloadDTO = new DemographicAddressPayloadDTO();
                    if (payloadString.length() > 1) {
                        addressPayloadDTO = gson.fromJson(payloadString, DemographicAddressPayloadDTO.class);
                    }
                }
                if (arguments.containsKey(CarePayConstants.PAYEEZY_MERCHANT_SERVICE_BUNDLE)){
                    payloadString = arguments.getString(CarePayConstants.PAYEEZY_MERCHANT_SERVICE_BUNDLE);
                    papiAccountsDTO = gson.fromJson(payloadString, new TypeToken<List<DemographicsSettingsPapiAccountsDTO>>(){}.getType());
                }else if(arguments.containsKey(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE)){
                    payloadString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
                    PaymentsModel paymentsModel = gson.fromJson(payloadString, PaymentsModel.class);
                    papiAccountsDTO = paymentsModel.getPaymentPayload().getPapiAccounts();
                }
                if (arguments.containsKey(CarePayConstants.PAYMENT_AMOUNT_BUNDLE)) {
                    amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupTitleViews(addNewCreditCardView);
        initializeViews(addNewCreditCardView);
//        setTypefaces();
        setTextWatchers();

        hideKeyboardOnViewTouch(addNewCreditCardView);
        return addNewCreditCardView;
    }

    protected void setChildFragment(IAuthoriseCreditCardResponse callback) {
        this.authoriseCreditCardResponseCallback = callback;
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


        verificationCodeEditText.addTextChangedListener(textWatcher);
        address1EditText.addTextChangedListener(textWatcher);
        zipCodeEditText.addTextChangedListener(textWatcher);
        cityEditText.addTextChangedListener(textWatcher);
        stateAutoCompleteTextView.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            validateCreditCardDetails();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    protected String getCreditCardType(String cardNumber) {
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

    protected String getLastFour() {
        String fullCard = creditCardNoEditText.getText().toString().replace(" ", "").trim();
        return fullCard.substring(fullCard.length() - 4, fullCard.length());
    }

    private void setupTitleViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        if(toolbar!=null) {
            title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.respons_toolbar_title);
            toolbar.setTitle("");
            if(getDialog()==null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), com.carecloud.carepaylibrary.R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });
            }else{
                View close = view.findViewById(R.id.closeViewLayout);
                if(close!=null){
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            if(callback!=null){
                                callback.onPayButtonClicked(amountToMakePayment, paymentsModel);
                            }
                        }
                    });
                }
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }

        }

    }

    private void initializeViews(View view) {
        creditCardNoTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.creditCardNoTextInputLayout);
        creditCardNoEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.creditCardNoEditText);

        cardTypeTextView = (CarePayTextView) view.findViewById(R.id.cardTypeTextView);

        nameOnCardTextInputLayout = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.nameOnCardTextInputLayout);
        nameOnCardEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.nameOnCardEditText);

        verificationCodeTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.verificationCodeTextInputLayout);
        verificationCodeEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.verificationCodeEditText);

        expirationDateTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.expirationDateTextView);
        pickDateTextView = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.pickDateTextView);
        pickDateTextView.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = (CheckBox) view.findViewById(com.carecloud.carepaylibrary.R.id.saveCardOnFileCheckBox);
        saveCardOnFileCheckBox.setChecked(false);
        saveCardOnFileCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCardAsDefaultLogic((CheckBox) view);
            }
        });

        setAsDefaultCheckBox = (CheckBox) view.findViewById(com.carecloud.carepaylibrary.R.id.setAsDefaultCheckBox);
        setAsDefaultCheckBox.setEnabled(false);
        setAsDefaultCheckBox.setChecked(false);

        billingAddressTextView = (CarePayTextView) view.findViewById(R.id.billingAddressTextView);
        useProfileAddressCheckBox = (CheckBox) view.findViewById(R.id.useProfileAddressCheckBox);
        useProfileAddressCheckBox.setOnCheckedChangeListener(useProfileAddressListener);

        address1TextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.address1TextInputLayout);
        address1EditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.addressEditTextId);

        address2TextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.address2TextInputLayout);
        address2EditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.addressEditText2Id);

        zipCodeTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.zipCodeTextInputLayout);
        zipCodeEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.zipCodeId);

        cityTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(com.carecloud.carepaylibrary.R.id.cityId);

        stateTextInput = (TextInputLayout) view.findViewById(com.carecloud.carepaylibrary.R.id.stateTextInputLayout);
        stateAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.addNewCredidCardStateAutoCompleteTextView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.autocomplete_state_item,
                R.id.text1,
                AddressUtil.states);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateAbbr = adapter.getItem(position);
            }
        });

        nextButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.nextButton);
        nextButton.setOnClickListener(nextButtonListener);

        setChangeFocusListeners();
        setActionListeners();

        saveCardOnFileCheckBox.setChecked(false);
        setAsDefaultCheckBox.setChecked(false);

        useProfileAddressCheckBox.setChecked(true);
        nextButton.setEnabled(false);
        nextButton.setClickable(false);

        useProfileAddressCheckBox.setChecked(true);
        setAddressFieldsEnabled(false);
        setDefaultBillingAddressTexts();

    }


    /**
     * SHMRK-1843
     * 1. 'Set as default' checkbox should be enabled onle when 'Save card on file' check box is enabled.
     * 2. If both 'Set as default' and 'Save card on file' checkboxes were checked, then
     * un-checking 'Save card on file' checkbox should also un-check 'Set as default' checkbox.
     */
    private void setCardAsDefaultLogic(CheckBox saveCardOnFileCheckBox) {
        setAsDefaultCheckBox.setEnabled(saveCardOnFileCheckBox.isChecked());

        if (!saveCardOnFileCheckBox.isChecked()) {
            setAsDefaultCheckBox.setChecked(false);
        }
    }

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            nextButton.setEnabled(false);
            setDTOs();
            authorizeCreditCard();
        }
    };

    private void setDTOs() {
        creditCardsPayloadDTO = new PaymentCreditCardsPayloadDTO();
        billingInformationDTO = new PaymentsCreditCardBillingInformationDTO();
        billingInformationDTO.setSameAsPatient(useProfileAddressCheckBox.isChecked());
        creditCardsPayloadDTO.setCardNumber(getLastFour());
        creditCardsPayloadDTO.setNameOnCard(nameOnCardEditText.getText().toString().trim());
        creditCardsPayloadDTO.setCvv(verificationCodeEditText.getText().toString().trim());
        String expiryDate = pickDateTextView.getText().toString();
        expiryDate = expiryDate.substring(0, 2) + expiryDate.substring(expiryDate.length() - 2);
        creditCardsPayloadDTO.setExpireDt(expiryDate);
        creditCardsPayloadDTO.setCardType(getCreditCardType(getCardNumber()));
        billingInformationDTO.setLine1(address1EditText.getText().toString().trim());
        billingInformationDTO.setLine2(address2EditText.getText().toString().trim());
        billingInformationDTO.setZip(zipCodeEditText.getText().toString().trim());
        billingInformationDTO.setCity(cityEditText.getText().toString().trim());
        billingInformationDTO.setState(stateAutoCompleteTextView.getText().toString().trim());
        creditCardsPayloadDTO.setBillingInformation(billingInformationDTO);
        creditCardsPayloadDTO.setTokenizationService(TokenizationService.payeezy);
    }

    private void authorizeCreditCard() {
//        String amount = String.valueOf((int) amountToMakePayment>0?amountToMakePayment:1);//this is to create an authorization
        String amount = String.valueOf(1);//this is to create an authorization
        String currency = "USD";
        String paymentMethod = "credit_card";
        String cvv = creditCardsPayloadDTO.getCvv();
        String expiryDate = creditCardsPayloadDTO.getExpireDt();
        String name = creditCardsPayloadDTO.getNameOnCard();
        String type = creditCardsPayloadDTO.getCardType();
        String number = getCardNumber();
        String state = billingInformationDTO.getState();
        String addressline1 = billingInformationDTO.getLine1();
        String zip = billingInformationDTO.getZip();
        String country = "US";
        String city = billingInformationDTO.getCity();

        try {
            DemographicsSettingsPapiMetadataMerchantServiceDTO merchantServiceDTO = null;
            for (DemographicsSettingsPapiAccountsDTO papiAccountDTO : papiAccountsDTO) {
                if (papiAccountDTO.getType().contains("payeezy")) {
                    merchantServiceDTO = papiAccountDTO.getMetadata().getMerchantService();
                    break;
                }
            }
            RequestTask requestTask = new RequestTask(getActivity(), BaseAddCreditCardFragment.this, merchantServiceDTO);
            requestTask.execute("authorize", amount, currency, paymentMethod, cvv, expiryDate, name, type, number, state, addressline1, zip, country, city);
            System.out.println("first authorize call end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("authorize call end");
    }

    private void setDefaultBillingAddressTexts() {
        if(addressPayloadDTO!=null) {
            address1EditText.setText(addressPayloadDTO.getAddress1());
            address1EditText.getOnFocusChangeListener().onFocusChange(address1EditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress1()));

            address2EditText.setText(addressPayloadDTO.getAddress2());
            address2EditText.getOnFocusChangeListener().onFocusChange(address2EditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress2()));

            zipCodeEditText.setText(addressPayloadDTO.getZipcode());
            zipCodeEditText.getOnFocusChangeListener().onFocusChange(zipCodeEditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getZipcode()));

            cityEditText.setText(addressPayloadDTO.getCity());
            cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getCity()));

            stateAutoCompleteTextView.setText(addressPayloadDTO.getState());
            stateAutoCompleteTextView.getOnFocusChangeListener().onFocusChange(stateAutoCompleteTextView, !StringUtil.isNullOrEmpty(addressPayloadDTO.getState()));
        }
    }


    private void setChangeFocusListeners() {
        creditCardNoEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(creditCardNoTextInput, null));
        nameOnCardEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(nameOnCardTextInputLayout, null));
        verificationCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(verificationCodeTextInput, null));
        address1EditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address1TextInput, null));
        address2EditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(address2TextInput, null));
        zipCodeEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(zipCodeTextInput, new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) { // for SmartyStreets
                    getCityAndState(zipCodeEditText.getText().toString());
                }
            }
        }));
        cityEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityTextInput, null));
        stateAutoCompleteTextView.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(stateTextInput, null));
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

    private CompoundButton.OnCheckedChangeListener useProfileAddressListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            validateCreditCardDetails();
            if (isChecked) {
                setAddressFieldsEnabled(false);
                setDefaultBillingAddressTexts();
            } else {
                setAddressFieldsEnabled(true);
                address1EditText.setText(null);
                address2EditText.setText(null);
                zipCodeEditText.setText(null);
                cityEditText.setText(null);
                stateAutoCompleteTextView.setText(null);
            }
        }
    };

    private void setAddressFieldsEnabled(boolean isEnabled) {
        address1EditText.setFocusable(isEnabled);
        address2EditText.setFocusable(isEnabled);
        zipCodeEditText.setFocusable(isEnabled);
        cityEditText.setFocusable(isEnabled);
        stateAutoCompleteTextView.setFocusable(isEnabled);
        stateAutoCompleteTextView.setClickable(isEnabled);
    }

    private View.OnClickListener pickDateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displaySimpleDatePickerDialogFragment();
        }
    };

    @Override
    public void onDateSet(int year, int monthOfYear) {
        pickDateTextView.setText(DateUtil.getInstance().formatMonthYear(year, monthOfYear));
        validateCreditCardDetails();
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        if (pickDateTextView.getText().toString().contains("/")) {
            String[] selectedDate = pickDateTextView.getText().toString().split("/");
            int month = Integer.parseInt(selectedDate[0]);
            int year = Integer.parseInt(selectedDate[1]);
            datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(year, month - 1);
        } else {
            DateUtil instance = DateUtil.getInstance();
            datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(instance.getYear(),
                    instance.getMonth());
        }
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

        if (!(verificationCodeEditText.getText().toString().length() > 2)) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }

        if (!useProfileAddressCheckBox.isChecked() &&
                (!(address1EditText.getText().toString().trim().length() > 0) ||
                        !(zipCodeEditText.getText().toString().trim().length() > 0) ||
                        !(cityEditText.getText().toString().trim().length() > 0) ||
                        !(stateAutoCompleteTextView.getText().toString().trim().length() > 0))) {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }

        if (pickDateTextView.getText().toString().contains("/")) {
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            return true;
        } else {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
    }

    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(String zipcode) {

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                smartyStreetsResponse = AddressUtil.getCityAndStateByZipCode(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (smartyStreetsResponse != null) {
                    cityEditText.setText(smartyStreetsResponse.getCity());

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateAutoCompleteTextView.setText(stateAbbr);
                }
            }
        }.execute(zipcode);
    }

    @Override
    public void onAuthorizeCreditCard(String resString) {

        if (resString != null && resString.length() > 800) {
            int startIndex = resString.indexOf("value");
            startIndex = resString.indexOf("=", startIndex + 1);
            int endIndex = resString.indexOf(",", startIndex);
            String tokenValue = resString.substring(startIndex, endIndex);
            tokenValue = tokenValue.replace(" ", "");
            tokenValue = tokenValue.replace(":", "");
            tokenValue = tokenValue.replace("=", "");
            tokenValue = tokenValue.replace("}", "");
            creditCardsPayloadDTO.setToken(tokenValue);

            authoriseCreditCardResponseCallback.onAuthorizeCreditCardSuccess();
        } else {
            nextButton.setEnabled(true);
            authoriseCreditCardResponseCallback.onAuthorizeCreditCardFailed();
        }
    }


}