package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import com.carecloud.carepay.patient.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepay.patient.demographics.fragments.scanner.IdDocScannerFragment;
import com.carecloud.carepay.patient.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragmentWthWrapper extends Fragment
        implements DocumentScannerFragment.NextAddRemoveStatusModifier {

    private int[] holderIds         = {
            R.id.demographicsDocsInsurance1,
            R.id.demographicsDocsInsurance2,
            R.id.demographicsDocsInsurance3
    };
    private int[] holderWrappersIds = {
            R.id.demographicsDocsCard1ContainerWrapper,
            R.id.demographicsDocsCard2ContainerWrapper,
            R.id.demographicsDocsCard3ContainerWrapper,
    };
    private FragmentManager                        fm;
    private View                                   view;
    private ScrollView                             detailsScrollView;
    private FrameLayout                            idCardContainer;
    private FrameLayout                            insCardContainer1;
    private FrameLayout                            insCardContainer2;
    private FrameLayout                            insCardContainer3;
    private boolean                                isSecondCardAdded;
    private boolean                                isThirdCardAdded;
    private TextView                               multipleInsClickable;
    private Button                                 nextButton;
    private DemographicIdDocPayloadDTO             demPayloadIdDocDTO;
    private List<DemographicInsurancePayloadDTO>   insuranceDTOsList;
    private DemographicInsurancePayloadDTO         insuranceModel1;
    private DemographicInsurancePayloadDTO         insuranceModel2;
    private DemographicInsurancePayloadDTO         insuranceModel3;
    private DemographicMetadataEntityIdDocsDTO     idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;
    private TextView                               header;
    private TextView                               subheader;
    private SwitchCompat                           switchCompat;
    private TextView                               idTypeClickable;
    private TextView                               idDocTypeLabel;
    private String[]                               docTypes;
    private InsuranceWrapperCollection             wrapperCollection;


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
        detailsScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        initializeUIFields();

        return view;
    }

    private void initializeUIFields() {
        getOptions();

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

        setButtons();
        setSwitch();

        // set the fonts
        setTypefaces();

        // hide add card button
        showAddCardButton(false);
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
                // add non trivial insurance models
                if (isInsuaranceNonTrivial(insuranceModel1)) {
                    insuranceDTOsList.add(insuranceModel1);
                }
                if (isInsuaranceNonTrivial(insuranceModel2)) {
                    insuranceDTOsList.add(insuranceModel2);
                }
                if (isInsuaranceNonTrivial(insuranceModel3)) {
                    insuranceDTOsList.add(insuranceModel3);
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
                if (!isSecondCardAdded) {
                    isSecondCardAdded = true;
                    showCard(insCardContainer2, true);
                } else if (!isThirdCardAdded) {
                    isThirdCardAdded = true;
                    showCard(insCardContainer3, true);
                    showAddCardButton(false);
                }
                scrollToBottom();
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
        insCardContainer1 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance1);
        insCardContainer2 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance2);
        insCardContainer3 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance3);

        isSecondCardAdded = false;
        isThirdCardAdded = false;

        fm = getChildFragmentManager();
        // add license fragment
        IdDocScannerFragment idDocFragment = (IdDocScannerFragment) fm.findFragmentByTag("license");
        if (idDocFragment == null) {
            idDocFragment = new IdDocScannerFragment();
            idDocFragment.setButtonsStatusCallback(this);
            idDocFragment.setModel(demPayloadIdDocDTO); // set the model
            idDocFragment.setIdDocsMetaDTO(idDocsMetaDTO == null ? null : idDocsMetaDTO.properties.items.identityDocument);
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, idDocFragment, "license").commit();

        LinearLayout insContainersWrapper = (LinearLayout) view.findViewById(R.id.demographicsDocsInsHoldersContainer);
        createInsuranceFragments(insContainersWrapper);
    }

    private void createInsuranceFragments(LinearLayout insContainersWrapper) {
        DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO
                = (insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        wrapperCollection = new InsuranceWrapperCollection((AppCompatActivity) getActivity(),
                                                           insContainersWrapper,
                                                           holderIds,
                                                           holderWrappersIds,
                                                           globalLabelsMetaDTO,
                                                           metadataInsuranceDTO);
        wrapperCollection.initWrappersList(insuranceDTOsList);
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
                showCard(insCardContainer1, on);
                if (isSecondCardAdded) {
                    showCard(insCardContainer2, on);
                } else {
                    showCard(insCardContainer2, false);
                }
                if (isThirdCardAdded) {
                    showCard(insCardContainer3, on);
                } else {
                    showCard(insCardContainer3, false);
                }
                showAddCardButton(on && !isThirdCardAdded);
            }
        });
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        switchCompat.setText(label);
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityIdDocsDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }

    public void setInsurancesMetaDTO(DemographicMetadataEntityInsurancesDTO insurancesMetaDTO) {
        this.insurancesMetaDTO = insurancesMetaDTO;
    }

    @Override
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            multipleInsClickable.setVisibility(View.GONE);
        } else {
            if (!isThirdCardAdded) { // show only if there is another add possibility
                multipleInsClickable.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void enableNextButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void scrollToBottom() {
        View bottomView = view.findViewById(R.id.demographicsDocsBottomView);
        detailsScrollView.scrollTo(0, bottomView.getBottom());
    }

    /**
     * Wrapper to a fragment scanner entity that facilitates operations on list of displayed
     * scanner fragments (add, remove, sort)
     */
    class InsuranceWrapper {

        private AppCompatActivity                         wrapperContext;
        private int                                       wrapperHolderId;
        private FragmentManager                           wrapperFm;
        private FrameLayout                               wrapperHolder;
        private InsuranceScannerFragment                  wrapperScannerFragment;
        private DemographicInsurancePayloadDTO            wrapperPayloadDTO;
        private DemographicMetadataEntityItemInsuranceDTO wrapperMetadataDTO;
        private DemographicLabelsDTO                      wrapperLabelsDTO;
        private String                                    wrapperTag;
        private LinearLayout                              holderWrapper;

        public InsuranceWrapper(AppCompatActivity context,
                                LinearLayout holderWrapper,
                                @IdRes int holderId,
                                DemographicLabelsDTO labels,
                                DemographicMetadataEntityItemInsuranceDTO metadata,
                                DemographicInsurancePayloadDTO payload) {
            this.wrapperContext = context;
            this.wrapperHolderId = holderId;
            this.wrapperPayloadDTO = payload;
            this.wrapperMetadataDTO = metadata;
            this.wrapperLabelsDTO = labels;
            this.wrapperFm = wrapperContext.getSupportFragmentManager();
            this.holderWrapper = holderWrapper;

            // create the fragment and add it to the holder
            if (wrapperPayloadDTO == null) {
                wrapperPayloadDTO = new DemographicInsurancePayloadDTO();
            }

            wrapperTag = "frag" + holderId;
            wrapperScannerFragment = (InsuranceScannerFragment) wrapperFm.findFragmentByTag(wrapperTag);
            if (wrapperScannerFragment == null) {
                wrapperScannerFragment = new InsuranceScannerFragment();
                wrapperScannerFragment.setInsuranceDTO(wrapperPayloadDTO); // set the data model (if avail)
                wrapperScannerFragment.setInsuranceMetadataDTO(wrapperMetadataDTO);
            }
            toggleContainerVisible(true);
            wrapperFm.beginTransaction()
                    .replace(holderId, wrapperScannerFragment, wrapperTag)
                    .commit();
        }

        public DemographicInsurancePayloadDTO getWrapperPayloadDTO() {
            return wrapperPayloadDTO;
        }

        public void toggleContainerVisible(boolean visible) {
            holderWrapper.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        public void setCardTypeFromIndex(int typeIndex) {
            wrapperScannerFragment.setCardTypeFromIndex(typeIndex);
        }

        public void removeFragment() {
            wrapperScannerFragment = null;
        }

        public void moveFragmentToContainerOf(InsuranceWrapper destinationWrapper) {
            int destHolderId = destinationWrapper.wrapperHolderId;
            wrapperFm.beginTransaction().replace(destHolderId, wrapperScannerFragment, wrapperTag).commitNow();
        }
    }

    /**
     * Helper class holding a collection of InsuranceWrappers
     */
    class InsuranceWrapperCollection {

        private AppCompatActivity wrapperContext;
        private int                MAX_ELEMS         = 3;
        private int[]              holderIds         = new int[MAX_ELEMS];
        private int[]              holdersWrapperIds = new int[MAX_ELEMS];
        private InsuranceWrapper[] wrappers          = new InsuranceWrapper[MAX_ELEMS];
        private int                count             = 0;
        private DemographicLabelsDTO                      globalLabelsMetaDTO;
        private DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO;
        private LinearLayout                              holdersContainer;

        public InsuranceWrapperCollection(AppCompatActivity wrapperContext,
                                          LinearLayout insContainersWrapper,
                                          int[] holderIds,
                                          int[] holderWrappersId,
                                          DemographicLabelsDTO globalLabelsMetaDTO,
                                          DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO) {
            this.wrapperContext = wrapperContext;
            this.holdersContainer = insContainersWrapper;
            this.holderIds = holderIds;
            this.holdersWrapperIds = holderWrappersId;
            this.globalLabelsMetaDTO = globalLabelsMetaDTO;
            this.metadataInsuranceDTO = metadataInsuranceDTO;
        }

        public void initWrappersList(List<DemographicInsurancePayloadDTO> insuranceDTOsList) {
            // add insurance fragments
            DemographicInsurancePayloadDTO insuranceModel = getInsuranceModelAtIndex(insuranceDTOsList, 0);
            LinearLayout holderWrapper1 = (LinearLayout) holdersContainer.findViewById(holdersWrapperIds[0]);
            wrappers[count++] = new InsuranceWrapper(wrapperContext,
                                                     holderWrapper1,
                                                     R.id.demographicsDocsInsurance1,
                                                     globalLabelsMetaDTO,
                                                     metadataInsuranceDTO,
                                                     insuranceModel);

            DemographicInsurancePayloadDTO insuranceModel2 = getInsuranceModelAtIndex(insuranceDTOsList, 1);
            if (insuranceModel2 != null) {
                isSecondCardAdded = true;
                LinearLayout holderWrapper2 = (LinearLayout) holdersContainer.findViewById(holdersWrapperIds[1]);
                wrappers[count++] = new InsuranceWrapper(wrapperContext,
                                                         holderWrapper2,
                                                         R.id.demographicsDocsInsurance2,
                                                         globalLabelsMetaDTO,
                                                         metadataInsuranceDTO,
                                                         insuranceModel2);
            }

            DemographicInsurancePayloadDTO insuranceModel3 = getInsuranceModelAtIndex(insuranceDTOsList, 2);
            if (insuranceModel3 != null) {
                isThirdCardAdded = true;
                LinearLayout holderWrapper3 = (LinearLayout) holdersContainer.findViewById(holdersWrapperIds[2]);
                wrappers[count++] = new InsuranceWrapper(wrapperContext,
                                                         holderWrapper3,
                                                         R.id.demographicsDocsInsurance3,
                                                         globalLabelsMetaDTO,
                                                         metadataInsuranceDTO,
                                                         insuranceModel3);
            }
        }

        private DemographicInsurancePayloadDTO getInsuranceModelAtIndex(List<DemographicInsurancePayloadDTO> insuranceDTOsList, int index) {
            DemographicInsurancePayloadDTO model = null;
            if (insuranceDTOsList != null) {
                int numOfInsurances = insuranceDTOsList.size();
                if (numOfInsurances > index) { // check if the list has an item at index i
                    model = insuranceDTOsList.get(index);
                }
            }
            return model;
        }

        public void add(DemographicInsurancePayloadDTO insurancePayloadDTO) {
            if (count < MAX_ELEMS) {
                LinearLayout holderWrapper = (LinearLayout) holdersContainer.findViewById(holdersWrapperIds[count]);
                InsuranceWrapper insuranceWrapper = new InsuranceWrapper(wrapperContext,
                                                                         holderWrapper,
                                                                         holderIds[count],
                                                                         globalLabelsMetaDTO,
                                                                         metadataInsuranceDTO,
                                                                         insurancePayloadDTO);
                insuranceWrapper.setCardTypeFromIndex(count - 1);
                wrappers[count] = insuranceWrapper;
                count++;
            }
        }

        /**
         * Removes an element
         *
         * @param index The index to remove
         */
        public void removeFor3At(int index) {
            if (index >= count) {
                return;
            }

            if (index == 1) {
                shiftLeft(2);
            } else if (index == 0) {
                shiftLeft(1);
                shiftLeft(2);
            }

            count--;
            wrappers[count].removeFragment();
            wrappers[count] = null;
        }

        private void shiftLeft(int indexToShift) {
            if (indexToShift > 0) {
                InsuranceWrapper previousInsWrapper = wrappers[indexToShift - 1];
                InsuranceWrapper currentInsWrapper = wrappers[indexToShift];
                currentInsWrapper.moveFragmentToContainerOf(previousInsWrapper);
                wrappers[indexToShift - 1] = wrappers[indexToShift];
            }
        }

        public List<DemographicInsurancePayloadDTO> exportPayloadDTOAsList() {
            List<DemographicInsurancePayloadDTO> payloadDTOs = new ArrayList<>();
            for (int index = 0; index < count; index++) {
                payloadDTOs.add(wrappers[index].getWrapperPayloadDTO());
            }
            return payloadDTOs;
        }
    }
}