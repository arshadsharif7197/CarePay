
package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;


import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypefaceInput;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypefaceLayout;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class DemographicReviewFragment extends Fragment implements View.OnClickListener {

    private Button buttonAddDemographicInfo;
    private View view;

    private String[] raceDataArray;
    private String[] ethnicityDataArray;
    private String[] genderSelectArray;

    int selectedDataArray;
    private TextView raceDataTextView;
    private TextView ethnicityDataTextView;
    private TextView selectGender;
    private ProgressBar demographicProgressBar;
    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;

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
    ;
    private LinearLayout rootview;


    public static DemographicReviewFragment newInstance() {
        return new DemographicReviewFragment();
    }

    public DemographicReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);
        DemographicReviewActivity.isFromReview = false;

        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        initialiseUIFields();
        setEditTexts(view);
        setTypefaces(view);
        initViewFromModels();

        formatEditText();


        return view;
    }

    private void initialiseUIFields() {

        phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (EditText) view.findViewById(R.id.stateAutoCompleteTextView);
        driverlicenseEditText = (EditText) view.findViewById(R.id.driverLicenseEditText);
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        demographicProgressBar.setVisibility(View.GONE);

        buttonAddDemographicInfo = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        buttonAddDemographicInfo.setOnClickListener(this);
        raceDataTextView = (TextView) view.findViewById(R.id.raceListDataTextView);
        raceDataTextView.setOnClickListener(this);
        ethnicityDataTextView = (TextView) view.findViewById(R.id.ethnicityListDataTextView);
        ethnicityDataTextView.setOnClickListener(this);
        selectGender = (TextView) view.findViewById(R.id.chooseGenderTextView);
        selectGender.setOnClickListener(this);
        raceDataArray = getResources().getStringArray(R.array.Race);
        ethnicityDataArray = getResources().getStringArray(R.array.Ethnicity);
        genderSelectArray = getResources().getStringArray(R.array.Gender);

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


    }


    private void formatEditText() {

        dobEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence dob, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence dob, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable dob) {
                if (dob.length() > 2 && Character.isDigit(dob.charAt(2))) {
                    dob.insert(2, "-");
                }
                if (dob.length() > 5 && Character.isDigit(dob.charAt(5))) {
                    dob.insert(5, "-");
                }

            }
        });

        zipCodeEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence zipcode, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence zipcode, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable zipcode) {
                if (zipcode.length() > 5 && Character.isDigit(zipcode.charAt(5))) {
                    zipcode.insert(5, "-");
                }

            }
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            int length_before = 0;

            @Override
            public void beforeTextChanged(CharSequence phonenumber, int start, int count, int after) {
                length_before = phonenumber.length();
            }

            @Override
            public void onTextChanged(CharSequence phonenumber, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable phonenumber) {

                if (length_before < phonenumber.length()) {
                    if (phonenumber.length() == 3 || phonenumber.length() == 7) {
                        phonenumber.append("-");
                    }
                    if (phonenumber.length() > 3 && Character.isDigit(phonenumber.charAt(3))) {
                        phonenumber.insert(3, "-");
                    }
                    if (phonenumber.length() > 7 && Character.isDigit(phonenumber.charAt(7))) {
                        phonenumber.insert(7, "-");
                    }
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddDemographicInfo) {
            // update the model
            updateModels();
            // post the changes
            postUpdates();
            // hide the keyboard
            SystemUtil.hideSoftKeyboard(getActivity());

        } else if (view == selectGender) {
            selectedDataArray = 1;
            showAlertDialogWithListview(genderSelectArray, "Select Gender");

        } else if (view == raceDataTextView) {
            selectedDataArray = 2;
            showAlertDialogWithListview(raceDataArray, "Select Race");

        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 3;
            showAlertDialogWithListview(ethnicityDataArray, "Select Ethnicity");

        }
    }

    private void showAlertDialogWithListview(final String[] listArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, (ViewGroup) getView(), false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(listArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedDataArray) {
                    case 1:
                        selectGender.setText(genderSelectArray[position]);

                        break;
                    case 2:
                        raceDataTextView.setText(raceDataArray[position]);

                        break;
                    case 3:
                        ethnicityDataTextView.setText(ethnicityDataArray[position]);

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

        firstNameLabel.setTag(getString(R.string.demofirstNameLabel));
        firstNameText.setTag(firstNameLabel);


        middleNameLabel.setTag(getString(R.string.demomiddleNameLabel));
        middleNameText.setTag(middleNameLabel);
        middleNameText.clearFocus();


        lastNameLabel.setTag(getString(R.string.demolastNameLabel));
        lastNameText.setTag(lastNameLabel);


        doblabel.setTag(getString(R.string.demoDobLabel));
        dobEditText.setTag(doblabel);


        phoneNumberLabel.setTag(getString(R.string.demoPhoneNumberLabel));
        phoneNumberEditText.setTag(phoneNumberLabel);


        driverLicenseLabel.setTag(getString(R.string.demodriverlicenseLabel));
        driverlicenseEditText.setTag(driverLicenseLabel);


        address1Label.setTag(getString(R.string.demoAddress1Label));
        address1EditText.setTag(address1Label);


        address2Label.setTag(getString(R.string.demoAddress2Label));
        address2EditText.setTag(address2Label);


        zipcodeLabel.setTag(getString(R.string.demoZipcodeLabel));
        zipCodeEditText.setTag(zipcodeLabel);


        cityLabel.setTag(getString(R.string.demoCityLabel));
        cityEditText.setTag(cityLabel);


        stateLabel.setTag(getString(R.string.demoStateLabel));
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

        DemographicService apptService = (new BaseServiceGenerator(getActivity()))
                .createService(DemographicService.class); // String token, String searchString
        Call<ResponseBody> call = apptService.updateDemographicInformation(postPayloadModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v(LOG_TAG, "" + response.code());
                if (response.code() == 200) {
                    demographicProgressBar.setVisibility(View.GONE);

                    Log.d(LOG_TAG, "demogr post succeeded");

                    openNewFragment();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.d(LOG_TAG, "demogr post failed", throwable);
                demographicProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "demo post failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewFromModels() {
        demographicAddressPayloadDTO = ((DemographicReviewActivity) getActivity())
                .getDemographicAddressPayloadDTO();

        demographicPersDetailsPayloadDTO = ((DemographicReviewActivity) getActivity())
                .getDemographicPersDetailsPayloadDTO();
        insurances = ((DemographicReviewActivity) getActivity())
                .getInsurances();
        demographicIdDocPayloadDTO = ((DemographicReviewActivity) getActivity())
                .getDemographicPayloadIdDocDTO();

        if (demographicPersDetailsPayloadDTO != null) {
            //Personal Details
            String firstName = demographicPersDetailsPayloadDTO.getFirstName();
            if (firstName != null) {
                firstNameText.setText(firstName);
                firstNameText.requestFocus();
            }

            String lastName = demographicPersDetailsPayloadDTO.getLastName();
            if (lastName != null) {
                lastNameText.setText(lastName);
                lastNameText.requestFocus();
            }

            String middleName = demographicPersDetailsPayloadDTO.getMiddleName();
            if (middleName != null) {
                middleNameText.setText(middleName);
                middleNameText.requestFocus();

            } else {
                Log.v(LOG_TAG, "middle name field is empty");
            }
            String datetime = demographicPersDetailsPayloadDTO.getDateOfBirth();
            if (datetime != null) {

                dobEditText.setText(DateUtil.getInstance().setDateRaw(datetime).getDateAsMMddyyyy());
                dobEditText.requestFocus();


            } else {
                Log.v(LOG_TAG, "date is null");
            }
            String getGender = demographicPersDetailsPayloadDTO.getGender();
            if (!getGender.isEmpty()) {
                selectGender.setText(getGender);
            } else {
                selectGender.setText(R.string.choose);

            }

            String getRace = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (!getRace.isEmpty()) {
                raceDataTextView.setText(getRace);
            } else {
                raceDataTextView.setText(R.string.choose);
            }
            String getethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (!getethnicity.isEmpty()) {
                ethnicityDataTextView.setText(getethnicity);
            } else {
                ethnicityDataTextView.setText(R.string.choose);
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
            if (addressLine1 != null) {
                address1EditText.setText(addressLine1);
                address1EditText.requestFocus();

            }

            String addressLine2 = demographicAddressPayloadDTO.getAddress2();
            if (addressLine2 != null) {
                address2EditText.setText(addressLine2);
                address2EditText.requestFocus();
            }

            String city = demographicAddressPayloadDTO.getCity();
            if (city != null) {
                cityEditText.setText(city);
                cityEditText.requestFocus();
            }
            String state = demographicAddressPayloadDTO.getState();
            if (state != null) {
                stateEditText.setText(state);
                stateEditText.requestFocus();
            }
            String zipcode = demographicAddressPayloadDTO.getZipcode();
            if (zipcode != null) {
                zipCodeEditText.setText(StringUtil.formatZipCode(zipcode));
                zipCodeEditText.requestFocus();

            }

            String phoneumber = demographicAddressPayloadDTO.getPhone();
            if (phoneumber != null) {
                phoneNumberEditText.setText(StringUtil.formatPhoneNumber(phoneumber));
            }

        } else {
            Log.v(LOG_TAG, "Demographic adress is empty ");
        }

        rootview.requestFocus();
        hideSoftKeyboard(getActivity());

    }


    private void openNewFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(DemographicReviewFragment.class.getName());
        if (fragment == null) {
            fragment = HealthInsuranceReviewFragment.newInstance();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.addToBackStack("DemographicReviewFragment -> HealthInsuranceReviewFragment");
        transaction.commit();

        ((DemographicReviewActivity) getActivity())
                .setDemographicAddressPayloadDTO(
                        demographicAddressPayloadDTO);
        ((DemographicReviewActivity) getActivity())
                .setDemographicPersDetailsPayloadDTO(
                        demographicPersDetailsPayloadDTO);
        ((DemographicReviewActivity) getActivity())
                .setInsurances(insurances);
        ((DemographicReviewActivity) getActivity())
                .setDemographicPayloadIdDocDTO(demographicIdDocPayloadDTO);

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


        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demographicsLabel));


        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.raceDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.raceListDataTextView));

        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.genderTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.chooseGenderTextView));

        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.demographicsAddressAddressSectionLabel));


        setProximaNovaRegularTypeface(getActivity(),
                (TextView) view.findViewById(R.id.ethnicityDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.ethnicityListDataTextView));
    }


}
