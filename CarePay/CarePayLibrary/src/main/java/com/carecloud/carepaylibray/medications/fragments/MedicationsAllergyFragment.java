package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraPreview;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerAdapter;
import com.carecloud.carepaylibray.media.MediaScannerPresenter;
import com.carecloud.carepaylibray.media.MediaViewInterface;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesQueryStrings;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
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
        DocumentScannerAdapter.ImageLoadCallback{

    public interface MedicationAllergyCallback {
        void showMedicationSearch();

        void showAllergiesSearch();

        void medicationSubmitSuccess(WorkflowDTO workflowDTO);

        void medicationSubmitFail(String message);
    }

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

    protected DemographicsPresenter callback;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;

    private List<MedicationsObject> currentMedications = new ArrayList<>();
    private List<MedicationsObject> addMedications = new ArrayList<>();
    private List<MedicationsObject> removeMedications = new ArrayList<>();

    private Handler handler = new Handler();

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    public void attachCallback(Context context){
        try{
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (DemographicsPresenter) context;
            }
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement DemographicsPresenter");
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if(callback == null){
            attachCallback(getContext());
        }
        callback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 1, 1);
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Gson gson = new Gson();
        Bundle args = getArguments();
        String jsonExtra = args.getString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA);
        medicationsAllergiesDTO = gson.fromJson(jsonExtra, MedicationsAllergiesResultsModel.class);
        currentMedications = medicationsAllergiesDTO.getPayload().getMedications().getPayload();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_medications_allergy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);
        initViews(view);

        setAdapters();

        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scroll_medications_allergy);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,0);

            }
        },30);

        View container = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(container);
    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(navigationClickListener);


        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        if(title!=null) {
            title.setText(Label.getLabel("medication_allergies_titlebar_text"));
        }
    }

    private void initViews(View view){
        TextView allergyChooseButton = (TextView) view.findViewById(R.id.allergy_choose_button);
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        TextView medicationChooseButton = (TextView) view.findViewById(R.id.medication_choose_button);
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        continueButton = (Button) view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setOnClickListener(continueClickListener);
        continueButton.setEnabled(false);

        EditText unlistedAllergies = (EditText) view.findViewById(R.id.allergy_none_placeholder_text);
        unlistedAllergies.setOnFocusChangeListener(getOnFocusChangeListener(Label.getLabel("allergy_none_placeholder_text")));
        unlistedAllergies.setEnabled(false);

        EditText unlistedMedication = (EditText) view.findViewById(R.id.medication_none_placeholder_text);
        unlistedMedication.setOnEditorActionListener(addUnlistedMedicationListener);
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
    }

    private void initImageViews(View view) {
        mediaScannerPresenter = new MediaScannerPresenter(getContext(), this,
                CarePayCameraPreview.CameraType.SCAN_DOC);
        mediaScannerPresenter.setCaptureView(medicationPhoto);
        documentScannerAdapter = new DocumentScannerAdapter(getContext(), view, mediaScannerPresenter, getApplicationMode().getApplicationType());

        String url =  medicationsAllergiesDTO.getPayload().getMedicationsImage().getPayload().getUrl();
        if(StringUtil.isNullOrEmpty(url)){
            emptyPhotoLayout.setVisibility(View.VISIBLE);
        }else{
            documentScannerAdapter.setImageView(url, medicationPhoto, false, 0, 0, R.id.placeHolderIconImageViewId, this);
        }

    }

    private void setAdapters(){
        if(medicationRecycler.getAdapter()==null) {
            MedicationAllergiesAdapter medicationAdapter = new MedicationAllergiesAdapter(getContext(), currentMedications, this);
            medicationRecycler.setAdapter(medicationAdapter);
        }else{
            MedicationAllergiesAdapter adapter =((MedicationAllergiesAdapter)medicationRecycler.getAdapter());
            adapter.setItems(currentMedications);
            adapter.notifyDataSetChanged();
        }
        setAdapterVisibility();
    }

    private void setAdapterVisibility(){
        if(currentMedications.isEmpty()){
            medicationRecycler.setVisibility(View.GONE);
            assertNoMedications.setVisibility(View.VISIBLE);
            assertNoMedications.setEnabled(true);
        }else{
            medicationRecycler.setVisibility(View.VISIBLE);
            medicationRecycler.getLayoutManager().setMeasuredDimension(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
            assertNoMedications.setChecked(false);
            assertNoMedications.setEnabled(false);
            assertNoMedications.setVisibility(View.GONE);
        }

        allergyRecycler.setVisibility(View.GONE);

        validateForm();
    }

    private void validateForm(){
        boolean valid = (allergyRecycler.getVisibility() == View.VISIBLE || !assertNoAllergies.isEnabled() || assertNoAllergies.isChecked()) &&
                (!currentMedications.isEmpty() || !assertNoMedications.isEnabled() || assertNoMedications.isChecked());

        continueButton.setEnabled(valid);
    }

    @Override
    public void deleteItem(MedicationsAllergiesObject item) {
        if(item instanceof MedicationsObject){
            //remove Medication from list
            currentMedications.remove(item);
            if(addMedications.contains(item)){
                addMedications.remove(item);
            }else {
                item.setAction(MedicationAllergiesAction.delete);
                removeMedications.add((MedicationsObject) item);
            }
        }
        setAdapters();

    }

    /**
     * Exposed method to add an Medication or Allergy item to the list
     * @param item item to add
     */
    public void addItem(MedicationsAllergiesObject item){
        if(item instanceof  MedicationsObject){
            //check if exists
            if(currentMedications.contains(item)){
                return;
            }
            if(removeMedications.contains(item)){
                removeMedications.remove(item);
            }
            item.setAction(MedicationAllergiesAction.add);
            currentMedications.add((MedicationsObject) item);
            addMedications.add((MedicationsObject) item);
        }
        setAdapters();
    }

    private List<MedicationsObject> getAllModifiedItems(){
        List<MedicationsObject> combinedList = new ArrayList<>();
        combinedList.addAll(addMedications);
        combinedList.addAll(removeMedications);
        return combinedList;
    }

    private View.OnClickListener chooseAllergyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showAllergiesSearch();
        }
    };

    private View.OnClickListener chooseMedicationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showMedicationSearch();
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
            //// TODO: 11/16/17
        }
    };

    private View.OnClickListener removePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //// TODO: 11/16/17
        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Gson gson = new Gson();
            TransitionDTO transitionDTO = medicationsAllergiesDTO.getMetadata().getTransitions().getMedications();

            MedicationsAllergiesQueryStrings medicationsAllergiesQueryStrings = gson.fromJson(transitionDTO.getQueryString().toString(), MedicationsAllergiesQueryStrings.class);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put(medicationsAllergiesQueryStrings.getPatientId().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPatientId());
            queryMap.put(medicationsAllergiesQueryStrings.getPracticeId().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
            queryMap.put(medicationsAllergiesQueryStrings.getPracticeMgmt().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
            queryMap.put(medicationsAllergiesQueryStrings.getAppointmentId().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getAppointmentId());

            Map<String, String> headers = ((ISession) getContext()).getWorkflowServiceHelper().getPreferredLanguageHeader();
            headers.put("transition", "true");

            String jsonBody = gson.toJson(getAllModifiedItems());
            ((ISession) getContext()).getWorkflowServiceHelper().execute(transitionDTO, submitMedicationAllergiesCallback, jsonBody, queryMap, headers);


        }
    };

    private TextView.OnEditorActionListener addUnlistedMedicationListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            if(!StringUtil.isNullOrEmpty(textView.getText().toString())) {
                MedicationsObject medicationsObject = new MedicationsObject();
                medicationsObject.setDispensableDrugId("");
                medicationsObject.setDisplayName(textView.getText().toString());

                addItem(medicationsObject);
            }
            SystemUtil.hideSoftKeyboard(getActivity());
            textView.clearFocus();
            return true;
        }
    };

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SystemUtil.hideSoftKeyboard(getActivity());
            getActivity().onBackPressed();
        }
    };


    private View.OnFocusChangeListener getOnFocusChangeListener(final String hintString){
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                TextView textView = (TextView) view;
                if(hasFocus){
                    textView.setText(null);
                    textView.setHint(null);
                }else{
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
            ((ISession) getContext()).hideProgressDialog();
            callback.medicationSubmitFail(exceptionMessage);
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
    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void setCapturedBitmap(String path, View view) {

    }

    @Override
    public void handleStartActivityForResult(Intent intent, int requestCode) {

    }

    @Nullable
    @Override
    public Fragment getCallingFragment() {
        return null;
    }

    @Override
    public void setupImageBase64() {

    }

    @Override
    public void onImageLoadCompleted(boolean success) {
        if(success){
            emptyPhotoLayout.setVisibility(View.GONE);
            photoLayout.setVisibility(View.VISIBLE);
        }else{
            emptyPhotoLayout.setVisibility(View.VISIBLE);
            photoLayout.setVisibility(View.GONE);
        }
    }


}

