package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.service.library.CarePayConstants;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapper;
import com.carecloud.carepaylibray.demographics.misc.InsuranceWrapperCollection;
import com.carecloud.carepaylibray.demographics.misc.OnClickRemoveOrAddCallback;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadAddressDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsUpdateDocumentsDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.zxing.qrcode.decoder.Version;

import org.json.JSONObject;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private SwitchCompat                           switchCompat;
    private TextView                               idTypeClickable;
    private TextView                               idDocTypeLabel;
    private TextView                               identityLabel;
    private TextView                               healthInsuranceLabel;
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
    private String selectDocumentString = null;
    private String documentsmMultipleInsurancesString = null;
    private String documentsHaveHealthInsurance = null;
    private String documentsLicenseString = null;
    private String documentsSaveChangesString = null;
    private String documentsCancelString = null;
    private String languageString = null;
    private String documentsTypeString = null;
    private String documentsString = null;
    private String documentsLicenseNumberString = null;
    private String documentsIdentityString = null;

    private AppCompatActivity appCompatActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_demographics_settings_documents, container, false);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

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
        title.setText(documentsString);

        // fetch the scroll view
        mainScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);
        identityLabel = (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle);
        setProximaNovaSemiboldTypeface(appCompatActivity, identityLabel);
        healthInsuranceLabel = (TextView) view.findViewById(R.id.demographicsDocsHealthInsurancetitle);
        healthInsuranceLabel.setText(documentsHealthInsuranceString);
        identityLabel.setText(documentsIdentityString);
        initializeUIFields();

        return view;
    }

    /**
     * documents labels
     */
    public void getDocumentsLabels() {
        try{
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
                    documentsCancelString = demographicsSettingsLabelsDTO.getDemographicsCancelLabel();
                    selectDocumentString = demographicsSettingsLabelsDTO.getDemographicsSelectDocumentLabel();
                    documentsmMultipleInsurancesString = demographicsSettingsLabelsDTO.getDemographicsMultipleInsurancesLabel();
                    documentsLicenseString = demographicsSettingsLabelsDTO.getDemographicsLicenseLabel();
                    documentsSaveChangesString = demographicsSettingsLabelsDTO.getDemographicsSaveChangesLabel();
                    documentsHealthInsuranceString = demographicsSettingsLabelsDTO.getDocumentsHealthInsuranceLabel();
                    documentsLicenseNumberString = demographicsSettingsLabelsDTO.getDemographics_driver_license_Label();
                    documentsIdentityString = demographicsSettingsLabelsDTO.getDemographicsIdentityLabel();
                    documentsString = demographicsSettingsLabelsDTO.getDocumentsLabel();

                }
            }
          }
        }catch(Exception e){
            e.printStackTrace();
        }
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
           try{
            if (demographicsSettingsDTO != null) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                if (demographicsSettingsMetadataDTO != null) {
                    DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                    DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    idDocsMetaDTO = demographicsSettingsDemographicsDTO.getIdentityDocuments();


                }
            }
           }catch(Exception e){
               e.printStackTrace();
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
        try{
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if(demographicsSettingsPayloadDTO!=null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                demPayloadIdDocDTO = demographicPayload.getIdentityDocuments();
                insuranceDTOsList = demographicPayload.getInsurances();

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
          }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    WorkflowServiceCallback updateDocumentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Log.d("TEST", workflowDTO.toString());
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

            SystemUtil.showFaultDialog(getActivity());
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
                                    DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();

                                    List<DemographicInsurancePayloadDTO> demographicsInsuranceDetailsPayloadDTO = demographicPayload.getInsurances();
                                    for(int i = 0; i<=demographicsInsuranceDetailsPayloadDTO.size()-1;i++){
                                        List<DemographicInsurancePhotoDTO> demographicInsurancePhotoDTOs = insuranceDTOsList.get(i).getInsurancePhotos();
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsurancePhotos(demographicInsurancePhotoDTOs);
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceProvider(insuranceDTOsList.get(i).getInsuranceProvider());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsurancePlan(insuranceDTOsList.get(i).getInsurancePlan());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceMemberId(insuranceDTOsList.get(i).getInsuranceMemberId());
                                        demographicsInsuranceDetailsPayloadDTO.get(i).setInsuranceType(insuranceDTOsList.get(i).getInsuranceType());

                                    }
                                    Gson gson = new Gson();
                                    String jsonInString = gson.toJson(demographicsSettingsPayloadDTO);
                                    WorkflowServiceHelper.getInstance().execute(demographicsSettingsUpdateDemographicsDTO, updateDocumentsCallback, jsonInString, header);
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

        fm = getChildFragmentManager();
        // add license fragment
        DocScannerFragment idDocFragment = (DocScannerFragment) fm.findFragmentByTag(documentsLicenseString);
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
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, idDocFragment, documentsLicenseString).commit();

        insContainersWrapper = (LinearLayout) view.findViewById(R.id.demographicsDocsInsHoldersContainer);
        createInsuranceFragments(insContainersWrapper);
    }

    private void createInsuranceFragments(LinearLayout insContainersWrapper) {
        DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                 demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
            }
        }
        DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO
                = (insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        wrapperCollection1 = new InsuranceWrapperCollection((AppCompatActivity) getActivity(),
                insContainersWrapper,
                metadataInsuranceDTO,
                demographicsSettingsDTO,
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
        wrapperCollection1.addAllCards(insuranceDTOsList);
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