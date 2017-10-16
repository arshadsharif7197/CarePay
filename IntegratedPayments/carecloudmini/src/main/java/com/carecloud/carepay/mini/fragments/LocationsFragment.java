package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.adapters.LocationsAdapter;
import com.carecloud.carepay.mini.models.response.LocationsDTO;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;

import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class LocationsFragment extends RegistrationFragment implements LocationsAdapter.SelectLocationListener {

    private View nextButton;
    private String selectedLocationId;

    private List<LocationsDTO> locations;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        String selectedPractice = getApplicationHelper().getApplicationPreferences().getPracticeId();
        locations = callback.getPreRegisterDataModel().getPracticeById(selectedPractice).getSortedLocations();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_locations, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_select_location_title), 3);

        nextButton = view.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });

        View backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBackPressed();
            }
        });

        initAdapter(view);
    }

    private void initAdapter(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.locations_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        LocationsDTO selectedLocation = null;
        selectedLocationId = getApplicationHelper().getApplicationPreferences().getLocationId();
        if(callback.getPreRegisterDataModel() != null && !StringUtil.isNullOrEmpty(selectedLocationId)){
            String selectedPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
            selectedLocation = callback.getPreRegisterDataModel().getPracticeById(selectedPracticeId).findLocationById(selectedLocationId);
            nextButton.setVisibility(View.VISIBLE);
        }
        LocationsAdapter locationsAdapter = new LocationsAdapter(getContext(), locations, this, selectedLocation);
        recyclerView.setAdapter(locationsAdapter);
    }

    private void selectLocation(){
        if(selectedLocationId == null){
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_select_location));
            return;
        }
        getApplicationHelper().getApplicationPreferences().setLocationId(selectedLocationId);
        callback.replaceFragment(new DeviceFragment(), true);
    }

    @Override
    public void onLocationSelected(String locationID) {
        this.selectedLocationId = locationID;
        if(selectedLocationId != null){
            nextButton.setVisibility(View.VISIBLE);
        }
    }
}
