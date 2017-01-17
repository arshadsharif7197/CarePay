package com.carecloud.carepay.patient.demographics.fragments.settings;

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

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;

import com.carecloud.carepay.service.library.CarePayConstants;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapper;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapperCollection;
import com.carecloud.carepaylibray.demographics.misc.OnClickRemoveOrAddCallback;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDriversLicenseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;


import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.List;


public class DemographicsSettingsDocumentsFragment extends Fragment {

    private FragmentManager                        fm;
    private View                                   view;
    private ScrollView                             mainScrollView;
    private FrameLayout                            idCardContainer;
    private TextView                               multipleInsClickable;
    private Button                                 nextButton;
    private List<DemographicIdDocPayloadDTO>       demPayloadIdDocDTO;
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
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String documentsdocumentsScanFirstString = null;
    private String documentsScanBackString = null;
    private String documentsDlNumberString = null;
    private String documentsDlStateString = null;
    private String documentsHealthInsuranceString = null;
    private String documentsHaveHealthInsuranceString = null;
    private String documentsAddnotherInsuranceString = null;
    private String documentsGoldenCrossString = null;
    private String languageString = null;
    private String documentsTypeString = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_demographics_settings_documents, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        getDocumentsLabels();
        // fetch the models
        getPayloadDTOs();

        // fetch the scroll view
        mainScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        initializeUIFields();

        return view;
    }

    /**
     * documents labels
     */
    public void getDocumentsLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    documentsdocumentsScanFirstString = demographicsSettingsLabelsDTO.getDocumentsScanFirstLabel();
                    documentsScanBackString = demographicsSettingsLabelsDTO.getDocumentsScanBackLabel();
                    documentsDlNumberString = demographicsSettingsLabelsDTO.getDocumentsDlNumberLabel();
                    documentsDlStateString = demographicsSettingsLabelsDTO.getDocumentsDlStateLabel();
                    documentsHealthInsuranceString = demographicsSettingsLabelsDTO.getDocumentsHealthInsuranceLabel();
                    documentsHaveHealthInsuranceString = demographicsSettingsLabelsDTO.getDocumentsHaveHealthInsuranceLabel();
                    documentsAddnotherInsuranceString = demographicsSettingsLabelsDTO.getDocumentsAddnotherInsuranceLabel();
                    documentsGoldenCrossString = demographicsSettingsLabelsDTO.getDocumentsGoldenCrossLabel();
                    documentsTypeString = demographicsSettingsLabelsDTO.getDocumentsTypeLabel();
                }
            }
        }
    }


    private void initializeUIFields() {
        getOptions();

        setButtons();

        // set the fragment
        setCardContainers();

        String label;

        //final String labelCancel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();

        idDocTypeLabel = (TextView) view.findViewById(R.id.demogrDocTypeLabel);
        //label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectIdType();
        idDocTypeLabel.setText("Type");

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        //label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setText("Select Document");
        //final String titleSelIdDoc = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.showChooseDialog(getActivity(),
                        docTypes, "Select Document", "Cancel",
                        idTypeClickable,
                        new SystemUtil.OnClickItemCallback() {
                            @Override
                            public void executeOnClick(TextView destination, String selectedOption) {
                               // demPayloadIdDocDTO.setIdType(selectedOption);
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
            if (demographicsSettingsDTO != null) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                if (demographicsSettingsMetadataDTO != null) {
                    DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                    DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    idDocsMetaDTO = demographicsSettingsDemographicsDTO.getIdentityDocuments();


                }
            }

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
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if(demographicsSettingsPayloadDTO!=null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                demPayloadIdDocDTO = demographicPayload.getIdentityDocuments();
                insuranceDTOsList = demographicPayload.getInsurances();

                //demPayloadIdDocDTO = ((DemographicsActivity) getActivity()).getIdDocModel();
                //insuranceDTOsList = ((DemographicsActivity) getActivity()).getInsuranceModelList();

                if (demPayloadIdDocDTO == null) {
                    demPayloadIdDocDTO = new ArrayList<>();
                    demPayloadIdDocDTO.add(new DemographicIdDocPayloadDTO());
                } else if (demPayloadIdDocDTO.size() == 0) {
                    demPayloadIdDocDTO.add(new DemographicIdDocPayloadDTO());
                }

                if (insuranceDTOsList == null) {
                    insuranceDTOsList = new ArrayList<>();
                    insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                } else if (insuranceDTOsList.size() == 0) {
                    insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                }
            }
        }    }

    private void setButtons() {
        String label;

        // next button
        nextButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        //label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsNext();
        nextButton.setText("Save Changes");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // ((DemographicsActivity) getActivity()).setIdDocModel(demPayloadIdDocDTO);
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
        //label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel();
        multipleInsClickable.setText("Have multiple Insurances");
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
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String demographicsSettingsDTOString = gson.toJson(demographicsSettingsDTO);
        bundle.putString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE, demographicsSettingsDTOString);

        // fetch nested fragments containers
        idCardContainer = (FrameLayout) view.findViewById(R.id.demographicsDocsLicense);

        fm = getChildFragmentManager();
        // add license fragment
        DocScannerFragment idDocFragment = (DocScannerFragment) fm.findFragmentByTag("license");
        if (idDocFragment == null) {
            idDocFragment = new DocScannerFragment();
            idDocFragment.setModel(demPayloadIdDocDTO.get(0)); // set the model
            idDocFragment.setIdDocsMetaDTO(idDocsMetaDTO == null ? null : idDocsMetaDTO.properties.items.identityDocument);
        }
        //fix for random crashes
        if(idDocFragment.getArguments() !=null){
            idDocFragment.getArguments().putAll(bundle);
        }else{
            idDocFragment.setArguments(bundle);
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
        //String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        switchCompat.setText("Do You Have Health Insurance?");
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