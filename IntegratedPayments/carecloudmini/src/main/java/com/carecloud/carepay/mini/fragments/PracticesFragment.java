package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.adapters.PracticesAdapter;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.services.ServiceCallback;
import com.carecloud.carepay.mini.services.ServiceRequestDTO;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;
import com.carecloud.carepay.mini.views.CustomErrorToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 6/24/17
 */

public class PracticesFragment extends RegistrationFragment implements PracticesAdapter.SelectPracticeListener {

    private View nextButton;
    private String selectedPracticeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_practices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_select_practice_title), 2);

        nextButton = view.findViewById(R.id.button_next);
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPractice();
            }
        });

        initAdapter(view);
    }

    private void initAdapter(View view){
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.practice_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<UserPracticeDTO> practices = callback.getRegistrationDataModel().getPayloadDTO().getUserPractices();
        PracticesAdapter practicesAdapter = new PracticesAdapter(getContext(), practices, this);
        recyclerView.setAdapter(practicesAdapter);
    }

    private void selectPractice(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", selectedPracticeId);

        ServiceRequestDTO authenticatePractice = callback.getRegistrationDataModel().getMetadata().getTransitions().getAuthenticate();
        getServiceHelper().execute(authenticatePractice, authenticatePracticeCallback, queryMap);
    }

    private void getLocations(){
        callback.replaceFragment(new LocationsFragment(), true);
    }

    @Override
    public void onPracticeSelected(String practiceID) {
        this.selectedPracticeId = practiceID;
        if(selectedPracticeId != null){
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private ServiceCallback authenticatePracticeCallback = new ServiceCallback() {
        @Override
        public void onPreExecute() {
            nextButton.setEnabled(false);
        }

        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {
            nextButton.setEnabled(true);
            Log.d(PracticesFragment.class.getName(), serviceResponseDTO.toString());
            callback.setRegistrationDataModel(serviceResponseDTO);
            getApplicationHelper().getApplicationPreferences().setPracticeId(selectedPracticeId);
            getLocations();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            nextButton.setEnabled(true);
            Log.d(PracticesFragment.class.getName(), exceptionMessage);
            CustomErrorToast.showWithMessage(getContext(), exceptionMessage);
        }
    };
}
