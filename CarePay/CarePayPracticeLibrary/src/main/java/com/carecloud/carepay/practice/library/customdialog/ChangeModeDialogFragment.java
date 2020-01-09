package com.carecloud.carepay.practice.library.customdialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenTransitionsDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeModeDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private List<String> options;
    private PracticeHomeScreenTransitionsDTO transitions;

    public static ChangeModeDialogFragment newInstance(String transitionsAsJsonObject) {
        Bundle args = new Bundle();
        args.putString("transitions", transitionsAsJsonObject);
        ChangeModeDialogFragment fragment = new ChangeModeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        transitions = DtoHelper.getConvertedDTO(PracticeHomeScreenTransitionsDTO.class,
                getArguments().getString("transitions"));
        ApplicationPreferences.getInstance().setPatientModeTransition(transitions.getPatientMode());
        options = new ArrayList<>();
        options.add(Label.getLabel("patient_mode_button"));
        options.add(Label.getLabel("logout_button"));
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_mode, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout patientMode = view.findViewById(R.id.dialog_patient_mode_option);

        TextView patientModeOptionText = view.findViewById(R.id.homeChangeModePatientOptionText);
        patientModeOptionText.setText(StringUtil.getLabelForView(options.get(0)));
        patientModeOptionText.setTextColor(ContextCompat.getColor(getContext(),
                R.color.textview_default_textcolor));
        patientMode.setOnClickListener(this);

        LinearLayout logout = view.findViewById(R.id.dialog_logout_option);
        TextView logoutOptionText = view.findViewById(R.id.homeChangeModeLogoutOptionText);
        logoutOptionText.setText(StringUtil.getLabelForView(options.get(1)));
        logoutOptionText.setTextColor(ContextCompat.getColor(getContext(),
                R.color.textview_default_textcolor));
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.dialog_patient_mode_option) {
            onPatientModeSelected();
            ((ISession) getContext()).getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE);
        } else if (viewId == R.id.dialog_logout_option) {
            onLogOutSelected();
        }
    }

    private void onLogOutSelected() {
        Map<String, String> query = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        query.put("transition", "true");
        getWorkflowServiceHelper().execute(transitions.getLogout(), logOutCall, query, headers);
    }

    private WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            getApplicationMode().clearUserPracticeDTO();
            getAppAuthorizationHelper().setUser(null);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            dismiss();
            getActivity().finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void onPatientModeSelected() {
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        query.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        getWorkflowServiceHelper().execute(transitions.getPatientMode(), commonTransitionCallback, query);
    }

    private WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
