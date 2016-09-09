package com.carecloud.carepaylibray.demographics.fragments.demographicReview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;

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
        return view;
    }

    private void initialiseUIFields() {
    }

    @Override
    public void onClick(View view) {
    }

}
