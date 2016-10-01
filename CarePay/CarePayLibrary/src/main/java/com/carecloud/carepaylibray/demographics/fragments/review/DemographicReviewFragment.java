package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;

import static com.carecloud.carepaylibrary.R.id.stateAutoCompleteTextView;
import static com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity.model;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class    DemographicReviewFragment extends Fragment implements View.OnClickListener  {

    Button buttonAddDemographicInfo;
    View view;

    String[] raceDataArray;
    String[] ethnicityDataArray;
    String[] prefferedLangauge;
    String[] genderSelect;
    int selectedDataArray;
    TextView raceDataTextView, ethnicityDataTextView,selectGender,selectlangauge;
    private TextInputLayout firstNameInputLayout;
    private TextInputLayout middleNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout phNoTextInputLayout;
    private TextInputLayout address1TextInputLayout;
    private TextInputLayout address2TextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipCodeTextInputLayout;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText emailEditText;
    private EditText dobEditText;
    private EditText stateEditText;

    private EditText cityEditText;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;

    public static DemographicReviewFragment newInstance()  {
        return new DemographicReviewFragment();
    }

    public DemographicReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
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
        raceDataArray = getResources().getStringArray(R.array.Race);
        ethnicityDataArray = getResources().getStringArray(R.array.Ethnicity);
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        phoneNumberEditText=(EditText)view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        firstNameText=(EditText)view.findViewById(R.id.reviewdemogrFirstNameEdit);
        firstNameText=(EditText)view.findViewById(R.id.reviewdemogrLastNameEdit);
        firstNameText=(EditText)view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        emailEditText=(EditText)view.findViewById(R.id.reviewdemogrEmailEdit);
        dobEditText=(EditText)view.findViewById(R.id.revewidemogrDOBEdit);
        address1EditText=(EditText)view.findViewById(R.id.addressEditTextId);
        address2EditText=(EditText)view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText=(EditText)view.findViewById(R.id.zipCodeId);
        cityEditText=(EditText)view.findViewById(R.id.cityId);
        stateEditText=(EditText)view.findViewById(R.id.stateAutoCompleteTextView);

        buttonAddDemographicInfo = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        buttonAddDemographicInfo.setOnClickListener(this);

        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceDataTextView.setOnClickListener(this);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityDataTextView.setOnClickListener(this);
        selectGender=(TextView)view.findViewById(R.id.choose_genderTextView);
        selectGender.setOnClickListener(this);
        selectlangauge=(TextView)view.findViewById(R.id.preferredLanguageListTextView);
        selectlangauge.setOnClickListener(this);



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
    private void openNewFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, DemographicReviewFragment.class.getName());
        transaction.addToBackStack("DemographicReviewFragment -> ReviewFragment");
        transaction.commit();
    }
    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsReviewHeading));
      //  setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsReviewSubHeading));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListDataTextView));


        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListDataTextView));
    }
}
