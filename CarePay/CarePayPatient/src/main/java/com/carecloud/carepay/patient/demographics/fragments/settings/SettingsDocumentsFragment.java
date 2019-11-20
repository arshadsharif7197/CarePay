package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.adapters.InsuranceLineItemsListAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPhotoDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePhotoDTO;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.BACK_PIC;
import static com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter.FRONT_PIC;

/**
 * Created by lmenendez on 5/24/17
 */

public class SettingsDocumentsFragment extends BaseFragment implements InsuranceLineItemsListAdapter.OnInsuranceEditClickListener, MediaViewInterface {
    private DemographicDTO demographicsSettingsDTO;
    private DemographicDTO demographicDTO;

    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;

    private boolean hasFrontImage = false;
    private boolean hasBackImage = false;
    private String base64FrontImage;
    private String base64BackImage;

    private InsuranceLineItemsListAdapter adapter;

    private DemographicsSettingsFragmentListener callback;

    private boolean showAlert = false;
    private boolean noPrimaryInsuranceFound = false;
    private boolean insuranceTypeRepeated = false;
    private boolean insuranceDataRepeated = false;
    private String insuranceTypeRepeatedErrorMessage;
    private View nextButton;
    private boolean insuranceChanged;
    private boolean photoAdded;

    private Button scanFrontButton;
    private Button scanBackButton;

    List<DemographicInsurancePayloadDTO> originalInsuranceList;

