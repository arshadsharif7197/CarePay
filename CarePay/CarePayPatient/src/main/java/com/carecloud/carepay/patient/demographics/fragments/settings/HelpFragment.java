package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

public class HelpFragment extends BaseFragment {

    public static final String URL_FAQ = "https://help.gobreeze.com";
    public static final String URL_TOS = "https://carecloud.app.box.com/v/breezetou";
    public static final String URL_PRIVACY = "https://carecloud.app.box.com/v/breezeprivacy";

    private HelpFragmentListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (HelpFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HelpFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initializeToolbar(view);
        initializeSupportButton(view);
        initializeFaqButton(view);
        initializeTosButton(view);
        initializePrivacyButton(view);
    }

    private void initializeToolbar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("help_label"));
    }

    private void initializeSupportButton(View view) {
        TextView textView = (TextView) view.findViewById(R.id.supportTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.showSupportFragment();
                }
            }
        });
    }

    private void initializeFaqButton(View view) {
        TextView textView = (TextView) view.findViewById(R.id.faqTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(URL_FAQ);
            }
        });
    }

    private void initializeTosButton(View view) {
        TextView textView = (TextView) view.findViewById(R.id.tosTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(URL_TOS);
            }
        });
    }

    private void initializePrivacyButton(View view) {
        TextView textView = (TextView) view.findViewById(R.id.privacyTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(URL_PRIVACY);
            }
        });
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public interface HelpFragmentListener {
        void showSupportFragment();
    }
}
