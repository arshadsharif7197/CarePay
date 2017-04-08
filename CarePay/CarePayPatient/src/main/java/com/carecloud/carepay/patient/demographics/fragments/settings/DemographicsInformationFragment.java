package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsAddressInfoDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsCityDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDateOfBirthDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsEthnicityDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsGenderDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLanguageDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataPropertiesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsOptionDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPhoneDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPreferredLanguageDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPrimaryRaceDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsStateDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsZipDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsInformationFragment extends BaseFragment {

    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String personalInfoString = null;
    private String driverLicenseString = null;
    private String addressString = null;
    private String doBString = null;
    private String phoneNumberString = null;
    private String genderString = null;
    private String raceString = null;
    private String ethnityString = null;
    private String languageString = null;
    private String address1String = null;
    private String address2String = null;
    private String zipString = null;
    private String cityString = null;
    private String stateString = null;
    private String successMessageString = null;
    private String demographicsHeaderString = null;
    private String addressHeaderString = null;
    int selectedDataArray;

    private String[] race;
    private String[] ethnicity;
    private String[] gender;
    private String[] language;

    private TextView addressSectionTextView;
    private TextView peronalInfoSectionTextview;
    private TextView demographicSectionTextView;
    private TextView raceDataTextView;
    private TextView raceLabelTextView;
    private TextView ethnicityDataTextView;
    private TextView ethnicityLabelTextView;
    private TextView selectGender;
    private TextView genderLabelTextView;
    private TextView languageDataTextView;
    private TextView languageLabelTextView;
    private TextView updateDemoGraphicTitleTextView;
    private TextView dateformatLabelTextView;
    private TextView optinalLabelTextView;

    private EditText dobEditText = null;
    private EditText phoneNumberEditext = null;
    private EditText driverLicenseEditText = null;
    private EditText addressLine1Editext = null;
    private EditText addressLine2Editext = null;
    private EditText zipCodeEditext = null;
    private EditText cityEditext = null;
    private EditText stateEditText = null;

    private TextInputLayout phoneNumberLabel;
    private TextInputLayout address1Label;
    private TextInputLayout address2Label;
    private TextInputLayout driverLicenseLabel;
    private TextInputLayout cityLabel;
    private TextInputLayout stateLabel;
    private TextInputLayout zipcodeLabel;
    private TextInputLayout doblabel;
    private LinearLayout rootview;

    private String dobValString = null;
    private String phoneValString = null;
    private String genderValString = null;
    private String raceValString = null;
    private String ethnityValString = null;
    private String licenceValString = null;
    private String address1ValString = null;
    private String address2ValString = null;
    private String zipCodeValString = null;
    private String cityValString = null;
    private String genderTitleString = null;
    private String raceTitleString = null;
    private String ethnicityTitleString = null;
    private String stateValString = null;
    private String languageValString = null;

    private Button updateProfileButton = null;

    private boolean isPhoneEmpty;
    private boolean isAddressEmpty;
    private boolean isCityEmpty;
    private boolean isStateEmtpy;
    private boolean isZipEmpty;

    private String stateAbbr = null;
    private City smartyStreetsResponse;

    private DemographicsSettingsMetadataPropertiesDTO demographicsSettingsDetailsDTO = null;
    private DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO1 = null;
    private ProgressBar progressBar = null;

    private DemographicsInformationFragment() {
    }

    public static DemographicsInformationFragment newInstance(DemographicsSettingsDTO demographicsSettingsDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicsSettingsDTO);
        DemographicsInformationFragment fragment = new DemographicsInformationFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, getArguments());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_demographics_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_label"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);
        getDemographicDetails();
        getLabels();

        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceLabelTextView = (TextView) view.findViewById(R.id.raceDataTextView);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityLabelTextView = (TextView) view.findViewById(R.id.ethnicityDataTextView);
        selectGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        genderLabelTextView = (TextView) view.findViewById(R.id.genderTextView);
        languageDataTextView = (TextView) view.findViewById(R.id.languageDataTextView);
        languageLabelTextView = (TextView) view.findViewById(R.id.languageTextView);

        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        phoneNumberEditext = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        driverLicenseEditText = (EditText) view.findViewById(R.id.driverLicenseEditText);
        addressLine1Editext = (EditText) view.findViewById(R.id.addressEditTextId);
        addressLine2Editext = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditext = (EditText) view.findViewById(R.id.zipCodeId);
        cityEditext = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (EditText) view.findViewById(R.id.stateAutoCompleteTextView);

        doblabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        driverLicenseLabel = (TextInputLayout) view.findViewById(R.id.reviewDriverLicenseLabel);
        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipcodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);

        initializeOptionsArray();
        getProfileProperties();
        setEditTexts(view);

        peronalInfoSectionTextview = (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel);
        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicsSectionLabel);
        addressSectionTextView = (TextView) view.findViewById(R.id.demographicsAddressSectionLabel);
        optinalLabelTextView = (TextView) view.findViewById(R.id.reviewdemogrMiddleNameOptionalLabel);
        dateformatLabelTextView = (TextView) view.findViewById(R.id.dobformatlabel);

        peronalInfoSectionTextview.setText(personalInfoString);
        demographicSectionTextView.setText(demographicsHeaderString);
        addressSectionTextView.setText(addressHeaderString);

        String dateOfBirthString = !StringUtil.isNullOrEmpty(dobValString) ? DateUtil.getInstance().setDateRaw(dobValString).toStringWithFormatMmSlashDdSlashYyyy() : "";
        if (SystemUtil.isNotEmptyString(dateOfBirthString)) {
            dobEditText.setText(dateOfBirthString);
            dobEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(phoneValString)) {
            phoneNumberEditext.setText(StringUtil.formatPhoneNumber(phoneValString));
            phoneNumberEditext.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(licenceValString)) {
            driverLicenseEditText.setText(licenceValString);
            driverLicenseEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(address1ValString)) {
            addressLine1Editext.setText(address1ValString);
            addressLine1Editext.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(address2ValString)) {
            addressLine2Editext.setText(address2ValString);
            addressLine2Editext.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(zipCodeValString)) {
            zipCodeEditext.setText(StringUtil.formatZipCode(zipCodeValString));
            zipCodeEditext.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(cityValString)) {
            cityEditext.setText(cityValString);
            cityEditext.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(stateValString)) {
            stateEditText.setText(stateValString);
            stateEditText.requestFocus();
        }
        dobEditText.requestFocus();

        SystemUtil.hideSoftKeyboard(getActivity());
        selectGender.setText(genderValString);

        ethnicityDataTextView.setText(ethnityValString);
        ethnicityLabelTextView.setText(ethnicityTitleString);
        genderLabelTextView.setText(genderTitleString);
        raceDataTextView.setText(raceValString);
        raceLabelTextView.setText(raceTitleString);
        languageLabelTextView.setText(languageString);
        languageDataTextView.setText(languageString);

        //dateformatLabelTextView.setText(doBString);
        phoneNumberEditext.setHint(phoneNumberString);
        dobEditText.setHint(dateOfBirthString);
        addressLine1Editext.setHint(address1String);
        addressLine2Editext.setHint(address2String);
        cityEditext.setHint(cityString);
        zipCodeEditext.setHint(zipString);

        driverLicenseEditText.setHint(driverLicenseString);


        setProximaNovaSemiboldTypeface(getActivity(), demographicSectionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), peronalInfoSectionTextview);
        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);
        demographicSectionTextView.setTextSize(14);
        peronalInfoSectionTextview.setTextSize(14);
        addressSectionTextView.setTextSize(14);

        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        setTypefaces(view);
        setClickables(view);
        formatEditText();
    }

    private void getLabels() {
        personalInfoString = Label.getLabel("demographics_personal_info_label");
        driverLicenseString = Label.getLabel("demographics_drivers_license_label");
        demographicsHeaderString = Label.getLabel("demographics_demographics_label");
        addressHeaderString = Label.getLabel("demographics_address_label");
        genderTitleString = Label.getLabel("demographics_gender_label");
        ethnicityTitleString = Label.getLabel("demographics_ethnicity_label");
        raceTitleString = Label.getLabel("demographics_race_label");
        languageString = Label.getLabel("demographics_preferred_language_label");
    }

    private void getValidations() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsDemographicsDTO.getPersonalDetails();
                DemographicsSettingsAddressInfoDTO demographicsSettingsAddressDetailsDTO = demographicsSettingsDemographicsDTO.getAddress();
                demographicsSettingsDetailsDTO = demographicsSettingsAddressDetailsDTO.getProperties();
                demographicsSettingsPersonalDetailsDTO1 = demographicsSettingsPersonalDetailsDTO.getProperties();

            }
        }
    }

    private boolean isPhoneNumberValid() {
        getValidations();
        final String phoneError = demographicsSettingsDetailsDTO.getPhone().getValidations().get(0).getErrorMessage();
        final boolean phoneValidation = demographicsSettingsDetailsDTO.getPhone().getValidations().get(0).getValue();
        if (!isPhoneEmpty) { // check validity only if non-empty
            String phone = phoneNumberEditext.getText().toString();
            if (StringUtil.isNullOrEmpty(phone)) {
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
        getValidations();
        final String errorMessage = demographicsSettingsPersonalDetailsDTO1.getDateOfBirth().getValidations().get(0).getErrorMessage();
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
            phoneNumberEditext.requestFocus();
        }

        boolean isdobValid = isDateOfBirthValid();
        if (!isdobValid) {
            dobEditText.requestFocus();
        }


        return isPhoneValid && isdobValid && !isAddressEmpty && !isCityEmpty && !isStateEmtpy;

    }

    private void formatEditText() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                DemographicsSettingsAddressInfoDTO demographicsSettingsAddressDTO = demographicsSettingsDemographicsDTO.getAddress();
                demographicsSettingsDetailsDTO = demographicsSettingsAddressDTO.getProperties();

            }
        }
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
                    return true;
                }
                return false;
            }
        });
        selectGender.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 1;
                final String title = "Title";
                showAlertDialogWithListview(gender, title, cancelLabel);
            }
        });

        raceDataTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 2;
                final String title = raceTitleString;
                showAlertDialogWithListview(race, title, cancelLabel);
            }
        });

        ethnicityDataTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 3;
                final String title = ethnicityTitleString;
                showAlertDialogWithListview(ethnicity, title, cancelLabel);

            }
        });
        languageLabelTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 4;
                final String title = languageString;
                showAlertDialogWithListview(language, title, cancelLabel);

            }
        });
        phoneNumberEditext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumberEditext.setSelection(phoneNumberEditext.length());
            }
        });
        phoneNumberEditext.addTextChangedListener(new TextWatcher() {
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
                String phone = phoneNumberEditext.getText().toString();
                isPhoneEmpty = StringUtil.isNullOrEmpty(phone);
                if (!isPhoneEmpty) {
                    phoneNumberLabel.setError(null);
                    phoneNumberLabel.setErrorEnabled(false);
                }
                // auto-format as typing
                StringUtil.autoFormatPhone(phonenumber, len);
            }
        });
        addressLine1Editext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isAddressEmpty = StringUtil.isNullOrEmpty(addressLine1Editext.getText().toString());
                if (!isAddressEmpty) {
                    address1Label.setError(null);
                    address1Label.setErrorEnabled(false);
                } else {
                    getValidations();
                    final String addressError = demographicsSettingsDetailsDTO.getAddress1().getValidations().get(0).getErrorMessage();
                    address1Label.setError(addressError);
                    address1Label.setErrorEnabled(true);
                }

            }
        });

        zipCodeEditext.addTextChangedListener(new TextWatcher() {
            int prevLen = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {
                prevLen = charSequence.length();
                zipCodeEditext.setSelection(charSequence.length());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String zip = zipCodeEditext.getText().toString();
                isZipEmpty = StringUtil.isNullOrEmpty(zip);
                if (!isZipEmpty) {
                    zipcodeLabel.setError(null);
                    zipcodeLabel.setErrorEnabled(false);
                }

                StringUtil.autoFormatZipcode(editable, prevLen);
            }
        });

        cityEditext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCityEmpty = StringUtil.isNullOrEmpty(cityEditext.getText().toString());
                if (!isCityEmpty) {
                    cityLabel.setError(null);
                    cityLabel.setErrorEnabled(false);
                } else {
                    final String cityError = demographicsSettingsDetailsDTO.getCity().getValidations().get(0).getErrorMessage();
                    cityLabel.setError(cityError);
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
                    final String stateError = demographicsSettingsDetailsDTO.getState().getValidations().get(0).getErrorMessage();
                    stateLabel.setError(stateError);
                    stateLabel.setErrorEnabled(true);
                }

            }
        });


    }

    private void initializeOptionsArray() {
        List<DemographicsSettingsLanguageDTO> languages = null;
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {

                languages = demographicsSettingsPayloadDTO.getLanguages();
            }
        }
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsDemographicsDTO.getPersonalDetails();
                DemographicsSettingsPersonalDetailsDTO demographicsSettingsPropertiesDTO = demographicsSettingsPersonalDetailsDTO.getProperties();
                DemographicsSettingsEthnicityDTO demographicsSettingsEthnityDTO = demographicsSettingsPropertiesDTO.getEthnicity();
                DemographicsSettingsPrimaryRaceDTO demographicsSettingsRaceDTO = demographicsSettingsPropertiesDTO.getPrimaryRace();

                List<DemographicsSettingsOptionDTO> options = demographicsSettingsRaceDTO.getOptions();
                List<String> races = new ArrayList<>();
                for (DemographicsSettingsOptionDTO o : options) {
                    races.add(o.getLabel());
                }
                race = races.toArray(new String[0]);
                options = demographicsSettingsEthnityDTO.getOptions();
                List<String> ethnicities = new ArrayList<>();
                for (DemographicsSettingsOptionDTO o : options) {
                    ethnicities.add(o.getLabel());
                }
                ethnicity = ethnicities.toArray(new String[0]);
                DemographicsSettingsGenderDTO demographicsSettingsGenderDTO = demographicsSettingsPropertiesDTO.getGender();

                options = demographicsSettingsGenderDTO.getOptions();
                List<String> genders = new ArrayList<>();
                for (DemographicsSettingsOptionDTO o : options) {
                    genders.add(o.getLabel());
                }
                gender = genders.toArray(new String[0]);

                List<String> langs = new ArrayList<>();
                for (DemographicsSettingsLanguageDTO l : languages) {
                    langs.add(l.getLabel());
                }
                language = langs.toArray(new String[0]);

            }
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
                        String language = dataArray[position];
                        languageLabelTextView.setText(language);

                        break;
                    default:
                        break;
                }
                alert.dismiss();
            }
        });
    }


    private void setEditTexts(View view) {


        doblabel.setTag(doBString);
        dobEditText.setTag(doblabel);


        phoneNumberLabel.setTag(phoneNumberString);
        phoneNumberEditext.setTag(phoneNumberLabel);


        driverLicenseLabel.setTag(driverLicenseString);
        driverLicenseEditText.setTag(driverLicenseLabel);


        address1Label.setTag(address1String);
        addressLine1Editext.setTag(address1Label);


        address2Label.setTag(address2String);
        addressLine2Editext.setTag(address2Label);


        zipcodeLabel.setTag(zipString);
        zipCodeEditext.setTag(zipcodeLabel);


        cityLabel.setTag(cityString);
        cityEditext.setTag(cityLabel);


        stateLabel.setTag(stateString);
        stateEditText.setTag(stateLabel);


        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {

        dobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        phoneNumberEditext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        driverLicenseEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        addressLine1Editext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        addressLine2Editext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        zipCodeEditext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
                if (!bool) { // for SmartyStreets
                    getCityAndState(zipCodeEditext.getText().toString());
                }
            }
        });

        cityEditext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    cityEditext.setText(smartyStreetsResponse.getCity());

                    stateAbbr = smartyStreetsResponse.getStateAbbreviation();
                    stateEditText.setText(stateAbbr);
                }
            }


        }.execute(zipcode);
    }

    public void getProfileProperties() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                if (demographicsSettingsDataModelsDTO != null) {
                    DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    if (demographicsSettingsDetailsDTO != null) {
                        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                        DemographicsSettingsAddressInfoDTO demographicsSettingsAddressDTO = demographicsSettingsDetailsDTO.getAddress();
                        addressString = demographicsSettingsAddressDTO.getLabel();
                        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                        DemographicsSettingsDateOfBirthDTO demographicsSettingsDOBDTO = demographicsSettingsPersonalDetailsDTO.getDateOfBirth();
                        doBString = demographicsSettingsDOBDTO.getLabel();
                        DemographicsSettingsPhoneDTO demographicsPhoneNumberDTO = demographicsSettingsAddressDTO.getProperties().getPhone();
                        phoneNumberString = demographicsPhoneNumberDTO.getLabel();
                        DemographicsSettingsGenderDTO demographicsSettingsGenderDTO = demographicsSettingsPersonalDetailsDTO.getGender();
                        genderString = demographicsSettingsGenderDTO.getLabel();
                        DemographicsSettingsPrimaryRaceDTO demographicsSettingsRaceDTO = demographicsSettingsPersonalDetailsDTO.getPrimaryRace();
                        raceString = demographicsSettingsRaceDTO.getLabel();
                        DemographicsSettingsEthnicityDTO demographicsSettingsEthnityDTO = demographicsSettingsPersonalDetailsDTO.getEthnicity();
                        ethnityString = demographicsSettingsEthnityDTO.getLabel();
                        DemographicsSettingsPreferredLanguageDTO demographicsSettingsPreferredLanguageDTO = demographicsSettingsPersonalDetailsDTO.getPreferredLanguage();
                        languageString = demographicsSettingsPreferredLanguageDTO.getLabel();
                        DemographicsSettingsAddressDTO demographicsSettingsAddressDTO1 = demographicsSettingsAddressDTO.getProperties().getAddress1();
                        address1String = demographicsSettingsAddressDTO1.getLabel();
                        DemographicsSettingsAddressDTO demographicsSettingsAddressDTO2 = demographicsSettingsAddressDTO.getProperties().getAddress2();
                        address2String = demographicsSettingsAddressDTO2.getLabel();
                        DemographicsSettingsZipDTO demographicsSettingsZipDTO = demographicsSettingsAddressDTO.getProperties().getZipcode();
                        zipString = demographicsSettingsZipDTO.getLabel();
                        DemographicsSettingsCityDTO demographicsSettingsCityDTO = demographicsSettingsAddressDTO.getProperties().getCity();
                        cityString = demographicsSettingsCityDTO.getLabel();
                        DemographicsSettingsStateDTO demographicsSettingsStateDTO = demographicsSettingsAddressDTO.getProperties().getState();
                        stateString = demographicsSettingsStateDTO.getLabel();
                    }
                }
            }
        }
    }

    private void getDemographicDetails() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                DemographicAddressPayloadDTO demographicsAddressDetails = demographicPayload.getAddress();
