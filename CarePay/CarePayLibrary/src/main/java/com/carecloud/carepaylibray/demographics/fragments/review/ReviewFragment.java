
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.ConsentActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicReviewActivity;
import com.carecloud.carepaylibray.demographics.models.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoMetaDataDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

public class ReviewFragment extends Fragment implements View.OnClickListener {

    private View view;

    private Button correctInformationButton;
    private Button updateInformationUpdate;

    private TextView firstnameTextView;
    private TextView middlenameTextView;
    private TextView lastNameTextView;
    private TextView dobTExtView;
    private TextView phoneNumberTextView;
    private TextView genderTextView;
    private TextView driverLicenseTextView;
    private TextView raceTextView;
    private TextView ethnicityTextView;
    private TextView companyTextView;
    private TextView planTextView;
    private TextView policyNumberTextView;
    private TextView address1TextView;
    private TextView address2TextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipcodeTextView;

    private ProgressBar demographicProgressBar;

    private DemographicPayloadResponseDTO        demographicPayloadResponseDTO;
    private DemographicPayloadInfoModel          demographics;
    private DemographicPayloadInfoMetaDataDTO    metadamodel;
    private DemographicPayloadDTO                payloadinfomodel;
    private DemographicPersDetailsPayloadDTO     demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO         demographicAddressPayloadDTO;
    private DemographicInsurancePayloadDTO       demographicInsurancePayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO           demPayloadIdDocPojo;


    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public ReviewFragment() {
    }


    @Override
    public void onResume() {
        super.onResume();
        getDemographicInformation();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insurance_review, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DemographicReviewActivity.isFromReview = true;

        initialiseUIFields();
        setTypefaces(view);

        return view;
    }


