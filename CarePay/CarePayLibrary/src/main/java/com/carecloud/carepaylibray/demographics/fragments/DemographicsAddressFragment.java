package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;
import com.carecloud.carepaylibray.myinterface.CallbackInterface;
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment implements CallbackInterface {

    View view;
    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText cityEditText;
    AutoCompleteTextView autoCompleteTextView;
    String state_var=null;
    Context mainContext;

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof DemographicsActivity) {
            mainContext =  getActivity();
            ((DemographicsActivity)mainContext).setAdapterViewItemClickListener(DemographicsAddressFragment.this);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_address, container, false);
        initialiseUIFields();




        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_state_item,R.id.text1, states);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                state_var = null;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        phoneNumberEditText = (EditText) view.findViewById(R.id.phNoEditText);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
    }



    private boolean checkState() {
        if(TextUtils.isEmpty(autoCompleteTextView.getText().toString().trim())){
            autoCompleteTextView.requestFocus();
            autoCompleteTextView.setError(getString(R.string.empty_state_error_messae));
            return false;
        }else if(state_var==null){
            autoCompleteTextView.requestFocus();
            autoCompleteTextView.setError(getString(R.string.select_state_error_messae));
            return false;
        }
        return true;
    }

    private boolean checkCity() {
        if(TextUtils.isEmpty(cityEditText.getText().toString().trim())){
            cityEditText.requestFocus();
            cityEditText.setError(getString(R.string.empty_city_error_messae));
            return false;
        }else if(cityEditText.getText().toString().trim().length()<3){
            cityEditText.requestFocus();
            cityEditText.setError(getString(R.string.minimum_char_in_city_error_messae));
            return false;
        }
        return true;
    }

    private boolean checkAddress() {
        if(TextUtils.isEmpty(address1EditText.getText().toString().trim())){
            address1EditText.requestFocus();
            address1EditText.setError(getString(R.string.empty_address_error_messae));
            return false;
        }
        return true;
    }

    private boolean checkZipcode() {
        if(TextUtils.isEmpty(zipCodeEditText.getText().toString().trim())){
            zipCodeEditText.requestFocus();
            zipCodeEditText.setError(getString(R.string.empty_zip_code_error_messae));
            return false;
        }
        return true;
    }

    private boolean checkPhonenumber() {
        if(TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())){
            phoneNumberEditText.requestFocus();
            phoneNumberEditText.setError(getString(R.string.empty_phone_number_error_messae));
            return false;
        }else if(!Utility.isValidPhoneNumber(phoneNumberEditText.getText().toString().trim())){
            phoneNumberEditText.requestFocus();
            phoneNumberEditText.setError(getString(R.string.incorrect_phone_number_error_messae));
            return false;
        }
        return true;
    }

    @Override
    public void nextbuttonCallback(int nextFramentPosition, boolean smoothScroll) {
        Log.e("getCurrentItem()",""+nextFramentPosition);
        if(checkPhonenumber()){
            if(checkZipcode()){
                if(checkAddress()){
                    if(checkCity()){
                        if(checkState()){
                            ((DemographicsActivity)mainContext).setCurrentItem(nextFramentPosition,  smoothScroll);
                        }
                    }
                }
            }
        }

    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.addressHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.addressSubHeading));

    }
}