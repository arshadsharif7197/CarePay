package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.patient.menu.PatientHelpActivity;
import com.carecloud.carepay.patient.tutorial.tutorial.TutorialActivity;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

public class HelpFragment extends BaseFragment {

    private FragmentActivityInterface callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof DemographicsSettingsFragmentListener) {
                callback = (DemographicsSettingsFragmentListener) context;
            } else if (context instanceof PatientHelpActivity) {
                callback = (PatientHelpActivity) context;
            }
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
        initializeTelehealthButton(view);
        initializeTosButton(view);
        initializePrivacyButton(view);
        initializePlayAgainButton(view);
    }

    private void initializeToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("help_label"));
        callback.setToolbar(toolbar);
    }

    private void initializeSupportButton(View view) {
        View textView = view.findViewById(R.id.supportTextView);
        textView.setOnClickListener(view1 -> callback.addFragment(SupportFragment.newInstance(), true));
    }

    private void initializeFaqButton(View view) {
        View textView = view.findViewById(R.id.faqTextView);
        textView.setOnClickListener(view1 -> openUrl(Label.getLabel("support_url_faq")));
    }

    private void initializeTelehealthButton(View view) {
        View telehealthBtn = view.findViewById(R.id.telehealthBtn);
        telehealthBtn.setOnClickListener(view1 -> openUrl(Label.getLabel("support_url_telehealth"))); //"https://www.carecloud.com/telehealth-patient-breeze"
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
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
