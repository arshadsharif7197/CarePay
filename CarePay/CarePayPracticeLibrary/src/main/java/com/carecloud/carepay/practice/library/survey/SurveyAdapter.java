package com.carecloud.carepay.practice.library.survey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.survey.model.SurveyQuestionDTO;

import java.util.List;

/**
 * @author pjohnson on 9/10/18.
 */
class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {

    private final List<SurveyQuestionDTO> questions;

    public SurveyAdapter(List<SurveyQuestionDTO> questions) {
        this.questions = questions;
    }

    @Override
    public SurveyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.survey_question_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final SurveyAdapter.ViewHolder holder, int position) {
        final SurveyQuestionDTO question = questions.get(position);

        holder.questionNumberTextView.setText(String.format(Label
                .getLabel("survey.patientMode.item.label.questionNumber"), position + 1));
        holder.questionTextView.setText(question.getTitle());
        holder.surveyQuestionRatingBar.setNumStars(5);
        holder.surveyQuestionRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    question.setRate(rating);
                }
            }
        });
        holder.surveyQuestionRatingBar.setStepSize((float) 0.5);
        holder.surveyQuestionRatingBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Dont ask why i did this (SuperHack)
                float num = question.getRate();
                holder.surveyQuestionRatingBar.setRating(Math.round(num));
                holder.surveyQuestionRatingBar.setRating(num);
            }
        }, 100);
        holder.surveyQuestionRatingBar.setRating(0);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView questionNumberTextView;
        TextView questionTextView;
        RatingBar surveyQuestionRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            questionNumberTextView = itemView.findViewById(R.id.questionNumberTextView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            surveyQuestionRatingBar = itemView.findViewById(R.id.surveyQuestionRatingBar);
        }
    }
}
