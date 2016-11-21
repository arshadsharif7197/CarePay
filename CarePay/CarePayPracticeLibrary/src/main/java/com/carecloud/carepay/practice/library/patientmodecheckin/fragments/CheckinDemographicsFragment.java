package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.smartystreets.api.us_zipcode.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinDemographicsFragment extends Fragment implements View.OnClickListener, DocumentScannerFragment.NextAddRemoveStatusModifier {

    private String[] genderArray;
    private String[] raceArray;
    private String[] ethnicityArray;

    private ProgressBar progressbar;

    private EditText firstNameEditText;
    private EditText middleNameEditText;
    private EditText lastNameEditText;
    private EditText dobEditText;
    private EditText phoneNumberEditText;
    private EditText driverlicenseEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText zipCodeEditText;
    private EditText stateEditText;
    private EditText cityEditText;


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

    private TextView addressSectionTextView;
    private TextView peronalInfoSectionTextview;
    private TextView demographicSectionTextView;
    private TextView driverLicenseSectionLabel;
    private TextView raceListTextView;
    private TextView raceLabelTextView;
    private TextView ethnicityListTextView;
    private TextView ethnicityLabelTextView;
    private TextView genderSelectList;
    private TextView genderLabelTextView;
    private TextView updateDemoGraphicTitleTextView;
    private TextView dateformatLabelTextView;
    private TextView optinalLabelTextView;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;

    private String stateAbbr = null;
    private City smartyStreetsResponse;

    private Button addDemogrButton;
    private DemographicDTO demographicsDTO;


    private DemographicLabelsDTO demographicLabelsDto;
    private DemographicMetadataEntityAddressDTO addressDto;
    private DemographicMetadataEntityPersDetailsDTO personalDto;
    private DemographicMetadataEntityIdDocsDTO driverLicenseDto;
    private boolean isPhoneEmpty;
    private int selectedDataArray;
    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;


    public static CheckinInsuranceScannerFragment newInstance() {
        return new CheckinInsuranceScannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_demographics, container, false);
        initialiseUIFields(view);
        // get the DTO
        demographicsDTO = ((PatientModeCheckinActivity) getActivity()).getDemographicDTO();
        demographicLabelsDto = demographicsDTO.getMetadata().getLabels();
        addressDto = demographicsDTO.getMetadata().getDataModels().demographic.address;
        personalDto = demographicsDTO.getMetadata().getDataModels().demographic.personalDetails;
        driverLicenseDto = demographicsDTO.getMetadata().getDataModels().demographic.identityDocuments;
        if (demographicsDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicAddressPayloadDTO = demographicsDTO.getPayload().getDemographics().getPayload().getAddress();
            demographicIdDocPayloadDTO = demographicsDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(0);
        }

        addDemogrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
                ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity) getActivity()).toggleVisibleBackButton(true);

            }
        });
        setEditTexts();
        setTypefaces(view);

        initViewFromModels();

        formatEditText();

        return view;
    }

    private void initialiseUIFields(View view) {

        progressbar = (ProgressBar) view.findViewById(R.id.checkindemographicProgressBar);

        //first name
        firstNameEditText = (EditText) view.findViewById(R.id.adddemoFirstNameEdit);
        firstNameEditText.setHint(personalDto.properties.firstName.getLabel());
        firstNameLabel = (TextInputLayout) view.findViewById(R.id.adddemoFirstNameLabel);


        //middle name
        middleNameEditText = (EditText) view.findViewById(R.id.adddemoMiddleNameEdit);
        middleNameEditText.setHint(personalDto.properties.middleName.getLabel());
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.adddemoMiddleNameTextInputLayout);

        //last name
        lastNameEditText = (EditText) view.findViewById(R.id.adddemoLastNameEdit);
        lastNameEditText.setHint(personalDto.properties.lastName.getLabel());
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.adddemoLastNameLabel);


        //date of birth
        dobEditText = (EditText) view.findViewById(R.id.adddemoDOBEdit);
        dobEditText.setHint(personalDto.properties.dateOfBirth.getLabel());
        doblabel = (TextInputLayout) view.findViewById(R.id.adddemoDOBLabel);


        //phone Number
        phoneNumberEditText = (EditText) view.findViewById(R.id.adddemoPhoneNumberEdit);
        phoneNumberEditText.setHint(addressDto.properties.phone.getLabel());
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.adddemoPhoneNumberLabel);


        //address line 1
        address1EditText = (EditText) view.findViewById(R.id.adddemoAddress1Edit);
        address1EditText.setHint(addressDto.properties.address1.getLabel());
        address1Label = (TextInputLayout) view.findViewById(R.id.adddemoAddress1Label);


        //address line 2
        address2EditText = (EditText) view.findViewById(R.id.adddemoAddress2Edit);
        address2EditText.setHint(addressDto.properties.address2.getLabel());
        address2Label = (TextInputLayout) view.findViewById(R.id.adddemoAddress2Label);

        //zipcode
        zipCodeEditText = (EditText) view.findViewById(R.id.adddemozipCodeEdit);
        zipCodeEditText.setHint(addressDto.properties.zipcode.getLabel());
        zipcodeLabel = (TextInputLayout) view.findViewById(R.id.adddemozipCodeLabel);


        //city
        cityEditText = (EditText) view.findViewById(R.id.adddemocityEdit);
        cityEditText.setHint(addressDto.properties.city.getLabel());
        cityLabel = (TextInputLayout) view.findViewById(R.id.adddemocityLabel);


        //state
        stateEditText = (EditText) view.findViewById(R.id.adddemostateAutoCompleteTextView);
        stateEditText.setHint(addressDto.properties.state.getLabel());
        stateLabel = (TextInputLayout) view.findViewById(R.id.adddemostateLabel);


        //gender
        genderLabelTextView = (TextView) view.findViewById(R.id.adddemogenderLabel);
        genderLabelTextView.setText(personalDto.properties.gender.getLabel());
        genderSelectList = (TextView) view.findViewById(R.id.adddemoGenderListTextView);
        genderSelectList.setText(demographicLabelsDto.getDemographicsChooseLabel());
        genderSelectList.setOnClickListener(this);

        raceLabelTextView = (TextView) view.findViewById(R.id.adddemoraceLabel);
        raceLabelTextView.setText(personalDto.properties.primaryRace.getLabel());
        raceListTextView = (TextView) view.findViewById(R.id.adddemoraceListTextView);
        raceListTextView.setText(demographicLabelsDto.getDemographicsChooseLabel());
        raceListTextView.setOnClickListener(this);

        ethnicityLabelTextView = (TextView) view.findViewById(R.id.adddemoethnicityLabel);
        ethnicityLabelTextView.setText(personalDto.properties.ethnicity.getLabel());
        ethnicityListTextView = (TextView) view.findViewById(R.id.adddemoethnicityListTextView);
        ethnicityListTextView.setText(demographicLabelsDto.getDemographicsChooseLabel());
        ethnicityListTextView.setOnClickListener(this);

