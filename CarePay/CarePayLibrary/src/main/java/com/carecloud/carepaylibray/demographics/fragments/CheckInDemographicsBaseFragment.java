package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jorge on 27/02/17.
 */

public abstract class CheckInDemographicsBaseFragment extends BaseCheckinFragment {

    protected CheckInNavListener checkInNavListener;
    StepProgressBar stepProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);
        stepProgressBar = (StepProgressBar) view.findViewById(R.id.stepProgressBarCheckin);
        stepProgressBar.setCumulativeDots(true);
        stepProgressBar.setNumDots(5);
        inflateContent(inflater,view);
        //initializeToolbar(view);
        inflateToolbarViews(view);
        return view;
    }


    private void inflateToolbarViews(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if(toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                stepProgressBar.setCurrentProgressDot(1);
            }
        });

        (view.findViewById(R.id.toolbar_layout)).setVisibility(View.INVISIBLE);
    }

    protected boolean checkTextEmptyValue( int textEditableId, View view) {
        EditText editText = (EditText) view.findViewById(textEditableId);
        return StringUtil.isNullOrEmpty(editText.getText().toString());
    }

    private void inflateContent(LayoutInflater inflater, View view){
        View childview = inflater.inflate(getContentId(),null);
        ((FrameLayout)view.findViewById(R.id.checkinDemographicsContentLayout)).addView(childview);
    }

    protected void setHeaderTitle(String title, View view){
        TextView textView = (TextView) view.findViewById(R.id.checkinDemographicsHeaderLabel);
        textView.setText(title);
        SystemUtil.setGothamRoundedMediumTypeface(getContext(), textView);
        (view.findViewById(R.id.toolbar_layout)).setVisibility(checkInNavListener.getCurrentStep()>1 ?View.VISIBLE:View.INVISIBLE);
        stepProgressBar.setCurrentProgressDot(checkInNavListener.getCurrentStep()-1);
    }

    protected void initNextButton(String label, View.OnClickListener listener, final View view){
        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setText(label);
        if (listener == null){
            listener =  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(passConstraints(view)) {
                        DemographicDTO demographicDTO = updateDemographicDTO(view);
                        openNextFragment(demographicDTO, (checkInNavListener.getCurrentStep() + 1)>4);
                    }
                }
            };
        }
        nextButton.setOnClickListener(listener);
    }

    protected void checkIfEnableButton(View view){
        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setEnabled(passConstraints(view));
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
            checkInNavListener = (CheckInNavListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement CheckInNavListener");
        }
    }

    public interface CheckInNavListener {
        public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step);
        public Integer getCurrentStep();
    }


    private void openNextFragment( DemographicDTO demographicDTO, boolean transition){
        Map<String, String> queries = new HashMap<>();
        queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
        queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
        queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition",  Boolean.valueOf(transition).toString());

        Gson gson = new Gson();
        String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicDTO.getPayload().getDemographics().getPayload().getAddress());
        getWorkflowServiceHelper().execute(transitionDTO, consentformcallback, demogrPayloadString, queries, header);
    }

    private WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicDTO demographicDTO = new Gson().fromJson(workflowDTO.toString(), DemographicDTO.class);

            if(checkInNavListener.getCurrentStep() ==4){
                ((CheckinDemographicsInterface)getActivity()).navigateToConsentFlow(workflowDTO);
            }else{
                checkInNavListener.applyChangesAndNavTo(demographicDTO, checkInNavListener.getCurrentStep() + 1);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getActivity().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };


}
