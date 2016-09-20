package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

public class ReviewFragment  extends Fragment implements View.OnClickListener {

    View view;
    Button testButton;

    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insurance_review, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(getContext(), SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();*/
            }
        });

        initialiseUIFields();
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {

        testButton = (Button) view.findViewById(R.id.YesCorrectButton);
        testButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view ==testButton){
            Intent intent = new Intent(getActivity(), ConsentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewtitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewSubtitle));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.healthInsuranceSubTitle));


        //PN - extra Bold

        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel));


        //PN-Regular
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceEditText));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityEditText));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyEditText));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanNoEditText));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceCardNoEditText));



        //GNRM
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.YesCorrectButton));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.needUpdateButton));




    }

}
