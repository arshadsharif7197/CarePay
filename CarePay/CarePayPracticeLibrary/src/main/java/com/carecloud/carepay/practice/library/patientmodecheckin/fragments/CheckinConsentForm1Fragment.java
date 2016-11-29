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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.consentform.FormData;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;

import static com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity.SUBFLOW_CONSENT;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import java.util.Date;



/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinConsentForm1Fragment extends Fragment {

    Date date = new Date();
    private Button signConsentFormButton;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView contentTextView;
    private TextView dateTextView;
    private Button signButton;
    private IFragmentCallback fragmentCallback;
    private ScrollView consentFormScrollView;
    private ConsentFormLabelsDTO consentFormLabelsDTO;
    private ConsentFormDTO consentFormDTO;
    private LinearLayout mainContainer;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View clickListener) {
            if (clickListener.getId() == com.carecloud.carepaylibrary.R.id.signButton && fragmentCallback != null) {
                fragmentCallback.signButtonClicked();
            }
        }
    };
    private int formIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.consent_form_layout, container, false);
        View view = inflater.inflate(R.layout.consent_form_layout, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTv);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTv);
        contentTextView = (TextView) view.findViewById(R.id.contentTv);
        dateTextView = (TextView) view.findViewById(R.id.dateTv);
        consentFormScrollView = (ScrollView) view.findViewById(R.id.consentform_scrollView);
        signConsentFormButton = (Button) view.findViewById(R.id.signButton);
        mainContainer= (LinearLayout) view.findViewById(R.id.consenrform1_mainContainer);
        mainContainer.setPadding(10,50,10,0);

        setTypefaces(view);

        // set the index of the form
        formIndex = ((PatientModeCheckinActivity)getActivity()).getShowingForm().ordinal() + 1;

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
                Log.e("Error", " Exception");
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signConsentFormButton != null) {
            signConsentFormButton.setOnClickListener(clickListener);
        }

        FormData formData = (FormData) getArguments().getSerializable(CarePayConstants.FORM_DATA);

        titleTextView.setText(formData.getTitle());
        descriptionTextView.setText(formData.getDescription());
        contentTextView.setText(formData.getContent());
        dateTextView.setText(formData.getDate());
        signConsentFormButton.setText(formData.getButtonLabel());

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
                    signConsentFormButton.setEnabled(true);
                }
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.titleTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.descriptionTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.contentTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.dateTv));

    }

    @Override
    public void onStart() {
        super.onStart();
        ((PatientModeCheckinActivity)getActivity()).updateSection(
                new PatientModeCheckinActivity.FlowStateInfo(SUBFLOW_CONSENT,
                                                             formIndex,
                                                             PatientModeCheckinActivity.NUM_CONSENT_FORMS));
    }
}