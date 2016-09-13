package com.carecloud.carepaylibray.demographics.fragments.demographicReview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

public class DemographicReviewFragment extends Fragment implements View.OnClickListener {

    Button buttonAddDemographicInfo;
    View view;

    String[] raceDataArray;
    String[] ethnicityDataArray;
    int selectedDataArray;
    TextView raceDataTextView, ethnicityDataTextView;

    public static DemographicReviewFragment newInstance() {
        return new DemographicReviewFragment();
    }

    public DemographicReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);

        initialiseUIFields();
        raceDataArray = getResources().getStringArray(R.array.Race);
        ethnicityDataArray = getResources().getStringArray(R.array.Ethnicity);
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        buttonAddDemographicInfo = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        buttonAddDemographicInfo.setOnClickListener(this);
        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceDataTextView.setOnClickListener(this);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityDataTextView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddDemographicInfo) {
            openNewFragment();
        } else if (view == raceDataTextView) {
            selectedDataArray = 1;
            showAlertDialogWithListview(raceDataArray, "Select Race");
        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 2;
            showAlertDialogWithListview(ethnicityDataArray, "Select Ethnicity");

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


    private void showAlertDialogWithListview(final String[] raceArray, String title) {
        Log.e("raceArray==", raceArray.toString());
        Log.e("raceArray 23==", Arrays.asList(raceArray).toString());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(raceArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedDataArray) {
                    case 1:
                        raceDataTextView.setText(raceDataArray[position]);
                        break;
                    case 2:
                        ethnicityDataTextView.setText(ethnicityDataArray[position]);
                        break;

                }
                alert.dismiss();
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsReviewHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsReviewSubHeading));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListDataTextView));


        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListDataTextView));
    }
}
