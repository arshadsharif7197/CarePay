package com.carecloud.carepaylibray.demographics.fragments.demographicReview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepaylibrary.R;

public class DemographicReviewFragment extends Fragment implements View.OnClickListener {

    Button buttonAddDemographicInfo;
    View     view;
    public static DemographicReviewFragment newInstance() {
        return new DemographicReviewFragment();
    }

    public DemographicReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);
        
        initialiseUIFields();
        return view;
    }

    private void initialiseUIFields() {
        buttonAddDemographicInfo = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        buttonAddDemographicInfo.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if(view==buttonAddDemographicInfo){
            openNewFragment();
        }
    }

    private void openNewFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = HealthInsuranceReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.addToBackStack("DemographicReviewFragment -> HealthInsuranceReviewFragment");

        transaction.commit();
    }
}
