package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypefaceEdittext;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {

    View view;
    private LinearLayout         rootContainer;
    private TextInputLayout      phNoTextInputLayout;
    private TextInputLayout      address1TextInputLayout;
    private TextInputLayout      address2TextInputLayout;
    private TextInputLayout      cityTextInputLayout;
    private TextInputLayout      stateTextInputLayout;
    private TextInputLayout      zipCodeTextInputLayout;
    private EditText             phoneNumberEditText;
    private EditText             zipCodeEditText;
    private EditText             address1EditText;
    private EditText             address2EditText;
    private EditText             cityEditText;
    private Button               nextButton;
    private boolean              isAddressValid;
    private boolean              isZipValid;
    private boolean              isCityValid;
    private boolean              isStateValid;
    private boolean              isPhoneValid;
    private AutoCompleteTextView stateAutoCompleteTextView;
    private String state_var = null;

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
        setTypefaces(view);

        isAddressValid = false;
        isCityValid = false;
        isStateValid = false;
        isZipValid = false;
        isPhoneValid = false;

        nextButton.setEnabled(false);

        return view;
    }

    /**
     * Inits the UI components; to manipulate the change of the hint to caps when floating,
     * set the textinputlayout as the tag of the corresponding edittext and the hint as the
     * the tag of the textinputlayout
     */
    private void initialiseUIFields() {
        rootContainer = (LinearLayout) view.findViewById(R.id.demographicsAddressRootContainer);

        String hint;

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

        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                                      R.layout.autocomplete_state_item,
                                                                      R.id.text1,
                                                                      states);
        stateAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoTextCompleteStates);
        stateAutoCompleteTextView.setThreshold(1);
        stateAutoCompleteTextView.setAdapter(adapter);
        stateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state_var = adapter.getItem(position);
            }
        });
        stateAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
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

        hint = getString(R.string.PhoneNumberEditText);
        phNoTextInputLayout = (TextInputLayout) view.findViewById(R.id.phNoTextInputLayout);
        phNoTextInputLayout.setTag(hint);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        phoneNumberEditText.setTag(phNoTextInputLayout);

        nextButton = (Button) view.findViewById(R.id.demographicsAddressNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to next
                ((DemographicsActivity) getActivity()).setCurrentItem(1, true);
            }
        });

        setFocusChangeListeners();
        setEditActionListeners();
    }

    private void setEditActionListeners() {
        address1EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    // check the validity
                    isAddressValid = checkAddress();
                    // maybe the user chose to edit this last. so check the other fields as well
                    // and enable 'Next' accordingly
                    nextButton.setEnabled(checkReadyForNext());
                    address2EditText.requestFocus();
                    return true;
                }
                return false;
            }
        });

        zipCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isZipValid = checkZipcode();
                    nextButton.setEnabled(checkReadyForNext());
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
                    isCityValid = checkCity();
                    nextButton.setEnabled(checkReadyForNext());
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
                    isStateValid = checkState();
                    nextButton.setEnabled(checkReadyForNext());
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
                    isPhoneValid = checkPhoneNumber();
                    nextButton.setEnabled(checkReadyForNext());
                    phoneNumberEditText.clearFocus();
                    Utility.hideSoftKeyboard(getActivity());
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
        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
                if (!b) { // when focus is lost, check for the validity and enable 'next' accordingly
                    isAddressValid = checkAddress();
                    nextButton.setEnabled(checkReadyForNext());
                }
            }
        });

        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
            }
        });

        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
                if (!b) {
                    isZipValid = checkZipcode();
                    nextButton.setEnabled(checkReadyForNext());
                }
            }
        });

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
                if (!b) {
                    isCityValid = checkCity();
                    nextButton.setEnabled(checkReadyForNext());
                }
            }
        });

        stateAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
                if (!b) {
                    isStateValid = checkState();
                    nextButton.setEnabled(checkReadyForNext());
                }
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Utility.handleHintChange(view, b);
                if (!b) {
                    isPhoneValid = checkPhoneNumber();
                    nextButton.setEnabled(checkReadyForNext());
                }
            }
        });
    }

    private boolean checkState() {
        if (TextUtils.isEmpty(stateAutoCompleteTextView.getText().toString().trim())) {
            stateTextInputLayout.setErrorEnabled(true);
            stateTextInputLayout.setError(getString(R.string.empty_state_error_messae));
            return false;
        } else if (state_var == null) {
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

    private boolean checkAddress() {
        if (TextUtils.isEmpty(address1EditText.getText().toString().trim())) {
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
            zipCodeTextInputLayout.setErrorEnabled(true);
            zipCodeTextInputLayout.setError(getString(R.string.empty_zip_code_error_messae));
            return false;
        }
        zipCodeTextInputLayout.setError(null);
        zipCodeTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkPhoneNumber() {
        if (TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())) {
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.empty_phone_number_error_messae));
            return false;
        } else if (!Utility.isValidPhoneNumber(phoneNumberEditText.getText().toString().trim())) {
            phNoTextInputLayout.setErrorEnabled(true);
            phNoTextInputLayout.setError(getString(R.string.incorrect_phone_number_error_messae));
            return false;
        }
        phNoTextInputLayout.setError(null);
        phNoTextInputLayout.setErrorEnabled(false);
        return true;
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.addressHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.addressSubHeading));
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), zipCodeEditText);
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), address1EditText);
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), address2EditText);
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), cityEditText);
        setProximaNovaSemiboldTypefaceEdittext(getActivity(), stateAutoCompleteTextView);
        setGothamRoundedMediumTypeface(getActivity(), nextButton);
    }

    private boolean checkReadyForNext() {
        return isAddressValid
                && isZipValid
                && isCityValid
                && isStateValid
                && isPhoneValid;
    }
}