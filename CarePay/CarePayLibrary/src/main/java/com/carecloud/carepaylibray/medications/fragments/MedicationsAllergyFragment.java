package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesLabelsDTO;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesQueryStrings;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergyFragment extends BaseFragment implements MedicationAllergiesAdapter.MedicationAllergiesAdapterCallback{

    public interface MedicationAllergyCallback {
        void showMedicationSearch();

        void showAllergiesSearch();

        void medicationSubmitSuccess(WorkflowDTO workflowDTO);

        void medicationSubmitFail(String message);
    }

    private View placeholderAllergies;
    private View placeholderMedication;
    private RecyclerView allergyRecycler;
    private RecyclerView medicationRecycler;

    private MedicationAllergyCallback callback;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationAllergiesLabelsDTO labels;

    private List<MedicationsObject> currentMedications = new ArrayList<>();
    private List<MedicationsObject> addMedications = new ArrayList<>();
    private List<MedicationsObject> removeMedications = new ArrayList<>();

    private Handler handler = new Handler();

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MedicationAllergyCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached Context must implement MedicationAllergyCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Gson gson = new Gson();
        Bundle args = getArguments();
        String jsonExtra = args.getString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA);
        medicationsAllergiesDTO = gson.fromJson(jsonExtra, MedicationsAllergiesResultsModel.class);
        labels = medicationsAllergiesDTO.getMetadata().getLabels();
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

    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(navigationClickListener);


        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(StringUtil.getLabelForView(labels.getMedicationAllergiesTitlebarText()));
    }

    private void initViews(View view){
        TextView header = (TextView) view.findViewById(R.id.allergy_medication_header);
        header.setText(StringUtil.getLabelForView(labels.getAllergyMedicationHeader()));

        TextView headerMessage = (TextView) view.findViewById(R.id.allergy_medication_header_message);
        headerMessage.setText(StringUtil.getLabelForView(labels.getAllergyMedicationHeaderMessage()));

        TextView allergySection = (TextView) view.findViewById(R.id.allergy_section_header);
        allergySection.setText(StringUtil.getLabelForView(labels.getAllergySectionHeader()));

        TextView allergyTitle = (TextView) view.findViewById(R.id.allergy_title);
        allergyTitle.setText(StringUtil.getLabelForView(labels.getAllergyTitle()));

        TextView allergyChooseButton = (TextView) view.findViewById(R.id.allergy_choose_button);
        allergyChooseButton.setText(StringUtil.getLabelForView(labels.getAllergyChooseButton()));
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        TextView allergyPlaceholderText = (TextView) view.findViewById(R.id.allergy_none_placeholder_text);
        allergyPlaceholderText.setText(StringUtil.getLabelForView(labels.getAllergyNonePlaceholderText()));

        TextView medicationSection = (TextView) view.findViewById(R.id.medications_section_header);
        medicationSection.setText(StringUtil.getLabelForView(labels.getMedicationsSectionHeader()));

        TextView medicationTitle = (TextView) view.findViewById(R.id.medications_title);
        medicationTitle.setText(StringUtil.getLabelForView(labels.getMedicationsTitle()));

        TextView medicationChooseButton = (TextView) view.findViewById(R.id.medication_choose_button);
        medicationChooseButton.setText(StringUtil.getLabelForView(labels.getMedicationChooseButton()));
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        TextView medicationPlaceholderText = (TextView) view.findViewById(R.id.medication_none_placeholder_text);
        medicationPlaceholderText.setText(StringUtil.getLabelForView(labels.getMedicationNonePlaceholderText()));

        Button continueButton = (Button) view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setText(StringUtil.getLabelForView(labels.getMedicationAllergiesContinueButton()));
        continueButton.setOnClickListener(continueClickListener);

        placeholderAllergies = view.findViewById(R.id.allergy_none_placeholder);
        placeholderMedication = view.findViewById(R.id.medication_none_placeholder);

        RecyclerView.LayoutManager allergyManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        allergyRecycler = (RecyclerView) view.findViewById(R.id.alergy_recycler);
        allergyRecycler.setLayoutManager(allergyManager);

        RecyclerView.LayoutManager medicationManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        medicationRecycler = (RecyclerView) view.findViewById(R.id.medication_recycler);
        medicationRecycler.setLayoutManager(medicationManager);

    }

    private void setAdapters(){
        if(medicationRecycler.getAdapter()==null) {
            MedicationAllergiesAdapter medicationAdapter = new MedicationAllergiesAdapter(getContext(), currentMedications, this, StringUtil.getLabelForView(labels.getMedicationAllergiesDeleteButton()));
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
            placeholderMedication.setVisibility(View.VISIBLE);
        }else{
            medicationRecycler.setVisibility(View.VISIBLE);
            placeholderMedication.setVisibility(View.GONE);
            medicationRecycler.getLayoutManager().setMeasuredDimension(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
        }

        allergyRecycler.setVisibility(View.GONE);
        placeholderAllergies.setVisibility(View.VISIBLE);
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

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().onBackPressed();
        }
    };

    private WorkflowServiceCallback submitMedicationAllergiesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) getContext()).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) getContext()).hideProgressDialog();
            callback.medicationSubmitSuccess(workflowDTO);

        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) getContext()).hideProgressDialog();
            callback.medicationSubmitFail(exceptionMessage);
        }
    };

}

