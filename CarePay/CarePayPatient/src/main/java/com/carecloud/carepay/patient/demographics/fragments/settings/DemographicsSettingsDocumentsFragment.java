package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapper;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapperCollection;
import com.carecloud.carepaylibray.demographics.misc.OnClickRemoveOrAddCallback;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class DemographicsSettingsDocumentsFragment extends BaseFragment {

    private View view;
    private ScrollView mainScrollView;
    private FrameLayout idCardContainer;
    private TextView multipleInsClickable;
    private Button nextButton;
    private DemographicIdDocPayloadDTO demPayloadIdDocDTO;
    private List<DemographicInsurancePayloadDTO> insuranceDTOsList;
    private DemographicMetadataEntityIdDocsDTO idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private SwitchCompat switchCompat;
    private TextView idTypeClickable;
    private TextView idDocTypeLabel;
    private String[] docTypes;
    private LinearLayout insContainersWrapper;
    private InsuranceWrapperCollection wrapperCollection1;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String documentsHealthInsuranceString = null;
    private String documentsHaveHealthInsuranceString = null;
    private String selectDocumentString = null;
    private String documentsLicenseString = null;
    private String documentsSaveChangesString = null;
    private String documentsCancelString = null;
    private String documentsTypeString = null;
    private String documentsString = null;
    private String documentsIdentityString = null;

    private AppCompatActivity appCompatActivity;

    private DemographicsSettingsDocumentsFragment() {
    }

    public static DemographicsSettingsDocumentsFragment newInstance(DemographicsSettingsDTO demographicsSettingsDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicsSettingsDTO);
        DemographicsSettingsDocumentsFragment fragment = new DemographicsSettingsDocumentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
        demographicsSettingsDTO = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_demographics_settings_documents, container, false);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        getDocumentsLabels();
        // fetch the models
        getPayloadDTOs();
        title.setText(documentsString);

        // fetch the scroll view
        mainScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);
        TextView identityLabel = (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle);
        setProximaNovaSemiboldTypeface(appCompatActivity, identityLabel);
        TextView healthInsuranceLabel = (TextView) view.findViewById(R.id.demographicsDocsHealthInsurancetitle);
        healthInsuranceLabel.setText(documentsHealthInsuranceString);
        identityLabel.setText(documentsIdentityString);
        initializeUIFields();

        return view;
    }

    /**
     * documents labels
     */
    public void getDocumentsLabels() {
        documentsHealthInsuranceString = Label.getLabel("documents_health_insurance_label");
        documentsHaveHealthInsuranceString = Label.getLabel("documents_have_health_insurance_label");
        documentsTypeString = Label.getLabel("documents_document_type_label");
        documentsCancelString = Label.getLabel("demographics_cancel_label");
        selectDocumentString = Label.getLabel("demographics_select_document");
        documentsLicenseString = Label.getLabel("demographics_license_label");
        documentsSaveChangesString = Label.getLabel("demographics_save_changes_label");
        documentsHealthInsuranceString = Label.getLabel("documents_health_insurance_label");
        documentsIdentityString = Label.getLabel("document_identity_label");
        documentsString = Label.getLabel("documents_label");
    }


    private void initializeUIFields() {
        getOptions();

        setButtons();

        // set the fragment
        setCardContainers();

        String label;

        idDocTypeLabel = (TextView) view.findViewById(R.id.demogrDocTypeLabel);
        idDocTypeLabel.setText(documentsTypeString);
        setProximaNovaRegularTypeface(appCompatActivity, idDocTypeLabel);

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        idTypeClickable.setText(documentsTypeString);
        idTypeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.showChooseDialog(getActivity(),
                        docTypes, selectDocumentString, documentsCancelString,
                        idTypeClickable,
                        new SystemUtil.OnClickItemCallback() {
                            @Override
                            public void executeOnClick(TextView destination, String selectedOption) {
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
        try {
            if (demographicsSettingsDTO != null) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                if (demographicsSettingsMetadataDTO != null) {
                    DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                    DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    idDocsMetaDTO = demographicsSettingsDemographicsDTO.getIdentityDocuments();


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (idDocsMetaDTO == null) {
            docTypes = new String[1];
            docTypes[0] = CarePayConstants.NOT_DEFINED;

            return;
        }
        // init doc types
        List<String> docTypesStrings = new ArrayList<>();
        for (MetadataOptionDTO o : idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.getOptions()) {
            docTypesStrings.add(o.getLabel());
        }
        docTypes = docTypesStrings.toArray(new String[0]);
    }

    private void getPayloadDTOs() {
        try {
            if (demographicsSettingsDTO != null) {
                DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                if (demographicsSettingsPayloadDTO != null) {
                    DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                    DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                    demPayloadIdDocDTO = demographicPayload.getIdDocument();
                    insuranceDTOsList = demographicPayload.getInsurances();

                    if (insuranceDTOsList == null) {
                        insuranceDTOsList = new ArrayList<>();
                        insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                    } else if (insuranceDTOsList.size() == 0) {
                        insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    WorkflowServiceCallback updateDocumentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            showErrorNotification(null);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private void setButtons() {
        // next button
        nextButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        nextButton.setText(documentsSaveChangesString);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButton.setEnabled(false);
                insuranceDTOsList.clear();
                for (DemographicInsurancePayloadDTO payloadDTO : wrapperCollection1.sendPayloads()) {
                    if (isInsuaranceNonTrivial(payloadDTO)) {
                        insuranceDTOsList.add(payloadDTO);
                    }
                }

                if (demographicsSettingsDTO != null) {
                    DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                    if (demographicsSettingsMetadataDTO != null) {
                        DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                        TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                        Map<String, String> queries = null;
                        Map<String, String> header = null;
                        try {
                            if (demographicsSettingsDTO != null) {
                                DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                if (demographicsSettingsPayloadDTO != null) {
                                    DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                    DemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                                    List<DemographicInsurancePayloadDTO> demographicsInsuranceDetailsPayloadDTO = demographicPayload.getInsurances();
                                    for (int i = 0; i <= demographicsInsuranceDetailsPayloadDTO.size() - 1; i++) {
                                        List<DemographicInsurancePhotoDTO> demographicInsurancePhotoDTOs = insuranceDTOsList.get(i).getInsurancePhotos();
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsurancePhotos(demographicInsurancePhotoDTOs);
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceProvider(insuranceDTOsList.get(i).getInsuranceProvider());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsurancePlan(insuranceDTOsList.get(i).getInsurancePlan());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceMemberId(insuranceDTOsList.get(i).getInsuranceMemberId());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceType(insuranceDTOsList.get(i).getInsuranceType());

                                    }
                                    Gson gson = new Gson();
                                    String jsonInString = gson.toJson(demographicsSettingsPayloadDTO);
                                    getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateDocumentsCallback, jsonInString, header);
                                }
                            }
                            header = new HashMap<>();
                            header.put("transition", "true");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // add button
        multipleInsClickable = (TextView) view.findViewById(R.id.demographicsDocumentsMultipleInsClickable);
        multipleInsClickable.setText(documentsHaveHealthInsuranceString);
        multipleInsClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                wrapperCollection1.addCard(new DemographicInsurancePayloadDTO());
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

        // add license fragment
        DocScannerFragment idDocFragment = (DocScannerFragment) getChildFragmentManager().findFragmentByTag(documentsLicenseString);
        if (idDocFragment == null) {
            idDocFragment = new DocScannerFragment();
            idDocFragment.setModel(demPayloadIdDocDTO); // set the model
        }
        //fix for random crashes
        if (idDocFragment.getArguments() != null) {
            idDocFragment.getArguments().putAll(bundle);
        } else {
            idDocFragment.setArguments(bundle);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.demographicsDocsLicense, idDocFragment, documentsLicenseString).commit();

        insContainersWrapper = (LinearLayout) view.findViewById(R.id.demographicsDocsInsHoldersContainer);
        createInsuranceFragments(insContainersWrapper);
    }

    private void createInsuranceFragments(LinearLayout insContainersWrapper) {
        DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO
                = (insurancesMetaDTO == null ? null : insurancesMetaDTO.getProperties().getItems().getInsurance());
        wrapperCollection1 = new InsuranceWrapperCollection((AppCompatActivity) getActivity(),
                insContainersWrapper,
                metadataInsuranceDTO,
                demographicsSettingsDTO,
                new OnClickRemoveOrAddCallback() {
                    @Override
                    public void onAfterRemove() {
                        showAddCardButton(true);
                        scrollToLast();
                        if (wrapperCollection1.isEmpty()) {
                            switchCompat.setChecked(false);
                        }
                    }

                    @Override
                    public void onAfterAdd() {
                        showAddCardButton(false);
                    }
                });
        wrapperCollection1.addAllCards(insuranceDTOsList);
    }

    private void scrollToLast() {
        InsuranceWrapper lastAdded = wrapperCollection1.getLast();
        if (lastAdded != null) {
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
        getChildFragmentManager().executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demographicsDocumentsInsuranceSwitch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                insContainersWrapper.setVisibility(on ? View.VISIBLE : View.GONE);
                multipleInsClickable.setVisibility(on ? View.VISIBLE : View.GONE);
                if (on && wrapperCollection1.isEmpty()) {
                    insuranceDTOsList.clear();
                    insuranceDTOsList.add(new DemographicInsurancePayloadDTO());
                    wrapperCollection1.addAllCards(insuranceDTOsList);
                }
            }
        });
        switchCompat.setText(documentsHaveHealthInsuranceString);
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