package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.medications.adapters.MedicationAllergiesAdapter;
import com.carecloud.carepaylibray.medications.models.AllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationAllergiesAction;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.medications.models.MedicationsPostModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 2/15/17
 */

public class AllergiesFragment extends BaseCheckinFragment implements
        MedicationAllergiesAdapter.MedicationAllergiesAdapterCallback {

    private RecyclerView allergyRecycler;
    private Button continueButton;

    protected DemographicsPresenter callback;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private MedicationsPostModel medicationsPostModel = new MedicationsPostModel();

    private List<AllergiesObject> currentAllergies = new ArrayList<>();
    private List<AllergiesObject> removeAllergies = new ArrayList<>();
    private List<AllergiesObject> addAllergies = new ArrayList<>();
    private List<AllergiesObject> tempAllergies = new ArrayList<>();

    private Handler handler = new Handler();

    public static AllergiesFragment newInstance(MedicationsAllergiesResultsModel medicationsAllergiesDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        AllergiesFragment fragment = new AllergiesFragment();
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
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, getArguments());
        currentAllergies = medicationsAllergiesDTO.getPayload().getAllergies().getPayload();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
        callback.setCheckinFlow(CheckinFlowState.ALLERGIES, 0, 0);
        hideProgressDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_allergies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);
        initViews(view);
        setAdapters();
        final NestedScrollView scrollView = view.findViewById(R.id.scroll_medications_allergy);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);

            }
        }, 30);
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
            title.setText(Label.getLabel("demographics_allergies_title"));
        }
    }

    private void initViews(View view) {
        View allergyChooseButton = view.findViewById(R.id.allergy_choose_button);
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        continueButton = view.findViewById(R.id.medication_allergies_continue_button);
        continueButton.setOnClickListener(continueClickListener);
        continueButton.setSelected(false);
        continueButton.setClickable(false);

        RecyclerView.LayoutManager allergyManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        allergyRecycler = view.findViewById(R.id.alergy_recycler);
        allergyRecycler.setLayoutManager(allergyManager);

    }

    private void setAdapters() {
        if (allergyRecycler.getAdapter() == null) {
            MedicationAllergiesAdapter allergyAdapter = new MedicationAllergiesAdapter(getContext(),
                    getApplicationMode().getApplicationType(), currentAllergies, this);
            allergyRecycler.setAdapter(allergyAdapter);
        } else {
            MedicationAllergiesAdapter adapter = ((MedicationAllergiesAdapter) allergyRecycler.getAdapter());
            adapter.setItems(currentAllergies);
            adapter.notifyDataSetChanged();
        }
        setAdapterVisibility();
    }

    private void setAdapterVisibility() {
        if (currentAllergies.isEmpty()) {
            allergyRecycler.setVisibility(View.GONE);
        } else {
            allergyRecycler.setVisibility(View.VISIBLE);
        }
        validateForm();
    }

    private void validateForm() {
        boolean valid = allergyRecycler.getVisibility() == View.VISIBLE;

        continueButton.setSelected(valid);
        continueButton.setClickable(valid);
    }

    @Override
    public void deleteItem(MedicationsAllergiesObject item) {
        item.setDeleted(true);
        if (item instanceof AllergiesObject) {
            //remove Medication from list
            if (addAllergies.contains(item)) {
                addAllergies.remove(item);
                tempAllergies.add((AllergiesObject) item);
            } else {
                item.setAction(MedicationAllergiesAction.delete);
                AllergiesObject allergyToDelete = new AllergiesObject();
                allergyToDelete.setAction(item.getAction());
                allergyToDelete.setUuid(item.getUuid());
                allergyToDelete.setInteroperableID(((AllergiesObject) item).getInteroperableID());
                allergyToDelete.setInteroperableDesc(((AllergiesObject) item).getInteroperableDesc());
                removeAllergies.add(allergyToDelete);
            }
        }
        setAdapters();
    }

    @Override
    public void restoreItem(MedicationsAllergiesObject item) {
        item.setDeleted(false);
        if (item instanceof AllergiesObject) {
            for (AllergiesObject deleteObject : removeAllergies) {
                if (deleteObject.getUuid() != null &&
                        deleteObject.getUuid().equals(item.getUuid())) {
                    removeAllergies.remove(deleteObject);
                    break;
                }
            }
            if (tempAllergies.contains(item)) {
                tempAllergies.remove(item);
                addAllergies.add((AllergiesObject) item);
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
        if (item instanceof AllergiesObject) {
            //check if exists
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

    private List<AllergiesObject> getAllModifiedAllergies() {
        List<AllergiesObject> combinedList = new ArrayList<>();
        combinedList.addAll(addAllergies);
        combinedList.addAll(removeAllergies);
        return combinedList;
    }

    private View.OnClickListener chooseAllergyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            callback.showMedicationAllergySearchFragment(MedicationAllergySearchFragment.ALLERGY_ITEM);
        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Gson gson = new Gson();
            TransitionDTO transitionDTO = medicationsAllergiesDTO.getMetadata().getTransitions().getAllergies();

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("patient_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPatientId());
            queryMap.put("practice_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeId());
            queryMap.put("practice_mgmt", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeMgmt());
            queryMap.put("appointment_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getAppointmentId());

            Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();
            headers.put("transition", "true");

            medicationsPostModel.setAllergiesList(getAllModifiedAllergies());
            String jsonBody = gson.toJson(medicationsPostModel);
            getWorkflowServiceHelper().execute(transitionDTO, submitMedicationAllergiesCallback,
                    jsonBody, queryMap, headers);
        }
    };

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SystemUtil.hideSoftKeyboard(getActivity());
            getActivity().onBackPressed();
        }
    };


    private WorkflowServiceCallback submitMedicationAllergiesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onUpdate(callback, workflowDTO);

            List<AllergiesObject> modifiedAllergies = getAllModifiedAllergies();
            if (!modifiedAllergies.isEmpty()) {
                MixPanelUtil.logEvent(getString(R.string.event_updated_allergies), getString(R.string.param_allergies_count), modifiedAllergies.size());
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public DTO getDto() {
        return medicationsAllergiesDTO;
    }
}

