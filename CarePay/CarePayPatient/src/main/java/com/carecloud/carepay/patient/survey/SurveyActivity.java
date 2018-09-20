package com.carecloud.carepay.patient.survey;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragment;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyActivity extends BasePatientActivity implements FragmentActivityInterface {

    private SurveyDTO surveyDto;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_survey);
        surveyDto = getConvertedDTO(SurveyDTO.class);
        if (icicle == null) {
            replaceFragment(SurveyFragment.newInstance(), false);
        }
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

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof BackPressedFragment && ((BackPressedFragment) fragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