//                DemographicsSettingsDriversLicenseDTO demographicsLicenseDetails = demographicPayload.getDriversLicense();

                dobValString = demographicsPersonalDetails.getDateOfBirth();
                phoneValString = demographicsAddressDetails.getPhone();
                genderValString = demographicsPersonalDetails.getGender();
                raceValString = demographicsPersonalDetails.getPrimaryRace();
                ethnityValString = demographicsPersonalDetails.getEthnicity();
                //licenceValString = demographicsLicenseDetails.getLicenseNumber();
                address1ValString = demographicsAddressDetails.getAddress1();
                address2ValString = demographicsAddressDetails.getAddress2();
                zipCodeValString = demographicsAddressDetails.getZipcode();
                cityValString = demographicsAddressDetails.getCity();
                stateValString = demographicsAddressDetails.getState();

            }
        }
    }

    private void setClickables(View view) {

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllFieldsValid()) {
                    updateProfileButton.setEnabled(false);
                    if (demographicsSettingsDTO != null) {
                        DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                        if (demographicsSettingsMetadataDTO != null) {
                            DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                            TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                            JSONObject payload = new JSONObject();
                            Map<String, String> queries = null;
                            Map<String, String> header = null;
                            try {
                                if (demographicsSettingsDTO != null) {
                                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                    if (demographicsSettingsPayloadDTO != null) {
                                        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                        DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                                        PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                                        DemographicAddressPayloadDTO demographicsAddressDetails = demographicPayload.getAddress();
                                        demographicsPersonalDetails.setDateOfBirth(dobEditText.getText().toString());
                                        demographicsAddressDetails.setPhone(phoneNumberEditext.getText().toString());
                                        demographicsAddressDetails.setAddress1(addressLine1Editext.getText().toString());
                                        demographicsAddressDetails.setAddress2(addressLine2Editext.getText().toString());
                                        demographicsAddressDetails.setZipcode(zipCodeEditext.getText().toString());
                                        demographicsAddressDetails.setCity(cityEditext.getText().toString());
                                        demographicsAddressDetails.setState(stateEditText.getText().toString());
                                        demographicsPersonalDetails.setGender(selectGender.getText().toString());
                                        demographicsPersonalDetails.setPrimaryRace(raceDataTextView.getText().toString());
                                        demographicsPersonalDetails.setEthnicity(ethnicityDataTextView.getText().toString());

                                        Gson gson = new Gson();
                                        String jsonInString = gson.toJson(demographicPayload);
                                        getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateDemographicsCallback, jsonInString, header);
                                    }
                                }
                                header = new HashMap<>();
                                header.put("transition", "true");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });


    }

    WorkflowServiceCallback updateDemographicsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            updateProfileButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
            SystemUtil.showSuccessToast(getContext(), successMessageString);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            updateProfileButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);

            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void setTypefaces(View view) {

        if (!StringUtil.isNullOrEmpty(dobEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), doblabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), doblabel);
        }

        if (!StringUtil.isNullOrEmpty(phoneNumberEditext.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), phoneNumberLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), phoneNumberLabel);
        }

        if (!StringUtil.isNullOrEmpty(addressLine1Editext.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address1Label);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address1Label);
        }


        if (!StringUtil.isNullOrEmpty(addressLine2Editext.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address2Label);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address2Label);
        }

        if (!StringUtil.isNullOrEmpty(zipCodeEditext.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), zipcodeLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), zipcodeLabel);
        }

        if (!StringUtil.isNullOrEmpty(cityEditext.getText().toString())) {
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
        setProximaNovaSemiboldTypeface(getActivity(), raceDataTextView);

        setProximaNovaRegularTypeface(getActivity(), languageDataTextView);
        setProximaNovaSemiboldTypeface(getActivity(), languageLabelTextView);

        setProximaNovaRegularTypeface(getActivity(), genderLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), selectGender);

        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);


        setProximaNovaRegularTypeface(getActivity(), ethnicityLabelTextView);
        setProximaNovaSemiboldTypeface(getActivity(), ethnicityDataTextView);
    }


}
