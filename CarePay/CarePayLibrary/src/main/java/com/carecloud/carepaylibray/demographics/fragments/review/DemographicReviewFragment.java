package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadAddressModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoMetaDataModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class DemographicReviewFragment extends Fragment implements View.OnClickListener {

    private Button buttonAddDemographicInfo;
    private View view;

    private String[] raceDataArray;
    private String[] ethnicityDataArray;
    private String[] prefferedLangaugeArray;
    private String[] genderSelectArray;
    int selectedDataArray;
    private TextView raceDataTextView, ethnicityDataTextView, selectGender, selectlangauge;
    private ProgressBar demographicProgressBar;
    private DemographicPayloadResponseModel demographicPayloadResponseModel;
    private DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel;
    private DemographicPayloadAddressModel demographicPayloadAddressModel;
    private List<DemographicPayloadInsuranceModel> insurances;
    private DemographicPayloadDriversLicenseModel demographicPayloadDriversLicenseModel;

    private EditText phoneNumberEditText;
    private EditText zipCodeEditText;
    private EditText address1EditText;
    private EditText address2EditText;
    private EditText emailEditText;
    private EditText dobEditText;
    private EditText stateEditText;

    private EditText driverLicense;
    private EditText cityEditText;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;

    public static DemographicReviewFragment newInstance() {
        return new DemographicReviewFragment();
    }

    public DemographicReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_demographic, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.demographics_review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.demographics_review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(getContext(), SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();*/
            }
        });
        initialiseUIFields();
        initViewFromModels();

        //getDemographicInformation();
       // phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        raceDataArray = getResources().getStringArray(R.array.Race);
        ethnicityDataArray = getResources().getStringArray(R.array.Ethnicity);
        prefferedLangaugeArray = getResources().getStringArray(R.array.Language);
        genderSelectArray = getResources().getStringArray(R.array.Gender);
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {

        phoneNumberEditText = (EditText) view.findViewById(R.id.reviewgrdemoPhoneNumberEdit);
        firstNameText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        lastNameText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        middleNameText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        emailEditText = (EditText) view.findViewById(R.id.reviewdemogrEmailEdit);
        dobEditText = (EditText) view.findViewById(R.id.revewidemogrDOBEdit);
        address1EditText = (EditText) view.findViewById(R.id.addressEditTextId);
        address2EditText = (EditText) view.findViewById(R.id.addressEditText2Id);
        zipCodeEditText = (EditText) view.findViewById(R.id.zipCodeId);
        cityEditText = (EditText) view.findViewById(R.id.cityId);
        stateEditText = (EditText) view.findViewById(R.id.stateAutoCompleteTextView);
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
        selectlangauge = (TextView) view.findViewById(R.id.preferredLanguageListTextView);
        selectlangauge.setOnClickListener(this);


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
            // open demographics review
            openNewFragment();
        } else if (view == raceDataTextView) {
            selectedDataArray = 1;
            showAlertDialogWithListview(raceDataArray, "Select Race");
        } else if (view == ethnicityDataTextView) {
            selectedDataArray = 2;
            showAlertDialogWithListview(ethnicityDataArray, "Select Ethnicity");
        } else if (view == selectlangauge) {
            selectedDataArray = 3;
            showAlertDialogWithListview(prefferedLangaugeArray, "Preferred Language");
        } else if (view == selectGender) {
            selectedDataArray = 4;
            showAlertDialogWithListview(genderSelectArray, "Select Gender");
        }
    }

    private void updateModels() {
        if(demographicPayloadPersonalDetailsModel == null) {
            demographicPayloadPersonalDetailsModel = new DemographicPayloadPersonalDetailsModel();
        }

        String firstName = firstNameText.getText().toString();
        if(!StringUtil.isNullOrEmpty(firstName)) {
            demographicPayloadPersonalDetailsModel.setFirstName(firstName);
        }

        // TODO: 10/1/16 for all personal

        if(demographicPayloadAddressModel == null) {
            demographicPayloadAddressModel = new DemographicPayloadAddressModel();
        }
        String address1 = address1EditText.getText().toString();
        if(!StringUtil.isNullOrEmpty(address1)) {
            demographicPayloadAddressModel.setAddress1(address1);
        }
        String phoneNumber = phoneNumberEditText.getText().toString();
        if(!StringUtil.isNullOrEmpty(phoneNumber)) {
            demographicPayloadAddressModel.setPhone(phoneNumber);
        }
        // TODO: 10/1/16 for all address fields

    }

    private void postUpdates() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicPayloadModel postPayloadModel = new DemographicPayloadModel();
        postPayloadModel.setAddress(demographicPayloadAddressModel);
        postPayloadModel.setPersonalDetails(demographicPayloadPersonalDetailsModel);
        postPayloadModel.setInsurances(insurances);
        postPayloadModel.setDriversLicense(demographicPayloadDriversLicenseModel);

        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.confirmDemographicInformation(postPayloadModel);
        call.enqueue(new Callback<DemographicModel>() {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                demographicProgressBar.setVisibility(View.GONE);
                Log.d(LOG_TAG, "demogr post succeeded");

                openNewFragment();
            }

            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {
                Log.d(LOG_TAG, "demogr post failed", t);
                demographicProgressBar.setVisibility(View.GONE);
            }
        });
    }
    private  void initViewFromModels(){

        demographicPayloadAddressModel = ((DemographicReviewActivity)getActivity()).getDemographicPayloadAddressModel();
        demographicPayloadPersonalDetailsModel = ((DemographicReviewActivity)getActivity()).getDemographicPayloadPersonalDetailsModel();
        insurances = ((DemographicReviewActivity)getActivity()).getInsurances();
        demographicPayloadDriversLicenseModel= ((DemographicReviewActivity)getActivity()).getDemographicPayloadDriversLicenseModel();


        if(demographicPayloadAddressModel != null) {
            //Personal Details
            firstNameText.setText(demographicPayloadPersonalDetailsModel.getFirstName());
            lastNameText.setText(demographicPayloadPersonalDetailsModel.getLastName());

            String datetime = demographicPayloadPersonalDetailsModel.getDateOfBirth();
            if(datetime!=null){
            String[] date = datetime.split("T");

            String dob = date[0];
            String time = date[1];
            dobEditText.setText(dob);}
            selectGender.setText(demographicPayloadPersonalDetailsModel.getGender());
            selectlangauge.setText(demographicPayloadPersonalDetailsModel.getPreferredLanguage());
            raceDataTextView.setText(demographicPayloadPersonalDetailsModel.getPrimaryRace());
            ethnicityDataTextView.setText(demographicPayloadPersonalDetailsModel.getEthnicity());

        }

        if(demographicPayloadPersonalDetailsModel != null) {
            //Address
            address1EditText.setText(demographicPayloadAddressModel.getAddress1());
            address2EditText.setText(demographicPayloadAddressModel.getAddress2());
            cityEditText.setText(demographicPayloadAddressModel.getCity());
            stateEditText.setText(demographicPayloadAddressModel.getState());
            zipCodeEditText.setText(StringUtil.formatZipCode(demographicPayloadAddressModel.getZipcode()));
            phoneNumberEditText.setText(StringUtil.formatPhoneNumber(demographicPayloadAddressModel.getPhone()));
            phoneNumberEditText.addTextChangedListener(new TextWatcher() {

                int length_before = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    length_before = s.length();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (length_before < s.length()) {
                        if (s.length() == 3 || s.length() == 7)
                            s.append("-");
                        if (s.length() > 3) {
                            if (Character.isDigit(s.charAt(3)))
                                s.insert(3, "-");
                        }
                        if (s.length() > 7) {
                            if (Character.isDigit(s.charAt(7)))
                                s.insert(7, "-");
                        }
                    }
                }
            });
        }
}

    private void getDemographicInformation() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicModel>() {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                DemographicModel demographicModel = response.body();
                if (demographicModel != null) {

                    demographicPayloadResponseModel = demographicModel.getPayload();
                    demographicProgressBar.setVisibility(View.GONE);
                    Log.d("sdadad", "adasdasdasd");
                    if (demographicPayloadResponseModel != null) {
                        DemographicPayloadInfoModel demographics = demographicPayloadResponseModel.getDemographics();


                        if (demographics != null) {
                            DemographicPayloadInfoMetaDataModel metadamodel = demographics.getMetadata();
                            DemographicPayloadInfoPayloadModel payloadinfomodel = demographics.getPayload();
                            if (metadamodel != null) {
                                emailEditText.setText(metadamodel.getUsername());
                            } else
                                Log.v(LOG_TAG, "demographic insurance model is null");

                            if (payloadinfomodel.getPersonalDetails() != null) {
                                demographicPayloadPersonalDetailsModel = payloadinfomodel.getPersonalDetails();
                                firstNameText.setText(demographicPayloadPersonalDetailsModel.getFirstName());
                                lastNameText.setText(demographicPayloadPersonalDetailsModel.getLastName());
                                String datetime = demographicPayloadPersonalDetailsModel.getDateOfBirth();
                                String[] date = datetime.split("T");
                                String dob = date[0];
                                String time = date[1];
                                dobEditText.setText(dob);
                                selectGender.setText(demographicPayloadPersonalDetailsModel.getGender());
//                                middleNameTextView.setText(demographicPayloadPersonalDetailsModel.getMiddleName());
                                selectlangauge.setText(demographicPayloadPersonalDetailsModel.getPreferredLanguage());
                                raceDataTextView.setText(demographicPayloadPersonalDetailsModel.getPrimaryRace());
                                ethnicityDataTextView.setText(demographicPayloadPersonalDetailsModel.getEthnicity());
                            } else
                                Log.v(LOG_TAG, "demographic personaldetail  model is null ");

                            if (payloadinfomodel.getAddress() != null) {
                               demographicPayloadAddressModel = payloadinfomodel.getAddress();
                                address1EditText.setText(demographicPayloadAddressModel.getAddress1());
                                address2EditText.setText(demographicPayloadAddressModel.getAddress2());
                                cityEditText.setText(demographicPayloadAddressModel.getCity());
                                stateEditText.setText(demographicPayloadAddressModel.getState());
                                zipCodeEditText.setText(demographicPayloadAddressModel.getZipcode());
                                phoneNumberEditText.setText(demographicPayloadAddressModel.getPhone());

                            } else
                                Log.v(LOG_TAG, "demographic Address model is null");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {
            }
        });
    }


    private void showAlertDialogWithListview(final String[] raceArray, String title) {
        Log.e("raceArray==", raceArray.toString());
        Log.e("raceArray 23==", Arrays.asList(raceArray).toString());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(raceArray));
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
                    case 4:
                        selectlangauge.setText(prefferedLangaugeArray[position]);
                        break;

                }
                alert.dismiss();
            }
        });
    }

    private void openNewFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(DemographicReviewFragment.class.getName());
        if(fragment == null) {
            fragment = ReviewFragment.newInstance();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.root_layout, fragment, DemographicReviewFragment.class.getName());
        transaction.addToBackStack("DemographicReviewFragment -> ReviewFragment");
        transaction.commit();

    }



    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsReviewHeading));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewdemogrPersonalInfoLabel));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsLabel));


        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListDataTextView));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.genderTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.chooseGenderTextView));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageListTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageTextView));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsAddressAddressSectionLabel));


        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityDataTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListDataTextView));
    }
}
