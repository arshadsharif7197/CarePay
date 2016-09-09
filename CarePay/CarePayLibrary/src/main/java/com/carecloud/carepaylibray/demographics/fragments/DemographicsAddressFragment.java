package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {



    private static final String[] states = new String[] {
          "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "DC", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY",
            "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
            "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, states);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                view.findViewById(R.id.autoTextCompleteStates);
        textView.setThreshold(1);
        textView.setAdapter(adapter);

return view;
}




}