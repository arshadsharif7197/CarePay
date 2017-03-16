package com.carecloud.carepaylibray.demographics.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;


public class IdentificationFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        checkIfEnableButton(view);
        (view.findViewById(R.id.toolbar_layout)).setVisibility(View.INVISIBLE);
        setHeaderTitle(demographicDTO.getMetadata().getLabels().getDemographicsReviewIdentification(), view);
        initNextButton(demographicDTO.getMetadata().getLabels().getDemographicsReviewNextButton(), null, view);
        stepProgressBar.setCurrentProgressDot(3);
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
        return demographicDTO;
    }
}
