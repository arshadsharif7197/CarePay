package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;

import com.carecloud.carepay.patient.demographics.misc.InsuranceWrapper;
import com.carecloud.carepay.patient.demographics.misc.InsuranceWrapperCollection;
import com.carecloud.carepay.patient.demographics.misc.OnClickRemoveOrAddCallback;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.scanner.IdDocScannerFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;


import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragmentWthWrapper extends Fragment
//        implements DocumentScannerFragment.NextAddRemoveStatusModifier
{

    private FragmentManager                        fm;
    private View                                   view;
    private ScrollView                             mainScrollView;
    private FrameLayout                            idCardContainer;
    private TextView                               multipleInsClickable;
    private Button                                 nextButton;
    private DemographicIdDocPayloadDTO             demPayloadIdDocDTO;
    private List<DemographicInsurancePayloadDTO>   insuranceDTOsList;
    private DemographicMetadataEntityIdDocsDTO     idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;
    private TextView                               header;
    private TextView                               subheader;
    private SwitchCompat                           switchCompat;
    private TextView                               idTypeClickable;
    private TextView                               idDocTypeLabel;
    private String[]                               docTypes;
    private LinearLayout                           insContainersWrapper;
    private InsuranceWrapperCollection             wrapperCollection1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        // fetch the global labels
        globalLabelsMetaDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();
        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);
        // fetch the models
        getPayloadDTOs();

        // fetch the scroll view
        mainScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        initializeUIFields();

        return view;
    }

    private void initializeUIFields() {
        getOptions();

        setButtons();

        // set the fragment
        setCardContainers();

        String label;
        // set primary views on parent fragment (ie, all views except sub-fragments)
        header = (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSubheader();
        subheader.setText(label);

        final String labelCancel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();

        idDocTypeLabel = (TextView) view.findViewById(R.id.demogrDocTypeLabel);
        label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectIdType();
        idDocTypeLabel.setText(label);

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setText(label);
        final String titleSelIdDoc = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.showChooseDialog(getActivity(),
                                            docTypes, titleSelIdDoc, labelCancel,
                                            idTypeClickable,
                                            new SystemUtil.OnClickItemCallback() {
                                                @Override
                                                public void executeOnClick(TextView destination, String selectedOption) {
                                                    demPayloadIdDocDTO.setIdType(selectedOption);
                                                    showCard(idCardContainer, true);
                                                }
                                            });
            }
        });

        setSwitch();

        // set the fonts
        setTypefaces();

        // hide add card button
        showAddCardButton(switchCompat.isChecked());
    }

    private void getOptions() {
        if (idDocsMetaDTO == null) {
            docTypes = new String[1];
            docTypes[0] = CarePayConstants.NOT_DEFINED;

            return;
        }
        // init doc types
        List<String> docTypesStrings = new ArrayList<>();
        for (MetadataOptionDTO o : idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.options) {
            docTypesStrings.add(o.getLabel());
        }
        docTypes = docTypesStrings.toArray(new String[0]);
    }

    private void getPayloadDTOs() {
        demPayloadIdDocDTO = ((DemographicsActivity) getActivity()).getIdDocModel();
        insuranceDTOsList = ((DemographicsActivity) getActivity()).getInsuranceModelList();

        if (demPayloadIdDocDTO == null) {
            demPayloadIdDocDTO = new DemographicIdDocPayloadDTO();
        }

        if (insuranceDTOsList == null) {
            insuranceDTOsList = new ArrayList<>();
            insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
        } else if(insuranceDTOsList.size() == 0) {
            insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
        }
    }

    private void setButtons() {
        String label;

        // next button
        nextButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsNext();
        nextButton.setText(label);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsActivity) getActivity()).setIdDocModel(demPayloadIdDocDTO);
                // clear the list
                insuranceDTOsList.clear();

                for (DemographicInsurancePayloadDTO payloadDTO : wrapperCollection1.exportPayloadsAsList()) {
                    if (isInsuaranceNonTrivial(payloadDTO)) {
                        insuranceDTOsList.add(payloadDTO);
                    }
                }

                // set the list in activity
                ((DemographicsActivity) getActivity()).setInsuranceModelList(insuranceDTOsList);
                // move to next tab
                ((DemographicsActivity) getActivity()).setCurrentItem(3, true);
            }
        });

        // add button
        multipleInsClickable = (TextView) view.findViewById(R.id.demographicsDocumentsMultipleInsClickable);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel();
        multipleInsClickable.setText(label);
        multipleInsClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                wrapperCollection1.add(new DemographicInsurancePayloadDTO());
            }
        });
    }

    private boolean isInsuaranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel != null &&
                insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    private void setCardContainers() {

        // fetch nested fragments containers
        idCardContainer = (FrameLayout) view.findViewById(R.id.demographicsDocsLicense);

        fm = getChildFragmentManager();
        // add license fragment
        IdDocScannerFragment idDocFragment = (IdDocScannerFragment) fm.findFragmentByTag("license");
        if (idDocFragment == null) {
            idDocFragment = new IdDocScannerFragment();
            idDocFragment.setModel(demPayloadIdDocDTO); // set the model
            idDocFragment.setIdDocsMetaDTO(idDocsMetaDTO == null ? null : idDocsMetaDTO.properties.items.identityDocument);
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, idDocFragment, "license").commit();

        insContainersWrapper = (LinearLayout) view.findViewById(R.id.demographicsDocsInsHoldersContainer);
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
                                                                    scrollToLast();
                                                                    if(wrapperCollection1.isEmpty()) {
                                                                        switchCompat.setChecked(false);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onAfterAdd() {
                                                                    showAddCardButton(false);
                                                                }
                                                            });
        wrapperCollection1.addAll(insuranceDTOsList);
    }

    private void scrollToLast() {
        InsuranceWrapper lastAdded = wrapperCollection1.getLast();
        if(lastAdded != null) {
            View container = lastAdded.getHolderWrapperView();
            mainScrollView.scrollTo(0, container.getTop());
        }
    }

    private void showCard(FrameLayout cardContainer, boolean isVisible) {
        if (isVisible) {
            cardContainer.setVisibility(View.VISIBLE);
        } else {
            cardContainer.setVisibility(View.GONE);
        }
    }

    private void setTypefaces() {
        Context context = getActivity();
        setProximaNovaRegularTypeface(context, idDocTypeLabel);
        setProximaNovaSemiboldTypeface(context, idTypeClickable);

        setGothamRoundedMediumTypeface(context, header);
        setProximaNovaRegularTypeface(context, subheader);
        setProximaNovaRegularTypeface(context, switchCompat);
        setGothamRoundedMediumTypeface(context, multipleInsClickable);
        setGothamRoundedMediumTypeface(context, nextButton);
    }

    private void setSwitch() {
        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demographicsDocumentsInsuranceSwitch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                insContainersWrapper.setVisibility(on ? View.VISIBLE : View.GONE);
                multipleInsClickable.setVisibility(on ? View.VISIBLE : View.GONE);
                if(on && wrapperCollection1.isEmpty()) {
                    insuranceDTOsList.clear();
                    insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                    wrapperCollection1.addAll(insuranceDTOsList);
                }
            }
        });
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        switchCompat.setText(label);
        SystemUtil.hideSoftKeyboard(getActivity());
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityIdDocsDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }

    public void setInsurancesMetaDTO(DemographicMetadataEntityInsurancesDTO insurancesMetaDTO) {
        this.insurancesMetaDTO = insurancesMetaDTO;
    }

    public void showAddCardButton(boolean isVisible) {
        multipleInsClickable.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}