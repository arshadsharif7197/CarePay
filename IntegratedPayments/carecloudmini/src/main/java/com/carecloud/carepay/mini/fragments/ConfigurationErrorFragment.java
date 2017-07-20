package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;

/**
 * Created by lmenendez on 7/19/17
 */

public class ConfigurationErrorFragment extends RegistrationFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_configuration_error, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(LoginFragment.class.getName(), 0);
            }
        });
    }
}
