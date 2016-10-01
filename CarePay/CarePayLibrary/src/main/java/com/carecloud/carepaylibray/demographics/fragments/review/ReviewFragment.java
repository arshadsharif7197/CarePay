package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadAddressModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoMetaDataModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.SystemUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

public class ReviewFragment extends Fragment implements View.OnClickListener {

    View view;
    Button correctInformationButton, updateInformationUpdate;
    TextView firstnameTextView, lastNameTextView, emailTextView, middleNameTextView,
            dobTExtView, phoneNumberTextView, genderTextView, driverLicenseTextView, prefferedLanguageTextView,
            raceTextView, ethnicityTextView, companyTextView, planTextView, policyNumberTextView,
            address1TextView, address2TextView, cityTextView, stateTextView, zipcodeTextView;
    DemographicPayloadResponseModel demographicPayloadResponseModel;
    ProgressBar demographicProgressBar;
    private DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel;
    private DemographicPayloadAddressModel demographicPayloadAddressModel;
    private DemographicPayloadInsuranceModel demographicPayloadInsuranceModel;


    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insurance_review, container, false);
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        ethnicityTextView = (TextView) view.findViewById(R.id.reviewEthnicityTextView);
        raceTextView = (TextView) view.findViewById(R.id.reviewRaceTextView);
        firstnameTextView = (TextView) view.findViewById(R.id.reviewFirstNameTextView);

        lastNameTextView = (TextView) view.findViewById(R.id.reviewLastNameTextView);
        emailTextView = (TextView) view.findViewById(R.id.reviewEmailTextView);
        dobTExtView = (TextView) view.findViewById(R.id.reviewDOBTextView);
        phoneNumberTextView = (TextView) view.findViewById(R.id.reviewPhoneNumberTextView);
        genderTextView = (TextView) view.findViewById(R.id.reviewGenderTextView);
        prefferedLanguageTextView = (TextView) view.findViewById(R.id.reviewPreferedLangugaeTextView);
        driverLicenseTextView = (TextView) view.findViewById(R.id.reviewDriverLicenseTextView);

        address1TextView = (TextView) view.findViewById(R.id.reviewAddress1TextView);
        address2TextView = (TextView) view.findViewById(R.id.reviewAddress2TextView);
        cityTextView = (TextView) view.findViewById(R.id.reviewCityTextView);
        stateTextView = (TextView) view.findViewById(R.id.reviewStateTextView);
        zipcodeTextView = (TextView) view.findViewById(R.id.reviewZipcodeTextView);

        planTextView = (TextView) view.findViewById(R.id.reviewPlanTextView);
        companyTextView = (TextView) view.findViewById(R.id.reviewCompanyTextView);
        policyNumberTextView = (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        getDemographicInformation();
        initialiseUIFields();
        setTypefaces(view);
        return view;
    }

    private void getDemographicInformation() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicModel>() {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                demographicProgressBar.setVisibility(View.GONE);
                DemographicModel demographicModel = response.body();
                if (demographicModel != null) {

                    demographicPayloadResponseModel = demographicModel.getPayload();

                    Log.d("sdadad", "adasdasdasd");
                    if (demographicPayloadResponseModel != null) {
                        DemographicPayloadInfoModel demographics = demographicPayloadResponseModel.getDemographics();


                        if (demographics != null) {
                            DemographicPayloadInfoMetaDataModel metadamodel = demographics.getMetadata();
                            DemographicPayloadInfoPayloadModel payloadinfomodel = demographics.getPayload();
                            if (metadamodel != null) {
                                emailTextView.setText(metadamodel.getUsername());
                            } else
                                Log.v(LOG_TAG, "demographic insurance model is null");

                            if (payloadinfomodel.getPersonalDetails() != null) {
                                demographicPayloadPersonalDetailsModel = payloadinfomodel.getPersonalDetails();

                                if(demographicPayloadPersonalDetailsModel != null) {
                                    firstnameTextView.setText(demographicPayloadPersonalDetailsModel.getFirstName());
                                    lastNameTextView.setText(demographicPayloadPersonalDetailsModel.getLastName());
                                    String datetime = demographicPayloadPersonalDetailsModel.getDateOfBirth();
                                    if (datetime != null) {
                                        String[] date = datetime.split("T");
                                        String dob = date[0];
                                        String time = date[1];
                                        dobTExtView.setText(dob);
                                        genderTextView.setText(demographicPayloadPersonalDetailsModel.getGender());
                                    }
                                    prefferedLanguageTextView.setText(demographicPayloadPersonalDetailsModel.getPreferredLanguage());
                                    raceTextView.setText(demographicPayloadPersonalDetailsModel.getPrimaryRace());
                                    ethnicityTextView.setText(demographicPayloadPersonalDetailsModel.getEthnicity());
                                }

                            } else
                                Log.v(LOG_TAG, "demographic personaldetail  model is null ");
                            if (payloadinfomodel.getInsurances() != null) {
                                for (DemographicPayloadInsuranceModel demographicPayloadInsuranceModel : demographics.getPayload().getInsurances()) {
                                    planTextView.setText(demographicPayloadInsuranceModel.getInsurancePlan());
                                    companyTextView.setText(demographicPayloadInsuranceModel.getInsuranceProvider());
                                    policyNumberTextView.setText(demographicPayloadInsuranceModel.getInsuranceMemberId());
                                }
                            } else
                                Log.v(LOG_TAG, "demographic insurance model is null");

                            if (payloadinfomodel.getAddress() != null) {
                                demographicPayloadAddressModel = payloadinfomodel.getAddress();
                                if(demographicPayloadAddressModel != null) {
                                    address1TextView.setText(demographicPayloadAddressModel.getAddress1());
                                    address2TextView.setText(demographicPayloadAddressModel.getAddress2());
                                    cityTextView.setText(demographicPayloadAddressModel.getCity());
                                    stateTextView.setText(demographicPayloadAddressModel.getState());
                                    zipcodeTextView.setText(demographicPayloadAddressModel.getZipcode());
                                    phoneNumberTextView.setText(demographicPayloadAddressModel.getPhone());
                                }


                            } else
                                Log.v(LOG_TAG, "demographic Address model is null");
                            if (payloadinfomodel.getDriversLicense() != null) {
                                DemographicPayloadDriversLicenseModel demographicPayloadDriversLicenseModel = payloadinfomodel.getDriversLicense();
                                driverLicenseTextView.setText(demographicPayloadDriversLicenseModel.getLicenseNumber());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {
                demographicProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initialiseUIFields() {

        correctInformationButton = (Button) view.findViewById(R.id.YesCorrectButton);
        updateInformationUpdate = (Button) view.findViewById(R.id.needUpdateButton);
        correctInformationButton.setOnClickListener(this);
        updateInformationUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == correctInformationButton) {
            Intent intent = new Intent(getActivity(), ConsentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
        if (view == updateInformationUpdate) {
            ((DemographicReviewActivity) getActivity()).setDemographicPayloadAddressModel(demographicPayloadAddressModel);
            ((DemographicReviewActivity) getActivity()).setDemographicPayloadPersonalDetailsModel(demographicPayloadPersonalDetailsModel);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = DemographicReviewFragment.newInstance();
            transaction.replace(R.id.root_layout, fragment, ReviewFragment.class.getName());
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.addToBackStack("ReviewFragment -> DemographicReviewFragment");
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewtitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewSubtitle));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.healthInsuranceSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewpersonalInformationLabel));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewAddress));


        //PN - extra Bold

        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewFirstNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewMiddleNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewLastNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEmailLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewDOBLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPhoneNumberLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewGenderLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPreferedLanguageLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewDriverLicenseLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewAddress1label));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewAddress2label));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCityLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewStateLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewZipcodeLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCountryLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel));


        //PN-Regular
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewFirstNameTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewLastNameTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewMiddelNameTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEmailTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewDOBTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPhoneNumberTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewGenderTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPreferedLangugaeTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewDriverLicenseTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewAddress1TextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewAddress2TextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCityTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewStateTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewZipcodeTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCountryTextView));


        //GNRM
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.YesCorrectButton));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.needUpdateButton));

    }

}
