package com.carecloud.carepay.practice.library.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.carecloud.carepaylibray.survey.model.SurveyQuestionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 3/10/18.
 */
public class SurveyActivity extends BasePracticeActivity implements FragmentActivityInterface {

    private SurveyDTO surveyDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        surveyDTO = getConvertedDTO(SurveyDTO.class);
        initializeLanguageSpinner();
        initializeHomeButton();
        replaceFragment(SurveyFragment.newInstance(), false);
        logSurveyEvent(getString(R.string.event_survey_started));
    }

    private void initializeLanguageSpinner() {
        final TextView languageSwitch = findViewById(R.id.languageSpinner);
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), true,
                surveyDTO.getPayload().getLanguages(), new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                TransitionDTO transition = surveyDTO.getMetadata().getLinks().getLanguage();
                changeLanguage(transition, language.getCode().toLowerCase(), headers, new SimpleCallback() {
                    @Override
                    public void callback() {
                        changeLeftMenuLabels();
                        loadSurvey();
                        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
                    }
                });
            }
        });
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offsetX = view.getWidth() / 2 - popupPickerLanguage.getWidth() / 2;
                int offsetY = -view.getHeight() - popupPickerLanguage.getHeight();
                popupPickerLanguage.showAsDropDown(view, offsetX, offsetY);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
    }

    private void loadSurvey() {
        final SurveyModel survey = surveyDTO.getPayload().getSurvey();
        final Map<String, String> query = new HashMap<>();
        query.put("practice_id", survey.getMetadata().getPracticeId());
        query.put("practice_mgmt", survey.getMetadata().getPracticeMgmt());
        query.put("patient_id", survey.getAppointment().getMetadata().getPatientId());
        query.put("appointment_id", survey.getAppointment().getMetadata().getAppointmentId());
        getWorkflowServiceHelper().execute(surveyDTO.getMetadata().getLinks().getGetSurvey(),
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        showProgressDialog();
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        hideProgressDialog();
                        refreshQuestionTitles(workflowDTO, survey);
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        hideProgressDialog();
                        showErrorNotification(exceptionMessage);
                        Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
                    }
                }, query);
    }

    private void refreshQuestionTitles(WorkflowDTO workflowDTO, SurveyModel survey) {
        SurveyDTO tempDto = DtoHelper.getConvertedDTO(SurveyDTO.class, workflowDTO);
        for (int i = 0; i < survey.getQuestions().size(); i++) {
            SurveyQuestionDTO question = survey.getQuestions().get(i);
            question.setTitle(tempDto.getPayload().getSurvey().getQuestions().get(i).getTitle());
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.root_layout);
        if (fragment instanceof SurveyFragment) {
            ((SurveyFragment) fragment).refreshQuestionTitles();
        }
    }

    private void changeLeftMenuLabels() {
        ((TextView) findViewById(R.id.checkoutMessage))
                .setText(Label.getLabel("survey.patientMode.form.label.leftMesssage"));
    }

    private void initializeHomeButton() {
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ConfirmDialogFragment fragment = ConfirmDialogFragment
                        .newInstance(Label.getLabel("survey.form.label.exitMenu.title"),
                                Label.getLabel("survey.form.label.exitMenu.message"),
                                Label.getLabel("button_no"),
                                Label.getLabel("button_yes"));
                fragment.setCallback(new ConfirmationCallback() {
                    @Override
                    public void onConfirm() {
                        logSurveyEvent(getString(R.string.event_survey_canceled));
                        callContinueService();
                    }
                });
                String tag = fragment.getClass().getName();
                fragment.show(ft, tag);
            }
        });
    }

    private void callContinueService() {
        TransitionDTO continueTransition = surveyDTO.getMetadata().getTransitions().getContinueTransition();
        Map<String, String> query = new HashMap<>();
        query.put("practice_mgmt", surveyDTO.getPayload().getSurvey().getMetadata().getPracticeMgmt());
        query.put("practice_id", surveyDTO.getPayload().getSurvey().getMetadata().getPracticeId());
        query.put("patient_id", surveyDTO.getPayload().getSurvey().getAppointment().getMetadata().getPatientId());
        query.put("appointment_id", surveyDTO.getPayload().getSurvey().getAppointment().getMetadata().getAppointmentId());
        getWorkflowServiceHelper().execute(continueTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                navigateToWorkflow(workflowDTO);
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
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return surveyDTO;
    }

    @Override
    public void onBackPressed() {
        //disable action
    }

    private void logSurveyEvent(String event) {
        boolean isGuest = !ValidationHelper.isValidEmail(getAppAuthorizationHelper().getCurrUser());
        AppointmentDTO appointmentDTO = surveyDTO.getPayload().getSurvey().getAppointment();
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id),
                getString(R.string.param_is_guest),
                getString(R.string.param_survey_access)
        };
        Object[] values = {surveyDTO.getPayload().getSurvey().getMetadata().getPracticeId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                isGuest,
                getString(R.string.survey_access_mode_checkout)
        };
        MixPanelUtil.logEvent(event, params, values);

        if (getString(R.string.event_survey_started).equals(event)) {
            MixPanelUtil.startTimer(getString(R.string.timer_survey));
        }
    }

}
