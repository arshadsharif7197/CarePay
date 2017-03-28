package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapperCollection;
import com.carecloud.carepaylibray.demographics.misc.OnClickRemoveOrAddCallback;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;


public class CheckinInsurancesSummaryFragment extends BaseCheckinFragment {

    private FragmentManager                      fm;
    private View                                 view;
    private SwitchCompat                         doYouHaveInsuranceSwitch;
    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO                       demographicDTO;

    private Button      addInsuranceaInfoButton;
    private ProgressBar demographicProgressBar;

    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;

    private TextView healthInsuranceTitleTextView;
    private TextView haveMultipleHealthInsuranceTextView;

    private LinearLayout               insContainersWrapper;
    private InsuranceWrapperCollection wrapperCollection1;
    private ScrollView                 detailsScrollView;

    public CheckinInsurancesSummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance_tmp, container, false);

        initDTOs();
        initViewsHandlers();

        fm = getChildFragmentManager();

        // hide the toolbar in practice app
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.healthinsurance_review_toolbar);
        toolbar.setVisibility(View.GONE);

        demographicProgressBar.setVisibility(View.GONE);
        setCardContainers();

        setButtons();
        setSwitch();
        setTypefaces(view);

        doYouHaveInsuranceSwitch.setChecked(!(insurancePayloadDTOs.size() == 0));

        if (insurancePayloadDTOs.size() == 3) {
            Log.v("show_card", "hiding add card...");
            showAddCardButton(false);
        }
        return view;
    }

    private void initViewsHandlers() {
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographichealthinsuranceReviewProgressBar);
        detailsScrollView = (ScrollView) view.findViewById(R.id.updateInsuranceDocsScroll);
        healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle);
        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceScreenTitle());
        addInsuranceaInfoButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        haveMultipleHealthInsuranceTextView = (TextView) view.findViewById(R.id.multipleInsurancesClickable);
        insContainersWrapper = (LinearLayout) view.findViewById(R.id.insuranceHoldersContainer);
        doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
    }

    private void initDTOs() {
        demographicDTO = ((PatientModeCheckinActivity) getActivity()).getDemographicDTO();

        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();

        // get the payload
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setButtons() {
        addInsuranceaInfoButton.setText(globalLabelsMetaDTO.getDemographicsInsuranceUpdateButton().toUpperCase());
        addInsuranceaInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postUpdates();
            }
        });
        haveMultipleHealthInsuranceTextView.setText(globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel());
        haveMultipleHealthInsuranceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listener) {
                wrapperCollection1.add(new DemographicInsurancePayloadDTO());
            }
        });
    }

    private void postUpdates() {
        demographicProgressBar.setVisibility(View.VISIBLE);

        // build the new payload
        insurancePayloadDTOs.clear();
        for (DemographicInsurancePayloadDTO payloadDTO : wrapperCollection1.exportPayloadsAsList()) {
            if (isInsuaranceNonTrivial(payloadDTO)) {
            insurancePayloadDTOs.add(payloadDTO);
            }
        }
        DemographicPayloadDTO postPayloadModel = new DemographicPayloadDTO();
        postPayloadModel.setInsurances(insurancePayloadDTOs);
        demographicDTO.getPayload().getDemographics().getPayload().setInsurances(insurancePayloadDTOs);


        final Gson gson = new Gson();
        ((PatientModeCheckinActivity) getActivity()).resetDemographicDTO(gson.toJson(demographicDTO));

        // move to demographics review
        FragmentManager fm = getActivity().getSupportFragmentManager();
        CheckinDemographicsRevFragment demInsRevFrag = (CheckinDemographicsRevFragment) fm.findFragmentByTag(CheckinDemographicsRevFragment.class.getSimpleName());
        if (demInsRevFrag == null) {
            demInsRevFrag = new CheckinDemographicsRevFragment();
        }
        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(demInsRevFrag, true);
    }

    private boolean isInsuaranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    private void setCardContainers() {
        fm = getChildFragmentManager();
        createInsuranceFragments(insContainersWrapper);
    }

    private void createInsuranceFragments(LinearLayout insContainersWrapper) {

        DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO
                = (insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        wrapperCollection1 = new InsuranceWrapperCollection((AppCompatActivity) getActivity(),
                                                            insContainersWrapper,
                                                            metadataInsuranceDTO,
                                                            globalLabelsMetaDTO,
                                                            new OnClickRemoveOrAddCallback() {
                                                                @Override
                                                                public void onAfterRemove() {
                                                                    showAddCardButton(true);
                                                                    if (wrapperCollection1.isEmpty()) {
                                                                        doYouHaveInsuranceSwitch.setChecked(false);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onAfterAdd() {
                                                                    showAddCardButton(false);
                                                                }
                                                            });
        wrapperCollection1.addAll(insurancePayloadDTOs);
    }

    private void setSwitch() {
        // set the switch
        fm.executePendingTransactions();

        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                insContainersWrapper.setVisibility(on ? View.VISIBLE : View.GONE);
                haveMultipleHealthInsuranceTextView.setVisibility(on ? View.VISIBLE : View.GONE);
                if (on && wrapperCollection1.isEmpty()) {
                    insurancePayloadDTOs.clear();
                    insurancePayloadDTOs.add(new DemographicInsurancePayloadDTO());
                    wrapperCollection1.addAll(insurancePayloadDTOs);
                }
            }
        });
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        doYouHaveInsuranceSwitch.setText(label);
        SystemUtil.hideSoftKeyboard(getActivity());
    }


    /**
     * @param isVisible checking add card button is visible or not
     */
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            haveMultipleHealthInsuranceTextView.setVisibility(View.GONE);
        } else {
            haveMultipleHealthInsuranceTextView.setVisibility(View.VISIBLE);
        }
    }

    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), healthInsuranceTitleTextView);
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.demographicsInsuranceSwitch));
        setGothamRoundedMediumTypeface(getActivity(), addInsuranceaInfoButton);
    }
}