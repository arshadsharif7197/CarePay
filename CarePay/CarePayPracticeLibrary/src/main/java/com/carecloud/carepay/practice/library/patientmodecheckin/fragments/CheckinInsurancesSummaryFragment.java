package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

public class CheckinInsurancesSummaryFragment extends Fragment {

    private FragmentManager                      fm;
    private View                                 view;
    private SwitchCompat                         doYouHaveInsuranceSwitch;
    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO                       demographicDTO;

    private Button      addInsuranceaInfoButton;
    private ProgressBar demographicProgressBar;

    private DemographicInsurancePayloadDTO         insuranceModel1;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;

    private TextView healthInsuranceTitleTextView;
    private TextView haveMultipleHealthInsuranceTextView;

    private boolean                    isThirdCardAdded;
    private LinearLayout               insContainersWrapper;
    private InsuranceWrapperCollection wrapperCollection1;
    private ScrollView                 detailsScrollView;

    public CheckinInsurancesSummaryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);

        initDTOs();

        fm = getChildFragmentManager();

        // hide the toolbar in practice app
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.healthinsurance_review_toolbar);
        toolbar.setVisibility(View.GONE);

        // init view handlers
        demographicProgressBar = (ProgressBar) view.findViewById(R.id.demographichealthinsuranceReviewProgressBar);
        demographicProgressBar.setVisibility(View.GONE);
        detailsScrollView = (ScrollView) view.findViewById(R.id.updateInsuranceDocsScroll);
        setSwitch();
        setCardContainers();

        if (insurancePayloadDTOs != null) {
            doYouHaveInsuranceSwitch.setChecked(true);
        }

        setButtons();
        setTypefaces(view);

        return view;
    }

    private void initDTOs() {
        demographicDTO = ((PatientModeCheckinActivity)getActivity()).getDemographicDTO();

        insurancesMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.insurances;
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();

        // get the payload
        insurancePayloadDTOs = new ArrayList<>(); // init in case no payload
        DemographicPayloadResponseDTO payloadResponseDTO = demographicDTO.getPayload();
        if(payloadResponseDTO != null) {
            DemographicPayloadInfoDTO demographicPayloadInfoDTO = payloadResponseDTO.getDemographics();
            if(demographicPayloadInfoDTO != null) {
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
        // next button
        healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle);
        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceScreenTitle());
        addInsuranceaInfoButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        addInsuranceaInfoButton.setText(globalLabelsMetaDTO.getDemographicsInsuranceUpdateButton().toUpperCase());
        addInsuranceaInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postUpdates();
            }
        });
        haveMultipleHealthInsuranceTextView = (TextView) view.findViewById(R.id.multipleInsurancesClickable);
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

        insurancePayloadDTOs.clear();
        for (DemographicInsurancePayloadDTO payloadDTO : wrapperCollection1.exportPayloadsAsList()) {
            if (isInsuaranceNonTrivial(payloadDTO)) {
                insurancePayloadDTOs.add(payloadDTO);
            }
        }

        DemographicPayloadDTO postPayloadModel = new DemographicPayloadDTO();
        postPayloadModel.setInsurances(insurancePayloadDTOs);

        // transition
        CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
        ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);

        // TODO: 11/22/2016 uncommnent
//        Map<String, String> queries = new HashMap<>();
//        queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
//        queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
//        queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());
//
//        Map<String, String> header = WorkflowServiceHelper.getPreferredLanguageHeader();
//        header.put("transition", "false");
//
//        Gson gson = new Gson();
//        String demographicinfo = gson.toJson(postPayloadModel);
//        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
//
//        WorkflowServiceHelper.getInstance().execute(transitionDTO, new WorkflowServiceCallback() {
//            @Override
//            public void onPreExecute() {
//
//            }
//
//            @Override
//            public void onPostExecute(WorkflowDTO workflowDTO) {
//                getActivity().finish();
//            }
//
//            @Override
//            public void onFailure(String exceptionMessage) {
//
//            }
//        }, demographicinfo, queries, header);
    }

    private boolean isInsuaranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    private void setCardContainers() {

        // fetch nested fragments containers
        isThirdCardAdded = false;

        fm = getChildFragmentManager();

        insContainersWrapper = (LinearLayout) view.findViewById(R.id.insuranceHoldersContainer);
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
        doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
        doYouHaveInsuranceSwitch.setText(globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel());

        doYouHaveInsuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                insContainersWrapper.setVisibility(on ? View.VISIBLE : View.GONE);
            }
        });

    }

    /**
     * @param isVisible checking add card button is visible or not
     */
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            haveMultipleHealthInsuranceTextView.setVisibility(View.GONE);
        } else {
            if (!isThirdCardAdded) { // show only if there is another add possibility
                haveMultipleHealthInsuranceTextView.setVisibility(View.VISIBLE);
            }
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