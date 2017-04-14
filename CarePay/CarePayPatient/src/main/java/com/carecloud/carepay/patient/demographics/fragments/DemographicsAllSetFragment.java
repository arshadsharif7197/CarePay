package com.carecloud.carepay.patient.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public class DemographicsAllSetFragment extends CheckInDemographicsBaseFragment {

    private DemographicDTO demographicDTO;

    private WorkflowServiceCallback confirmDemWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());

        initializeViews(view);

        return view;
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_demographics_moredetails;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        return demographicDTO;
    }

    private void initializeViews(View view) {
        initNextButton(view);

        TextView header = (TextView) view.findViewById(R.id.moreDetailsHeading);
        header.setText(Label.getLabel("demographics_allset_header"));

        Button gotoCarePay = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        gotoCarePay.setText(Label.getLabel("demographics_allset_go_button"));

        setGothamRoundedMediumTypeface(getActivity(), header);

        view.findViewById(R.id.stepProgressBarCheckin).setVisibility(View.GONE);
    }

    @Override
    protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
        Map<String, String> queries = new HashMap<>();

        // dynamic transition
        Gson gson = new Gson();
        String body = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getConfirmDemographics();

        getWorkflowServiceHelper().execute(transitionDTO, confirmDemWorkflowCallback, body, queries, getWorkflowServiceHelper().getPreferredLanguageHeader());
    }
}
