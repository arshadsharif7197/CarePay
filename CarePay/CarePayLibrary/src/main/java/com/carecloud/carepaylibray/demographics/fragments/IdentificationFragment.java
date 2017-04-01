package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.List;


public class IdentificationFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        checkIfEnableButton(view);
        view.findViewById(R.id.toolbar_layout).setVisibility(View.INVISIBLE);

        setHeaderTitle(demographicDTO.getMetadata().getLabels().getDemographicsReviewIdentification(), view);
        initNextButton(null, view, View.VISIBLE);

        List<DemographicIdDocPayloadDTO> idDocuments = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments();
        initialiseChildFragment(idDocuments.size() > 0 ? idDocuments.get(0) : new DemographicIdDocPayloadDTO(),
                demographicDTO.getMetadata().getDataModels().getDemographic().getIdentityDocuments().properties.items.identityDocument);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(3);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 4);
        checkInNavListener.setCurrentStep(4);
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_identification;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        IdDocScannerFragment fragment = (IdDocScannerFragment) getChildFragmentManager().findFragmentById(R.id.revDemographicsIdentificationPicCapturer);
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        if (fragment != null) {
            updatableDemographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().clear();
            updatableDemographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().add(fragment.getModel());
        }
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    private void initialiseChildFragment(DemographicIdDocPayloadDTO demPayloadIdDocDTO,
                                         DemographicMetadataEntityItemIdDocDTO demographicMetadataEntityItemIdDocDTO) {

        FragmentManager fm = getChildFragmentManager();
        String tag = IdDocScannerFragment.class.getSimpleName();
        IdDocScannerFragment fragment = (IdDocScannerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IdDocScannerFragment();
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demPayloadIdDocDTO);

            if (null != demographicMetadataEntityItemIdDocDTO) {
                DtoHelper.bundleDto(args, demographicMetadataEntityItemIdDocDTO);
            }

            fragment.setArguments(args);
        }
        fm.beginTransaction()
                .replace(com.carecloud.carepaylibrary.R.id.revDemographicsIdentificationPicCapturer, fragment, tag)
                .commit();
    }
}