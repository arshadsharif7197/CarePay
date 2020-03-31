package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.options.OnOptionSelectedListener;
import com.carecloud.carepaylibray.common.options.SelectOptionFragment;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.adapters.PhysicianAdapter;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.PhysicianDto;
import com.carecloud.carepaylibray.demographics.interfaces.PhysicianInterface;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.EndlessRecyclerViewScrollListener;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 30/10/17.
 */

public class SearchPhysicianFragment extends BaseDialogFragment implements PhysicianAdapter.PhysicianSelectedInterface {

    public static final int REFERRING_PHYSICIAN = 100;
    public static final int PRIMARY_PHYSICIAN = 101;


    private PhysicianInterface callback;
    private DemographicDTO dto;
    private SearchView searchView;
    private TextView stateTextView;
    private PhysicianAdapter adapter;
    private int physicianType;
    private PhysicianDto physician;
    private EndlessRecyclerViewScrollListener scrollListener;

    DemographicsOption allStatesOption;
    DemographicsOption selectedState;

    public SearchPhysicianFragment() {

    }

    /**
     * @param physicianDto  the physician Dto
     * @param physicianType the physician type (primary or referral)
     * @return a new instance of SearchPhysicianFragment
     */
    public static SearchPhysicianFragment newInstance(PhysicianDto physicianDto, int physicianType) {
        Bundle args = new Bundle();
        if (physicianDto != null) {
            DtoHelper.bundleDto(args, physicianDto);
        }
        args.putInt("physicianType", physicianType);
        SearchPhysicianFragment fragment = new SearchPhysicianFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (PhysicianInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmergencyContactInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        dto = (DemographicDTO) callback.getDto();
        physicianType = getArguments().getInt("physicianType");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_physician, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.toolbarLayout);
        if (getDialog() == null) {
            searchView = toolbar.findViewById(R.id.search_entry_view);
            ImageView lenImage = searchView.findViewById(R.id.search_mag_icon);
            if (lenImage != null) {
                lenImage.setVisibility(View.GONE);
            }
            callback.setToolbar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    getActivity().onBackPressed();
                }
            });
        } else {
            searchView = view.findViewById(R.id.search_entry_view);
            TextView title = toolbar.findViewById(R.id.toolbar_title);
            if (physicianType == PRIMARY_PHYSICIAN) {
                title.setText(Label.getLabel("demographics_primary_care_physician"));
            } else {
                title.setText(Label.getLabel("demographics_referring_physician"));
            }
            view.findViewById(R.id.edit_insurance_close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    SystemUtil.hideSoftKeyboard(getActivity());
                }
            });
        }
        setUpView(view);
    }

    private void setUpView(View view) {
        List<DemographicsOption> states = new ArrayList<>();
        allStatesOption = new DemographicsOption();
        allStatesOption.setLabel(Label.getLabel("demographics_physician_all_states_label"));
        allStatesOption.setName(Label.getLabel("demographics_physician_all_states_label"));
        selectedState = allStatesOption;
        states.add(allStatesOption);
        states.addAll(dto.getMetadata().getNewDataModel()
                .getDemographic().getAddress().getProperties()
                .getState().getOptions());
        stateTextView = view.findViewById(R.id.stateSelectorTextView);
        stateTextView.setOnClickListener(getOptionsListener(states,
                new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option, int position) {
                        selectedState = option;
                        stateTextView.setText(option.getLabel());
                        if (!StringUtil.isNullOrEmpty(searchView.getQuery().toString())) {
                            showProgressDialog();
                            searchPhysicians(searchView.getQuery().toString(),
                                    EndlessRecyclerViewScrollListener.DEFAULT_FIRST_PAGE);
                        }
                    }
                }, Label.getLabel("demographics_documents_title_select_state")));
        RecyclerView physicianRecyclerView = view.findViewById(R.id.physicianRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        physicianRecyclerView.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                searchPhysicians(searchView.getQuery().toString(), page);
            }

            @Override
            public void maximumNumberOfItemsReached() {
                adapter.maximumNumberOfItemsReached();
            }
        };
        physicianRecyclerView.addOnScrollListener(scrollListener);
        adapter = new PhysicianAdapter(new ArrayList<PhysicianDto>(), this);
        physicianRecyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SystemUtil.hideSoftKeyboard(getActivity());
                searchView.clearFocus();
                if (!StringUtil.isNullOrEmpty(query)) {
                    showProgressDialog();
                    searchPhysicians(query, EndlessRecyclerViewScrollListener.DEFAULT_FIRST_PAGE);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (physician != null) {
            searchView.setQuery(physician.getFullName().replace("'", ""), true);
        } else {
            SystemUtil.showSoftKeyboard(getContext());
            searchView.requestFocus();
        }
    }

    private void searchPhysicians(String query, int page) {
        if (page == EndlessRecyclerViewScrollListener.DEFAULT_FIRST_PAGE) {
            adapter.resetData();
            scrollListener.resetState();
        }
        Map<String, String> queries = new HashMap<>();
        if (!selectedState.equals(allStatesOption)) {
            queries.put("state_code", selectedState.getName());
        }
        queries.put("page", String.valueOf(page));

        if (callback.getAppointment() != null) {
            AppointmentDTO appointment = callback.getAppointment();
            queries.put("practice_mgmt", appointment.getMetadata().getPracticeMgmt());
            queries.put("practice_id", appointment.getMetadata().getPracticeId());
            queries.put("appointment_id", appointment.getMetadata().getAppointmentId());
        }

        JsonObject postBodyObj = new JsonObject();
        postBodyObj.addProperty("term", query);

        TransitionDTO searchPhysiciansLink = dto.getMetadata().getLinks().getSearchPhysicians();
        getWorkflowServiceHelper().execute(searchPhysiciansLink, searchPhysicianCallback,
                postBodyObj.toString(), queries);
    }


    protected View.OnClickListener getOptionsListener(final List<DemographicsOption> options,
                                                      final OnOptionSelectedListener listener,
                                                      final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionFragment fragment = SelectOptionFragment.newInstance(title);
                fragment.setOptions(options);
                fragment.setCallback(listener);
                fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getName());
            }
        };
    }

    private WorkflowServiceCallback searchPhysicianCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicDTO dto = DtoHelper.getConvertedDTO(DemographicDTO.class, workflowDTO);
            adapter.setData(dto.getPayload().getPhysicians());
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    @Override
    public void onPhysicianSelected(PhysicianDto physician) {
        SystemUtil.hideSoftKeyboard(getActivity());
        if (getDialog() == null) {
            getActivity().onBackPressed();
        } else {
            dismiss();
        }
        callback.onPhysicianSelected(physician, physicianType);
    }
}
