package com.carecloud.carepaylibray.demographics.fragments.demographicReview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepaylibrary.R;

public class HealthInsuranceReviewFragment extends Fragment implements View.OnClickListener {

    Button buttonAddHealthInsuranceInfo;
    View view;

    public static HealthInsuranceReviewFragment newInstance() {
        return new HealthInsuranceReviewFragment();
    }

    public HealthInsuranceReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);

        initialiseUIFields();
        return view;
    }

    private void initialiseUIFields() {
        buttonAddHealthInsuranceInfo = (Button) view.findViewById(R.id.buttonAddHealthInsuranceInfo);
        buttonAddHealthInsuranceInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddHealthInsuranceInfo) {
            openNewFragment();
        }
    }

    private void openNewFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.addToBackStack("HealthInsuranceReviewFragment -> ReviewFragment");
        transaction.commit();
    }
}