// Section Headers
        peronalInfoSectionTextview = (TextView) view.findViewById(R.id.adddemoPersonalInfoLabel);
        peronalInfoSectionTextview.setText(demographicsDTO.getMetadata().getLabels().getDemographicsReviewPeronsonalinfoSection().toUpperCase());
        addressSectionTextView = (TextView) view.findViewById(R.id.adddemoAddressSectionLabel);
        addressSectionTextView.setText(demographicsDTO.getMetadata().getLabels().getDemographicsAddressSection().toUpperCase());
        driverLicenseSectionLabel = (TextView) view.findViewById(R.id.adddemoDriverSectionLabel);
        demographicSectionTextView = (TextView) view.findViewById(R.id.adddemodemographicsSectionLabel);
        demographicSectionTextView.setText(demographicsDTO.getMetadata().getLabels().getDemographicSectionTitle().toUpperCase());

        addDemogrButton = (Button) view.findViewById(R.id.checkinDemographicsAddClickable);


        FragmentManager fm = getChildFragmentManager();
        String tag = PracticeProfilePictureFragment.class.getSimpleName();
        PracticeProfilePictureFragment fragment = (PracticeProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new PracticeProfilePictureFragment();
            fragment.setButtonsStatusCallback(this);
            fragment.setPayloadDTO(demographicPersDetailsPayloadDTO);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsProfilePicCapturer, fragment, tag)
                .commit();

        FragmentManager drivingLicnesefm = getChildFragmentManager();
        String drivingLicensetag = PracticeIdDocScannerFragment.class.getSimpleName();
        PracticeIdDocScannerFragment drivinglicensefragment = (PracticeIdDocScannerFragment) drivingLicnesefm.findFragmentByTag(drivingLicensetag);

        demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        if (drivinglicensefragment == null) {
            drivinglicensefragment = new PracticeIdDocScannerFragment();
            drivinglicensefragment.setButtonsStatusCallback(this);
            drivinglicensefragment.setModel(demographicIdDocPayloadDTO);
        }
        drivingLicnesefm.beginTransaction()
                .replace(R.id.drivingLicneseFragment, drivinglicensefragment, drivingLicensetag)
                .commit();


        List<MetadataOptionDTO> options = personalDto.properties.primaryRace.options;
        List<String> races = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            races.add(o.getLabel());
        }
        raceArray = races.toArray(new String[0]);

        options = personalDto.properties.ethnicity.options;
        List<String> ethnicities = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            ethnicities.add(o.getLabel());
        }
        ethnicityArray = ethnicities.toArray(new String[0]);

        options = personalDto.properties.gender.options;
        List<String> genders = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            genders.add(o.getLabel());
        }
        genderArray = genders.toArray(new String[0]);

    }

    private void setEditTexts() {
        firstNameLabel.setTag(personalDto.properties.firstName.getLabel());
        firstNameEditText.setTag(firstNameLabel);

        middleNameLabel.setTag(personalDto.properties.middleName.getLabel());
        middleNameEditText.setTag(middleNameLabel);

        lastNameLabel.setTag(personalDto.properties.lastName.getLabel());
        lastNameEditText.setTag(lastNameLabel);

        doblabel.setTag(personalDto.properties.dateOfBirth.getLabel());
        dobEditText.setTag(personalDto.properties.dateOfBirth.getLabel());

        phoneNumberLabel.setTag(addressDto.properties.phone.getLabel());
        phoneNumberEditText.setTag(phoneNumberLabel);

        address1Label.setTag(addressDto.properties.address1.getLabel());
        address1EditText.setTag(address1Label);

        address2Label.setTag(addressDto.properties.address2.getLabel());
        address2EditText.setTag(address2Label);

        zipcodeLabel.setTag(addressDto.properties.zipcode.getLabel());
        zipCodeEditText.setTag(zipcodeLabel);

        cityLabel.setTag(addressDto.properties.city.getLabel());
        cityEditText.setTag(cityLabel);

        stateLabel.setTag(addressDto.properties.state.getLabel());
        stateEditText.setTag(stateLabel);

        setChangeFocusListeners();

    }


    private void updateModelWithSelectedOption(String selectedOption) {
        demographicPersDetailsPayloadDTO = new DemographicPersDetailsPayloadDTO();

        switch (selectedDataArray) {
            case 1:
                demographicPersDetailsPayloadDTO.setPrimaryRace(selectedOption);
                break;
            case 2:
                demographicPersDetailsPayloadDTO.setEthnicity(selectedOption);
                break;
            case 3:
                demographicPersDetailsPayloadDTO.setGender(selectedOption);
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

    private void formatEditText() {

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameEditText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameLabel.setError(null);
                    firstNameLabel.setErrorEnabled(false);
                } else {
                    final String firstNameError = addressDto == null ? CarePayConstants.NOT_DEFINED : personalDto.properties.firstName.validations.get(0).getErrorMessage();
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


        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameEditText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameLabel.setError(null);
                    lastNameLabel.setErrorEnabled(false);
                } else {
                    final String lastNameError = addressDto == null ? CarePayConstants.NOT_DEFINED : personalDto.properties.lastName.validations.get(0).getErrorMessage();
                    lastNameLabel.setError(lastNameError);
                    lastNameLabel.setErrorEnabled(true);
                }

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

                if (!isPhoneEmpty) {
                    phoneNumberLabel.setError(null);
                    phoneNumberLabel.setErrorEnabled(false);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
            }
        });

    }

    private boolean checkPhoneNumber() {
        final String phoneError = addressDto == null ? CarePayConstants.NOT_DEFINED : addressDto.properties.phone.validations.get(0).getErrorMessage();
        final String phoneValidation = addressDto == null ? CarePayConstants.NOT_DEFINED : ((String) addressDto.properties.phone.validations.get(0).value);
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
        final String errorMessage = personalDto == null ? CarePayConstants.NOT_DEFINED : personalDto.properties.dateOfBirth.validations.get(0).getErrorMessage();
        String dob = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
            doblabel.setErrorEnabled(!isValid);
            doblabel.setError(isValid ? null : errorMessage);
            return isValid;
        }
        return true;
    }

    private boolean checkReadyForNext() {

        boolean isPhoneValid = checkPhoneNumber();
        // for non-required field, check validity only if non-empty
        if (!isPhoneValid) {
            phoneNumberEditText.requestFocus();
        }

        return isPhoneValid;
    }


    private void setChangeFocusListeners() {
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        middleNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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


    private void initViewFromModels() {


        if (demographicPersDetailsPayloadDTO != null) {
            //Personal Details
            String firstName = demographicPersDetailsPayloadDTO.getFirstName();
            if (SystemUtil.isNotEmptyString(firstName)) {
                firstNameEditText.setText(firstName);
                firstNameEditText.requestFocus();
            }

            String lastName = demographicPersDetailsPayloadDTO.getLastName();
            if (SystemUtil.isNotEmptyString(lastName)) {
                lastNameEditText.setText(lastName);
                lastNameEditText.requestFocus();
            }

            String middleName = demographicPersDetailsPayloadDTO.getMiddleName();
            if (SystemUtil.isNotEmptyString(middleName)) {
                middleNameEditText.setText(middleName);
                middleNameEditText.requestFocus();

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
                genderSelectList.setText(getGender);
            } else {
                genderSelectList.setText(demographicLabelsDto.getDemographicsChooseLabel());

            }

            String getRace = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (SystemUtil.isNotEmptyString(getRace)) {
                raceListTextView.setText(getRace);
            } else {
                raceListTextView.setText(demographicLabelsDto.getDemographicsChooseLabel());
            }
            String getethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (SystemUtil.isNotEmptyString(getethnicity)) {
                ethnicityListTextView.setText(getethnicity);
            } else {
                ethnicityListTextView.setText(demographicLabelsDto.getDemographicsChooseLabel());
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

        hideSoftKeyboard(getActivity());

    }


    private void postUpdates() {

        progressbar.setVisibility(View.VISIBLE);
        DemographicPayloadDTO postPayloadModel = new DemographicPayloadDTO();
        postPayloadModel.setAddress(demographicAddressPayloadDTO);
        postPayloadModel.setPersonalDetails(demographicPersDetailsPayloadDTO);
        // add id docs
        List<DemographicIdDocPayloadDTO> idDocDTOs = new ArrayList<>();
        idDocDTOs.add(demographicIdDocPayloadDTO);
        postPayloadModel.setIdDocuments(idDocDTOs);

        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", demographicsDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", demographicsDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", demographicsDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "false");
        //  WorkflowServiceHelper.getInstance().execute(demographicDTO.getMetadata().getTransitions().getUpdateDemographics(), consentformcallback,queries,header);

        Gson gson = new Gson();
        String demographicinfo = gson.toJson(postPayloadModel);
        TransitionDTO transitionDTO = demographicsDTO.getMetadata().getTransitions().getUpdateDemographics();
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

    private void updateModels() {

        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new DemographicPersDetailsPayloadDTO();
        }

        String firstName = firstNameEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(firstName)) {
            demographicPersDetailsPayloadDTO.setFirstName(firstName);
        }

        String middleName = middleNameEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(middleName)) {
            demographicPersDetailsPayloadDTO.setMiddleName(middleName);
        }
        String lastName = lastNameEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(lastName)) {
            demographicPersDetailsPayloadDTO.setLastName(lastName);
        }

        String dateOfBirth = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dateOfBirth)) {
            // the date is DateUtil as

            demographicPersDetailsPayloadDTO.setDateOfBirth(
                    DateUtil.getDateRaw(DateUtil.parseFromDateAsMMddyyyy(dateOfBirth)));
        }

        String gender = genderSelectList.getText().toString();
        if (!StringUtil.isNullOrEmpty(gender)) {
            demographicPersDetailsPayloadDTO.setGender(gender);
        }

        String race = raceListTextView.getText().toString();
        if (!StringUtil.isNullOrEmpty(race)) {
            demographicPersDetailsPayloadDTO.setPrimaryRace(race);
        }

        String ethnicity = ethnicityListTextView.getText().toString();
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

    private void openNewFragment() {
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentByTag(CheckinDemographicsFragment.class.getName());
//        if (fragment == null) {
//            fragment = CheckinInsuranceScannerFragment.newInstance();
//        }
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.replace(com.carecloud.carepaylibrary.R.id.root_layout, fragment, CheckinInsuranceScannerFragment.class.getName());
//        transaction.addToBackStack("DemographicReviewFragment -> HealthInsuranceReviewFragment");
//        transaction.commit();

        CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
        ((PatientModeCheckinActivity) getActivity()).toggleVisibleBackButton(true);

    }


    private void setTypefaces(View view) {

        if (!StringUtil.isNullOrEmpty(firstNameEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), firstNameLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), firstNameLabel);
        }


        if (!StringUtil.isNullOrEmpty(lastNameEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), lastNameLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), lastNameLabel);
        }

        if (!StringUtil.isNullOrEmpty(middleNameEditText.getText().toString())) {
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


        setProximaNovaSemiboldTypeface(getActivity(), peronalInfoSectionTextview);
        setProximaNovaSemiboldTypeface(getActivity(), demographicSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), raceLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), raceListTextView);

        setProximaNovaRegularTypeface(getActivity(), genderLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), genderSelectList);

        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), ethnicityLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), ethnicityListTextView);
    }

    @Override
    public void onClick(View view) {
        final String cancelLabel = demographicLabelsDto == null ? CarePayConstants.NOT_DEFINED : demographicLabelsDto.getDemographicsCancelLabel();
        if (view == raceListTextView) {
            selectedDataArray = 1;
            final String title = demographicLabelsDto == null ? CarePayConstants.NOT_DEFINED : demographicLabelsDto.getDemographicsTitleSelectRace();
            showAlertDialogWithListview(raceArray, title, cancelLabel, raceListTextView);
        } else if (view == ethnicityListTextView) {
            selectedDataArray = 2;
            final String title = demographicLabelsDto == null ? CarePayConstants.NOT_DEFINED : demographicLabelsDto.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicityArray, title, cancelLabel, ethnicityListTextView);
        } else if (view == genderSelectList) {
            selectedDataArray = 3;
            final String title = demographicLabelsDto == null ? CarePayConstants.NOT_DEFINED : demographicLabelsDto.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(genderArray, title, cancelLabel, genderSelectList);
        } else if (view == addDemogrButton) {

            CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
            ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
            ((PatientModeCheckinActivity) getActivity()).toggleVisibleBackButton(true);
//            if (checkReadyForNext() && isDateOfBirthValid() && !isFirstNameEmpty && !isLastNameEmpty) {
//
//
//
//                // update the model
//               // updateModels();
//                // post the changes
//                //postUpdates();
//                // hide the keyboard
//                SystemUtil.hideSoftKeyboard(getActivity());
//            }

        }
    }

    @Override
    public void showAddCardButton(boolean isVisible) {

    }

    @Override
    public void enableNextButton(boolean isEnabled) {

    }

    @Override
    public void scrollToBottom() {

    }
}
