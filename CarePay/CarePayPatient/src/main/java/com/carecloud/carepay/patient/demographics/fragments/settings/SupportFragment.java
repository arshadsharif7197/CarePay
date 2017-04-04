package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

public class SupportFragment extends BaseFragment {

    public static final String EMAIL = "help@gobreeze.com";
    public static final String SUBJECT = "Android Support";
    public static final String CHOOSE_LABEL = "Choose an Email client :";
    public static final String PHONE = "tel:18552044075";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("support_label"));

        initializeEmailButton(view);
        initializePhoneButton(view);
    }

    private void initializeEmailButton(View view) {
        Button button = (Button) view.findViewById(R.id.email_us_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", EMAIL, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
                startActivity(Intent.createChooser(intent, CHOOSE_LABEL));
            }
        });
    }

    private void initializePhoneButton(View view) {
        Button button = (Button) view.findViewById(R.id.call_us_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(PHONE));
                startActivity(Intent.createChooser(intent, CHOOSE_LABEL));
            }
        });
    }
}
