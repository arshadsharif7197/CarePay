package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
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
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.smartystreets.api.us_zipcode.City;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemographicsInformationFragment extends BaseFragment {

    private DemographicsSettingsDTO demographicsSettingsDTO;
    private String personalInfoString = null;
    private String driverLicenseString = null;
    private String doBString = null;
    private String phoneNumberString = null;
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

    private TextView raceDataTextView;
    private TextView ethnicityDataTextView;
    private TextView selectGender;
    private TextView languageLabelTextView;

    private EditText dobEditText = null;
    private EditText phoneNumberEditText = null;
    private EditText driverLicenseEditText = null;
    private EditText addressLine1EditText = null;
    private EditText addressLine2EditText = null;
    private EditText zipCodeEditText = null;
    private EditText cityEditText = null;
    private EditText stateEditText = null;

    private TextInputLayout phoneNumberLabel;
    private TextInputLayout address1Label;
    private TextInputLayout address2Label;
    private TextInputLayout driverLicenseLabel;
    private TextInputLayout cityLabel;
    private TextInputLayout stateLabel;
    private TextInputLayout zipCodeLabel;
    private TextInputLayout doblabel;

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
    private DemographicsSettingsFragmentListener callback;

    public DemographicsInformationFragment() {

    }

    /**
     * @return an instance of DemographicsInformationFragment
     */
    public static DemographicsInformationFragment newInstance() {
        return new DemographicsInformationFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
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

        getDemographicDetails();
        getLabels();

        raceDataTextView = (TextView) view.findViewById(R.id.chooseRaceTextView);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.chooseEthnicityTextView);
        selectGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        languageLabelTextView = (TextView) view.findViewById(R.id.languageTextView);

        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        driverLicenseEditText = (EditText) view.findViewById(R.id.driverLicenseEditText);
        addressLine1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        addressLine2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (EditText) view.findViewById(R.id.stateAutoCompleteTextView);
        progressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);

        doblabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrDOBTextInput);
        phoneNumberLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrPhoneNumberTextInput);
        driverLicenseLabel = (TextInputLayout) view.findViewById(R.id.reviewDriverLicenseLabel);
        address1Label = (TextInputLayout) view.findViewById(R.id.address1TextInputLayout);
        address2Label = (TextInputLayout) view.findViewById(R.id.address2TextInputLayout);
        zipCodeLabel = (TextInputLayout) view.findViewById(R.id.zipCodeTextInputLayout);
        cityLabel = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateLabel = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);

        initializeOptionsArray();
        getProfileProperties();
        setEditTexts();

        TextView peronalInfoSectionTextview = (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel);
//        TextView demographicSectionTextView = (TextView) view.findViewById(R.id.demographicsSectionLabel);

        peronalInfoSectionTextview.setText(personalInfoString);
//        demographicSectionTextView.setText(demographicsHeaderString);

        String dateOfBirthString = !StringUtil.isNullOrEmpty(dobValString) ? DateUtil.getInstance().setDateRaw(dobValString).toStringWithFormatMmSlashDdSlashYyyy() : "";
        if (SystemUtil.isNotEmptyString(dateOfBirthString)) {
            dobEditText.setText(dateOfBirthString);
            dobEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(phoneValString)) {
            phoneNumberEditText.setText(StringUtil.formatPhoneNumber(phoneValString));
            phoneNumberEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(licenceValString)) {
            driverLicenseEditText.setText(licenceValString);
            driverLicenseEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(address1ValString)) {
            addressLine1EditText.setText(address1ValString);
            addressLine1EditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(address2ValString)) {
            addressLine2EditText.setText(address2ValString);
            addressLine2EditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(zipCodeValString)) {
            zipCodeEditText.setText(StringUtil.formatZipCode(zipCodeValString));
            zipCodeEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(cityValString)) {
            cityEditText.setText(cityValString);
            cityEditText.requestFocus();
        }
        if (SystemUtil.isNotEmptyString(stateValString)) {
            stateEditText.setText(stateValString);
            stateEditText.requestFocus();
        }
        dobEditText.requestFocus();

        SystemUtil.hideSoftKeyboard(getActivity());
        selectGender.setText(genderValString);

        TextView ethnicityLabelTextView = (TextView) view.findViewById(R.id.ethnicityDataTextView);
        ethnicityLabelTextView.setText(ethnicityTitleString);
        TextView genderLabelTextView = (TextView) view.findViewById(R.id.genderTextView);
        genderLabelTextView.setText(genderTitleString);
        TextView languageDataTextView = (TextView) view.findViewById(R.id.languageDataTextView);
        languageDataTextView.setText(languageString);
        TextView raceLabelTextView = (TextView) view.findViewById(R.id.raceDataTextView);
        raceLabelTextView.setText(raceTitleString);
        raceDataTextView.setText(raceValString);
        ethnicityDataTextView.setText(ethnityValString);
        languageLabelTextView.setText(languageString);

        phoneNumberEditText.setHint(phoneNumberString);
        dobEditText.setHint(dateOfBirthString);
        addressLine1EditText.setHint(address1String);
        addressLine2EditText.setHint(address2String);
        cityEditText.setHint(cityString);
        zipCodeEditText.setHint(zipString);

        driverLicenseEditText.setHint(driverLicenseString);

