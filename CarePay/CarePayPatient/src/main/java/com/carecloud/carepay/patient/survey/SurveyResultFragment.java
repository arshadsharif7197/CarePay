package com.carecloud.carepay.patient.survey;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragmentInterface;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SocialNetworkLink;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.carecloud.carepaylibray.survey.model.SurveyQuestionDTO;
import com.carecloud.carepaylibray.survey.model.SurveySettings;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyResultFragment extends BaseFragment implements BackPressedFragmentInterface {

    private FragmentActivityInterface callback;
    private SurveyDTO surveyDto;
    private boolean showFeedBackLayout;
    private Button noThanksButton;
    private TextView subtitleTextView;
    private EditText feedbackEditText;
    private Button submitButton;
    private boolean comesFromNotification;

    public static SurveyResultFragment newInstance(String patientId, boolean comesFromNotifications) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PATIENT_ID, patientId);
        args.putBoolean(CarePayConstants.NOTIFICATIONS_FLOW, comesFromNotifications);
        SurveyResultFragment fragment = new SurveyResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        } else {
            throw new ClassCastException("attached context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        comesFromNotification = getArguments().getBoolean(CarePayConstants.NOTIFICATIONS_FLOW);
        surveyDto = (SurveyDTO) callback.getDto();
        SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        float average = getRateAverage(surveyModel);
        if (!surveyModel.isAlreadyFilled()) {
            if (average >= surveyDto.getPayload().getSurveySettings().getSatisfiedRate()
                    || surveyModel.isZeroAnswers()) {
                sendResponse(surveyModel, true, false);
            } else {
                showFeedBackLayout = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noThanksButton = view.findViewById(R.id.noThanksButton);
        subtitleTextView = view.findViewById(R.id.subtitleTextView);
        submitButton = view.findViewById(R.id.submitButton);
        feedbackEditText = view.findViewById(R.id.feedbackEditText);
        final SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        if (surveyModel.isAlreadyFilled() || surveyModel.isZeroAnswers()) {
            manageGoBackButton(view, surveyModel, null);
        } else {
            if (showFeedBackLayout) {
                showNegativeFeedbackLayout(view, surveyModel);
            } else {
                view.findViewById(R.id.socialNetworksLayout).setVisibility(View.VISIBLE);
                subtitleTextView.setText(Label.getLabel("surveys_click_spread_word"));
            }
        }
    }

    private void finishFlow(WorkflowDTO workflowDTO) {
        if (workflowDTO == null || comesFromNotification) {
            getActivity().finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean(CarePayConstants.REFRESH, true);
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
        }
    }

    private void showNegativeFeedbackLayout(View view, final SurveyModel surveyModel) {
        view.findViewById(R.id.negativeFeedbackLayout).setVisibility(View.VISIBLE);
        noThanksButton.setVisibility(View.VISIBLE);
        noThanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(surveyModel, false, true);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResponse(surveyModel, true, true);
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
        subtitleTextView.setVisibility(View.VISIBLE);
    }

    private Button manageGoBackButton(View view, SurveyModel surveyModel, final WorkflowDTO workflowDTO) {
        Button goBackButton = view.findViewById(R.id.okButton);
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFlow(workflowDTO);
            }
        });
        view.findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
        if (surveyModel.isZeroAnswers() && !surveyModel.isAlreadyFilled()) {
            subtitleTextView.setVisibility(View.GONE);
            if (comesFromNotification) {
                goBackButton.setText(Label.getLabel("survey.successScreen.button.title.back"));
            } else {
                goBackButton.setText(Label.getLabel("add_appointment_back_to_appointments_button"));
            }
        } else {
            goBackButton.setBackgroundResource(R.drawable.round_white_border);
            goBackButton.setTextColor(getResources().getColor(R.color.white));
            goBackButton.setText(Label.getLabel("go_back_label"));
            subtitleTextView.setText(Label.getLabel("survey.successScreen.subtitle.message.alreadyFilled"));
            subtitleTextView.setVisibility(View.VISIBLE);
        }
        return goBackButton;
    }

    private void showOkButton(final WorkflowDTO workflowDTO) {
        getView().findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);
        feedbackEditText.setVisibility(View.GONE);
        subtitleTextView.setVisibility(View.GONE);
        noThanksButton.setVisibility(View.GONE);
        Button okButton = getView().findViewById(R.id.okButton);
        okButton.setVisibility(View.VISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFlow(workflowDTO);
            }
        });
    }

    private void createSocialLinkViews(View view, SurveySettings settings) {
        boolean thereAre4 = false;
        if (settings.getNetworkLinks().getLinks().size() == 4) {
            thereAre4 = true;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        LinearLayout firstRow = view.findViewById(R.id.firstRow);
        firstRow.removeAllViews();
        LinearLayout secondRow = view.findViewById(R.id.secondRow);
        secondRow.removeAllViews();
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
                    noThanksButton.setText(Label.getLabel("survey.form.button.title.done"));
                }
            });
            child.setLayoutParams(lp);
            row.addView(child);
            if (row.getChildCount() == 2) {
                if (thereAre4) {
                    row = secondRow;
                }
            } else if (row.getChildCount() > 2) {
                row = secondRow;
            }
        }
    }

    private int getLinkImageResource(SocialNetworkLink link) {
        int linkImageId = -1;
        switch (link.getId()) {
            case "facebook":
                linkImageId = R.drawable.icn_survey_facebook;
                break;
            case "vitals":
                linkImageId = R.drawable.icn_survey_vitals;
                break;
            case "yelp":
                linkImageId = R.drawable.icn_survey_yelp;
                break;
            case "healthgrades":
                linkImageId = R.drawable.icn_survey_healthgrades;
                break;
            case "google":
                linkImageId = R.drawable.icn_survey_google;
                break;
        }
        return linkImageId;
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

    private void sendResponse(final SurveyModel survey, final boolean submitFeedbackButtonPressed,
                              final boolean showOkButton) {
        SurveyModel surveyResponse = createSurveyResponse(survey, submitFeedbackButtonPressed);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(surveyResponse);
        Map<String, String> query = new HashMap<>();
        query.put("practice_id", survey.getMetadata().getPracticeId());
        query.put("practice_mgmt", survey.getMetadata().getPracticeMgmt());
        String patientId = null;
        String appointmentId = survey.getMetadata().getAppointmentId();
        if (survey.getAppointment() != null) {
            patientId = survey.getAppointment().getMetadata().getPatientId();
            appointmentId = survey.getAppointment().getMetadata().getAppointmentId();
        }
        query.put("appointment_id", appointmentId);
        query.put("patient_id", getArguments().getString(CarePayConstants.PATIENT_ID, patientId));
        TransitionDTO surveyTransition = surveyDto.getMetadata().getTransitions().getSaveSurvey();
        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        if (!comesFromNotification) {
            header.put("transition", "true");
        }
        getWorkflowServiceHelper().execute(surveyTransition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                if (survey.isZeroAnswers()) {
                    manageGoBackButton(getView(), survey, workflowDTO);
                } else if (!submitFeedbackButtonPressed) {
                    finishFlow(workflowDTO);
                } else if (showOkButton) {
                    showOkButton(workflowDTO);
                } else if (comesFromNotification
                        || getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
                    displaySocialNetworksLinks(getView(), survey, workflowDTO);
                } else {
                    finishCheckOutFlow(workflowDTO);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
                Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
                showOkButton(null);
            }
        }, jsonResponse, query, header);
    }

    protected void displaySocialNetworksLinks(View view, SurveyModel survey, final WorkflowDTO workflowDTO) {
        noThanksButton.setVisibility(View.VISIBLE);
        SurveySettings settings = surveyDto.getPayload().getSurveySettings();
        if (settings.getNetworkLinks().isEnable()
                && !settings.getNetworkLinks().getLinks().isEmpty()) {
            subtitleTextView.setVisibility(View.VISIBLE);
            createSocialLinkViews(view, settings);
            noThanksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishFlow(workflowDTO);
                }
            });
        } else {
            view.findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
            Button goBackButton = manageGoBackButton(view, survey, workflowDTO);
            subtitleTextView.setVisibility(View.GONE);
            goBackButton.setText(Label.getLabel("survey.successScreen.button.title.back"));
            noThanksButton.setVisibility(View.GONE);
        }
    }

    private void finishCheckOutFlow(final WorkflowDTO workflowDTO) {
        getView().findViewById(R.id.fakeView).setVisibility(View.VISIBLE);
        Button goBackButton = getView().findViewById(R.id.okButton);
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(CarePayConstants.REFRESH, true);
                PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, bundle);
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

    @Override
    public boolean onBackPressed() {
        //disable back
        return true;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }
}
