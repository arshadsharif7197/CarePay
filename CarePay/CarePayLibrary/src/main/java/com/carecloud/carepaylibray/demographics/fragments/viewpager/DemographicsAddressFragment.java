package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;

import com.smartystreets.api.us_zipcode.City;

/**
 * Created by lsoco_user on 9/2/2016.
 * Fragment for on-boarding demographics address.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {

    View view;

    private LinearLayout rootLayout;

    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout phNoTextInputLayout;
    private TextInputLayout address1TextInputLayout;
    private TextInputLayout address2TextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipCodeTextInputLayout;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText cityEditText;
    private EditText firstNameText;
    private EditText lastNameText;

    private Button nextButton;

    private AutoCompleteTextView stateAutoCompleteTextView;
    private String stateAbbr = null;
    private City smartyStreetsResponse;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;
    private boolean isPhoneEmpty;
    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isStateEmtpy;
    private boolean isZipEmpty;

    private boolean isNextVisible = false;
    private DemographicAddressPayloadDTO     modelAddress;
    private DemographicPersDetailsPayloadDTO modelPersDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_address, container, false);

        // used to detect soft keyboard hide/show and thus toggle the next button visible/invisible
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = view.getRootView().getHeight() - view.getHeight();
                if (heightDiff > SystemUtil.dpToPx(getActivity(), 200)) { // if more than 200 dp, it's probably a keyboard...
                    if (isNextVisible) {
                        nextButton.setVisibility(View.GONE);
                        isNextVisible = false;
                    }
                } else {
                    if (!isNextVisible) {
                        nextButton.setVisibility(View.VISIBLE);
                        isNextVisible = true;
                    }
                }
            }
        });
        rootLayout = (LinearLayout) view.findViewById(R.id.demographicsAddressRootContainer);

        isFirstNameEmpty = true;
        isLastNameEmpty = true;
        isAddressEmpty = true;
        isZipEmpty = true;
        isCityEmpty = true;
        isStateEmtpy = true;
        isPhoneEmpty = true;

        initModels();
        initialiseUIFields();
        setTypefaces(view);

        nextButton.setEnabled(!isFirstNameEmpty && !isLastNameEmpty);

        return view;
    }

    /**
     * Inits the UI components; to manipulate the change of the hint to caps when floating,
     * set the textinputlayout as the tag of the corresponding edittext and the hint as the
     * the tag of the textinputlayout
     */
    private void initialiseUIFields() {
        String hint;

        hint = getString(R.string.firstname_text);
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressFirstNameTextInput);
        firstNameInputLayout.setTag(hint);
        firstNameText = (EditText) view.findViewById(R.id.demogrAddressFirstNameEdit);
        firstNameText.setTag(firstNameInputLayout);

        // set the require hints  fonts
        TextView firstNameReqHint = (TextView) view.findViewById(R.id.demogrAddressFirstNameReqHint);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), firstNameReqHint);

        // set the require hints  fonts
        TextView lastNameReqHint = (TextView) view.findViewById(R.id.demogrAddressLastNameReqHint);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), lastNameReqHint);

        hint = getString(R.string.lastname_text);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressLastNameTextInput);
        lastNameInputLayout.setTag(hint);
        lastNameText = (EditText) view.findViewById(R.id.demogrAddressLastNameEdit);
        lastNameText.setTag(lastNameInputLayout);

        hint = getString(R.string.Address1EditText);
        address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address1TextInputLayout.setTag(hint);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address1EditText.setTag(address1TextInputLayout);

        hint = getString(R.string.Address2EditText);
        address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        address2TextInputLayout.setTag(hint);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        address2EditText.setTag(address2TextInputLayout);

        hint = getString(R.string.ZipCodeEditText);
        zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        zipCodeTextInputLayout.setTag(hint);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        zipCodeEditText.setTag(zipCodeTextInputLayout);

        hint = getString(R.string.CityEditText);
        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        cityTextInputLayout.setTag(hint);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        cityEditText.setTag(cityTextInputLayout);

        hint = getString(R.string.StateEditText);
        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        stateTextInputLayout.setTag(hint);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                                                R.layout.autocomplete_state_item,
                                                                R.id.text1,
                                                                AddressUtil.states);
        stateAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.stateAutoCompleteTextView);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stateAbbr = adapter.getItem(position);
            }
        });
        stateAutoCompleteTextView.setTag(stateTextInputLayout);

        hint = getString(R.string.PhoneNumberEditText);
        phNoTextInputLayout = (TextInputLayout) view.findViewById(R.id.phNoTextInputLayout);
        phNoTextInputLayout.setTag(hint);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        phoneNumberEditText.setTag(phNoTextInputLayout);

        nextButton = (Button) view.findViewById(R.id.demographicsAddressNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkReadyForNext()) { // if all valid; // TODO: 9/27/2016 uncomment
                    // update personal details
                    modelPersDetails.setFirstName(firstNameText.getText().toString());
                    modelPersDetails.setLastName(lastNameText.getText().toString());
                    // update the modelAddress with values from UI
                    modelAddress.setAddress1(address1EditText.getText().toString());
                    modelAddress.setAddress2(address2EditText.getText().toString());
                    String formattedZipCode = zipCodeEditText.getText().toString();
                    modelAddress.setZipcode(StringUtil.revertZipToRawFormat(formattedZipCode));
                    modelAddress.setCity(cityEditText.getText().toString());
                    modelAddress.setState(stateAutoCompleteTextView.getText().toString());
                    // eliminate '-' from the phone number
                    String formattedPhoneNum = phoneNumberEditText.getText().toString();
                    modelAddress.setPhone(StringUtil.revertToRawPhoneFormat(formattedPhoneNum));

                    ((DemographicsActivity) getActivity()).setAddressModel(modelAddress); // sent the modelAddress to the activity
                    ((DemographicsActivity) getActivity()).setDetailsModel(modelPersDetails); // sent the modelDetails to the activity

                    ((DemographicsActivity) getActivity()).setCurrentItem(1, true);
                }
            }
        });

        setTypefaces(view);
        setFocusChangeListeners();
        setEditActionListeners();
        setTextWachers();

        // populate views
        populateViewsWithData();
    }

    /**
     * Init the models (DTOs) for this screen
     */
    public void initModels() {
        modelAddress = ((DemographicsActivity) getActivity()).getAddressModel();
        if (modelAddress == null) {
            modelAddress = new DemographicAddressPayloadDTO();
        }
        modelPersDetails = ((DemographicsActivity) getActivity()).getDetailsModel();
        if (modelPersDetails == null) {
            modelPersDetails = new DemographicPersDetailsPayloadDTO();
        }
    }

    private void populateViewsWithData() {
        if (modelPersDetails != null) {
            // populate the views
            String firstName = modelPersDetails.getFirstName();
            if (!StringUtil.isNullOrEmpty(firstName)) {
                firstNameText.setText(firstName);
                firstNameText.requestFocus();
                isFirstNameEmpty = false;
            }

            String lastName = modelPersDetails.getLastName();
            if (!StringUtil.isNullOrEmpty(lastName)) {
                lastNameText.setText(lastName);
                lastNameText.requestFocus();
                isLastNameEmpty = false;
            }
        }
        if (modelAddress != null) {
            String address1 = modelAddress.getAddress1();
            if (!StringUtil.isNullOrEmpty(address1)) {
                address1EditText.setText(address1);
                address1EditText.requestFocus();
            }

            String address2 = modelAddress.getAddress2();
            if (!StringUtil.isNullOrEmpty(address2)) {
                address2EditText.setText(address2);
                address2EditText.requestFocus();
            }

            String zip = modelAddress.getZipcode();
            if (!StringUtil.isNullOrEmpty(zip)) {
                zipCodeEditText.setText(StringUtil.formatZipCode(zip));
                zipCodeEditText.requestFocus();
            }

            String city = modelAddress.getCity();
            if (!StringUtil.isNullOrEmpty(city)) {
                cityEditText.setText(city);
                cityEditText.requestFocus();
            }

            String state = modelAddress.getState();
            if (!StringUtil.isNullOrEmpty(state)) {
                stateAutoCompleteTextView.setText(state);
                stateAutoCompleteTextView.requestFocus();
            }

            String phone = modelAddress.getPhone();
            if (!StringUtil.isNullOrEmpty(phone) && phone.length() == 10) {
                // expected as xxxxxxxxxx; convert to xxx-xxx-xxxx
                phoneNumberEditText.setText(StringUtil.formatPhoneNumber(phone));
                phoneNumberEditText.requestFocus();
            }
        }
        rootLayout.requestFocus();
    }

    private void setTextWachers() {
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameInputLayout.setError(null);
                    firstNameInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameInputLayout.setError(null);
                    lastNameInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        address1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String addr1 = address1EditText.getText().toString();
                isAddressEmpty = StringUtil.isNullOrEmpty(addr1);
                if (!isAddressEmpty) {
                    address1TextInputLayout.setError(null);
                    address1TextInputLayout.setErrorEnabled(false);
                    modelAddress.setAddress1(addr1);
                }
            }
        });

        zipCodeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zipCodeEditText.setSelection(zipCodeEditText.length());
            }
        });
        zipCodeEditText.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                prevLen = charSequence.length();
                zipCodeEditText.setSelection(charSequence.length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zip = zipCodeEditText.getText().toString();
                isZipEmpty = StringUtil.isNullOrEmpty(zip);
                if (!isZipEmpty) {
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                    modelAddress.setZipcode(zip);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String city = cityEditText.getText().toString();
                isCityEmpty = StringUtil.isNullOrEmpty(city);
                if (!isCityEmpty) {
                    cityTextInputLayout.setError(null);
                    cityTextInputLayout.setErrorEnabled(false);
                    modelAddress.setCity(city);
                }
            }
        });
        stateAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String state = stateAutoCompleteTextView.getText().toString();
                isStateEmtpy = StringUtil.isNullOrEmpty(state);
                if (!isStateEmtpy) {
                    stateTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateAbbr = state;
                    modelAddress.setState(state);
                }
            }
        });

        // place the cursor on the last char when clicked
        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberEditText.setSelection(phoneNumberEditText.length());
            }
        });
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                len = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable phonenumber) {
                String phone = phoneNumberEditText.getText().toString();
                isPhoneEmpty = StringUtil.isNullOrEmpty(phone);
                if (!isPhoneEmpty) {
                    phNoTextInputLayout.setError(null);
                    phNoTextInputLayout.setErrorEnabled(false);
                    modelAddress.setPhone(phone);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
            }
        });
    }

    private void enableNextButton() {
        boolean areAllReqNonEmpty = !(isFirstNameEmpty || isLastNameEmpty);
        nextButton.setEnabled(areAllReqNonEmpty);
    }

    private void setEditActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    lastNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lastNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    address1EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address1EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    address2EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address2EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    zipCodeEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        zipCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    cityEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    stateAutoCompleteTextView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        stateAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT) {
                    phNoTextInputLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_DONE) {
                    phoneNumberEditText.clearFocus();
                    rootLayout.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Helper to set focus change listener in order to toggle hint strings to caps
     */
    private void setFocusChangeListeners() {
        firstNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
                if (!hasFocus) { // for SmartyStreets
                    getCityAndState(zipCodeEditText.getText().toString());
                }
            }
        });

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });

        stateAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }

    private boolean checkState() {
        if (!isStateEmtpy) {
            boolean isStateAbbrUnknown = true; // check valid state
            for (String state : AddressUtil.states) {
                if (state.equals(stateAbbr)) {
                    isStateAbbrUnknown = false;
                }
            }
            if (stateAbbr == null || isStateAbbrUnknown) {
                stateTextInputLayout.setErrorEnabled(true);
                stateTextInputLayout.setError(getString(R.string.select_state_error_messae));
                return false;
            }
            stateTextInputLayout.setError(null);
            stateTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean checkCity() {
        if (!isCityEmpty) {
            String city = cityEditText.getText().toString();
            if (!StringUtil.isNullOrEmpty(city) && city.trim().length() < 3) {
                cityTextInputLayout.setErrorEnabled(true);
                cityTextInputLayout.setError(getString(R.string.minimum_char_in_city_error_messae));
                return false;
            }
            cityTextInputLayout.setError(null);
            cityTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean checkPhoneNumber() {
        if (!isPhoneEmpty) { // check validity only if non-empty
            String phone = phoneNumberEditText.getText().toString();
            if (!StringUtil.isNullOrEmpty(phone)
                    && !StringUtil.isValidPhoneNumber(phone.trim())) {
                phNoTextInputLayout.setErrorEnabled(true);
                phNoTextInputLayout.setError(getString(R.string.incorrect_phone_number_error_messae));
                return false;
            }
            phNoTextInputLayout.setError(null);
            phNoTextInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.addressHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.addressSubHeading));

        // for each text input test if its edit has text and set the font accordingly
        if (!StringUtil.isNullOrEmpty(firstNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), firstNameInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), firstNameInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), firstNameText);

        if (!StringUtil.isNullOrEmpty(lastNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), lastNameInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), lastNameInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), lastNameText);

        if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), zipCodeTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), zipCodeTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), zipCodeEditText);

        if (!StringUtil.isNullOrEmpty(address1EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address1TextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address1TextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), address1EditText);

        if (!StringUtil.isNullOrEmpty(address2EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address2TextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address2TextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), address2EditText);

        if (!StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), cityTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), cityTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), cityEditText);

        if (!StringUtil.isNullOrEmpty(stateAutoCompleteTextView.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), stateTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), stateTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), stateAutoCompleteTextView);

        if (!StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), phNoTextInputLayout);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), phNoTextInputLayout);
        }
        setProximaNovaRegularTypeface(getActivity(), phoneNumberEditText);

        setGothamRoundedMediumTypeface(getActivity(), nextButton);
    }

    private boolean checkReadyForNext() {
        boolean isPhoneValid = checkPhoneNumber();
        // for non-required field, check validity only if non-empty
        if (!isPhoneValid) {
            phoneNumberEditText.requestFocus();
        }

        boolean isStateValid = checkState();
        if (!isStateValid) {
            stateAutoCompleteTextView.requestFocus();
        }

        boolean isCityValid = checkCity();
        if (!isCityValid) {
            cityEditText.requestFocus();
        }

        return isPhoneValid && isStateValid && isCityValid;
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