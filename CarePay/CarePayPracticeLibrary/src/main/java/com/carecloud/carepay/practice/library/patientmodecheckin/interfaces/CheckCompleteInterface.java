package com.carecloud.carepay.practice.library.patientmodecheckin.interfaces;

import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.carecloud.carepaylibray.survey.model.SurveySettings;

/**
 * @author pjohnson on 8/06/17.
 */

public interface CheckCompleteInterface extends FragmentActivityInterface {
    void logout();

    void showConfirmationPinDialog();

    void goToShop();

    void fillSurvey();

}
