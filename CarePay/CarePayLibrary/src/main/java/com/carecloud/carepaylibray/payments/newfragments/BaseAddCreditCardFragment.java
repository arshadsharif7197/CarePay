package com.carecloud.carepaylibray.payments.newfragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialog;
import com.carecloud.carepaylibray.customdialogs.SimpleDatePickerDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.payeeze.PayeezyCall;
import com.carecloud.carepaylibray.payeeze.model.CreditCard;
import com.carecloud.carepaylibray.payments.PaymentsViewModel;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.CreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServiceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.TokenizationService;
import com.carecloud.carepaylibray.payments.utils.CreditCardUtil;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseAddCreditCardFragment extends BaseDialogFragment
        implements SimpleDatePickerDialog.OnDateSetListener {

    public interface IAuthoriseCreditCardResponse {
        void onAuthorizeCreditCardSuccess();

        void onAuthorizeCreditCardFailed();
    }

    protected TextInputLayout nameOnCardTextInputLayout;
    protected TextInputLayout creditCardNoTextInput;
    protected TextInputLayout verificationCodeTextInput;
    protected TextInputLayout expirationDateTextInput;
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
    protected EditText expirationDateEditText;
    protected TextView title;
    protected CheckBox saveCardOnFileCheckBox;
    protected CheckBox setAsDefaultCheckBox;
    protected CheckBox useProfileAddressCheckBox;

    protected TextView nameOnCardRequiredTextView;
    protected TextView creditCardNoRequiredTextView;
    protected TextView verificationCodeRequiredTextView;
    protected TextView expirationDateRequiredTextView;

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
    protected PaymentCreditCardsPayloadDTO creditCardsPayloadDTO;
    protected CreditCardBillingInformationDTO billingInformationDTO;
    protected IAuthoriseCreditCardResponse authoriseCreditCardResponseCallback;
    protected List<MerchantServicesDTO> merchantServicesList;

    protected PaymentConfirmationInterface callback;
    protected PaymentsModel paymentsModel;
    protected boolean onlySelectMode;
    protected PaymentsViewModel viewModel;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        viewModel = new ViewModelProvider(getActivity()).get(PaymentsViewModel.class);
        paymentsModel = viewModel.getPaymentsModel();
        Bundle arguments = getArguments();
        Gson gson = new Gson();
        String payloadString;
        if (arguments != null) {
            merchantServicesList = paymentsModel.getPaymentPayload().getMerchantServices();
            if (arguments.containsKey(CarePayConstants.PAYMENT_AMOUNT_BUNDLE)) {
                amountToMakePayment = arguments.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            }
            onlySelectMode = arguments.getBoolean(CarePayConstants.ONLY_SELECT_MODE);
        }
        if (addressPayloadDTO == null) {
            payloadString = getApplicationPreferences().readStringFromSharedPref(CarePayConstants
                    .DEMOGRAPHICS_ADDRESS_BUNDLE);
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
        View addNewCreditCardView = inflater.inflate(R.layout
                .fragment_add_new_credit_card, container, false);

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
                String type = CreditCardUtil.getCreditCardType(getCardNumber());
                if (!StringUtil.isNullOrEmpty(getCardNumber()) && type != null) {
                    cardTypeTextView.setVisibility(View.VISIBLE);
                    cardTypeTextView.setText(type);
                    creditCardNoRequiredTextView.setVisibility(View.GONE);
                } else {
                    cardTypeTextView.setVisibility(View.GONE);
                    creditCardNoRequiredTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        nameOnCardEditText.addTextChangedListener(textWatcher);
        verificationCodeEditText.addTextChangedListener(textWatcher);
        address1EditText.addTextChangedListener(textWatcher);
        zipCodeEditText.addTextChangedListener(textWatcher);
        cityEditText.addTextChangedListener(textWatcher);
        stateAutoCompleteTextView.addTextChangedListener(textWatcher);

        address1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    address2EditText.setEnabled(false);
                    address2EditText.setText("");
                } else if (!useProfileAddressCheckBox.isChecked()) {
                    address2EditText.setEnabled(true);
                }
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            validateCreditCardDetails();
            checkRequiredFields(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public String getCardNumber() {
        return creditCardNoEditText.getText().toString().replace(" ", "").trim();
    }

    protected String getLastFour() {
        String fullCard = creditCardNoEditText.getText().toString().replace(" ", "").trim();
        return fullCard.substring(fullCard.length() - 4, fullCard.length());
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            title = toolbar.findViewById(R.id.respons_toolbar_title);
            toolbar.setTitle("");
            if (getDialog() == null) {
                toolbar.setNavigationIcon(R.drawable.icn_nav_back);
                toolbar.setNavigationOnClickListener(view12 -> getActivity().onBackPressed());
            } else {
                View close = view.findViewById(R.id.closeViewLayout);
                if (close != null) {
                    close.setOnClickListener(view1 -> cancel());
                }
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
            }

        }

    }

    private void initializeViews(View view) {
        creditCardNoTextInput = view.findViewById(R.id.creditCardNoTextInputLayout);
        creditCardNoEditText = view.findViewById(R.id.creditCardNoEditText);
        creditCardNoRequiredTextView = view.findViewById(R.id.creditCardNoRequiredTextView);

        cardTypeTextView = view.findViewById(R.id.cardTypeTextView);

        nameOnCardTextInputLayout = view.findViewById(R.id.nameOnCardTextInputLayout);
        nameOnCardEditText = view.findViewById(R.id.nameOnCardEditText);
        nameOnCardRequiredTextView = view.findViewById(R.id.nameOnCardRequiredTextView);

        verificationCodeTextInput = view.findViewById(R.id.verificationCodeTextInputLayout);
        verificationCodeEditText = view.findViewById(R.id.verificationCodeEditText);
        verificationCodeRequiredTextView = view.findViewById(R.id.verificationCodeRequiredTextView);

        expirationDateTextInput = view.findViewById(R.id.expirationDateInputLayout);
        expirationDateEditText = view.findViewById(R.id.expirationDateEditText);
        expirationDateRequiredTextView = view.findViewById(R.id.expirationDateRequiredTextView);
        expirationDateEditText.setOnClickListener(pickDateListener);

        saveCardOnFileCheckBox = view.findViewById(R.id.saveCardOnFileCheckBox);
        saveCardOnFileCheckBox.setChecked(false);
        saveCardOnFileCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setAsDefaultCheckBox.setEnabled(isChecked);
            setAsDefaultCheckBox.setChecked(false);
            if (isChecked && paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
                setAsDefaultCheckBox.setChecked(true);
                setAsDefaultCheckBox.setEnabled(false);
            }

        });

        setAsDefaultCheckBox = view.findViewById(R.id.setAsDefaultCheckBox);
        setAsDefaultCheckBox.setEnabled(false);
        setAsDefaultCheckBox.setChecked(false);

        billingAddressTextView = view.findViewById(R.id.billingAddressTextView);
        useProfileAddressCheckBox = view.findViewById(R.id.useProfileAddressCheckBox);
        useProfileAddressCheckBox.setOnCheckedChangeListener(useProfileAddressListener);

        address1TextInput = view.findViewById(R.id.address1TextInputLayout);
        address1EditText = view.findViewById(R.id.addressEditTextId);

        address2TextInput = view.findViewById(R.id.address2TextInputLayout);
        address2EditText = view.findViewById(R.id.addressEditText2Id);

        zipCodeTextInput = view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeEditText = view.findViewById(R.id.zipCodeId);

        cityTextInput = view.findViewById(R.id.cityTextInputLayout);
        cityEditText = view.findViewById(R.id.cityId);

        stateTextInput = view.findViewById(R.id.stateTextInputLayout);
        stateAutoCompleteTextView = view
                .findViewById(R.id.addNewCredidCardStateAutoCompleteTextView);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.autocomplete_state_item,
                R.id.text1,
                AddressUtil.states);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener((parent, view1, position, id)
                -> stateAbbr = adapter.getItem(position));

        nextButton = view.findViewById(R.id.nextButton);
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
            authorizeOrSelectCreditCard();
        }
    };

    protected void authorizeOrSelectCreditCard() {
        authorizeCreditCard();
    }

    private void setDTOs() {
        creditCardsPayloadDTO = new PaymentCreditCardsPayloadDTO();
        billingInformationDTO = new CreditCardBillingInformationDTO();
        billingInformationDTO.setSameAsPatient(useProfileAddressCheckBox.isChecked());
        creditCardsPayloadDTO.setCardNumber(getLastFour());
        creditCardsPayloadDTO.setNameOnCard(nameOnCardEditText.getText().toString().trim());
        creditCardsPayloadDTO.setCvv(verificationCodeEditText.getText().toString().trim());
        creditCardsPayloadDTO.setExpireDtDisplay(expirationDateEditText.getText().toString().trim());
        String expiryDate = expirationDateEditText.getText().toString();
        expiryDate = expiryDate.substring(0, 2) + expiryDate.substring(expiryDate.length() - 2);
        creditCardsPayloadDTO.setExpireDt(expiryDate);
        creditCardsPayloadDTO.setCardType(CreditCardUtil.getCreditCardType(getCardNumber()));
        billingInformationDTO.setLine1(address1EditText.getText().toString().trim());
        billingInformationDTO.setLine2(address2EditText.getText().toString().trim());
        billingInformationDTO.setZip(zipCodeEditText.getText().toString().trim());
        billingInformationDTO.setCity(cityEditText.getText().toString().trim());
        billingInformationDTO.setState(stateAutoCompleteTextView.getText().toString().trim());
        creditCardsPayloadDTO.setBillingInformation(billingInformationDTO);
        creditCardsPayloadDTO.setTokenizationService(TokenizationService.payeezy);
    }

    protected void authorizeCreditCard() {
        String cvv = creditCardsPayloadDTO.getCvv();
        String expiryDate = creditCardsPayloadDTO.getExpireDt();
        String name = creditCardsPayloadDTO.getNameOnCard();
        String cardType = creditCardsPayloadDTO.getCardType();
        String number = getCardNumber();

        MerchantServiceMetadataDTO merchantServiceDTO = null;
        for (MerchantServicesDTO merchantService : merchantServicesList) {
            if (merchantService.getName().toLowerCase().contains("payeezy")) {
                merchantServiceDTO = merchantService.getMetadata();
                break;
            }
        }

        CreditCard creditCard = new CreditCard();
        creditCard.setCardHolderName(name);
        creditCard.setCardNumber(number);
        creditCard.setCvv(cvv);
        creditCard.setExpDate(expiryDate);
        creditCard.setType(cardType);

        showProgressDialog();
        PayeezyCall payeezyCall = new PayeezyCall();
        payeezyCall.doCall(creditCard, merchantServiceDTO, tokenizeResponse -> {
            hideProgressDialog();
            if (tokenizeResponse != null) {
                if (tokenizeResponse.getToken() != null) {
                    creditCardsPayloadDTO.setToken(tokenizeResponse.getToken().getValue());
                    authoriseCreditCardResponseCallback.onAuthorizeCreditCardSuccess();
                } else {
                    nextButton.setEnabled(true);
                    authoriseCreditCardResponseCallback.onAuthorizeCreditCardFailed();

                }
            } else {
                nextButton.setEnabled(true);
                authoriseCreditCardResponseCallback.onAuthorizeCreditCardFailed();
            }
        });

    }

    private void setDefaultBillingAddressTexts() {
        if (addressPayloadDTO != null && !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress1())) {
            address1EditText.setText(addressPayloadDTO.getAddress1());
            address1EditText.getOnFocusChangeListener().onFocusChange(address1EditText,
                    !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress1()));

            address2EditText.setText(!StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress2())
                    ? addressPayloadDTO.getAddress2() : " ");
            address2EditText.getOnFocusChangeListener().onFocusChange(address2EditText,
                    !StringUtil.isNullOrEmpty(addressPayloadDTO.getAddress2()));

            zipCodeEditText.setText(addressPayloadDTO.getZipcode());
            zipCodeEditText.getOnFocusChangeListener().onFocusChange(zipCodeEditText,
                    !StringUtil.isNullOrEmpty(addressPayloadDTO.getZipcode()));

            cityEditText.setText(addressPayloadDTO.getCity());
            cityEditText.getOnFocusChangeListener().onFocusChange(cityEditText,
                    !StringUtil.isNullOrEmpty(addressPayloadDTO.getCity()));

            stateAutoCompleteTextView.setText(addressPayloadDTO.getState());
            stateAutoCompleteTextView.getOnFocusChangeListener().onFocusChange(stateAutoCompleteTextView,
                    !StringUtil.isNullOrEmpty(addressPayloadDTO.getState()));
        } else {
//            useProfileAddressCheckBox.setChecked(false);
            useProfileAddressCheckBox.setEnabled(false);
            useProfileAddressCheckBox.setVisibility(View.GONE);
            setAddressFieldsEnabled(true);
        }
    }


    private void setChangeFocusListeners() {
        creditCardNoEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(creditCardNoTextInput, null));
        nameOnCardEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(nameOnCardTextInputLayout, null));
        expirationDateEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(expirationDateTextInput, null));
        verificationCodeEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(verificationCodeTextInput, null));
        address1EditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address1TextInput, null));
        address2EditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(address2TextInput, null));
        zipCodeEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(zipCodeTextInput, (view, hasFocus) -> {
                    if (!hasFocus) { // for SmartyStreets
                        getCityAndState(zipCodeEditText.getText().toString());
                    }
                }));
        cityEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(cityTextInput, null));
        stateAutoCompleteTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(stateTextInput, null));
    }

    private void setActionListeners() {
        creditCardNoEditText.setOnEditorActionListener((textView, action, keyEvent) -> {
            if (action == EditorInfo.IME_ACTION_NEXT) {
                verificationCodeEditText.requestFocus();
                return true;
            }
            return false;
        });
        nameOnCardEditText.setOnEditorActionListener((textView, action, keyEvent) -> {
            if (action == EditorInfo.IME_ACTION_NEXT) {
                creditCardNoEditText.requestFocus();
                return true;
            }
            return false;
        });
        verificationCodeEditText.setOnEditorActionListener((textView, action, keyEvent) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                verificationCodeEditText.clearFocus();
                expirationDateEditText.requestFocus();
                SystemUtil.hideSoftKeyboard(getActivity());
                return true;
            }
            return false;
        });
    }

    private CompoundButton.OnCheckedChangeListener useProfileAddressListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            validateCreditCardDetails();
            if (isChecked) {
                setAddressFieldsEnabled(false);
                setDefaultBillingAddressTexts();
                stateAutoCompleteTextView.dismissDropDown();
            } else {
                setAddressFieldsEnabled(true);
                address1EditText.setText(null);
                address2EditText.setText(null);
                zipCodeEditText.setText(null);
                cityEditText.setText(null);
                stateAutoCompleteTextView.dismissDropDown();
                stateAutoCompleteTextView.setText(null);
            }
        }
    };

    private void setAddressFieldsEnabled(boolean isEnabled) {
        address1EditText.setEnabled(isEnabled);
        address1TextInput.setEnabled(isEnabled);
        address2EditText.setEnabled(isEnabled);
        address2TextInput.setEnabled(isEnabled);
        zipCodeEditText.setEnabled(isEnabled);
        zipCodeTextInput.setEnabled(isEnabled);
        cityEditText.setEnabled(isEnabled);
        cityTextInput.setEnabled(isEnabled);
        stateAutoCompleteTextView.setEnabled(isEnabled);
        stateAutoCompleteTextView.setClickable(isEnabled);
        stateTextInput.setEnabled(isEnabled);
    }

    private View.OnClickListener pickDateListener = view -> displaySimpleDatePickerDialogFragment();

    @Override
    public void onDateSet(int year, int monthOfYear) {
        expirationDateEditText.setText(DateUtil.getInstance().formatMonthYear(year, monthOfYear));
        expirationDateEditText.getOnFocusChangeListener().onFocusChange(expirationDateEditText,
                !StringUtil.isNullOrEmpty(expirationDateEditText.getText().toString().trim()));
        expirationDateRequiredTextView.setVisibility(View.GONE);
        validateCreditCardDetails();
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        if (expirationDateEditText.getText().toString().contains("/")) {
            String[] selectedDate = expirationDateEditText.getText().toString().split("/");
            int month = Integer.parseInt(selectedDate[0]);
            int year = Integer.parseInt(selectedDate[1]);
            datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(year, month - 1);
        } else {
            DateUtil instance = DateUtil.getInstance().setToCurrent();
            datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(instance.getYear(),
                    instance.getMonth());
        }
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }

    private boolean validateCreditCardDetails() {
        if (!CreditCardUtil.isCreditCardNumberValid(getCardNumber())) {
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

        if (expirationDateEditText.getText().toString().contains("/")) {
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            return true;
        } else {
            nextButton.setEnabled(false);
            nextButton.setClickable(false);
            return false;
        }
    }

    private void checkRequiredFields(CharSequence charSequence) {
        if (nameOnCardEditText.getText().hashCode() == charSequence.hashCode()) {
            nameOnCardRequiredTextView.setVisibility(!(nameOnCardEditText.getText().toString().trim().length() > 0) ? View.VISIBLE : View.GONE);
        } else if (verificationCodeEditText.getText().hashCode() == charSequence.hashCode()) {
            verificationCodeRequiredTextView.setVisibility(!(verificationCodeEditText.getText().toString().length() > 2) ? View.VISIBLE : View.GONE);
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

}