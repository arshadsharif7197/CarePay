package com.carecloud.carepaylibray.medications.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergyFragment extends BaseDialogFragment {

    private View placeholderAllergies;
    private View placeholderMedication;
    private RecyclerView allergyRecycler;
    private RecyclerView medicationRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_medications_allergy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        inflateToolbarViews(view);
        initViews(view);
    }

    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.medications_toolbar);
        toolbar.setTitle("");
        if(getDialog()==null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_patient_mode_nav_back));
            toolbar.setNavigationOnClickListener(navigationClickListener);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        }
        TextView title = (TextView) toolbar.findViewById(R.id.medications_toolbar_title);
//        title.setText();//TODO get from DTO
    }

    private void initViews(View view){//TODO get all labels from DTO
        TextView header = (TextView) view.findViewById(R.id.allergy_medication_header);
//        header.setText();

        TextView headerMessage = (TextView) view.findViewById(R.id.allergy_medication_header_message);
//        headerMessage.setText();

        TextView allergySection = (TextView) view.findViewById(R.id.allergy_section_header);
//        allergySection.setText();

        TextView allergyTitle = (TextView) view.findViewById(R.id.allergy_title);
//        allergyTitle.setText();

        TextView allergyChooseButton = (TextView) view.findViewById(R.id.allergy_choose_button);
//        allergyChooseButton.setText();
        allergyChooseButton.setOnClickListener(chooseAllergyClickListener);

        TextView allergyPlaceholderText = (TextView) view.findViewById(R.id.allergy_none_placeholder_text);
//        allergyPlaceholderText.setText();

        TextView medicationSection = (TextView) view.findViewById(R.id.medications_section_header);
//        medicationSection.setText();

        TextView medicationTitle = (TextView) view.findViewById(R.id.medications_title);
//        medicationTitle.setText();

        TextView medicationChooseButton = (TextView) view.findViewById(R.id.medication_choose_button);
//        medicationChooseButton.setText();
        medicationChooseButton.setOnClickListener(chooseMedicationClickListener);

        TextView medicationPlaceholderText = (TextView) view.findViewById(R.id.medication_none_placeholder_text);
//        medicationPlaceholderText.setText();

        TextView continueButton = (TextView) view.findViewById(R.id.medication_continue_button);
//        continueButton.setText();
        continueButton.setOnClickListener(continueClickListener);

        placeholderAllergies = view.findViewById(R.id.allergy_none_placeholder);
        placeholderMedication = view.findViewById(R.id.medication_none_placeholder);

        allergyRecycler = (RecyclerView) view.findViewById(R.id.alergy_recycler);
        medicationRecycler = (RecyclerView) view.findViewById(R.id.medication_recycler);

    }


    private View.OnClickListener chooseAllergyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener chooseMedicationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener continueClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };
}
