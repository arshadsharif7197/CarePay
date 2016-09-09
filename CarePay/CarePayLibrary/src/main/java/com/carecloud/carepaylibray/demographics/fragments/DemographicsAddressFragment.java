package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SignupFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {

    private static final String[] states = new String[]{
            "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demographics_address, container, false);
        initialiseUIFields();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, states);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView)
                view.findViewById(R.id.autoTextCompleteStates);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        return view;
    }

    private void initialiseUIFields() {

    }




}