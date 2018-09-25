package com.carecloud.carepay.patient.survey;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragmentInterface;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.survey.model.SurveyDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
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
        String patientId = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO)
                .getString(CarePayConstants.PATIENT_ID, null);
        if (icicle == null) {
            if (surveyDto.getPayload().getSurvey().getResponses() == null ||
                    surveyDto.getPayload().getSurvey().getResponses().isEmpty()) {
                replaceFragment(SurveyFragment.newInstance(patientId), false);
            } else {
                surveyDto.getPayload().getSurvey().setAlreadyFilled();
                replaceFragment(SurveyResultFragment.newInstance(patientId), false);
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
                    finish();
                }
            });
            displayDialogFragment(fragment, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof BackPressedFragmentInterface && ((BackPressedFragmentInterface) fragment).onBackPressed()) {
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
}
