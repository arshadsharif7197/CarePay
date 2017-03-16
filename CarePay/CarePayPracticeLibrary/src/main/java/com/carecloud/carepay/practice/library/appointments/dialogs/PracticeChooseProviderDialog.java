package com.carecloud.carepay.practice.library.appointments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.ProviderSearchAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

public class PracticeChooseProviderDialog extends BaseDialogFragment
        implements ProviderSearchAdapter.SelectPracticeCallback {

    private AppointmentNavigationCallback callback;

    private List<AppointmentResourcesDTO> providerList = new ArrayList<>();
    private AppointmentResourcesDTO selectedProvider;
    private AppointmentsResultModel resourcesToScheduleModel;

    private RecyclerView searchRecycler;
    private Button continueButton;
    private SearchView searchView;
    private String continueButtonLabel;
    private String titleLabel;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            if (null == callback) {
                callback = (AppointmentNavigationCallback) context;
            }
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        continueButtonLabel = bundle.getString("continueButtonLabel");
        titleLabel = bundle.getString("titleLabel");

        resourcesToScheduleModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, bundle);
        providerList = resourcesToScheduleModel.getPayload().getResourcesToSchedule().get(0).getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_practice_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if(title!=null){
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);

                title.setText(titleLabel);
            }
        }

        continueButton = (Button) findViewById(R.id.nextButton);
        continueButton.setText(continueButtonLabel);
        continueButton.setOnClickListener(continueClick);
        continueButton.setEnabled(false);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        searchView.setOnQueryTextListener(queryTextListener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        setAdapter(providerList);
    }

    private void setAdapter(List<AppointmentResourcesDTO> practiceList){
        ProviderSearchAdapter practiceSearchAdapter;
        if(searchRecycler.getAdapter() == null){
            practiceSearchAdapter = new ProviderSearchAdapter(getContext(), practiceList, this);
            searchRecycler.setAdapter(practiceSearchAdapter);
        }else{
            practiceSearchAdapter = (ProviderSearchAdapter) searchRecycler.getAdapter();
            practiceSearchAdapter.setPracticeList(practiceList);
            practiceSearchAdapter.notifyDataSetChanged();
        }
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            SystemUtil.hideSoftKeyboard(getActivity());
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            clearSelectedProvider();
            findPractice(newText);
            continueButton.setEnabled(false);
            return true;
        }
    };

    private View.OnClickListener continueClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(selectedProvider != null){
                callback.selectVisitType(selectedProvider, resourcesToScheduleModel);
                dismiss();
            }
        }
    };

    @Override
    public void onSelectPractice(AppointmentResourcesDTO practice) {
        searchView.clearFocus();
        SystemUtil.hideSoftKeyboard(getActivity());
        selectedProvider = practice;
        continueButton.setEnabled(true);
    }

    private void findPractice(String search){
        List<AppointmentResourcesDTO> searchList = new ArrayList<>();
        for(AppointmentResourcesDTO practice : providerList){
            if(practice.getResource().getProvider().getName().toLowerCase().contains(search.toLowerCase())){
                searchList.add(practice);
            }
        }

        setAdapter(searchList);
    }

    private void clearSelectedProvider(){
        selectedProvider = null;
        ProviderSearchAdapter searchAdapter = (ProviderSearchAdapter) searchRecycler.getAdapter();
        searchAdapter.setSelectedPractice(selectedProvider);
    }
}
