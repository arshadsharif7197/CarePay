package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.review.CheckinDemographicsFragment;
import com.carecloud.carepay.patient.demographics.fragments.review.CheckinInsurancesSummaryFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsCheckInDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.demographics.scanner.InsuranceDocumentScannerFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.List;

/**
 * Created by lsoco_user on 11/28/2016.
 */

public class NewReviewDemographicsActivity extends BasePatientActivity
        implements DemographicsLabelsHolder,
        CheckinDemographicsFragment.CheckinDemographicsFragmentListener,
        DemographicsCheckInDocumentsFragment.DemographicsCheckInDocumentsFragmentListener,
        HealthInsuranceFragment.InsuranceDocumentScannerListener {

    private DemographicDTO demographicDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_demographic_review);

        // demographics DTO
        demographicDTO = getConvertedDTO(DemographicDTO.class);

        // place the initial fragment
        navigateToParentFragment();
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.root_layout, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        if(demographicDTO != null && demographicDTO.getMetadata() != null && demographicDTO.getMetadata().getLabels() != null) {
            return demographicDTO.getMetadata().getLabels();
        }
        return null;
    }

    /**
     * Changes the global DTO
     *
     * @param demographicDTO The new DTO
     */
    @Override
    public void onDemographicDtoChanged(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;

        /*CheckinInsurancesSummaryFragment checkinInsurancesSummaryFragment = (CheckinInsurancesSummaryFragment)
                getSupportFragmentManager().findFragmentById(R.id.insuranceCapturer);

        if (checkinInsurancesSummaryFragment != null) {
            demographicDTO.getPayload().getDemographics().getPayload()
                    .setInsurances(checkinInsurancesSummaryFragment.getInsurancePayloadDTOs());
        }*/

        IdDocScannerFragment idDocScannerFragment = (IdDocScannerFragment)
                getSupportFragmentManager().findFragmentById(R.id.demographicsDocsLicense);

        if (idDocScannerFragment != null) {
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().clear();
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments()
                    .add(idDocScannerFragment.getModel());
        }
    }

    @Override
    public void initializeDocumentFragment(){

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getDataModels().demographic.identityDocuments);
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getLabels());
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicsCheckInDocumentsFragment fragment = new DemographicsCheckInDocumentsFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.documentCapturer, fragment, DemographicsCheckInDocumentsFragment.class.getSimpleName());
        transaction.commit();

    }

    @Override
    public void initializeInsurancesFragment(){
        String tag = HealthInsuranceFragment.class.getSimpleName();

        HealthInsuranceFragment fragment = new HealthInsuranceFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.insuranceCapturer, fragment, tag);
        transaction.commit();
    }

    @Override
    public void navigateToInsuranceDocumentFragment(int index, DemographicInsurancePayloadDTO model) {

        CheckinDemographicsFragment checkinFragment = (CheckinDemographicsFragment)
                getSupportFragmentManager().findFragmentById(R.id.root_layout);
        onDemographicDtoChanged(checkinFragment.updateModels());

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getLabels());
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getDataModels().demographic.insurances.properties.items.insurance);
        DtoHelper.bundleDto(args, model);
        DtoHelper.bundleDto(args, index);
        InsuranceDocumentScannerFragment fragment = new InsuranceDocumentScannerFragment();
        fragment.setArguments(args);

        navigateToFragment(fragment, false);
    }

    @Override
    public void navigateToParentFragment() {
        CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        navigateToFragment(fragment, false);
        Log.v(NewReviewDemographicsActivity.class.getSimpleName(), "NewReviewDemographicsActivity");
    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {
        List<DemographicInsurancePayloadDTO> insurances = demographicDTO.getPayload().getDemographics().getPayload()
                .getInsurances();
        if (index>=0){
            insurances.set(index, model);
        } else {
            insurances.add(model);
        }
    }

    @Override
    public void initializeIdDocScannerFragment() {

        // add license fragment
        IdDocScannerFragment fragment = new IdDocScannerFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicMetadataEntityIdDocsDTO idDocsMetaDTO =
                demographicDTO.getMetadata().getDataModels().demographic.identityDocuments;

        if (null != idDocsMetaDTO) {
            DtoHelper.bundleDto(args, idDocsMetaDTO.properties.items.identityDocument);
        }
        String tag = "license";
        FragmentManager fm = getSupportFragmentManager();
        fragment.setArguments(args);
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, fragment, tag).commit();
    }

    private DemographicIdDocPayloadDTO getDemographicIdDocPayloadDTO() {
        DemographicIdDocPayloadDTO demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();

        if (demographicDTO.getPayload().getDemographics() != null) {
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i < size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

        return demographicIdDocPayloadDTO;
    }


}
