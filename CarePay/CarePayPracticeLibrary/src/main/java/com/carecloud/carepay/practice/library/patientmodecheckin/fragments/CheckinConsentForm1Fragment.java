package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.ConsentForm1Fragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.google.gson.Gson;

import java.util.Date;


/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinConsentForm1Fragment extends Fragment {

    private Button signConsentForm;
    Date date = new Date();
    private CarePayTextView titleTextView;
    private CarePayTextView descriptionTextView;
    private CarePayTextView contentTextView;
    private CarePayTextView dateTextView;
    private CarePayTextView agreeTextview;
    private CarePayTextView minorInfoTextview;
    private CarePayTextView minorFirstNameTextview;
    private CarePayTextView minorLastNameTextview;
    private CarePayTextView minorDobTextview;
    private TextView minorGenderTextview;
    private Button signButton;
    private IFragmentCallback fragmentCallback;
    private ScrollView consentFormScrollView;
    private ConsentFormLabelsDTO consentFormLabelsDTO;
    private ConsentFormDTO consentFormDTO;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View clickListener) {
            if (clickListener.getId() == R.id.signButton && fragmentCallback != null) {
                fragmentCallback.signButtonClicked();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_consent_form1, container, false);
        initViews(view);
        signConsentForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinConsentForm2Fragment fragment = new CheckinConsentForm2Fragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).changeCounterOfForm(PatientModeCheckinActivity.SUBFLOW_CONSENT, 2,
                                                                                PatientModeCheckinActivity.NUM_CONSENT_FORMS);

            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = null;

        if (context instanceof PatientModeCheckinActivity) {
            activity = (Activity) context;

            try {
                fragmentCallback = (IFragmentCallback) activity;
            } catch (Exception e) {
                Log.d(ConsentForm1Fragment.class.getSimpleName(), e.getMessage(), e);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signButton != null) {
            signButton.setOnClickListener(clickListener);
        }
        String workflowDTO  = getArguments().getString(CarePayConstants.ATTR_RESPONSE);
        if(workflowDTO!=null){
            Gson gson = new Gson();
            ConsentFormDTO consentFormDTO = gson.fromJson(workflowDTO, ConsentFormDTO.class);
            titleTextView.setText(consentFormDTO.getMetadata().getLabel().getAuthorizationFormTitle());
            descriptionTextView.setText(consentFormDTO.getMetadata().getLabel().getConsentReadCarefullyWarning());
            contentTextView.setText(consentFormDTO.getMetadata().getLabel().getAuthorizationGrantText());
            agreeTextview.setText(consentFormDTO.getMetadata().getLabel().getAuthorizationLegalText());
            minorInfoTextview.setText(consentFormDTO.getMetadata().getLabel().getMinorsInformation());
            minorFirstNameTextview.setText(consentFormDTO.getMetadata().getLabel().getLegalFirstNameLabel());
            minorLastNameTextview.setText(consentFormDTO.getMetadata().getLabel().getLegalLastNameLabel());
            minorDobTextview.setText(consentFormDTO.getMetadata().getLabel().getSelectDateLabel());
            minorGenderTextview.setText(consentFormDTO.getMetadata().getLabel().getSelectGenderLabel());

            signButton.setText(consentFormDTO.getMetadata().getLabel().getSignConsentForMedicareTitle());

        }

        // enable next button on scrolling all the way to the bottom
        setEnableNextButtonOnFullScroll();
    }

    private void setEnableNextButtonOnFullScroll() {
        // enable next button on scrolling all the way to the bottom
        consentFormScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = consentFormScrollView.getChildAt(consentFormScrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (consentFormScrollView.getHeight() + consentFormScrollView.getScrollY()));

                if (diff == 0) {
                    signButton.setEnabled(true);
                }
            }
        });
    }

    private void initViews(View view){
        titleTextView = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.titleTv);
        descriptionTextView = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.descriptionTv);
        contentTextView = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.contentTv);
        dateTextView = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.dateTv);
        agreeTextview = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.agreeTextview);
        minorInfoTextview = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.minor_information);
        minorFirstNameTextview = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.minor_first_name);
        minorLastNameTextview = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.minor_last_name);
        minorDobTextview = (CarePayTextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.dob_ET);
        minorGenderTextview = (TextView) view.findViewById(com.carecloud.carepay.practice.library.R.id.minor_gender);
        consentFormScrollView = (ScrollView) view.findViewById(com.carecloud.carepay.practice.library.R.id.consentform_scrollView);
        signButton = (Button) view.findViewById(com.carecloud.carepaylibrary.R.id.signButton);
        signButton.setEnabled(false);

        signConsentForm = (Button) view.findViewById(R.id.signButton);
    }
}

