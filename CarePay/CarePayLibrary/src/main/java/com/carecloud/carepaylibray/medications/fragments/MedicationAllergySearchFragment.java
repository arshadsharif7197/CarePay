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

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergySearchAdapter;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17
 */

public class MedicationAllergySearchFragment extends BaseDialogFragment implements MedicationAllergySearchAdapter.SearchItemSelectedCallback {


    private int searchMode;

    public interface MedicationAllergySearchCallback {
        void addMedicationAllergyItem(MedicationsAllergiesObject item);
    }

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationAllergySearchCallback callback;
    private RecyclerView searchRecycler;
    private SearchView searchView;

    public static MedicationAllergySearchFragment newInstance(MedicationsAllergiesResultsModel medicationsAllergiesDTO,
                                                              int searchMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        args.putInt("searchMode", searchMode);
        MedicationAllergySearchFragment fragment = new MedicationAllergySearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, getArguments());
        searchMode = getArguments().getInt("searchMode");
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
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    getActivity().onBackPressed();
                }
            });
        }
        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(searchQueryListener);
        searchView.requestFocus(View.FOCUS_DOWN);
        SystemUtil.showSoftKeyboard(getActivity());

        TextView title = (TextView) view.findViewById(R.id.toolbar_title);
        if (title != null) {
            if (searchMode == MedicationsAllergyFragment.ALLERGY_ITEM) {
                title.setText(Label.getLabel("allergies_title"));
            } else {
                title.setText(Label.getLabel("medications_title"));
            }
            title.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    private void initViews(View view) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);
        View closeButton = view.findViewById(R.id.closeViewLayout);
        if (closeButton != null) {
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUtil.hideSoftKeyboard(getContext(), view);
                    dismiss();
                }
            });
        }
    }

    private void setAdapters() {
        MedicationAllergySearchAdapter adapter = new MedicationAllergySearchAdapter(getContext(),
                new ArrayList<MedicationsAllergiesObject>(), this, searchMode);
        searchRecycler.setAdapter(adapter);
    }


    private void submitSearch(String searchQuery, int searchMode) {
        TransitionDTO searchDTO;
        if (searchMode == MedicationsAllergyFragment.ALLERGY_ITEM) {
            searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearchAllergies();
        } else {
            searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearchMedications();
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", medicationsAllergiesDTO.getPayload()
                .getMedications().getMetadata().getPracticeId());
        queryMap.put("practice_mgmt", medicationsAllergiesDTO.getPayload()
                .getMedications().getMetadata().getPracticeMgmt());
        try {
            queryMap.put("search", URLEncoder.encode(searchQuery, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();
        getWorkflowServiceHelper().execute(searchDTO, searchCallback, queryMap, headers);

    }

    @Override
    public void searchItemSelected(MedicationsAllergiesObject item) {
        SystemUtil.hideSoftKeyboard(getContext(), getView());
        if (getDialog() == null) {
            getActivity().onBackPressed();
        } else {
            dismiss();
        }
        callback.addMedicationAllergyItem(item);
    }

    private SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            SystemUtil.hideSoftKeyboard(getActivity());
            switch (searchMode) {
                case MedicationsAllergyFragment.MEDICATION_ITEM:
                    submitSearch(query, searchMode);
                    break;
                case MedicationsAllergyFragment.ALLERGY_ITEM:
                    submitSearch(query, searchMode);
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
                submitSearch(newText, searchMode);
            }
            return false;
        }
    };

    private WorkflowServiceCallback searchCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            Gson gson = new Gson();
            MedicationsAllergiesResultsModel searchResults = gson.fromJson(workflowDTO.toString(),
                    MedicationsAllergiesResultsModel.class);
            List<? extends MedicationsAllergiesObject> resultsList;
            if (searchMode == MedicationsAllergyFragment.ALLERGY_ITEM) {
                resultsList = searchResults.getPayload().getSearchedAllergies().getAllergies();
            } else {
                resultsList = searchResults.getPayload().getSearchMedications().getMedicationsObjects();
            }

            MedicationAllergySearchAdapter adapter = (MedicationAllergySearchAdapter) searchRecycler.getAdapter();
            adapter.setItems(resultsList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            if (isVisible()){
                hideProgressDialog();
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }
    };

}