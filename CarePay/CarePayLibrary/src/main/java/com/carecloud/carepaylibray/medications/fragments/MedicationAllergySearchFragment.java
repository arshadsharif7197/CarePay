package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergySearchAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesLinkDTO;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergySearchFragment extends BaseFragment implements MedicationAllergySearchAdapter.SearchItemSelectedCallback {


    public interface MedicationAllergySearchCallback {
        void addMedicationAllergyItem(MedicationsAllergiesObject item);
    }

    public enum SearchMode {
        MEDICATION,
        ALLERGY;
    }

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationAllergySearchCallback callback;
    private SearchMode searchMode;

    private RecyclerView searchRecycler;
    private SearchView searchView;

    private List<? extends MedicationsAllergiesObject> resultsList = new ArrayList<>();


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MedicationAllergySearchCallback) context;
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

        String searchModeExtra = args.getString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA);
        this.searchMode = SearchMode.valueOf(searchModeExtra);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);
        initViews(view);

        setAdapters();
    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        toolbar.setTitle("");
        if(getDialog()==null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(navigationClickListener);
//            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        }
        searchView = (SearchView) toolbar.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(searchQueryListener);

    }

    private void initViews(View view){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        if(getDialog()!=null){
            View closeButton = view.findViewById(R.id.closeViewLayout);
            if(closeButton!= null){
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
            }
        }
    }

    private void setAdapters(){
        MedicationAllergySearchAdapter adapter = new MedicationAllergySearchAdapter(getContext(), resultsList, this);
        searchRecycler.setAdapter(adapter);
    }


    private void submitMedicatonSearch(String searchQuery){
        MedicationAllergiesLinkDTO searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearch();
        TransitionDTO transitionDTO = new TransitionDTO();
        transitionDTO.setMethod(searchDTO.getMethod());
        transitionDTO.setUrl(searchDTO.getUrl());

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(searchDTO.getQueryStrings().getPracticeId().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
        queryMap.put(searchDTO.getQueryStrings().getPracticeMgmt().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
        queryMap.put(searchDTO.getQueryStrings().getSearch().getName(), searchQuery);

        Map<String, String> headers = ((ISession) getContext()).getWorkflowServiceHelper().getPreferredLanguageHeader();

        ((ISession) getContext()).getWorkflowServiceHelper().execute(transitionDTO, medicationSearchCallback, queryMap, headers);

    }

    @Override
    public void searchItemSelected(MedicationsAllergiesObject item) {
        if(item instanceof MedicationsObject){
            callback.addMedicationAllergyItem(item);
            if(getDialog()!=null){
                dismiss();
            }
        }
    }

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().onBackPressed();
        }
    };

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            SystemUtil.hideSoftKeyboard(getActivity());
            switch (searchMode){
                case MEDICATION:
                    submitMedicatonSearch(query);
                    break;
                case  ALLERGY:
                    submitMedicatonSearch(query);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private WorkflowServiceCallback medicationSearchCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) getContext()).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) getContext()).hideProgressDialog();

            Gson gson = new Gson();
            medicationsAllergiesDTO =gson.fromJson(workflowDTO.toString(), MedicationsAllergiesResultsModel.class);
            resultsList = medicationsAllergiesDTO.getPayload().getSearchMedications().getMedicationsObjects();

            MedicationAllergySearchAdapter adapter = (MedicationAllergySearchAdapter) searchRecycler.getAdapter();
            adapter.setItems(resultsList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) getContext()).hideProgressDialog();
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}
