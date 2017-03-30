package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment implements
        InsuranceLineItemsListAdapter.OnInsuranceEditClickListener {

    private DemographicDTO demographicDTO;

    protected ImageCaptureHelper imageCaptureHelper;

    private InsuranceLineItemsListAdapter adapter;
    private InsuranceDocumentScannerListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (InsuranceDocumentScannerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceDocumentScannerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface InsuranceDocumentScannerListener {
        void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog);

        void navigateToParentFragment();

        void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        initActiveSection(view);

        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());

        stepProgressBar.setCurrentProgressDot(4);
        checkInNavListener.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 5);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = ((RecyclerView) findViewById(R.id.available_health_insurance_list));
        if (recyclerView != null) {
            adapter = new InsuranceLineItemsListAdapter(getContext(), demographicDTO, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        initializeViews();
    }

    private void initializeViews() {
        if (hasInsurance()) {

            adapter.setDemographicDTO(demographicDTO);

        } else {

            editInsurance(null, false);

        }
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
        return demographicDTO;
    }

    /**
     * enable or disable sections
     * @param view main view
     */
    public void initActiveSection(final View view) {
        Button addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                editInsurance(null, true);
            }
        });

        imageCaptureHelper = new ImageCaptureHelper(getActivity(), null);

        setHeaderTitle(Label.getLabel("demographics_insurance_label"), view);
        initNextButton(null, view, View.VISIBLE);

        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setText(Label.getLabel("demographics_review_go_to_consent"));
    }

    @Override
    public void onEditInsuranceClicked(int position) {
        editInsurance(position, true);
    }

    private void editInsurance(Integer editedIndex, boolean showAsDialog) {
        if (callback != null) {
            callback.editInsurance(demographicDTO, editedIndex, showAsDialog);
        }
    }

    public void updateInsuranceList(DemographicDTO demographicDTO) {
        boolean hadInsurance = hasInsurance();
        this.demographicDTO = demographicDTO;

        if (hadInsurance || hasInsurance()) {
            initializeViews();
        } else {
            openNextFragment(demographicDTO, true);
        }
    }

    private boolean hasInsurance() {
        return !this.demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty();
    }
}
