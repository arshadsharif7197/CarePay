package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.smartystreets.api.us_zipcode.City;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypefaceEdittext;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {

    private static final String LOG_TAG = DemographicsAddressFragment.class.getSimpleName();
    View view;

    private LinearLayout rootLayout;

    private TextInputLayout firstNameInputLayout;
    private TextInputLayout middleNameInputLayout;
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
    private EditText middleNameText;
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
                    if(isNextVisible) {
                        Log.v(LOG_TAG, "onShown()");
                        nextButton.setVisibility(View.GONE);
                        isNextVisible = false;
                    }
                } else {
                    if(!isNextVisible) {
                        Log.v(LOG_TAG, "onHidden()");
                        nextButton.setVisibility(View.VISIBLE);
                        isNextVisible = true;
                    }
                }
            }
        });
        rootLayout = (LinearLayout) view.findViewById(R.id.demographicsAddressRootContainer);

        initialiseUIFields();
        setTypefaces(view);

        isFirstNameEmpty = true;
        isLastNameEmpty = true;
        isAddressEmpty = true;
        isZipEmpty = true;
        isCityEmpty = true;
        isStateEmtpy = true;
        isPhoneEmpty = true;

        nextButton.setEnabled(false);

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

        hint = getString(R.string.middlename_text);
        middleNameInputLayout = (TextInputLayout) view.findViewById(R.id.demogrAddressMiddleNameTextInputLayout);
        middleNameInputLayout.setTag(hint);
        middleNameText = (EditText) view.findViewById(R.id.demogrAddressMiddleNameEditText);
        middleNameText.setTag(middleNameInputLayout);

        // set the label
        TextView optionalLabelMiddleName = (TextView) view.findViewById(R.id.demogrAddresssMiddleNameOptionalLabel);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), optionalLabelMiddleName);

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

        // set the label
        TextView optionalLabelAddress = (TextView) view.findViewById(R.id.demogrAddressOptionalLabel);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), optionalLabelAddress);

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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
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
        stateTextInputLayout.setTag(stateTextInputLayout);

        hint = getString(R.string.PhoneNumberEditText);
        phNoTextInputLayout = (TextInputLayout) view.findViewById(R.id.phNoTextInputLayout);
        phNoTextInputLayout.setTag(hint);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        phoneNumberEditText.setTag(phNoTextInputLayout);

        nextButton = (Button) view.findViewById(R.id.demographicsAddressNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkReadyForNext()) { // if all valid;
                    // move to next (plus, eventually, perform backend stuff)
                    ((DemographicsActivity) getActivity()).setCurrentItem(1, true);
                }
            }
        });

        setTypefaces(view);
        setFocusChangeListeners();
        setEditActionListeners();
        setTextWachers();
    }

    private void setTextWachers() {
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isAddressEmpty = StringUtil.isNullOrEmpty(address1EditText.getText().toString());
                if (!isAddressEmpty) {
                    address1TextInputLayout.setError(null);
                    address1TextInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        zipCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isZipEmpty = StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString());
                if (!isZipEmpty) {
                    zipCodeTextInputLayout.setError(null);
                    zipCodeTextInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCityEmpty = StringUtil.isNullOrEmpty(cityEditText.getText().toString());
                if (!isCityEmpty) {
                    cityTextInputLayout.setError(null);
                    cityTextInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
        stateAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isStateEmtpy = StringUtil.isNullOrEmpty(stateAutoCompleteTextView.getText().toString());
                if (!isStateEmtpy) {
                    stateTextInputLayout.setError(null);
                    stateTextInputLayout.setErrorEnabled(false);
                    stateAbbr = stateAutoCompleteTextView.getText().toString();
                }
                enableNextButton();
            }
        });
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isPhoneEmpty = StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString());
                if (!isPhoneEmpty) {
                    phNoTextInputLayout.setError(null);
                    phNoTextInputLayout.setErrorEnabled(false);
                }
                enableNextButton();
            }
        });
    }

    private void enableNextButton() {
        Log.v(LOG_TAG, "isFirstNameEmpty=" + isFirstNameEmpty
                + "\nisLastNameEmpty="+isLastNameEmpty
                + "\nisAddressEmpty=" + isAddressEmpty
                + "\nisZipEmpty" + isZipEmpty
                + "\nisCityEmpty" + isCityEmpty
                + "\nisStateEmtpy" + isStateEmtpy
                + "\nisPhoneEmpty=" + isPhoneEmpty);
        Log.v(LOG_TAG, "state_null=" + stateAbbr);

        boolean areAllNonEmpty =
                !( isFirstNameEmpty
                || isLastNameEmpty
                || isAddressEmpty
                || isZipEmpty
                || isCityEmpty
                || isStateEmtpy
                || isPhoneEmpty );
        nextButton.setEnabled(areAllNonEmpty);
    }

    private void setEditActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    middleNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        middleNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) { // no validations for middle name
                    lastNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lastNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    address1EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address1EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    address2EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        address2EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    zipCodeEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        zipCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    cityEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        cityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    stateAutoCompleteTextView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        stateAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    phNoTextInputLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
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
            public void onFocusChange(View view, boolean b) {
                if (b) { // show the keyboard
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });

        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });

        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
                if (!b) { // for SmartyStreets
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
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private boolean checkState() {
        boolean isStateAbbrUnknown = true; // check valid state
        for(String state : AddressUtil.states) {
            if(state.equals(stateAbbr)) {
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
        return true;
    }

    private boolean checkCity() {
        if (TextUtils.isEmpty(cityEditText.getText().toString().trim())) {
            cityTextInputLayout.setErrorEnabled(true);
            cityTextInputLayout.setError(getString(R.string.empty_city_error_messae));
            return false;
        } else if (cityEditText.getText().toString().trim().length() < 3) {
            cityTextInputLayout.setErrorEnabled(true);
            cityTextInputLayout.setError(getString(R.string.minimum_char_in_city_error_messae));
            return false;
        }
        cityTextInputLayout.setError(null);
        cityTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPhoneNumber() {
        if (TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())) {
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.empty_phone_number_error_messae));
            return false;
        } else if (!StringUtil.isValidPhoneNumber(phoneNumberEditText.getText().toString().trim())) {
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.incorrect_phone_number_error_messae));
            return false;
        }
        phNoTextInputLayout.setError(null);
        phNoTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private void setTypefaces(View view) {
        setProximaNovaRegularTypefaceLayout(getActivity(), firstNameInputLayout);
        setProximaNovaRegularTypeface(getActivity(), firstNameText);

        setProximaNovaRegularTypefaceLayout(getActivity(), middleNameInputLayout);
        setProximaNovaRegularTypeface(getActivity(), middleNameText);

        setProximaNovaRegularTypefaceLayout(getActivity(), lastNameInputLayout);
        setProximaNovaRegularTypeface(getActivity(), lastNameText);

        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.addressHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.addressSubHeading));

        setProximaNovaRegularTypeface(getActivity(), zipCodeEditText);
        setProximaNovaRegularTypeface(getActivity(), address1EditText);
        setProximaNovaRegularTypeface(getActivity(), address2EditText);
        setProximaNovaRegularTypeface(getActivity(), cityEditText);
        setProximaNovaRegularTypeface(getActivity(), stateAutoCompleteTextView);
        setGothamRoundedMediumTypeface(getActivity(), nextButton);

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsAddressNameSectionLabel));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsAddressAddressSectionLabel));
    }

    private boolean checkReadyForNext() {
        boolean isPhoneValid = checkPhoneNumber();
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
     *
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
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (smartyStreetsResponse != null) {
                    cityEditText.setText(smartyStreetsResponse.getCity());
//                    isCityEmpty = false;

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateAutoCompleteTextView.setText(stateAbbr);
//                    isStateEmtpy = false;

                    enableNextButton();
                }

            }
        }.execute(zipcode);
    }
}