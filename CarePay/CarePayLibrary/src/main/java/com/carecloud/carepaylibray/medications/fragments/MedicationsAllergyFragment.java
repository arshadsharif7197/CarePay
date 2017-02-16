package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesLabelsDTO;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergyFragment extends BaseDialogFragment implements MedicationAllergiesAdapter.MedicationAllergiesAdapterCallback{

    public interface MedicationAllergyCallback {
        void showMedicationSearch();
        void showAllergiesSearch();
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
    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.medications_toolbar);
        toolbar.setTitle("");
        if(getDialog()==null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_patient_mode_nav_back));
            toolbar.setNavigationOnClickListener(navigationClickListener);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        }
        TextView title = (TextView) toolbar.findViewById(R.id.medications_toolbar_title);
        title.setText(getTextForLabel(labels.getMedicationAllergiesTitlebarText()));
    }

    private void initViews(View view){
        TextView header = (TextView) view.findViewById(R.id.allergy_medication_header);
        header.setText(getTextForLabel(labels.getAllergyMedicationHeader()));

        TextView headerMessage = (TextView) view.findViewById(R.id.allergy_medication_header_message);
        headerMessage.setText(getTextForLabel(labels.getAllergyMedicationHeaderMessage()));

        TextView allergySection = (TextView) view.findViewById(R.id.allergy_section_header);
        allergySection.setText(getTextForLabel(labels.getAllergySectionHeader()));

        TextView allergyTitle = (TextView) view.findViewById(R.id.allergy_title);
        allergyTitle.setText(getTextForLabel(labels.getAllergyTitle()));

        TextView allergyChooseButton = (TextView) view.findViewById(R.id.allergy_choose_button);
        allergyChooseButton.setText(getTextForLabel(labels.getAllergyChooseButton()));
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        TextView allergyPlaceholderText = (TextView) view.findViewById(R.id.allergy_none_placeholder_text);
        allergyPlaceholderText.setText(getTextForLabel(labels.getAllergyNonePlaceholderText()));

        TextView medicationSection = (TextView) view.findViewById(R.id.medications_section_header);
        medicationSection.setText(getTextForLabel(labels.getMedicationsSectionHeader()));

        TextView medicationTitle = (TextView) view.findViewById(R.id.medications_title);
        medicationTitle.setText(getTextForLabel(labels.getMedicationsTitle()));

        TextView medicationChooseButton = (TextView) view.findViewById(R.id.medication_choose_button);
        medicationChooseButton.setText(getTextForLabel(labels.getMedicationChooseButton()));
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        TextView medicationPlaceholderText = (TextView) view.findViewById(R.id.medication_none_placeholder_text);
        medicationPlaceholderText.setText(getTextForLabel(labels.getMedicationNonePlaceholderText()));

        TextView continueButton = (TextView) view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setText(getTextForLabel(labels.getMedicationAllergiesContinueButton()));
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
        MedicationAllergiesAdapter medicationAdapter = new MedicationAllergiesAdapter(getContext(), currentMedications, this, getTextForLabel(labels.getMedicationAllergiesDeleteButton()));
        medicationRecycler.setAdapter(medicationAdapter);

        setAdapterVisibility();
    }

    private void setAdapterVisibility(){
        if(currentMedications.isEmpty()){
            medicationRecycler.setVisibility(View.GONE);
            placeholderMedication.setVisibility(View.VISIBLE);
        }else{
            medicationRecycler.setVisibility(View.VISIBLE);
            placeholderMedication.setVisibility(View.GONE);
        }

        allergyRecycler.setVisibility(View.GONE);
        placeholderAllergies.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteItem(MedicationsAllergiesObject item) {
        if(item instanceof MedicationsObject){
            //remove Medication from list
            item.setAction(MedicationAllergiesAction.DELETE);
            removeMedications.add((MedicationsObject) item);
            currentMedications.remove(item);

            MedicationAllergiesAdapter adapter =((MedicationAllergiesAdapter)medicationRecycler.getAdapter());
            adapter.setItems(currentMedications);
            adapter.notifyDataSetChanged();

            setAdapterVisibility();
        }

    }


    private View.OnClickListener chooseAllergyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callback.showAllergiesSearch();
        }
    };

    private View.OnClickListener chooseMedicationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callback.showMedicationSearch();
        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };


}

