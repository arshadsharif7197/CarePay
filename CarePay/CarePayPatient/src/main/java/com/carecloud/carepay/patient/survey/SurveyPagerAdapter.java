package com.carecloud.carepay.patient.survey;

import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.survey.model.SurveyQuestionDTO;

import java.util.List;

/**
 * @author pjohnson on 10/09/18.
 */
public class SurveyPagerAdapter extends PagerAdapter {

    private final List<SurveyQuestionDTO> questions;

    public SurveyPagerAdapter(List<SurveyQuestionDTO> questions) {
        this.questions = questions;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final SurveyQuestionDTO question = questions.get(position);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.item_survey, container, false);
        TextView surveyQuestionTextView = view.findViewById(R.id.surveyQuestionTextView);
        surveyQuestionTextView.setText(question.getTitle());
        container.addView(view);
        view.setTag(question.getUuid());
        final RatingBar surveyQuestionRatingBar = view.findViewById(R.id.surveyQuestionRatingBar);

        surveyQuestionRatingBar.setNumStars(5);
        surveyQuestionRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    question.setRate(rating);
                }
            }
        });
        surveyQuestionRatingBar.setStepSize((float) 0.5);
        surveyQuestionRatingBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Dont ask why i did this (SuperHack)
                float num = question.getRate();
                surveyQuestionRatingBar.setRating(Math.round(num));
                surveyQuestionRatingBar.setRating(num);
            }
        }, 500);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return questions.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
