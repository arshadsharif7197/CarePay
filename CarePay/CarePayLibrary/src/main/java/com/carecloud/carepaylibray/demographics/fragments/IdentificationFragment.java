package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;


public class IdentificationFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        checkIfEnableButton(view);
        view.findViewById(R.id.toolbar_layout).setVisibility(View.INVISIBLE);

        setHeaderTitle(Label.getLabel("demographics_review_identification"),
                Label.getLabel("demographics_identity_heading"),
                Label.getLabel("demographics_identity_subheading"),
                view);

        initNextButton(view);

        DemographicIdDocPayloadDTO idDocument = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument();
        initialiseChildFragment(idDocument
                /*, demographicDTO.getMetadata().getDataModels().getDemographic().getIdentityDocuments().getProperties().getItems().getIdentityDocument()*/);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        stepProgressBar.setCurrentProgressDot(3);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 4);
        checkinFlowCallback.setCurrentStep(4);
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
            updatableDemographicDTO.getPayload().getDemographics().getPayload().setIdDocument(fragment.getPostModel());
        }
        updatableDemographicDTO.getPayload().setAppointmentpayloaddto(demographicDTO.getPayload().getAppointmentpayloaddto());
        updatableDemographicDTO.setMetadata(demographicDTO.getMetadata());
        return updatableDemographicDTO;
    }

    private void initialiseChildFragment(DemographicIdDocPayloadDTO demPayloadIdDocDTO
                                         /*, DemographicMetadataEntityItemIdDocDTO demographicMetadataEntityItemIdDocDTO*/) {

        FragmentManager fm = getChildFragmentManager();
        String tag = IdDocScannerFragment.class.getSimpleName();
        IdDocScannerFragment fragment = (IdDocScannerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IdDocScannerFragment();
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demPayloadIdDocDTO);

//            if (null != demographicMetadataEntityItemIdDocDTO) {
//                DtoHelper.bundleDto(args, demographicMetadataEntityItemIdDocDTO);
//            }

            fragment.setArguments(args);
        }
        fm.beginTransaction()
                .replace(com.carecloud.carepaylibrary.R.id.revDemographicsIdentificationPicCapturer, fragment, tag)
                .commit();
    }
}
