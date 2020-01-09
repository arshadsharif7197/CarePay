package com.carecloud.carepay.practice.library.survey;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;

/**
 * @author pjohnson on 3/10/18.
 */
public class SurveyResultActivity extends BasePracticeActivity implements FragmentActivityInterface {

    private SurveyDTO surveyDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_survey);
        surveyDTO = getConvertedDTO(SurveyDTO.class);
        replaceFragment(SurveyResultFragment.newInstance(), false);
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
        //disable the action
    }

    @Override
    public boolean manageSession() {
        return true;
    }
}
