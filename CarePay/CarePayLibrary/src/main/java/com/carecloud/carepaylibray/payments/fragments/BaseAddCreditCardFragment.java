package com.carecloud.carepaylibray.payments.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPapiAccountsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.TokenizationService;
import com.carecloud.carepaylibray.payments.utils.CardPattern;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.PayeezyRequestTask;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.payeezy.sdk.payeezytokenised.TransactionDataProvider;
import com.smartystreets.api.us_zipcode.City;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseAddCreditCardFragment extends BasePaymentDialogFragment implements PayeezyRequestTask.AuthorizeCreditCardCallback, SimpleDatePickerDialog.OnDateSetListener {

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
    protected List<MerchantServicesDTO> merchantServicesList;

    protected PaymentConfirmationInterface callback;
    protected PaymentsModel paymentsModel;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle arguments = getArguments();
        Gson gson = new Gson();
        String payloadString;
        if (arguments != null) {
            if (arguments.containsKey(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE)) {
                payloadString = arguments.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
                PaymentsModel paymentsModel = gson.fromJson(payloadString, PaymentsModel.class);
                papiAccountsDTO = paymentsModel.getPaymentPayload().getPapiAccounts();
                merchantServicesList = paymentsModel.getPaymentPayload().getMerchantServices();
            }
            if (arguments.containsKey(CarePayConstants.PAYMENT_AMOUNT_BUNDLE)) {
                amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            }
        }
        if (addressPayloadDTO == null) {
            payloadString = getApplicationPreferences().readStringFromSharedPref(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE);
            addressPayloadDTO = new DemographicAddressPayloadDTO();
            if (payloadString.length() > 1) {
                addressPayloadDTO = gson.fromJson(payloadString, DemographicAddressPayloadDTO.class);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View addNewCreditCardView = inflater.inflate(com.carecloud.carepaylibrary.R.layout.fragment_add_new_credit_card,
                container, false);

        setupTitleViews(addNewCreditCardView);
        initializeViews(addNewCreditCardView);

        setTextWatchers();

        hideKeyboardOnViewTouch(addNewCreditCardView);
        return addNewCreditCardView;
    }

    protected void setAuthorizeCallback(IAuthoriseCreditCardResponse callback) {
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

        nameOnCardEditText.addTextChangedListener(textWatcher);
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

    private void setupTitleViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(R.drawable.icn_nav_back);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();
                    }
                });
            } else {
                View close = view.findViewById(R.id.closeViewLayout);
                if (close != null) {
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                            if (callback != null) {
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
        creditCardNoTextInput = (TextInputLayout) view.findViewById(R.id.creditCardNoTextInputLayout);
        creditCardNoEditText = (EditText) view.findViewById(R.id.creditCardNoEditText);

        cardTypeTextView = (CarePayTextView) view.findViewById(R.id.cardTypeTextView);

        nameOnCardTextInputLayout = (TextInputLayout) view.findViewById(R.id.nameOnCardTextInputLayout);
        nameOnCardEditText = (EditText) view.findViewById(R.id.nameOnCardEditText);

        verificationCodeTextInput = (TextInputLayout) view.findViewById(R.id.verificationCodeTextInputLayout);
        verificationCodeEditText = (EditText) view.findViewById(R.id.verificationCodeEditText);

        expirationDateTextView = (TextView) view.findViewById(R.id.expirationDateTextView);
        pickDateTextView = (TextView) view.findViewById(R.id.pickDateTextView);
        pickDateTextView.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = (CheckBox) view.findViewById(R.id.saveCardOnFileCheckBox);
        saveCardOnFileCheckBox.setChecked(false);
        saveCardOnFileCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAsDefaultCheckBox.setEnabled(isChecked);
                if (!isChecked) {
                    setAsDefaultCheckBox.setChecked(false);
                }
            }
        });

        setAsDefaultCheckBox = (CheckBox) view.findViewById(R.id.setAsDefaultCheckBox);
        setAsDefaultCheckBox.setEnabled(false);
        setAsDefaultCheckBox.setChecked(false);

        billingAddressTextView = (CarePayTextView) view.findViewById(R.id.billingAddressTextView);
        useProfileAddressCheckBox = (CheckBox) view.findViewById(R.id.useProfileAddressCheckBox);
        useProfileAddressCheckBox.setOnCheckedChangeListener(useProfileAddressListener);

        address1TextInput = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);

        address2TextInput = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);

        zipCodeTextInput = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);

        cityTextInput = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityEditText = (EditText) view.findViewById(R.id.cityId);

        stateTextInput = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
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

        nextButton = (Button) view.findViewById(R.id.nextButton);
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

        hideKeyboardOnViewTouch(view.findViewById(R.id.container_main));

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
        String currency = "USD";
        String cvv = creditCardsPayloadDTO.getCvv();
        String expiryDate = creditCardsPayloadDTO.getExpireDt();
        String name = creditCardsPayloadDTO.getNameOnCard();
        String cardType = creditCardsPayloadDTO.getCardType();
        String number = getCardNumber();

        try {
            MerchantServiceMetadataDTO merchantServiceDTO = null;
            for (MerchantServicesDTO merchantService : merchantServicesList) {
                if (merchantService.getName().toLowerCase().contains("payeezy")) {
                    merchantServiceDTO = merchantService.getMetadata();
                    break;
                }
            }

            String tokenUrl = merchantServiceDTO.getBaseUrl() + merchantServiceDTO.getUrlPath();
            if (!tokenUrl.endsWith("?")) {
                tokenUrl += "?";
            }

            TransactionDataProvider.tokenUrl = tokenUrl;
            TransactionDataProvider.appIdCert = merchantServiceDTO.getApiKey();
            TransactionDataProvider.secureIdCert = merchantServiceDTO.getApiSecret();
            TransactionDataProvider.tokenCert = merchantServiceDTO.getMasterMerchantToken();
            TransactionDataProvider.trTokenInt = merchantServiceDTO.getMasterTaToken();
            TransactionDataProvider.jsSecurityKey = merchantServiceDTO.getMasterJsSecurityKey();
            TransactionDataProvider.taToken = merchantServiceDTO.getMasterTaToken();

            String tokenType = merchantServiceDTO.getTokenType();
            String tokenAuth = merchantServiceDTO.getTokenizationAuth();
            PayeezyRequestTask requestTask = new PayeezyRequestTask(getContext(), this);
            requestTask.execute("gettokenvisa", tokenAuth, "", currency, tokenType, cardType, name, number, expiryDate, cvv);
            System.out.println("first authorize call end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("authorize call end");
    }

    private void setDefaultBillingAddressTexts() {
        if (addressPayloadDTO != null && !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress1())) {
            address1EditText.setText(addressPayloadDTO.getAddress1());
            address1EditText.getOnFocusChangeListener().onFocusChange(address1EditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress1()));

            address2EditText.setText(!StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress2()) ? addressPayloadDTO.getAddress2() : " ");
            address2EditText.getOnFocusChangeListener().onFocusChange(address2EditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress2()));

            zipCodeEditText.setText(addressPayloadDTO.getZipcode());
            zipCodeEditText.getOnFocusChangeListener().onFocusChange(zipCodeEditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getZipcode()));

            cityEditText.setText(addressPayloadDTO.getCity());
            cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText, !StringUtil.isNullOrEmpty(addressPayloadDTO.getCity()));

            stateAutoCompleteTextView.setText(addressPayloadDTO.getState());
            stateAutoCompleteTextView.getOnFocusChangeListener().onFocusChange(stateAutoCompleteTextView, !StringUtil.isNullOrEmpty(addressPayloadDTO.getState()));
        } else {
            useProfileAddressCheckBox.setChecked(false);
            useProfileAddressCheckBox.setEnabled(false);
            setAddressFieldsEnabled(true);
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
        address1EditText.setEnabled(isEnabled);
        address2EditText.setEnabled(isEnabled);
        zipCodeEditText.setEnabled(isEnabled);
        cityEditText.setEnabled(isEnabled);
        stateAutoCompleteTextView.setEnabled(isEnabled);
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
        String valueString = "value";
        if (resString != null && resString.contains(valueString)) {

            String group1 = "(\\\"value\\\":\")";
            String group2 = "(\\d+)";
            String regex = group1 + group2;
            String tokenValue = null;
            Matcher matcher = Pattern.compile(regex).matcher(resString);
            if (matcher.find()) {
                tokenValue = matcher.group().replaceAll(group1, "");
            }

            if (tokenValue != null && tokenValue.matches(group2)) {
                creditCardsPayloadDTO.setToken(tokenValue);
                authoriseCreditCardResponseCallback.onAuthorizeCreditCardSuccess();
            } else {
                nextButton.setEnabled(true);
                authoriseCreditCardResponseCallback.onAuthorizeCreditCardFailed();

            }

        } else {
            nextButton.setEnabled(true);
            authoriseCreditCardResponseCallback.onAuthorizeCreditCardFailed();
        }
    }


}