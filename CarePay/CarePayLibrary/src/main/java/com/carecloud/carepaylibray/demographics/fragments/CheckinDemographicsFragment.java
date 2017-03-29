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
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class CheckinDemographicsFragment extends DocumentScannerFragment implements View.OnClickListener {

    private static final String LOG_TAG = CheckinDemographicsFragment.class.getSimpleName();
    int selectedDataArray;
    private Button buttonConfirmData;
    private String[] gender;
    private String[] race;
    private String[] ethnicity;

    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private PatientModel demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;
    private DemographicDTO demographicDTO;

    //profile image
    private ImageView profileImageView;
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
    private TextInputLayout zipCodeLabel;
    private TextInputLayout dobLabel;
    private LinearLayout rootView;

    private TextView addressSectionTextView;
    private TextView personalInfoSectionTextView;
    private TextView demographicSectionTextView;
    private TextView raceDataTextView;
    private TextView raceLabelTextView;
    private TextView ethnicityDataTextView;
    private TextView ethnicityLabelTextView;
    private TextView selectGender;
    private TextView genderLabelTextView;
    private TextView dateFormatLabelTextView;
    private TextView optimalLabelTextView;
    private TextView reviewTitleTextView;
    private TextView reviewSubtitleTextView;

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
        rootView = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);
        initializeDemographicsDTO();

        initialiseUIFields(view);
        setEditTexts(view);
        setTypefaces(view);
        initViewFromModels();

        if (!isPractice) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
            TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
            SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
            title.setText(Label.getLabel("demographics_review_toolbar_title"));
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
        ((ScrollView) view.findViewById(R.id.adddemoScrollview)).smoothScrollTo(0, 0);
        return view;
    }


    private void initializeDemographicsDTO() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
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

        if (demographicIdDocPayloadDTO == null) {
            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
        }
    }

    private void initialiseUIFields(View view) {

        profileImageView = (ImageView) view.findViewById(R.id.patientPicImageView);
        imageCaptureHelper = new ImageCaptureHelper(getActivity(), profileImageView);
        updateProfileImageButton = (Button) view.findViewById(R.id.updateProfileImageButton);


        if (HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE)) {
            updateProfileImageButton.setVisibility(View.GONE);
        }
        reviewTitleTextView = (TextView) view.findViewById(R.id.reviewtitle);
        reviewSubtitleTextView = (TextView) view.findViewById(R.id.reviewSubtitle);
        personalInfoSectionTextView = (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel);
        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicsSectionLabel);
        addressSectionTextView = (TextView) view.findViewById(R.id.demographicsAddressSectionLabel);

        optimalLabelTextView = (TextView) view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);

        dateFormatLabelTextView = (TextView) view.findViewById(R.id.dobformatlabel);
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
        dobLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipCodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextView) view.findViewById(R.id.stateTextInputLayout);
        initializeLabels();
        initializeOptionsArray();

    }


    private String[] getOptionsFrom(List<MetadataOptionDTO> options) {
        List<String> strOptions = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            strOptions.add(o.getLabel());
        }
        return strOptions.toArray(new String[0]);
    }

    private void initializeOptionsArray() {

        List<MetadataOptionDTO> options = persDetailsMetaDTO.getProperties().getPrimaryRace().options;
        race = getOptionsFrom(options);

        options = persDetailsMetaDTO.getProperties().getEthnicity().options;
        ethnicity = getOptionsFrom(options);

        options = persDetailsMetaDTO.getProperties().getGender().options;
        gender = getOptionsFrom(options);

    }

    private void initializeLabels() {
        firstNameText.setHint(persDetailsMetaDTO.getProperties().getFirstName().getLabel());
        lastNameText.setHint(persDetailsMetaDTO.getProperties().getLastName().getLabel());
        middleNameText.setHint(persDetailsMetaDTO.getProperties().getMiddleName().getLabel());
        updateProfileImageButton.setText(Label.getLabel("demographics_details_recapture_picture_caption").toUpperCase());
        reviewTitleTextView.setText(Label.getLabel("demographics_review_screen_title"));
        reviewSubtitleTextView.setText(Label.getLabel("demographics_review_screen_subtitle"));
//        personalInfoSectionTextView.setText(Label.getLabel("demographics_review_peronsonalinfo_section").toUpperCase());
        demographicSectionTextView.setText(Label.getLabel("demographics_section").toUpperCase());
//        addressSectionTextView.setText(Label.getLabel("demographics_address_section").toUpperCase());
        optimalLabelTextView.setText(Label.getLabel("demographics_details_optional_hint"));


        dateFormatLabelTextView.setText(Label.getLabel("demographics_details_dob_hint"));
        phoneNumberEditText.setHint(addressMetaDTO.getProperties().getPhone().getLabel());
        dobEditText.setHint(persDetailsMetaDTO.getProperties().getDateOfBirth().getLabel());
        address1EditText.setHint(addressMetaDTO.getProperties().getAddress1().getLabel());
        address2EditText.setHint(addressMetaDTO.getProperties().getAddress2().getLabel());
        cityEditText.setHint(addressMetaDTO.getProperties().getCity().getLabel());
        zipCodeEditText.setHint(addressMetaDTO.getProperties().getZipcode().getLabel());

        selectGender.setText(Label.getLabel("demographics_choose"));
        selectGender.setOnClickListener(this);

        ethnicityDataTextView.setOnClickListener(this);
        ethnicityDataTextView.setText(Label.getLabel("demographics_choose"));
        ethnicityLabelTextView.setText(persDetailsMetaDTO.getProperties().getEthnicity().getLabel());

        genderLabelTextView.setText(persDetailsMetaDTO.getProperties().getGender().getLabel());

        raceDataTextView.setText(Label.getLabel("demographics_choose"));
        raceDataTextView.setOnClickListener(this);

        raceLabelTextView.setText(persDetailsMetaDTO.getProperties().getPrimaryRace().getLabel());
        buttonConfirmData.setOnClickListener(this);
        buttonConfirmData.setText(Label.getLabel("demographics_review_correct_button").toUpperCase());

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
                    final String firstNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getFirstName().validations.get(0).getErrorMessage();
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
                    dobLabel.setErrorEnabled(false);
                    dobLabel.setError(null);
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
                    final String phoneNumberError = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getPhone().validations.get(0).getErrorMessage();
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
                    final String lastNameError = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getLastName().validations.get(0).getErrorMessage();
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
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getAddress1().validations.get(0).getErrorMessage();
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
                    zipCodeLabel.setError(null);
                    zipCodeLabel.setErrorEnabled(false);
                } else {
                    final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getZipcode().validations.get(0).getErrorMessage();
                    zipCodeLabel.setError(zipcodeError);
                    zipCodeLabel.setErrorEnabled(true);
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
                    final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getCity().validations.get(0).getErrorMessage();
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
                zipCodeLabel,
                addressMetaDTO.getProperties().getZipcode(),
                null);
    }

    private boolean isPhoneNumberValid() {
        final String phoneError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getPhone().validations.get(0).getErrorMessage();
        final String phoneValidation = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : ((String) addressMetaDTO.getProperties().getPhone().validations.get(0).value);
        if (!isPhoneEmpty) { // check validity only if non-empty
            String phone = phoneNumberEditText.getText().toString();
            if (!StringUtil.isNullOrEmpty(phone)
                    && !ValidationHelper.isValidString(phone.trim(), phoneValidation)) {
                phoneNumberLabel.setErrorEnabled(true);
                phoneNumberLabel.setError(phoneError);
                phoneNumberEditText.requestFocus();
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
        final String errorMessage = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getDateOfBirth().validations.get(0).getErrorMessage();
        String dob = dobEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(dob)) {
            boolean isValid = DateUtil.isValidateStringDateOfBirth(dob);
            dobLabel.setErrorEnabled(!isValid);
            dobLabel.setError(isValid ? null : errorMessage);
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

    private void openNextFragment() {
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
            ((CheckinDemographicsInterface) getActivity()).navigateToConsentFlow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            buttonConfirmData.setEnabled(true);
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onClick(View view) {
        String cancelLabel = Label.getLabel("demographics_cancel_label");
        if (view == buttonConfirmData) {
            if (isAllFieldsValid() && checkFormatedFields()) {
                buttonConfirmData.setEnabled(false);
                updateModels();
                openNextFragment();
                SystemUtil.hideSoftKeyboard(getActivity());
            } else {
                showErrorNotification(Label.getLabel("demographics_missing_information"));
            }
        } else if (view == selectGender) {
            selectedDataArray = 1;
            final String title = Label.getLabel("demographics_documents_title_select_gender");
            showAlertDialogWithListview(gender, title, cancelLabel);

        } else if (view == raceDataTextView) {
            selectedDataArray = 2;
            final String title = Label.getLabel("demographics_documents_title_select_race");
            showAlertDialogWithListview(race, title, cancelLabel);

        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 3;
            final String title = Label.getLabel("demographics_documents_title_select_ethnicity");
            showAlertDialogWithListview(ethnicity, title, cancelLabel);

        } else if (view == stateEditText) {
            selectedDataArray = 4;
            final String title = Label.getLabel("demographics_documents_title_select_state");
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
     *
     * @return main demographic DTO
     */
    public DemographicDTO updateModels() {

        // save the personal details
        if (demographicPersDetailsPayloadDTO == null) {
            demographicPersDetailsPayloadDTO = new PatientModel();
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

//        // save id doc
//        if (demographicIdDocPayloadDTO == null) {
//            demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();
//        }

        if (demographicIdDocPayloadDTO != null && demographicIdDocPayloadDTO.getIdType() != null) {
            List<DemographicIdDocPayloadDTO> ids = new ArrayList<>();
            ids.add(demographicIdDocPayloadDTO);
            demographicDTO.getPayload().getDemographics().getPayload().setIdDocuments(ids);
        }

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
//            String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
//            demographicPersDetailsPayloadDTO.setProfilePhoto(imageAsBase64);
//        }
        return demographicDTO;
    }

    private void setEditTexts(View view) {

        firstNameLabel.setTag(persDetailsMetaDTO.getProperties().getFirstName().getLabel());
        firstNameText.setTag(firstNameLabel);


        middleNameLabel.setTag(persDetailsMetaDTO.getProperties().getMiddleName().getLabel());
        middleNameText.setTag(middleNameLabel);


        lastNameLabel.setTag(persDetailsMetaDTO.getProperties().getLastName().getLabel());
        lastNameText.setTag(lastNameLabel);


        dobLabel.setTag(persDetailsMetaDTO.getProperties().getDateOfBirth().getLabel());
        dobEditText.setTag(dobLabel);


        phoneNumberLabel.setTag(addressMetaDTO.getProperties().getPhone().getLabel());
        phoneNumberEditText.setTag(phoneNumberLabel);


        address1Label.setTag(addressMetaDTO.getProperties().getAddress1().getLabel());
        address1EditText.setTag(address1Label);


        address2Label.setTag(addressMetaDTO.getProperties().getAddress2().getLabel());
        address2EditText.setTag(address2Label);


        zipCodeLabel.setTag(addressMetaDTO.getProperties().getZipcode().getLabel());
        zipCodeEditText.setTag(zipCodeLabel);


        cityLabel.setTag(addressMetaDTO.getProperties().getCity().getLabel());
        cityEditText.setTag(cityLabel);

        stateLabel.setText(addressMetaDTO.getProperties().getState().getLabel().toUpperCase());


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
        String chooseLabel = Label.getLabel("demographics_choose");
        if (demographicPersDetailsPayloadDTO != null) {
            String imageUrl = demographicPersDetailsPayloadDTO.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(imageUrl)) {
                Picasso.with(getActivity()).load(imageUrl).transform(
                        new CircleImageTransform()).fit().into(this.profileImageView);
            } else {
                profileImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icn_placeholder_user_profile_png));
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
                selectGender.setText(chooseLabel);

            }

            String getRace = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (SystemUtil.isNotEmptyString(getRace)) {
                raceDataTextView.setText(getRace);
            } else {
                raceDataTextView.setText(chooseLabel);
            }
            String getethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (SystemUtil.isNotEmptyString(getethnicity)) {
                ethnicityDataTextView.setText(getethnicity);
            } else {
                ethnicityDataTextView.setText(chooseLabel);
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
                stateEditText.setText(chooseLabel);
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

        rootView.requestFocus();
        hideSoftKeyboard(getActivity());
        isAllFieldsValid();
    }

    private boolean isAllFieldsValid() {
        boolean allFieldsValid = true;
        if (!StringUtil.isNullOrEmpty(firstNameText.getText().toString())) {
            firstNameLabel.setError(null);
            firstNameLabel.setErrorEnabled(false);
        } else {
            final String firstNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getFirstName().validations.get(0).getErrorMessage();
            firstNameLabel.setError(firstNameError);
            firstNameLabel.setErrorEnabled(true);
            firstNameText.requestFocus();
            allFieldsValid = false;
        }

        if (!StringUtil.isNullOrEmpty(lastNameText.getText().toString())) {
            lastNameLabel.setError(null);
            lastNameLabel.setErrorEnabled(false);
        } else {
            final String lastNameError = persDetailsMetaDTO == null ? CarePayConstants.NOT_DEFINED : persDetailsMetaDTO.getProperties().getLastName().validations.get(0).getErrorMessage();
            lastNameLabel.setError(lastNameError);
            lastNameLabel.setErrorEnabled(true);
            lastNameText.requestFocus();
            allFieldsValid = false;
        }

        if (!isDateOfBirthValid()) {
            allFieldsValid = false;
        }

        if (!isPhoneNumberValid()) {
            allFieldsValid = false;
        }
        String chooseLabel = Label.getLabel("demographics_choose");
        if (chooseLabel.equals(selectGender.getText().toString())) {
            allFieldsValid = false;
        }

        if (chooseLabel.equals(ethnicityDataTextView.getText().toString())) {
            allFieldsValid = false;
        }

        if (chooseLabel.equals(raceDataTextView.getText().toString())) {
            allFieldsValid = false;
        }

        if (demographicDTO.getMetadata().getDataModels().demographic.identityDocuments.properties.items.identityDocument.properties.required.size() > 0 &&
                demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(0).getIdDocPhothos().get(0) == null) {
            allFieldsValid = false;
        }

        if (demographicDTO.getMetadata().getDataModels().demographic.insurances.properties.items.insurance.properties.required.size() > 0 &&
                demographicDTO.getPayload().getDemographics().getPayload().getInsurances().get(0).getInsurancePhotos().get(0) == null) {
            allFieldsValid = false;
        }

        if (!StringUtil.isNullOrEmpty(address1EditText.getText().toString())) {
            address1Label.setError(null);
            address1Label.setErrorEnabled(false);
        } else {
            final String lastNameError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getAddress1().validations.get(0).getErrorMessage();
            address1Label.setError(lastNameError);
            address1Label.setErrorEnabled(true);
            address1EditText.requestFocus();
            allFieldsValid = false;
        }

        if (!StringUtil.isNullOrEmpty(zipCodeEditText.getText().toString())) {
            zipCodeLabel.setError(null);
            zipCodeLabel.setErrorEnabled(false);
        } else {
            final String zipcodeError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getZipcode().validations.get(0).getErrorMessage();
            zipCodeLabel.setError(zipcodeError);
            zipCodeLabel.setErrorEnabled(true);
            zipCodeEditText.requestFocus();
            allFieldsValid = false;
        }

        if (!StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
            cityLabel.setError(null);
            cityLabel.setErrorEnabled(false);
        } else {
            final String cityError = addressMetaDTO == null ? CarePayConstants.NOT_DEFINED : addressMetaDTO.getProperties().getCity().validations.get(0).getErrorMessage();
            cityLabel.setError(cityError);
            cityLabel.setErrorEnabled(true);
            cityEditText.requestFocus();
            allFieldsValid = false;
        }
        enableButton(allFieldsValid);

        return allFieldsValid;
    }

    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), reviewTitleTextView);
        setProximaNovaRegularTypeface(getActivity(), reviewSubtitleTextView);

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
            setProximaNovaExtraboldTypefaceInput(getActivity(), dobLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), dobLabel);
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
            setProximaNovaExtraboldTypefaceInput(getActivity(), zipCodeLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), zipCodeLabel);
        }

        if (!StringUtil.isNullOrEmpty(cityEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), cityLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), cityLabel);
        }

        setProximaNovaExtraboldTypeface(getActivity(), stateLabel);

//        setProximaNovaSemiboldTypeface(getActivity(), personalInfoSectionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), demographicSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), raceLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), raceDataTextView);

        setProximaNovaRegularTypeface(getActivity(), genderLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), selectGender);

//        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);


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

    public void checkIfEnableButton() {
        checkIfDisableButton(false);
    }

    public void checkIfDisableButton(boolean isDisabled) {
        enableButton(isAllFieldsValid() && !isDisabled);
    }

    /**
     * Enable or disable main button
     *
     * @param isEnabled is Button enabled
     */
    public void enableButton(boolean isEnabled) {
        if (isPractice) {
            buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(), isEnabled ? R.drawable.bg_green_overlay : R.drawable.bg_silver_overlay));
            buttonConfirmData.setPadding(20, 0, 20, 0);
        } else {
            buttonConfirmData.setBackground(ContextCompat.getDrawable(getContext(), isEnabled ? R.drawable.language_button_selector : R.drawable.button_light_gray_bg));
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