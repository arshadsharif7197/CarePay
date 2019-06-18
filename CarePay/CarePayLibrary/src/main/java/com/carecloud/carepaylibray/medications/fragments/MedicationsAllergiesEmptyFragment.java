package com.carecloud.carepaylibray.medications.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.Map;

public class MedicationsAllergiesEmptyFragment extends BaseCheckinFragment {
    public static final String KEY_DISPLAY_MODE = "display_mode";
    public static final int ALLERGY_MODE = 100;
    public static final int MEDICATION_MODE = 101;


    private DemographicsPresenter callback;
    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private int selectedMode;

    public static MedicationsAllergiesEmptyFragment newInstance(MedicationsAllergiesResultsModel medicationsAllergiesDTO, int mode) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, medicationsAllergiesDTO);
        args.putInt(KEY_DISPLAY_MODE, mode);

        MedicationsAllergiesEmptyFragment fragment = new MedicationsAllergiesEmptyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, args);
        selectedMode = args.getInt(KEY_DISPLAY_MODE, MEDICATION_MODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedMode == ALLERGY_MODE) {
            callback.setCheckinFlow(CheckinFlowState.ALLERGIES, 0, 0);
        } else {
            callback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 0, 0);
        }
        hideProgressDialog();
    }

    @Override
    public void attachCallback(Context context) {
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (DemographicsPresenter) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement DemographicsPresenter");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_empty_medications_allergies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        inflateToolbarViews(view);

        ImageView headerIcon = view.findViewById(R.id.headerIconImageView);
        headerIcon.setImageResource(selectedMode == ALLERGY_MODE ?
                R.drawable.icn_allergies_lg : R.drawable.icn_medications_lg);
        TextView emptyMessage = view.findViewById(R.id.empty_message);
        emptyMessage.setText(selectedMode == ALLERGY_MODE ?
                Label.getLabel("medication_allergies_check_allergies") :
                Label.getLabel("medication_allergies_check_medications"));

        View yesButton = view.findViewById(R.id.button_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkflowDTO workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, DtoHelper.getStringDTO(medicationsAllergiesDTO));
                switch (selectedMode) {
                    case ALLERGY_MODE:
                        callback.navigateToAllergy(workflowDTO, false);
                        break;
                    case MEDICATION_MODE:
                    default:
                        callback.navigateToMedications(workflowDTO, false);
                        break;
                }
            }
        });

        View noButton = view.findViewById(R.id.button_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueToNextStep();
            }
        });
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        if (title != null) {
            title.setText(Label.getLabel(selectedMode == ALLERGY_MODE ?
                    "allergies_subtitle" : "medications_subtitle"));
        }
    }

    @Override
    public DTO getDto() {
        return medicationsAllergiesDTO;
    }

    private void continueToNextStep() {
        TransitionDTO transitionDTO;
        Map<String, String> queryMap = new HashMap<>();

        if (selectedMode == ALLERGY_MODE) {
            transitionDTO = medicationsAllergiesDTO.getMetadata().getTransitions().getAllergies();
            queryMap.put("patient_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPatientId());
            queryMap.put("practice_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeId());
            queryMap.put("practice_mgmt", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getPracticeMgmt());
            queryMap.put("appointment_id", medicationsAllergiesDTO.getPayload().getAllergies().getMetadata().getAppointmentId());
        } else {
            transitionDTO = medicationsAllergiesDTO.getMetadata().getTransitions().getMedications();
            queryMap.put("patient_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPatientId());
            queryMap.put("practice_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeId());
            queryMap.put("practice_mgmt", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getPracticeMgmt());
            queryMap.put("appointment_id", medicationsAllergiesDTO.getPayload().getMedications().getMetadata().getAppointmentId());
        }


        Map<String, String> headers = getWorkflowServiceHelper().getPreferredLanguageHeader();
        headers.put("transition", "true");

        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, headers);
    }

    private WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            onUpdate(callback, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    public int getSelectedMode(){
        return selectedMode;
    }
}
