package com.carecloud.carepay.patient.demographics.fragments.review;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
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
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import java.util.ArrayList;
import java.util.List;


public class CheckinInsurancesSummaryFragment extends BaseFragment {

    private FragmentManager                      fm;
    private View                                 view;
    private SwitchCompat                         doYouHaveInsuranceSwitch;
    private List<DemographicInsurancePayloadDTO> insurancePayloadDTOs;
    private DemographicDTO                       demographicDTO;

//    private Button      addInsuranceaInfoButton;

    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;

    private TextView healthInsuranceTitleTextView;
    private TextView haveMultipleHealthInsuranceTextView;

    private boolean                    isThirdCardAdded;
    private LinearLayout               insContainersWrapper;
    private InsuranceWrapperCollection wrapperCollection1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);

        initDTOs();
        initViewsHandlers();

        fm = getChildFragmentManager();

        setCardContainers();

        setButtons();
        setSwitch();
        setTypefaces(view);

        doYouHaveInsuranceSwitch.setChecked(!(insurancePayloadDTOs.size() == 0));

        return view;
    }

    private void initViewsHandlers() {
        healthInsuranceTitleTextView = (TextView) view.findViewById(R.id.insurancesTitleLabel);
        healthInsuranceTitleTextView.setText(globalLabelsMetaDTO.getDemographicsUpdateInsuranceToolbarTitle().toUpperCase());
        //addInsuranceaInfoButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        haveMultipleHealthInsuranceTextView = (TextView) view.findViewById(R.id.multipleInsurancesClickable);
        insContainersWrapper = (LinearLayout) view.findViewById(R.id.insuranceHoldersContainer);
        doYouHaveInsuranceSwitch = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);
    }

    private void initDTOs() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

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

    private void setButtons() {
        haveMultipleHealthInsuranceTextView.setText(globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel());
        haveMultipleHealthInsuranceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listener) {
                wrapperCollection1.add(new DemographicInsurancePayloadDTO());
            }
        });
    }

    /**
     * Get the data payload
     * @return the data payload
     */
    public List<DemographicInsurancePayloadDTO> getInsurancePayloadDTOs() {
        // build the new payload
        insurancePayloadDTOs.clear();
        for (DemographicInsurancePayloadDTO payloadDTO : wrapperCollection1.exportPayloadsAsList()) {
            if (isInsuranceNonTrivial(payloadDTO)) {
                insurancePayloadDTOs.add(payloadDTO);
            }
        }
        return insurancePayloadDTOs;
    }

    private boolean isInsuranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    private void setCardContainers() {
        // fetch nested fragments containers
        isThirdCardAdded = false;

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
            if (!isThirdCardAdded) { // show only if there is another add possibility
                haveMultipleHealthInsuranceTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), healthInsuranceTitleTextView);
//        setProximaNovaRegularTypeface(getActivity(),
//                                      (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle));
        setProximaNovaRegularTypeface(getActivity(),
                                      (TextView) view.findViewById(R.id.demographicsInsuranceSwitch));
 //       setGothamRoundedMediumTypeface(getActivity(), addInsuranceaInfoButton);
    }
}