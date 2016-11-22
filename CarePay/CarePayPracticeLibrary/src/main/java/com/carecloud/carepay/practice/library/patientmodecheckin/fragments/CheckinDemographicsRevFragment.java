package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityAddressDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class CheckinDemographicsRevFragment extends Fragment implements View.OnClickListener {

   /* WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);

            // end-splash activity and transition
            // SplashActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };*/
    private View view;
    private Button correctInformationButton;
    private Button updateInformationUpdate;
    private TextView reviewTitleTextView;
    private TextView reviewSubtitileTextView;
    private TextView addressSectionTextView;
    private TextView peronalInfoSectionTextview;
    private TextView demographicSectionTextView;
    private TextView healthInsurance1SecionTextView;
    private TextView healthInsurance2SecionTextView;
    private TextView healthInsurance3SecionTextView;
    private TextView firstnameTextView;
    private TextView middlenameTextView;
    private TextView lastNameTextView;
    private TextView dobTExtView;
    private TextView phoneNumberTextView;
    private TextView genderTextView;
    private TextView driverLicenseTextView;
    private TextView raceTextView;
    private TextView ethnicityTextView;
    private TextView insurance1companyTextView;
    private TextView insurance1planTextView;
    private TextView insurance1policyNumberTextView;
    private TextView insurance2companyTextView;
    private TextView insurance2planTextView;
    private TextView insurance2policyNumberTextView;
    private TextView insurance3companyTextView;
    private TextView insurance3planTextView;
    private TextView insurance3policyNumberTextView;
    private TextView address1TextView;
    private TextView address2TextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipcodeTextView;
    private FrameLayout addressline2label;
    private View address2labelview;
    private TextView firstNameLabel;
    private TextView lastNameLabel;
    private TextView middleNameLabel;
    private TextView phoneNumberLabel;
    private TextView dobLabel;
    private TextView genderLabel;
    private TextView raceLabel;
    private TextView ethnicityLabel;
    private TextView insurance1companyLabel;
    private TextView insurance1planLabel;
    private TextView insurance1policyNumberLabel;
    private TextView address1Label;
    private TextView address2Label;
    private TextView stateLabel;
    private TextView cityLabel;
    private TextView zipcodeLabel;
    private TextView driverLicenseLabel;
    private TextView insurance2companyLabel;
    private TextView insurance2planLabel;
    private TextView insurance2policyNumberLabel;
    private TextView insurance3companyLabel;
    private TextView insurance3planLabel;
    private TextView insurance3policyNumberLabel;
    private LinearLayout healthInsurance2;
    private LinearLayout healthInsurance3;
    private ProgressBar demographicProgressBar;
    private DemographicDTO demographicDTO;


    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private DemographicInsurancePayloadDTO demographicInsurancePayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO idDocPayloadDTO;
    private DemographicInsurancePayloadDTO insuranceModel1;
    private DemographicInsurancePayloadDTO insuranceModel2;
    private DemographicInsurancePayloadDTO insuranceModel3;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private DemographicMetadataEntityAddressDTO addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicMetadataEntityIdDocsDTO idDocsMetaDTO;




    private boolean isPhoneEmpty;
    private int selectedDataArray;
    private DemographicIdDocPayloadDTO demographicIdDocPayloadDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;

    public CheckinDemographicsRevFragment() {
    }

    public static CheckinDemographicsRevFragment newInstance() {
        return new CheckinDemographicsRevFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateViewsFromModel();
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
toolbar.setVisibility(view.GONE);

     //   DemographicReviewActivity.isFromReview = true;
        //initModels();
        initializeDemographicsDTO();
        initialiseUIFields();
        setTypefaces(view);

        return view;
    }

    /**
     * Initialize the models from main Demographic Review Activity
     */

    private void initializeDemographicsDTO(){
        demographicDTO = ((PatientModeCheckinActivity) getActivity()).getDemographicDTO();
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        idDocsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.identityDocuments;
        insurancesMetaDTO=demographicDTO.getMetadata().getDataModels().demographic.insurances;

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicAddressPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getAddress();

            int size=demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for(int i=0;i>size;i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

    }

    private void populateViewsFromModel() {
        if (demographicPersDetailsPayloadDTO != null) {

            String firstname = demographicPersDetailsPayloadDTO.getFirstName();
            if (SystemUtil.isNotEmptyString(firstname)) {
                firstnameTextView.setText(firstname);
            }

            String middlename = demographicPersDetailsPayloadDTO.getMiddleName();
            if (SystemUtil.isNotEmptyString(middlename)) {
                middlenameTextView.setText(middlename);
            }

            String lastname = demographicPersDetailsPayloadDTO.getLastName();
            if (SystemUtil.isNotEmptyString(lastname)) {
                lastNameTextView.setText(lastname);
            }

            String datetime = demographicPersDetailsPayloadDTO.getDateOfBirth();
            if (SystemUtil.isNotEmptyString(datetime)) {
                String dateOfBirthString = DateUtil.getInstance().setDateRaw(datetime).getDateAsMMddyyyyWithSlash();
                dobTExtView.setText(dateOfBirthString);
            }

            String phoneNumber = demographicAddressPayloadDTO.getPhone();
            if (SystemUtil.isNotEmptyString(phoneNumber)) {
                phoneNumberTextView.setText(StringUtil.formatPhoneNumber(phoneNumber));
            }

            String gender = demographicPersDetailsPayloadDTO.getGender();
            if (SystemUtil.isNotEmptyString(gender) && !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(gender)) {
                genderTextView.setText(gender);
            } else {
                genderTextView.setText(" ");
            }

            String race = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (SystemUtil.isNotEmptyString(race) && !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(race)) {
                raceTextView.setText(race);
            } else {
                raceTextView.setText(" ");
            }

            String ethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (SystemUtil.isNotEmptyString(ethnicity) && !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(ethnicity)) {
                ethnicityTextView.setText(ethnicity);
            } else {
                ethnicityTextView.setText(" ");
            }

            String addresline1 = demographicAddressPayloadDTO.getAddress1();
            if (SystemUtil.isNotEmptyString(addresline1)) {
                address1TextView.setText(addresline1);
            }


            String addresline2 = demographicAddressPayloadDTO.getAddress2();
            if (SystemUtil.isNotEmptyString(addresline2)) {
                address2TextView.setText(addresline2);
            }

            String zipcode = demographicAddressPayloadDTO.getZipcode();
            if (SystemUtil.isNotEmptyString(zipcode)) {
                zipcodeTextView.setText(zipcode);
            }

            String state = demographicAddressPayloadDTO.getState();
            if (SystemUtil.isNotEmptyString(state)) {
                stateTextView.setText(state);
            }

            String city = demographicAddressPayloadDTO.getCity();
            if (SystemUtil.isNotEmptyString(city)) {
                cityTextView.setText(city);
            }


            if (insurances != null) {

                if (insurances.size() > 0 && insurances.get(0) != null) {

                    String plan1 = insurances.get(0).getInsurancePlan();
                    if (SystemUtil.isNotEmptyString(plan1)) {
                        insurance1planTextView.setText(plan1);
                    }
                    String company1 = insurances.get(0).getInsuranceProvider();
                    if (SystemUtil.isNotEmptyString(company1)) {
                        insurance1companyTextView.setText(company1);
                    }

                    String memberid = insurances.get(0).getInsuranceMemberId();
                    if (SystemUtil.isNotEmptyString(memberid)) {
                        insurance1policyNumberTextView.setText(memberid);
                    }
                    if (insurances.size() > 1 && insurances.get(1) != null) {

                        healthInsurance2.setVisibility(View.VISIBLE);
                        String plan2 = insurances.get(1).getInsurancePlan();
                        if (SystemUtil.isNotEmptyString(plan2)) {
                            insurance2planTextView.setText(plan2);
                        }
                        String company2 = insurances.get(1).getInsuranceProvider();
                        if (SystemUtil.isNotEmptyString(company2)) {
                            insurance2companyTextView.setText(company2);
                        }

                        String memberid2 = insurances.get(1).getInsuranceMemberId();
                        if (SystemUtil.isNotEmptyString(memberid2)) {
                            insurance2policyNumberTextView.setText(memberid2);
                        }

                        if (insurances.size() > 2 && insurances.get(2) != null) {

                            healthInsurance3.setVisibility(View.VISIBLE);
                            String plan3 = insurances.get(2).getInsurancePlan();
                            if (SystemUtil.isNotEmptyString(plan3)) {
                                insurance3planTextView.setText(plan3);
                            }
                            String company3 = insurances.get(2).getInsuranceProvider();
                            if (SystemUtil.isNotEmptyString(company3)) {
                                insurance3companyTextView.setText(company3);
                            }

                            String memberid3 = insurances.get(2).getInsuranceMemberId();
                            if (SystemUtil.isNotEmptyString(memberid3)) {
                                insurance3policyNumberTextView.setText(memberid3);
                            }

                        }
                    }
                }

            }
        }
    }

    /**
     * .
     * Initializing the view
     */

    private void initialiseUIFields() {

        correctInformationButton = (Button) view.findViewById(R.id.YesCorrectButton);
        correctInformationButton.setText(globalLabelsMetaDTO.getDemographicsReviewCorrectButton().toUpperCase());
        updateInformationUpdate = (Button) view.findViewById(R.id.needUpdateButton);
        updateInformationUpdate.setText(globalLabelsMetaDTO.getDemographicsReviewUpdateButton().toUpperCase());
        correctInformationButton.setOnClickListener(this);
        updateInformationUpdate.setOnClickListener(this);
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);

        reviewTitleTextView = (TextView) view.findViewById(R.id.reviewtitle);
        reviewTitleTextView.setText(globalLabelsMetaDTO.getDemographicsReviewScreenTitle());

        reviewSubtitileTextView = (TextView) view.findViewById(R.id.reviewSubtitle);
        reviewSubtitileTextView.setText(globalLabelsMetaDTO.getDemographicsReviewScreenSubtitle());

        peronalInfoSectionTextview = (TextView) view.findViewById(R.id.reviewpersonalInformationLabel);
        peronalInfoSectionTextview.setText(globalLabelsMetaDTO.getDemographicsReviewPeronsonalinfoSection().toUpperCase());

        addressSectionTextView = (TextView) view.findViewById(R.id.reviewAddress);
        addressSectionTextView.setText(globalLabelsMetaDTO.getDemographicsAddressSection().toUpperCase());

        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicSectionLabel);
        demographicSectionTextView.setText(globalLabelsMetaDTO.getDemographicSectionTitle().toUpperCase());

        healthInsurance1SecionTextView = (TextView) view.findViewById(R.id.healthInsurance1SubTitle);
        healthInsurance1SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance1Section().toUpperCase());

        healthInsurance2SecionTextView = (TextView) view.findViewById(R.id.healthInsurance2SubTitle);
        healthInsurance2SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance2Section().toUpperCase());

        healthInsurance3SecionTextView = (TextView) view.findViewById(R.id.healthInsurance3SubTitle);
        healthInsurance3SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance3Section().toUpperCase());


        //  Personal Deatails Model View
        firstNameLabel = (TextView) view.findViewById(R.id.reviewFirstNameLabel);
        firstNameLabel.setText(persDetailsMetaDTO.properties.firstName.getLabel().toUpperCase());
        firstnameTextView = (TextView) view.findViewById(R.id.reviewFirstNameTextView);

        middleNameLabel = (TextView) view.findViewById(R.id.reviewMiddleNameLabel);
        middleNameLabel.setText(persDetailsMetaDTO.properties.middleName.getLabel().toUpperCase());
        middlenameTextView = (TextView) view.findViewById(R.id.reviewMiddelNameTextView);

        lastNameLabel = (TextView) view.findViewById(R.id.reviewLastNameLabel);
        lastNameLabel.setText(persDetailsMetaDTO.properties.lastName.getLabel().toUpperCase());
        lastNameTextView = (TextView) view.findViewById(R.id.reviewLastNameTextView);

        dobLabel = (TextView) view.findViewById(R.id.reviewDOBLabel);
        dobLabel.setText(persDetailsMetaDTO.properties.dateOfBirth.getLabel().toUpperCase());
        dobTExtView = (TextView) view.findViewById(R.id.reviewDOBTextView);

        phoneNumberLabel = (TextView) view.findViewById(R.id.reviewPhoneNumberLabel);
        phoneNumberLabel.setText(addressMetaDTO.properties.phone.getLabel().toUpperCase());
        phoneNumberTextView = (TextView) view.findViewById(R.id.reviewPhoneNumberTextView);

        ethnicityLabel = (TextView) view.findViewById(R.id.reviewEthnicityLabel);
        ethnicityLabel.setText(persDetailsMetaDTO.properties.ethnicity.getLabel());
        ethnicityTextView = (TextView) view.findViewById(R.id.reviewEthnicityTextView);

        raceLabel = (TextView) view.findViewById(R.id.reviewRaceLabel);
        raceLabel.setText(persDetailsMetaDTO.properties.primaryRace.getLabel());
        raceTextView = (TextView) view.findViewById(R.id.reviewRaceTextView);

        genderLabel = (TextView) view.findViewById(R.id.reviewGenderLabel);
        genderLabel.setText(persDetailsMetaDTO.properties.gender.getLabel());
        genderTextView = (TextView) view.findViewById(R.id.reviewGenderTextView);

        driverLicenseLabel = (TextView) view.findViewById(R.id.reviewDriverLicenseLabel);
        driverLicenseLabel.setText(idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.options.get(0).getLabel().toUpperCase());
        driverLicenseTextView = (TextView) view.findViewById(R.id.reviewDriverLicenseTextView);

        //  Address Model View
        address1Label = (TextView) view.findViewById(R.id.reviewAddress1label);
        address1Label.setText(addressMetaDTO.properties.address1.getLabel().toUpperCase());
        address1TextView = (TextView) view.findViewById(R.id.reviewAddress1TextView);

        address2Label = (TextView) view.findViewById(R.id.reviewAddress2label);
        address2Label.setText(addressMetaDTO.properties.address2.getLabel().toUpperCase());
        address2TextView = (TextView) view.findViewById(R.id.reviewAddress2TextView);
        addressline2label = (FrameLayout) view.findViewById(R.id.address2layout);
        address2labelview = view.findViewById(R.id.address2labelview);

        cityLabel = (TextView) view.findViewById(R.id.reviewCityLabel);
        cityLabel.setText(addressMetaDTO.properties.city.getLabel().toUpperCase());
        cityTextView = (TextView) view.findViewById(R.id.reviewCityTextView);

        stateLabel = (TextView) view.findViewById(R.id.reviewStateLabel);
        stateLabel.setText(addressMetaDTO.properties.state.getLabel().toUpperCase());
        stateTextView = (TextView) view.findViewById(R.id.reviewStateTextView);

        zipcodeLabel = (TextView) view.findViewById(R.id.reviewZipcodeLabel);
        zipcodeLabel.setText(addressMetaDTO.properties.zipcode.getLabel().toUpperCase());
        zipcodeTextView = (TextView) view.findViewById(R.id.reviewZipcodeTextView);

        //  Insurance Model View

        healthInsurance2 = (LinearLayout) view.findViewById(R.id.insurnace2);
        healthInsurance3 = (LinearLayout) view.findViewById(R.id.insurance3);
        insurance1planLabel = (TextView) view.findViewById(R.id.reviewPlanLabel);
        insurance1planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance1planTextView = (TextView) view.findViewById(R.id.reviewPlanTextView);

        insurance1companyLabel = (TextView) view.findViewById(R.id.reviewCompanyLabel);
        insurance1companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance1companyTextView = (TextView) view.findViewById(R.id.reviewCompanyTextView);

        insurance1policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel);
        insurance1policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
        insurance1policyNumberTextView = (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView);

        insurance2planLabel = (TextView) view.findViewById(R.id.reviewPlan2Label);
        insurance2planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance2planTextView = (TextView) view.findViewById(R.id.reviewPlan2TextView);

        insurance3planLabel = (TextView) view.findViewById(R.id.reviewPlan3Label);
        insurance3planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance3planTextView = (TextView) view.findViewById(R.id.reviewPlan3TextView);

        insurance2companyLabel = (TextView) view.findViewById(R.id.reviewCompany2Label);
        insurance2companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance2companyTextView = (TextView) view.findViewById(R.id.reviewCompany2TextView);

        insurance3companyLabel = (TextView) view.findViewById(R.id.reviewCompany3Label);
        insurance3companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance3companyTextView = (TextView) view.findViewById(R.id.reviewCompany3TextView);

        insurance2policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsurance2CardNoLabel);
        insurance2policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
        insurance2policyNumberTextView = (TextView) view.findViewById(R.id.reviewInsurance2PolicyNoTextView);

        insurance3policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsurance3CardNoLabel);
        insurance3policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
        insurance3policyNumberTextView = (TextView) view.findViewById(R.id.reviewInsurance3PolicyNoTextView);

    }

        WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {

                CheckinConsentForm1Fragment fragment = new CheckinConsentForm1Fragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).toggleHighlight(PatientModeCheckinActivity.SUBFLOW_CONSENT, true);
                ((PatientModeCheckinActivity)getActivity()).changeCounterOfForm(PatientModeCheckinActivity.SUBFLOW_CONSENT, 1,
                        PatientModeCheckinActivity.NUM_CONSENT_FORMS);

                // end-splash activity and transition
                // SplashActivity.this.finish();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
            }
        };

    /**
     * @param view on click listener
     */
    @Override
    public void onClick(View view) {
        if (view == correctInformationButton){

            ((PatientModeCheckinActivity)getActivity()).toggleVisibleFormCounter(PatientModeCheckinActivity.SUBFLOW_CONSENT, true);
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt",demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt() );
            queries.put("practice_id",demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

            Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
            header.put("transition","true");

            Gson gson= new Gson();
            String demographicinfo=gson.toJson(demographicDTO.getPayload());
            TransitionDTO transitionDTO=demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
            WorkflowServiceHelper.getInstance().execute(transitionDTO, consentformcallback,demographicinfo,queries,header);


        } else if (view == updateInformationUpdate) {

            // transition
            CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
            ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
            ((PatientModeCheckinActivity)getActivity()).toggleVisibleBackButton(false);
          /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            WorkflowServiceHelper.getInstance().execute(demographicDTO.getMetadata().getTransitions().getUpdateDemographics(), consentformcallback);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            CheckinDemographicsFragment demographicReviewFragment = new CheckinDemographicsFragment();
            transaction.replace(R.id.root_layout, demographicReviewFragment, CheckinDemographicsRevFragment.class.getName());
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.addToBackStack("CheckinDemographicsRevFragment -> CheckinDemographicsFragment");
            transaction.commit();*/
        }
    }

   /* private void launchDemographics(ConsentFormDTO consentFormDTO) {
        // do to Demographics
        Intent intent = new Intent(getActivity(), ConsentActivity.class);
        if (consentFormDTO != null) {
            // pass the object into the gson
            Gson gson = new Gson();
            String dtostring = gson.toJson(consentFormDTO, ConsentFormDTO.class);
            intent.putExtra("consentform_model", dtostring);
            startActivity(intent);
        }

    }*/

    /*public DemographicMetadataEntityIdDocsDTO getIdDocsMetaDTO() {
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

    public DemographicMetadataEntityInsurancesDTO getInsurancesMetaDTO() {
        return insurancesMetaDTO;
    }

    public void setInsurancesMetaDTO(DemographicMetadataEntityInsurancesDTO insurancesMetaDTO) {
        this.insurancesMetaDTO = insurancesMetaDTO;
    }

    public DemographicMetadataEntityAddressDTO getAddressMetaDTO() {
        return addressMetaDTO;
    }

    public void setAddressMetaDTO(DemographicMetadataEntityAddressDTO addressMetaDTO) {
        this.addressMetaDTO = addressMetaDTO;
    }*/

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), reviewTitleTextView);
        setProximaNovaRegularTypeface(getActivity(), reviewSubtitileTextView);

        setProximaNovaSemiboldTypeface(getActivity(), demographicSectionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), healthInsurance1SecionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), healthInsurance2SecionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), healthInsurance3SecionTextView);
        setProximaNovaSemiboldTypeface(getActivity(), peronalInfoSectionTextview);
        setProximaNovaSemiboldTypeface(getActivity(), addressSectionTextView);
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.reviewRaceTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.reviewEthnicityTextView));
        setProximaNovaSemiboldTypeface(getActivity(),
                (TextView) view.findViewById(R.id.reviewGenderTextView));


        //PN - extra Bold
        setProximaNovaExtraboldTypeface(getActivity(), insurance1companyLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance2companyLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance3companyLabel);

        setProximaNovaExtraboldTypeface(getActivity(), insurance1planLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance2planLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance3planLabel);

        setProximaNovaExtraboldTypeface(getActivity(), insurance1policyNumberLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance2policyNumberLabel);
        setProximaNovaExtraboldTypeface(getActivity(), insurance3policyNumberLabel);

        setProximaNovaExtraboldTypeface(getActivity(), firstNameLabel);
        setProximaNovaExtraboldTypeface(getActivity(), middleNameLabel);
        setProximaNovaExtraboldTypeface(getActivity(), lastNameLabel);
        setProximaNovaExtraboldTypeface(getActivity(), dobLabel);
        setProximaNovaExtraboldTypeface(getActivity(), phoneNumberLabel);
        setProximaNovaExtraboldTypeface(getActivity(), driverLicenseLabel);
        setProximaNovaExtraboldTypeface(getActivity(), address1Label);
        setProximaNovaExtraboldTypeface(getActivity(), address2Label);
        setProximaNovaExtraboldTypeface(getActivity(), cityLabel);
        setProximaNovaExtraboldTypeface(getActivity(), stateLabel);
        setProximaNovaExtraboldTypeface(getActivity(), zipcodeLabel);


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