    public static SettingsDocumentsFragment newInstance() {
        return new SettingsDocumentsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicDTO) callback.getDto();
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, DtoHelper.getStringDTO(demographicsSettingsDTO));
        insuranceChanged = false;
        photoAdded = false;
        originalInsuranceList = demographicDTO.getPayload().getDemographics().getPayload().getInsurances();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_settings_documents, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Toolbar toolbar = view.findViewById(R.id.settings_toolbar);
        TextView title = toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("demographics_documents_section"));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForUnsavedChanges();
            }
        });

        nextButton = findViewById(R.id.buttonAddDemographicInfo);
        nextButton.setOnClickListener(view1 -> updateDemographics());

        initDocumentViews(view);

        initHealthInsuranceList(view);
        setUpBottomSheet(view);
    }

    private void checkForUnsavedChanges() {
        if (insuranceChanged || photoAdded) {
            ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("demographics_insurance_unsaved_alert_title"),
                            Label.getLabel("demographics_insurance_unsaved_alert_message"));
            confirmDialogFragment.setCallback(new ConfirmationCallback() {
                @Override
                public void onConfirm() {
                    getFragmentManager().popBackStack();
                }
            });
            confirmDialogFragment.show(getFragmentManager(), confirmDialogFragment.getClass().getName());
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void initDocumentViews(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this, CarePayCameraPreview.CameraType.SCAN_DOC);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter,
                getApplicationMode().getApplicationType());
        documentScannerAdapter.setIdDocumentsFromData(demographicDTO.getPayload().getDemographics()
                .getPayload().getIdDocument());

        scanFrontButton = view.findViewById(com.carecloud.carepaylibrary.R.id.demogrDocsFrontScanButton);
        scanBackButton = view.findViewById(com.carecloud.carepaylibrary.R.id.demogrDocsBackScanButton);
    }


    private void initHealthInsuranceList(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.available_health_insurance_list);
        List<DemographicInsurancePayloadDTO> insuranceList = getInsurances(demographicDTO);
        if (adapter == null) {
            adapter = new InsuranceLineItemsListAdapter(getContext(), insuranceList, this, getApplicationMode().getApplicationType());
        } else {
            adapter.setInsurancesList(insuranceList);
        }
        if (showAlert) {
            showAlert();
            showAlert = false;
            noPrimaryInsuranceFound = false;
            insuranceTypeRepeated =  false;
            insuranceDataRepeated = false;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Button addAnotherButton = view.findViewById(R.id.health_insurance_add_another);
        addAnotherButton.setOnClickListener(addAnotherButton1 -> editInsurance(-1));

        View noInsurance = view.findViewById(R.id.no_insurance_view);

        if (insuranceList.size() == 0) {
            noInsurance.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            addAnotherButton.setText(Label.getLabel("demographics_add_insurance_link"));
        } else {
            noInsurance.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            addAnotherButton.setText(Label.getLabel("demographics_add_another_insurance_link"));
        }

    }

    private List<DemographicInsurancePayloadDTO> getInsurances(DemographicDTO demographicDTO) {
        boolean hasOnePhoto = false;
        boolean isThereAnyPrimaryInsurance = false;
        Map<String, Integer> insurancesTypeMap = new HashMap<>();
        List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();
        for (DemographicInsurancePayloadDTO insurance : demographicDTO.getPayload().getDemographics().getPayload().getInsurances()) {
            if (!insurance.isDeleted()) {
                insuranceList.add(insurance);
                if (insurance.getInsuranceType().toLowerCase().equals("primary")) {
                    isThereAnyPrimaryInsurance = true;
                }
                if (insurance.getInsurancePhotos().size() == 0) {
                    showAlert = true;
                } else if (!hasOnePhoto) {
                    hasOnePhoto = true;
                }
                String insuranceType = StringUtil.captialize(insurance.getInsuranceType()).trim();
                if (insurancesTypeMap.containsKey(insuranceType)) {
                    insurancesTypeMap.put(insuranceType, insurancesTypeMap.get(insuranceType) + 1);
                } else {
                    insurancesTypeMap.put(insuranceType, 1);
                }
            }
        }
        if (!demographicDTO.getPayload().getDemographics().getPayload().getInsurances().isEmpty()
                && !isThereAnyPrimaryInsurance) {
            noPrimaryInsuranceFound = true;
            showAlert = true;
        }
        if (insuranceChanged) {
            insuranceChanged = checkIfInsuranceListChanged(insuranceList) && !checkIfInsuranceDataMatches(insuranceList);
        }
        nextButton.setEnabled(insuranceChanged &&
                !checkIfHasDuplicateInsuranceType(insurancesTypeMap));

        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_has_identity_doc), hasOnePhoto);

        return insuranceList;
    }

    private boolean checkIfInsuranceListChanged(List<DemographicInsurancePayloadDTO> modifiedList) {
        if (originalInsuranceList.size() != modifiedList.size()) {
            return true;
        } else {
            for (int i = 0; i < originalInsuranceList.size(); i++) {
                boolean match = originalInsuranceList.get(i).checkChanges(modifiedList.get(i));
                if (!match) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIfHasDuplicateInsuranceType(Map<String, Integer> insurancesTypeMap) {
        insuranceTypeRepeated = true;
        insuranceTypeRepeatedErrorMessage = Label.getLabel("insurance.insuranceList.alert.message.duplicatedInsuranceAlert");
        String insuranceType = "";
        if (insurancesTypeMap.containsKey("Primary") && insurancesTypeMap.get("Primary") > 1) {
            showAlert = true;
            insuranceType = "Primary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Secondary") && insurancesTypeMap.get("Secondary") > 1) {
            showAlert = true;
            insuranceType = "Secondary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Tertiary") && insurancesTypeMap.get("Tertiary") > 1) {
            showAlert = true;
            insuranceType = "Tertiary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else if (insurancesTypeMap.containsKey("Quaternary") && insurancesTypeMap.get("Quaternary") > 1) {
            showAlert = true;
            insuranceType = "Quaternary";
            insuranceTypeRepeatedErrorMessage = String.format(insuranceTypeRepeatedErrorMessage,
                    insurancesTypeMap.get(insuranceType), insuranceType, insuranceType);
        } else {
            insuranceTypeRepeated = false;
        }
        return insuranceTypeRepeated;
    }

    private boolean checkIfInsuranceDataMatches(List<DemographicInsurancePayloadDTO> modifiedList) {
        if (modifiedList.size() > 1) {
            for (DemographicInsurancePayloadDTO insurance : modifiedList) {
                if (!insurance.isDeleted()) {
                    for (DemographicInsurancePayloadDTO insuranceVerify : modifiedList) {
                        if (!insuranceVerify.isDeleted() && !insurance.equals(insuranceVerify)) {
                            boolean match = checkEqualValues(insurance.getInsuranceProvider(), insuranceVerify.getInsuranceProvider()) &&
                                    checkEqualValues(insurance.getInsurancePlan(), insuranceVerify.getInsurancePlan()) &&
                                    checkEqualValues(insurance.getInsuranceMemberId(), insuranceVerify.getInsuranceMemberId());
                            if (match) {
                                insuranceDataRepeated = showAlert = true;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        insuranceDataRepeated = false;
        return false;
    }

    private boolean checkEqualValues(String value1, String value2) {
        return StringUtils.equalsIgnoreCase(value1, value2) || StringUtils.isEmpty(value1) && StringUtils.isEmpty(value2);
    }

    private void showAlert() {
        String alertMessage = Label.getLabel("demographics_insurance_no_photo_alert");
        int notificationType = CustomMessageToast.NOTIFICATION_TYPE_WARNING;
        if (insuranceTypeRepeated) {
            alertMessage = insuranceTypeRepeatedErrorMessage;
            notificationType = CustomMessageToast.NOTIFICATION_TYPE_ERROR;
        } else if (insuranceDataRepeated) {
            alertMessage = Label.getLabel("demographics_insurance_duplicate_insurance");
            notificationType = CustomMessageToast.NOTIFICATION_TYPE_ERROR;
        } else if (noPrimaryInsuranceFound) {
            alertMessage = Label.getLabel("demographics_insurance_no_primary_alert");
        }
        new CustomMessageToast(getActivity(), alertMessage, notificationType).show();

    }


    protected DemographicDTO getUpdateModel() {
        DemographicDTO updatableDemographicDTO = new DemographicDTO();
        updatableDemographicDTO.getPayload().getDemographics().getPayload().setIdDocument(getPostModel());
        inflateNewImages();
        updatableDemographicDTO.getPayload().getDemographics().getPayload()
                .setInsurances(demographicDTO.getPayload().getDemographics().getPayload().getInsurances());
        return updatableDemographicDTO;
    }

    private void updateDemographics() {
        DemographicDTO updateModel = getUpdateModel();
        Gson gson = new Gson();
        String jsonPayload = gson.toJson(updateModel.getPayload().getDemographics().getPayload());

        TransitionDTO updateDemographics = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateDemographics();
        getWorkflowServiceHelper().execute(updateDemographics, updateDemographicsCallback, jsonPayload);
    }

    private DemographicIdDocPayloadDTO getPostModel() {
        setupImageBase64();
        DemographicIdDocPayloadDTO docPayloadDTO = new DemographicIdDocPayloadDTO();
        if ((hasFrontImage && base64FrontImage != null) ||
                (hasBackImage && base64BackImage != null)) {
            //Log new Identity Doc
            MixPanelUtil.logEvent(getString(R.string.event_add_identity_doc), getString(R.string.param_is_checkin), false);
        }

        if (hasFrontImage && base64FrontImage != null) {
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(FRONT_PIC);
            docPhotoDTO.setIdDocPhoto(base64FrontImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        if (hasBackImage && base64BackImage != null) {
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(BACK_PIC);
            docPhotoDTO.setIdDocPhoto(base64BackImage);
            docPhotoDTO.setDelete(false);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
        return docPayloadDTO;
    }


    private WorkflowServiceCallback updateDemographicsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            DemographicDTO updatedModel = DtoHelper.getConvertedDTO(DemographicDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setIdDocument(updatedModel.getPayload().getDemographics().getPayload().getIdDocument());
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setInsurances(updatedModel.getPayload().getDemographics().getPayload().getInsurances());
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onEditInsuranceClicked(DemographicInsurancePayloadDTO demographicInsurancePayloadDTO) {
        int position = demographicDTO.getPayload().getDemographics().getPayload().getInsurances().indexOf(demographicInsurancePayloadDTO);
        editInsurance(position);
    }

    private void editInsurance(int editedIndex) {
        if (callback != null) {
            callback.editInsurance(demographicDTO, editedIndex);
        }
    }

    /**
     * Refresh this fragments list of insurances
     *
     * @param demographicDTO updated demographic dto payload
     */
    public void updateInsuranceList(DemographicDTO demographicDTO) {
        insuranceChanged = true;
        this.demographicDTO = demographicDTO;
        if (getView() != null && isAdded()) {
            initHealthInsuranceList(getView());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return mediaScannerPresenter != null && mediaScannerPresenter.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mediaScannerPresenter != null) {
            mediaScannerPresenter.handleRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setCapturedBitmap(String filePath, View view) {
        if (filePath != null) {
            documentScannerAdapter.setImageView(filePath, view, true);
            int page;
            if (view.getId() == documentScannerAdapter.getFrontImageId()) {
                page = FRONT_PIC;
                hasFrontImage = true;
            } else {
                page = BACK_PIC;
                hasBackImage = true;
            }
            nextButton.setEnabled(true);
            photoAdded = true;

            DemographicIdDocPayloadDTO docPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument();
            for (DemographicIdDocPhotoDTO docPhotoDTO : docPayloadDTO.getIdDocPhothos()) {
                if (docPhotoDTO.getPage() == page) {
                    docPhotoDTO.setIdDocPhoto(filePath);
                    return;
                }
            }

            //did not find an item to update
            DemographicIdDocPhotoDTO docPhotoDTO = new DemographicIdDocPhotoDTO();
            docPhotoDTO.setPage(page);
            docPhotoDTO.setDelete(false);
            docPhotoDTO.setIdDocPhoto(filePath);
            docPayloadDTO.getIdDocPhothos().add(docPhotoDTO);
        }
    }

    @Override
    public void handleStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Nullable
    @Override
    public Fragment getCallingFragment() {
        return this;
    }

    @Override
    public void setupImageBase64() {
        List<DemographicIdDocPhotoDTO> docPhotos = demographicDTO.getPayload().getDemographics().getPayload().getIdDocument().getIdDocPhothos();
        String filePath;
        for (DemographicIdDocPhotoDTO docPhotoDTO : docPhotos) {
            if (docPhotoDTO.getPage() == FRONT_PIC && hasFrontImage) {
                filePath = docPhotoDTO.getIdDocPhoto();
                base64FrontImage = DocumentScannerAdapter.getBase64(getContext(), filePath);
            }
            if (docPhotoDTO.getPage() == BACK_PIC && hasBackImage) {
                filePath = docPhotoDTO.getIdDocPhoto();
                base64BackImage = DocumentScannerAdapter.getBase64(getContext(), filePath);
            }
        }
    }

    private void inflateNewImages() {
        for (DemographicInsurancePayloadDTO insurancePayloadDTO : demographicDTO.getPayload().getDemographics().getPayload().getInsurances()) {
            for (DemographicInsurancePhotoDTO photoDTO : insurancePayloadDTO.getInsurancePhotos()) {
                if (!photoDTO.isDelete() && photoDTO.isNewPhoto()) {
                    photoDTO.setInsurancePhoto(DocumentScannerAdapter.getBase64(getContext(), photoDTO.getInsurancePhoto()));
                }
            }
        }
    }

    private void setUpBottomSheet(View view) {
        final View shadow = view.findViewById(R.id.shadow);
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    shadow.setClickable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                shadow.setAlpha(slideOffset);
            }
        });

        scanFrontButton.setOnClickListener(view15 -> {
            documentScannerAdapter.setFrontCaptureImage();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_EXPANDED);
            shadow.setClickable(true);
        });
        scanBackButton.setOnClickListener(view14 -> {
            documentScannerAdapter.setBackCaptureImage();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_EXPANDED);
            shadow.setClickable(true);
        });

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setOnClickListener(view1 -> bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN));
        shadow.setClickable(false);

        View takePhotoContainer = view.findViewById(R.id.takePhotoContainer);
        takePhotoContainer.setOnClickListener(view12 -> {
            mediaScannerPresenter.handlePictureAction();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });

        View chooseFileContainer = view.findViewById(R.id.chooseFileContainer);
        chooseFileContainer.setOnClickListener(view13 -> {
            mediaScannerPresenter.selectFile();
            bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void bottomMenuAction(BottomSheetBehavior bottomSheetBehavior, int stateHidden) {
        bottomSheetBehavior.setState(stateHidden);
    }
}
