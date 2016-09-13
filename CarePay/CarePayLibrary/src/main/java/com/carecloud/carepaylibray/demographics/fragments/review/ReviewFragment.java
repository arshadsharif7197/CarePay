package com.carecloud.carepaylibray.demographics.fragments.review;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

public class ReviewFragment  extends Fragment implements View.OnClickListener {

    View view;

    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insurance_review, container, false);

        initialiseUIFields();
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
    }

    @Override
    public void onClick(View view) {
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewtitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.healthInsuranceSubTitle));
    }

}
