package com.carecloud.carepaylibray.demographics.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsMoreDetailsFragment extends Fragment implements View.OnClickListener {
    View view;
    SwitchCompat wantUpdateSwitch;
    String[] getUpdateItemList;
    List<String> selectedUpdateItemList = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);

        getUpdateItemList = getResources().getStringArray(R.array.UpdatesMode);
        return view;
    }


    @Override
    public void onClick(View view) {
        if (view == wantUpdateSwitch) {
            switchActionForWantUpdateSwitch(wantUpdateSwitch.isChecked());
        }
    }

    private void switchActionForWantUpdateSwitch(boolean checked) {
        if (checked) {
            selectedUpdateItemList = new ArrayList<String>();
            showAlertDialog();
        } else {

        }
    }

    private void showAlertDialog() {
        final CharSequence[] dialogList = getUpdateItemList;
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle("Select Updates");
        int count = dialogList.length;
        final boolean[] is_checked = new boolean[count];

        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                        if (isChecked) {
                            selectedUpdateItemList.add(getUpdateItemList[whichButton]);
                        } else {
                            String selectedLable = getUpdateItemList[whichButton];
                            for (int index = 0; index < selectedUpdateItemList.size(); index++) {
                                if (selectedLable.equalsIgnoreCase(selectedUpdateItemList.get(index))) {
                                    selectedUpdateItemList.remove(index);
                                }
                            }
                        }
                    }
                });

        builderDialog.setPositiveButton("Select",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (selectedUpdateItemList.size() <= 0)
                            wantUpdateSwitch.setChecked(false);
                        Toast.makeText(getActivity(), selectedUpdateItemList.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        builderDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedUpdateItemList.clear();
                        wantUpdateSwitch.setChecked(false);
                        Toast.makeText(getActivity(), "" + selectedUpdateItemList.toString(), Toast.LENGTH_SHORT).show();


                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();
    }

}
