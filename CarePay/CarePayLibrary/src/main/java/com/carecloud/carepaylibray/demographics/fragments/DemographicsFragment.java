package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link CheckInDemographicsBaseFragment} subclass.
 */
public class DemographicsFragment extends CheckInDemographicsBaseFragment  {

    private DemographicDTO demographicDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private PatientModel demographicPersDetailsPayloadDTO;


    /**
     * gender listener
     */
    private View.OnClickListener genderListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.gender.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectGender(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.chooseGenderTextView),
                    getView());
        }
    };

    /**
     * race listener
     */
    private View.OnClickListener raceListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.primaryRace.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectRace(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.raceListDataTextView),
                    getView());
        }
    };

    /**
     * ethnicity listener
     */
    private View.OnClickListener ethnicityListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.ethnicity.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectEthnicity(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.ethnicityListDataTextView),
                    getView());
        }
    };

    /**
     * constructor
     */
    public DemographicsFragment() {
        // Required empty public constructor
    }



    /**
     * on create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView  = super.onCreateView(inflater, container, savedInstanceState);

        initializeDemographicsDTO();

        initialiseUIFields(mainView);

        setTypefaces(mainView);

        initUiFromModels(mainView);

        checkIfEnableButton(mainView);

        stepProgressBar.setCurrentProgressDot(2);

        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 3);

        return mainView;
    }


    /**
     * Set type faces
     */
    public void setTypefaces(View view){

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.raceDataTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), ((TextView) view.findViewById(R.id.raceListDataTextView)));

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.genderTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.chooseGenderTextView));

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.ethnicityDataTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), ((TextView) view.findViewById(R.id.ethnicityListDataTextView)));
    }



    /**
     * on create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    /**
     * Update demographic dto
     */
    @Override
    protected DemographicDTO updateDemographicDTO(View view) {

        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.setPayload(new DemographicPayloadResponseDTO());
        updatableDemographicDTO.getPayload().setDemographics(new DemographicPayloadInfoDTO());
        updatableDemographicDTO.getPayload().getDemographics().setPayload(new DemographicPayloadDTO());

        String gender = ((TextView)findViewById(R.id.chooseGenderTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(gender)) {
            demographicPersDetailsPayloadDTO.setGender(gender);
        }

        String race = ((TextView) findViewById(R.id.raceListDataTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(race)) {
            demographicPersDetailsPayloadDTO.setPrimaryRace(race);
        }

        String ethnicity = ((TextView) findViewById(R.id.ethnicityListDataTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity)) {
            demographicPersDetailsPayloadDTO.setEthnicity(ethnicity);
        }

        updatableDemographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        return updatableDemographicDTO;

    }

    /**
     * Init ui fields
     */
    private void initialiseUIFields(View view) {

        setHeaderTitle(globalLabelsMetaDTO.getDemographicsReviewDemographics(), view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view, View.VISIBLE);

        ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.raceDataTextView)).setText(persDetailsMetaDTO.properties.primaryRace.getLabel());
        view.findViewById(R.id.raceListDataTextView).setOnClickListener(raceListener);

        ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.ethnicityDataTextView)).setText(persDetailsMetaDTO.properties.ethnicity.getLabel());
        view.findViewById(R.id.ethnicityListDataTextView).setOnClickListener(ethnicityListener);

        ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.genderTextView)).setText(persDetailsMetaDTO.properties.gender.getLabel());
        view.findViewById(R.id.chooseGenderTextView).setOnClickListener(genderListener);

    }

    /**
     * Init ui from models
     */
    private void initUiFromModels(View view){

        if (demographicPersDetailsPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getGender())) {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(demographicPersDetailsPayloadDTO.getGender());
            } else {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getPrimaryRace())) {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(demographicPersDetailsPayloadDTO.getPrimaryRace());
            } else {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getEthnicity())) {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(demographicPersDetailsPayloadDTO.getEthnicity());
            } else {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

        }

        hideSoftKeyboard(getActivity());
    }



    /**
     * on attach
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    /**
     * Init data
     * */
    private void initializeDemographicsDTO() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Get options
     */
    private String[] getOptionsFrom(List<MetadataOptionDTO> options){
        List<String> strOptions = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            strOptions.add(o.getLabel());
        }
        return strOptions.toArray(new String[0]);
    }

    /**
     * Show dialog
     * */
    private void showDialog(final String[] dataArray, String title, String cancelLabel, final TextView editText, final View view){
        SystemUtil.showChooseDialog(getActivity(),
                dataArray, title, cancelLabel,
                editText,
                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        editText.setText(selectedOption);
                        checkIfEnableButton(view);
                    }
                });
    }






    /**
     * Pass contraints
     */
    @Override
    protected boolean passConstraints(View view) {

        boolean isGenderValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(  ((TextView) view.findViewById(R.id.chooseGenderTextView)).getText().toString());
        boolean isEthnicityValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals( ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).getText().toString());
        boolean isRaceValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(  ((TextView) view.findViewById(R.id.raceListDataTextView)).getText().toString());

        return isGenderValid && isEthnicityValid && isRaceValid;

    }

    /**
     * Get content id
     */
    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_demographics;
    }



}
