package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.tutorial.tutorial.TutorialActivity;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

public class HelpFragment extends BaseFragment {

    private DemographicsSettingsFragmentListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HelpFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        initializeToolbar(view);
        initializeSupportButton(view);
        initializeFaqButton(view);
        initializeTosButton(view);
        initializePrivacyButton(view);
        initializePlayAgainButton(view);
    }

    private void initializeToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("help_label"));
    }

    private void initializeSupportButton(View view) {
        View textView = view.findViewById(R.id.supportTextView);
        textView.setOnClickListener(view1 -> callback.addFragment(SupportFragment.newInstance(), true));
    }

    private void initializeFaqButton(View view) {
        View textView = view.findViewById(R.id.faqTextView);
        textView.setOnClickListener(view1 -> openUrl(Label.getLabel("support_url_faq")));
    }

    private void initializeTosButton(View view) {
        View textView = view.findViewById(R.id.tosTextView);
        textView.setOnClickListener(view1 -> openUrl(Label.getLabel("support_url_tos")));
    }

    private void initializePrivacyButton(View view) {
        View textView = view.findViewById(R.id.privacyTextView);
        textView.setOnClickListener(view1 -> openUrl(Label.getLabel("support_url_privacy")));
    }

    private void initializePlayAgainButton(View view) {
        View textView = view.findViewById(R.id.playAgainTextView);
        textView.setOnClickListener(view1 -> startActivity(new Intent(getContext(), TutorialActivity.class)));
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