    /**
     * .
     * get demographic information from the models
     * String token, String searchString
     */
    private void getDemographicInformation() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity()))
                .createService(DemographicService.class);
        Call<DemographicDTO> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicDTO>() {
            @Override
            public void onResponse(Call<DemographicDTO> call, Response<DemographicDTO> response) {
                demographicProgressBar.setVisibility(View.GONE);
                DemographicDTO demographicDTO = response.body();
                if (demographicDTO != null) {

                    demographicPayloadResponseDTO = demographicDTO.getPayload();
                    if (demographicPayloadResponseDTO != null) {
                        demographics = demographicPayloadResponseDTO.getDemographics();

                        if (demographics != null) {
                            metadamodel = demographics.getMetadata();
                            payloadinfomodel = demographics.getPayload();
                            if (payloadinfomodel.getPersonalDetails() != null) {
                                demographicPersDetailsPayloadDTO = payloadinfomodel.getPersonalDetails();

                                if (demographicPersDetailsPayloadDTO != null) {
                                    firstnameTextView.setText(demographicPersDetailsPayloadDTO.getFirstName());
                                    lastNameTextView.setText(demographicPersDetailsPayloadDTO.getLastName());

                                    String middleName = demographicPersDetailsPayloadDTO.getMiddleName();
                                    if (middleName != null) {
                                        middlenameTextView.setText(middleName);
                                    } else {
                                        Log.v(LOG_TAG, "middle name field is empty");
                                    }
                                    String datetime = demographicPersDetailsPayloadDTO.getDateOfBirth();
                                    if (datetime != null) {
                                        String dateOfBirthString = DateUtil.getInstance().setDateRaw(datetime).getDateAsMMddyyyy();
                                        dobTExtView.setText(dateOfBirthString);
                                    }
                                    genderTextView.setText(demographicPersDetailsPayloadDTO.getGender());
                                    raceTextView.setText(demographicPersDetailsPayloadDTO.getPrimaryRace());
                                    ethnicityTextView.setText(demographicPersDetailsPayloadDTO.getEthnicity());
                                }

                            } else {
                                Log.v(LOG_TAG, "demographic personal detail  model is null");
                            }

                            insurances = payloadinfomodel.getInsurances();
                            if (insurances != null && insurances.size() > 0) {
                                demographicInsurancePayloadDTO = insurances.get(0);
                                if (demographicInsurancePayloadDTO != null) {
                                    planTextView.setText(demographicInsurancePayloadDTO.getInsurancePlan());
                                    companyTextView.setText(demographicInsurancePayloadDTO.getInsuranceProvider());
                                    policyNumberTextView.setText(demographicInsurancePayloadDTO.getInsuranceMemberId());
                                }
                            } else {
                                Log.v(LOG_TAG, "demographic insurance model is null");
                            }

                            if (payloadinfomodel.getAddress() != null) {
                                demographicAddressPayloadDTO = payloadinfomodel.getAddress();
                                if (demographicAddressPayloadDTO != null) {
                                    address1TextView.setText(demographicAddressPayloadDTO.getAddress1());
                                    address2TextView.setText(demographicAddressPayloadDTO.getAddress2());
                                    cityTextView.setText(demographicAddressPayloadDTO.getCity());
                                    stateTextView.setText(demographicAddressPayloadDTO.getState());
                                    String zipcode = StringUtil.formatZipCode(
                                            demographicAddressPayloadDTO.getZipcode());
                                    zipcodeTextView.setText(zipcode);
                                    String phoneNumber = StringUtil.formatPhoneNumber(
                                            demographicAddressPayloadDTO.getPhone());
                                    phoneNumberTextView.setText(phoneNumber);
                                }
                            } else {
                                Log.v(LOG_TAG, "demographic Address model is null");
                            }
                            // fetch the id doc(s)
                            List<DemographicIdDocPayloadDTO> idDocsDTOs = payloadinfomodel.getIdDocuments();
                            if (idDocsDTOs != null && idDocsDTOs.size() > 0) {
                                demPayloadIdDocPojo = idDocsDTOs.get(0);
                                if(demPayloadIdDocPojo != null) {
                                    driverLicenseTextView.setText(demPayloadIdDocPojo.getIdNumber());
                                }
                            } else {
                                Log.v(LOG_TAG, "demographic Driver License model is null");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DemographicDTO> call, Throwable t) {
                demographicProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * .
     * Initializing the view
     */

    private void initialiseUIFields() {

        correctInformationButton = (Button) view.findViewById(R.id.YesCorrectButton);
        updateInformationUpdate = (Button) view.findViewById(R.id.needUpdateButton);
        correctInformationButton.setOnClickListener(this);
        updateInformationUpdate.setOnClickListener(this);
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        ethnicityTextView = (TextView) view.findViewById(R.id.reviewEthnicityTextView);
        raceTextView = (TextView) view.findViewById(R.id.reviewRaceTextView);
        firstnameTextView = (TextView) view.findViewById(R.id.reviewFirstNameTextView);

        //  Personal Deatails Model View
        middlenameTextView = (TextView) view.findViewById(R.id.reviewMiddelNameTextView);
        lastNameTextView = (TextView) view.findViewById(R.id.reviewLastNameTextView);
        dobTExtView = (TextView) view.findViewById(R.id.reviewDOBTextView);
        phoneNumberTextView = (TextView) view.findViewById(R.id.reviewPhoneNumberTextView);
        genderTextView = (TextView) view.findViewById(R.id.reviewGenderTextView);
        driverLicenseTextView = (TextView) view.findViewById(R.id.reviewDriverLicenseTextView);

        //  Address Model View
        address1TextView = (TextView) view.findViewById(R.id.reviewAddress1TextView);
        address2TextView = (TextView) view.findViewById(R.id.reviewAddress2TextView);
        cityTextView = (TextView) view.findViewById(R.id.reviewCityTextView);
        stateTextView = (TextView) view.findViewById(R.id.reviewStateTextView);
        zipcodeTextView = (TextView) view.findViewById(R.id.reviewZipcodeTextView);

        //  Insurance Model View
        planTextView = (TextView) view.findViewById(R.id.reviewPlanTextView);
        companyTextView = (TextView) view.findViewById(R.id.reviewCompanyTextView);
        policyNumberTextView = (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView);
    }

    /**
     * @param view on click listener
     */
    @Override
    public void onClick(View view) {
        if (view == correctInformationButton) {
            Intent intent = new Intent(getActivity(), ConsentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        } else if (view == updateInformationUpdate) {
            ((DemographicReviewActivity) getActivity())
                    .setDemographicAddressPayloadDTO(
                            demographicAddressPayloadDTO);
            ((DemographicReviewActivity) getActivity())
                    .setDemographicPersDetailsPayloadDTO(
                            demographicPersDetailsPayloadDTO);
            ((DemographicReviewActivity) getActivity())
                    .setInsurances(insurances);
            ((DemographicReviewActivity) getActivity())
                    .setDemographicPayloadIdDocDTO(
                            demPayloadIdDocPojo);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment fragment = DemographicReviewFragment.newInstance();
            transaction.replace(R.id.root_layout, fragment, ReviewFragment.class.getName());
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                                            R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.addToBackStack("ReviewFragment -> DemographicReviewFragment");
            transaction.commit();
        }
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewtitle));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewSubtitle));

        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.demographicSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.healthInsuranceSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewpersonalInformationLabel));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewAddress));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewRaceTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewEthnicityTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                                       (TextView) view.findViewById(R.id.reviewGenderTextView));


        //PN - extra Bold
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewCompanyLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewPlanLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewFirstNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewMiddleNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewLastNameLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewDOBLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewPhoneNumberLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewDriverLicenseLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewAddress1label));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewAddress2label));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewCityLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewStateLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewZipcodeLabel));
        setProximaNovaExtraboldTypeface(getActivity(),
                                        (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel));


        //PN-Regular
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewRaceLabel));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewEthnicityLabel));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewCompanyTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewPlanTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewFirstNameTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewLastNameTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewMiddelNameTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewDOBTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewPhoneNumberTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewGenderLabel));

        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewDriverLicenseTextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewAddress1TextView));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.reviewAddress2TextView));
    }
}
