package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
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

public class HealthInsuranceFragment extends Fragment {
    private List<DemographicInsurancePayloadDTO>   insurancePayloadDTOs;
    private DemographicDTO                         demographicDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;
    private InsuranceDocumentScannerListener       documentCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_insurance_main, container, false);
        initDTOs();
        initTitle(view);
        initSwitch(view);
        initActiveSection(view);
        SystemUtil.hideSoftKeyboard(getActivity());
        return view;
    }

    private void initTitle(View view){
        TextView healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.insurancesTitleLabel);
        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceToolbarTitle().toUpperCase());
        SystemUtil.setProximaNovaSemiboldTypeface(getContext(), healthInsuranceTitleTextView);
    }

    private void initActiveSection(View view) {
        boolean loadResources = insurancePayloadDTOs.size() > 0;
        view.findViewById(R.id.setupContainer).setVisibility(loadResources? View.GONE : View.VISIBLE);
        view.findViewById(R.id.existingContainer).setVisibility(loadResources? View.VISIBLE : View.GONE);
        if( loadResources ){
            fillDetailAdapter(view);
            initAddButton(view);
        } else {
            ((TextView)view.findViewById(R.id.setupInsuranceLabel)).setText(globalLabelsMetaDTO.getDemographicsInsuranceSetupHealthInsurance());
            TextView setup = (TextView)view.findViewById(R.id.setupLabel);
            setup.setText(globalLabelsMetaDTO.getDemographicsInsuranceSetup());
            setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemographicInsurancePayloadDTO insurance = new DemographicInsurancePayloadDTO();
                insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
                insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
                documentCallback.navigateToInsuranceDocumentFragment(CarePayConstants.NO_INDEX, insurance);
            }
            });

        }
    }

    private void initAddButton(View view){
        Button addButton = (Button)view.findViewById(R.id.addInsuranceButton);
        addButton.setText(globalLabelsMetaDTO.getPracticeCheckinDemogrInsAddAnother());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DemographicInsurancePayloadDTO insurance = new DemographicInsurancePayloadDTO();
                insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
                insurance.getInsurancePhotos().add(new DemographicInsurancePhotoDTO());
                documentCallback.navigateToInsuranceDocumentFragment(CarePayConstants.NO_INDEX, insurance);
            }
        });
    }

    protected void fillDetailAdapter(View view){
        RecyclerView detailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.insurance_detail_list));
        detailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        InsuranceLineItemsListAdapter adapter = new InsuranceLineItemsListAdapter(this.getContext(), demographicDTO, insurancePayloadDTOs, documentCallback);
        detailsListRecyclerView.setAdapter(adapter);
    }

    private void initSwitch(View view) {
        final LinearLayout setupInsuranceHolders = (LinearLayout) view.findViewById(R.id.setupInsuranceHolders);
        final SwitchCompat doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
        getChildFragmentManager().executePendingTransactions();

        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                setupInsuranceHolders.setVisibility(on ? View.VISIBLE : View.GONE);
            }
        });
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        doYouHaveInsuranceSwitch.setText(label);

    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            documentCallback = (InsuranceDocumentScannerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement InsuranceDocumentScannerListener");
        }
    }

    public interface InsuranceDocumentScannerListener {
        public void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model);

        public void navigateToParentFragment();

        public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);
    }
}
