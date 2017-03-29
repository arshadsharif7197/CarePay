package com.carecloud.carepaylibray.demographics.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;


public class IdentificationFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        checkIfEnableButton(view);
       // (view.findViewById(R.id.toolbar_layout)).setVisibility(View.INVISIBLE);

        setHeaderTitle(demographicDTO.getMetadata().getLabels().getDemographicsReviewIdentification(),
                Label.getLabel("demographics_identity_heading"),
                Label.getLabel("demographics_identity_subheading"),
                view);

        initNextButton(null, view, View.VISIBLE);

        stepProgressBar.setCurrentProgressDot(3);
        initialiseChildFragment(demographicDTO.getMetadata().getLabels(),
                demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size()>0?demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(0):new DemographicIdDocPayloadDTO(),
                demographicDTO.getMetadata().getDataModels().demographic.identityDocuments.properties.items.identityDocument);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 4);
        return view;
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
        PracticeIdDocScannerFragment fragment = (PracticeIdDocScannerFragment)getChildFragmentManager().findFragmentById(R.id.revDemographicsIdentificationPicCapturer);
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        if (fragment != null) {
            updatableDemographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().clear();
            updatableDemographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().add(fragment.getModel());
        }
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    @Override
    public void imageCaptured(Bitmap bitmap) {

    }

    private void initialiseChildFragment(DemographicLabelsDTO globalLabelDTO,
                                                 DemographicIdDocPayloadDTO demPayloadIdDocDTO,
                                                 DemographicMetadataEntityItemIdDocDTO demographicMetadataEntityItemIdDocDTO) {

        FragmentManager fm = getChildFragmentManager();
        String tag = PracticeIdDocScannerFragment.class.getSimpleName();
        PracticeIdDocScannerFragment fragment = (PracticeIdDocScannerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new PracticeIdDocScannerFragment();
            fragment.setGlobalLabelsDTO(globalLabelDTO);
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
