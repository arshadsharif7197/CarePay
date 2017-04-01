package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
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

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataValidationDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 * Implements demographics details screen
 */
public class DemographicsDetailsFragment extends BaseFragment
        implements View.OnClickListener{

    private String[] raceArray;
    private String[] ethnicityArray;
    private String[] genderArray;
    private int      selectedArray;

    private TextView        ethnicityTextView;
    private TextView        genderTextView;
    private EditText        dobEdit;
    private Button          nextButton;
    private TextInputLayout dobInputText;
    private TextView        header;
    private TextView        subheader;
    private TextView        raceLabel;
    private TextView        ethnicityLabel;
    private TextView        genderLabel;
    private TextView        dobHint;

    private PatientModel persDetailsDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicLabelsDTO                    globalLabelDTO;

    DemographicsDetailsFragmentListener activityCallback;

    public interface DemographicsDetailsFragmentListener {
        void initializeProfilePictureFragment(DemographicLabelsDTO globalLabelDTO, PatientModel persDetailsDTO);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            activityCallback = (DemographicsDetailsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsDetailsFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fetch the DTOs (payload and metadata)
        globalLabelDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();
        persDetailsDTO = ((DemographicsActivity) getActivity()).getDetailsDTO();

        // create the view
        View view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        setOptions();

        // init view references
        initialiseUIFields(view);

        // set data in view
        populateViewsFromModel(view);

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

        List<MetadataOptionDTO> options = persDetailsMetaDTO.getProperties().getPrimaryRace().getOptions();
        List<String> races = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            races.add(o.getLabel());
        }
        raceArray = races.toArray(new String[0]);

        options = persDetailsMetaDTO.getProperties().getEthnicity().getOptions();
        List<String> ethnicities = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            ethnicities.add(o.getLabel());
        }
        ethnicityArray = ethnicities.toArray(new String[0]);

        options = persDetailsMetaDTO.getProperties().getGender().getOptions();
        List<String> genders = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            genders.add(o.getLabel());
        }
        genderArray = genders.toArray(new String[0]);
    }

    private void setupEdit(View view) {
        dobInputText = (TextInputLayout) view.findViewById(R.id.demogrDetailsDobInputText);
        dobEdit = (EditText) view.findViewById(R.id.demogrDetailsDobEdit);
        String hint = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getDateOfBirth().getLabel();
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

                    View view = getView();
                    if (view != null) {
                        view.requestFocus();
                    }

                    return true;
                }
                return false;
            }
        });
    }

    private boolean isDateOfBirthValid() {
        final String dob = dobEdit.getText().toString();
        boolean isEmpty = StringUtil.isNullOrEmpty(dob);
        if (isEmpty) { // if no DOB, allow to go next
            return true;
        }

        // apply pattern validate from backend
        boolean isValidFormat = ValidationHelper.applyPatternValidationToWrappedEdit(
                dobEdit,
                dobInputText,
                persDetailsMetaDTO.getProperties().getDateOfBirth(),
                new ValidationHelper.LocalValidation() {
                    @Override
                    public boolean validate(MetadataValidationDTO validation) {
                        // apply local extra validate
                        final String errorMessage = "Invalid date; Must be MM/DD/YYYY and between 01/01/1901 and today";
                        boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
                        dobInputText.setErrorEnabled(!isValid);
                        dobInputText.setError(isValid ? null : errorMessage);
                        return isValid;
                    }
                });

        return isValidFormat;
    }

    private void initialiseUIFields(View view) {
        String label;

        header = (TextView) view.findViewById(R.id.detailsHeading);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.detailsSubHeading);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsSubheader();
        subheader.setText(label);

        raceLabel = (TextView) view.findViewById(R.id.demogrDetailsRaceLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getPrimaryRace().getLabel();
        raceLabel.setText(label);

        ethnicityLabel = (TextView) view.findViewById(R.id.demogrDetailsEthnicityLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getEthnicity().getLabel();
        ethnicityLabel.setText(label);

        genderLabel = (TextView) view.findViewById(R.id.demogrDetailsGenderLabel);
        label = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getGender().getLabel();
        genderLabel.setText(label);

        dobHint = (TextView) view.findViewById(R.id.demogrDetailsDobHint);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsDetailsDobHint();
        dobHint.setText(label);

        // init click-ables and dob edit
        String chooseCaption = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsChooseLabel();

        TextView raceTextView = (TextView) view.findViewById(R.id.demogrDetailsRaceListTextView);
        raceTextView.setText(chooseCaption);
        raceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedArray = 1;
                String cancelLabel = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsCancelLabel();
                String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectRace();
                showAlertDialogWithListview(raceArray, title, cancelLabel, (TextView) view);
            }
        });

        ethnicityTextView = (TextView) view.findViewById(R.id.demogrDetailsEthnicityListTextView);
        ethnicityTextView.setText(chooseCaption);
        ethnicityTextView.setOnClickListener(this);

        genderTextView = (TextView) view.findViewById(R.id.demogrDetailsGenderClickable);
        genderTextView.setText(chooseCaption);
        genderTextView.setOnClickListener(this);

        setupEdit(view);

        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        label = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsNext();
        nextButton.setText(label);
        nextButton.setOnClickListener(this);

        // add capture picture fragment
        activityCallback.initializeProfilePictureFragment(globalLabelDTO, persDetailsDTO);

        // set the fonts
        setTypefaces(raceTextView);
    }

    /**
     * Getter
     *
     * @return The DTO for personal details
     */
    public PatientModel getPersDetailsDTO() {
        DemographicPayloadDTO payload
                = ((DemographicsActivity) getActivity()).getDemographicInfoPayloadModel();
        if (payload != null) {
            persDetailsDTO = payload.getPersonalDetails();
        }
        return persDetailsDTO;
    }

    private void populateViewsFromModel(View view) {
        if (persDetailsDTO != null) {
            String race = persDetailsDTO.getPrimaryRace();
            if (!StringUtil.isNullOrEmpty(race)) {
                TextView raceTextView = (TextView) view.findViewById(R.id.demogrDetailsRaceListTextView);
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
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(unformattedDob).toStringWithFormatMmSlashDdSlashYyyy();
                dobEdit.setText(dateOfBirthString);
                dobEdit.requestFocus();
            }
            view.requestFocus();
        } else {
            Log.v(LOG_TAG, "demographics details: views populated with defaults");
            persDetailsDTO = new PatientModel();
        }
    }

    @Override
    public void onClick(View view) {
        final String cancelLabel = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsCancelLabel();
        if (view == ethnicityTextView) {
            selectedArray = 2;
            final String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicityArray, title, cancelLabel, ethnicityTextView);
        } else if (view == genderTextView) {
            selectedArray = 3;
            final String title = globalLabelDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelDTO.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(genderArray, title, cancelLabel, genderTextView);
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
        TextView raceTextView = (TextView) findViewById(R.id.demogrDetailsRaceListTextView);
        String race = raceTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(race) && !race.equals(globalLabelDTO.getDemographicsChooseLabel())) {
            persDetailsDTO.setPrimaryRace(race);
        }
        String ethnicity = ethnicityTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity) && !ethnicity.equals(globalLabelDTO.getDemographicsChooseLabel())) {
            persDetailsDTO.setEthnicity(ethnicity);
        }
        String gender = genderTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(gender) && !gender.equals(globalLabelDTO.getDemographicsChooseLabel())) {
            persDetailsDTO.setGender(gender);
        }
        // at this point date of birth has been validated (if not empty nor null)
        String formattedDob = dobEdit.getText().toString();
        if (!StringUtil.isNullOrEmpty(formattedDob)) { // simply test if empty (or null)
            String rawDob = DateUtil.getInstance().setDateRaw(formattedDob).toStringWithFormatIso8601();
            persDetailsDTO.setDateOfBirth(rawDob);
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

    private void setTypefaces(TextView raceTextView) {
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

        setGothamRoundedMediumTypeface(context, nextButton);
    }

    public void setPersDetailsMetaDTO(DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO) {
        this.persDetailsMetaDTO = persDetailsMetaDTO;
    }
}