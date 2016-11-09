package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DemographicReviewActivity extends BasePatientActivity {

    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO demPayloadIdDocPojo;


    private DemographicDTO modelGet = null;
    private DemographicMetadataEntityAddressDTO addressEntityMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicMetadataEntityIdDocsDTO idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO labelsDTO;

    /**
     * Updating with info model
      * @return updated model
     */
    public DemographicPayloadDTO getDemographicInfoPayloadModel() {
        DemographicPayloadDTO infoModel = null;
        if (modelGet != null && modelGet.getPayload() != null) {
                DemographicPayloadInfoDTO infoModelPayload = modelGet.getPayload().getDemographics();
                if (infoModelPayload != null) {
                    infoModel = infoModelPayload.getPayload();
                }

        }
        return infoModel;
    }


    public static boolean isFromReview = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);

        modelGet=getConvertedDTO(DemographicDTO.class);
        Intent intent = getIntent();
        if (intent.hasExtra("demographics_model")) {
            String demographicsModelString = intent.getStringExtra("demographics_model");
            Gson gson = new Gson();
            modelGet = gson.fromJson(demographicsModelString, DemographicDTO.class);
        }
        initDTOsForFragments();
        ReviewFragment reviewFragment = new ReviewFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().addToBackStack("reviewscreen")
                .replace(R.id.root_layout, reviewFragment, ReviewFragment.class.getName())
                .commit();

    }

    @Override
    public void onBackPressed() {
        if (isFromReview) {
            this.finish();

        } else {
            super.onBackPressed();
        }
    }

    private void initDTOsForFragments() {
        // init payload DTOs
        DemographicPayloadDTO infoModel = getDemographicInfoPayloadModel();
        if (infoModel != null) {
            // init payload DTOs
            demographicAddressPayloadDTO = infoModel.getAddress();
            demographicPersDetailsPayloadDTO = infoModel.getPersonalDetails();
            List<DemographicIdDocPayloadDTO> idDocDTOs = infoModel.getIdDocuments();
            if (idDocDTOs != null && idDocDTOs.size() > 0) {
                demPayloadIdDocPojo = infoModel.getIdDocuments().get(0);
            }
            insurances = infoModel.getInsurances();
        } else {
            demographicAddressPayloadDTO = new DemographicAddressPayloadDTO();
            demographicPersDetailsPayloadDTO = new DemographicPersDetailsPayloadDTO();
            demPayloadIdDocPojo = new DemographicIdDocPayloadDTO();
            insurances = new ArrayList<>();
        }
        // init metadata DTOs
        DemographicMetadataDTO metadataDTO = modelGet.getMetadata();
        labelsDTO = metadataDTO.getLabels();
        addressEntityMetaDTO = metadataDTO.getDataModels().demographic.address;
        persDetailsMetaDTO = metadataDTO.getDataModels().demographic.personalDetails;
        idDocsMetaDTO = metadataDTO.getDataModels().demographic.identityDocuments;
        insurancesMetaDTO = metadataDTO.getDataModels().demographic.insurances;
    }

    public DemographicDTO getModel() {
        return modelGet;
    }


    public DemographicPersDetailsPayloadDTO getDemographicPersDetailsPayloadDTO() {
        return demographicPersDetailsPayloadDTO;
    }

    public void setDemographicPersDetailsPayloadDTO(DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO) {
        this.demographicPersDetailsPayloadDTO = demographicPersDetailsPayloadDTO;
    }

    public DemographicAddressPayloadDTO getDemographicAddressPayloadDTO() {
        return demographicAddressPayloadDTO;
    }

    public void setDemographicAddressPayloadDTO(DemographicAddressPayloadDTO demographicAddressPayloadDTO) {
        this.demographicAddressPayloadDTO = demographicAddressPayloadDTO;
    }


    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    public DemographicIdDocPayloadDTO getDemographicPayloadIdDocDTO() {
        return demPayloadIdDocPojo;
    }

    public void setDemographicPayloadIdDocDTO(DemographicIdDocPayloadDTO demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }

    public DemographicMetadataEntityPersDetailsDTO getPersDetailsMetaDTO() {
        return persDetailsMetaDTO;
    }

    public void setPersDetailsMetaDTO(DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO) {
        this.persDetailsMetaDTO = persDetailsMetaDTO;
    }

    public DemographicMetadataEntityAddressDTO getAddressEntityMetaDTO() {
        return addressEntityMetaDTO;
    }

    public void setAddressEntityMetaDTO(DemographicMetadataEntityAddressDTO addressEntityMetaDTO) {
        this.addressEntityMetaDTO = addressEntityMetaDTO;
    }

    public DemographicMetadataEntityIdDocsDTO getIdDocsMetaDTO() {
        return idDocsMetaDTO;
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityIdDocsDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }

    public DemographicMetadataEntityInsurancesDTO getInsurancesMetaDTO() {
        return insurancesMetaDTO;
    }

    public void setInsurancesMetaDTO(DemographicMetadataEntityInsurancesDTO insurancesMetaDTO) {
        this.insurancesMetaDTO = insurancesMetaDTO;
    }

    public DemographicLabelsDTO getLabelsDTO() {
        return labelsDTO;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
