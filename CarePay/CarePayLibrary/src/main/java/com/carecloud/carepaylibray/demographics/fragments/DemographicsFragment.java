package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
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
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private PatientModel demographicPersDetailsPayloadDTO;

    /**
     * gender listener
     */
    private View.OnClickListener genderListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.getProperties().getGender().getOptions()),
                    Label.getLabel("demographics_documents_title_select_gender"),
                    Label.getLabel("demographics_cancel_label"),
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
                    getOptionsFrom(persDetailsMetaDTO.getProperties().getPrimaryRace().getOptions()),
                    Label.getLabel("demographics_documents_title_select_race"),
                    Label.getLabel("demographics_cancel_label"),
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
                    getOptionsFrom(persDetailsMetaDTO.getProperties().getEthnicity().getOptions()) ,
                    Label.getLabel("demographics_documents_title_select_ethnicity"),
                    Label.getLabel("demographics_cancel_label"),
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

        initialiseUIFields(mainView);

        setTypefaces(mainView);

        initUiFromModels(mainView);

        checkIfEnableButton(mainView);

        return mainView;
    }

    @Override
    public void onResume(){
        super.onResume();
        stepProgressBar.setCurrentProgressDot(2);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 3);
        checkinFlowCallback.setCurrentStep(3);
    }

    /**
     * Set type faces
     */
    public void setTypefaces(View view) {
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
        initializeDemographicsDTO();
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
        setHeaderTitle(Label.getLabel("demographics_review_demographics"),
                Label.getLabel("demographics_demographics_heading"),
                Label.getLabel("demographics_demographics_subheading"),
                view);
        initNextButton(view);

        ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(Label.getLabel("demographics_choose"));
        ((TextView) view.findViewById(R.id.raceDataTextView)).setText(persDetailsMetaDTO.getProperties().getPrimaryRace().getLabel());
        view.findViewById(R.id.raceListDataTextView).setOnClickListener(raceListener);

        ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(Label.getLabel("demographics_choose"));
        ((TextView) view.findViewById(R.id.ethnicityDataTextView)).setText(persDetailsMetaDTO.getProperties().getEthnicity().getLabel());
        view.findViewById(R.id.ethnicityListDataTextView).setOnClickListener(ethnicityListener);

        ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(Label.getLabel("demographics_choose"));
        ((TextView) view.findViewById(R.id.genderTextView)).setText(persDetailsMetaDTO.getProperties().getGender().getLabel());
        view.findViewById(R.id.chooseGenderTextView).setOnClickListener(genderListener);
    }

    /**
     * Init ui from models
     */
    private void initUiFromModels(View view) {
        if (demographicPersDetailsPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getGender())) {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(demographicPersDetailsPayloadDTO.getGender());
            } else {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(Label.getLabel("demographics_choose"));
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getPrimaryRace())) {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(demographicPersDetailsPayloadDTO.getPrimaryRace());
            } else {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(Label.getLabel("demographics_choose"));
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getEthnicity())) {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(demographicPersDetailsPayloadDTO.getEthnicity());
            } else {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(Label.getLabel("demographics_choose"));
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
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().getDemographic().getPersonalDetails();

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
        boolean isGenderValid = !Label.getLabel("demographics_choose").equals(((TextView) view.findViewById(R.id.chooseGenderTextView)).getText().toString());
        boolean isEthnicityValid = !Label.getLabel("demographics_choose").equals(((TextView) view.findViewById(R.id.ethnicityListDataTextView)).getText().toString());
        boolean isRaceValid = !Label.getLabel("demographics_choose").equals(((TextView) view.findViewById(R.id.raceListDataTextView)).getText().toString());

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
