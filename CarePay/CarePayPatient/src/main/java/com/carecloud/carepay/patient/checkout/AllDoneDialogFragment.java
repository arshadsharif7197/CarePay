package com.carecloud.carepay.patient.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 30/05/17.
 */

public class AllDoneDialogFragment extends BaseDialogFragment {

    private WorkflowDTO workflowDto;

    /**
     * @param workflowDTO the workflow
     * @return a new instance of AllDoneDialogFragment
     */
    public static AllDoneDialogFragment newInstance(WorkflowDTO workflowDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, workflowDTO);
        AllDoneDialogFragment fragment = new AllDoneDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workflowDto = DtoHelper.getConvertedDTO(WorkflowDTO.class, getArguments());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_all_done, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = view.findViewById(R.id.okButton);
        button.setOnClickListener(view1 -> finishCheckOut());
    }

    private void finishCheckOut() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(CarePayConstants.REFRESH, true);
        PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDto, bundle);
    }
}
