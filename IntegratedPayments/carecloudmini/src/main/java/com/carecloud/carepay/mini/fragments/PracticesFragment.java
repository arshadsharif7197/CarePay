package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.adapters.PracticesAdapter;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.views.CustomErrorToast;

import java.util.ArrayList;
import java.util.List;

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
            userPractices = new ArrayList<>(callback.getPreRegisterDataModel().getUserPracticeDTOList());
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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPractice();
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
        RecyclerView practicesRecycler = (RecyclerView) view.findViewById(R.id.practice_recycler);
        practicesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        selectedPractice = null;
        String selectedPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
        if(callback.getPreRegisterDataModel() != null && selectedPracticeId != null){
            selectedPractice = callback.getPreRegisterDataModel().getPracticeById(selectedPracticeId);
            nextButton.setVisibility(View.VISIBLE);
        }

        PracticesAdapter practicesAdapter = new PracticesAdapter(getContext(), userPractices, this, selectedPractice);
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
        if(selectedPractice == null){
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_select_practice));
            return;
        }

        getApplicationHelper().getApplicationPreferences().setPracticeId(selectedPractice.getPracticeId());
        getLocations();

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

}
