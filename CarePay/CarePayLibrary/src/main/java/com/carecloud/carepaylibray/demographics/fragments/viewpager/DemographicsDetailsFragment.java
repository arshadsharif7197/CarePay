package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.adapters.DemographicsDetailsAllergiesAdapter;
import com.carecloud.carepaylibray.demographics.adapters.DemographicsDetailsMedicationsAdapter;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by lsoco_user on 9/2/2016.
 * Implements demographics details screen
 */
public class DemographicsDetailsFragment extends Fragment
        implements View.OnClickListener,
                   DocumentScannerFragment.NextAddRemoveStatusModifier {

    private View     view;
    private String[] raceArray;
    private String[] ethnicityArray;
    private String[] genderArray;
    private int      selectedArray;

    private TextView        raceTextView;
    private TextView        ethnicityTextView;
    private TextView        genderTextView;
    private EditText        dobEdit;
    private TextView        addUnlistedAllergyTextView;
    private TextView        addUnlistedMedTextView;
    private TextView        addAnotherAllergyTextView;
    private TextView        addAnotherMedTextView;
    private Button          nextButton;
    private RecyclerView    allergiesRecyclerView;
    private RecyclerView    medicRecyclerView;
    private TextInputLayout dobInputText;

    private DemographicPersDetailsPayloadDTO        model;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        // get data
        raceArray = getResources().getStringArray(R.array.Race);
        ethnicityArray = getResources().getStringArray(R.array.Ethnicity);
        genderArray = getResources().getStringArray(R.array.Gender);
        model = ((DemographicsActivity) getActivity()).getDetailsModel();

        // init view references
        initialiseUIFields();

        // set the fonts
        setTypefaces(view);

        // set data in view
        populateViewsFromModel();

        return view;
    }

    private void setupEdit() {
        dobInputText = (TextInputLayout) view.findViewById(R.id.demogrDetailsDobInputText);
        dobEdit = (EditText) view.findViewById(R.id.demogrDetailsDobEdit);
        dobInputText.setTag(getString(R.string.demoDobLabel));
        dobEdit.setTag(dobInputText);

        dobEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });

        dobEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dobEdit.setSelection(dobEdit.length());
            }
        });

        dobEdit.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                prevLen = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dob = dobEdit.getText().toString();
                if (!StringUtil.isNullOrEmpty(dob)) {
                    dobInputText.setErrorEnabled(false);
                    dobInputText.setError(null);
                }
                // auto-format to mm/dd/yyyy
                StringUtil.autoFormatDateOfBirth(editable, prevLen);
            }
        });

        dobEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT || inputType == EditorInfo.IME_ACTION_DONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    dobEdit.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean isDateOfBirthValid() {
        String dob = dobEdit.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateMMDDYYYY(dob);
            dobInputText.setErrorEnabled(!isValid);
            dobInputText.setError(isValid ? null : getString(R.string.invalid_date_of_birth_format));
            return isValid;
        }
        return true;
    }

    private void setupRecyclerViews() {
        // set up the allergies recycler view
        allergiesRecyclerView = (RecyclerView) view.findViewById(R.id.demogrDetailsAllergiesRecView);
        allergiesRecyclerView.setHasFixedSize(true);
        allergiesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // for now generate dummy data
        List<DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO> allergies = getAllergies();
        allergiesRecyclerView.setAdapter(new DemographicsDetailsAllergiesAdapter(allergies));

        // set up the medications recycler view
        medicRecyclerView = (RecyclerView) view.findViewById(R.id.demogrDetailsMedRecView);
        medicRecyclerView.setHasFixedSize(true);
        medicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // dummy meds for now
        List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> meds = createMeds();
        medicRecyclerView.setAdapter(new DemographicsDetailsMedicationsAdapter(meds));
    }

    // for test
    private List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> createMeds() {
        List<DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO> meds = new ArrayList<>();
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 111", "33mg"));
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 222", "333mg"));
        meds.add(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication 333", "23mg"));
        return meds;
    }

    // for test
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
        // init click-ables and dob edit
        raceTextView = (TextView) view.findViewById(R.id.demogrDetailsRaceListTextView);
        raceTextView.setOnClickListener(this);
        ethnicityTextView = (TextView) view.findViewById(R.id.demogrDetailsEthnicityListTextView);
        ethnicityTextView.setOnClickListener(this);
        genderTextView = (TextView) view.findViewById(R.id.demogrDetailsGenderClickable);
        genderTextView.setOnClickListener(this);
        setupEdit();
        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        nextButton.setOnClickListener(this);

        addUnlistedAllergyTextView = (TextView) view.findViewById(R.id.demogrDetailsAllergyAddUnlisted);
        addUnlistedAllergyTextView.setOnClickListener(this);

        addUnlistedMedTextView = (TextView) view.findViewById(R.id.demogrDetailsMedAddUnlisted);
        addUnlistedMedTextView.setOnClickListener(this);

        // rec views
        setupRecyclerViews();

        // add click listener for 'Add Another' (allergy)
        addAnotherAllergyTextView = (TextView) view.findViewById(R.id.demogrDetailsAddAllergyClickable);
        addAnotherAllergyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsDetailsAllergiesAdapter) allergiesRecyclerView.getAdapter()) // for now add the same dummy allergy
                        .addAtFront(new DemographicsDetailsAllergiesAdapter.AllergyPayloadDTO("Category C",
                                                                                              "Allergy 8",
                                                                                              "Severe",
                                                                                              "Reaction #22"));
            }
        });

        addAnotherMedTextView = (TextView) view.findViewById(R.id.demogrDetailsAddAnotherMedClickable);
        addAnotherMedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsDetailsMedicationsAdapter) medicRecyclerView.getAdapter())
                        .addAtFront(new DemographicsDetailsMedicationsAdapter.MedicationPayloadDTO("Medication Z122",
                                                                                                   "11 mg"));
            }
        });

        // add capture picture fragment
        FragmentManager fm = getChildFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        ProfilePictureFragment fragment = (ProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new ProfilePictureFragment();
            fragment.setButtonsStatusCallback(this);
            fragment.setModel(model);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsAddressPicCapturer, fragment, tag)
                .commit();

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
        if (model != null) {
            String race = model.getPrimaryRace();
            if (!StringUtil.isNullOrEmpty(race)) {
                raceTextView.setText(race);
            }
            String ethnicity = model.getEthnicity();
            if (!StringUtil.isNullOrEmpty(ethnicity)) {
                ethnicityTextView.setText(ethnicity);
            }

            String gender = model.getGender();
            if (!StringUtil.isNullOrEmpty(gender)) {
                genderTextView.setText(gender);
            }

            String unformattedDob = model.getDateOfBirth(); // date from model is excepted to be unformatted
            if (!StringUtil.isNullOrEmpty(unformattedDob)) {
                // format date as mm/dd/yyyy
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(unformattedDob).getDateAsMMddyyyyWithSlash();
                dobEdit.setText(dateOfBirthString);
                dobEdit.requestFocus();
            }
            view.requestFocus();
        } else {
            Log.v(LOG_TAG, "demographics details: views populated with defaults");
            model = new DemographicPersDetailsPayloadDTO();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == raceTextView) {
            selectedArray = 1;
            showAlertDialogWithListview(raceArray, getString(R.string.select_race));
        } else if (view == ethnicityTextView) {
            selectedArray = 2;
            showAlertDialogWithListview(ethnicityArray, getString(R.string.select_ethnicity));
        } else if (view == genderTextView) {
            selectedArray = 3;
            showAlertDialogWithListview(genderArray, getString(R.string.select_gender));
        } else if (view == addUnlistedAllergyTextView) {
            Snackbar.make(view, "In progress", Snackbar.LENGTH_SHORT).show();
        } else if (view == addUnlistedMedTextView) {
            Snackbar.make(view, "In progress", Snackbar.LENGTH_SHORT).show();
        } else if (view == nextButton) {
            onNextButtonClick();
        }
    }

    private void onNextButtonClick() {
        if (isDateOfBirthValid()) { // proceed only if valid date of birth
            // update the model with values from UI
            updateViewsFromModel();

            // move to next page
            ((DemographicsActivity) getActivity()).setCurrentItem(2, true);
        }
    }

    private void updateViewsFromModel() {
        String race = raceTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(race) && !race.equals(getString(R.string.choose))) {
            model.setPrimaryRace(race);
        }
        String ethnicity = ethnicityTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity) && !ethnicity.equals(getString(R.string.choose))) {
            model.setEthnicity(ethnicity);
        }
        String gender = genderTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(gender) && !gender.equals(getString(R.string.choose))) {
            model.setGender(gender);
        }
        // at this point date of birth has been validated (if not empty nor null)
        String formattedDob = dobEdit.getText().toString();
        if (!StringUtil.isNullOrEmpty(formattedDob)) { // simply test if empty (or null)
            // convert back to raw format
            Date dob = DateUtil.parseFromDateAsMMddyyyy(formattedDob);
            model.setDateOfBirth(DateUtil.getDateRaw(dob));
        }
        ((DemographicsActivity) getActivity()).setDetailsModel(model); // save the updated model in the activity
    }

    private void showAlertDialogWithListview(final String[] dataArray, String title) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater
                .from(getActivity()).inflate(R.layout.alert_list_layout,
                                             (ViewGroup) getView(),
                                             false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter adapter = new CustomAlertAdapter(getActivity(), Arrays.asList(dataArray));
        listView.setAdapter(adapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedArray) {
                    case 1:
                        // by 'convention' the last position means 'Decline to answer'
                        if (position == dataArray.length - 1) {
                            // restore the label
                            raceTextView.setText(getString(R.string.choose));
                            model.setPrimaryRace(null);
                        } else {
                            String race = dataArray[position];
                            raceTextView.setText(race);
                            model.setPrimaryRace(race);
                        }
                        break;
                    case 2:
                        if (position == dataArray.length - 1) {
                            // restore the label
                            ethnicityTextView.setText(getString(R.string.choose));
                            model.setEthnicity(null);
                        } else {
                            String ethnicity = ethnicityArray[position];
                            ethnicityTextView.setText(ethnicity);
                            model.setEthnicity(ethnicity);
                        }
                        break;
                    case 3:
                        if (position == dataArray.length - 1) {
                            // restore the label
                            genderTextView.setText(getString(R.string.choose));
                            model.setGender(null);
                        } else {
                            String gender = dataArray[position];
                            genderTextView.setText(gender);
                            model.setGender(gender);
                        }
                }
                alert.dismiss();
            }
        });
    }

    private void setTypefaces(View view) {

        Context context = getActivity();

        setGothamRoundedMediumTypeface(context, (TextView) view.findViewById(R.id.detailsHeading));
        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.detailsSubHeading));

        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsRaceTextView));
        setProximaNovaSemiboldTypeface(context, raceTextView);

        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsEthnicityTextView));
        setProximaNovaSemiboldTypeface(context, ethnicityTextView);

        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsGenderLabel));
        setProximaNovaSemiboldTypeface(context, genderTextView);

        if (!StringUtil.isNullOrEmpty(dobEdit.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), dobInputText);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), dobInputText);
        }

        setProximaNovaRegularTypeface(context, dobEdit);
        setProximaNovaSemiboldTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsDobHint));

        setProximaNovaSemiboldTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsAllergiesLabel));
        setProximaNovaSemiboldTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsAllergiesHint));

        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsAddAllergyLabel));
        setProximaNovaSemiboldTypeface(context, addAnotherAllergyTextView);

        setProximaNovaRegularTypeface(context, addUnlistedAllergyTextView);

        setProximaNovaSemiboldTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsMedLabel));
        setProximaNovaSemiboldTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsMedHint));

        setProximaNovaRegularTypeface(context, (TextView) view.findViewById(R.id.demogrDetailsMedAddLabel));
        setProximaNovaSemiboldTypeface(context, addAnotherMedTextView);

        setProximaNovaRegularTypeface(context, addUnlistedMedTextView);

        setGothamRoundedMediumTypeface(context, nextButton);
    }

    public void setPersDetailsMetaDTO(DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO) {
        this.persDetailsMetaDTO = persDetailsMetaDTO;
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