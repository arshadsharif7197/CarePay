package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergySearchAdapter;
import com.carecloud.carepaylibray.medications.interfaces.MedicationAllergyCallback;
import com.carecloud.carepaylibray.medications.models.AllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsObject;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
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

public class MedicationAllergySearchFragment extends BaseDialogFragment
        implements MedicationAllergySearchAdapter.SearchItemSelectedCallback {
    public static final int ALLERGY_ITEM = 100;
    public static final int MEDICATION_ITEM = 101;

    private int searchMode;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationAllergyCallback callback;
    private RecyclerView searchRecycler;
    private SearchView searchView;
    private Button unlisted;
    private View noResultsContainer;
    private View initialScreenContainer;

    public static MedicationAllergySearchFragment newInstance(MedicationsAllergiesResultsModel medicationsAllergiesDTO,
                                                              int searchMode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        args.putInt(CarePayConstants.SEARCH_MODE, searchMode);
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
                callback = (MedicationAllergyCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement MedicationAllergyCallback");
        }

    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, getArguments());
        searchMode = getArguments().getInt(CarePayConstants.SEARCH_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_medication_allergy_search, container, false);
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
        Toolbar toolbar = view.findViewById(R.id.search_toolbar);
        toolbar.setTitle("");
        if (getDialog() == null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(v -> {
                SystemUtil.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            });
        }
        searchView = view.findViewById(R.id.search_entry_view);
        searchView.setQueryHint(Label.getLabel("search_field_hint"));
        searchView.setOnQueryTextListener(searchQueryListener);
        searchView.requestFocus(View.FOCUS_DOWN);
        SystemUtil.showSoftKeyboard(getActivity());

        TextView title = view.findViewById(R.id.toolbar_title);
        if (title != null) {
            if (searchMode == ALLERGY_ITEM) {
                title.setText(Label.getLabel("allergies_title"));
            } else {
                title.setText(Label.getLabel("medications_title"));
            }
            title.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    private void initViews(View view) {
        TextView headerTitle = view.findViewById(R.id.search_header_title);
        if (headerTitle != null) {
            headerTitle.setText(searchMode == ALLERGY_ITEM ?
                    Label.getLabel("medications_allergies_search_allergy_title") :
                    Label.getLabel("medications_allergies_search_medication_title"));
        }

        TextView headerSubtitle = view.findViewById(R.id.search_header_subtitle);
        if (headerSubtitle != null) {
            headerSubtitle.setText(searchMode == ALLERGY_ITEM ?
                    Label.getLabel("medicationsAndAllergies.search.allergies.hint.subtitle_1") :
                    Label.getLabel("medicationsAndAllergies.search.medication.hint.subtitle_1"));
        }

        unlisted = view.findViewById(R.id.add_unlisted);
        unlisted.setText(searchMode == ALLERGY_ITEM ?
                Label.getLabel("medication_allergies_add_unlisted_allergy") :
                Label.getLabel("medication_allergies_add_unlisted_medication"));
        unlisted.setOnClickListener(unlistedItemListener);
        unlisted.setEnabled(false);

        //disable adding unlisted allergies as these are not yet supported
        unlisted.setVisibility(searchMode == ALLERGY_ITEM ? View.GONE : View.VISIBLE);

        View unlistedBorder = view.findViewById(R.id.add_unlisted_border);
        if (unlistedBorder != null) {
            unlistedBorder.setVisibility(searchMode == ALLERGY_ITEM ? View.GONE : View.VISIBLE);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);
        View closeButton = view.findViewById(R.id.closeViewLayout);
        if (closeButton != null) {
            closeButton.setOnClickListener(view1 -> {
                SystemUtil.hideSoftKeyboard(getContext(), view1);
                dismiss();
            });
        }

        noResultsContainer = view.findViewById(R.id.noResultsContainer);

        if (getApplicationMode().getApplicationType().equals(ApplicationMode.ApplicationType.PATIENT)) {
            initialScreenContainer = view.findViewById(R.id.initialScreenContainer);
            TextView initialScreenTitleTextView = view.findViewById(R.id.initialScreenTitleTextView);
            initialScreenTitleTextView.setText(searchMode == ALLERGY_ITEM ?
                    Label.getLabel("medications_allergies_search_allergy_title") :
                    Label.getLabel("medications_allergies_search_medication_title"));
            TextView initialScreenSubTitleTextView = view.findViewById(R.id.initialScreenSubTitleTextView);
            initialScreenSubTitleTextView.setText(searchMode == ALLERGY_ITEM ?
                    Label.getLabel("medicationsAndAllergies.search.allergies.hint.subtitle_1") :
                    Label.getLabel("medicationsAndAllergies.search.medication.hint.subtitle_1"));
        }
    }

    private void setAdapters() {
        MedicationAllergySearchAdapter adapter = new MedicationAllergySearchAdapter(getContext(),
                new ArrayList<>(), this, searchMode);
        searchRecycler.setAdapter(adapter);
    }


    private void submitSearch(String searchQuery, int searchMode) {
        TransitionDTO searchDTO;
        if (searchMode == ALLERGY_ITEM) {
            searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearchAllergies();
        } else {
            searchDTO = medicationsAllergiesDTO.getMetadata().getLinks().getSearchMedications();
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", searchMode == ALLERGY_ITEM ?
                medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeId() :
                medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
        queryMap.put("practice_mgmt", searchMode == ALLERGY_ITEM ?
                medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeMgmt() :
                medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
        try {
            queryMap.put("search", URLEncoder.encode(searchQuery, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();
        getWorkflowServiceHelper().interrupt();
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
            submitSearch(query, searchMode);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText != null && newText.length() > 0) {
//                getWorkflowServiceHelper().interrupt();
                submitSearch(newText, searchMode);
                showHideInitialScreen(false);
            } else {
                showHideInitialScreen(true);
                noResultsContainer.setVisibility(View.GONE);
                MedicationAllergySearchAdapter adapter = (MedicationAllergySearchAdapter) searchRecycler.getAdapter();
                adapter.setItems(new ArrayList<>());
                adapter.notifyDataSetChanged();
            }
            unlisted.setEnabled(!StringUtil.isNullOrEmpty(newText));
            return false;
        }
    };

    private void showHideInitialScreen(boolean show) {
        if (initialScreenContainer != null) {
            initialScreenContainer.setVisibility(show ? View.VISIBLE : View.GONE);
            searchRecycler.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

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
            if (searchMode == ALLERGY_ITEM) {
                resultsList = searchResults.getPayload().getSearchedAllergies().getAllergies();
            } else {
                resultsList = searchResults.getPayload().getSearchMedications().getMedicationsObjects();
            }

            MedicationAllergySearchAdapter adapter = (MedicationAllergySearchAdapter) searchRecycler.getAdapter();
            adapter.setItems(resultsList);
            adapter.notifyDataSetChanged();

            if (!StringUtil.isNullOrEmpty(searchView.getQuery().toString()) && resultsList.isEmpty()) {
                noResultsContainer.setVisibility(View.VISIBLE);
            } else if (!resultsList.isEmpty()) {
                noResultsContainer.setVisibility(View.GONE);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            if (isVisible()) {
                hideProgressDialog();
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }
    };

    private View.OnClickListener unlistedItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String query = searchView.getQuery().toString();
            if (StringUtil.isNullOrEmpty(query)) {
                return;
            }
            MedicationsAllergiesObject newItem;
            if (searchMode == ALLERGY_ITEM) {
                AllergiesObject allergiesObject = new AllergiesObject();
                allergiesObject.setInteroperableID("");
                newItem = allergiesObject;
            } else {
                MedicationsObject medicationsObject = new MedicationsObject();
                medicationsObject.setDispensableDrugId("");
                newItem = medicationsObject;
            }
            newItem.setDisplayName(query);

            SystemUtil.hideSoftKeyboard(getContext(), getView());
            if (getDialog() == null) {
                getActivity().onBackPressed();
            } else {
                dismiss();
            }
            callback.promptAddUnlisted(newItem, searchMode);
        }

    };


}