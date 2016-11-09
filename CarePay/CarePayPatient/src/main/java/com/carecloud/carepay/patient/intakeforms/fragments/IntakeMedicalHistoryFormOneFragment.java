package com.carecloud.carepay.patient.intakeforms.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;

/**
 * Created by prem_mourya on 9/28/2016.
 */

public class IntakeMedicalHistoryFormOneFragment extends InTakeFragment {

    private View mainView;
    private Context context;
    private TextView chooseAllergyTextView;
    private String[] allergiesArray = {"Penicillin", "Sulfa", "Latex", "Tree Nuts"};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = super.onCreateView(inflater, container, savedInstanceState);
        onAddChildView();
        return mainView;
    }

    private void onAddChildView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.fragment_intake_medical_history_form, null);
        TextView allegiesHaveTextView = (TextView) childActionView.findViewById(R.id.allergiesHaveHeaderTextView);
        EditText addUnlistedAllergiesEditText = (EditText) childActionView.findViewById(R.id.addUnlistedAllergiesEditText);
        addUnlistedAllergiesEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        chooseAllergyTextView = (TextView) childActionView.findViewById(R.id.choose_alleryTextView);
        chooseAllergyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogWithListview(allergiesArray, "Allergy Select");
            }
        });
        ((LinearLayout) mainView.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);

        SystemUtil.setProximaNovaSemiboldTypeface(this.context, allegiesHaveTextView);
        SystemUtil.setProximaNovaRegularTypeface(this.context, addUnlistedAllergiesEditText);
    }

    private void showAlertDialogWithListview(final String[] allergiesArray, String title) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int listener) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(allergiesArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                chooseAllergyTextView.setText(allergiesArray[position]);
                alert.dismiss();
            }
        });
    }

}
