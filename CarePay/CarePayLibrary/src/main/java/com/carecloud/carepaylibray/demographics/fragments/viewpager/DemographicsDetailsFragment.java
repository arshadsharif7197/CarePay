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
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.ProfilePictureFragment;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

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
    private TextView        header;
    private TextView        subheader;
    private TextView        raceLabel;
    private TextView        ethnicityLabel;
    private TextView        genderLabel;
    private TextView        dobHint;
    private TextView        allergiesOptionalHint;
    private TextView        allergiesSectionHeader;
    private TextView        medicationsSectionHeader;
    private TextView        medicationsOptionalHint;
    private TextView        addAllergyLabel;
    private TextView        addMedLabel;

    private DemographicPersDetailsPayloadDTO        persDetailsDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicLabelsDTO                    globalLabelDTO;
    private String                                  chooseCaption;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fetch the DTOs (payload and metadata)
        globalLabelDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();
        persDetailsDTO = ((DemographicsActivity) getActivity()).getDetailsDTO();

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        setOptions();

        // init view references
        initialiseUIFields();

        // set data in view
        populateViewsFromModel();

        return view;
    }

    private void setOptions() {
        List<MetadataOptionDTO> options = persDetailsMetaDTO.properties.primaryRace.options;
        List<String> races = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            races.add(o.getLabel());
        }
        raceArray = races.toArray(new String[0]);

        options = persDetailsMetaDTO.properties.ethnicity.options;
        List<String> ethnicities = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            ethnicities.add(o.getLabel());
        }
        ethnicityArray = ethnicities.toArray(new String[0]);

        options = persDetailsMetaDTO.properties.gender.options;
        List<String> genders = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            genders.add(o.getLabel());
        }
        genderArray = genders.toArray(new String[0]);
    }

    private void setupEdit() {
        dobInputText = (TextInputLayout) view.findViewById(R.id.demogrDetailsDobInputText);
        dobEdit = (EditText) view.findViewById(R.id.demogrDetailsDobEdit);
        String hint = persDetailsMetaDTO.properties.dateOfBirth.getLabel();
        dobInputText.setTag(hint);
        dobEdit.setTag(dobInputText);
        dobEdit.setHint(hint);

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
        final String errorMessage = persDetailsMetaDTO.properties.dateOfBirth.validations.get(0).getErrorMessage();
        String dob = dobEdit.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateMMDDYYYY(dob);
            dobInputText.setErrorEnabled(!isValid);
            dobInputText.setError(isValid ? null : errorMessage);
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
        String label;

        header = (TextView) view.findViewById(R.id.detailsHeading);
        label = globalLabelDTO.getDemographicsDetailsHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.detailsSubHeading);
        label = globalLabelDTO.getDemographicsDetailsSubheader();
        subheader.setText(label);

        raceLabel = (TextView) view.findViewById(R.id.demogrDetailsRaceLabel);
        label = persDetailsMetaDTO.properties.primaryRace.getLabel();
        raceLabel.setText(label);

        ethnicityLabel = (TextView) view.findViewById(R.id.demogrDetailsEthnicityLabel);
        label = persDetailsMetaDTO.properties.ethnicity.getLabel();
        ethnicityLabel.setText(label);

        genderLabel = (TextView) view.findViewById(R.id.demogrDetailsGenderLabel);
        label = persDetailsMetaDTO.properties.gender.getLabel();
        genderLabel.setText(label);

        dobHint = (TextView) view.findViewById(R.id.demogrDetailsDobHint);
        label = globalLabelDTO.getDemographicsDetailsDobHint();
        dobHint.setText(label);

        // init click-ables and dob edit
        chooseCaption = globalLabelDTO.getDemographicsChooseLabel();

        raceTextView = (TextView) view.findViewById(R.id.demogrDetailsRaceListTextView);
        raceTextView.setText(chooseCaption);
        raceTextView.setOnClickListener(this);

        ethnicityTextView = (TextView) view.findViewById(R.id.demogrDetailsEthnicityListTextView);
        ethnicityTextView.setText(chooseCaption);
        ethnicityTextView.setOnClickListener(this);

        genderTextView = (TextView) view.findViewById(R.id.demogrDetailsGenderClickable);
        genderTextView.setText(chooseCaption);
        genderTextView.setOnClickListener(this);

        setupEdit();

        allergiesOptionalHint = (TextView) view.findViewById(R.id.demogrDetailsAllergiesHint);
        medicationsOptionalHint = (TextView) view.findViewById(R.id.demogrDetailsMedHint);
        label = globalLabelDTO.getDemographicsDetailsOptionalHint();
        allergiesOptionalHint.setText(label);
        medicationsOptionalHint.setText(label);

        allergiesSectionHeader = (TextView) view.findViewById(R.id.demogrDetailsAllergiesLabel);
        label = globalLabelDTO.getDemographicsDetailsAllergiesSection();
        allergiesSectionHeader.setText(label);

        addAllergyLabel = (TextView) view.findViewById(R.id.demogrDetailsAddAllergyLabel);
        label = globalLabelDTO.getDemographicsDetailAllergyLabel();
        addAllergyLabel.setText(label);

        medicationsSectionHeader = (TextView) view.findViewById(R.id.demogrDetailsMedLabel);
        label = globalLabelDTO.getDemographicsDetailsMedicationsSection();
        medicationsSectionHeader.setText(label);

        addMedLabel = (TextView) view.findViewById(R.id.demogrDetailsMedAddLabel);
        label = globalLabelDTO.getDemographicsDetailMedicationLabel();
        addMedLabel.setText(label);

        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        label = globalLabelDTO.getDemographicsNext();
        nextButton.setText(label);
        nextButton.setOnClickListener(this);

        addUnlistedAllergyTextView = (TextView) view.findViewById(R.id.demogrDetailsAllergyAddUnlisted);
        label = globalLabelDTO.getDemographicsDetailsAllergyAddUnlistedLabel();
        addUnlistedAllergyTextView.setText(label);
        addUnlistedAllergyTextView.setOnClickListener(this);

        addUnlistedMedTextView = (TextView) view.findViewById(R.id.demogrDetailsMedAddUnlisted);
        label = globalLabelDTO.getDemographicsDetailsMedAddUnlistedLabel();
        addUnlistedMedTextView.setText(label);
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
            fragment.setPayloadDTO(persDetailsDTO);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsAddressPicCapturer, fragment, tag)
                .commit();

        // set the fonts
        setTypefaces(view);
    }

    /**
     * Getter
     * @return The DTO for personal details
     */
    public DemographicPersDetailsPayloadDTO getPersDetailsDTO() {
        DemographicPayloadDTO payload
                = ((DemographicsActivity) getActivity()).getDemographicInfoPayloadModel();
        if (payload != null) {
            persDetailsDTO = payload.getPersonalDetails();
        }
        return persDetailsDTO;
    }

    private void populateViewsFromModel() {
        if (persDetailsDTO != null) {
            String race = persDetailsDTO.getPrimaryRace();
            if (!StringUtil.isNullOrEmpty(race)) {
                raceTextView.setText(race);
            }
            String ethnicity = persDetailsDTO.getEthnicity();
            if (!StringUtil.isNullOrEmpty(ethnicity)) {
                ethnicityTextView.setText(ethnicity);
            }

            String gender = persDetailsDTO.getGender();
            if (!StringUtil.isNullOrEmpty(gender)) {
                genderTextView.setText(gender);
            }

            String unformattedDob = persDetailsDTO.getDateOfBirth(); // date from persDetailsDTO is excepted to be unformatted
            if (!StringUtil.isNullOrEmpty(unformattedDob)) {
                // format date as mm/dd/yyyy
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(unformattedDob).getDateAsMMddyyyyWithSlash();
                dobEdit.setText(dateOfBirthString);
                dobEdit.requestFocus();
            }
            view.requestFocus();
        } else {
            Log.v(LOG_TAG, "demographics details: views populated with defaults");
            persDetailsDTO = new DemographicPersDetailsPayloadDTO();
        }
    }

    @Override
    public void onClick(View view) {
        final String cancelLabel = globalLabelDTO.getDemographicsCancelLabel();
        if (view == raceTextView) {
            selectedArray = 1;
            final String title = globalLabelDTO.getDemographicsTitleSelectRace();
            showAlertDialogWithListview(raceArray, title, cancelLabel);
        } else if (view == ethnicityTextView) {
            selectedArray = 2;
            final String title = globalLabelDTO.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicityArray, title, cancelLabel);
        } else if (view == genderTextView) {
            selectedArray = 3;
            final String title = globalLabelDTO.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(genderArray, title, cancelLabel);
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
            // update the persDetailsDTO with values from UI
            updateViewsFromModel();

            // move to next page
            ((DemographicsActivity) getActivity()).setCurrentItem(2, true);
        }
    }

    private void updateViewsFromModel() {
        String race = raceTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(race) && !race.equals(getString(R.string.chooseTextView))) {
            persDetailsDTO.setPrimaryRace(race);
        }
        String ethnicity = ethnicityTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity) && !ethnicity.equals(getString(R.string.chooseTextView))) {
            persDetailsDTO.setEthnicity(ethnicity);
        }
        String gender = genderTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(gender) && !gender.equals(getString(R.string.chooseTextView))) {
            persDetailsDTO.setGender(gender);
        }
        // at this point date of birth has been validated (if not empty nor null)
        String formattedDob = dobEdit.getText().toString();
        if (!StringUtil.isNullOrEmpty(formattedDob)) { // simply test if empty (or null)
            // convert back to raw format
            Date dob = DateUtil.parseFromDateAsMMddyyyy(formattedDob);
            persDetailsDTO.setDateOfBirth(DateUtil.getDateRaw(dob));
        }
        ((DemographicsActivity) getActivity()).setDetailsModel(persDetailsDTO); // save the updated persDetailsDTO in the activity
    }

    private void showAlertDialogWithListview(final String[] dataArray, String title, String cancelLabel) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int ii) {
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
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long ll) {
                switch (selectedArray) {
                    case 1:
                        String race = dataArray[position];
                        raceTextView.setText(race);
                        persDetailsDTO.setPrimaryRace(race);
                        break;
                    case 2:
                        String ethnicity = ethnicityArray[position];
                        ethnicityTextView.setText(ethnicity);
                        persDetailsDTO.setEthnicity(ethnicity);
                        break;
                    case 3:
                        String gender = dataArray[position];
                        genderTextView.setText(gender);
                        persDetailsDTO.setGender(gender);
                        break;
                    default:
                        break;
                }
                alert.dismiss();
            }
        });
    }

    private void setTypefaces(View view) {
        Context context = getActivity();

        setGothamRoundedMediumTypeface(context, header);
        setProximaNovaRegularTypeface(context, subheader);
        setProximaNovaRegularTypeface(context, raceLabel);
        setProximaNovaSemiboldTypeface(context, raceTextView);
        setProximaNovaRegularTypeface(context, ethnicityLabel);
        setProximaNovaSemiboldTypeface(context, ethnicityTextView);
        setProximaNovaRegularTypeface(context, genderLabel);
        setProximaNovaSemiboldTypeface(context, genderTextView);

        if (!StringUtil.isNullOrEmpty(dobEdit.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), dobInputText);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), dobInputText);
        }

        setProximaNovaSemiboldTypeface(context, dobHint);
        setProximaNovaRegularTypeface(context, dobEdit);

        setProximaNovaSemiboldTypeface(context, allergiesOptionalHint);

        setProximaNovaSemiboldTypeface(context, allergiesSectionHeader);

        setProximaNovaRegularTypeface(context, addAllergyLabel);
        setProximaNovaSemiboldTypeface(context, addAnotherAllergyTextView);

        setProximaNovaRegularTypeface(context, addUnlistedAllergyTextView);

        setProximaNovaSemiboldTypeface(context, medicationsSectionHeader);
        setProximaNovaSemiboldTypeface(context, medicationsOptionalHint);

        setProximaNovaRegularTypeface(context, addMedLabel);
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