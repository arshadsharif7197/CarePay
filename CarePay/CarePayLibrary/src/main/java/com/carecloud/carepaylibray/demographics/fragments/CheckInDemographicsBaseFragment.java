package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jorge on 27/02/17.
 */

public abstract class CheckInDemographicsBaseFragment extends BaseCheckinFragment {

    StepProgressBar stepProgressBar;

    protected CheckinFlowCallback checkinFlowCallback;

    private WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicDTO demographicDTO = new Gson().fromJson(workflowDTO.toString(), DemographicDTO.class);

            if (checkinFlowCallback.getCurrentStep() == 5) {
                checkinFlowCallback.navigateToWorkflow(workflowDTO);
            } else {
                checkinFlowCallback.applyChangesAndNavTo(demographicDTO, checkinFlowCallback.getCurrentStep() + 1);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);
        stepProgressBar = (StepProgressBar) view.findViewById(R.id.stepProgressBarCheckin);
        stepProgressBar.setCumulativeDots(true);
        stepProgressBar.setNumDots(5);
        inflateContent(inflater, view);
        //initializeToolbar(view);
        inflateToolbarViews(view);

        View mainContainer = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(mainContainer);
        hideKeyboardOnViewTouch(view);
        return view;
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    protected boolean checkTextEmptyValue(int textEditableId, View view) {
        EditText editText = (EditText) view.findViewById(textEditableId);
        return StringUtil.isNullOrEmpty(editText.getText().toString());
    }

    private void inflateContent(LayoutInflater inflater, View view) {
        View childview = inflater.inflate(getContentId(), null);
        ((FrameLayout) view.findViewById(R.id.checkinDemographicsContentLayout)).addView(childview);
    }

    protected void setHeaderTitle(String title, String heading, String subHeading, View view){
        TextView textView = (TextView) view.findViewById(R.id.toolbar_title);
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT ) {
            TextView mainHeadingTextView = (TextView) view.findViewById(R.id.demographicsMainHeading);
            TextView subHeadingTextView = (TextView)  view.findViewById(R.id.demographicsSubHeading);
            (view.findViewById(R.id.toolbar_layout)).setVisibility(View.VISIBLE);

            mainHeadingTextView.setText(heading);
            subHeadingTextView.setText(subHeading);
        }else{
            (view.findViewById(R.id.toolbar_layout)).setVisibility(View.VISIBLE);
            textView.setText(title);
        }
    }

    protected void initNextButton(final View view) {
        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                if (passConstraints(view)) {
                    DemographicDTO demographicDTO = updateDemographicDTO(view);
                    openNextFragment(demographicDTO, (checkinFlowCallback.getCurrentStep() + 1) > 5);
                }
            }
        });
    }

    protected void checkIfEnableButton(View view) {
        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        boolean isEnabled = passConstraints(view);
        nextButton.setEnabled(isEnabled);
        nextButton.setClickable(isEnabled);
        Context context = getActivity();
        if (context != null && getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            nextButton.setBackground(ContextCompat.getDrawable(context, isEnabled ? R.drawable.bg_green_overlay : R.drawable.bg_silver_overlay));
        }
    }

    protected abstract boolean passConstraints(View view);

    protected abstract int getContentId();

    protected abstract DemographicDTO updateDemographicDTO(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if (context instanceof DemographicsView) {
                checkinFlowCallback = ((DemographicsView) context).getPresenter();
            } else {
                checkinFlowCallback = (CheckinFlowCallback) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CheckinFlowCallback");
        }
    }

    protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", Boolean.valueOf(transition).toString());

        Gson gson = new Gson();
        String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicDTO.getPayload().getDemographics().getPayload().getAddress());
        getWorkflowServiceHelper().execute(transitionDTO, consentformcallback, demogrPayloadString, queries, header);
    }
}
