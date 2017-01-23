package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
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
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity.SUBFLOW_DEMOGRAPHICS_INS;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckinDemographicsRevFragment extends BaseCheckinFragment implements View.OnClickListener {

    private static final int MAX_INSURANCES = 3;

    WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            demographicProgressBar.setVisibility(View.GONE);
            PracticeNavigationHelper.getInstance().navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private View                                    view;
    private Button                                  correctInformationButton;
    private Button                                  updateInformationUpdate;
    private TextView                                reviewTitleTextView;
    private TextView                                reviewSubtitileTextView;
    private TextView                                addressSectionTextView;
    private TextView                                peronalInfoSectionTextview;
    private TextView                                demographicSectionTextView;
    private TextView                                healthInsurance1SecionTextView;
    private TextView                                healthInsurance2SecionTextView;
    private TextView                                healthInsurance3SecionTextView;
    private TextView                                firstnameTextView;
    private TextView                                middlenameTextView;
    private TextView                                lastNameTextView;
    private TextView                                dobTExtView;
    private TextView                                phoneNumberTextView;
    private TextView                                genderTextView;
    private TextView                                raceTextView;
    private TextView                                ethnicityTextView;
    private TextView                                address1TextView;
    private TextView                                address2TextView;
    private TextView                                cityTextView;
    private TextView                                stateTextView;
    private TextView                                zipcodeTextView;
    private TextView                                firstNameLabel;
    private TextView                                lastNameLabel;
    private TextView                                middleNameLabel;
    private TextView                                phoneNumberLabel;
    private TextView                                dobLabel;
    private TextView                                genderLabel;
    private TextView                                raceLabel;
    private TextView                                ethnicityLabel;
    private TextView                                insurance1companyLabel;
    private TextView                                insurance1planLabel;
    private TextView                                insurance1policyNumberLabel;
    private TextView                                address1Label;
    private TextView                                address2Label;
    private TextView                                stateLabel;
    private TextView                                cityLabel;
    private TextView                                zipcodeLabel;
    private TextView                                driverLicenseLabel;
    private TextView                                insurance2companyLabel;
    private TextView                                insurance2planLabel;
    private TextView                                insurance2policyNumberLabel;
    private TextView                                insurance3companyLabel;
    private TextView                                insurance3planLabel;
    private TextView                                insurance3policyNumberLabel;
    private ProgressBar                             demographicProgressBar;
    private DemographicDTO                          demographicDTO;
    private DemographicPersDetailsPayloadDTO        demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO            demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO>    insurances;
    private DemographicLabelsDTO                    globalLabelsMetaDTO;
    private DemographicMetadataEntityAddressDTO     addressMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicMetadataEntityIdDocsDTO      idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO  insurancesMetaDTO;
    private TextView[]                              companyTextViews;
    private TextView[]                              planTextViews;
    private TextView[]                              policyTextViews;
    private LinearLayout[]                          insContainers;
    private DemographicIdDocPayloadDTO              demographicIdDocPayloadDTO;
    private TextView                                getDriverLicenseTextView;

    public CheckinDemographicsRevFragment() {
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

        initializeDemographicsDTO();
        initialiseUIFields();
        setTypefaces(view);

        return view;
    }

    /**
     * Initialize the models from main Demographic Review Activity
     */
    private void initializeDemographicsDTO() {
        // fetch the main DTO
        demographicDTO = ((PatientModeCheckinActivity) getActivity()).getDemographicDTO();

        // fetch the metadata
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        addressMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.address;
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;
        idDocsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.identityDocuments;
        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;

        // get the payloads
        DemographicPayloadInfoDTO demographicPayloadInfoDTO = demographicDTO.getPayload().getDemographics();
        if (demographicPayloadInfoDTO != null) {
            DemographicPayloadDTO payload = demographicPayloadInfoDTO.getPayload();
            if (payload != null) {
                // get personal
                demographicPersDetailsPayloadDTO = payload.getPersonalDetails();
                // get address
                demographicAddressPayloadDTO = payload.getAddress();
                // get id docs

                List<DemographicIdDocPayloadDTO> idDocs = payload.getIdDocuments();
                if (idDocs != null) {
                    int size = payload.getIdDocuments().size();
                    if (size > 0) {
                        demographicIdDocPayloadDTO = idDocs.get(0);
                    }
                }
                // get insurances
                insurances = payload.getInsurances();
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
                DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_FILTER_DATE_FORMAT);
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

            String driversLicense =  demographicIdDocPayloadDTO==null?"":demographicIdDocPayloadDTO.getIdNumber();
            if (SystemUtil.isNotEmptyString(driversLicense)) {
                getDriverLicenseTextView.setText(driversLicense);
            }

            // initializeInsurances from the model
            initializeInsuranceFromModel();
        }
    }

    private void initializeInsuranceFromModel() {

        if (insurances != null) {
            for (int i = 0; i < insurances.size()-1; i++) {
                DemographicInsurancePayloadDTO insurance = insurances.get(i);

                String plan = insurance.getInsurancePlan();
                if (SystemUtil.isNotEmptyString(plan)) {
                    planTextViews[i].setText(plan);
                }
                String company = insurance.getInsuranceProvider();
                if (SystemUtil.isNotEmptyString(company)) {
                    companyTextViews[i].setText(company);
                }
                String memberid = insurance.getInsuranceMemberId();
                if (SystemUtil.isNotEmptyString(memberid)) {
                    policyTextViews[i].setText(memberid);
                }
                insContainers[i].setVisibility(View.VISIBLE);
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

        getDriverLicenseTextView = (TextView) view.findViewById(R.id.reviewDriverLicenseTextView);

        demographicSectionTextView = (TextView) view.findViewById(R.id.demographicSectionLabel);
        demographicSectionTextView.setText(globalLabelsMetaDTO.getDemographicSectionTitle().toUpperCase());

        healthInsurance1SecionTextView = (TextView) view.findViewById(R.id.healthInsurance1SubTitle);
        healthInsurance1SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance1Section().toUpperCase());

        healthInsurance2SecionTextView = (TextView) view.findViewById(R.id.healthInsurance2SubTitle);
        healthInsurance2SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance2Section().toUpperCase());

        healthInsurance3SecionTextView = (TextView) view.findViewById(R.id.healthInsurance3SubTitle);
        healthInsurance3SecionTextView.setText(globalLabelsMetaDTO.getDemographicsHealthinsurance3Section().toUpperCase());

        //  Personal Deatails Model View
        initializePersonalDetailsSectionView();

        //  Address Model View
        initializeAddressSectionView();

        //  Insurance Model View
        initializeInsuranceSectionView();
    }

    private void initializePersonalDetailsSectionView() {
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
    }

    private void initializeAddressSectionView() {
        address1Label = (TextView) view.findViewById(R.id.reviewAddress1label);
        address1Label.setText(addressMetaDTO.properties.address1.getLabel().toUpperCase());
        address1TextView = (TextView) view.findViewById(R.id.reviewAddress1TextView);

        address2Label = (TextView) view.findViewById(R.id.reviewAddress2label);
        address2Label.setText(addressMetaDTO.properties.address2.getLabel().toUpperCase());
        address2TextView = (TextView) view.findViewById(R.id.reviewAddress2TextView);

        cityLabel = (TextView) view.findViewById(R.id.reviewCityLabel);
        cityLabel.setText(addressMetaDTO.properties.city.getLabel().toUpperCase());
        cityTextView = (TextView) view.findViewById(R.id.reviewCityTextView);

        stateLabel = (TextView) view.findViewById(R.id.reviewStateLabel);
        stateLabel.setText(addressMetaDTO.properties.state.getLabel().toUpperCase());
        stateTextView = (TextView) view.findViewById(R.id.reviewStateTextView);

        zipcodeLabel = (TextView) view.findViewById(R.id.reviewZipcodeLabel);
        zipcodeLabel.setText(addressMetaDTO.properties.zipcode.getLabel().toUpperCase());
        zipcodeTextView = (TextView) view.findViewById(R.id.reviewZipcodeTextView);
    }

    private void initializeInsuranceSectionView() {
        insContainers = new LinearLayout[MAX_INSURANCES];
        insContainers[0] = (LinearLayout) view.findViewById(R.id.insurance1);
        insContainers[1] = (LinearLayout) view.findViewById(R.id.insurnace2);
        insContainers[2] = (LinearLayout) view.findViewById(R.id.insurance3);

        insurance1companyLabel = (TextView) view.findViewById(R.id.reviewCompanyLabel);
        insurance2companyLabel = (TextView) view.findViewById(R.id.reviewCompany2Label);
        insurance3companyLabel = (TextView) view.findViewById(R.id.reviewCompany3Label);

        insurance1planLabel = (TextView) view.findViewById(R.id.reviewPlanLabel);
        insurance2planLabel = (TextView) view.findViewById(R.id.reviewPlan2Label);
        insurance3planLabel = (TextView) view.findViewById(R.id.reviewPlan3Label);

        insurance1policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel);
        insurance2policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsurance2CardNoLabel);
        insurance3policyNumberLabel = (TextView) view.findViewById(R.id.reviewInsurance3CardNoLabel);

        companyTextViews = new TextView[MAX_INSURANCES];
        companyTextViews[0] = (TextView) view.findViewById(R.id.reviewCompanyTextView);
        companyTextViews[1] = (TextView) view.findViewById(R.id.reviewCompany2TextView);
        companyTextViews[2] = (TextView) view.findViewById(R.id.reviewCompany3TextView);

        planTextViews = new TextView[MAX_INSURANCES];
        planTextViews[0] = (TextView) view.findViewById(R.id.reviewPlanTextView);
        planTextViews[1] = (TextView) view.findViewById(R.id.reviewPlan2TextView);
        planTextViews[2] = (TextView) view.findViewById(R.id.reviewPlan3TextView);

        policyTextViews = new TextView[MAX_INSURANCES];
        policyTextViews[0] = (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView);
        policyTextViews[1] = (TextView) view.findViewById(R.id.reviewInsurance2PolicyNoTextView);
        policyTextViews[2] = (TextView) view.findViewById(R.id.reviewInsurance3PolicyNoTextView);

        insContainers[0].setVisibility(View.GONE);
        insContainers[1].setVisibility(View.GONE);
        insContainers[2].setVisibility(View.GONE);

        insurance1policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
        insurance1planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance1companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance2planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance2companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance2policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
        insurance3planLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insurancePlan.getLabel().toUpperCase());
        insurance3companyLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceProvider.getLabel().toUpperCase());
        insurance3policyNumberLabel.setText(insurancesMetaDTO.properties.items.insurance.properties.insuranceMemberId.getLabel().toUpperCase());
    }

    /**
     * @param view on click listener
     */
    @Override
    public void onClick(View view) {
        if (view == correctInformationButton) {
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
            queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

            Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
            header.put("transition", "true");
            header.put("username_patient", demographicDTO.getPayload().getDemographics().getMetadata().getUsername());

            Gson gson = new Gson();
            String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
            TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
            ApplicationPreferences.Instance.saveObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                    demographicDTO.getPayload().getDemographics().getPayload().getAddress());

            WorkflowServiceHelper.getInstance().execute(transitionDTO, consentformcallback, demogrPayloadString, queries, header);

        } else if (view == updateInformationUpdate) {
            // transition
            CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
            ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
            ((PatientModeCheckinActivity) getActivity()).toggleVisibleBackButton(false);
        }
    }

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowStateInfo = new FlowStateInfo(SUBFLOW_DEMOGRAPHICS_INS, 0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PatientModeCheckinActivity)getActivity()).toggleVisibleBackButton(false);
        ((PatientModeCheckinActivity)getActivity()).updateSection(flowStateInfo);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((PatientModeCheckinActivity)getActivity()).toggleVisibleBackButton(true);
    }
}
