package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckinDemographicsFragment extends DocumentScannerFragment implements View.OnClickListener {

    private static final String LOG_TAG = CheckinDemographicsFragment.class.getSimpleName();
    int selectedDataArray;
    private Button buttonConfirmData;
    private String[] gender;
    private String[] race;
    private String[] ethnicity;

    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;
    private DemographicDTO demographicDTO;

    //profile image
    private ImageView profileImageview;
    private Button updateProfileImageButton;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText dobEditText;
    private TextView stateEditText;
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
    private TextInputLayout cityLabel;
    private TextView stateLabel;
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
    private TextView dateformatLabelTextView;
    private TextView optinalLabelTextView;
    private TextView reviewTitleTextView;
    private TextView reviewSubtitileTextView;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;
    private boolean isPhoneEmpty;
    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isZipEmpty;
    private boolean isPractice;

    private String stateAbbr = null;
    private City smartyStreetsResponse;

    private CheckinDemographicsFragmentListener activityCallback;

    public interface CheckinDemographicsFragmentListener {
        void onDemographicDtoChanged(DemographicDTO demographicDTO);

        void initializeDocumentFragment();

        void initializeInsurancesFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            activityCallback = (CheckinDemographicsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CheckinDemographicsFragmentListener");
        }
    }

    public CheckinDemographicsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        isPractice = getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        View view = inflater.inflate(R.layout.fragment_review_demographic, container, false);


        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        initializeDemographicsDTO();

        initialiseUIFields(view);
        setEditTexts(view);
        setTypefaces(view);
        initViewFromModels();

        if (!isPractice) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
            SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
            title.setText(globalLabelsMetaDTO.getDemographicsReviewToolbarTitle());
            toolbar.setTitle("");
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
        }

        formatEditText();
        checkIfEnableButton();
        ((ScrollView)view.findViewById(R.id.adddemoScrollview)).smoothScrollTo(0,0);
        return view;
    }


    private void initializeDemographicsDTO() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i < size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

        if(demographicIdDocPayloadDTO == null){
            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        }
    }

    private void initialiseUIFields(View view) {

        profileImageview = (ImageView) view.findViewById(R.id.patientPicImageView);
        imageCaptureHelper = new ImageCaptureHelper(getActivity(), profileImageview, globalLabelsMetaDTO);
        updateProfileImageButton = (Button) view.findViewById(R.id.updateProfileImageButton);


        if (HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE)){
            updateProfileImageButton.setVisibility(View.GONE);
        }
        reviewTitleTextView = (TextView) view.findViewById(R.id.reviewtitle);
        reviewSubtitileTextView = (TextView) view.findViewById(R.id.reviewSubtitle);
        peronalInfoSectionTextview = (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel);
        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicsSectionLabel);
        addressSectionTextView = (TextView) view.findViewById(R.id.demographicsAddressSectionLabel);

        optinalLabelTextView = (TextView) view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);

        dateformatLabelTextView = (TextView) view.findViewById(R.id.dobformatlabel);
        phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);

        firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);

        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (TextView) view.findViewById(R.id.reviewDemographicsStateAutoCompleteTextView);
        stateEditText.setOnClickListener(this);

        buttonConfirmData = (Button) view.findViewById(R.id.buttonAddDemographicInfo);


        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceLabelTextView = (TextView) view.findViewById(R.id.raceDataTextView);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityLabelTextView = (TextView) view.findViewById(R.id.ethnicityDataTextView);
        selectGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        genderLabelTextView = (TextView) view.findViewById(R.id.genderTextView);


        firstNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        doblabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipcodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextView) view.findViewById(R.id.stateTextInputLayout);
        initializeLabels();
        initializeOptionsArray();

    }


    private String[] getOptionsFrom(List<MetadataOptionDTO> options){
        List<String> strOptions = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            strOptions.add(o.getLabel());
        }
        return strOptions.toArray(new String[0]);
    }

    private void initializeOptionsArray() {

        List<MetadataOptionDTO> options = persDetailsMetaDTO.properties.primaryRace.options;
        race = getOptionsFrom(options);

        options = persDetailsMetaDTO.properties.ethnicity.options;
        ethnicity = getOptionsFrom(options);

        options = persDetailsMetaDTO.properties.gender.options;
        gender = getOptionsFrom(options);

    }

    private void initializeLabels() {
        firstNameText.setHint(persDetailsMetaDTO.properties.firstName.getLabel());
        lastNameText.setHint(persDetailsMetaDTO.properties.lastName.getLabel());
        middleNameText.setHint(persDetailsMetaDTO.properties.middleName.getLabel());
        updateProfileImageButton.setText(globalLabelsMetaDTO.getDemographicsProfileReCaptureCaption().toUpperCase());
        reviewTitleTextView.setText(globalLabelsMetaDTO.getDemographicsReviewScreenTitle());
        reviewSubtitileTextView.setText(globalLabelsMetaDTO.getDemographicsReviewScreenSubtitle());
        peronalInfoSectionTextview.setText(globalLabelsMetaDTO.getDemographicsReviewPeronsonalinfoSection().toUpperCase());
        demographicSectionTextView.setText(globalLabelsMetaDTO.getDemographicSectionTitle().toUpperCase());
        addressSectionTextView.setText(globalLabelsMetaDTO.getDemographicsAddressSection().toUpperCase());
        optinalLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsOptionalHint());


        dateformatLabelTextView.setText(globalLabelsMetaDTO.getDemographicsDetailsDobHint());
        phoneNumberEditText.setHint(addressMetaDTO.properties.phone.getLabel());
        dobEditText.setHint(persDetailsMetaDTO.properties.dateOfBirth.getLabel());
        address1EditText.setHint(addressMetaDTO.properties.address1.getLabel());
        address2EditText.setHint(addressMetaDTO.properties.address2.getLabel());
        cityEditText.setHint(addressMetaDTO.properties.city.getLabel());
        zipCodeEditText.setHint(addressMetaDTO.properties.zipcode.getLabel());

        selectGender.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        selectGender.setOnClickListener(this);

        ethnicityDataTextView.setOnClickListener(this);
        ethnicityDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ethnicityLabelTextView.setText(persDetailsMetaDTO.properties.ethnicity.getLabel());

        genderLabelTextView.setText(persDetailsMetaDTO.properties.gender.getLabel());

        raceDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        raceDataTextView.setOnClickListener(this);

        raceLabelTextView.setText(persDetailsMetaDTO.properties.primaryRace.getLabel());
        buttonConfirmData.setOnClickListener(this);
        buttonConfirmData.setText(globalLabelsMetaDTO.getDemographicsReviewCorrectButton().toUpperCase());

        enableButton(isAllFieldsValid());

    }

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
                checkIfEnableButton();
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
                checkIfEnableButton();
            }
        });

        dobEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int inputType, KeyEvent keyEvent) {
                if (inputType == EditorInfo.IME_ACTION_NEXT || inputType == EditorInfo.IME_ACTION_DONE) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    dobEditText.clearFocus();
                    phoneNumberEditText.requestFocus();
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
                } else {
                    final String phoneNumberError = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.phone.validations.get(0).getErrorMessage();
                    phoneNumberLabel.setError(phoneNumberError);
                    phoneNumberLabel.setErrorEnabled(true);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
                checkIfEnableButton();
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
                checkIfEnableButton();
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
                checkIfEnableButton();

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
                } else {
                    final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.properties.zipcode.validations.get(0).getErrorMessage();
                    zipcodeLabel.setError(zipcodeError);
                    zipcodeLabel.setErrorEnabled(true);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
                checkIfEnableButton();
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

                checkIfEnableButton();
            }
        });


        updateProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }

        });
    }

    private boolean isZipCodeValid() {
        String zipCode = zipCodeEditText.getText().toString();
        if (StringUtil.isNullOrEmpty(zipCode)) {
            return true;
        }
        // apply validate from backend
        return ValidationHelper.applyPatternValidationToWrappedEdit(zipCodeEditText,
                zipcodeLabel,
                addressMetaDTO.properties.zipcode,
                null);
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
        } else {
            return false;
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

    private boolean checkFormatedFields() {
        boolean isPhoneValid = isPhoneNumberValid();
        if (!isPhoneValid) {
            phoneNumberEditText.requestFocus();
            return false;
        }
        boolean isZipValid = isZipCodeValid();
        if (!isZipValid) {
            zipCodeEditText.requestFocus();
            return false;
        }

        boolean isdobValid = isDateOfBirthValid();
        if (!isdobValid) {
            dobEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isAllFieldsValid() {

        boolean isdobValid = !StringUtil.isNullOrEmpty(dobEditText.getText().toString());
        boolean isGenderValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(selectGender.getText().toString());
        boolean isEthnicityValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(ethnicityDataTextView.getText().toString());
        boolean isRaceValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(raceDataTextView.getText().toString());
        boolean isStateValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(stateEditText.getText().toString());

        return !isPhoneEmpty && !isZipEmpty && isdobValid && isRaceValid && isEthnicityValid && isGenderValid && !isAddressEmpty && !isFirstNameEmpty && !isLastNameEmpty && !isCityEmpty && isStateValid;

    }

    private void openNextFragment(){
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        Gson gson = new Gson();
        String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicDTO.getPayload().getDemographics().getPayload().getAddress());
        getWorkflowServiceHelper().execute(transitionDTO, consentformcallback, demogrPayloadString, queries, header);
    }

    private WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            buttonConfirmData.setEnabled(true);
            ((CheckinDemographicsInterface)getActivity()).navigateToConsentFlow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            buttonConfirmData.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onClick(View view) {
        String cancelLabel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();
        if (view == buttonConfirmData) {
            //   openNewFragment();
            if (isAllFieldsValid() && checkFormatedFields()) {
                buttonConfirmData.setEnabled(false);
                // update the model
                updateModels();

                // post the changes
//                postUpdates();

                // next
                openNextFragment();

                // hide the keyboard
                SystemUtil.hideSoftKeyboard(getActivity());
            } else {

                showErrorNotification("Validation Error");
//                CustomPopupNotification popup = new CustomPopupNotification(getActivity(), getActivity().getWindow().getCurrentFocus(),
//                        globalLabelsMetaDTO.getDemographicsMissingInformation(), CustomPopupNotification.TYPE_ERROR_NOTIFICATION);
//                    popup.showPopWindow();
            }
        } else if (view == selectGender) {
            selectedDataArray = 1;
            final String title = globalLabelsMetaDTO.getDemographicsTitleSelectGender();
            showAlertDialogWithListview(gender, title, cancelLabel);

        } else if (view == raceDataTextView) {
            selectedDataArray = 2;
            final String title = globalLabelsMetaDTO.getDemographicsTitleSelectRace();
            showAlertDialogWithListview(race, title, cancelLabel);

        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 3;
            final String title = globalLabelsMetaDTO.getDemographicsTitleSelectEthnicity();
            showAlertDialogWithListview(ethnicity, title, cancelLabel);

        } else if (view == stateEditText) {
            selectedDataArray = 4;
            final String title = globalLabelsMetaDTO.getDemographicsTitleSelectState();
            showAlertDialogWithListview(AddressUtil.states, title, cancelLabel);

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
                    case 4:
                        stateAbbr = dataArray[position];
                        stateEditText.setText(stateAbbr);
                        break;
                    default:
                        break;
                }
                checkIfEnableButton();
                alert.dismiss();
            }
        });
    }

    /**
     * update demographic DTO
     * @return main demographic DTO
     */
    public DemographicDTO updateModels() {

        // save the personal details
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
                    DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatYyyyDashMmDashDd()
            );
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
        demographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);

        // save id doc
        if (demographicIdDocPayloadDTO == null) {
            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        }

        List<DemographicIdDocPayloadDTO> ids = new ArrayList<>();
        ids.add(demographicIdDocPayloadDTO);
        demographicDTO.getPayload().getDemographics().getPayload().setIdDocuments(ids);

        // save address
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
        demographicDTO.getPayload().getDemographics().getPayload().setAddress(demographicAddressPayloadDTO);

        // update DTO in the activity
        activityCallback.onDemographicDtoChanged(demographicDTO);

