package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.CheckinSettingsDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsOnlyResultModel;
import com.carecloud.carepaylibray.medications.models.MedicationsImagePostModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.medications.models.MedicationsPostModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17
 */

public class MedicationsFragment extends BaseCheckinFragment implements
        MedicationAllergiesAdapter.MedicationAllergiesAdapterCallback, MediaViewInterface,
        DocumentScannerAdapter.ImageLoadCallback {

    private RecyclerView medicationRecycler;
    private Button continueButton;
    private View photoLayout;
    private ImageView medicationPhoto;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;
    private String photoPath;
    private View newPhotoButton;
    private View changePhotoButton;
    public boolean shouldRemove = false;

    protected DemographicsPresenter callback;
    private WorkflowDTO workflowDTOMedicine;

    private MedicationsOnlyResultModel medicationsAllergiesDTO;
    private MedicationsPostModel medicationsPostModel = new MedicationsPostModel();

    private List<MedicationsObject> currentMedications = new ArrayList<>();
    private List<MedicationsObject> addMedications = new ArrayList<>();
    private List<MedicationsObject> removeMedications = new ArrayList<>();
    private List<MedicationsObject> tempMedications = new ArrayList<>();

    private Handler handler = new Handler();
    private boolean shouldAllowMedPicture = true;

    public static MedicationsFragment newInstance(MedicationsOnlyResultModel medicationsAllergiesDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        MedicationsFragment fragment = new MedicationsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (DemographicsPresenter) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement DemographicsPresenter");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsOnlyResultModel.class, getArguments());
        currentMedications = medicationsAllergiesDTO.getPayload().getMedications().getPayload();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args,medicationsAllergiesDTO);

        callback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 0, 0);
        hideProgressDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_medications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        inflateToolbarViews(view);
        initViews(view);
        setAdapters();
        final NestedScrollView scrollView = view.findViewById(R.id.scroll_medications_allergy);
        handler.postDelayed(() -> scrollView.scrollTo(0, 0), 30);
        View container = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(container);
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(navigationClickListener);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(Label.getLabel("demographics_meds_title"));
        }
    }

    private void initViews(View view) {
        View medicationChooseButton = view.findViewById(R.id.medication_choose_button);
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        continueButton = view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setOnClickListener(continueClickListener);
        continueButton.setSelected(false);
        continueButton.setClickable(false);

        RecyclerView.LayoutManager medicationManager = new LinearLayoutManager(getContext(),
                RecyclerView.VERTICAL, true);
        medicationRecycler = view.findViewById(R.id.medication_recycler);
        medicationRecycler.setLayoutManager(medicationManager);

        photoLayout = view.findViewById(R.id.medication_photo_layout);
        medicationPhoto = view.findViewById(R.id.medications_image);

        newPhotoButton = view.findViewById(R.id.medication_list_photo_button);
        changePhotoButton = view.findViewById(R.id.medication_list_photo_change);

        View removePhotoButton = view.findViewById(R.id.medication_list_photo_remove);
        removePhotoButton.setOnClickListener(removePhotoListener);

        initImageViews(view);

        CheckinSettingsDTO checkinSettings = medicationsAllergiesDTO.getPayload().getCheckinSettings();
        if (!checkinSettings.isAllowMedicationPicture()) {
            newPhotoButton.setVisibility(View.INVISIBLE);
            photoLayout.setVisibility(View.GONE);
            shouldAllowMedPicture = false;
        }

    }

    private void initImageViews(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this,
                CarePayCameraPreview.CameraType.SCAN_DOC);
        mediaScannerPresenter.setCaptureView(medicationPhoto);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view,
                mediaScannerPresenter, getApplicationMode().getApplicationType(), false);

        String url = medicationsAllergiesDTO.getPayload().getMedicationsImage().getPayload().getUrl();
        if (!StringUtil.isNullOrEmpty(url)) {
            documentScannerAdapter.setImageView(url, medicationPhoto, false, 0, 0,
                    R.drawable.icn_placeholder_document, this);
        }
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            setUpBottomSheet(view);
        } else {
            newPhotoButton.setOnClickListener(view1 -> mediaScannerPresenter.handlePictureAction());
            changePhotoButton.setOnClickListener(view12 -> mediaScannerPresenter.handlePictureAction());
        }
    }

    private void setAdapters() {
        if (medicationRecycler.getAdapter() == null) {
            MedicationAllergiesAdapter medicationAdapter = new MedicationAllergiesAdapter(getContext(),
                    getApplicationMode().getApplicationType(), currentMedications, this);
            medicationRecycler.setAdapter(medicationAdapter);
        } else {
            MedicationAllergiesAdapter adapter = ((MedicationAllergiesAdapter) medicationRecycler.getAdapter());
            adapter.setItems(currentMedications);
            adapter.notifyDataSetChanged();
        }
        setAdapterVisibility();
    }

    private void setAdapterVisibility() {
        if (currentMedications.isEmpty() && !hasPhoto()) {
            medicationRecycler.setVisibility(View.GONE);
        } else {
            medicationRecycler.setVisibility(View.VISIBLE);
        }
        validateForm();
    }

    private void validateForm() {
        boolean validMeds = !currentMedications.isEmpty();
        boolean valid = validMeds || hasPhoto();

        continueButton.setSelected(valid);
        continueButton.setClickable(valid);
    }

    private boolean hasPhoto() {
        if (!shouldAllowMedPicture) {
            return false;
        }
        return (!StringUtil.isNullOrEmpty(medicationsAllergiesDTO.getPayload()
                .getMedicationsImage().getPayload().getUrl()) &&
                (medicationsPostModel.getMedicationsImage() == null ||
                        !medicationsPostModel.getMedicationsImage().isDelete())) ||
                !StringUtil.isNullOrEmpty(photoPath);
    }

    @Override
    public void deleteItem(MedicationsAllergiesObject item) {
        item.setDeleted(true);
        if (item instanceof MedicationsObject) {
            //remove Medication from list
            if (addMedications.contains(item)) {
                addMedications.remove(item);
                tempMedications.add((MedicationsObject) item);
            } else {
                item.setAction(MedicationAllergiesAction.delete);
                MedicationsObject medicationToDelete = new MedicationsObject();
                medicationToDelete.setAction(item.getAction());
                medicationToDelete.setUuid(item.getUuid());
                medicationToDelete.setDisplayName(item.getDisplayName());
                removeMedications.add(medicationToDelete);
            }
        }
        setAdapters();
    }

    @Override
    public void restoreItem(MedicationsAllergiesObject item) {
        item.setDeleted(false);
        if (item instanceof MedicationsObject) {
            for (MedicationsObject deleteObject : removeMedications) {
                if (deleteObject.getUuid() != null &&
                        deleteObject.getUuid().equals(item.getUuid())) {
                    removeMedications.remove(deleteObject);
                    break;
                }
            }
            if (tempMedications.contains(item)) {
                tempMedications.remove(item);
                addMedications.add((MedicationsObject) item);
            }
        }
        setAdapters();
    }

    /**
     * Exposed method to add an Medication or Allergy item to the list
     *
     * @param item item to add
     */
    public void addItem(MedicationsAllergiesObject item) {
        if (item instanceof MedicationsObject) {
            //check if exists
            if (currentMedications.contains(item)) {
                return;
            }
            if (removeMedications.contains(item)) {
                removeMedications.remove(item);
            }
            item.setAction(MedicationAllergiesAction.add);
            currentMedications.add((MedicationsObject) item);
            addMedications.add((MedicationsObject) item);
            shouldRemove = true;
        }
        setAdapters();
    }

    private List<MedicationsObject> getAllModifiedMedications() {
        List<MedicationsObject> combinedList = new ArrayList<>();
        combinedList.addAll(addMedications);
        for (MedicationsObject removeMedication : removeMedications) {
            MedicationsObject cloneObject = new MedicationsObject();
            cloneObject.setUuid(removeMedication.getUuid());
            cloneObject.setAction(removeMedication.getAction());
            combinedList.add(cloneObject);
        }
        return combinedList;
    }

    private View.OnClickListener chooseMedicationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showMedicationAllergySearchFragment(MedicationAllergySearchFragment.MEDICATION_ITEM);
        }
    };

    private View.OnClickListener takePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mediaScannerPresenter.selectImage(true);
        }
    };

    private View.OnClickListener removePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!StringUtil.isNullOrEmpty(medicationsAllergiesDTO.getPayload().getMedicationsImage().getPayload().getUrl())) {
                MedicationsImagePostModel medicationsImagePostModel = new MedicationsImagePostModel();
                medicationsImagePostModel.setDelete(true);
                medicationsPostModel.setMedicationsImage(medicationsImagePostModel);
            }
            photoPath = null;
            medicationPhoto.setImageDrawable(null);
            onImageLoadCompleted(false);
        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Gson gson = new Gson();
            TransitionDTO transitionDTO = medicationsAllergiesDTO.getMetadata().getTransitions().getMedications();

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("patient_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPatientId());
            queryMap.put("practice_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
            queryMap.put("practice_mgmt", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
            queryMap.put("appointment_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getAppointmentId());

            Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();
            headers.put("transition", "true");

            medicationsPostModel.setMedicationsList(getAllModifiedMedications());
            setupImageBase64();
            String jsonBody = gson.toJson(medicationsPostModel);
            patientResponsibilityViewModel.setAllergiesdata(medicationsAllergiesDTO,jsonBody);
            getWorkflowServiceHelper().execute(transitionDTO, submitMedicationAllergiesCallback,
                    jsonBody, queryMap, headers);
        }
    };

    private View.OnClickListener navigationClickListener = view -> {
        SystemUtil.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    };


    private WorkflowServiceCallback submitMedicationAllergiesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            List<MedicationsObject> modifiedMeds = getAllModifiedMedications();
            if (!modifiedMeds.isEmpty() || (hasPhoto() && !StringUtil.isNullOrEmpty(photoPath))) {
                String[] params = {getString(R.string.param_meds_count),
                        getString(R.string.param_meds_photo)
                };

                if(modifiedMeds.size()>0)
                {
                    Bundle args = new Bundle();
                    DtoHelper.bundleDto(args, medicationsAllergiesDTO);
                    shouldRemove=true;

                }else {
                    shouldRemove=false;
                }
                Object[] values = {modifiedMeds.size(),
                        hasPhoto() && !StringUtil.isNullOrEmpty(photoPath)
                };

                MixPanelUtil.logEvent(getString(R.string.event_updated_meds), params, values);
            }

            MixPanelUtil.endTimer(getString(R.string.timer_medications));
            updateMedicationsList(workflowDTO);


        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e("Server Error", exceptionMessage);
        }
    };

    private void updateMedicationsList(WorkflowDTO workflowDTO) {
        workflowDTOMedicine=workflowDTO;
        DemographicDTO demographicDTO = patientResponsibilityViewModel.getDemographicDTOData();


        Map<String, String> queries = new HashMap<>();
        if (!demographicDTO.getPayload().getAppointmentpayloaddto().isEmpty()) {
            queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
            queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());
        }

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", Boolean.valueOf(true).toString());

        Gson gson = new Gson();
        String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicDTO.getPayload().getDemographics().getPayload().getAddress());

        getWorkflowServiceHelper().execute(transitionDTO, consentFormCallback, demogrPayloadString, queries, header);

    }

    private WorkflowServiceCallback consentFormCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            MedicationsOnlyResultModel medicationsAllergiesDTO = new Gson().fromJson(workflowDTO.toString(), MedicationsOnlyResultModel.class);
            currentMedications = medicationsAllergiesDTO.getPayload().getMedications().getPayload();
            addMedications.clear();
            setAdapters();
            onUpdate(callback, workflowDTOMedicine);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);

        }
    };

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
    public void setCapturedBitmap(String path, View view) {
        documentScannerAdapter.setImageView(path, view, false, 0, 0, R.id.placeHolderIconImageViewId, this);
        photoPath = path;
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
        if (!StringUtil.isNullOrEmpty(photoPath)) {
            photoPath = DocumentScannerAdapter.getBase64(getContext(), photoPath);
            if (!StringUtil.isNullOrEmpty(photoPath)) {
                MedicationsImagePostModel medicationsImagePostModel = new MedicationsImagePostModel();
                medicationsImagePostModel.setPhoto(photoPath);
                medicationsPostModel.setMedicationsImage(medicationsImagePostModel);

                //update current payload to handle back nav
                medicationsAllergiesDTO.getPayload().getMedicationsImage().getPayload().setUrl(photoPath);
            }
        }
    }

    @Override
    public void onImageLoadCompleted(boolean success) {
        if (shouldAllowMedPicture) {
            if (success) {
                photoLayout.setVisibility(View.VISIBLE);
            } else {
                photoLayout.setVisibility(View.GONE);
            }
        }
        setAdapterVisibility();
    }

    @Override
    public DTO getDto() {
        return medicationsAllergiesDTO;
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

        View.OnClickListener bottomSheetClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenuAction(bottomSheetBehavior, BottomSheetBehavior.STATE_EXPANDED);
                shadow.setClickable(true);
            }
        };
        newPhotoButton.setOnClickListener(bottomSheetClickListener);
        changePhotoButton.setOnClickListener(bottomSheetClickListener);

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

