package com.carecloud.carepaylibray.fragments.demographics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.GenericEditsFragment;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsAddressFragment extends GenericEditsFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demogr_address, container, false);

        addEditForBindingWithKeyboard((EditText) view.findViewById(R.id.demogr_docs_street));
        addEditForBindingWithKeyboard((EditText) view.findViewById(R.id.demogr_docs_city));

        return view;
    }
}