package com.carecloud.carepay.practice.library.survey;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SocialNetworkLink;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.carecloud.carepaylibray.survey.model.SurveyQuestionDTO;
import com.carecloud.carepaylibray.survey.model.SurveySettings;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 9/10/18.
 */
public class SurveyResultFragment extends BaseFragment {

    private FragmentActivityInterface callback;
    private SurveyDTO surveyDto;
    private boolean showFeedBackLayout;
    private EditText feedbackEditText;
    private TextView noThanksButtonLinks;
    private float average;


    public static SurveyResultFragment newInstance() {
        return new SurveyResultFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        } else {
            throw new ClassCastException("context must implement FragmentActivityInterface.");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        surveyDto = (SurveyDTO) callback.getDto();
        SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        average = getRateAverage(surveyModel);
        if (average >= surveyDto.getPayload().getSurveySettings().getSatisfiedRate()
                || surveyModel.isZeroAnswers()) {
            sendResponse(surveyModel, false);
        } else {
            showFeedBackLayout = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noThanksButtonLinks = view.findViewById(R.id.noThanksButtonLinks);
        final SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        if (showFeedBackLayout) {
            view.findViewById(R.id.negativeContainer).setVisibility(View.VISIBLE);
            feedbackEditText = view.findViewById(R.id.feedbackEditText);
            final View submitButton = view.findViewById(R.id.submitButton);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendResponse(surveyModel, true);
                }
            });
            view.findViewById(R.id.noThanksButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendResponse(surveyModel, false);
                }
            });
            feedbackEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        submitButton.setEnabled(false);
                    } else {
                        submitButton.setEnabled(true);
                    }
                }
            });
        } else {
            view.findViewById(R.id.socialNetworksLayout).setVisibility(View.VISIBLE);
        }
    }

    private float getRateAverage(SurveyModel survey) {
        float rate = 0;
        int dividend = 0;
        for (SurveyQuestionDTO question : survey.getQuestions()) {
            if (question.getRate() > 0) {
                rate += question.getRate();
                dividend++;
            }
        }
        if (dividend == 0) {
            survey.setZeroAnswers(true);
            survey.setSurveyRating(0.0f);
            return 0.0f;
        }
        survey.setSurveyRating(rate / dividend);
        return rate / dividend;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    private void sendResponse(final SurveyModel survey, final boolean submitFeedbackButtonPressed) {
        SurveyModel surveyResponse = createSurveyResponse(survey, submitFeedbackButtonPressed);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(surveyResponse);
        Map<String, String> query = new HashMap<>();
        query.put("practice_id", survey.getMetadata().getPracticeId());
        query.put("practice_mgmt", survey.getMetadata().getPracticeMgmt());
        String appointmentId = survey.getMetadata().getAppointmentId();
        if (survey.getAppointment() != null) {
            appointmentId = survey.getAppointment().getMetadata().getAppointmentId();
        }
        query.put("appointment_id", appointmentId);
        query.put("patient_id", survey.getAppointment().getMetadata().getPatientId());
        TransitionDTO surveyTransition = surveyDto.getMetadata().getTransitions().getSaveSurvey();
        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");
        getWorkflowServiceHelper().execute(surveyTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                logSurveyCompleted();

                if (survey.isZeroAnswers()) {
                    showDoneButton(workflowDTO);
                } else if (submitFeedbackButtonPressed) {
                    PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                } else {
                    displaySocialNetworksLinks(getView(), survey, workflowDTO);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }, jsonResponse, query, header);
    }

    protected void displaySocialNetworksLinks(View view, SurveyModel survey, final WorkflowDTO workflowDTO) {
        noThanksButtonLinks.setVisibility(View.VISIBLE);
        SurveyModel settings = surveyDto.getPayload().getSurvey();
        if (settings.getNetworkLinks().isEnable()
                && !settings.getNetworkLinks().getLinks().isEmpty()) {
//            subtitleTextView.setVisibility(View.VISIBLE);
            createSocialLinkViews(view, settings);
            noThanksButtonLinks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                }
            });
        } else {
//            view.findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
            showDoneButton(workflowDTO);
//            Button goBackButton = manageGoBackButton(view, survey, workflowDTO);
//            subtitleTextView.setVisibility(View.GONE);
//            goBackButton.setText(Label.getLabel("survey.successScreen.button.title.back"));
            noThanksButtonLinks.setVisibility(View.GONE);
        }
    }

    private void createSocialLinkViews(View view, SurveyModel settings) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        LinearLayout firstRow = view.findViewById(R.id.firstRow);
        firstRow.removeAllViews();
        LinearLayout row = firstRow;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ActionBar.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;
        for (final SocialNetworkLink link : settings.getNetworkLinks().getLinks()) {
            View child = layoutInflater
                    .inflate(R.layout.layout_survey_social_network, null, false);
            TextView socialNetworkNameTextView = child.findViewById(R.id.socialNetworkNameTextView);
            socialNetworkNameTextView.setText(Label
                    .getLabel("survey.successScreen.socialLink.label." + link.getId()));
            int linkImageId = getLinkImageResource(link);
            if (linkImageId != -1) {
                ImageView linkImageView = child.findViewById(R.id.socialNetworkImageView);
                linkImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), linkImageId));
            }
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link.getUrl()));
                    startActivity(intent);
                    noThanksButtonLinks.setText(Label.getLabel("survey.form.button.title.done"));
                }
            });
            child.setLayoutParams(lp);
            row.addView(child);
           /* if (row.getChildCount() == 2) {
                if (thereAre4) {
                    row = secondRow;
                }
            } else if (row.getChildCount() > 2) {
                row = secondRow;
            }*/
        }
    }

    private int getLinkImageResource(SocialNetworkLink link) {
        int linkImageId = -1;
        switch (link.getId()) {
            case "facebook":
                linkImageId = R.drawable.logo_survey_facebook;
                break;
            case "vitals":
                linkImageId = R.drawable.logo_survey_vitals;
                break;
            case "yelp":
                linkImageId = R.drawable.logo_survey_yelp;
                break;
            case "healthgrades":
                linkImageId = R.drawable.logo_survey_healthgrades;
                break;
            case "google":
                linkImageId = R.drawable.logo_survey_google;
                break;
        }
        return linkImageId;
    }

    private void showDoneButton(final WorkflowDTO workflowDTO) {
        noThanksButtonLinks.setVisibility(View.GONE);
        TextView okButton = getView().findViewById(R.id.okButton);
        okButton.setVisibility(View.VISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            }
        });
    }

    @NonNull
    private SurveyModel createSurveyResponse(@NonNull SurveyModel survey, boolean attachFeedback) {
        SurveyModel surveyResponse = new SurveyModel();
        surveyResponse.setUuid(survey.getUuid());
        surveyResponse.setVersion(survey.getVersion());
        surveyResponse.setAppointmentId(survey.getMetadata().getAppointmentId());
        surveyResponse.setSurveyRating(survey.getSurveyRating());
        surveyResponse.setType(survey.getType());

        List<SurveyQuestionDTO> questions = new ArrayList<>();
        for (SurveyQuestionDTO question : survey.getQuestions()) {
            SurveyQuestionDTO questionResponse = new SurveyQuestionDTO();
            questionResponse.setUuid(question.getUuid());
            questionResponse.setRate(question.getRate());
            questions.add(questionResponse);
        }
        surveyResponse.setResponses(questions);
        if (attachFeedback && feedbackEditText != null) {
            surveyResponse.setFeedback(feedbackEditText.getText().toString());
        }
        return surveyResponse;
    }

    private void logSurveyCompleted() {
        AppointmentDTO appointmentDTO = surveyDto.getPayload().getSurvey().getAppointment();
        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_provider_id),
                getString(R.string.param_location_id),
                getString(R.string.param_is_guest),
                getString(R.string.param_survey_rating),
                getString(R.string.param_survey_access)
        };
        Object[] values = {surveyDto.getPayload().getSurvey().getMetadata().getPracticeId(),
                appointmentDTO.getPayload().getProvider().getGuid(),
                appointmentDTO.getPayload().getLocation().getGuid(),
                false,
                average,
                getString(R.string.survey_access_mode_checkout)
        };
        MixPanelUtil.logEvent(getString(R.string.event_survey_completed), params, values);
        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_surveys_completed), 1);
        if (average >= surveyDto.getPayload().getSurveySettings().getSatisfiedRate()
                || surveyDto.getPayload().getSurvey().isZeroAnswers()) {
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_satisfied_surveys), 1);
        } else {
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_unsatisfied_surveys), 1);
        }
        MixPanelUtil.endTimer(getString(R.string.timer_survey));
    }

}
