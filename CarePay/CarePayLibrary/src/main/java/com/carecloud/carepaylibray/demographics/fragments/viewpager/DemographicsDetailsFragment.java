package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.adapters.DemographicsDetailsAllergiesAdapter;
import com.carecloud.carepaylibray.demographics.adapters.DemographicsDetailsMedicationsAdapter;
import com.carecloud.carepaylibray.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Implements demographics details screen
 */
public class DemographicsDetailsFragment extends Fragment
        implements View.OnClickListener,
                   DocumentScannerFragment.NextAddRemoveStatusModifier {

    private View                             view;
    private String[]                         raceArray;
    private String[]                         ethnicityArray;
    private int                              selectedArray;
    private TextView                         raceTextView;
    private TextView                         ethnicityTextView;
    private TextView                         addAnotherAllergyTextView;
    private TextView                         addAnotherMedTextView;
    private Button                           nextButton;
    private DemographicPersDetailsPayloadDTO model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        initialiseUIFields();

        raceArray = getResources().getStringArray(R.array.Race);
        ethnicityArray = getResources().getStringArray(R.array.Ethnicity);

        // set up the allergies recycler view
        final RecyclerView allergiesRecyclerView = (RecyclerView) view.findViewById(R.id.demogrDetailsAllergiesRecView);
        allergiesRecyclerView.setHasFixedSize(true);
        allergiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // for now generate dummy data
        List<DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO> allergies = getAllergies();
        allergiesRecyclerView.setAdapter(new DemographicsDetailsAllergiesAdapter(allergies));

        // add click listener for 'Add Another' (allergy)
        addAnotherAllergyTextView = (TextView) view.findViewById(R.id.demogrDetailsAddAllergyClickable);
        addAnotherAllergyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsDetailsAllergiesAdapter)allergiesRecyclerView.getAdapter()) // for now add the same dummy allergy
                        .addAtFront(new DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO("Category C",
                                                                                              "Allergy 8",
                                                                                              "Severe",
                                                                                              "Reaction #22"));
            }
        });

        // set up the medications recycler view
        final RecyclerView medicRecyclerView = (RecyclerView) view.findViewById(R.id.demogrDetailsMedRecView);
        medicRecyclerView.setHasFixedSize(true);
        medicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // dummy meds for now
        List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> meds = createMeds();
        medicRecyclerView.setAdapter(new DemographicsDetailsMedicationsAdapter(meds));

        addAnotherMedTextView = (TextView) view.findViewById(R.id.demogrDetailsAddAnotherMedClickable);
        addAnotherMedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsDetailsMedicationsAdapter)medicRecyclerView.getAdapter())
                        .addAtFront(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication Z122",
                                                                                                   "11 mg"));
            }
        });

        return view;
    }

    private List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> createMeds() {
        List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> meds = new ArrayList<>();
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 111", "33mg"));
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 222", "333mg"));
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 333", "23mg"));
        return meds;
    }

    private List<DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO> getAllergies() {
        List<DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO> allergies = new ArrayList<>();
        allergies.add(new DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO("Category Alpha",
                                                                                "Allergy A",
                                                                                "Mild",
                                                                                "Reaction #01"));
        allergies.add(new DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO("Category Alpha",
                                                                                "Allergy B",
                                                                                "Benign",
                                                                                "Reaction #001"));
        return allergies;
    }

    private void initialiseUIFields() {
        // add capture picture fragment
        FragmentManager fm = getChildFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        ProfilePictureFragment fragment
                = (ProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new ProfilePictureFragment();
            fragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsAddressPicCapturer, fragment, tag)
                .commit();

        // get handlers to the other views
        raceTextView = (TextView) view.findViewById(R.id.raceListTextView);
        raceTextView.setOnClickListener(this);
        ethnicityTextView = (TextView) view.findViewById(R.id.ethnicityListTextView);
        ethnicityTextView.setOnClickListener(this);
        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        nextButton.setOnClickListener(this);
//        enableNextButton(false); // 'next' is initially disabled // TODO: 9/27/2016 uncomment

        setTypefaces(view);

        populateViewsFromModel();
    }

    public DemographicPersDetailsPayloadDTO getModel() {
        DemographicPayloadDTO payload
                = ((DemographicsActivity) getActivity()).getDemographicInfoPayloadModel();
        if (payload != null) {
            model = payload.getPersonalDetails();
        }
        return model;
    }

    private void populateViewsFromModel() {
        getModel();

        if (model != null) {
            raceTextView.setText(model.getPrimaryRace());
            ethnicityTextView.setText(model.getEthnicity());
            String pictureByteStream = model.getProfilePhoto();
            setPictureFromByteStream(pictureByteStream);
        } else {
            Log.v(LOG_TAG, "demographics details: views populated with defaults");
            model = new DemographicPersDetailsPayloadDTO();
        }
    }

    private void setPictureFromByteStream(String pictureByteStream) {
        // TODO: 9/28/2016 implement
    }

    @Override
    public void onClick(View view) {
        if (view == raceTextView) {
            selectedArray = 1;
            showAlertDialogWithListview(raceArray, "Select Race");
        } else if (view == ethnicityTextView) {
            selectedArray = 2;
            showAlertDialogWithListview(ethnicityArray, "Select Ethnicity");
        } else if (view == nextButton) {
            nextbuttonClick();
        }
    }

    private void nextbuttonClick() {
        // update the model with values from UI
        model.setPrimaryRace(raceTextView.getText().toString());
        model.setEthnicity(ethnicityTextView.getText().toString());

        ((DemographicsActivity) getActivity()).setDetailsModel(model); // save the updated model in the activity

        // move to next page
        ((DemographicsActivity) getActivity()).setCurrentItem(2, true);
    }

    private void showAlertDialogWithListview(final String[] raceArray, String title) {
        Log.e("raceArray==", Arrays.toString(raceArray));
        Log.e("raceArray 23==", Arrays.asList(raceArray).toString());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater
                .from(getActivity()).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(raceArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedArray) {
                    case 1:
                        String race = raceArray[position];
                        raceTextView.setText(race);
                        model.setPrimaryRace(race);
                        break;
                    case 2:
                        String ethnicity = ethnicityArray[position];
                        ethnicityTextView.setText(ethnicity);
                        model.setEthnicity(ethnicity);
                        break;
                }
                alert.dismiss();
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsSubHeading));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListTextView));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListTextView));

        setGothamRoundedMediumTypeface(getActivity(), nextButton);

    }

    @Override
    public void showAddCardButton(boolean isVisible) {

    }

    @Override
    public void enableNextButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void scrollToBottom() {

    }
}