package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.demographics.DemographicsView;
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
 * Created by lmenendez on 2/15/17
 */

public class MedicationAllergySearchFragment extends BaseDialogFragment implements MedicationAllergySearchAdapter.SearchItemSelectedCallback {


    public interface MedicationAllergySearchCallback {
        void addMedicationAllergyItem(MedicationsAllergiesObject item);
    }

    public enum SearchMode {
        MEDICATION,
        ALLERGY
    }

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationAllergySearchCallback callback;
    private SearchMode searchMode;

    private RecyclerView searchRecycler;
    private SearchView searchView;

    private List<? extends MedicationsAllergiesObject> resultsList = new ArrayList<>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    private void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (MedicationAllergySearchCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement MedicationAllergyCallback");
        }

    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Gson gson = new Gson();
        Bundle args = getArguments();
        String jsonExtra = args.getString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA);
        medicationsAllergiesDTO = gson.fromJson(jsonExtra, MedicationsAllergiesResultsModel.class);

        String searchModeExtra = args.getString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA);
        this.searchMode = SearchMode.valueOf(searchModeExtra);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);
        initViews(view);

        setAdapters();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        toolbar.setTitle("");
        if (getDialog() == null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(navigationClickListener);
        }
        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(searchQueryListener);
        searchView.requestFocus(View.FOCUS_DOWN);
        SystemUtil.showSoftKeyboard(getActivity());

        TextView title = (TextView) view.findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(Label.getLabel("medications_title"));
            title.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    private void initViews(View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getContext(), view);
                dismiss();
            }
        });

    }

    private void setAdapters() {
        MedicationAllergySearchAdapter adapter = new MedicationAllergySearchAdapter(getContext(), resultsList, this);
        searchRecycler.setAdapter(adapter);
    }


    private void submitMedicatonSearch(String searchQuery) {
        MedicationAllergiesLinkDTO searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearch();
        TransitionDTO transitionDTO = new TransitionDTO();
        transitionDTO.setMethod(searchDTO.getMethod());
        transitionDTO.setUrl(searchDTO.getUrl());

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(searchDTO.getQueryStrings().getPracticeId().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
        queryMap.put(searchDTO.getQueryStrings().getPracticeMgmt().getName(), medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
        queryMap.put(searchDTO.getQueryStrings().getSearch().getName(), searchQuery);

        Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();

        getWorkflowServiceHelper().execute(transitionDTO, medicationSearchCallback, queryMap, headers);

    }

    @Override
    public void searchItemSelected(MedicationsAllergiesObject item) {
        SystemUtil.hideSoftKeyboard(getContext(), getView());
        if (item instanceof MedicationsObject) {
                    callback.addMedicationAllergyItem(item);
            if (getDialog() != null) {
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
            switch (searchMode) {
                case MEDICATION:
                    submitMedicatonSearch(query);
                    break;
                case ALLERGY:
                    submitMedicatonSearch(query);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() > 0) {
                getWorkflowServiceHelper().interrupt();

                submitMedicatonSearch(newText);
            }
            return false;
        }
    };

    private WorkflowServiceCallback medicationSearchCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            Gson gson = new Gson();
            MedicationsAllergiesResultsModel searchResults = gson.fromJson(workflowDTO.toString(), MedicationsAllergiesResultsModel.class);
            resultsList = searchResults.getPayload().getSearchMedications().getMedicationsObjects();

            MedicationAllergySearchAdapter adapter = (MedicationAllergySearchAdapter) searchRecycler.getAdapter();
            adapter.setItems(resultsList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

}