package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.adapters.EmployerRecyclerViewAdapter;
import com.carecloud.carepaylibray.demographics.EmployerInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 4/10/17.
 */

public class SearchEmployerFragment extends BaseFragment
        implements EmployerRecyclerViewAdapter.EmployerAdapterInterface {

    private EmployerInterface callback;
    private DemographicsSettingsDTO demographicsSettingsDTO;
    private EmployerRecyclerViewAdapter adapter;

    public static SearchEmployerFragment newInstance() {
        return new SearchEmployerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (EmployerInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmployerInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
//        dataModel = demographicsSettingsDTO.getMetadata().getNewDataModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_employer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarLayout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);
        setUpUi(view);

    }

    private void setUpUi(View view) {
        Button addNewEmployerButton = (Button) findViewById(R.id.addNewEmployerButton);
        addNewEmployerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getActivity());
                callback.displayAddEmployerFragment();
            }
        });

        final SearchView searchView = (SearchView) view.findViewById(com.carecloud.carepaylibrary.R.id.search_entry_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                SystemUtil.hideSoftKeyboard(getActivity());
                searchEmployers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3) {
                    ((ISession) getContext()).getWorkflowServiceHelper().interrupt();

                    searchEmployers(newText);
                }
                return false;
            }
        });
        searchView.requestFocus(View.FOCUS_DOWN);
        searchView.setIconified(false);
        SystemUtil.showSoftKeyboard(getActivity());

        RecyclerView employerRecyclerView = (RecyclerView) view.findViewById(R.id.employerRecyclerView);
        employerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmployerRecyclerViewAdapter();
        adapter.setCallback(this);
        employerRecyclerView.setAdapter(adapter);

    }

    private void searchEmployers(String query) {
        TransitionDTO searchEmployersTransition = demographicsSettingsDTO.getMetadata().getLinks()
                .getSearchEmployers();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("practice_mgmt", "carecloud");
        queryMap.put("term", query);

        Map<String, String> headers = ((ISession) getContext()).getWorkflowServiceHelper()
                .getPreferredLanguageHeader();
        getWorkflowServiceHelper().execute(searchEmployersTransition,
                employersCallback, queryMap, headers);
    }

    WorkflowServiceCallback employersCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            DemographicsSettingsDTO dto = DtoHelper
                    .getConvertedDTO(DemographicsSettingsDTO.class, workflowDTO);
            adapter.setData(dto.getPayload().getEmployers().getEmployersData());
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(String exceptionMessage) {
//            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    public void onEmployerClicked(EmployerDto employer) {
        SystemUtil.hideSoftKeyboard(getActivity());
        callback.addEmployer(employer);
    }
}
