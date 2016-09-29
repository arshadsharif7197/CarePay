package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsMoreDetailsFragment extends Fragment implements View.OnClickListener {
    View view;
    String[] getUpdateItemList;
    Button gotoCarePay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);
        getUpdateItemList = getResources().getStringArray(R.array.UpdatesMode);
        setTypefaces(view);

        gotoCarePay = (Button) view.findViewById(R.id.demographicsGoToCarePayButton);
        onClick(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        gotoCarePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((DemographicsActivity) getActivity()).confirmDemographicInformation(); // post the updates
                /*
                Intent appointmentIntent = new Intent(getActivity(), AppointmentsActivity.class);
                startActivity(appointmentIntent);
                getActivity().finish();*/
                Log.v(LOG_TAG, "demogr_check_point");
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsSubHeading));
        setGothamRoundedMediumTypeface(getActivity(),(Button)view.findViewById(R.id.demographicsGoToCarePayButton));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.placeHolderIconTextViewId));
    }

}
