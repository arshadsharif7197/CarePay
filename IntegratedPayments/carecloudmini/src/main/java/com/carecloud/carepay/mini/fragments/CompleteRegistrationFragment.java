package com.carecloud.carepay.mini.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.activities.WelcomeActivity;

/**
 * Created by lmenendez on 7/25/17
 */

public class CompleteRegistrationFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_complete, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View goButton = view.findViewById(R.id.button_go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchWelcomeActivity();
            }
        });
    }

    private void launchWelcomeActivity(){
        Intent main = new Intent(getContext(), WelcomeActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
    }

}
