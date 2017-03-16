package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
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
import com.carecloud.carepaylibray.utils.DtoHelper;


public class IdentificationFragment extends CheckInDemographicsBaseFragment {

    public interface UpdateIdentificationDocListener{
        DemographicIdDocPayloadDTO getUpdatedDocuments();
    }

    private DemographicDTO demographicDTO;
    private UpdateIdentificationDocListener updateIdentificationDocListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        checkIfEnableButton(view);
        (view.findViewById(R.id.toolbar_layout)).setVisibility(View.INVISIBLE);
        setHeaderTitle(demographicDTO.getMetadata().getLabels().getDemographicsReviewIdentification(), view);
        initNextButton(demographicDTO.getMetadata().getLabels().getDemographicsReviewNextButton(), null, view);
        stepProgressBar.setCurrentProgressDot(3);

        initialiseChildFragment(demographicDTO.getMetadata().getLabels(),
                demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(0),
                demographicDTO.getMetadata().getDataModels().demographic.identityDocuments.properties.items.identityDocument);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            updateIdentificationDocListener = (UpdateIdentificationDocListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement UpdateIdentificationDocListener");
        }
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
        demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().add(0,updateIdentificationDocListener.getUpdatedDocuments());
        return demographicDTO;
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
