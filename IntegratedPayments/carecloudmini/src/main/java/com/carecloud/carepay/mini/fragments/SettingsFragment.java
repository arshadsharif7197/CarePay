package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;

/**
 * Created by lmenendez on 10/16/17
 */

public class SettingsFragment extends ReviewInfoFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    protected void initProgressToolbar(View view, String titleString, int progress) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.registration_toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.settings_title));

        View exitButton = view.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBackPressed();
            }
        });

        View signOutButton = view.findViewById(R.id.button_signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finishAffinity();
            }
        });
    }

}
