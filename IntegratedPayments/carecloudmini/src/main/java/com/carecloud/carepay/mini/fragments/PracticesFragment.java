package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.adapters.PracticesAdapter;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.services.ServiceCallback;
import com.carecloud.carepay.mini.services.ServiceRequestDTO;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;
import com.carecloud.carepay.mini.views.CustomErrorToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lmenendez on 6/24/17
 */

public class PracticesFragment extends RegistrationFragment implements PracticesAdapter.SelectPracticeListener {

    private View nextButton;
    private boolean preventSelection = false;

    private UserPracticeDTO selectedPractice;

    private List<UserPracticeDTO> userPractices;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        if(userPractices == null){
            userPractices = new ArrayList<>(callback.getRegistrationDataModel().getPayloadDTO().getUserPractices());
        }
    }

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
        RecyclerView practicesRecycler = (RecyclerView) view.findViewById(R.id.practice_recycler);
        practicesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        PracticesAdapter practicesAdapter = new PracticesAdapter(getContext(), userPractices, this);
        practicesRecycler.setAdapter(practicesAdapter);

        practicesRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return preventSelection;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void selectPractice(){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());

        ServiceRequestDTO authenticatePractice = callback.getRegistrationDataModel().getMetadata().getTransitions().getAuthenticate();
        getServiceHelper().execute(authenticatePractice, authenticatePracticeCallback, queryMap);
    }

    private void getLocations(){
        callback.replaceFragment(new LocationsFragment(), true);
    }

    @Override
    public void onPracticeSelected(UserPracticeDTO selectedPractice) {
        this.selectedPractice = selectedPractice;
        if(selectedPractice.getPracticeId() != null){
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private ServiceCallback authenticatePracticeCallback = new ServiceCallback() {
        @Override
        public void onPreExecute() {
            nextButton.setEnabled(false);
            preventSelection = true;
        }

        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {
            nextButton.setEnabled(true);
            preventSelection = false;
            Log.d(PracticesFragment.class.getName(), serviceResponseDTO.toString());
            callback.setRegistrationDataModel(serviceResponseDTO);
            getApplicationHelper().getApplicationPreferences().setPracticeId(selectedPractice.getPracticeId());
            getLocations();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            nextButton.setEnabled(true);
            preventSelection = false;
            Log.d(PracticesFragment.class.getName(), exceptionMessage);
            CustomErrorToast.showWithMessage(getContext(), exceptionMessage);
        }
    };
}
