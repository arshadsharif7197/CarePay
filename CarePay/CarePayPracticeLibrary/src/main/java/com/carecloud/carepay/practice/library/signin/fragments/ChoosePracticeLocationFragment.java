package com.carecloud.carepay.practice.library.signin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.adapters.PracticeLocationSearchAdapter;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionDTO;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepay.practice.library.signin.interfaces.SelectPracticeCallback;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pjohnson on 02/21/18.
 */
public class ChoosePracticeLocationFragment extends BaseDialogFragment
        implements PracticeLocationSearchAdapter.SelectPracticeLocationAdapterCallback {

    private List<LocationDTO> locationsList = new ArrayList<>();
    private LocationDTO selectedLocation;


    private RecyclerView searchRecycler;
    private Button continueButton;
    private SearchView searchView;

    private SelectPracticeCallback callback;
    private PracticeSelectionUserPractice selectedPractice;
    private WorkflowDTO workflowDTO;

    public static ChoosePracticeLocationFragment newInstance(PracticeSelectionUserPractice selectedPractice, WorkflowDTO workflowDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedPractice);
        if (workflowDTO != null) {
            DtoHelper.bundleDto(args, workflowDTO);
        }

        ChoosePracticeLocationFragment fragment = new ChoosePracticeLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (SelectPracticeCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement SelectPracticeLocationAdapterCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, getArguments());
        selectedPractice = DtoHelper.getConvertedDTO(PracticeSelectionUserPractice.class, getArguments());
        if (selectedPractice == null && workflowDTO != null) {
            PracticeSelectionDTO practiceSelectionDTO = DtoHelper.getConvertedDTO(PracticeSelectionDTO.class, workflowDTO);
            selectedPractice = practiceSelectionDTO.getPayload().getUserPracticesList().get(0);
        }
        locationsList = selectedPractice.getLocations();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_choose_practice_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.search_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (title != null) {
                ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                title.setLayoutParams(layoutParams);
                title.setGravity(Gravity.CENTER_HORIZONTAL);
                title.setText(Label.getLabel("signIn.selectLocation.header.label.screenTitle"));
            }
        }

        continueButton = (Button) findViewById(R.id.nextButton);
        continueButton.setText(Label.getLabel("practice_list_continue"));
        continueButton.setOnClickListener(continueClick);

        View closeButton = findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.onSelectPracticeLocationCanceled(selectedPractice);
            }
        });

        searchView = (SearchView) view.findViewById(R.id.search_entry_view);
        for (TextView textView : ViewUtils.findChildrenByClass(searchView, TextView.class)) {
            textView.setHintTextColor(getResources().getColor(R.color.textview_default_textcolor));
            textView.setTextColor(getResources().getColor(R.color.textview_default_textcolor));
        }

        searchView.setQueryHint(Label.getLabel("search_field_hint"));
        searchView.setOnQueryTextListener(queryTextListener);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(layoutManager);

        setAdapter(locationsList);
        if (locationsList.size() > 5) {
            view.findViewById(R.id.search_edit_frame).setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter(List<LocationDTO> locationList) {
        PracticeLocationSearchAdapter practiceLocationSearchAdapter;
        if (searchRecycler.getAdapter() == null) {
            practiceLocationSearchAdapter = new PracticeLocationSearchAdapter(locationList, this);
            selectedLocation = getPreviousSelectedLocation(ApplicationPreferences
                    .getInstance().getPracticeLocationId());
            if (selectedLocation != null) {
                practiceLocationSearchAdapter.setSelectedLocation(selectedLocation);
                continueButton.setEnabled(true);
            }
            searchRecycler.setAdapter(practiceLocationSearchAdapter);
        } else {
            practiceLocationSearchAdapter = (PracticeLocationSearchAdapter) searchRecycler.getAdapter();
            practiceLocationSearchAdapter.setLocationList(locationList);
            practiceLocationSearchAdapter.notifyDataSetChanged();
        }
    }

    private LocationDTO getPreviousSelectedLocation(Integer practiceLocationId) {
        if (practiceLocationId != null) {
            for (LocationDTO locationDTO : locationsList) {
                if (locationDTO.getId().equals(practiceLocationId)) {
                    return locationDTO;
                }
            }
        }
        return null;
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
            clearSelectedLocationPractice();
            findPractice(newText);
            continueButton.setEnabled(false);
            return true;
        }
    };

    private View.OnClickListener continueClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedLocation != null) {
                dismiss();
                if (workflowDTO != null) {
                    callback.onSelectPracticeLocation(workflowDTO, selectedPractice, selectedLocation);
                } else {
                    callback.onSelectPracticeLocation(selectedPractice, selectedLocation);
                }
            }
        }
    };

    private void findPractice(String search) {
        List<LocationDTO> searchList = new ArrayList<>();
        for (LocationDTO location : locationsList) {
            if (location.getName().toLowerCase().contains(search.toLowerCase())) {
                searchList.add(location);
            }
        }
        setAdapter(searchList);
    }

    private void clearSelectedLocationPractice() {
        selectedLocation = null;
        PracticeLocationSearchAdapter searchAdapter = (PracticeLocationSearchAdapter) searchRecycler.getAdapter();
        searchAdapter.setSelectedLocation(selectedLocation);
    }

    @Override
    public void onSelectPracticeLocation(LocationDTO location) {
        searchView.clearFocus();
        SystemUtil.hideSoftKeyboard(getActivity());
        selectedLocation = location;
        continueButton.setEnabled(true);
    }
}
