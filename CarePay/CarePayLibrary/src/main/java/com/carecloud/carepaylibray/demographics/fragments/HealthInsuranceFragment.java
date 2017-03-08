package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 07/02/17.
 */

public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment {
    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO demographicDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private InsuranceDocumentScannerListener documentCallback;
    private boolean isPractice;

//    private View.OnClickListener addNewElementListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            DemographicInsurancePayloadDTO insurance = new DemographicInsurancePayloadDTO();
//            insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
//            insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
//            documentCallback.navigateToInsuranceDocumentFragment(CarePayConstants.NO_INDEX, insurance);
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        isPractice = getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);

        initDTOs();
        initLabels(view);
//        initTitle(view);
//        initSwitch(view);
        initActiveSection(view);

        SystemUtil.hideSoftKeyboard(getActivity());
        return view;
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_health_insurance_main;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        return new DemographicDTO();
    }

//    private void initTitle(View view){
//        TextView healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.insurancesTitleLabel);
//        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceToolbarTitle().toUpperCase());
//        SystemUtil.setProximaNovaSemiboldTypeface(getContext(), healthInsuranceTitleTextView);
//    }

    private void initLabels(View view) {
        // Set Labels
        ((TextView) view.findViewById(R.id.health_insurance_provider_label)).setText(globalLabelsMetaDTO.getDemographicsTitleSelectProvider());
        ((TextView) view.findViewById(R.id.health_insurance_plan_label)).setText(globalLabelsMetaDTO.getDemographicsTitleSelectPlan());
        ((TextView) view.findViewById(R.id.health_insurance_type_label)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTypeLabel());

        ((EditText) view.findViewById(R.id.health_insurance_card_number)).setHint(globalLabelsMetaDTO.getDemographicsInsuranceCardNumber());
        ((EditText) view.findViewById(R.id.health_insurance_group_number)).setHint(globalLabelsMetaDTO.getDemographicsInsuranceGroupNumber());

        ((Button) view.findViewById(R.id.take_front_photo_button)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTakeFrontPhotoLabel());
        ((Button) view.findViewById(R.id.take_back_photo_button)).setText(globalLabelsMetaDTO.getDemographicsInsuranceTakeBackPhotoLabel());
        ((Button) view.findViewById(R.id.health_insurance_dont_have_button)).setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsDontHaveOneButtonLabel());
        ((Button) view.findViewById(R.id.health_insurance_add_new_button)).setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddNewButtonLabel());
    }

    /**
     * enable or disable sections
     * @param view main view
     */
    public void initActiveSection(View view) {
        setHeaderTitle(globalLabelsMetaDTO.getDemographicsReviewPeronsonalinfoSection(), view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view);

        // Set Values
        if (insurancesMetaDTO != null) {
            DemographicMetadataPropertiesInsuranceDTO properties = insurancesMetaDTO.properties.items.insurance.properties;

            // Providers
            List<MetadataOptionDTO> providerList = properties.insuranceProvider.options;
            Spinner providers = (Spinner) view.findViewById(R.id.health_insurance_providers);

            ArrayAdapter<MetadataOptionDTO> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, providerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            providers.setAdapter(adapter);

            // Plans
            List<MetadataOptionDTO> planList = properties.insurancePlan.options;
            Spinner plans = (Spinner) view.findViewById(R.id.health_insurance_plans);

            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, planList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            plans.setAdapter(adapter);

            // Types
            List<MetadataOptionDTO> typeList = properties.insuranceType.options;
            Spinner types = (Spinner) view.findViewById(R.id.health_insurance_types);

            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, typeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            types.setAdapter(adapter);
        }

//        boolean loadResources = insurancePayloadDTOs.size() > 0;
//        boolean isSetup = !isPractice && !loadResources;
//        boolean isRatio = isPractice && !loadResources;
//        view.findViewById(R.id.setupContainer).setVisibility( isSetup ? View.VISIBLE : View.GONE );
//        view.findViewById(R.id.setupInsurancePracticeContainer).setVisibility( isRatio ? View.VISIBLE : View.GONE );
//        view.findViewById(R.id.existingContainer).setVisibility(loadResources? View.VISIBLE : View.GONE);
//        if( loadResources ){
//            fillDetailAdapter(view);
//            initAddButton(view);
//        } else if(!isPractice){
//            ((TextView)view.findViewById(R.id.setupInsuranceLabel)).setText(globalLabelsMetaDTO.getDemographicsSetupInsuranceTitle());
//            TextView setup = (TextView)view.findViewById(R.id.setupLabel);
//            setup.setText(globalLabelsMetaDTO.getDemographicsSetupInsuranceLabel());
//            setup.setOnClickListener(addNewElementListener);
//
//        } else {
//            initAddOtherButton(view);
//            RadioButton dontHaveInsurance = (RadioButton)view.findViewById(R.id.dontHaveInsurance);
//            dontHaveInsurance.setText(globalLabelsMetaDTO.getDemographicsDontHaveHealthInsuranceLabel());
//            RadioButton haveInsurance = (RadioButton)view.findViewById(R.id.haveInsurance);
//            haveInsurance.setText(globalLabelsMetaDTO.getDemographicsHaveHealthInsuranceLabel());
//            final Button addButton = (Button)view.findViewById(R.id.addNewButton);
//            haveInsurance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
//                    addButton.setEnabled(on);
//                    documentCallback.disableMainButton(on);
//                }
//            });
//
//        }
    }

