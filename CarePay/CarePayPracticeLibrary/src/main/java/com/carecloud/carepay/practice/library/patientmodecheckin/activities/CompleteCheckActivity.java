package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckInCompletedDialogFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckCompleteInterface;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.Gson;

/**
 * @author pjohnson on 8/06/17.
 */

public class CompleteCheckActivity extends BasePracticeActivity implements CheckCompleteInterface {

    private DTO dto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_check);
        Gson gson = new Gson();
        dto = gson.fromJson(getIntent().getStringExtra("workflow"), AppointmentsResultModel.class);
        if (savedInstanceState == null) {
            replaceFragment(CheckInCompletedDialogFragment
                    .newInstance(false), false);
        }
    }

    @Override
    public DTO getDto() {
        return dto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void navigateToWorkflow() {
        Gson gson = new Gson();
        navigateToWorkflow(gson.fromJson(getIntent().getStringExtra("workflow"), WorkflowDTO.class));
    }

    @Override
    public void showConfirmationPinDialog() {
        ConfirmationPinDialog confirmationPinDialog = new ConfirmationPinDialog(this,
                ((AppointmentsResultModel) dto).getMetadata().getLinks().getPinpad(), false);
        confirmationPinDialog.show();
    }
}