//        demographicSectionTextView.setTextSize(14);
        peronalInfoSectionTextview.setTextSize(14);

        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        setTypefaces();
        setClickables();
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

    private boolean isPhoneNumberValid() {
        getValidations();
        final String phoneError = demographicsSettingsDetailsDTO.getPhone().getValidations().get(0).getErrorMessage();
        final boolean phoneValidation = demographicsSettingsDetailsDTO.getPhone().getValidations().get(0).getValue();
        if (!isPhoneEmpty) { // check validity only if non-empty
            String phone = phoneNumberEditText.getText().toString();
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
            phoneNumberEditText.requestFocus();
        }

        boolean isdobValid = isDateOfBirthValid();
        if (!isdobValid) {
            dobEditText.requestFocus();
        }


        return isPhoneValid && isdobValid && !isAddressEmpty && !isCityEmpty && !isStateEmtpy;

    }

    private void formatEditText() {
        DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
        if (demographicsSettingsMetadataDTO != null) {
            DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
            DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
            DemographicsSettingsAddressInfoDTO demographicsSettingsAddressDTO = demographicsSettingsDemographicsDTO.getAddress();
            demographicsSettingsDetailsDTO = demographicsSettingsAddressDTO.getProperties();

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
                showAlertDialogWithListView(gender, title, cancelLabel);
            }
        });

        raceDataTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 2;
                final String title = raceTitleString;
                showAlertDialogWithListView(race, title, cancelLabel);
            }
        });

        ethnicityDataTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 3;
                final String title = ethnicityTitleString;
                showAlertDialogWithListView(ethnicity, title, cancelLabel);

            }
        });
        languageLabelTextView.setOnClickListener(new View.OnClickListener() {
            String cancelLabel = "Cancel";

            @Override
            public void onClick(View view) {
                selectedDataArray = 4;
                final String title = languageString;
                showAlertDialogWithListView(language, title, cancelLabel);

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
        addressLine1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isAddressEmpty = StringUtil.isNullOrEmpty(addressLine1EditText.getText().toString());
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
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            languages = demographicsSettingsPayloadDTO.getLanguages();
        }

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
            for (DemographicsSettingsOptionDTO option : options) {
                races.add(option.getLabel());
            }
            race = races.toArray(new String[0]);
            options = demographicsSettingsEthnityDTO.getOptions();
            List<String> ethnicities = new ArrayList<>();
            for (DemographicsSettingsOptionDTO option : options) {
                ethnicities.add(option.getLabel());
            }
            ethnicity = ethnicities.toArray(new String[0]);
            DemographicsSettingsGenderDTO demographicsSettingsGenderDTO = demographicsSettingsPropertiesDTO.getGender();

            options = demographicsSettingsGenderDTO.getOptions();
            List<String> genders = new ArrayList<>();
            for (DemographicsSettingsOptionDTO option : options) {
                genders.add(option.getLabel());
            }
            gender = genders.toArray(new String[0]);

            List<String> langs = new ArrayList<>();
            for (DemographicsSettingsLanguageDTO language : languages) {
                langs.add(language.getLabel());
            }
            language = langs.toArray(new String[0]);

        }
    }

    private void showAlertDialogWithListView(final String[] dataArray, String title, String cancelLabel) {

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


    private void setEditTexts() {
        doblabel.setTag(doBString);
        dobEditText.setTag(doblabel);

        phoneNumberLabel.setTag(phoneNumberString);
        phoneNumberEditText.setTag(phoneNumberLabel);

        driverLicenseLabel.setTag(driverLicenseString);
        driverLicenseEditText.setTag(driverLicenseLabel);

        address1Label.setTag(address1String);
        addressLine1EditText.setTag(address1Label);

        address2Label.setTag(address2String);
        addressLine2EditText.setTag(address2Label);

        zipCodeLabel.setTag(zipString);
        zipCodeEditText.setTag(zipCodeLabel);

        cityLabel.setTag(cityString);
        cityEditText.setTag(cityLabel);

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

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        addressLine1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        addressLine2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

    public void getProfileProperties() {
        DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
        DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
        DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();

        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
        DemographicsSettingsAddressInfoDTO demographicsSettingsAddressDTO = demographicsSettingsDetailsDTO.getAddress();
        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
        DemographicsSettingsDateOfBirthDTO demographicsSettingsDOBDTO = demographicsSettingsPersonalDetailsDTO.getDateOfBirth();
        doBString = demographicsSettingsDOBDTO.getLabel();
        DemographicsSettingsPhoneDTO demographicsPhoneNumberDTO = demographicsSettingsAddressDTO.getProperties().getPhone();
        phoneNumberString = demographicsPhoneNumberDTO.getLabel();
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

    private void getDemographicDetails() {
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            DemographicAddressPayloadDTO demographicsAddressDetails = demographicPayload.getAddress();

            dobValString = demographicsPersonalDetails.getDateOfBirth();
            phoneValString = demographicsAddressDetails.getPhone();
            genderValString = demographicsPersonalDetails.getGender();
            raceValString = demographicsPersonalDetails.getPrimaryRace();
            ethnityValString = demographicsPersonalDetails.getEthnicity();
            address1ValString = demographicsAddressDetails.getAddress1();
            address2ValString = demographicsAddressDetails.getAddress2();
            zipCodeValString = demographicsAddressDetails.getZipcode();
            cityValString = demographicsAddressDetails.getCity();
            stateValString = demographicsAddressDetails.getState();
        }

    }

    private void setClickables() {
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllFieldsValid()) {
                    updateProfileButton.setEnabled(false);
                    DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();

                    DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                    TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                    Map<String, String> header = null;

                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                    if (demographicsSettingsPayloadDTO != null) {
                        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                        DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                        PatientModel demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                        DemographicAddressPayloadDTO demographicsAddressDetails = demographicPayload.getAddress();
                        demographicsPersonalDetails.setDateOfBirth(dobEditText.getText().toString());
                        demographicsAddressDetails.setPhone(phoneNumberEditText.getText().toString());
                        demographicsAddressDetails.setAddress1(addressLine1EditText.getText().toString());
                        demographicsAddressDetails.setAddress2(addressLine2EditText.getText().toString());
                        demographicsAddressDetails.setZipcode(zipCodeEditText.getText().toString());
                        demographicsAddressDetails.setCity(cityEditText.getText().toString());
                        demographicsAddressDetails.setState(stateEditText.getText().toString());
                        demographicsPersonalDetails.setGender(selectGender.getText().toString());
                        demographicsPersonalDetails.setPrimaryRace(raceDataTextView.getText().toString());
                        demographicsPersonalDetails.setEthnicity(ethnicityDataTextView.getText().toString());

                        Gson gson = new Gson();
                        String jsonInString = gson.toJson(demographicPayload);
                        getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateDemographicsCallback, jsonInString, header);
                    }
                    header = new HashMap<>();
                    header.put("transition", "true");
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
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
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

    private void setTypefaces() {

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

        if (!StringUtil.isNullOrEmpty(addressLine1EditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), address1Label);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), address1Label);
        }


        if (!StringUtil.isNullOrEmpty(addressLine2EditText.getText().toString())) {
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

        if (!StringUtil.isNullOrEmpty(stateEditText.getText().toString())) {
            setProximaNovaExtraboldTypefaceInput(getActivity(), stateLabel);
        } else {
            setProximaNovaRegularTypefaceLayout(getActivity(), stateLabel);
        }
    }


}