//    private void initAddButton(View view){
//        Button addButton = (Button)view.findViewById(R.id.addInsuranceButton);
//        addButton.setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddAnother());
//        addButton.setOnClickListener(addNewElementListener);
//        if(insurancePayloadDTOs.size() >= CarePayConstants.MAX_INSURANCE_DOC){
//            addButton.setEnabled(false);
//        }
//    }
//
//    private void initAddOtherButton(View view){
//        Button addButton = (Button)view.findViewById(R.id.addNewButton);
//        addButton.setText(globalLabelsMetaDTO.getDemographicsAddHealthInsuranceButtonTitle());
//        addButton.setOnClickListener(addNewElementListener);
//    }
//
//    protected void fillDetailAdapter(View view){
//        RecyclerView detailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.insurance_detail_list));
//        detailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//
//        InsuranceLineItemsListAdapter adapter = new InsuranceLineItemsListAdapter(this.getContext(), demographicDTO, insurancePayloadDTOs, documentCallback);
//        detailsListRecyclerView.setAdapter(adapter);
//    }
//
//    private void initSwitch(View view) {
//        final LinearLayout setupInsuranceHolders = (LinearLayout) view.findViewById(R.id.setupInsuranceHolders);
//        final SwitchCompat doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
//        getChildFragmentManager().executePendingTransactions();
//
//        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
//                setupInsuranceHolders.setVisibility(on ? View.VISIBLE : View.GONE);
//                documentCallback.disableMainButton(on);
//            }
//        });
//        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
//        doYouHaveInsuranceSwitch.setText(label);
//    }

    private void initDTOs() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();

        insurancePayloadDTOs = new ArrayList<>(); // init in case no payload
        DemographicPayloadResponseDTO payloadResponseDTO = demographicDTO.getPayload();
        if (payloadResponseDTO != null) {
            DemographicPayloadInfoDTO demographicPayloadInfoDTO = payloadResponseDTO.getDemographics();
            if (demographicPayloadInfoDTO != null) {
                DemographicPayloadDTO payloadDTO = demographicPayloadInfoDTO.getPayload();
                if (payloadDTO != null) {
                    insurancePayloadDTOs = payloadDTO.getInsurances();
                }
            }
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            documentCallback = (InsuranceDocumentScannerListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement InsuranceDocumentScannerListener");
//        }
//    }

    public interface InsuranceDocumentScannerListener {
        void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model);

        void navigateToParentFragment();

        void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);

        void disableMainButton(boolean isDisabled);
    }
}
