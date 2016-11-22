package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class CheckinDemographicsFragment extends Fragment implements View.OnClickListener {

    int selectedDataArray;
    private Button buttonAddDemographicInfo;
    private View view;
    private String[] gender;
    private String[] race;
    private String[] ethnicity;
    private ProgressBar demographicProgressBar;

    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicMetadataEntityIdDocsDTO idDocsMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;
    private DemographicDTO demographicDTO;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText dobEditText;
    private EditText stateEditText;
    private EditText driverlicenseEditText;
    private EditText cityEditText;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;

    private TextInputLayout firstNameLabel;
    private TextInputLayout middleNameLabel;
    private TextInputLayout lastNameLabel;
    private TextInputLayout phoneNumberLabel;
    private TextInputLayout address1Label;
    private TextInputLayout address2Label;
    private TextInputLayout driverLicenseLabel;
    private TextInputLayout cityLabel;
    private TextInputLayout stateLabel;
    private TextInputLayout zipcodeLabel;
    private TextInputLayout doblabel;
    private LinearLayout rootview;

    private TextView addressSectionTextView;
    private TextView peronalInfoSectionTextview;
    private TextView demographicSectionTextView;
    private TextView raceDataTextView;
    private TextView raceLabelTextView;
    private TextView ethnicityDataTextView;
    private TextView ethnicityLabelTextView;
    private TextView selectGender;
    private TextView genderLabelTextView;
    private TextView updateDemoGraphicTitleTextView;
    private TextView dateformatLabelTextView;
    private TextView optinalLabelTextView;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;
    private boolean isPhoneEmpty;
    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isStateEmtpy;
    private boolean isZipEmpty;
    private boolean isDriversLicenseEmpty;

    private ScrollView adddemoScrollView;
    private LinearLayout addDemoLineraLayout;

    private String stateAbbr = null;
    private City smartyStreetsResponse;


    public CheckinDemographicsFragment() {
    }

    public static CheckinDemographicsFragment newInstance() {
        return new CheckinDemographicsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);

        initializeDemographicsDTO();
        //initModels();
        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setVisibility(view.GONE);

        initialiseUIFields();
        setEditTexts(view);
        setTypefaces(view);
        initViewFromModels();


        formatEditText();
        return view;
    }

    private void initializeDemographicsDTO() {
        demographicDTO = ((PatientModeCheckinActivity) getActivity()).getDemographicDTO();
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        idDocsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.identityDocuments;

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i > size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

    }

    private void initialiseUIFields() {

        adddemoScrollView = (ScrollView) view.findViewById(R.id.adddemoScrollview);
        addDemoLineraLayout = (LinearLayout) view.findViewById(R.id.adddemoLinearLayout);
        addDemoLineraLayout.setPadding(20, 0, 20, 0);

        updateDemoGraphicTitleTextView = (TextView) view.findViewById(R.id.detailsReviewHeading);
        updateDemoGraphicTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateDemographicTitle());

        peronalInfoSectionTextview = (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel);
        peronalInfoSectionTextview.setText(globalLabelsMetaDTO.getDemographicsReviewPeronsonalinfoSection().toUpperCase());

        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicsSectionLabel);
        demographicSectionTextView.setText(globalLabelsMetaDTO.getDemographicSectionTitle().toUpperCase());

        addressSectionTextView = (TextView) view.findViewById(R.id.demographicsAddressSectionLabel);
        addressSectionTextView.setHint(globalLabelsMetaDTO.getDemographicsAddressSection().toUpperCase());

        optinalLabelTextView = (TextView) view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);
        optinalLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsOptionalHint());

        dateformatLabelTextView = (TextView) view.findViewById(R.id.dobformatlabel);
        dateformatLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsDobHint());


        phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        phoneNumberEditText.setHint(addressMetaDTO.properties.phone.getLabel());

        firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        firstNameText.setHint(persDetailsMetaDTO.properties.firstName.getLabel());

        lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        lastNameText.setHint(persDetailsMetaDTO.properties.lastName.getLabel());

        middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        middleNameText.setHint(persDetailsMetaDTO.properties.middleName.getLabel());

        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        dobEditText.setHint(persDetailsMetaDTO.properties.dateOfBirth.getLabel());

        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address1EditText.setHint(addressMetaDTO.properties.address1.getLabel());

        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        address2EditText.setHint(addressMetaDTO.properties.address2.getLabel());

        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        zipCodeEditText.setHint(addressMetaDTO.properties.zipcode.getLabel());

        cityEditText = (EditText) view.findViewById(R.id.cityId);
        cityEditText.setHint(addressMetaDTO.properties.city.getLabel());

        stateEditText = (EditText) view.findViewById(R.id.stateAutoCompleteTextView);
        stateEditText.setHint(addressMetaDTO.properties.state.getLabel());

        driverlicenseEditText = (EditText) view.findViewById(R.id.driverLicenseEditText);
        driverlicenseEditText.setHint(idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.options.get(0).getLabel());
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

        buttonAddDemographicInfo = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        buttonAddDemographicInfo.setOnClickListener(this);
        buttonAddDemographicInfo.setText(globalLabelsMetaDTO.getDemographicsUpdateButton().toUpperCase());

        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        raceDataTextView.setOnClickListener(this);
        raceLabelTextView = (TextView) view.findViewById(R.id.raceDataTextView);
        raceLabelTextView.setText(persDetailsMetaDTO.properties.primaryRace.getLabel());

        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityDataTextView.setOnClickListener(this);
        ethnicityDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ethnicityLabelTextView = (TextView) view.findViewById(R.id.ethnicityDataTextView);
        ethnicityLabelTextView.setText(persDetailsMetaDTO.properties.ethnicity.getLabel());


        selectGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        selectGender.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        selectGender.setOnClickListener(this);
        genderLabelTextView = (TextView) view.findViewById(R.id.genderTextView);
        genderLabelTextView.setText(persDetailsMetaDTO.properties.gender.getLabel());

        firstNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        doblabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        driverLicenseLabel = (TextInputLayout) view.findViewById(R.id.reviewDriverLicenseLabel);
        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipcodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);

        List<MetadataOptionDTO> options = persDetailsMetaDTO.properties.primaryRace.options;
        List<String> races = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            races.add(o.getLabel());
        }
        race = races.toArray(new String[0]);

        options = persDetailsMetaDTO.properties.ethnicity.options;
        List<String> ethnicities = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            ethnicities.add(o.getLabel());
        }
        ethnicity = ethnicities.toArray(new String[0]);

        options = persDetailsMetaDTO.properties.gender.options;
        List<String> genders = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            genders.add(o.getLabel());
        }
        gender = genders.toArray(new String[0]);

    }

    /**
     * Initialize the models from main Demographic Review Activity
     */
   /* public void initModels() {
        demographicAddressPayloadDTO = ((DemographicReviewActivity) getActivity()).getDemographicAddressPayloadDTO();
        if (demographicAddressPayloadDTO == null) {
            demographicAddressPayloadDTO = new DemographicAddressPayloadDTO();
        }
        demographicPersDetailsPayloadDTO = ((DemographicReviewActivity) getActivity()).getDemographicPersDetailsPayloadDTO();
        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new DemographicPersDetailsPayloadDTO();
        }
        demographicIdDocPayloadDTO = ((DemographicReviewActivity) getActivity()).getDemographicPayloadIdDocDTO();
        if (demographicIdDocPayloadDTO == null) {
            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        }
    }*/
    private void formatEditText() {

        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameLabel.setError(null);
                    firstNameLabel.setErrorEnabled(false);
                } else {
                    final String firstNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.firstName.validations.get(0).getErrorMessage();
                    firstNameLabel.setError(firstNameError);
                    firstNameLabel.setErrorEnabled(true);
                }
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dobEditText.setSelection(dobEditText.length());
            }
        });

        dobEditText.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence dob, int start, int count, int after) {
                prevLen = dob.length();
            }

            @Override
            public void onTextChanged(CharSequence dob, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dob = dobEditText.getText().toString();
                if (!StringUtil.isNullOrEmpty(dob)) {
                    doblabel.setErrorEnabled(false);
                    doblabel.setError(null);
                }
                StringUtil.autoFormatDateOfBirth(editable, prevLen);
            }
        });
        dobEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT || inputType == EditorInfo.IME_ACTION_DONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    dobEditText.clearFocus();
                    view.requestFocus();
                    return true;
                }
                return false;
            }
        });


        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberEditText.setSelection(phoneNumberEditText.length());
            }
        });
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                len = charSequence.length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable phonenumber) {
                String phone = phoneNumberEditText.getText().toString();
                isPhoneEmpty = StringUtil.isNullOrEmpty(phone);
                if (!isPhoneEmpty) {
                    phoneNumberLabel.setError(null);
                    phoneNumberLabel.setErrorEnabled(false);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
            }
        });
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameLabel.setError(null);
                    lastNameLabel.setErrorEnabled(false);
                } else {
                    final String lastNameError = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.lastName.validations.get(0).getErrorMessage();
                    lastNameLabel.setError(lastNameError);
                    lastNameLabel.setErrorEnabled(true);
                }

            }
        });

        address1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isAddressEmpty = StringUtil.isNullOrEmpty(address1EditText.getText().toString());
                if (!isAddressEmpty) {
                    address1Label.setError(null);
                    address1Label.setErrorEnabled(false);
                } else {
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.address1.validations.get(0).getErrorMessage();
                    address1Label.setError(lastNameError);
                    address1Label.setErrorEnabled(true);
                }

            }
        });
        zipCodeEditText.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                prevLen = charSequence.length();
                zipCodeEditText.setSelection(charSequence.length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zip = zipCodeEditText.getText().toString();
                isZipEmpty = StringUtil.isNullOrEmpty(zip);
                if (!isZipEmpty) {
                    zipcodeLabel.setError(null);
                    zipcodeLabel.setErrorEnabled(false);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
            }
        });

        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCityEmpty = StringUtil.isNullOrEmpty(cityEditText.getText().toString());
                if (!isCityEmpty) {
                    cityLabel.setError(null);
                    cityLabel.setErrorEnabled(false);
                } else {
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.city.validations.get(0).getErrorMessage();
                    cityLabel.setError(lastNameError);
                    cityLabel.setErrorEnabled(true);
                }

            }
        });

        stateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isStateEmtpy = StringUtil.isNullOrEmpty(stateEditText.getText().toString());
                if (!isStateEmtpy) {
                    stateLabel.setError(null);
                    stateLabel.setErrorEnabled(false);
                } else {
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.state.validations.get(0).getErrorMessage();
                    stateLabel.setError(lastNameError);
                    stateLabel.setErrorEnabled(true);
                }

            }
        });


    }

    private boolean isZipCodeValid() {
        String zipCode = zipCodeEditText.getText().toString();
        if (StringUtil.isNullOrEmpty(zipCode)) {
            return true;
        }
        // apply validate from backend
        boolean isValidFormat = ValidationHelper.applyPatternValidationToWrappedEdit(zipCodeEditText,
                zipcodeLabel,
                addressMetaDTO.properties.zipcode,
                null);
        return isValidFormat;
    }

    private boolean isPhoneNumberValid() {
        final String phoneError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.phone.validations.get(0).getErrorMessage();
        final String phoneValidation = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : ((String) addressMetaDTO.properties.phone.validations.get(0).value);
        if (!isPhoneEmpty) { // check validity only if non-empty
            String phone = phoneNumberEditText.getText().toString();
            if (!StringUtil.isNullOrEmpty(phone)
                    && !ValidationHelper.isValidString(phone.trim(), phoneValidation)) {
                phoneNumberLabel.setErrorEnabled(true);
                phoneNumberLabel.setError(phoneError);
                return false;
            }
            phoneNumberLabel.setError(null);
            phoneNumberLabel.setErrorEnabled(false);
        }
        return true;
    }


    private boolean isDateOfBirthValid() {
        final String errorMessage = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.properties.dateOfBirth.validations.get(0).getErrorMessage();
        String dob = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
            doblabel.setErrorEnabled(!isValid);
            doblabel.setError(isValid ? null : errorMessage);
            return isValid;
        }
        return true;
    }

    private boolean isAllFieldsValid() {

        boolean isPhoneValid = isPhoneNumberValid();
        // for non-required field, check validity only if non-empty
        if (!isPhoneValid) {
            phoneNumberEditText.requestFocus();
        }
        boolean isZipValid = isZipCodeValid();
        if (!isZipValid) {
            zipCodeEditText.requestFocus();
        }

        boolean isdobValid = isDateOfBirthValid();
        if (!isdobValid) {
            dobEditText.requestFocus();
        }


        return isPhoneValid && isZipValid && isdobValid && !isAddressEmpty && !isFirstNameEmpty && !isLastNameEmpty && !isCityEmpty && !isStateEmtpy;

    }

    @Override
    public void onClick(View view) {
        String cancelLabel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();
        if (view == buttonAddDemographicInfo) {

            //   openNewFragment();

            if (isAllFieldsValid()) {
                // update the model
                updateModels();
                // post the changes
                postUpdates();
                // hide the keyboard
                SystemUtil.hideSoftKeyboard(getActivity());
            }

        } else if (view == selectGender) {
            selectedDataArray = 1;
            final String title = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(gender, title, cancelLabel);

        } else if (view == raceDataTextView) {
            selectedDataArray = 2;
            final String title = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectRace();
            showAlertDialogWithListview(race, title, cancelLabel);


        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 3;
            final String title = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicity, title, cancelLabel);

        }
    }

    private void showAlertDialogWithListview(final String[] dataArray, String title, String cancelLabel) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, (ViewGroup) getView(), false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter alertAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(dataArray));
        listView.setAdapter(alertAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long listener) {
                switch (selectedDataArray) {
                    case 1:
                        String gender = dataArray[position];
                        selectGender.setText(gender);
                        break;
                    case 2:
                        String race = dataArray[position];
                        raceDataTextView.setText(race);

                        break;
                    case 3:
                        String ethnicity = dataArray[position];
                        ethnicityDataTextView.setText(ethnicity);

                        break;
                    default:
                        break;
                }
                alert.dismiss();
            }
        });
    }


    private void updateModels() {

        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new DemographicPersDetailsPayloadDTO();
        }

        String firstName = firstNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            demographicPersDetailsPayloadDTO.setFirstName(firstName);
        }

        String middleName = middleNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            demographicPersDetailsPayloadDTO.setMiddleName(middleName);
        }
        String lastName = lastNameText.getText().toString();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            demographicPersDetailsPayloadDTO.setLastName(lastName);
        }

        String dateOfBirth = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            // the date is DateUtil as

            demographicPersDetailsPayloadDTO.setDateOfBirth(
                    DateUtil.getDateRaw(DateUtil.parseFromDateAsMMddyyyy(dateOfBirth)));
        }

        String gender = selectGender.getText().toString();
        if (!StringUtil.isNullOrEmpty(gender)) {
            demographicPersDetailsPayloadDTO.setGender(gender);
        }

        String race = raceDataTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(race)) {
            demographicPersDetailsPayloadDTO.setPrimaryRace(race);
        }

        String ethnicity = ethnicityDataTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity)) {
            demographicPersDetailsPayloadDTO.setEthnicity(ethnicity);
        }


        if (demographicIdDocPayloadDTO == null) {
            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        }
        String driverLicense = driverlicenseEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(driverLicense)) {
            demographicIdDocPayloadDTO.setIdNumber(driverLicense);
        }


        if (demographicAddressPayloadDTO == null) {
            demographicAddressPayloadDTO = new DemographicAddressPayloadDTO();
        }
        String address1 = address1EditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(address1)) {
            demographicAddressPayloadDTO.setAddress1(address1);
        }
        String address2 = address2EditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(address2)) {
            demographicAddressPayloadDTO.setAddress2(address2);
        }
        String zipCode = zipCodeEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(zipCode)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setZipcode(StringUtil.revertZipToRawFormat(zipCode));
        }
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(phoneNumber)) {
            // 'de-format' before saving to model
            demographicAddressPayloadDTO.setPhone(StringUtil.revertToRawPhoneFormat(phoneNumber));
        }

        String city = cityEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(city)) {
            demographicAddressPayloadDTO.setCity(city);
        }

        String state = stateEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(state)) {
            demographicAddressPayloadDTO.setState(state);
        }

    }

    private void setEditTexts(View view) {

        firstNameLabel.setTag(persDetailsMetaDTO.properties.firstName.getLabel());
        firstNameText.setTag(firstNameLabel);


        middleNameLabel.setTag(persDetailsMetaDTO.properties.middleName.getLabel());
        middleNameText.setTag(middleNameLabel);


        lastNameLabel.setTag(persDetailsMetaDTO.properties.lastName.getLabel());
        lastNameText.setTag(lastNameLabel);


        doblabel.setTag(persDetailsMetaDTO.properties.dateOfBirth.getLabel());
        dobEditText.setTag(doblabel);


        phoneNumberLabel.setTag(addressMetaDTO.properties.phone.getLabel());
        phoneNumberEditText.setTag(phoneNumberLabel);


        driverLicenseLabel.setTag(idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.options.get(0).getLabel());
        driverlicenseEditText.setTag(driverLicenseLabel);


        address1Label.setTag(addressMetaDTO.properties.address1.getLabel());
        address1EditText.setTag(address1Label);


        address2Label.setTag(addressMetaDTO.properties.address2.getLabel());
        address2EditText.setTag(address2Label);


        zipcodeLabel.setTag(addressMetaDTO.properties.zipcode.getLabel());
        zipCodeEditText.setTag(zipcodeLabel);


        cityLabel.setTag(addressMetaDTO.properties.city.getLabel());
        cityEditText.setTag(cityLabel);


        stateLabel.setTag(addressMetaDTO.properties.state.getLabel());
        stateEditText.setTag(stateLabel);


        setChangeFocusListeners();
    }


    private void setChangeFocusListeners() {
        firstNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        dobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        driverlicenseEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        address1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        address2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        zipCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
                if (!bool) { // for SmartyStreets
                    getCityAndState(zipCodeEditText.getText().toString());
                }
            }
        });

        cityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        stateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }

    private void postUpdates() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicPayloadDTO postPayloadModel = new DemographicPayloadDTO();
        postPayloadModel.setAddress(demographicAddressPayloadDTO);
        postPayloadModel.setPersonalDetails(demographicPersDetailsPayloadDTO);
        postPayloadModel.setInsurances(insurances);
        // add id docs
        List<DemographicIdDocPayloadDTO> idDocDTOs = new ArrayList<>();
        idDocDTOs.add(demographicIdDocPayloadDTO);
        postPayloadModel.setIdDocuments(idDocDTOs);

        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "false");
        //  WorkflowServiceHelper.getInstance().execute(demographicDTO.getMetadata().getTransitions().getUpdateDemographics(), consentformcallback,queries,header);

        Gson gson = new Gson();
        String demographicinfo = gson.toJson(postPayloadModel);
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                openNewFragment();
            }

            @Override
            public void onFailure(String exceptionMessage) {

            }
        }, demographicinfo, queries, header);
    }


    private void initViewFromModels() {

        if (demographicPersDetailsPayloadDTO != null) {
            //Personal Details
            String firstName = demographicPersDetailsPayloadDTO.getFirstName();
            if (SystemUtil.isNotEmptyString(firstName)) {
                firstNameText.setText(firstName);
                firstNameText.requestFocus();
            }

            String lastName = demographicPersDetailsPayloadDTO.getLastName();
            if (SystemUtil.isNotEmptyString(lastName)) {
                lastNameText.setText(lastName);
                lastNameText.requestFocus();
            }

            String middleName = demographicPersDetailsPayloadDTO.getMiddleName();
            if (SystemUtil.isNotEmptyString(middleName)) {
                middleNameText.setText(middleName);
                middleNameText.requestFocus();

            } else {
                Log.v(LOG_TAG, "middle name field is empty");
            }
            String datetime = demographicPersDetailsPayloadDTO.getDateOfBirth();
            if (datetime != null) {
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(datetime).getDateAsMMddyyyyWithSlash();
                dobEditText.setText(dateOfBirthString);
                dobEditText.requestFocus();
            } else {
                Log.v(LOG_TAG, "date is null");
            }
            String getGender = demographicPersDetailsPayloadDTO.getGender();
            if (SystemUtil.isNotEmptyString(getGender)) {
                selectGender.setText(getGender);
            } else {
                selectGender.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());

            }

            String getRace = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (SystemUtil.isNotEmptyString(getRace)) {
                raceDataTextView.setText(getRace);
            } else {
                raceDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }
            String getethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (SystemUtil.isNotEmptyString(getethnicity)) {
                ethnicityDataTextView.setText(getethnicity);
            } else {
                ethnicityDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

        } else {
            Log.v(LOG_TAG, "demographic personal details is empty");
        }

        if (demographicIdDocPayloadDTO != null) {
            String driverlicense = demographicIdDocPayloadDTO.getIdNumber();
            if (driverlicense != null) {
                driverlicenseEditText.setText(driverlicense);
                driverlicenseEditText.requestFocus();
            }
        } else {
            Log.v(LOG_TAG, "demographic personal details is empty");
        }

        if (demographicAddressPayloadDTO != null) {
            //Address
            String addressLine1 = demographicAddressPayloadDTO.getAddress1();
            if (SystemUtil.isNotEmptyString(addressLine1)) {
                address1EditText.setText(addressLine1);
                address1EditText.requestFocus();

            }

            String addressLine2 = demographicAddressPayloadDTO.getAddress2();
            if (SystemUtil.isNotEmptyString(addressLine2)) {
                address2EditText.setText(addressLine2);
                address2EditText.requestFocus();
            }

            String city = demographicAddressPayloadDTO.getCity();
            if (SystemUtil.isNotEmptyString(city) || !cityEditText.getText().toString().isEmpty()) {
                cityEditText.setText(city);
                cityEditText.requestFocus();
            }
            String state = demographicAddressPayloadDTO.getState();
            if (SystemUtil.isNotEmptyString(state) || !stateEditText.getText().toString().isEmpty()) {
                stateEditText.setText(state);
                stateEditText.requestFocus();
            }
            String zipcode = demographicAddressPayloadDTO.getZipcode();
            if (SystemUtil.isNotEmptyString(zipcode)) {
                zipCodeEditText.setText(StringUtil.formatZipCode(zipcode));
                zipCodeEditText.requestFocus();

            }

            String phoneumber = demographicAddressPayloadDTO.getPhone();
            if (SystemUtil.isNotEmptyString(phoneumber)) {
                phoneNumberEditText.setText(StringUtil.formatPhoneNumber(phoneumber));
                phoneNumberEditText.requestFocus();
            }

        } else {
            Log.v(LOG_TAG, "Demographic adress is empty ");
        }

        rootview.requestFocus();
        hideSoftKeyboard(getActivity());

    }


    private void openNewFragment() {
        CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
        ((PatientModeCheckinActivity) getActivity()).toggleVisibleBackButton(true);

    }

    public DemographicMetadataEntityIdDocsDTO getIdDocsMetaDTO() {
        return idDocsMetaDTO;
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityIdDocsDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }

    public DemographicMetadataEntityPersDetailsDTO getPersDetailsMetaDTO() {
        return persDetailsMetaDTO;
    }

    public void setPersDetailsMetaDTO(DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO) {
        this.persDetailsMetaDTO = persDetailsMetaDTO;
    }

    public DemographicMetadataEntityAddressDTO getAddressMetaDTO() {
        return addressMetaDTO;
    }

    public void setAddressMetaDTO(DemographicMetadataEntityAddressDTO addressMetaDTO) {
        this.addressMetaDTO = addressMetaDTO;
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(),
                (TextView) view.findViewById(R.id.detailsReviewHeading));

        if (!StringUtil.isNullOrEmpty(firstNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), firstNameLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), firstNameLabel);
        }


        if (!StringUtil.isNullOrEmpty(lastNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), lastNameLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), lastNameLabel);
        }

        if (!StringUtil.isNullOrEmpty(middleNameText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), middleNameLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), middleNameLabel);
        }

        if (!StringUtil.isNullOrEmpty(dobEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), doblabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), doblabel);
        }

        if (!StringUtil.isNullOrEmpty(phoneNumberEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), phoneNumberLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), phoneNumberLabel);
        }

        if (!StringUtil.isNullOrEmpty(address1EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address1Label);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address1Label);
        }


        if (!StringUtil.isNullOrEmpty(address2EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address2Label);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address2Label);
        }

        if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), zipcodeLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), zipcodeLabel);
        }

        if (!StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), cityLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), cityLabel);
        }

        if (!StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), stateLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), stateLabel);
        }
        if (!StringUtil.isNullOrEmpty(driverlicenseEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), driverLicenseLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), driverLicenseLabel);
        }


        setProximaNovaSemiboldTypeface(getActivity(), peronalInfoSectionTextview);
        setProximaNovaSemiboldTypeface(getActivity(), demographicSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), raceLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), raceDataTextView);

        setProximaNovaRegularTypeface(getActivity(), genderLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), selectGender);

        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), ethnicityLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), ethnicityDataTextView);
    }

    /**
     * Background task to call smarty streets zip code lookup.
     * The response is a com.smartystreets.api.us_zipcode.City object,
     * that contains city, mailableCity, stateAbbreviation and state.
     */
    private void getCityAndState(String zipcode) {

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                smartyStreetsResponse = AddressUtil.getCityAndStateByZipCode(params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

                if (smartyStreetsResponse != null) {
                    cityEditText.setText(smartyStreetsResponse.getCity());

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                }
            }


        }.execute(zipcode);
    }
}