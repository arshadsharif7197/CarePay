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
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class HealthInsuranceFragment extends CheckInDemographicsBaseFragment implements
        InsuranceLineItemsListAdapter.OnInsuranceEditClickListener {

    private TextView insurancePhotoAlert;

    public interface InsuranceDocumentScannerListener {
        void editInsurance(DemographicDTO demographicDTO, Integer editedIndex, boolean showAsDialog);
    }

    private DemographicDTO demographicDTO;

    private InsuranceLineItemsListAdapter adapter;
    private InsuranceDocumentScannerListener callback;

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (InsuranceDocumentScannerListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement InsuranceDocumentScannerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (demographicDTO == null) {
            demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        } else if (!hasInsurance()) {
            demographicDTO = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initActiveSection(view);

        checkIfEnableButton(view);
        SystemUtil.hideSoftKeyboard(getActivity());

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
        if (demographicDTO == null) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else if (hasInsurance()) {
            insurancePhotoAlert.setVisibility(View.GONE);
            adapter.setDemographicDTO(demographicDTO);
        } else {
            editInsurance(null, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        stepProgressBar.setCurrentProgressDot(4);
        checkinFlowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 5, 5);
        checkinFlowCallback.setCurrentStep(5);
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
     *
     * @param view main view
     */
    public void initActiveSection(final View view) {

        insurancePhotoAlert = (TextView) view.findViewById(R.id.insurancePhotoAlert);
        Button addAnotherButton = (Button) view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View addAnotherButton) {
                editInsurance(null, true);
            }
        });

        setHeaderTitle(Label.getLabel("demographics_insurance_label"),
                Label.getLabel("demographics_health_insurance_heading"),
                Label.getLabel("demographics_health_insurance_subheading"),
                view);
        initNextButton(view);
    }

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO demographicInsurancePayloadDTO) {
        int position = -1;
        for (int i = 0; i < demographicDTO.getPayload().getDemographics().getPayload().getInsurances().size(); i++) {
            DemographicInsurancePayloadDTO insurancePayloadDTO = demographicDTO.getPayload()
                    .getDemographics().getPayload().getInsurances().get(i);
            if (insurancePayloadDTO.equals(demographicInsurancePayloadDTO)) {
                position = i;
            }
        }
        editInsurance(position, true);
    }

    @Override
    public void showAlert(DemographicInsurancePayloadDTO demographicInsurancePayloadDTO) {
        insurancePhotoAlert.setText(String
                .format(Label.getLabel("demographics_insurance_no_photo_alert"),
                        demographicInsurancePayloadDTO.getInsuranceProvider() + " "
                                + (demographicInsurancePayloadDTO.getInsurancePlan() != null
                                ? demographicInsurancePayloadDTO.getInsurancePlan() : "")));
        insurancePhotoAlert.setVisibility(View.VISIBLE);
    }

    private void editInsurance(Integer editedIndex, boolean showAsDialog) {
        if (callback != null) {
            callback.editInsurance(demographicDTO, editedIndex, showAsDialog);
        }
    }

    /**
     * @param demographicDTO Demographic DTO
     */
    public void updateInsuranceList(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        initializeViews();
    }

    public void openNextFragment(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
        openNextFragment(demographicDTO, true);
    }

    private boolean hasInsurance() {
        return !this.demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty();
    }
}
