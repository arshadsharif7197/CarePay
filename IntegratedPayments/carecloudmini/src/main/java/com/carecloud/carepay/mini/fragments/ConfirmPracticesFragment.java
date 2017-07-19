package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.views.CustomErrorToast;

/**
 * Created by lmenendez on 6/24/17
 */

public class ConfirmPracticesFragment extends RegistrationFragment {

    private View lastSelectedOption;
    private View nextButton;

    private UserPracticeDTO selectedPractice;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        String selectedPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
        selectedPractice = callback.getPreRegisterDataModel().getPracticeById(selectedPracticeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_confirm_practice, container, false);
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

        TextView practiceName = (TextView) view.findViewById(R.id.practice_name);
        practiceName.setText(selectedPractice.getPracticeName());

        View selectYes = view.findViewById(R.id.button_yes);
        View selectableYes = view.findViewById(R.id.indicator_yes);
        selectYes.setOnClickListener(getSelectConfirmOption(selectableYes));

        View selectNo = view.findViewById(R.id.button_no);
        View selectableNo = view.findViewById(R.id.indicator_no);
        selectNo.setOnClickListener(getSelectConfirmOption(selectableNo));
    }


    private void selectPractice(){
        if(lastSelectedOption == null){
            CustomErrorToast.showWithMessage(getContext(), getString(R.string.error_confirm_practice));
            return;
        }
        switch (lastSelectedOption.getId()){
            case R.id.indicator_yes:
                getLocations();
                break;
            case R.id.indicator_no:
            default:
                callback.replaceFragment(new ConfigurationErrorFragment(), true);
                break;
        }
    }

    private void getLocations(){
        callback.replaceFragment(new LocationsFragment(), true);
    }

    private View.OnClickListener getSelectConfirmOption(final View selectableView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastSelectedOption != null) {
                    lastSelectedOption.setSelected(false);
                }
                nextButton.setEnabled(true);
                selectableView.setSelected(true);
                lastSelectedOption = selectableView;
            }
        };
    }

}