//        if (bitmap != null) {
//            String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
//            demographicPersDetailsPayloadDTO.setProfilePhoto(imageAsBase64);
//        }
        return demographicDTO;
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


        address1Label.setTag(addressMetaDTO.properties.address1.getLabel());
        address1EditText.setTag(address1Label);


        address2Label.setTag(addressMetaDTO.properties.address2.getLabel());
        address2EditText.setTag(address2Label);


        zipcodeLabel.setTag(addressMetaDTO.properties.zipcode.getLabel());
        zipCodeEditText.setTag(zipcodeLabel);


        cityLabel.setTag(addressMetaDTO.properties.city.getLabel());
        cityEditText.setTag(cityLabel);

        stateLabel.setText(addressMetaDTO.properties.state.getLabel().toUpperCase());


        setChangeFocusListeners();
    }

    protected void setChangeFocusListeners() {
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


    }

    private void initViewFromModels() {
//        activityCallback. ();
//        activityCallback.initializeInsurancesFragment();
        if (demographicPersDetailsPayloadDTO != null) {
            String imageUrl = demographicPersDetailsPayloadDTO.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(imageUrl)) {
                Picasso.with(getActivity()).load(imageUrl).transform(
                        new CircleImageTransform()).fit().into(this.profileImageview);
            }else{
                profileImageview.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icn_placeholder_user_profile_png));
            }

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
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(datetime).toStringWithFormatMmSlashDdSlashYyyy();
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
            } else {
                stateEditText.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
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

    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), reviewTitleTextView);
        setProximaNovaRegularTypeface(getActivity(), reviewSubtitileTextView);

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

        setProximaNovaExtraboldTypeface(getActivity(), stateLabel);

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

    public void checkIfEnableButton(){
        checkIfDisableButton(false);
    }

    public void checkIfDisableButton(boolean isDisabled){
        enableButton(isAllFieldsValid() && !isDisabled);
    }

    /**
     * Enable or disable main button
     * @param isEnabled is Button enabled
     */
    public void enableButton(boolean isEnabled){
        if(isPractice){
            buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(),isEnabled? R.drawable.bg_green_overlay  : R.drawable.bg_silver_overlay));
            buttonConfirmData.setPadding(20, 0, 20, 0);
        } else {
            buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(),isEnabled? R.drawable.language_button_selector  : R.drawable.button_light_gray_bg));
        }
        buttonConfirmData.setEnabled(isEnabled);
    }

    @Override
    public ImageCaptureHelper.ImageShape getImageShape() {
        return ImageCaptureHelper.ImageShape.CIRCULAR;
    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner, Bitmap bitmap) {

    }

    @Override
    public void populateViewsFromModel(View view) {

    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    protected void setInsuranceDTO(DemographicInsurancePayloadDTO insuranceDTO, String placeholderBase64) {

    }

    @Override
    protected void enablePlanClickable(boolean enabled) {

    }
}