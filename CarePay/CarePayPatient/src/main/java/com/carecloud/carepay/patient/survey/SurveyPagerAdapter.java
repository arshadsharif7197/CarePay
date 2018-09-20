package com.carecloud.carepay.patient.survey;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.survey.model.SurveyQuestionDTO;

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
        SurveyQuestionDTO question = questions.get(position);
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View view = inflater.inflate(R.layout.item_survey, container, false);
        TextView surveyQuestionTextView = (TextView) view.findViewById(R.id.surveyQuestionTextView);
        surveyQuestionTextView.setText(question.getTitle());
        container.addView(view);
        view.setTag(question.getUuid());
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
