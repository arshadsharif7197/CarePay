package com.carecloud.carepay.practice.library.patientmodecheckin.consentform;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;


import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IFragmentCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.google.gson.Gson;

import java.util.Date;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;


public class ConsentForm1Fragment extends Fragment {

    Date date = new Date();
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView contentTextView;
    private TextView dateTextView;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent_form_layout, container, false);

      //  consentFormDTO=((PatientModeCheckinActivity)getActivity()).getConsentFormDTO();
      //  String workflowDTO = savedInstanceState.getString("Test");
       // Gson gson = new Gson();
        //ConsentFormDTO consentFormDTO = gson.fromJson(workflowDTO, ConsentFormDTO.class);

        //Gson gson = new Gson();
        //ConsentFormDTO consentFormDTO = gson.fromJson(workflowDTO.toString(), ConsentFormDTO.class);
        //consentFormDTO.getMetadata().getLabel().getConfirmSignatureButton();
//        Log.d("Testing",consentFormDTO.getState());
        titleTextView = (TextView) view.findViewById(R.id.titleTv);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTv);
        contentTextView = (TextView) view.findViewById(R.id.contentTv);
        dateTextView = (TextView) view.findViewById(R.id.dateTv);
        consentFormScrollView = (ScrollView) view.findViewById(R.id.consentform_scrollView);
        signButton = (Button) view.findViewById(R.id.signButton);
        signButton.setEnabled(false);

        setTypefaces(view);
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
        String workflowDTO  = getArguments().getString("Test");
        if(workflowDTO!=null){
            Gson gson = new Gson();
            ConsentFormDTO consentFormDTO = gson.fromJson(workflowDTO, ConsentFormDTO.class);
        }

     /*   FormData formData = (FormData) getArguments().getSerializable(CarePayConstants.FORM_DATA);

        //     titleTextView.setText(consentFormLabelsDTO.getConsentForMedicareTitle());
        titleTextView.setText(formData.getTitle());
        descriptionTextView.setText(formData.getDescription());
        contentTextView.setText(formData.getContent());
        dateTextView.setText(formData.getDate());
        signButton.setText(formData.getButtonLabel());*/

        // enable next button on scrolling all the way to the bottom
        setEnableNextButtonOnFullScroll();
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.titleTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.descriptionTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.contentTv));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.dateTv));

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
}
