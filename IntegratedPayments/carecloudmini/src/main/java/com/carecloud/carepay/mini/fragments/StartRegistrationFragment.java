package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.activities.FullScreenActivity;

/**
 * Created by lmenendez on 6/23/17
 */

public class StartRegistrationFragment extends RegistrationFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_start, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View startButton = view.findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });

        View exitButton = view.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FullScreenActivity) getActivity()).toggleCustomerMode();
                getActivity().finish();
            }
        });
    }

    private void startRegistration(){
        callback.replaceFragment(new LoginFragment(), true);
    }


}
