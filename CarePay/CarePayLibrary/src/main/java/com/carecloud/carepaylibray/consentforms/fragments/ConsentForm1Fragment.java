package com.carecloud.carepaylibray.consentforms.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.ConsentActivity;
import com.carecloud.carepaylibray.consentforms.FormData;
import com.carecloud.carepaylibray.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibray.constants.CarePayConstants;

import java.util.Date;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;


public class ConsentForm1Fragment extends Fragment {

    private TextView titleTextView, descriptionTextView, contentTextView, dateTextView;
    private Button            signButton;
    private IFragmentCallback fragmentCallback;
    private ScrollView        consentFormScrollView;
    Date date = new Date();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent_form_layout, container, false);

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

        Activity a = null;

        if (context instanceof ConsentActivity) {
            a = (Activity) context;

            try {
                fragmentCallback = (IFragmentCallback) a;
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signButton != null) {
            signButton.setOnClickListener(clickListener);
        }

        FormData formData = (FormData) getArguments().getSerializable(CarePayConstants.FORM_DATA);

        titleTextView.setText(formData.getTitle());
        descriptionTextView.setText(formData.getDescription());
        contentTextView.setText(formData.getContent());
        dateTextView.setText(formData.getDate());

        // enable next button on scrolling all the way to the bottom
        setEnableNextButtonOnFullScroll();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.signButton) {
                if (fragmentCallback != null)
                    fragmentCallback.signButtonClicked();
            }
        }
    };

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
