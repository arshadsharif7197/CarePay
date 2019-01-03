package com.carecloud.carepay.patient.survey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragmentInterface;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.utils.MixPanelUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyActivity extends BasePatientActivity implements FragmentActivityInterface {

    public static final int FLAG_SURVEY_FLOW = 110;
    private SurveyDTO surveyDto;
    private boolean comesFromNotifications;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_survey);
        surveyDto = getConvertedDTO(SurveyDTO.class);
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        String patientId = extra.getString(CarePayConstants.PATIENT_ID, null);
        comesFromNotifications = extra.getBoolean(CarePayConstants.NOTIFICATIONS_FLOW, false);
        if (icicle == null) {
            if (surveyDto.getPayload().getSurvey().getResponses() == null ||
                    surveyDto.getPayload().getSurvey().getResponses().isEmpty()) {
                replaceFragment(SurveyFragment.newInstance(patientId, comesFromNotifications), false);
                logSurveyEvent(getString(R.string.event_survey_started));
            } else {
                surveyDto.getPayload().getSurvey().setAlreadyFilled();
                replaceFragment(SurveyResultFragment.newInstance(patientId, comesFromNotifications), false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_in_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exitFlow) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("survey.form.label.exitMenu.title"),
                            Label.getLabel("survey.form.label.exitMenu.message"),
                            Label.getLabel("button_no"),
                            Label.getLabel("button_yes"));
            fragment.setCallback(new ConfirmationCallback() {
                @Override
                public void onConfirm() {
                    logSurveyEvent(getString(R.string.event_survey_canceled));
                    if (comesFromNotifications) {
                        finish();
                    } else {
                        callContinueService();
                    }
                }
            });
            displayDialogFragment(fragment, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callContinueService() {
        TransitionDTO continueTransition = surveyDto.getMetadata().getTransitions().getContinueTransition();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", surveyDto.getPayload().getSurvey().getMetadata().getPracticeMgmt());
        query.put("practice_id", surveyDto.getPayload().getSurvey().getMetadata().getPracticeId());
        query.put("patient_id", surveyDto.getPayload().getSurvey().getAppointment().getMetadata().getPatientId());
        query.put("appointment_id", surveyDto.getPayload().getSurvey().getAppointment().getMetadata().getAppointmentId());
        getWorkflowServiceHelper().execute(continueTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean(CarePayConstants.REFRESH, true);
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, query);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof BackPressedFragmentInterface
                && ((BackPressedFragmentInterface) fragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
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
    public DTO getDto() {
        return surveyDto;
    }

    private void logSurveyEvent(String event) {
        AppointmentDTO appointmentDTO = surveyDto.getPayload().getSurvey().getAppointment();
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id),
                getString(R.string.param_is_guest),
                getString(R.string.param_survey_access)
        };
        Object[] values = {surveyDto.getPayload().getSurvey().getMetadata().getPracticeId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                false,
                comesFromNotifications ?
                        getString(R.string.survey_access_mode_standard) :
                        getString(R.string.survey_access_mode_checkout)
        };
        MixPanelUtil.logEvent(event, params, values);
    }
}
