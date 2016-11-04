package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
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
        if (persDetailsMetaDTO == null) {
            raceArray = new String[1];
            raceArray[0] = CarePayConstants.NOT_DEFINED;
            ethnicityArray = new String[1];
            ethnicityArray[0] = CarePayConstants.NOT_DEFINED;
            genderArray = new String[1];
            genderArray[0] = CarePayConstants.NOT_DEFINED;
            return;
        }

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
        String hint = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.dateOfBirth.getLabel();
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
        String dob = dobEdit.getText().toString();
        boolean isEmpty = StringUtil.isNullOrEmpty(dob);
        if (isEmpty) { // if no DOB, allow to go next
            return true;
        }

        // apply pattern validation from backend
        boolean isValidFormat = SystemUtil.applyPatternValidation(dobEdit, dobInputText, persDetailsMetaDTO.properties.dateOfBirth);
        if (!isValidFormat) {
            return false;
        }

        // apply local extra validation
        final String errorMessage = "Must be MM/DD/YYYY and between 01/01/1901 and today";
        boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
        dobInputText.setErrorEnabled(!isValid);
        dobInputText.setError(isValid ? null : errorMessage);
        return isValid;
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
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.detailsSubHeading);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsSubheader();
        subheader.setText(label);

        raceLabel = (TextView) view.findViewById(R.id.demogrDetailsRaceLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.primaryRace.getLabel();
        raceLabel.setText(label);

        ethnicityLabel = (TextView) view.findViewById(R.id.demogrDetailsEthnicityLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.ethnicity.getLabel();
        ethnicityLabel.setText(label);

        genderLabel = (TextView) view.findViewById(R.id.demogrDetailsGenderLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.gender.getLabel();
        genderLabel.setText(label);

        dobHint = (TextView) view.findViewById(R.id.demogrDetailsDobHint);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsDobHint();
        dobHint.setText(label);

        // init click-ables and dob edit
        String chooseCaption = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsChooseLabel();

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
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsOptionalHint();
        allergiesOptionalHint.setText(label);
        medicationsOptionalHint.setText(label);

        allergiesSectionHeader = (TextView) view.findViewById(R.id.demogrDetailsAllergiesLabel);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsAllergiesSection();
        allergiesSectionHeader.setText(label);

        addAllergyLabel = (TextView) view.findViewById(R.id.demogrDetailsAddAllergyLabel);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailAllergyLabel();
        addAllergyLabel.setText(label);

        medicationsSectionHeader = (TextView) view.findViewById(R.id.demogrDetailsMedLabel);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsMedicationsSection();
        medicationsSectionHeader.setText(label);

        addMedLabel = (TextView) view.findViewById(R.id.demogrDetailsMedAddLabel);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailMedicationLabel();
        addMedLabel.setText(label);

        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsNext();
        nextButton.setText(label);
        nextButton.setOnClickListener(this);

        addUnlistedAllergyTextView = (TextView) view.findViewById(R.id.demogrDetailsAllergyAddUnlisted);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsAllergyAddUnlistedLabel();
        addUnlistedAllergyTextView.setText(label);
        addUnlistedAllergyTextView.setOnClickListener(this);

        addUnlistedMedTextView = (TextView) view.findViewById(R.id.demogrDetailsMedAddUnlisted);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsMedAddUnlistedLabel();
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
     *
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
        final String cancelLabel = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsCancelLabel();
        if (view == raceTextView) {
            selectedArray = 1;
            final String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectRace();
            showAlertDialogWithListview(raceArray, title, cancelLabel, raceTextView);
        } else if (view == ethnicityTextView) {
            selectedArray = 2;
            final String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicityArray, title, cancelLabel, ethnicityTextView);
        } else if (view == genderTextView) {
            selectedArray = 3;
            final String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(genderArray, title, cancelLabel, genderTextView);
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

    private void updateModelWithSelectedOption(String selectedOption) {
        switch (selectedArray) {
            case 1:
                persDetailsDTO.setPrimaryRace(selectedOption);
                break;
            case 2:
                persDetailsDTO.setEthnicity(selectedOption);
                break;
            case 3:
                persDetailsDTO.setGender(selectedOption);
                break;
            default:
                break;
        }
    }

    private void showAlertDialogWithListview(final String[] dataArray, String title, String cancelLabel,
                                             final TextView selectionDestination) {
        SystemUtil.showChooseDialog(getActivity(),
                                    dataArray, title, cancelLabel,
                                    selectionDestination,
                                    new SystemUtil.OnClickItemCallback() {
                                        @Override
                                        public void executeOnClick(TextView destination, String selectedOption) {
                                            updateModelWithSelectedOption(selectedOption);
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