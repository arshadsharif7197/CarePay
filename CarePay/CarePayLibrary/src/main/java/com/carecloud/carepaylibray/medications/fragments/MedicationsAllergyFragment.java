package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.CheckinSettingsDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.AllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsImagePostModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.medications.models.MedicationsPostModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17
 */

public class MedicationsAllergyFragment extends BaseCheckinFragment implements
        MedicationAllergiesAdapter.MedicationAllergiesAdapterCallback, MediaViewInterface,
        DocumentScannerAdapter.ImageLoadCallback {

    public static final int ALLERGY_ITEM = 100;
    public static final int MEDICATION_ITEM = 101;
    private TextView checkBoxAlert;
    private RecyclerView allergyRecycler;
    private RecyclerView medicationRecycler;
    private CheckBox assertNoMedications;
    private CheckBox assertNoAllergies;
    private Button continueButton;
    private View emptyPhotoLayout;
    private View photoLayout;
    private ImageView medicationPhoto;
    private MediaScannerPresenter mediaScannerPresenter;
    private DocumentScannerAdapter documentScannerAdapter;
    private String photoPath;

    protected DemographicsPresenter callback;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationsPostModel medicationsPostModel = new MedicationsPostModel();

    private List<MedicationsObject> currentMedications = new ArrayList<>();
    private List<MedicationsObject> addMedications = new ArrayList<>();
    private List<MedicationsObject> removeMedications = new ArrayList<>();

    private List<AllergiesObject> currentAllergies = new ArrayList<>();
    private List<AllergiesObject> removeAllergies = new ArrayList<>();
    private List<AllergiesObject> addAllergies = new ArrayList<>();

    private Handler handler = new Handler();
    private TextView allergyChooseButton;
    private TextView medicationChooseButton;


    public interface MedicationAllergyCallback {
        void showMedicationAllergySearchFragment(int searchType);
    }

    public static MedicationsAllergyFragment newInstance(MedicationsAllergiesResultsModel medicationsAllergiesDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        MedicationsAllergyFragment fragment = new MedicationsAllergyFragment();
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
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        callback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 1, 1);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, getArguments());
        currentMedications = medicationsAllergiesDTO.getPayload().getMedications().getPayload();
        currentAllergies = medicationsAllergiesDTO.getPayload().getAllergies().getPayload();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_medications_allergy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);
        initViews(view);
        setAdapters();
        manageSectionsVisibility(view);
        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scroll_medications_allergy);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);

            }
        }, 30);
        View container = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(container);
    }

    private void manageSectionsVisibility(View view) {
        CheckinSettingsDTO checkinSettings = medicationsAllergiesDTO.getPayload().getCheckinSettings();
        if (!checkinSettings.shouldShowAllergies()) {
            view.findViewById(R.id.allergies_layout).setVisibility(View.GONE);
        }
        if (!checkinSettings.shouldShowMedications()) {
            view.findViewById(R.id.medications_layout).setVisibility(View.GONE);
        }
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(navigationClickListener);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(Label.getLabel("medication_allergies_titlebar_text"));
        }
    }

    private void initViews(View view) {
        allergyChooseButton = (TextView) view.findViewById(R.id.allergy_choose_button);
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        medicationChooseButton = (TextView) view.findViewById(R.id.medication_choose_button);
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        continueButton = (Button) view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setOnClickListener(continueClickListener);
        continueButton.setSelected(false);
        continueButton.setClickable(false);
        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    showAlert();
                    return true;
                }
                return false;
            }
        });

        EditText unlistedAllergies = (EditText) view.findViewById(R.id.allergy_none_placeholder_text);
        unlistedAllergies.setOnEditorActionListener(getUnlistedItemActionListener(ALLERGY_ITEM));
        unlistedAllergies.setOnFocusChangeListener(getOnFocusChangeListener(Label.getLabel("allergy_none_placeholder_text")));

        EditText unlistedMedication = (EditText) view.findViewById(R.id.medication_none_placeholder_text);
        unlistedMedication.setOnEditorActionListener(getUnlistedItemActionListener(MEDICATION_ITEM));
        unlistedMedication.setOnFocusChangeListener(getOnFocusChangeListener(Label.getLabel("medication_none_placeholder_text")));

        RecyclerView.LayoutManager allergyManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        allergyRecycler = (RecyclerView) view.findViewById(R.id.alergy_recycler);
        allergyRecycler.setLayoutManager(allergyManager);

        RecyclerView.LayoutManager medicationManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        medicationRecycler = (RecyclerView) view.findViewById(R.id.medication_recycler);
        medicationRecycler.setLayoutManager(medicationManager);

        assertNoMedications = (CheckBox) view.findViewById(R.id.assert_no_meds);
        assertNoMedications.setEnabled(false);
        assertNoMedications.setChecked(false);
        assertNoMedications.setOnCheckedChangeListener(assertCheckListener);

        assertNoAllergies = (CheckBox) view.findViewById(R.id.assert_no_allergies);
        assertNoAllergies.setEnabled(false);
        assertNoAllergies.setChecked(false);
        assertNoAllergies.setOnCheckedChangeListener(assertCheckListener);

        emptyPhotoLayout = view.findViewById(R.id.medication_photo_empty_layout);
        photoLayout = view.findViewById(R.id.medication_photo_layout);
        medicationPhoto = (ImageView) view.findViewById(R.id.medications_image);

        View newPhotoButton = view.findViewById(R.id.medication_list_photo_button);
        newPhotoButton.setOnClickListener(takePhotoListener);

        View changePhotoButton = view.findViewById(R.id.medication_list_photo_change);
        changePhotoButton.setOnClickListener(takePhotoListener);

        View removePhotoButton = view.findViewById(R.id.medication_list_photo_remove);
        removePhotoButton.setOnClickListener(removePhotoListener);

        initImageViews(view);
        checkBoxAlert = (TextView) view.findViewById(R.id.checkBoxAlert);
    }

    private void showAlert() {
        String alertMessage = Label.getLabel("demographics_check_alert_message");
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            new CustomMessageToast(getActivity(), alertMessage,
                    CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        } else {
            checkBoxAlert.setText(alertMessage);
            checkBoxAlert.setVisibility(View.VISIBLE);
        }
    }

    private void initImageViews(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this,
                CarePayCameraPreview.CameraType.SCAN_DOC);
        mediaScannerPresenter.setCaptureView(medicationPhoto);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType(), false);

        String url = medicationsAllergiesDTO.getPayload().getMedicationsImage().getPayload().getUrl();
        if (StringUtil.isNullOrEmpty(url)) {
            emptyPhotoLayout.setVisibility(View.VISIBLE);
        } else {
            documentScannerAdapter.setImageView(url, medicationPhoto, false, 0, 0, R.drawable.icn_placeholder_document, this);
        }

    }

    private void setAdapters() {
        if (medicationRecycler.getAdapter() == null) {
            MedicationAllergiesAdapter medicationAdapter = new MedicationAllergiesAdapter(currentMedications,
                    this);
            medicationRecycler.setAdapter(medicationAdapter);
        } else {
            MedicationAllergiesAdapter adapter = ((MedicationAllergiesAdapter) medicationRecycler.getAdapter());
            adapter.setItems(currentMedications);
            adapter.notifyDataSetChanged();
        }
        if (allergyRecycler.getAdapter() == null) {
            MedicationAllergiesAdapter allergyAdapter = new MedicationAllergiesAdapter(currentAllergies,
                    this);
            allergyRecycler.setAdapter(allergyAdapter);
        } else {
            MedicationAllergiesAdapter adapter = ((MedicationAllergiesAdapter) allergyRecycler.getAdapter());
            adapter.setItems(currentAllergies);
            adapter.notifyDataSetChanged();
        }
        setAdapterVisibility();
    }

    private void setAdapterVisibility() {
        if (currentMedications.isEmpty() && !hasPhoto()) {
            medicationRecycler.setVisibility(View.GONE);
            assertNoMedications.setVisibility(View.VISIBLE);
            assertNoMedications.setEnabled(true);
            medicationChooseButton.setText(Label.getLabel("demographics_choose"));
        } else {
            medicationRecycler.setVisibility(View.VISIBLE);
//            medicationRecycler.getLayoutManager().setMeasuredDimension(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
            assertNoMedications.setChecked(false);
            assertNoMedications.setEnabled(false);
            assertNoMedications.setVisibility(View.GONE);
            if (!currentMedications.isEmpty()){
                medicationChooseButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_another"));
            }else{
                medicationChooseButton.setText(Label.getLabel("demographics_choose"));
            }
        }

        if (currentAllergies.isEmpty()) {
            allergyRecycler.setVisibility(View.GONE);
            assertNoAllergies.setVisibility(View.VISIBLE);
            assertNoAllergies.setEnabled(true);;
            allergyChooseButton.setText(Label.getLabel("demographics_choose"));
        } else {
            allergyRecycler.setVisibility(View.VISIBLE);
//            allergyRecycler.getLayoutManager().setMeasuredDimension(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
            assertNoAllergies.setChecked(false);
            assertNoAllergies.setEnabled(false);
            assertNoAllergies.setVisibility(View.GONE);
            allergyChooseButton.setText(Label.getLabel("practice_checkin_demogr_ins_add_another"));
        }

        validateForm();
    }

    private void validateForm() {
        boolean isAllerySectionEnabled = medicationsAllergiesDTO.getPayload().getCheckinSettings()
                .shouldShowAllergies();
        boolean isMedicineSectionEnabled = medicationsAllergiesDTO.getPayload().getCheckinSettings()
                .shouldShowMedications();
        boolean validAllergies = (!isAllerySectionEnabled || (allergyRecycler.getVisibility() == View.VISIBLE ||
                !assertNoAllergies.isEnabled() || assertNoAllergies.isChecked()));
        boolean validMeds = (!isMedicineSectionEnabled ||
                (!currentMedications.isEmpty() || !assertNoMedications.isEnabled()
                        || assertNoMedications.isChecked()));

        boolean valid = (validAllergies && (validMeds || hasPhoto()));
        if (valid && checkBoxAlert != null) {
            checkBoxAlert.setVisibility(View.GONE);
        }
        continueButton.setSelected(valid);
        continueButton.setClickable(valid);
    }

    private boolean hasPhoto() {
        return (!StringUtil.isNullOrEmpty(medicationsAllergiesDTO.getPayload()
                .getMedicationsImage().getPayload().getUrl()) &&
                (medicationsPostModel.getMedicationsImage() == null ||
                        !medicationsPostModel.getMedicationsImage().isDelete())) ||
                !StringUtil.isNullOrEmpty(photoPath);
    }

    @Override
    public void deleteItem(MedicationsAllergiesObject item) {
        if (item instanceof MedicationsObject) {
            //remove Medication from list
            currentMedications.remove(item);
            if (addMedications.contains(item)) {
                addMedications.remove(item);
            } else {
                item.setAction(MedicationAllergiesAction.delete);
                MedicationsObject medicationToDelete = new MedicationsObject();
                medicationToDelete.setAction(item.getAction());
                medicationToDelete.setUuid(item.getUuid());
                removeMedications.add(medicationToDelete);
            }
        } else {
            currentAllergies.remove(item);
            if (addAllergies.contains(item)) {
                addAllergies.remove(item);
            } else {
                item.setAction(MedicationAllergiesAction.delete);
                AllergiesObject allergyToDelete = new AllergiesObject();
                allergyToDelete.setAction(item.getAction());
                allergyToDelete.setUuid(item.getUuid());
                removeAllergies.add(allergyToDelete);
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
        } else if (item instanceof AllergiesObject) {
            if (currentAllergies.contains(item)) {
                return;
            }
            if (removeAllergies.contains(item)) {
                removeAllergies.remove(item);
            }
            item.setAction(MedicationAllergiesAction.add);
            currentAllergies.add((AllergiesObject) item);
            addAllergies.add((AllergiesObject) item);
        }
        setAdapters();
    }

    private List<MedicationsObject> getAllModifiedMedications() {
        List<MedicationsObject> combinedList = new ArrayList<>();
        combinedList.addAll(addMedications);
        combinedList.addAll(removeMedications);
        return combinedList;
    }

    private List<AllergiesObject> getAllModifiedAllergies() {
        List<AllergiesObject> combinedList = new ArrayList<>();
        combinedList.addAll(addAllergies);
        combinedList.addAll(removeAllergies);
        return combinedList;
    }

    private View.OnClickListener chooseAllergyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showMedicationAllergySearchFragment(ALLERGY_ITEM);
        }
    };

    private View.OnClickListener chooseMedicationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showMedicationAllergySearchFragment(MEDICATION_ITEM);
        }
    };

    private CompoundButton.OnCheckedChangeListener assertCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            validateForm();
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

            Map<String, String> headers = ((ISession) getContext()).getWorkflowServiceHelper().getPreferredLanguageHeader();
            headers.put("transition", "true");

            medicationsPostModel.setMedicationsList(getAllModifiedMedications());
            medicationsPostModel.setAllergiesList(getAllModifiedAllergies());
            setupImageBase64();
            String jsonBody = gson.toJson(medicationsPostModel);
            getWorkflowServiceHelper().execute(transitionDTO, submitMedicationAllergiesCallback,
                    jsonBody, queryMap, headers);
        }
    };

    private TextView.OnEditorActionListener getUnlistedItemActionListener(final int itemType) {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (!StringUtil.isNullOrEmpty(textView.getText().toString())) {
                    if (itemType == MEDICATION_ITEM) {
                        MedicationsObject medicationsObject = new MedicationsObject();
                        medicationsObject.setDispensableDrugId("");
                        medicationsObject.setDisplayName(textView.getText().toString());

                        addItem(medicationsObject);
                    }
                }
                SystemUtil.hideSoftKeyboard(getActivity());
                textView.clearFocus();
                return true;
            }
        };
    }

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SystemUtil.hideSoftKeyboard(getActivity());
            getActivity().onBackPressed();
        }
    };


    private View.OnFocusChangeListener getOnFocusChangeListener(final String hintString) {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                TextView textView = (TextView) view;
                if (hasFocus) {
                    textView.setText(null);
                    textView.setHint(null);
                } else {
                    textView.setText(null);
                    textView.setHint(hintString);
                }
            }
        };
    }

    private WorkflowServiceCallback submitMedicationAllergiesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) getContext()).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) getContext()).hideProgressDialog();
            onUpdate(callback, workflowDTO);

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
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
        if (success) {
            emptyPhotoLayout.setVisibility(View.GONE);
            photoLayout.setVisibility(View.VISIBLE);
        } else {
            emptyPhotoLayout.setVisibility(View.VISIBLE);
            photoLayout.setVisibility(View.GONE);
        }
        setAdapterVisibility();
//        validateForm();
    }


}

