package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;
import com.carecloud.carepaylibray.utils.StringFunctions;
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypefaceEdittext;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment  {

    View view;
    private TextInputLayout phNoTextInputLayout,
            address1TextInputLayout,
            address2TextInputLayout,
            cityTextInputLayout,
            stateTextInputLayout,
            zipCodeTextInputLayout;
    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText cityEditText;
    private Button nextButton;
    AutoCompleteTextView autoCompleteTextView;
    String state_var = null;

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_address, container, false);

        initialiseUIFields();


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_state_item, R.id.text1, states);
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoTextCompleteStates);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state_var = adapter.getItem(position).toString();
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                state_var = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setTypefaces(view);
        setFocusChangeListeners();

        return view;
    }

    /**
     * Helper to set focus change listener in order to toggle hint strings to caps
     */
    private void setFocusChangeListeners() {
        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.PhoneNumberEditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    phNoTextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(phoneNumberEditText.getText().toString())) {
                        // change hint to lower
                        phNoTextInputLayout.setHint(hint);

                    } else {
                        phoneNumberEditText.setHint(hint);
                    }
                }
            }
        });
        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.Address1EditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    address1TextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(address1EditText.getText().toString())) {
                        // change hint to lower
                        address1TextInputLayout.setHint(hint);

                    } else {
                        address1EditText.setHint(hint);
                    }
                }
            }
        });
        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.Address2EditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    address2TextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(address2EditText.getText().toString())) {
                        // change hint to lower
                        address2TextInputLayout.setHint(hint);

                    } else {
                        address2EditText.setHint(hint);
                    }
                }
            }
        });
        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.CityEditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    cityTextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(cityEditText.getText().toString())) {
                        // change hint to lower
                        cityTextInputLayout.setHint(hint);
                    } else {
                        cityEditText.setHint(hint);
                    }
                }
            }
        });
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.StateEditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    stateTextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(autoCompleteTextView.getText().toString())) {
                        // change hint to lower
                        stateTextInputLayout.setHint(hint);
                    } else {
                        autoCompleteTextView.setHint(hint);
                    }
                }
            }
        });
        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String hint = getString(R.string.ZipCodeEditText);
                String hintCaps = hint.toUpperCase();
                if(b) {
                    zipCodeTextInputLayout.setHint(hintCaps);
                } else {
                    if(StringFunctions.isNullOrEmpty(zipCodeEditText.getText().toString())) {
                        // change hint to lower
                        zipCodeTextInputLayout.setHint(hint);
                    } else {
                        zipCodeEditText.setHint(hint);
                    }
                }
            }
        });
    }

    private void initialiseUIFields() {
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        cityEditText = (EditText) view.findViewById(R.id.cityId);

        phNoTextInputLayout = (TextInputLayout) view.findViewById(R.id.phNoTextInputLayout);
        address1TextInputLayout = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2TextInputLayout = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        zipCodeTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);

        nextButton = (Button) view.findViewById(R.id.demographicsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextbuttonClick();
            }
        });
    }


    private boolean checkState() {
        if (TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())) {
            autoCompleteTextView.requestFocus();
            stateTextInputLayout.setErrorEnabled(true);
            stateTextInputLayout.setError(getString(R.string.empty_state_error_messae));
            return false;
        } else if (state_var == null) {
            autoCompleteTextView.requestFocus();
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
            cityEditText.requestFocus();
            cityTextInputLayout.setErrorEnabled(true);
            cityTextInputLayout.setError(getString(R.string.empty_city_error_messae));
            return false;
        } else if (cityEditText.getText().toString().trim().length() < 3) {
            cityEditText.requestFocus();
            cityTextInputLayout.setErrorEnabled(true);
            cityTextInputLayout.setError(getString(R.string.minimum_char_in_city_error_messae));
            return false;
        }
        cityTextInputLayout.setError(null);
        cityTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkAddress() {
        if (TextUtils.isEmpty(address1EditText.getText().toString().trim())) {
            address1EditText.requestFocus();
            address1TextInputLayout.setErrorEnabled(true);
            address1TextInputLayout.setError(getString(R.string.empty_address_error_messae));
            return false;
        }
        address1TextInputLayout.setError(null);
        address1TextInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkZipcode() {
        if (TextUtils.isEmpty(zipCodeEditText.getText().toString().trim())) {
            zipCodeEditText.requestFocus();
            zipCodeTextInputLayout.setErrorEnabled(true);
            zipCodeTextInputLayout.setError(getString(R.string.empty_zip_code_error_messae));
            return false;
        }
        zipCodeTextInputLayout.setError(null);
        zipCodeTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPhonenumber() {
        if (TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())) {
            phoneNumberEditText.requestFocus();
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.empty_phone_number_error_messae));
            return false;
        } else if (!Utility.isValidPhoneNumber(phoneNumberEditText.getText().toString().trim())) {
            phoneNumberEditText.requestFocus();
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.incorrect_phone_number_error_messae));
            return false;
        }
        phNoTextInputLayout.setError(null);
        phNoTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private void nextbuttonClick() {
        if (checkPhonenumber()) {
            if (checkAddress()) {
                if (checkCity()) {
                    if (checkState()) {
                        if (checkZipcode()) {
                            ((DemographicsActivity) getActivity()).setCurrentItem(1, true);
                        }
                    }
                }
            }
        }
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.addressHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.addressSubHeading));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), (EditText) view.findViewById(R.id.zipCodeId));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), (EditText) view.findViewById(R.id.addressEditTextId));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), (EditText) view.findViewById(R.id.addressEditText2Id));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), (EditText) view.findViewById(R.id.cityId));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), (EditText) view.findViewById(R.id.autoTextCompleteStates));

        setGothamRoundedMediumTypeface(getActivity(),(Button)view.findViewById(R.id.demographicsNextButton));

    }
}